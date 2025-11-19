package com.hayden.hap.dbop.dao;

import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface BaseDao {

    <T extends AbstractVO> T insert(T vo);

    <T extends AbstractVO> T insert(T vo, String dataSourceId);

    <T extends AbstractVO> T insert(T vo, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> T insert(T vo, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO);

    <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> insertBatchHavePks(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName, int perBatchSize);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, String dataSourceId);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, int perBatchSize);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName, int perBatchSize);

    <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList, DynaSqlVO dynaSqlVO, int perBatchSize);

    <T extends AbstractVO> int update(T vo);

    <T extends AbstractVO> int update(T vo, String dataSourceId);

    <T extends AbstractVO> int update(T vo, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int update(T vo, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO);

    <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int updateBatch(List<T> voList);

    <T extends AbstractVO> int updateBatch(List<T> voList, String dataSourceId);

    <T extends AbstractVO> int updateBatch(List<T> voList, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int updateBatch(List<T> voList, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int updateBatch(List<T> voList, int perBatchSize);

    <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO);

    <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName, int perBatchSize);

    <T extends AbstractVO> int updateBatch(List<T> voList, DynaSqlVO dynaSqlVO, int perBatchSize);

    <T extends AbstractVO> int delete(T vo);

    <T extends AbstractVO> int delete(T vo, String dataSourceId);

    <T extends AbstractVO> int delete(T vo, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int delete(T vo, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO);

    <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO);

    <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);

    <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int delete(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int deleteBatch(List<T> voList);

    <T extends AbstractVO> int deleteBatch(List<T> voList, String dataSourceId);

    <T extends AbstractVO> int deleteBatch(List<T> voList, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int deleteBatch(List<T> voList, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int deleteByPK(T vo, String primaryKey);

    <T extends AbstractVO> int deleteByPK(T vo, String primaryKey, String dataSourceId);

    <T extends AbstractVO> int deleteByPK(T vo, String primaryKey, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int deleteByPK(T vo, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);

    int deleteByPK(String tableName, String primaryKey);

    int deleteByPK(String tableName, String primaryKey, String dataSourceId);


    int deleteByPK(String tableName, String primaryKey, String ruleName, String dataSourcePoolName);


    int deleteByPK(String tableName, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys);


    <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys, String dataSourceId);


    <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> int deleteByPKs(T vo, Collection<String> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> int deleteByPKsOfLong(T vo, Collection<Long> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);


    int deleteByPKs(String tableName, Collection<String> primaryKeys);


    int deleteByPKs(String tableName, Collection<String> primaryKeys, String dataSourceId);


    int deleteByPKs(String tableName, Collection<String> primaryKeys, String ruleName, String dataSourcePoolName);


    int deleteByPKs(String tableName, Collection<String> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);


    int deleteByPKsOfLong(String tableName, Collection<Long> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> T queryByPK(T vo, String primaryKey);


    <T extends AbstractVO> T queryByPK(T vo, String primaryKey, String dataSourceId);


    <T extends AbstractVO> T queryByPK(T vo, String primaryKey, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> T queryByPK(T vo, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> T queryByPK(T vo, String primaryKey, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPK(String tableName, String primaryKey);


    AbstractVO queryByPK(String tableName, String primaryKey, String dataSourceId);


    AbstractVO queryByPK(String tableName, String primaryKey, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPK(String tableName, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPK(String tableName, String primaryKey, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPK4update(String tableName, String primaryKey, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys);


    <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys, String dataSourceId);


    <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> queryByPKs(T vo, Collection<String> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo, Collection<Long> primaryKeys, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo, Collection<Long> primaryKeys, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO);


    <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO, String dataSourceId);


    <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);


    VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO);


    VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);


    VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);


    VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO);


    <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO, String dataSourceId);


    <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> query(Class<T> voClass, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);


    VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO);


    VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO, String dataSourceId);


    VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);


    VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO, String dataSourceId);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, String sql, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[] preStatementParamType, String tableName);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[] preStatementParamType, String tableName, String dataSourceId);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[] preStatementParamType, String tableName, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass, final String sql, DynaSqlVO dynaSqlVO, final Object[] preStatementParam, final int[] preStatementParamType, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName);


    <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName, String dataSourceId);


    <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName, String ruleName, String dataSourcePoolName);


    <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam, ResultSetExtractor<T> resultSetExtractor, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);


    int executeUpate(String sql, String tableName);


    int executeUpate(String sql, String tableName, String dataSourceId);


    int executeUpate(String sql, String tableName, String ruleName, String dataSourcePoolName);


    int executeUpate(String sql, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);


    int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName);


    int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String dataSourceId);


    int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String ruleName, String dataSourcePoolName);


    int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName);


    int executeUpate(String sql, Object[] preStatementParam, int[] preStatementParamType, String tableName, String dataSourceId, String ruleName, String dataSourcePoolName, boolean isAddTS);


    <T extends AbstractVO> int deleteByPKAndTenantid(T vo, String primaryKey, Long tenantid);


    <T extends AbstractVO> int deleteByPKAndTenantid(T vo, String primaryKey, Long tenantid, String dataSourceId);


    <T extends AbstractVO> int deleteByPKAndTenantid(T vo, String primaryKey, Long tenantid, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> int deleteByPKAndTenantid(T vo, String primaryKey, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    int deleteByPKAndTenantid(String tableName, String primaryKey, Long tenantid);


    int deleteByPKAndTenantid(String tableName, String primaryKey, Long tenantid, String dataSourceId);


    int deleteByPKAndTenantid(String tableName, String primaryKey, Long tenantid, String ruleName, String dataSourcePoolName);


    int deleteByPKAndTenantid(String tableName, String primaryKey, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> int deleteByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid);


    <T extends AbstractVO> int deleteByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid, String dataSourceId);


    <T extends AbstractVO> int deleteByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> int deleteByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo, Collection<Long> primaryKeys, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys, Long tenantid);


    int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys, Long tenantid, String dataSourceId);


    int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys, Long tenantid, String ruleName, String dataSourcePoolName);


    int deleteByPKsAndTenantid(String tableName, Collection<String> primaryKeys, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);

    int deleteByPKsOfLongAndTenantid(String tableName, Collection<Long> primaryKeys, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> T queryByPKAndTenantid(T vo, String primaryKey, Long tenantid);


    <T extends AbstractVO> T queryByPKAndTenantid(T vo, String primaryKey, Long tenantid, String dataSourceId);


    <T extends AbstractVO> T queryByPKAndTenantid(T vo, String primaryKey, Long tenantid, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> T queryByPKAndTenantid(T vo, String primaryKey, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> T queryByPKAndTenantid(T vo, String primaryKey, Long tenantid, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPKAndTenantid(String tableName, String primaryKey, Long tenantid);


    AbstractVO queryByPKAndTenantid(String tableName, String primaryKey, Long tenantid, String dataSourceId);


    AbstractVO queryByPKAndTenantid(String tableName, String primaryKey, Long tenantid, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPKAndTenantid(String tableName, String primaryKey, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPKAndTenantid(String tableName, String primaryKey, Long tenantid, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);


    AbstractVO queryByPKAndTenantid4update(String tableName, Long primaryKey, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid);


    <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid, String dataSourceId);


    <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid, String ruleName, String dataSourcePoolName);


    <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo, Collection<String> primaryKeys, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo, Collection<Long> primaryKeys, Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName);

    <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo, Collection<Long> primaryKeys, Long tenantid, Boolean isReadSlave, String dataSourceId, String ruleName, String dataSourcePoolName);


    Object getVOPkColValue(AbstractVO vo);


    List<TableColumnVO> getTableColVoList(String catalog, String schema, String tableName, Long parentid);


    int getCount(String tableName, DynaSqlVO dynaSqlVO);


    int getCount(String tableName, DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName);


    int getCount(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId);


    int getCount(String tableName, DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName, String dataSourcePoolName);


    Map<String, List<String>> getCreateTableSql(String dbType, List<TableDefVO> tableDefVO) throws HDException;

}
