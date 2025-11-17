package com.hayden.hap.dbop.dao;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.exception.DaoRuntimeException;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.db.keyGen.KeyGeneratorManager;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.dbop.db.orm.sql.SqlBuilderManager;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVoConstants;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.util.DBConstants;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.common.utils.SyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BaseDaoImpl
 * @Description:
 * @author LUYANYING
 * @date 2015年3月24日 上午11:53:10
 * @version V1.0
 * 
 */
@Repository("baseDao")
public class BaseDaoImpl implements BaseDao {
	private static final Logger logger = LoggerFactory
			.getLogger(BaseDaoImpl.class);

	@Autowired
	private JdbcTemplateSupportDao jdbcTemplateSupportDao;

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
		T result = null;
		try {
			TableDefVO tableDefVO = jdbcTemplateSupportDao.getTableDefVO(vo
					.getTableName());
			String gencode = this.getPkColGencode(tableDefVO);
			Long primaryKey = Long.parseLong(KeyGeneratorManager
					.generate(gencode));
			jdbcTemplateSupportDao.setVOPkColValue(vo, primaryKey, null);
			jdbcTemplateSupportDao.setDefaultValue(vo);
			result = jdbcTemplateSupportDao.insert(vo, dynaSqlVO, dataSourceId,
					ruleName, dataSourcePoolName);
		}catch(DuplicateKeyException e){
			throw new DaoRuntimeException("值重复:"+e.getCause().getMessage(), e);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList) {
		return this.insertBatch(voList, null, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			String dataSourceId) {
		return this.insertBatch(voList, null, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			String ruleName, String dataSourcePoolName) {
		return this.insertBatch(voList, null, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return this.insertBatch(voList, null, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO) {
		return this.insertBatch(voList, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.insertBatch(voList, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName) {
		return this.insertBatch(voList, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return this.insertBatch(voList, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName, DBConstants.PER_BATCH_SIZE);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			int perBatchSize) {
		return this.insertBatch(voList, null, null, null, null, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize) {
		return this.insertBatch(voList, dynaSqlVO, null, null, null,
				perBatchSize);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> insertBatchHavePks(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize){
		VOSet<T> result = new VOSet<T>();
		if (voList == null || voList.isEmpty())
			return result;
		try {
/*			TableDefVO tableDefVO = jdbcTemplateSupportDao.getTableDefVO(voList
					.get(0).getTableName());
			String gencode = this.getPkColGencode(tableDefVO);
			String[] keys = KeyGeneratorManager
					.generate(gencode, voList.size());
			for (int i = 0; i < voList.size(); i++) {
				T t = voList.get(i);
				Long primaryKey = Long.parseLong(keys[i]);
				jdbcTemplateSupportDao.setVOPkColValue(t, primaryKey,
						tableDefVO);
			}*/
			result = jdbcTemplateSupportDao.insertBatch(voList, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName, perBatchSize);
			result.setVoList(voList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
	
	}
			
	@Override
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize) {
		VOSet<T> result = new VOSet<T>();
		if (voList == null || voList.isEmpty())
			return result;
		try {			
			TableDefVO tableDefVO = jdbcTemplateSupportDao.getTableDefVO(voList
					.get(0).getTableName());
			String gencode = this.getPkColGencode(tableDefVO);
			String[] keys = KeyGeneratorManager
					.generate(gencode, voList.size());
			for (int i = 0; i < voList.size(); i++) {
				T t = voList.get(i);
				Long primaryKey = Long.parseLong(keys[i]);
				jdbcTemplateSupportDao.setVOPkColValue(t, primaryKey,
						tableDefVO);
			}
			result = jdbcTemplateSupportDao.insertBatch(voList, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName, perBatchSize);
			result.setVoList(voList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.delete(null, vo, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.delete(tableName, null, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public <T extends AbstractVO> int deleteBatch(List<T> voList) {
		return this.deleteBatch(voList, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String dataSourceId) {
		return this.deleteBatch(voList, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String ruleName, String dataSourcePoolName) {
		return this.deleteBatch(voList, null, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.deleteBatch(voList, dataSourceId,
					ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotNull(primaryKey))
			return 0;
		String tableName = vo.getTableName();
		return this.deleteByPK(tableName, primaryKey, dataSourceId, ruleName,
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
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		return this.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
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
	public <T extends AbstractVO> int deleteByPKs(T vo,
			Collection<String> primaryKeys, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return 0;
		String tableName = vo.getTableName();
		return this.deleteByPKs(tableName, primaryKeys, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int deleteByPKs(String tableName, Collection<String> primaryKeys) {
		return this.deleteByPKs(tableName, primaryKeys, null, null, null);
	}

	@Override
	public int deleteByPKs(String tableName, Collection<String> primaryKeys,
			String dataSourceId) {
		return this.deleteByPKs(tableName, primaryKeys, dataSourceId, null,
				null);
	}

	@Override
	public int deleteByPKs(String tableName, Collection<String> primaryKeys,
			String ruleName, String dataSourcePoolName) {
		return this.deleteByPKs(tableName, primaryKeys, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int deleteByPKs(String tableName, Collection<String> primaryKeys,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		return this.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
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
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.update(vo, dynaSqlVO, dataSourceId,
					ruleName, dataSourcePoolName);
		}catch(DuplicateKeyException e){
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException("值重复:"+e.getCause().getMessage(), e);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList) {
		return this.updateBatch(voList, null, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			String dataSourceId) {
		return this.updateBatch(voList, null, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			String ruleName, String dataSourcePoolName) {
		return this.updateBatch(voList, null, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return this.updateBatch(voList, null, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO) {
		return this.updateBatch(voList, dynaSqlVO, null, null, null);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId) {
		return this.updateBatch(voList, dynaSqlVO, dataSourceId, null, null);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String ruleName, String dataSourcePoolName) {
		return this.updateBatch(voList, dynaSqlVO, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return this.updateBatch(voList, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName, DBConstants.PER_BATCH_SIZE);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			int perBatchSize) {
		return this.updateBatch(voList, null, null, null, null, perBatchSize);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, int perBatchSize) {
		return this.updateBatch(voList, dynaSqlVO, null, null, null,
				perBatchSize);
	}

	@Override
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize) {
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.updateBatch(voList, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName, perBatchSize);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		VOSet<AbstractVO> result = null;
		try {
			result = jdbcTemplateSupportDao.query(tableName, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		return this.queryByPK(vo, primaryKey, false, dataSourceId, ruleName,
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
	 * @param isReadSlave
	 *            是否从库进行读取，默认为false           
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
	public <T extends AbstractVO> T queryByPK(T vo, String primaryKey, Boolean isReadSlave,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		T result = null;
		VOSet<T> resultVOSet = null;
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotNull(primaryKey)) {
			logger.warn("vo or primaryKey is null, return null");
			return null;
		}
		String tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName can't be given from vo .");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		if(isReadSlave!=null&&isReadSlave)
			dynaSqlVO.setReadSlave(isReadSlave);
		resultVOSet = this.query(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
		if (resultVOSet != null && !resultVOSet.isEmpty())
			result = resultVOSet.getVoList().get(0);
		return result;
	}
	
	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid) {
		return queryByPKAndTenantid(vo, primaryKey, tenantid, null, null, null);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, String dataSourceId) {
		return queryByPKAndTenantid(vo, primaryKey, tenantid, dataSourceId,
				null, null);
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
		return queryByPKAndTenantid(vo, primaryKey, tenantid, false, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> T queryByPKAndTenantid(T vo,
			String primaryKey, Long tenantid, Boolean isReadSlave, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		if (!ObjectUtil.isNotNull(tenantid)) {
			logger.warn("tenantid is null, return null");
			return null;
		}
		T result = null;
		VOSet<T> resultVOSet = null;
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotNull(primaryKey))
			return null;
		String tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName can't be given from vo .");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		if(isReadSlave!=null&&isReadSlave)
			dynaSqlVO.setReadSlave(isReadSlave);
		resultVOSet = this.query(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
		if (resultVOSet != null && !resultVOSet.isEmpty())
			result = resultVOSet.getVoList().get(0);
		return result;
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
		VOSet<T> resultVOSet = new VOSet<T>();
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return resultVOSet;
		String tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName can't be given from vo .");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		return this.query(vo, dynaSqlVO, dataSourceId, ruleName,
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
		return this.queryByPK(tableName, primaryKey, false, dataSourceId, ruleName,
				dataSourcePoolName);
	}
	
	@Override
	public AbstractVO queryByPK(String tableName, String primaryKey, Boolean isReadSlave,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		AbstractVO result = null;
		VOSet<AbstractVO> resultVOSet = null;
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		if(isReadSlave!=null&&isReadSlave)
			dynaSqlVO.setReadSlave(isReadSlave);
		resultVOSet = this.query(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
		if (resultVOSet != null && !resultVOSet.isEmpty())
			result = resultVOSet.getVoList().get(0);
		return result;
	}
	/**
	 * 
	 * @param tableName 表名
	 * @param primaryKey 主键值
	 * @param dataSourceId 执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源, 如果没有路由到数据源则返回默认数据源
	 * @param ruleName 规则名  对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName 数据源池名 对应路由规则配置文件 <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return AbstractVO 查询结果
	 * @author zhangfeng
	 * @date 2016年8月2日
	 */
	@Override
	public AbstractVO queryByPK4update(String tableName, String primaryKey, 
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		AbstractVO result = null;
		VOSet<AbstractVO> resultVOSet = null;
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		dynaSqlVO.setSelectForUpdate(true);
		resultVOSet = this.query(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
		if (resultVOSet != null && !resultVOSet.isEmpty())
			result = resultVOSet.getVoList().get(0);
		return result;
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
		VOSet<T> result = null;
		try {
			result = jdbcTemplateSupportDao.query(vo, dynaSqlVO, dataSourceId,
					ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		VOSet<T> result = null;
		try {
			result = jdbcTemplateSupportDao.query(voClass, dynaSqlVO,
					dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		List<T> result = null;
		try {
			result = jdbcTemplateSupportDao.executeQuery(sql, dynaSqlVO,
					preStatementParam, resultSetExtractor, tableName,
					dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		VOSet<T> result = null;
		try {
			result = jdbcTemplateSupportDao.executeQuery(voClass, sql,
					dynaSqlVO, preStatementParam, preStatementParamType,
					tableName, dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) { 
			logger.error(e.getMessage());
			throw new DaoRuntimeException("数据库查询异常", e);
		}
		return result;
	}

	@Override
	public int executeUpate(String sql, String tableName) {
		return this.executeUpate(sql, tableName, null, null, null);
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
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.executeUpate(sql, null, null,
					tableName, dataSourceId, ruleName, dataSourcePoolName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
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
		//默认isAddTS传值为true
		return this.executeUpate(sql, preStatementParam, preStatementParamType,
				tableName, dataSourceId, ruleName, dataSourcePoolName, true);
	}
	
	@Override
	public int executeUpate(String sql, Object[] preStatementParam,
			int[] preStatementParamType, String tableName, String dataSourceId,
			String ruleName, String dataSourcePoolName, boolean isAddTS) {
		int result = 0;
		try {
			result = jdbcTemplateSupportDao.executeUpate(sql,
					preStatementParam, preStatementParamType, tableName,
					dataSourceId, ruleName, dataSourcePoolName, isAddTS);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DaoRuntimeException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 
	 * @Title: getPkColGencode
	 * @Description: 获取主键字段生成器编码,用以生成主键
	 * @param tableDefVO
	 * @return
	 * @return String
	 * @throws
	 */
	private String getPkColGencode(TableDefVO tableDefVO) {
		String gencode = tableDefVO.getPkColumnVO() != null
				&& tableDefVO.getPkColumnVO().getIspk()==SyConstant.SY_TRUE? tableDefVO
				.getPkColumnVO().getGencode() : null;
		return gencode;
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
		return jdbcTemplateSupportDao.getVOPkColValue(null, vo);
	}

	public JdbcTemplateSupportDao getJdbcTemplateSupportDao() {
		return jdbcTemplateSupportDao;
	}

	public void setJdbcTemplateSupportDao(
			JdbcTemplateSupportDao jdbcTemplateSupportDao) {
		this.jdbcTemplateSupportDao = jdbcTemplateSupportDao;
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
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotNull(primaryKey))
			return 0;
		String tableName = vo.getTableName();
		return this.deleteByPKAndTenantid(tableName, primaryKey, tenantid, dataSourceId, ruleName, dataSourcePoolName);
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
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		return this.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid) {
		return deleteByPKsAndTenantid(vo, primaryKeys, tenantid, null, null,
				null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId) {
		return deleteByPKsAndTenantid(vo, primaryKeys, tenantid, dataSourceId,
				null, null);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return deleteByPKsAndTenantid(vo, primaryKeys, tenantid, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return 0;
		String tableName = vo.getTableName();
		return this.deleteByPKsAndTenantid(tableName, primaryKeys, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid) {
		return deleteByPKsAndTenantid(tableName, primaryKeys, tenantid, null,
				null, null);
	}

	@Override
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId) {
		return deleteByPKsAndTenantid(tableName, primaryKeys, tenantid,
				dataSourceId, null, null);
	}

	@Override
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return deleteByPKsAndTenantid(tableName, primaryKeys, tenantid, null,
				ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKsAndTenantid(String tableName,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		return this.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
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
		return queryByPKAndTenantid(tableName, primaryKey, tenantid, false, dataSourceId,
				ruleName, dataSourcePoolName);
	}
	
	@Override
	public AbstractVO queryByPKAndTenantid(String tableName, String primaryKey,
			Long tenantid, Boolean isReadSlave, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		AbstractVO result = null;
		VOSet<AbstractVO> resultVOSet = null;
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		if(isReadSlave!=null&&isReadSlave)
			dynaSqlVO.setReadSlave(isReadSlave);
		resultVOSet = this.query(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
		if (resultVOSet != null && !resultVOSet.isEmpty())
			result = resultVOSet.getVoList().get(0);
		return result;
	}
	
	/**
	 * 
	 *
	 * @see BaseDao#queryByPKAndTenantid4update(java.lang.String, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 * @author zhangfeng
	 * @date 2016年8月2日
	 */
	@Override
	public AbstractVO queryByPKAndTenantid4update(String tableName,
			Long primaryKey, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		AbstractVO result = null;
		VOSet<AbstractVO> resultVOSet = null;
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKey);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		dynaSqlVO.setSelectForUpdate(true);
		resultVOSet = this.query(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
		if (resultVOSet != null && !resultVOSet.isEmpty())
			result = resultVOSet.getVoList().get(0);
		return result;
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid) {
		return queryByPKsAndTenantid(vo, primaryKeys, tenantid, null, null,
				null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId) {
		return queryByPKsAndTenantid(vo, primaryKeys, tenantid, dataSourceId,
				null, null);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String ruleName,
			String dataSourcePoolName) {
		return queryByPKsAndTenantid(vo, primaryKeys, tenantid, null, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsAndTenantid(T vo,
			Collection<String> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		VOSet<T> resultVOSet = new VOSet<T>();
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return resultVOSet;
		String tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName can't be given from vo .");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		return this.query(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public  List<TableColumnVO> getTableColVoList(String catalog,String schema,String tableName,Long parentid) {
		
		return jdbcTemplateSupportDao.getTableColVoList(catalog,schema,tableName,parentid);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO) {
		return this.getCount(tableName, dynaSqlVO, null, null, null);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO, String ruleName,
			String dataSourcePoolName) {
		return this.getCount(tableName, dynaSqlVO, ruleName, dataSourcePoolName, null);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId) {
		return this.getCount(tableName, dynaSqlVO, null, null, dataSourceId);
	}

	@Override
	public int getCount(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return jdbcTemplateSupportDao.getCount(tableName, dynaSqlVO, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> int deleteByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return 0;
		String tableName = vo.getTableName();
		return this.deleteByPKsOfLong(tableName, primaryKeys, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public int deleteByPKsOfLong(String tableName,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		return this.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return this.queryByPKsOfLong(vo,primaryKeys, false, dataSourceId, ruleName,
				dataSourcePoolName);
	}
	
	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLong(T vo,
			Collection<Long> primaryKeys, Boolean isReadSlave, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		VOSet<T> resultVOSet = new VOSet<T>();
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return resultVOSet;
		String tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName can't be given from vo .");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setWhereByKey(true);
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		if(isReadSlave!=null&&isReadSlave)
			dynaSqlVO.setReadSlave(isReadSlave);
		return this.query(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}
	
	@Override
	public <T extends AbstractVO> int deleteByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return 0;
		String tableName = vo.getTableName();
		return this.deleteByPKsOfLongAndTenantid(tableName, primaryKeys, tenantid, dataSourceId, ruleName, dataSourcePoolName);
	}

	@Override
	public int deleteByPKsOfLongAndTenantid(String tableName,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is required.");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		return this.delete(tableName, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		return this.queryByPKsOfLongAndTenantid(vo, primaryKeys, tenantid, false, dataSourceId,
				ruleName, dataSourcePoolName);
	}

	@Override
	public <T extends AbstractVO> VOSet<T> queryByPKsOfLongAndTenantid(T vo,
			Collection<Long> primaryKeys, Long tenantid, Boolean isReadSlave, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		VOSet<T> resultVOSet = new VOSet<T>();
		if (!ObjectUtil.isNotNull(vo) || !ObjectUtil.isNotEmpty(primaryKeys))
			return resultVOSet;
		String tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName can't be given from vo .");
		String pkColName = jdbcTemplateSupportDao.getVOPkColName(tableName);
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(pkColName, primaryKeys);
		dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, tenantid);
		if(isReadSlave!=null&&isReadSlave)
			dynaSqlVO.setReadSlave(isReadSlave);
		return this.query(vo, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}
	
	/** 
	 * 针对mysql、oracle数据库，返回表定义值相关的建表语句。
	 * @see BaseDao#getCreateTableSql(java.lang.String, java.util.List)
	 * @author wangyi
	 * @date 2017年11月9日
	 */
	@Override
	public Map<String,List<String>> getCreateTableSql(String dbType, List<TableDefVO> tableDefVOList) throws HDException {
		Map<String,List<String>> dllSql = new HashMap<String,List<String>>();
		SqlBuilderManager sbManager = new SqlBuilderManager();
		for (TableDefVO tableDefVO : tableDefVOList) {
			String tableName = tableDefVO.getTable_code();
			dllSql.put(tableDefVO.getTable_code(),sbManager.getCreateTableSql(dbType, tableDefVO));
		}
		return dllSql;
	}

}
