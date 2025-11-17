package com.hayden.hap.dbop.service;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.itf.IBaseAllService;
import com.hayden.hap.dbop.itf.IBaseService;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BaseServiceImpl
 * @Description:
 * @author LUYANYING
 * @date 2015年4月15日 上午9:59:26
 * @version V1.0
 * 
 */
@Service("baseService")
public class BaseServiceImpl implements IBaseService {
	@Autowired
	IBaseAllService baseAllService;

	@Override
	public <T extends AbstractVO> T insert(T vo) {
		return baseAllService.insert(vo);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO) {
		return baseAllService.insert(vo, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList) {
		return baseAllService.insertBatch(voList);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatchHavePks(List<T> voList) {
		return baseAllService.insertBatchHavePks(voList);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			int perBatchSize) {
		return baseAllService.insertBatch(voList, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO) {
		return baseAllService.insertBatch(voList, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize) {
		return baseAllService.insertBatch(voList, dynaSqlVO, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> int update(T vo) {
		return baseAllService.update(vo);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO) {
		return baseAllService.update(vo, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList) {
		return baseAllService.updateBatch(voList);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			int perBatchSize) {
		return baseAllService.updateBatch(voList, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO) {
		return baseAllService.updateBatch(voList, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize) {
		return baseAllService.updateBatch(voList, dynaSqlVO, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> int updateBatchForList(List<T> voList,
			List<DynaSqlVO> dynaSqlVOList) {
		return baseAllService.updateBatchForList(voList, dynaSqlVOList);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo) {
		return baseAllService.delete(vo);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO) {
		return baseAllService.delete(vo, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO) {
		return baseAllService.delete(tableName, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> int deleteBatch(List<T> voList) {
		return baseAllService.deleteBatch(voList);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey) {
		return baseAllService.deleteByPK(vo, primaryKey);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, Long primaryKey) {
		return baseAllService.deleteByPK(vo, primaryKey);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid) {
		return baseAllService.deleteByPKAndTenantid(vo, primaryKey, tenantid);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid) {
		return baseAllService.deleteByPKAndTenantid(vo, primaryKey, tenantid);
	}

	@Override
	public int deleteByPK(String tableName, String primaryKey) {
		return baseAllService.deleteByPK(tableName, primaryKey);
	}

	@Override
	public int deleteByPK(String tableName, Long primaryKey) {
		return baseAllService.deleteByPK(tableName, primaryKey);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid) {
		return baseAllService.deleteByPKAndTenantid(tableName, primaryKey, tenantid);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid) {
		return baseAllService.deleteByPKAndTenantid(tableName, primaryKey, tenantid);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys) {
		return baseAllService.deleteByPKs(vo, primaryKeys);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys) {
		return baseAllService.deleteByPKsOfLong(vo, primaryKeys);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid) {
		return baseAllService.deleteByPKsAndTenantid(vo, primaryKeys, tenantid);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid) {
		return baseAllService.deleteByPKsOfLongAndTenantid(vo, primaryKeys, tenantid);
	}

	@Override
	public int deleteByPKs(String tableName, Collection<String> primaryKeys) {
		return baseAllService.deleteByPKs(tableName, primaryKeys);
	}

	@Override
	public int deleteByPKsOfLong(String tableName, Collection<Long> primaryKeys) {
		return baseAllService.deleteByPKsOfLong(tableName, primaryKeys);
	}

	@Override
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid) {
		return baseAllService.deleteByPKsAndTenantid(tableName, primaryKeys, tenantid);
	}

	@Override
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid) {
		return baseAllService.deleteByPKsOfLongAndTenantid(tableName, primaryKeys, tenantid);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey) {
		return baseAllService.queryByPK(vo, primaryKey);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, Long primaryKey) {
		return baseAllService.queryByPK(vo, primaryKey);
	}

	@Override
	public <T extends AbstractVO> T queryByPK_ReadSlave(T vo, Long primaryKey) {
		return baseAllService.queryByPK_ReadSlave(vo, primaryKey);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid) {
		return baseAllService.queryByPKAndTenantid(vo, primaryKey, tenantid);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo, Long primaryKey,
			Long tenantid) {
		return baseAllService.queryByPKAndTenantid(vo, primaryKey, tenantid);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid_ReadSlave(T vo,
			Long primaryKey, Long tenantid) {
		return baseAllService.queryByPKAndTenantid_ReadSlave(vo, primaryKey, tenantid);
	}

	@Override
	public AbstractVO queryByPK(String tableName, String primaryKey) {
		return baseAllService.queryByPK(tableName, primaryKey);
	}

	@Override
	public AbstractVO queryByPK(String tableName, Long primaryKey) {
		return baseAllService.queryByPK(tableName, primaryKey);
	}

	@Override
	public AbstractVO queryByPK_ReadSlave(String tableName, Long primaryKey) {
		return baseAllService.queryByPK_ReadSlave(tableName, primaryKey);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid) {
		return baseAllService.queryByPKAndTenantid(tableName, primaryKey, tenantid);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid) {
		return baseAllService.queryByPKAndTenantid(tableName, primaryKey, tenantid);
	}

	@Override
	public AbstractVO queryByPKAndTenantid_ReadSlave(String tableName,
			Long primaryKey, Long tenantid) {
		return baseAllService.queryByPKAndTenantid_ReadSlave(tableName, primaryKey, tenantid);
	}

	@Override
	public AbstractVO queryByPKAndTenantid4update(String tableName,
			Long primaryKey, Long tenantid) {
		return baseAllService.queryByPKAndTenantid4update(tableName, primaryKey, tenantid);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo,
			Collection<String> primaryKeys) {
		return baseAllService.queryByPKs(vo, primaryKeys);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys) {
		return baseAllService.queryByPKsOfLong(vo, primaryKeys);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong_ReadSlave(T vo,
			Collection<Long> primaryKeys) {
		return baseAllService.queryByPKsOfLong_ReadSlave(vo, primaryKeys);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid) {
		return baseAllService.queryByPKsAndTenantid(vo, primaryKeys, tenantid);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid) {
		return baseAllService.queryByPKsOfLongAndTenantid(vo, primaryKeys, tenantid);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid_ReadSlave(
			T vo, Collection<Long> primaryKeys, Long tenantid) {
		return baseAllService.queryByPKsOfLongAndTenantid_ReadSlave(vo, primaryKeys, tenantid);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.query(vo, dynaSqlVO);
	}

	@Override
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.query(tableName, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.query(voClass, dynaSqlVO);
	}

	@Override
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.executeQuery(sql, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.executeQuery(voClass, sql, dynaSqlVO);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam,
			int[] preStatementParamType, String tableName) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.executeQuery(voClass, sql, dynaSqlVO, preStatementParam, 
				preStatementParamType, tableName);
	}

	@Override
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			Object[] preStatementParam,
			ResultSetExtractor<T> resultSetExtractor, String tableName) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.executeQuery(sql, dynaSqlVO, preStatementParam, 
				resultSetExtractor, tableName);
	}

	@Override
	public int executeUpate(String sql, String tableName) {
		return baseAllService.executeUpate(sql, tableName);
	}

	@Override
	public int executeUpate(String sql, String tableName, boolean isAddTS) {
		return baseAllService.executeUpate(sql, tableName, isAddTS);
	}

	@Override
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName) {
		return baseAllService.executeUpate(sql, preStatementParam, preStatementParamType, tableName);
	}

	@Override
	public Object getVOPkColValue(AbstractVO vo) {
		return baseAllService.getVOPkColValue(vo);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO) {
		dealIsNullCondition(dynaSqlVO);
		return baseAllService.getCount(tableName, dynaSqlVO);
	}

	@Override
	public Map<String, List<String>> getCreateTableSql(String dbType,
			List<TableDefVO> tableDefVOList) throws HDException {
		return baseAllService.getCreateTableSql(dbType, tableDefVOList);
	}

	@Override
	public boolean validateConsistency(AbstractVO vo) {
		return baseAllService.validateConsistency(vo);
	}

	@Override
	public <T extends AbstractVO> boolean validateConsistency(List<T> list) {
		return baseAllService.validateConsistency(list);
	}

	private static void dealIsNullCondition(DynaSqlVO dynaSqlVO) {
		if (null != dynaSqlVO &&
				StringUtils.isNotBlank(dynaSqlVO.getWhereClause()) &&
				dynaSqlVO.getWhereClause().contains("= 'null'")) {
			dynaSqlVO.setWhereClause(dynaSqlVO.getWhereClause().replace("= 'null'", " is null"));
		}
	}
	
}
