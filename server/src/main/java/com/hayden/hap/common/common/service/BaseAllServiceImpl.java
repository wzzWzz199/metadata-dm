package com.hayden.hap.common.common.service;

import com.hayden.hap.common.common.dao.BaseDao;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.common.itf.IBaseAllService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBConstants;
import com.hayden.hap.common.db.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BaseAllServiceImpl
 * @Description:
 * @author LUYANYING
 * @date 2015年4月15日 上午9:59:26
 * @version V1.0
 * 
 */
@Service("baseAllService")
public class BaseAllServiceImpl implements IBaseAllService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseAllServiceImpl.class);

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private ITableDefService tableDefService;
	
	private static final String TS_COLUMN = "ts";

	@Override
	public <T extends AbstractVO> T insert(T vo) {
		return this.insert(vo, null, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, String dataSourceId) {
		return this.insert(vo, null, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, String ruleName,
			String dataSourcePoolName) {
		return this.insert(vo, null, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return this
				.insert(vo, null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO) {
		return this.insert(vo, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.insert(vo, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO,
			String ruleName, String dataSourcePoolName) {
		return this.insert(vo, dynaSqlVO, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.insert(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatchHavePks(List<T> voList){
		return baseDao.insertBatchHavePks(voList, null, null, null, null,DBConstants.PER_BATCH_SIZE);
	}
	
	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList) {
		return this.insertBatch(voList, null, null, null, null);
	}


	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			String dataSourceId) {
		return this.insertBatch(voList, null, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			String ruleName, String dataSourcePoolName) {
		return this.insertBatch(voList, null, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return this.insertBatch(voList, null, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO) {
		return this.insertBatch(voList, dynaSqlVO, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.insertBatch(voList, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName) {
		return this.insertBatch(voList, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.insertBatch(voList, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			int perBatchSize) {
		return baseDao.insertBatch(voList, perBatchSize);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize) {
		return baseDao.insertBatch(voList, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName, perBatchSize);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize) {
		return baseDao.insertBatch(voList, dynaSqlVO, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo) {
		return this.delete(vo, null, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, String dataSourceId) {
		return this.delete(vo, null, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, String ruleName,
			String dataSourcePoolName) {
		return this.delete(vo, null, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return this
				.delete(vo, null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO) {
		return this.delete(vo, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.delete(vo, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO,
			String ruleName, String dataSourcePoolName) {
		return this.delete(vo, dynaSqlVO, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int delete(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.delete(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO) {
		return this.delete(tableName, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.delete(tableName, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName) {
		return this.delete(tableName, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteBatch(List<T> voList) {
		return this.deleteBatch(voList, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String dataSourceId) {
		return this.deleteBatch(voList, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String ruleName, String dataSourcePoolName) {
		return this.deleteBatch(voList, null, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.deleteBatch(voList, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey) {
		return this.deleteByPK(vo, primaryKey, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey,
			String dataSourceId) {
		return this.deleteByPK(vo, primaryKey, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey,
			String ruleName, String dataSourcePoolName) {
		return this.deleteByPK(vo, primaryKey, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys) {
		return this.deleteByPKs(vo, primaryKeys, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys, String dataSourceId) {
		return this.deleteByPKs(vo, primaryKeys, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPKs(vo, primaryKeys, null, ruleName,
				dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: deleteByPK
	 * @Description: 按主键删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            待删除的主键值
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	public <T extends AbstractVO> int deleteByPK(T vo, String primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPK(vo, primaryKey, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int deleteByPK(String tableName, String primaryKey) {
		return this.deleteByPK(tableName, primaryKey, null, null, null);
	}

	@Override
	public int deleteByPK(String tableName, String primaryKey,
			String dataSourceId) {
		return this.deleteByPK(tableName, primaryKey, dataSourceId, null, null);
	}

	@Override
	public int deleteByPK(String tableName, String primaryKey, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPK(tableName, primaryKey, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int deleteByPK(String tableName, String primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPK(tableName, primaryKey, dataSourceId,
				ruleName, dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: deleteByPKs
	 * @Description: 按主键批量删除
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            待删除的主键值
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除记录数
	 * @throws
	 */
	@Transactional()
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKs(vo, primaryKeys, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKs(String tableName, Collection<String> primaryKeys) {
		return this.deleteByPKs(tableName, primaryKeys, null, null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKs(String tableName, Collection<String> primaryKeys,
			String dataSourceId) {
		return this.deleteByPKs(tableName, primaryKeys, dataSourceId, null,
				null);
	}

	@Override
	@Transactional()
	public int deleteByPKs(String tableName, Collection<String> primaryKeys,
			String ruleName, String dataSourcePoolName) {
		return this.deleteByPKs(tableName, primaryKeys, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKs(String tableName, Collection<String> primaryKeys,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKs(tableName, primaryKeys, dataSourceId,
				ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int update(T vo) {
		return this.update(vo, null, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, String dataSourceId) {
		return this.update(vo, null, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, String ruleName,
			String dataSourcePoolName) {
		return this.update(vo, null, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return this
				.update(vo, null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO) {
		return this.update(vo, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.update(vo, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO,
			String ruleName, String dataSourcePoolName) {
		return this.update(vo, dynaSqlVO, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.update(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList) {
		return this.updateBatch(voList, null, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			String dataSourceId) {
		return this.updateBatch(voList, null, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			String ruleName, String dataSourcePoolName) {
		return this.updateBatch(voList, null, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return this.updateBatch(voList, null, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO) {
		return this.updateBatch(voList, dynaSqlVO, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatchForList(List<T> voList,
			List<DynaSqlVO> dynaSqlVOList) {
		return updateBatchForList(voList, dynaSqlVOList, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatchForList(List<T> voList,
			List<DynaSqlVO> dynaSqlVOList, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		int i = 0;
		int size = voList.size();
		for (int p = 0; p < size; p++) {
			i += update(voList.get(p), dynaSqlVOList.get(p), dataSourceId,
					ruleName, dataSourcePoolName);
		}
		return i;
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.updateBatch(voList, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName) {
		return this.updateBatch(voList, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.updateBatch(voList, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			int perBatchSize) {
		return baseDao.updateBatch(voList, perBatchSize);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize) {
		return baseDao.updateBatch(voList, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName, perBatchSize);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize) {
		return baseDao.updateBatch(voList, dynaSqlVO, perBatchSize);
	}

	@Override
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO) {
		return this.query(tableName, dynaSqlVO, null, null, null);
	}

	@Override
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.query(tableName, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO,
			String ruleName, String dataSourcePoolName) {
		return this.query(tableName, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.query(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey) {
		return this.queryByPK(vo, primaryKey, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey,
			String dataSourceId) {
		return this.queryByPK(vo, primaryKey, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey,
			String ruleName, String dataSourcePoolName) {
		return this.queryByPK(vo, primaryKey, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo,
			Collection<String> primaryKeys) {
		return this.queryByPKs(vo, primaryKeys, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo,
			Collection<String> primaryKeys, String dataSourceId) {
		return this.queryByPKs(vo, primaryKeys, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo,
			Collection<String> primaryKeys, String ruleName,
			String dataSourcePoolName) {
		return this.queryByPKs(vo, primaryKeys, null, ruleName,
				dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: queryByPK
	 * @Description: 按主键查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKey
	 *            主键值
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T 查询结果
	 * @throws
	 */
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPK(vo, primaryKey, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: queryByPKs
	 * @Description: 按主键批量查询
	 * @param vo
	 *            实体对象 这里用于获取表名而已
	 * @param primaryKeys
	 *            主键值集合
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<AbstractVO> 查询结果封装对象
	 * @throws
	 */
	public <T extends AbstractVO> VOSet<T> queryByPKs(T vo,
			Collection<String> primaryKeys, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPKs(vo, primaryKeys, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPK(String tableName, String primaryKey) {
		return this.queryByPK(tableName, primaryKey, null, null, null);
	}

	@Override
	public AbstractVO queryByPK(String tableName, String primaryKey,
			String dataSourceId) {
		return this.queryByPK(tableName, primaryKey, dataSourceId, null, null);
	}

	@Override
	public AbstractVO queryByPK(String tableName, String primaryKey,
			String ruleName, String dataSourcePoolName) {
		return this.queryByPK(tableName, primaryKey, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPK(String tableName, String primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPK(tableName, primaryKey, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO) {
		return this.query(vo, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.query(vo, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO,
			String ruleName, String dataSourcePoolName) {
		return this.query(vo, dynaSqlVO, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.query(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO) {
		return this.query(voClass, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.query(voClass, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName) {
		return this.query(voClass, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.query(voClass, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			Object[] preStatementParam,
			ResultSetExtractor<T> resultSetExtractor, String tableName) {
		return executeQuery(sql, dynaSqlVO, preStatementParam,
				resultSetExtractor, tableName, null, null, null);
	}

	@Override
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			Object[] preStatementParam,
			ResultSetExtractor<T> resultSetExtractor, String tableName,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.executeQuery(sql, dynaSqlVO, preStatementParam,
				resultSetExtractor, tableName, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			Object[] preStatementParam,
			ResultSetExtractor<T> resultSetExtractor, String tableName,
			String dataSourceId) {
		return this.executeQuery(sql, dynaSqlVO, preStatementParam,
				resultSetExtractor, tableName, dataSourceId, null, null);
	}

	@Override
	public <T> List<T> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			Object[] preStatementParam,
			ResultSetExtractor<T> resultSetExtractor, String tableName,
			String ruleName, String dataSourcePoolName) {
		return this.executeQuery(sql, dynaSqlVO, preStatementParam,
				resultSetExtractor, tableName, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO) {
		return this.executeQuery(null, sql, dynaSqlVO, null, null, null, null,
				null, null);
	}

	@Override
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.executeQuery(null, sql, dynaSqlVO, null, null, null,
				dataSourceId, null, null);
	}

	@Override
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			String ruleName, String dataSourcePoolName) {
		return this.executeQuery(null, sql, dynaSqlVO, null, null, null, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	public VOSet<AbstractVO> executeQuery(String sql, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return this.executeQuery(null, sql, dynaSqlVO, null, null, null,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO) {
		return this.executeQuery(voClass, sql, dynaSqlVO, null, null, null,
				null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.executeQuery(voClass, sql, dynaSqlVO, null, null, null,
				dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, String ruleName,
			String dataSourcePoolName) {
		return this.executeQuery(voClass, sql, dynaSqlVO, null, null, null,
				null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return this.executeQuery(voClass, sql, dynaSqlVO, null, null, null,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam,
			int[] preStatementParamType, String tableName) {
		return this.executeQuery(voClass, sql, dynaSqlVO, preStatementParam,
				preStatementParamType, tableName, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String dataSourceId) {
		return this.executeQuery(voClass, sql, dynaSqlVO, preStatementParam,
				preStatementParamType, tableName, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String ruleName,
			String dataSourcePoolName) {
		return this.executeQuery(voClass, sql, dynaSqlVO, preStatementParam,
				preStatementParamType, tableName, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			String sql, DynaSqlVO dynaSqlVO, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.executeQuery(voClass, sql, dynaSqlVO, preStatementParam,
				preStatementParamType, tableName, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int executeUpate(String sql, String tableName) {
		return this.executeUpate(sql, tableName, null, null, null);
	}

	@Override
	public int executeUpate(String sql, String tableName, boolean isAddTS) {
		return baseDao.executeUpate(sql, null, null, tableName, null,
				null, null, isAddTS);
	}
	
	@Override
	public int executeUpate(String sql, String tableName, String dataSourceId) {
		return this.executeUpate(sql, tableName, dataSourceId, null, null);
	}

	@Override
	public int executeUpate(String sql, String tableName, String ruleName,
			String dataSourcePoolName) {
		return this.executeUpate(sql, tableName, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int executeUpate(String sql, String tableName, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.executeUpate(sql, null, null, tableName, dataSourceId,
				ruleName, dataSourcePoolName);
	}

	@Override
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName) {
		return this.executeUpate(sql, preStatementParam, preStatementParamType,
				tableName, null, null, null);
	}

	@Override
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String dataSourceId) {
		return this.executeUpate(sql, preStatementParam, preStatementParamType,
				tableName, dataSourceId, null, null);
	}

	@Override
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String ruleName,
			String dataSourcePoolName) {
		return this.executeUpate(sql, preStatementParam, preStatementParamType,
				tableName, null, ruleName, dataSourcePoolName);
	}

	@Override
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.executeUpate(sql, preStatementParam,
				preStatementParamType, tableName, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: getVOPkColValue
	 * @Description: 获取实体对象的主键值
	 * @param vo
	 * @return
	 * @return Object
	 * @throws
	 */
	public Object getVOPkColValue(AbstractVO vo) {
		return baseDao.getVOPkColValue(vo);
	}

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid) {
		return queryByPKAndTenantid(vo, primaryKey, tenantid, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return queryByPKAndTenantid(vo, primaryKey, tenantid, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPKAndTenantid(vo, primaryKey, tenantid,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid) {
		return queryByPKAndTenantid(tableName, primaryKey, tenantid, null,
				null, null);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId) {
		return queryByPKAndTenantid(tableName, primaryKey, tenantid,
				dataSourceId, null, null);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName) {
		return queryByPKAndTenantid(tableName, primaryKey, tenantid, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.queryByPKAndTenantid(tableName, primaryKey, tenantid,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid) {
		return baseDao.queryByPKsAndTenantid(vo, primaryKeys, tenantid, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid) {
		return deleteByPKAndTenantid(vo, primaryKey, tenantid, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId) {
		return deleteByPKAndTenantid(vo, primaryKey, tenantid, dataSourceId,
				null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return deleteByPKAndTenantid(vo, primaryKey, tenantid, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKAndTenantid(vo, primaryKey, tenantid,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid) {
		return deleteByPKAndTenantid(tableName, primaryKey, tenantid, null,
				null, null);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId) {
		return deleteByPKAndTenantid(tableName, primaryKey, tenantid,
				dataSourceId, null, null);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName) {
		return deleteByPKAndTenantid(tableName, primaryKey, tenantid, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.deleteByPKAndTenantid(tableName, primaryKey, tenantid,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid) {
		return deleteByPKsAndTenantid(vo, primaryKeys, tenantid, null, null,
				null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId) {
		return deleteByPKsAndTenantid(vo, primaryKeys, tenantid, dataSourceId,
				null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return deleteByPKsAndTenantid(vo, primaryKeys, tenantid, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKsAndTenantid(vo, primaryKeys, tenantid,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid) {
		return deleteByPKsAndTenantid(tableName, primaryKeys, tenantid, null,
				null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId) {
		return deleteByPKsAndTenantid(tableName, primaryKeys, tenantid,
				dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return deleteByPKsAndTenantid(tableName, primaryKeys, tenantid, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKsAndTenantid(tableName, primaryKeys, tenantid,
				dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO) {
		return getCount(tableName, dynaSqlVO, null, null, null);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return getCount(tableName, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO, String ruleName,
			String dataSourcePoolName) {
		return getCount(tableName, dynaSqlVO, null, ruleName, dataSourcePoolName);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.getCount(tableName, dynaSqlVO, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, Long primaryKey) {
		return this.deleteByPK(vo, primaryKey, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, Long primaryKey,
			String dataSourceId) {
		return this.deleteByPK(vo, primaryKey, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, Long primaryKey,
			String ruleName, String dataSourcePoolName) {
		return this.deleteByPK(vo, primaryKey, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPK(T vo, Long primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPK(vo, null!=primaryKey?String.valueOf(primaryKey):null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid) {
		return this.deleteByPKAndTenantid(vo, primaryKey, tenantid, null, null,null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid, String dataSourceId) {
		return this.deleteByPKAndTenantid(vo, primaryKey, tenantid, dataSourceId, null,null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPKAndTenantid(vo, primaryKey, tenantid, null, ruleName,dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKAndTenantid(T vo,
			Long primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKAndTenantid(vo, null!=primaryKey?String.valueOf(primaryKey):null, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPK(String tableName, Long primaryKey) {
		return this.deleteByPK(tableName, primaryKey, null, null, null);
	}

	@Override
	public int deleteByPK(String tableName, Long primaryKey, String dataSourceId) {
		return this.deleteByPK(tableName, primaryKey, dataSourceId, null, null);
	}

	@Override
	public int deleteByPK(String tableName, Long primaryKey, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPK(tableName, primaryKey, null, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPK(String tableName, Long primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPK(tableName, null!=primaryKey?String.valueOf(primaryKey):null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid) {
		return this.deleteByPKAndTenantid(tableName, primaryKey, tenantid, null, null, null);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid, String dataSourceId) {
		return this.deleteByPKAndTenantid(tableName, primaryKey, tenantid, dataSourceId, null, null);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName) {
		return this.deleteByPKAndTenantid(tableName, primaryKey, tenantid, null, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.deleteByPKAndTenantid(tableName, null!=primaryKey?String.valueOf(primaryKey):null, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys) {
		return this.deleteByPKsOfLong(vo, primaryKeys, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String dataSourceId) {
		return this.deleteByPKsOfLong(vo, primaryKeys, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPKsOfLong(vo, primaryKeys, null, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.deleteByPKsOfLong(vo, primaryKeys, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid) {
		return this.deleteByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, null, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId) {
		return this.deleteByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, null, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLong(String tableName, Collection<Long> primaryKeys) {
		return this.deleteByPKsOfLong(tableName, primaryKeys, null, null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLong(String tableName,
			Collection<Long> primaryKeys, String dataSourceId) {
		return this.deleteByPKsOfLong(tableName, primaryKeys, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLong(String tableName,
			Collection<Long> primaryKeys, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPKsOfLong(tableName, primaryKeys, null, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLong(String tableName,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.deleteByPKsOfLong(tableName, primaryKeys, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid) {
		return this.deleteByPKsOfLongAndTenantid(tableName, primaryKeys, tenantid, null, null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId) {
		return this.deleteByPKsOfLongAndTenantid(tableName, primaryKeys, tenantid, dataSourceId, null, null);
	}

	@Override
	@Transactional()
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return this.deleteByPKsOfLongAndTenantid(tableName, primaryKeys, tenantid, null, ruleName, dataSourcePoolName);

	}

	@Override
	@Transactional()
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return baseDao.deleteByPKsOfLongAndTenantid(tableName, primaryKeys, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, Long primaryKey) {
		return this.queryByPK(vo, primaryKey, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPK_ReadSlave(T vo, Long primaryKey) {
		return this.queryByPK_ReadSlave(vo, primaryKey, null, null, null);
	}
	
	@Override
	public <T extends AbstractVO> T queryByPK(T vo, Long primaryKey,
			String dataSourceId) {
		return this.queryByPK(vo, primaryKey, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, Long primaryKey,
			String ruleName, String dataSourcePoolName) {
		return this.queryByPK(vo, primaryKey, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPK(T vo, Long primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPK(vo, null!=primaryKey?String.valueOf(primaryKey):null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPK_ReadSlave(T vo, Long primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPK(vo, null!=primaryKey?String.valueOf(primaryKey):null, true, dataSourceId, ruleName, dataSourcePoolName);
	}
	
	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo, Long primaryKey,
			Long tenantid) {
		return this.queryByPKAndTenantid(vo, primaryKey, tenantid, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid_ReadSlave(T vo, Long primaryKey, Long tenantid) {
		return this.queryByPKAndTenantid_ReadSlave(vo, primaryKey, tenantid, null, null, null);
	}
	
	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo, Long primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName) {
		return this.queryByPKAndTenantid(vo, primaryKey, tenantid, dataSourcePoolName, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo, Long primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.queryByPKAndTenantid(vo, null!=primaryKey?String.valueOf(primaryKey):null, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid_ReadSlave(T vo, Long primaryKey,
			Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPKAndTenantid(vo, null!=primaryKey?String.valueOf(primaryKey):null, tenantid, true, dataSourceId, ruleName, dataSourcePoolName);
	}
	
	@Override
	public AbstractVO queryByPK(String tableName, Long primaryKey) {
		return this.queryByPK(tableName, primaryKey, null, null, null);
	}

	@Override
	public AbstractVO queryByPK_ReadSlave(String tableName, Long primaryKey) {
		return this.queryByPK_ReadSlave(tableName, primaryKey, null, null, null);
	}
	
//	/**
//	 * 
//	 *
//	 * @see com.hayden.hap.common.common.itf.IBaseService#queryByPK4update(java.lang.String, java.lang.Long)
//	 * @author zhangfeng
//	 * @date 2016年8月2日
//	 */
//	@Override
//	public AbstractVO queryByPK4update(String tableName, Long primaryKey) {
//		return baseDao.queryByPK(tableName, null!=primaryKey?String.valueOf(primaryKey):null, null, null, null);
//	}
	
	@Override
	public AbstractVO queryByPK(String tableName, Long primaryKey,
			String dataSourceId) {
		return this.queryByPK(tableName, primaryKey, dataSourceId, null, null);
	}

	@Override
	public AbstractVO queryByPK(String tableName, Long primaryKey,
			String ruleName, String dataSourcePoolName) {
		return this.queryByPK(tableName, primaryKey, null, ruleName, dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPK(String tableName, Long primaryKey,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPK(tableName, null!=primaryKey?String.valueOf(primaryKey):null, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPK_ReadSlave(String tableName, Long primaryKey,  
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPK(tableName, null!=primaryKey?String.valueOf(primaryKey):null, true, dataSourceId, ruleName, dataSourcePoolName);
	}
	
	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid) {
		return this.queryByPKAndTenantid(tableName, primaryKey, tenantid, null, null, null);
	}
	
	@Override
	public AbstractVO queryByPKAndTenantid_ReadSlave(String tableName, Long primaryKey,	Long tenantid) {
		return this.queryByPKAndTenantid_ReadSlave(tableName, primaryKey, tenantid, null, null, null);
	}
	
	/**
	 * 
	 *
	 * @see com.hayden.hap.common.common.itf.IBaseService#queryByPKAndTenantid4update(java.lang.String, java.lang.Long, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年8月2日
	 */
	@Override
	public AbstractVO queryByPKAndTenantid4update(String tableName, Long primaryKey,
			Long tenantid) {
		return baseDao.queryByPKAndTenantid4update(tableName, primaryKey, tenantid, null, null, null);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid, String dataSourceId) {
		return this.queryByPKAndTenantid(tableName, primaryKey, tenantid, dataSourceId, null, null);
	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid, String ruleName, String dataSourcePoolName) {
		return this.queryByPKAndTenantid(tableName, primaryKey, tenantid, null, ruleName, dataSourcePoolName);

	}

	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, Long primaryKey,
			Long tenantid, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.queryByPKAndTenantid(tableName, null!=primaryKey?String.valueOf(primaryKey):null, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public AbstractVO queryByPKAndTenantid_ReadSlave(String tableName, Long primaryKey,
			Long tenantid, String dataSourceId, String ruleName, String dataSourcePoolName) {
		return baseDao.queryByPKAndTenantid(tableName, null!=primaryKey?String.valueOf(primaryKey):null, tenantid, true, dataSourceId, ruleName, dataSourcePoolName);
	}
	
	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys) {
		return this.queryByPKsOfLong(vo, primaryKeys, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong_ReadSlave(T vo,
			Collection<Long> primaryKeys) {
		return this.queryByPKsOfLong_ReadSlave(vo, primaryKeys, null, null, null);
	}
	
	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String dataSourceId) {
		return this.queryByPKsOfLong(vo, primaryKeys, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String ruleName,
			String dataSourcePoolName) {
		return this.queryByPKsOfLong(vo, primaryKeys, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.queryByPKsOfLong(vo, primaryKeys, dataSourceId, ruleName, dataSourcePoolName);
	}
	
	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong_ReadSlave(T vo,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return baseDao.queryByPKsOfLong(vo, primaryKeys, true, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid) {
		return baseDao.queryByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid_ReadSlave(T vo,
			Collection<Long> primaryKeys, Long tenantid) {
		return baseDao.queryByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, true, null, null, null);
	}
	
	@Override
	public Map<String,List<String>> getCreateTableSql(String dbType, List<TableDefVO> tableDefVOList) throws HDException {
		return baseDao.getCreateTableSql(dbType, tableDefVOList);
	}
    
	/**
	 * 校验一致性
	 *
	 * @see com.hayden.hap.common.common.itf.IBaseService#validateConsistency(com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @date 2016年7月21日
	 */
	@Override
	public boolean validateConsistency(AbstractVO vo) {
		boolean needValidate = tableDefService.isSupportConsistencyValidate(vo.getTableName());
		if(needValidate) {
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.setSelectForUpdate(true);
			
			//只查ts字段
			List<String> tsCol = new ArrayList<>();
			tsCol.add(TS_COLUMN);
			dynaSqlVO.setSqlColumnList(tsCol);
			
			//根据主键查
			String pkCol = tableDefService.getPkColName(vo.getTableName());
			dynaSqlVO.addWhereParam(pkCol, vo.get(pkCol));
			
			//没有查到记录，则认为有并发操作，没验证通过
			VOSet<AbstractVO> voset = query(vo, dynaSqlVO);
			if(!ObjectUtil.isNotEmpty(voset.getVoList()))
				return false;
			
			AbstractVO oldVO = voset.getVoList().get(0);			
			Long oldTs = oldVO.getLong(TS_COLUMN);
			if(oldTs==null) {
				logger.error("ts值为空异常...");
				throw new HDRuntimeException("系统错误，请联系管理员");
			}				
			if(!oldTs.equals(vo.getLong(TS_COLUMN))) 
				return false;
		}
		return true;
	}

	/**
	 * 校验一致性
	 *
	 * @see com.hayden.hap.common.common.itf.IBaseService#validateConsistency(java.util.List)
	 * @author zhangfeng
	 * @date 2016年7月21日
	 */
	@Override
	public <T extends AbstractVO> boolean validateConsistency(List<T> list) {
		if(ObjectUtil.isNotEmpty(list)) {
			String tableName = list.get(0).getTableName();
			boolean needValidate = tableDefService.isSupportConsistencyValidate(tableName);
			if(needValidate) {				
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.setSelectForUpdate(true);
				
				//只查ts字段
				List<String> tsCol = new ArrayList<>();
				tsCol.add(TS_COLUMN);
				dynaSqlVO.setSqlColumnList(tsCol);
				
				//根据主键查
				String pkCol = tableDefService.getPkColName(list.get(0).getTableName());
				List<Long> pks = new ArrayList<>();
				for(AbstractVO vo : list) {
					pks.add(vo.getLong(pkCol));
				}
				dynaSqlVO.addWhereParam(pkCol, pks);
				
				VOSet<T> voSet = query(list.get(0), dynaSqlVO);				
				if(voSet.getVoList().size() != list.size()) 
					return false;
				
				String pkColumn = tableDefService.getPkColName(tableName);
				for(T oldVO : voSet.getVoList()) {
					for(T newVO : list) {
						if(oldVO.get(pkColumn).equals(newVO.get(pkColumn))) {
							Long oldTs = oldVO.getLong(TS_COLUMN);
							if(oldTs==null) {
								logger.error("ts值为空异常...");
								throw new HDRuntimeException("系统错误，请联系管理员");
							}				
							if(!oldTs.equals(newVO.getLong(TS_COLUMN))) 
								return false;
						}
					}
				}
			}
		}
		return true;
	}
}
