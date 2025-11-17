package com.hayden.hap.upgrade.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateManager;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.common.db.orm.sql.SqlBuilderManager;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.DBType;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.itf.IDbHandleService;
import com.hayden.hap.upgrade.itf.ILogsService;
import com.hayden.hap.upgrade.itf.IProgressService;
import com.hayden.hap.upgrade.utils.MetaDataFileUtils;
import com.hayden.hap.upgrade.utils.RunnableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/11 10:10
 */
@Service("dbHandleService")
public class DbHandleServiceImpl implements IDbHandleService {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    private final String DBTABLEKEY = "changedtablemap";
    @Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;
    @Autowired
    private JdbcTemplateSupportDao jdbcTemplateSupportDao;
    @Autowired
    private ILogsService logsService;
    @Autowired
    private IProgressService progressService;
    @Autowired
    private DataSourceCreator dataSourceCreator;

    @Override
    public void recordUpgradeTable(ParamVO paramVO, String tableName) {
        Set<String> tableNameSet = new HashSet<>();
        String key = paramVO.getKey();
        if (redisTemplate.opsForHash().hasKey(DBTABLEKEY, key)) {
            tableNameSet = (Set<String>) redisTemplate.opsForHash().get(DBTABLEKEY, key);
        }
        tableNameSet.add(tableName);
        redisTemplate.opsForHash().put(DBTABLEKEY, key, tableNameSet);
    }

    @Override
    public void createTable(ParamVO paramVO, String dataSourceId, ProgressModuleVO progressModuleVO) throws HDException {
        try {
            Set<String> tableNameSet = getTableNameList(paramVO);
            if (tableNameSet != null && tableNameSet.size() > 0) {
                logsService.setLogs(paramVO, "dbhandle", System.currentTimeMillis(), "存在数据库变更，准备修改物理表结构", progressModuleVO);
                String msg = "";
                for (String tableName : tableNameSet) {
                    if (!tableName.toLowerCase().startsWith("v_")) {
                        //判断线程是否终止，终止则向上抛异常
                        RunnableUtils.isInterrupt(paramVO);

                        msg = getCreateTableMessage(tableName, dataSourceId);
                        progressModuleVO.setProgress(progressModuleVO.getProgress() + progressModuleVO.getPct());
                        progressService.setProgressModuleVO(paramVO, progressModuleVO);
                        logsService.setLogs(paramVO, "dbhandle", System.currentTimeMillis(), msg, progressModuleVO);
                    }
                }
                removeTableCache(paramVO);
                logsService.setLogs(paramVO, "dbhandle", System.currentTimeMillis(), "数据库变更完成", progressModuleVO);
            }
        } catch (Exception e) {
            throw new HDException("数据库结构变更异常请手动处理后再点击升级 " + e.getMessage());
        }
    }

    public Set<String> getTableNameList(ParamVO paramVO) {
        return (Set<String>) redisTemplate.opsForHash().get(DBTABLEKEY, paramVO.getKey());
    }

    public void removeTableCache(ParamVO paramVO) {
        redisTemplate.opsForHash().delete(DBTABLEKEY, paramVO.getKey());
    }

    private String getCreateTableMessage(String tableName, String dataSourceId) throws Exception {

        Object[] createTableLinkData = getCreateTableLinkData(tableName, dataSourceId);

        List<TableDefVO> tableDefVOList = (List<TableDefVO>) createTableLinkData[0];
        Map<String, Map<String, List<String>>> sqlListMap = (Map<String, Map<String, List<String>>>) createTableLinkData[1];
        StringBuilder tableStr = (StringBuilder) createTableLinkData[2];

        StringBuilder message = new StringBuilder();

        JdbcTemplateSupportDao jdbcTemplateSupportDao = AppServiceHelper.findBean(JdbcTemplateSupportDao.class, "jdbcTemplateSupportDao");
        //获取数据源配置
        JdbcTemplateManager jdbcTemplateManager = jdbcTemplateSupportDao.getJdbcTemplateManager();
        JdbcTemplate jdbcTemplate = jdbcTemplateManager.getJdbcTemplate(dataSourceId);
        //mysql数据源只更新mysql脚本，oracle只执行oracle脚本
        String tmpDbType = jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId);
        Map<String, List<String>> tmpSqlList = sqlListMap.get(tmpDbType);
        //执行sql
        if (tableDefVOList.size() != 0) {
            Map<String, List<String>> creatTableSql = this.getCreateTableSql(tmpDbType, tableDefVOList);
            Iterator<Entry<String, List<String>>> iter = creatTableSql.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, List<String>> entry = iter.next();
                for (String sql : entry.getValue()) {
                    simpleJdbcTemplateSupportDao.executeUpate(sql, entry.getKey(), dataSourceId);
                }
                message.append(entry.getKey()).append("、");
            }
            message.deleteCharAt(message.length() - 1).append("表创建完成!");
        }
        if (tableStr.length() != 0) {
            //批量更新sql
            for (Map.Entry<String, List<String>> entry : tmpSqlList.entrySet()) {
                List<String> sqlLists=entry.getValue();
                for(String sql:sqlLists){
                    jdbcTemplate.execute(sql);
                }
            }
            tableStr.append("更新完成!");
            message.append(tableStr);
        }
        if (message.length() == 0)
            message.append("没有变更!");

        return message.toString();
    }


    @Override
    public Map<String, List<String>> getDbTypeSqlList(String tableName, String dataSource) throws Exception {
        Object[] createTableLinkData = getCreateTableLinkData(tableName, dataSource);

        List<TableDefVO> tableDefVOList = (List<TableDefVO>) createTableLinkData[0];
        Map<String, Map<String, List<String>>> sqlListMap = (Map<String, Map<String, List<String>>>) createTableLinkData[1];
        StringBuilder tableStr = (StringBuilder) createTableLinkData[2];

        //处理脚本，生成文件。
        Map<String, List<String>> dbTypeSqlList = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, Map<String, List<String>>> tmpMap : sqlListMap.entrySet()) {
            List<String> sqlList = new ArrayList<String>();
            if (tableDefVOList.size() != 0) {
                sqlList.add("创建表语句：");
                Map<String, List<String>> creatTableSql = this.getCreateTableSql(tmpMap.getKey(), tableDefVOList);
                Iterator<Map.Entry<String, List<String>>> iter = creatTableSql.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, List<String>> entry = iter.next();
                    sqlList.addAll(entry.getValue());
                }
            }
            if (tableStr.length() != 0) {
                sqlList.add("更新表语句：");
                for (Map.Entry<String, List<String>> entry : tmpMap.getValue().entrySet()) {
                    sqlList.addAll(entry.getValue());
                }
            }
            dbTypeSqlList.put(tmpMap.getKey(), sqlList);
        }

        return dbTypeSqlList;
    }

    private Object[] getCreateTableLinkData(String tbname, String dataSourceId) throws Exception {
        List<TableDefVO> tableDefVOList = new ArrayList<TableDefVO>();
        //记录更新的表名
        StringBuilder tableStr = new StringBuilder();
        //预览sql，添加oracle的脚本
        Map<String, Map<String, List<String>>> sqlListMap = new LinkedHashMap<String, Map<String, List<String>>>();
        //如果为mysql数据源，生成mysql和oracle的ddl语句。当为oracle数据源时，只生成oracle的语句。
        sqlListMap.put(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), new HashMap<String, List<String>>());


        //将数据库中表列转换为TableColumnVO对象，然后后表定义中数据进行比较
        //判断表定义主键
        TableDefVO tableDefVO = simpleJdbcTemplateSupportDao.queryDetailedTableByTbname(tbname, dataSourceId);
        if (tableDefVO != null) {
            Long tabledefid = tableDefVO.getTabledefid();

            String schema = jdbcTemplateSupportDao.getDataSourceManager().getShards().get(dataSourceId).getDataSource().getConnection().getCatalog();
            String catalog = schema;
            List<TableColumnVO> dbTableColumnVOList = simpleJdbcTemplateSupportDao.getTableColVoList(
                    catalog, schema, tbname, tabledefid, dataSourceId);
            //为0时表示数据库中没有该表，则进行新增
            if (dbTableColumnVOList == null || dbTableColumnVOList.size() == 0) {
                tableDefVOList.add(tableDefVO);
            } else {
                SqlBuilderManager sqlBuilderManager = jdbcTemplateSupportDao.getSqlBuilderManager();
                //比较差异，包括两种，新增和修改
                Object[] diffInfos = getDiffInfo(tableDefVO.getColumnList(), dbTableColumnVOList, true);
                Map<String, TableColumnVO> pdmTableColInfoMap = (Map<String, TableColumnVO>) diffInfos[0];
                List<TableColumnVO> updateTableColList = new ArrayList<>();// (List<TableColumnVO>) diffInfos[1];
                List<TableColumnVO> changePkColList = new ArrayList<>();//(List<TableColumnVO>) diffInfos[2];
                //生成ddl语句
                for (Map.Entry<String, Map<String, List<String>>> tmpMap : sqlListMap.entrySet()) {
                    //拼写变更字段
                    StringBuilder singleTableStr = new StringBuilder();
                    //记录涉及主键字段
                    List<String> sqlList = new ArrayList<String>();
                    if (pdmTableColInfoMap.size() != 0) {
                        List<TableColumnVO> tableColumnVOList = new ArrayList<TableColumnVO>();
                        tableColumnVOList.addAll(pdmTableColInfoMap.values());
                        sqlList.addAll(sqlBuilderManager.getAddColByTableColVO(tmpMap.getKey(), tbname, tableColumnVOList));
                        singleTableStr.append("添加列:");
                        for (TableColumnVO tableColumnVO : tableColumnVOList) {
                            singleTableStr.append(tableColumnVO.getColcode()).append("、");
                        }
                        singleTableStr.deleteCharAt(singleTableStr.length() - 1);
                    }
                    if (updateTableColList.size() != 0) {
                        sqlList.addAll(sqlBuilderManager.getUpdateColByTableColVO(tmpMap.getKey(), tbname, updateTableColList));
                        if (singleTableStr.length() != 0)
                            singleTableStr.append(";");
                        singleTableStr.append("更新列:");
                        for (TableColumnVO tableColumnVO : updateTableColList) {
                            singleTableStr.append(tableColumnVO.getColcode()).append("、");
                        }
                        singleTableStr.deleteCharAt(singleTableStr.length() - 1);
                    }
                    if (singleTableStr.length() != 0) {
                        singleTableStr.insert(0, "表" + tbname + ",").append("\r\n");
                        tableStr.append(singleTableStr);
                    }
                    //处理主键变更
                    if (changePkColList.size() != 0) {
                        sqlList.addAll(sqlBuilderManager.getUpdatePkByTableColVO(tmpMap.getKey(), tbname, changePkColList));
                    }
                    tmpMap.getValue().put(tbname, sqlList);
                }
            }
        }
        return new Object[]{tableDefVOList, sqlListMap, tableStr};
    }

    private Object[] getDiffInfo(List<TableColumnVO> pdmOrTDefTableColList, List<TableColumnVO> dbTableColList, boolean isLinkPdm) {
        //pdm定义的列信息
        Map<String, TableColumnVO> pdmTableColInfoMap = new HashMap<String, TableColumnVO>();
        if (ObjectUtil.isNotEmpty(pdmOrTDefTableColList)) {
            for (TableColumnVO tableColumnVO : pdmOrTDefTableColList) {
                if (tableColumnVO.getDf() == null || tableColumnVO.getDf() == SyConstant.SY_TRUE) {
                    continue;
                }
                pdmTableColInfoMap.put(tableColumnVO.getColcode().toLowerCase(), tableColumnVO);
            }
        }
        // 数据库中表定义的列信息
        Map<String, TableColumnVO> dbTableColInfoMap = new HashMap<String, TableColumnVO>();
        if (ObjectUtil.isNotEmpty(dbTableColList)) {
            for (TableColumnVO tableColumnVO : dbTableColList) {
                if (tableColumnVO.getDf() == null || tableColumnVO.getDf() == SyConstant.SY_TRUE) {
                    continue;
                }
                dbTableColInfoMap.put(tableColumnVO.getColcode(), tableColumnVO);
            }
        }
        // 比较数据库中和pdm的差异
        List<TableColumnVO> updateTableColList = new ArrayList<TableColumnVO>();
        //记录主键的变化，创建表的时候使用，isLinkPdm为true时。
        List<TableColumnVO> changePkColList = new ArrayList<TableColumnVO>();
        //获取关联数据库类型
        String dbType = DBType.MYSQL.getCode();
        for (Map.Entry<String, TableColumnVO> entry : dbTableColInfoMap
                .entrySet()) {
            // 如果表定义中存在列，在pdm中没有，这里不用检查，跳过。
            if (!pdmTableColInfoMap.containsKey(entry.getKey())) {
                continue;
            }
//            TableColumnVO pdmTableColumnVO = pdmTableColInfoMap.get(entry
//                    .getKey());
//            TableColumnVO dbTableColumnVO = entry.getValue();
//
//            //判断coltype是否修改
//            //boolean isChgColType = false;
//            //isChgColType = !dbTableColumnVO.getColtype().equals(pdmTableColumnVO.getColtype());
//            //包括其他属性值是否修改
//            //取消自增判断
//            //|| !isEqualInt(dbTableColumnVO.getIsautoinc(),pdmTableColumnVO.getIsautoinc())
//            //oracle数据库date类型取消长度的比较
//            boolean isChange = false;
//            if (//isChgColType||
//                     (!(dbType.equals(DBType.ORACLE.getCode()) && "DATE".equals(dbTableColumnVO.getOra_coltype())) &&
//                    !isEqualInt(dbTableColumnVO.getCollen(), pdmTableColumnVO.getCollen()))
//                    || !isEqualInt(dbTableColumnVO.getColscale(), pdmTableColumnVO.getColscale())
//                    //|| !isEqualInt(dbTableColumnVO.getIspk(), pdmTableColumnVO.getIspk())
//                    //|| !isEqualInt(dbTableColumnVO.getIsnotnull(), pdmTableColumnVO.getIsnotnull())
//                    //|| !isEqualStr(dbTab leColumnVO.getColdefault(), pdmTableColumnVO.getColdefault())
//                    //|| !isEqualStr(dbTableColumnVO.getColname(), pdmTableColumnVO.getColname())
//            )
//            //|| !isNameAndCommentEqual(dbTableColumnVO.getColname(),dbTableColumnVO.getColdesc(),pdmTableColumnVO.getColname(),pdmTableColumnVO.getColdesc())
//            //先不检查注释的差异
//            {
//                isChange = true;
//                if (!isLinkPdm) {
//                   // dbTableColumnVO.setColtype(pdmTableColumnVO.getColtype());
//                   // dbTableColumnVO.setOra_coltype(pdmTableColumnVO.getOra_coltype());
//                    dbTableColumnVO.setCollen(pdmTableColumnVO.getCollen());
//                    dbTableColumnVO.setColscale(pdmTableColumnVO.getColscale());
//                    //dbTableColumnVO.setIspk(pdmTableColumnVO.getIspk());
//                   // dbTableColumnVO.setIsnotnull(pdmTableColumnVO.getIsnotnull());
//                    //dbTableColumnVO.setIsautoinc(pdmTableColumnVO.getIsautoinc());
//                  //  dbTableColumnVO.setColdefault(pdmTableColumnVO.getColdefault());
//                    //dbTableColumnVO.setColdesc(pdmTableColumnVO.getColdesc());
//                   // dbTableColumnVO.setColname(pdmTableColumnVO.getColname());
//                } else {
//                    if (dbTableColumnVO.getIspk().intValue() != pdmTableColumnVO.getIspk().intValue())
//                        changePkColList.add(pdmTableColumnVO);
//                }
//
//
//
//            }
//            if (isChange) {
//                pdmTableColumnVO.setColtype(dbTableColumnVO.getColtype());
//                pdmTableColumnVO.setOra_coltype(dbTableColumnVO.getOra_coltype());
//                pdmTableColumnVO.setIspk(dbTableColumnVO.getIspk());
//                pdmTableColumnVO.setIsnotnull(dbTableColumnVO.getIsnotnull());
//                pdmTableColumnVO.setIsautoinc(dbTableColumnVO.getIsautoinc());
//                pdmTableColumnVO.setColdefault(dbTableColumnVO.getColdefault());
//                pdmTableColumnVO.setColdesc(dbTableColumnVO.getColdesc());
//                pdmTableColumnVO.setColname(dbTableColumnVO.getColname());
//                if (!isLinkPdm) {
//                    updateTableColList.add(dbTableColumnVO);
//                } else {
//                    updateTableColList.add(pdmTableColumnVO);
//                }
//            }
            // 移除列,用于最后判断pdm中多余的列，这些需要同步新增到数据库
            pdmTableColInfoMap.remove(entry.getKey());
        }
        if (isLinkPdm)
            return new Object[]{pdmTableColInfoMap, updateTableColList, changePkColList};
        else
            return new Object[]{pdmTableColInfoMap, updateTableColList};
    }

    private boolean isEqualInt(Integer int1, Integer int2) {
        if (int1 == null && int2 == null)
            return true;
        else {
            if (int1 == null && int2 != null)
                return false;
            else if (int1 != null && int2 == null)
                return false;
            else
                return int1.intValue() == int2.intValue();
        }
    }

    private boolean isEqualStr(String str1, String str2) {
        if (StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2))
            return true;
        else {
            if (StringUtils.isEmpty(str1) && StringUtils.isNotEmpty(str2))
                return false;
            else if (StringUtils.isNotEmpty(str1) && StringUtils.isEmpty(str2))
                return false;
            else
                return str1.equals(str2);
        }
    }


    private Map<String, List<String>> getCreateTableSql(String dbType, List<TableDefVO> tableDefVOList) throws Exception {
        Map<String, List<String>> dllSql = new HashMap<String, List<String>>();
        SqlBuilderManager sbManager = new SqlBuilderManager();
        for (TableDefVO tableDefVO : tableDefVOList) {
            dllSql.put(tableDefVO.getTable_code(), sbManager.getCreateTableSql(dbType, tableDefVO));
        }
        return dllSql;
    }

    @Override
    public void initDataBase(ParamVO paramVO) throws IOException, HDException {
        String initSql = MetaDataFileUtils.readFile2Str(new File(DbHandleServiceImpl.class.getClassLoader().getResource("/initsql/init.sql").getPath()));
        String dataSourceId = dataSourceCreator.getDataSourceId(paramVO.getProject(), paramVO.getEnv());
        dataSourceCreator.getDataSource(dataSourceId);
        for (String sql : initSql.split(";")) {
            simpleJdbcTemplateSupportDao.executeSql(sql, dataSourceId);
        }
    }

}
