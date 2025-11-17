package com.hayden.hap.upgrade.service.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hayden.hap.cache.itf.ICacheService;
import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.dbop.db.util.ResourceUtil;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.entity.tenant.TenantVO;
import com.hayden.hap.db.dataSource.entity.MetaRelationVO;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;
import com.hayden.hap.db.dataSource.itf.IDataSourceGeneratorService;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.db.dataSource.itf.ISimpleSqlBuilder;
import com.hayden.hap.serial.JsonUtils;
import com.hayden.hap.upgrade.entity.PatchFileVO;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.entity.UpgradeContext;
import com.hayden.hap.upgrade.itf.IDataHandleService;
import com.hayden.hap.upgrade.itf.ILogsService;
import com.hayden.hap.upgrade.itf.IProgressService;
import com.hayden.hap.upgrade.utils.MetaDataFileUtils;
import com.hayden.hap.utils.MetaDataRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 17:04
 */
@Service("commonDataHandleService")
public class ComDataHandleServiceImpl implements IDataHandleService {

    @Value("${META.UPGRADE.ISBUYCOLUMN}")
    private String ISBUYCOLUMN;

    @Autowired
    private MetaDataRelation metaDataRelation;
    @Autowired
    private IProgressService progressService;
    @Autowired
    private ISimpleSqlBuilder mdSqlBuilder;
    @Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;
    @Autowired
    private IDataSourceGeneratorService dataSourceGeneratorService;
    @Autowired
    private ILogsService logsService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ICacheService cacheService;

    @Override
    public void metaDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception {
        commonDataUpgradeHandle(patchFile, paramVO, progressModuleVO);
    }

    //元数据导入通用处理方法
    @Override
    public void commonDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception {
        //数据源id
        String dataSourceId = paramVO.getDataSourceId();
        //上下文对象
        UpgradeContext upgradeContext = new UpgradeContext();
        upgradeContext.setDataSourceId(dataSourceId);
        //处理升级文件类型
        PatchFileVO patchFileVO = new PatchFileVO(patchFile);

        logsService.setLogs(paramVO, patchFileVO, patchFileVO.getFileName() + "预处理数据", progressModuleVO);

        //预处理数据得到实际处理数据
        List<AbstractVO> vos = initUpgrade(upgradeContext, patchFile, patchFileVO);

        //得到升级sql
        SqlResultVO[] sqlResultVOS = getUpgradePreSql(upgradeContext);
        //取得升级租户，如果没有传租户则全部升级
        List<TenantVO> tenantvos = getTenantVOs(paramVO, dataSourceId);
        if (patchFileVO.getTenantid().longValue()!=1L){
            tenantvos = tenantvos.stream().filter(x->x.getTenantpk().longValue()==patchFileVO.getTenantid().longValue()).collect(Collectors.toList());
        }
        //得到升级进度
        float dataPct = progressModuleVO.getPct() / (vos.size() * tenantvos.size());

        logsService.setLogs(paramVO, patchFileVO, patchFileVO.getFileName() + "准备升级", progressModuleVO);
        for (TenantVO tenantVO : tenantvos) {
            Long tenantid = tenantVO.getTenantpk();

            logsService.setLogs(paramVO, patchFileVO, patchFileVO.getFileName() + " 正在升级租户 tenantid=" + tenantid, progressModuleVO);

            for (AbstractVO vo : vos) {
                //先更新进度
                progressModuleVO.setProgress(progressModuleVO.getProgress() + dataPct);
                progressService.setProgressModuleVO(paramVO, progressModuleVO);
                //当前执行数据放入上下文
                upgradeContext.setTenantid(tenantid);
                upgradeContext.setVo(vo);

                if (this.doCommonUpdate(upgradeContext, paramVO, sqlResultVOS)) {
                    continue;
                }
                //特殊处理方法
                this.spicelMetaDataHandle(upgradeContext, paramVO);
            }
            //清除redis缓存
            try{
                clearRedisCache(upgradeContext, paramVO);
            }catch (HDException e){
                logsService.setLogs(paramVO, patchFileVO, patchFileVO.getFileName() + " 租户 tenantid=" + tenantid + " 清楚redis缓存失败", progressModuleVO);
            }


            logsService.setLogs(paramVO, patchFileVO, patchFileVO.getFileName() + " 租户 tenantid=" + tenantid + " 升级完成", progressModuleVO);
        }



        clearCache(upgradeContext);

        logsService.setLogs(paramVO, patchFileVO, patchFileVO.getFileName() + "升级完成", progressModuleVO);
        progressModuleVO.setLastfilets(patchFileVO.getTimestamp());
        progressModuleVO.setCurrentver(paramVO.getVersion());

        if(patchFileVO.getVersion()!=null&&patchFileVO.getOldtimestap()!=null){
            progressModuleVO.setProlastfilets(patchFileVO.getOldtimestap());
            progressModuleVO.setProductver(patchFileVO.getVersion());
        }

        progressService.setProgressModuleVO(paramVO, progressModuleVO);
    }

    @Override
    public boolean doCommonUpdate(UpgradeContext upgradeContext, ParamVO paramVO, SqlResultVO[] sqlResultVOS) throws Exception {
        AbstractVO vo = upgradeContext.getVo();
        String dataSourceId = upgradeContext.getDataSourceId();
        //拼装条件
        DynaSqlVO dynaSqlVO = getUniqueWhereClause(vo, upgradeContext.getUniqueColumns());
        if (dynaSqlVO != null) {
            this.addWhereParam(dynaSqlVO,upgradeContext);
            //查询所选租户下存在不存在此条元数据
            VOSet<AbstractVO> oldDataVOs = simpleJdbcTemplateSupportDao.query(vo.getTableName(), dynaSqlVO, dataSourceId);
            //存在则更新不存在新增
            if (ObjectUtil.isNotNull(oldDataVOs) && ObjectUtil.isNotEmpty(oldDataVOs.getVoList())) {
                AbstractVO oldDataVO = oldDataVOs.getVoList().get(0);
                if (oldDataVO.getString("product_flag") != null && oldDataVO.getString("product_flag").equals("2"))
                    return true;
                this.preDataHandle(upgradeContext, oldDataVO, dataSourceId, paramVO);
                simpleJdbcTemplateSupportDao.update(vo, sqlResultVOS[0], dataSourceId);
            } else {
                this.preDataHandle(upgradeContext, null, dataSourceId, paramVO);
                if (",sy_form_item,sy_form_button,sy_form_item_mobile,sy_form_button_mobile,sy_perm_package_item,sy_export_template_item,".contains("," + upgradeContext.getTableDefVO().getTable_code() + ",")) {
                    if (upgradeContext.getParentTableDefVO() != null && upgradeContext.getParentVO() != null
                            && upgradeContext.getVo().get(upgradeContext.getMetaRelationVO().getParentColumn()) != null) {
                        simpleJdbcTemplateSupportDao.update(vo, sqlResultVOS[1], dataSourceId);
                    }
                }else{
                    simpleJdbcTemplateSupportDao.update(vo, sqlResultVOS[1], dataSourceId);
                }
            }
        }
        return false;
    }

    @Override
    public void addWhereParam(DynaSqlVO dynaSqlVO, UpgradeContext upgradeContext){
        dynaSqlVO.addWhereParam("tenantid", upgradeContext.getTenantid());
    }

    /**
     * 初始化数据
     *
     * @param upgradeContext
     * @param patchFile
     * @param patchFileVO
     * @return
     * @throws Exception
     */
    private List<AbstractVO> initUpgrade(UpgradeContext upgradeContext, File patchFile, PatchFileVO patchFileVO) throws Exception {

        String dataSourceId = upgradeContext.getDataSourceId();
        //元数据类型及其配置vo
        String metaDataType = patchFileVO.getMetaDataType();
        MetaRelationVO metaRelationVO = metaDataRelation.getMetaDataRelation(metaDataType);
        String[] uniqueColumns = metaRelationVO.getImportColumn().split(",");
        //得到升级数据实体
        Class clz = ResourceUtil.classForName(metaRelationVO.getNodevo());
        String jsonData = MetaDataFileUtils.readFile2Str(patchFile);
        List<String> colList = getColList(jsonData);
        //List<String> colList =null;

        //得到元数据基础vo
        AbstractVO abstractVO = (AbstractVO) clz.newInstance();
        TableDefVO tableDefVO = simpleJdbcTemplateSupportDao.queryDetailedTableByTbname(abstractVO.getTableName(), colList, dataSourceId);

        upgradeContext.setTableDefVO(tableDefVO);
        upgradeContext.setVo(abstractVO);
        upgradeContext.setMetaRelationVO(metaRelationVO);
        upgradeContext.setUniqueColumns(uniqueColumns);
        upgradeContext.setJsonData(jsonData);
        //得到父表相关信息
        this.getParentData(upgradeContext, dataSourceId);

        return Json2VOs(jsonData, clz);
    }

    public List<String> getColList(String jsonData) throws IOException {
        List<String> coList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) mapper.readTree(jsonData);
        if (arrayNode != null && arrayNode.size() > 0) {
            ObjectNode objectNode = (ObjectNode) arrayNode.get(0);
            Iterator<String> iterator = objectNode.fieldNames();
            while (iterator.hasNext()) {
                coList.add(iterator.next());
            }
        }
        return coList;
    }

    private SqlResultVO[] getUpgradePreSql(UpgradeContext upgradeContext) {
        MetaRelationVO metaRelationVO = upgradeContext.getMetaRelationVO();
        AbstractVO abstractVO = upgradeContext.getVo();
        TableDefVO tableDefVO = upgradeContext.getTableDefVO();
        //预处理sql语句
        DynaSqlVO dynaSqlVO = getUniqueWhereClause(abstractVO, upgradeContext.getUniqueColumns());
        dynaSqlVO.addWhereParam("tenantid", 1);
        String excludeCols = metaRelationVO.getImportColumn();

        SqlResultVO updateSqlResultVO = mdSqlBuilder.getUpdateSql(tableDefVO, dynaSqlVO, excludeCols);
        SqlResultVO insertSqlResultVO = mdSqlBuilder.getInsertSql(tableDefVO, dynaSqlVO, excludeCols);

        return new SqlResultVO[]{updateSqlResultVO, insertSqlResultVO};
    }

    private List<TenantVO> getTenantVOs(ParamVO paramVO, String dataSourceId) {
        List<TenantVO> tenantvos = new ArrayList<>();
        if (paramVO.getTenantid() == null) {
            tenantvos = getProjectTenant(dataSourceId);
        } else {
            TenantVO tenantVO = new TenantVO();
            tenantVO.setTenantpk(paramVO.getTenantid());
            tenantvos.add(tenantVO);
        }
        return tenantvos;
    }

    /**
     * 清理元数据缓存
     *
     * @param upgradeContext
     */
    private void clearCache(UpgradeContext upgradeContext) {
        TableDefVO tableDefVO = upgradeContext.getTableDefVO();
        if (tableDefVO != null) {
            String code = tableDefVO.getTable_code().toUpperCase();

            if (cacheManager.getCache(code) != null)
                cacheManager.getCache(code).clear();
        }
    }

    public void clearRedisCache(UpgradeContext upgradeContext, ParamVO paramVO) throws HDException{
        cacheService.evict(upgradeContext, paramVO);
    }

    /**
     * 数据预处理
     *
     * @param
     */
    @Override
    public void preDataHandle(UpgradeContext upgradeContext, AbstractVO oldvo, String dataSourceId, ParamVO paramVO) throws Exception {
        String pkcol = upgradeContext.getTableDefVO().getPkColumnVO().getColcode();
        Long tenantid = upgradeContext.getTenantid();
        Long pkValue;
        Long parentid;

        if (oldvo == null) {
            pkValue = Long.parseLong(dataSourceGeneratorService.generate(dataSourceGeneratorService.getPkColGencode(upgradeContext.getTableDefVO()), dataSourceId));
            parentid = this.getParentId(upgradeContext, dataSourceId);
        } else {
            pkValue = oldvo.getLong(pkcol);
            tenantid = oldvo.getLong("tenantid");
            parentid=oldvo.getLong(upgradeContext.getMetaRelationVO().getParentColumn());
            if(parentid==null){
                parentid = this.getParentId(upgradeContext, dataSourceId);
            }
            //parentid = oldvo.getLong(upgradeContext.getParentTableDefVO() != null ? upgradeContext.getParentTableDefVO().getPkColumnVO().getColcode() : null);
        }

        upgradeContext.getVo().set(pkcol, pkValue);
        upgradeContext.getVo().set("tenantid", tenantid);

        if (upgradeContext.getParentTableDefVO() != null && upgradeContext.getParentVO() != null) {
            upgradeContext.getVo().set(upgradeContext.getMetaRelationVO().getParentColumn(), parentid);
        }

        if (getIsBuyColumnByType(upgradeContext.getMetaRelationVO().getNodetype()) != null) {
            upgradeContext.getVo().setInt(getIsBuyColumnByType(upgradeContext.getMetaRelationVO().getNodetype()), 1);
        }
    }

    @Override
    public Long getParentId(UpgradeContext upgradeContext, String dataSourceId) throws Exception {
        Long id = null;
        if (upgradeContext.getParentTableDefVO() != null && upgradeContext.getParentVO() != null) {
            String[] uniqueColumns = upgradeContext.getParentMetaRelationVO().getExportColumn().split(",");
            String[] childUniqueColumns = upgradeContext.getMetaRelationVO().getExportColumn().split(",");

            if (uniqueColumns.length == childUniqueColumns.length) {

                DynaSqlVO dynaSqlVO = new DynaSqlVO();
                String pkcol = upgradeContext.getParentTableDefVO().getPkColumnVO().getColcode();

                for (int i = 0; i < uniqueColumns.length; i++) {
                    dynaSqlVO.addWhereParam(uniqueColumns[i], upgradeContext.getVo().get(childUniqueColumns[i]));
                }
                dynaSqlVO.addWhereParam("tenantid", upgradeContext.getTenantid());
                VOSet<AbstractVO> abstractVOS = simpleJdbcTemplateSupportDao.query(upgradeContext.getParentVO(), dynaSqlVO, dataSourceId);

                if (ObjectUtil.isNotNull(abstractVOS) && ObjectUtil.isNotEmpty(abstractVOS.getVoList())) {
                    id = abstractVOS.getVoList().get(0).getLong(pkcol);
                }
            } else {
                throw new HDException("元数据" + upgradeContext.getMetaRelationVO().getNodetype() + "父子导出编码配置错误，唯一字段个数不一致，请检查");
            }
        }
        return id;
    }

    @Override
    public void getParentData(UpgradeContext upgradeContext, String dataSourceId) throws Exception {
        //得到父对象
        MetaRelationVO parentMetaRelationVO = null;
        AbstractVO parentVO = null;
        TableDefVO parentTableDefVO = null;
        if (ObjectUtil.isNotNull(upgradeContext.getMetaRelationVO().getParentColumn())) {
            parentMetaRelationVO = metaDataRelation.getMetaDataParentRelation(upgradeContext.getMetaRelationVO().getNodetype());
            if (parentMetaRelationVO != null) {
                Class parentClz = ResourceUtil.classForName(parentMetaRelationVO.getNodevo());
                parentVO = (AbstractVO) parentClz.newInstance();
                parentTableDefVO = simpleJdbcTemplateSupportDao.queryDetailedTableByTbname(parentVO.getTableName(), dataSourceId);
            }
        }
        upgradeContext.setParentMetaRelationVO(parentMetaRelationVO);
        upgradeContext.setParentTableDefVO(parentTableDefVO);
        upgradeContext.setParentVO(parentVO);
    }

    private List<TenantVO> getProjectTenant(String dataSourceId) {
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("isenable", 1);
        VOSet voSet = simpleJdbcTemplateSupportDao.query(new TenantVO(), dynaSqlVO, dataSourceId);
        if (voSet != null && voSet.getVoList() != null)
            return voSet.getVoList();
        return new ArrayList<>();
    }

    /**
     * 组装条件
     *
     * @param vo
     * @param uniqueColumns
     * @return
     */
    private DynaSqlVO getUniqueWhereClause(AbstractVO vo, String[] uniqueColumns) {
        boolean hasWhere = false;
        DynaSqlVO dynaSqlVO = new DynaSqlVO();

        for (String uniqueColumn : uniqueColumns) {
            hasWhere = true;
            dynaSqlVO.addWhereParam(uniqueColumn, vo.get(uniqueColumn));
        }

        if (hasWhere)
            return dynaSqlVO;
        else
            return null;
    }

    /**
     * json字符串转实体
     *
     * @param json
     * @param clz
     * @param <T>
     * @return
     * @throws HDException
     */
    private <T extends AbstractVO> List<T> Json2VOs(String json, Class<T> clz) throws Exception {
        return JsonUtils.parseArray(json, clz);
    }

    /**
     * 特殊处理方法
     *
     * @param
     * @param upgradeContext
     * @return
     */
    @Override
    public void spicelMetaDataHandle(UpgradeContext upgradeContext, ParamVO paramVO) throws Exception {

    }

    private String getIsBuyColumnByType(String metaDataType) throws HDException {
        Map<String, String> isbuyMap = JsonUtils.parse(ISBUYCOLUMN, Map.class);
        if (isbuyMap.containsKey(metaDataType)) {
            return isbuyMap.get(metaDataType);
        }
        return null;
    }

}
