package com.hayden.hap.db.service;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.BaseVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.db.orm.jdbc.*;
import com.hayden.hap.dbop.exception.DaoRuntimeException;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.db.orm.entity.ClobInfoVO;
import com.hayden.hap.dbop.db.orm.sql.ConnectionCallbackMetaData;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlResultVO;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.orm.sql.SqlBuilderManager;
import com.hayden.hap.dbop.db.orm.typeHandler.TypeHandlerRegistry;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.util.DBConstants;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;
import com.hayden.hap.db.dataSource.itf.IDataSourceGeneratorService;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 11:06
 */
@Service("simpleJdbcTemplateSupportDao")
public class SimpleJdbcTemplateSupportDao implements ISimpleJdbcTemplateSupportDao {

    private SqlBuilderManager sqlBuilderManager = new SqlBuilderManager();

    @Autowired
    private JdbcTemplateSupportDao jdbcTemplateSupportDao;
    @Autowired
    private IDataSourceGeneratorService dataSourceGeneratorService;

    private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    @Override
    public <T extends AbstractVO> int update(T vo, SqlResultVO sqlResultVO, String dataSourceId) {
        List<Object> preStatementParams = new ArrayList<Object>();

        for (String col : sqlResultVO.getPreStatementCols()) {
            preStatementParams.add(vo.get(col));
        }


        VOPreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(preStatementParams, sqlResultVO.getPreStatementParamJdbcTypes(), this.typeHandlerRegistry);
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        int updateResult = jdbcTemplate.update(sqlResultVO.getSql(), preparedStatementSetter);

        return updateResult;
    }

    @Override
    public <T extends AbstractVO> List<TableColumnVO> getTableColVoList(String catalog, String schema, String tableName, Long parentid, String dataSourceId) {
        List<TableColumnVO> tableColVoList = null;

        ObjectUtil.validNotNull(tableName, "sql is required.");
        String dbType = jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId);
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        //首先检查表是否存在
        String chkTableSql = sqlBuilderManager.getChkTableSql(dbType, tableName);
        Boolean isExistTable = (Boolean) jdbcTemplate.execute(chkTableSql, new PreparedStatementCallback<Object>() {

            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                ResultSet rs = ps.executeQuery();
                Boolean isExistTable = Boolean.valueOf(false);
                while (rs.next())
                    isExistTable = Boolean.valueOf(true);
                return isExistTable;
            }

        });
        if (!isExistTable.booleanValue())
            return tableColVoList;
        //获取列sql
        String colSql = sqlBuilderManager.getColSqlByTable(dbType, tableName);
        //执行sql，查询该表已定义的列数据
        Map<String, String> colMap = (Map<String, String>) jdbcTemplate.execute(colSql, new PreparedStatementCallback<Object>() {

            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                ResultSet rs = ps.executeQuery();
                Map<String, String> valMap = new HashMap<String, String>();
                while (rs.next())
                    valMap.put(rs.getString("Field"), rs.getString("Type"));
                return valMap;
            }

        });
        //这里执行上面语句的原因是，下面的语句获取的数据类型不对。依据上面的type，重置下列类型。
        ConnectionCallbackMetaData<TableColumnVO> connectionCallbackMetaData = new ConnectionCallbackMetaData<TableColumnVO>(dbType, catalog, schema, tableName, parentid);
        connectionCallbackMetaData.setColMap(colMap);
        tableColVoList = jdbcTemplate.execute(connectionCallbackMetaData);

        return tableColVoList;
    }

    @Override
    public TableDefVO queryDetailedTableByTbname(String tbname, List<String> colList, String dataSourceId) {
        if (!ObjectUtil.isNotNull(tbname)) {
            return null;
        } else {
            tbname = tbname.toLowerCase();
            StringBuffer sb = new StringBuffer();
            sb.append("select t1.tabledefid, t1.table_code, t1.modulecode, t1.table_name, t1.table_type, t1.issqllog ");
            sb.append(", t1.table_desc, t1.ddlsql, t1.classname");
            sb.append(", t1.created_by, t1.created_dt, t1.updated_by, t1.updated_dt, t1.ver, t1.df, t1.isenable");
            sb.append(", t2.tablecolumnid, t2.colcode, t2.table_code as col_tbname, t2.coltype, t2.ora_coltype, t2.collen, t2.colscale");
            sb.append(", t2.colname, t2.coldesc, t2.ispk, t2.coldefault, t2.isnotnull, t2.isautoinc, t2.gencode, t2.colorder");
            sb.append(", t2.created_by as col_created_by, t2.created_dt as col_created_dt, t2.updated_by as col_updated_by, t2.updated_dt as col_updated_dt, t2.ver as col_ver, t2.df as col_df");
            sb.append(" from sy_table_def t1 left join sy_table_column t2 on t1.table_code = t2.table_code");
            sb.append(" where t1.table_code=:tbname ");

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("tbname", tbname);

            if (colList != null && colList.size() > 0) {
                sb.append(" and (t2.colcode in (:codes) or t2.ispk=1 or t2.colcode='tenantid') ");
                mapSqlParameterSource.addValue("codes", colList);
            }

            List<TableDefVO> resultList = this.executeQueryByNameParameter(sb.toString(), mapSqlParameterSource, new ResultSetExtractor<TableDefVO>() {
                public TableDefVO extractData(ResultSet rs) throws SQLException, DataAccessException {
                    TableDefVO tableDefVO = null;
                    ArrayList columnList = null;

                    while (rs.next()) {
                        if (rs.getString("colcode") == null) {
                            return null;
                        }

                        Timestamp updatedDtx;
                        if (tableDefVO == null) {
                            tableDefVO = new TableDefVO();
                            tableDefVO.setTabledefid(rs.getLong("tabledefid"));
                            tableDefVO.setTable_code(rs.getString("col_tbname"));
                            tableDefVO.setModulecode(rs.getString("modulecode"));
                            tableDefVO.setTable_name(rs.getString("table_name"));
                            tableDefVO.setTable_type(rs.getInt("table_type"));
                            tableDefVO.setIssqllog(rs.getInt("issqllog"));
                            tableDefVO.setTable_desc(rs.getString("table_desc"));
                            tableDefVO.setDdlsql(rs.getString("ddlsql"));
                            tableDefVO.setClassname(rs.getString("classname"));
                            tableDefVO.setCreated_by(rs.getLong("created_by"));
                            Timestamp createdDt = rs.getTimestamp("created_dt");
                            if (createdDt != null) {
                                tableDefVO.setCreated_dt(new Date(createdDt.getTime()));
                            }

                            tableDefVO.setUpdated_by(rs.getLong("updated_by"));
                            updatedDtx = rs.getTimestamp("updated_dt");
                            if (updatedDtx != null) {
                                tableDefVO.setUpdated_dt(new Date(updatedDtx.getTime()));
                            }

                            tableDefVO.setVer(rs.getInt("ver"));
                            tableDefVO.setDf(rs.getInt("df"));
                            tableDefVO.setIsenable(rs.getInt("isenable"));
                            columnList = new ArrayList();
                            tableDefVO.setColumnList(columnList);
                        }

                        TableColumnVO columnVO = new TableColumnVO();
                        columnVO.setTabledefid(tableDefVO.getTabledefid());
                        columnVO.setTablecolumnid(rs.getLong("tablecolumnid"));
                        columnVO.setColcode(rs.getString("colcode"));
                        columnVO.setTable_code(rs.getString("col_tbname"));
                        columnVO.setColtype(rs.getString("coltype"));
                        columnVO.setOra_coltype(rs.getString("ora_coltype"));
                        columnVO.setCollen(Integer.parseInt(rs.getString("collen") == null ? "0" : rs.getString("collen")));
                        columnVO.setColscale(Integer.parseInt(rs.getString("colscale") == null ? "0" : rs.getString("colscale")));
                        columnVO.setColname(rs.getString("colname"));
                        columnVO.setColdesc(rs.getString("coldesc"));
                        columnVO.setIspk(rs.getInt("ispk"));
                        columnVO.setColdefault(rs.getString("coldefault"));
                        columnVO.setIsnotnull(rs.getInt("isnotnull"));
                        columnVO.setIsautoinc(rs.getInt("isautoinc"));
                        columnVO.setGencode(rs.getString("gencode"));
                        columnVO.setColorder(rs.getInt("colorder"));
                        columnVO.setCreated_by(rs.getLong("col_created_by"));
                        updatedDtx = rs.getTimestamp("col_created_dt");
                        if (updatedDtx != null) {
                            columnVO.setCreated_dt(new Date(updatedDtx.getTime()));
                        }

                        columnVO.setUpdated_by(rs.getLong("col_updated_by"));
                        Timestamp updatedDt = rs.getTimestamp("col_updated_dt");
                        if (updatedDt != null) {
                            columnVO.setUpdated_dt(new Date(updatedDt.getTime()));
                        }

                        columnVO.setVer(rs.getInt("col_ver"));
                        columnVO.setDf(rs.getInt("col_df"));
                        columnList.add(columnVO);
                        if (columnVO.getIspk() == 1) {
                            tableDefVO.setPkColumnVO(columnVO);
                            if (tableDefVO.getPkColumnVOList() == null) {
                                List<TableColumnVO> pkColumnVOList = new ArrayList();
                                pkColumnVOList.add(columnVO);
                                tableDefVO.setPkColumnVOList(pkColumnVOList);
                            } else {
                                tableDefVO.getPkColumnVOList().add(columnVO);
                            }
                        }
                    }

                    return tableDefVO;
                }
            }, dataSourceId);
            return ObjectUtil.isNotEmpty(resultList) ? (TableDefVO) resultList.get(0) : null;
        }
    }

    @Override
    public TableDefVO queryDetailedTableByTbname(String tbname, String dataSourceId) {
        return queryDetailedTableByTbname(tbname, null, dataSourceId);
    }

    @Override
    public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String dataSourceId) {
        String matchedSql = sql;
        List<T> resultList = new ArrayList();

        String newSql = sqlBuilderManager.getDialectSql(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), matchedSql, dynaSqlVO);
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);

        resultList.add(jdbcTemplate.query(newSql, preStatementParam, resultSetExtractor));

        return resultList;

    }

    @Override
    public <T> List<T> executeQueryByNameParameter(String sql, MapSqlParameterSource mapSqlParameterSource, ResultSetExtractor<T> resultSetExtractor, String dataSourceId) {
        String matchedSql = sql;
        List<T> resultList = new ArrayList();
        String newSql = sqlBuilderManager.getDialectSql(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), matchedSql, null);
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        resultList.add(namedParameterJdbcTemplate.query(newSql, mapSqlParameterSource, resultSetExtractor));

        return resultList;

    }

    @Override
    public <T extends AbstractVO> VOSet<T> query(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId) {
        TableDefVO tableDefVO = this.queryDetailedTableByTbname(tableName, dataSourceId);
        if ("SY_FORM_ITEM".equals(tableName.toUpperCase())) {
        	tableDefVO.setClassname("com.hayden.hap.common.entity.form.FormItemPCVOForExport");
		}

        VOSet<T> resultVOSet = new VOSet();
        List<T> voList = new ArrayList();
        int totalRows = 0;

        DynaSqlResultVO dynaSqlResultVO = sqlBuilderManager.getSelectSql(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), tableDefVO, dynaSqlVO, tableName);
        resultVOSet.setSql(dynaSqlResultVO.getSql());

        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(dynaSqlResultVO.getPreStatementParams(), dynaSqlResultVO.getPreStatementParamJdbcTypes(), this.typeHandlerRegistry);
        voList.addAll((Collection) jdbcTemplate.query(dynaSqlResultVO.getSql(), preparedStatementSetter, new VORowMapperResultSetExtractor(tableDefVO, this.typeHandlerRegistry)));
        if (dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage())) {
            totalRows += this.queryCount(dynaSqlResultVO.getSelectCountSql(), dataSourceId, preparedStatementSetter);
        }

        resultVOSet.setVoList(voList);
        if (dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage())) {
            dynaSqlVO.getPage().setTotalRows(totalRows);
            resultVOSet.setPage(dynaSqlVO.getPage());
        }

        return resultVOSet;
    }

    @Override
    public <T extends AbstractVO> VOSet<T> query(AbstractVO vo, DynaSqlVO dynaSqlVO, String dataSourceId) {
        return this.query(vo.getTableName(), dynaSqlVO, dataSourceId);
    }

    @Override
    public <T extends AbstractVO> VOSet<T> query(Class clz, DynaSqlVO dynaSqlVO, String dataSourceId) throws HDException {
        try {
            return this.query((AbstractVO) clz.newInstance(), dynaSqlVO, dataSourceId);
        } catch (InstantiationException e) {
            throw new HDException(e);
        } catch (IllegalAccessException e) {
            throw new HDException(e);
        }
    }

    @Override
    public int queryCount(String countSql, String dataSourceId, PreparedStatementSetter pss) {
        if (!ObjectUtil.isNotNull(countSql)) {
            return 0;
        } else {
            int count = 0;
            JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
            List<BaseVO> list = (List) jdbcTemplate.query(countSql, pss, new VORowMapperResultSetExtractor<BaseVO>((TableDefVO) null, this.typeHandlerRegistry) {
            });
            if (ObjectUtil.isNotEmpty(list)) {
                count = ((BaseVO) list.get(0)).getInt("totalCount");
            }
            return count;
        }
    }

    @Override
    public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String dataSourceId) {
        int updateResult = 0;

        String tableName = vo.getTableName();
        TableDefVO tableDefVO = this.queryDetailedTableByTbname(tableName, dataSourceId);

        DynaSqlResultVO dynaSqlResultVO = sqlBuilderManager.getUpdateSql(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), tableDefVO, vo, dynaSqlVO, tableDefVO.getTable_code());

        PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(dynaSqlResultVO.getPreStatementParams(), dynaSqlResultVO.getPreStatementParamJdbcTypes(), this.typeHandlerRegistry);
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        updateResult += jdbcTemplate.update(dynaSqlResultVO.getSql(), preparedStatementSetter);

        return updateResult;
    }

    @Override
    public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId) {
        VOSet<T> result = new VOSet();
        if (voList != null && !voList.isEmpty()) {
            try {
                TableDefVO tableDefVO = this.queryDetailedTableByTbname(voList.get(0).getTableName(), dataSourceId);
                String gencode = dataSourceGeneratorService.getPkColGencode(tableDefVO);
                String[] keys = dataSourceGeneratorService.generate(gencode, dataSourceId, voList.size());

                for (int i = 0; i < voList.size(); ++i) {
                    T t = (T) voList.get(i);
                    Long primaryKey = Long.parseLong(keys[i]);
                    this.setVOPkColValue(t, primaryKey, tableDefVO);
                }

                DynaSqlResultVO dynaSqlResultVO = sqlBuilderManager.getInsertBatchSql(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), tableDefVO, (List<AbstractVO>) voList, dynaSqlVO, tableDefVO.getTable_code());
                BatchPreparedStatementSetter batchPreparedStatementSetter = new VOBatchPreparedStatementSetter(
                        dynaSqlResultVO.getPreStatementParams(),
                        dynaSqlResultVO.getPreStatementParamJdbcTypes(),
                        typeHandlerRegistry, voList.size());
                VOBatchPreparedStatementCallback batchPreparedStatementCallback = new VOBatchPreparedStatementCallback(
                        batchPreparedStatementSetter, DBConstants.PER_BATCH_SIZE);
                JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
                jdbcTemplate.execute(dynaSqlResultVO.getSql(), batchPreparedStatementCallback);
                result.setVoList(voList);
                return result;
            } catch (Exception e) {
                throw new DaoRuntimeException(e.getMessage(), e);
            }
        } else {
            return result;
        }
    }

    public void setVOPkColValue(AbstractVO vo, Object pkValue, TableDefVO tableDefVO) {
        String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO().getColcode() : null;
        if (ObjectUtil.isNotNull(vo.getLong(pkColName))) {
            vo.set(pkColName, pkValue);
        }
    }

    @Override
    public int executeUpate(String sql, String tableName, String dataSourceId) {
        int updateResult = 0;
        Map<String, Object> paramMap = null;

        TableDefVO tableDefVO = null;
        if (ObjectUtil.isNotNull(tableName))
            tableDefVO = this.queryDetailedTableByTbname(tableName, dataSourceId);
        //判断isAddTS值是否需要添加更新ts

        sql = sqlBuilderManager.getSqlBuilder(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId)).addTS(sql, tableDefVO);
        //检查是否包含clob类型字段
        boolean isDealClob = false;

        ClobInfoVO clobInfoVO = null;
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        if (!isDealClob) {
            updateResult += jdbcTemplate.update(sql);
        } else {
            //更新
            int result = ClobUtil.updateClobData(jdbcTemplate, clobInfoVO, dataSourceId, sql, null, null);
            updateResult += result;
        }
        return updateResult;
    }

    @Override
    public void executeSql(String sql, String dataSourceId) {
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        jdbcTemplate.execute(sql);
    }

    @Override
    public <T extends AbstractVO> int insert(T vo, DynaSqlVO dynaSqlVO, String dataSourceId) {
        int insetResult = 0;
        String tableName = vo.getTableName();
        TableDefVO tableDefVO = this.queryDetailedTableByTbname(tableName, dataSourceId);

        DynaSqlResultVO dynaSqlResultVO = sqlBuilderManager.getInsertSql(jdbcTemplateSupportDao.getDataSourceManager().getDbType(dataSourceId), tableDefVO, vo, dynaSqlVO, tableDefVO.getTable_code());

        PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(dynaSqlResultVO.getPreStatementParams(), dynaSqlResultVO.getPreStatementParamJdbcTypes(), this.typeHandlerRegistry);
        JdbcTemplate jdbcTemplate = jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId);
        insetResult += jdbcTemplate.update(dynaSqlResultVO.getSql(), preparedStatementSetter);
        return insetResult;
    }


}
