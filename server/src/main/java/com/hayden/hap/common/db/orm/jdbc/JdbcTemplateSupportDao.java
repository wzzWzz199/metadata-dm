package com.hayden.hap.common.db.orm.jdbc;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.db.orm.entity.ClobInfoVO;
import com.hayden.hap.common.db.orm.sql.*;
import com.hayden.hap.common.db.orm.typeHandler.TypeHandlerRegistry;
import com.hayden.hap.common.db.sharding.dataSource.DataSourceManager;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.temptable.dao.TempTableDaoImpl;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.db.util.DBType;
import com.hayden.hap.common.db.util.JdbcUtil;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.reflect.ClassInfo;
import com.hayden.hap.common.utils.ModuleDataSrcUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: JdbcTemplateSupportDao
 * @Description:
 * @author LUYANYING
 * @date 2015年4月20日 下午5:46:19
 * @version V1.0
 * 
 */
public class JdbcTemplateSupportDao implements DisposableBean, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateSupportDao.class);

	private String defaultDataSourceId = null;

	private DataSourceManager dataSourceManager;

	private JdbcTemplateManager jdbcTemplateManager;

	private ExecutorService executor;

	private long timeout = 100 * 1000;

	private SqlBuilderManager sqlBuilderManager = new SqlBuilderManager();
	private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
	@Autowired
	private ITableDefService tableDefService;


	@Override
	public void destroy() throws Exception {
		if (executor != null) {
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.MINUTES);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dataSourceManager == null)
			dataSourceManager = new DataSourceManager();
		ObjectUtil.validNotNull(defaultDataSourceId, this.getClass()
				.getSimpleName()
				+ " config error: defaultDataSourceId is required.");
		dataSourceManager.setDefaultDataSourceId(defaultDataSourceId);
		if (jdbcTemplateManager == null)
			jdbcTemplateManager = new JdbcTemplateManager(dataSourceManager);

	}

	public void init(Map<String, DataSource> dataSources) {
		jdbcTemplateManager.init(dataSources);
	}


	

	public <T extends AbstractVO> VOSet<T> query(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(vo), "vo is required.");
		return (VOSet<T>) this.query(vo.getTableName(), vo.getClass(),
				dynaSqlVO, dataSourceId, ruleName, dataSourcePoolName);
	}


	public <T extends AbstractVO> VOSet<T> query(Class<T> voClass,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		return this.query(null, voClass, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}


	public <T extends AbstractVO> VOSet<T> query(String tableName,
			Class<T> voClass, DynaSqlVO dynaSqlVO, String dataSourceId,
			String ruleName, String dataSourcePoolName) {
		ObjectUtil.validIsTrue(
				!ObjectUtil.isNotNull(tableName)
						&& !ObjectUtil.isNotNull(voClass),
				"at least one of 'tableName' or 'voClass' must be given.");
		if (!ObjectUtil.isNotNull(tableName)) {
			T vo = ClassInfo.newInstance(voClass);
			tableName = vo.getTableName();
		}
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is null and can't be given from vo class.");

		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);

		VOSet<T> resultVOSet = new VOSet<T>();
		List<T> voList = new ArrayList<T>();
		int totalRows = 0;


			final DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getSelectSql(dbType, tableDefVO, dynaSqlVO,
							tableShard.getTableName());
			resultVOSet.setSql(dynaSqlResultVO.getSql());
			this.createTempTableAndInsertData(dynaSqlResultVO, dataSourceIdTmp);
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					dynaSqlResultVO.getPreStatementParams(),
					dynaSqlResultVO.getPreStatementParamJdbcTypes(),
					typeHandlerRegistry);
			voList.addAll(jdbcTemplate.query(dynaSqlResultVO.getSql(),
					preparedStatementSetter,
					new VORowMapperResultSetExtractor<T>(voClass, tableDefVO,
							typeHandlerRegistry)));
			if (dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage()))
				totalRows += this.queryCount(
						dynaSqlResultVO.getSelectCountSql(), dataSourceIdTmp,
						preparedStatementSetter);
		}
		resultVOSet.setVoList(voList);
		if (dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage())) {
			dynaSqlVO.getPage().setTotalRows(totalRows);
			resultVOSet.setPage(dynaSqlVO.getPage());
		}	
		return resultVOSet;
	}

	/**
	 * 
	 * @Title: query
	 * @Description: 查询
	 * @param tableName
	 *            数据库表名, 必填, 用于拼接查询sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取查询条件、order by、group
	 *            by等语句、查询的字段列表、分页信息以及是否开启预编译
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
	public VOSet<AbstractVO> query(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return this.query(tableName, null, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: queryCount
	 * @Description: 执行count sql 统计查询行数
	 * @param countSql
	 *            count sql
	 * @param dataSourceId
	 *            数据源ID
	 * @param pss
	 * @return
	 * @return int 结果
	 * @throws
	 */
	public int queryCount(String countSql, String dataSourceId,
			PreparedStatementSetter pss) {
		if (!ObjectUtil.isNotNull(countSql))
			return 0;
		int count = 0;
		JdbcTemplate jdbcTemplate = getJdbcTemplateManager().getJdbcTemplate(
				dataSourceId);
		List<BaseVO> list = jdbcTemplate.query(countSql, pss,
				new VORowMapperResultSetExtractor<BaseVO>(null,
						typeHandlerRegistry) {
				});
		if (ObjectUtil.isNotEmpty(list))
			count = list.get(0).getInt(SqlBuilder.RS_COLUMN);
		return count;
	}

	/**
	 * 
	 * @Title: executeQuery
	 * @Description: 查询
	 * @param sql
	 *            执行查询的sql语句
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam
	 *            预编译参数
	 * @param resultSetExtractor
	 *            ResultSet处理对象
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return List<T> 查询结果集
	 * @throws
	 */
	public <T> List<T> executeQuery(final String sql, DynaSqlVO dynaSqlVO,
			final Object[] preStatementParam,
			final ResultSetExtractor<T> resultSetExtractor, String tableName,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		ObjectUtil.validNotNull(sql, "sql is required.");
		//适配oracle数据库，处理sql转换
		String matchedSql = getDBMatchedSql(sql);
		List<T> resultList = new ArrayList<T>();
		Set<TableShard> tableShards = this.route(null, dataSourceId, tableName,
				ruleName, dataSourcePoolName, preStatementParam);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.executeQuery(tableShards, matchedSql, dynaSqlVO,
					preStatementParam, resultSetExtractor);
		for (final TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			String newSql = getSqlBuilderManager().getDialectSql(dbType, matchedSql,
					dynaSqlVO);
			Object[] preStatementParamTmp = tableShard.getRouteResult() == null ? preStatementParam
					: (Object[]) tableShard.getRouteResult();
			resultList.add(getJdbcTemplateManager().getJdbcTemplate(
					dataSourceIdTmp).query(newSql, preStatementParamTmp,
					resultSetExtractor));
		}
		return resultList;
	}
	/**
	 * 获取满足的数据库语句，适配底层数据库
	 * @param sql
	 * @return
	 */
	private String getDBMatchedSql(String sql){
		//获取数据库类型
		String dbType = ModuleDataSrcUtils.getDbType();
		//oracle dm库时调用转换方法
		sql = DBSqlUtil.convertMysql2Other(sql, dbType);
        return sql;	        
	}
	/**
	 * 
	 * @Title: executeQuery
	 * @Description: 执行sql语句查询
	 * @param voClass
	 *            实体对象class 用来返回该实体对象集合,如果为空则返回BaseVO的集合
	 * @param sql
	 *            sql语句
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取分页信息
	 * @param preStatementParam
	 *            sql语句中预编译参数
	 * @param preStatementParamType
	 *            sql语句中预编译参数jdbc类别
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId
	 *            执行查询的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return VOSet<T> 查询结果集
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> VOSet<T> executeQuery(Class<T> voClass,
			final String sql, DynaSqlVO dynaSqlVO,
			final Object[] preStatementParam,
			final int[] preStatementParamType, String tableName,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		ObjectUtil.validNotNull(sql, "sql is required.");
		//适配oracle数据库，处理sql转换
		String matchedSql = getDBMatchedSql(sql);
		Map<String, Object> paramMap = null;
		if (preStatementParam != null) {
			paramMap = new HashMap<String, Object>();
			paramMap.put("preStatementParam", preStatementParam);
			if (preStatementParamType != null)
				paramMap.put("preStatementParamType", preStatementParamType);
		}

		if (!ObjectUtil.isNotNull(tableName) && voClass != null) {
			T vo = ClassInfo.newInstance(voClass);
			tableName = vo.getTableName();
		}

		TableDefVO tableDefVO = null;
		if (ObjectUtil.isNotNull(tableName))
			tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, paramMap);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.executeQuery(tableShards, tableDefVO,
					voClass, matchedSql, dynaSqlVO, preStatementParam,
					preStatementParamType);
		VOSet<T> resultVOSet = new VOSet<T>();
		List<T> voList = new ArrayList<T>();
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			String newSql = getSqlBuilderManager().getDialectSql(dbType, matchedSql,
					dynaSqlVO);
			Map<String, Object> paramMapTmp = tableShard.getRouteResult() == null ? null
					: (Map<String, Object>) tableShard.getRouteResult();
			Object[] preStatementParamTmp = paramMapTmp == null ? preStatementParam
					: (Object[]) paramMapTmp.get("preStatementParam");
			int[] preStatementParamTypeTmp = paramMapTmp == null ? preStatementParamType
					: (int[]) paramMapTmp.get("preStatementParamType");
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					preStatementParamTmp != null ? Arrays.asList(preStatementParamTmp)
							: null,
					ObjectUtil.asList(preStatementParamTypeTmp),
					typeHandlerRegistry);
			ResultSetExtractor<List<T>> resultSetExtractor = new VORowMapperResultSetExtractor<T>(
					voClass, tableDefVO, typeHandlerRegistry) {
			};
			voList.addAll(jdbcTemplate.query(newSql, preparedStatementSetter,
					resultSetExtractor));
		}
		resultVOSet.setSql(matchedSql);
		resultVOSet.setVoList(voList);
		if (dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage()))
			resultVOSet.setPage(dynaSqlVO.getPage());
		return resultVOSet;
	}

	/**
	 * 
	 * @Title: insert
	 * @Description: 新增
	 * @param vo
	 *            实体对象
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId
	 *            执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return T 实体对象
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> T insert(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		if (vo == null)
			return null;
		int insertCount = 0;
		String tableName = vo.getTableName();
		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);

		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, vo);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.insert(tableShards, tableDefVO, vo,
					dynaSqlVO);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			T voTmp = tableShard.getRouteResult() == null ? vo : (T) tableShard
					.getRouteResult();
			DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getInsertSql(dbType, tableDefVO, voTmp, dynaSqlVO,
							tableShard.getTableName());
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					dynaSqlResultVO.getPreStatementParams(),
					dynaSqlResultVO.getPreStatementParamJdbcTypes(),
					typeHandlerRegistry);
			insertCount += jdbcTemplate.update(dynaSqlResultVO.getSql(),
					preparedStatementSetter);
		}
		logger.debug("affected row count : " + insertCount);
		return vo;
	}

	/**
	 * 
	 * @Title: insertBatch
	 * @Description: 批量新增
	 * @param voList
	 *            实体对象
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取插入字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId
	 *            执行新增的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param perBatchSize
	 *            批量处理时每批次处理记录数
	 * @return
	 * @return VOSet<T> 批量新增结果集
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> VOSet<T> insertBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize) {
		VOSet<T> resultVOSet = new VOSet<T>();
		int insertCount = 0;
		if (!ObjectUtil.isNotEmpty(voList))
			return resultVOSet;

		String tableName = voList.get(0).getTableName();
		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);

		final Set<TableShard> tableShards = this.route(tableDefVO,
				dataSourceId, tableName, ruleName, dataSourcePoolName, voList);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.insertBatch(tableShards, tableDefVO, voList,
					dynaSqlVO, perBatchSize);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			List<T> voListTmp = tableShard.getRouteResult() == null ? voList
					: (List<T>) tableShard.getRouteResult();
			DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getInsertBatchSql(dbType, tableDefVO,
							(List<AbstractVO>) voListTmp, dynaSqlVO,
							tableShard.getTableName());
			JdbcTemplate jdbcTemplate = this.getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			int insertResult = 0;
			if (dynaSqlResultVO.getUsePreStatement()) {
				BatchPreparedStatementSetter batchPreparedStatementSetter = new VOBatchPreparedStatementSetter(
						dynaSqlResultVO.getPreStatementParams(),
						dynaSqlResultVO.getPreStatementParamJdbcTypes(),
						typeHandlerRegistry, voListTmp.size());
				VOBatchPreparedStatementCallback batchPreparedStatementCallback = new VOBatchPreparedStatementCallback(
						batchPreparedStatementSetter, perBatchSize);
				// int[] commondResult =
				// jdbcTemplate.batchUpdate(dynaSqlResultVO.getSql(),
				// batchPreparedStatementSetter);
				int[] commondResult = jdbcTemplate.execute(
						dynaSqlResultVO.getSql(),
						batchPreparedStatementCallback);
				insertResult = JdbcUtil.CountBatchUpdateRows(commondResult);
			} else {
				VOBatchUpdateStatementCallback batchUpdateStatementCallback = new VOBatchUpdateStatementCallback(
						dynaSqlResultVO.getBatchSqls(), perBatchSize);
				// int[] commondResult =
				// jdbcTemplate.batchUpdate(dynaSqlResultVO.getBatchSqls());
				int[] commondResult = jdbcTemplate
						.execute(batchUpdateStatementCallback);
				insertResult = JdbcUtil.CountBatchUpdateRows(commondResult);
			}
			insertCount += insertResult;
		}
		logger.debug("affected row count : " + insertCount);
		resultVOSet.setVoList(voList);
		return resultVOSet;
	}

	/**
	 * 
	 * @Title: delete
	 * @Description: 删除
	 * @param tableName
	 *            数据库表名 跟voClass必须有一个不为空, 用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	public <T extends AbstractVO> int delete(String tableName,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		ObjectUtil.validNotNull(tableName, "tableName is required.");
		return this.delete(tableName, null, dynaSqlVO, dataSourceId, ruleName,
				dataSourcePoolName);
	}

	/**
	 * 
	 * @Title: delete
	 * @Description: 删除
	 * @param tableName
	 *            数据库表名 跟vo必须有一个不为空, 用于拼接删除sql及数据库切分情况下按照配置的路由规则寻找数据源
	 * @param vo
	 *            实体对象
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象,用于获取删除条件以及是否开启预编译
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 删除结果
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> int delete(String tableName, T vo,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName) {
		ObjectUtil.validIsTrue(
				!ObjectUtil.isNotNull(tableName) && !ObjectUtil.isNotNull(vo),
				"at least one of 'tableName' or 'vo' must be given.");
		int deleteCount = 0;
		if (!ObjectUtil.isNotNull(tableName))
			tableName = vo.getTableName();
		ObjectUtil.validIsTrue(!ObjectUtil.isNotNull(tableName),
				"tableName is null and can't be given from vo .");
		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);
		boolean isByVO = vo != null
				&& ObjectUtil.isNotNull(getVOPkColValue(tableDefVO, vo));
		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, isByVO ? vo
						: dynaSqlVO);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.delete(tableShards, tableDefVO, vo,
					dynaSqlVO);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			T voTmp = vo;
			DynaSqlVO dynaSqlVOTmp = dynaSqlVO;
			if (tableShard.getRouteResult() != null && isByVO)
				voTmp = (T) tableShard.getRouteResult();
			if (tableShard.getRouteResult() != null && !isByVO)
				dynaSqlVOTmp = (DynaSqlVO) tableShard.getRouteResult();
			DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getDeleteSql(dbType, tableDefVO, voTmp, dynaSqlVOTmp,
							tableShard.getTableName());
			this.createTempTableAndInsertData(dynaSqlResultVO, dataSourceIdTmp);
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					dynaSqlResultVO.getPreStatementParams(),
					dynaSqlResultVO.getPreStatementParamJdbcTypes(),
					typeHandlerRegistry);
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			deleteCount += jdbcTemplate.update(dynaSqlResultVO.getSql(),
					preparedStatementSetter);
		}
		logger.debug("affected row count : " + deleteCount);
		return deleteCount;
	}

	/**
	 * 
	 * @Title: deleteBatch
	 * @Description: 批量删除
	 * @param voList
	 *            待删除的实体对象
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> int deleteBatch(List<T> voList,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		int deleteCount = 0;
		if (!ObjectUtil.isNotEmpty(voList))
			return deleteCount;
		String tableName = voList.get(0).getTableName();
		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);
		String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO
				.getPkColumnVO().getColcode() : null;
		ObjectUtil.validNotNull(pkColName, "primary key column of table ["
				+ tableName + "] can't be null.");

		final Set<TableShard> tableShards = this.route(tableDefVO,
				dataSourceId, tableName, ruleName, dataSourcePoolName, voList);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.deleteBatch(tableShards, tableDefVO, voList);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			List<T> voListTmp = tableShard.getRouteResult() == null ? voList
					: (List<T>) tableShard.getRouteResult();
			List<Object> primaryKeyList = new ArrayList<Object>(
					voListTmp.size());
			for (AbstractVO vo : voListTmp) {
				Object pkColValue = this.getVOPkColValue(tableDefVO, vo);
				if (ObjectUtil.isNotNull(pkColValue))
					primaryKeyList.add(pkColValue);
			}
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam(pkColName, primaryKeyList);
			DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getDeleteSql(dbType, tableDefVO, null, dynaSqlVO,
							tableShard.getTableName());
			this.createTempTableAndInsertData(dynaSqlResultVO, dataSourceIdTmp);
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					dynaSqlResultVO.getPreStatementParams(),
					dynaSqlResultVO.getPreStatementParamJdbcTypes(),
					typeHandlerRegistry);
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			deleteCount += jdbcTemplate.update(dynaSqlResultVO.getSql(),
					preparedStatementSetter);
		}
		logger.debug("affected row count : " + deleteCount);
		return deleteCount;
	}

	/**
	 * 
	 * @Title: update
	 * @Description: 更新
	 * @param vo
	 *            更新的实体对象 包含新值
	 * @param dynaSqlVO
	 *            用于动态拼接SQL的对象 这里用于获取更新字段、更新条件以及是否开启预编译
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> int update(T vo, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		int updateResult = 0;
		if (vo == null)
			return updateResult;
		String tableName = vo.getTableName();
		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);

		boolean isByVO = vo != null
				&& ObjectUtil.isNotNull(getVOPkColValue(tableDefVO, vo));

		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, isByVO ? vo
						: dynaSqlVO);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.update(tableShards, tableDefVO, vo,
					dynaSqlVO);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			T voTmp = vo;
			DynaSqlVO dynaSqlVOTmp = dynaSqlVO;
			if (tableShard.getRouteResult() != null && isByVO)
				voTmp = (T) tableShard.getRouteResult();
			if (tableShard.getRouteResult() != null && !isByVO)
				dynaSqlVOTmp = (DynaSqlVO) tableShard.getRouteResult();
			DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getUpdateSql(dbType, tableDefVO, voTmp, dynaSqlVOTmp,
							tableShard.getTableName());
			this.createTempTableAndInsertData(dynaSqlResultVO, dataSourceIdTmp);
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					dynaSqlResultVO.getPreStatementParams(),
					dynaSqlResultVO.getPreStatementParamJdbcTypes(),
					typeHandlerRegistry);
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			updateResult += jdbcTemplate.update(dynaSqlResultVO.getSql(),
					preparedStatementSetter);
		}
		logger.debug("affected row count : " + updateResult);
		return updateResult;
	}

	/**
	 * 
	 * @Title: updateBatch
	 * @Description: 批量更新
	 * @param voList
	 *            更新的实体对象集合
	 * @param dynaSqlVO
	 *            动态sql对象 用于动态拼接SQL的对象 这里主要用于获取更新字段列表及是否关闭预编译, 默认开启预编译
	 * @param dataSourceId
	 *            执行更新的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param perBatchSize
	 *            批量处理时每批次处理记录数
	 * @return
	 * @return int 更新记录数
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractVO> int updateBatch(List<T> voList,
			DynaSqlVO dynaSqlVO, String dataSourceId, String ruleName,
			String dataSourcePoolName, int perBatchSize) {
		int updateCount = 0;
		if (!ObjectUtil.isNotEmpty(voList))
			return updateCount;
		String tableName = voList.get(0).getTableName();
		TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);

		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, voList);

		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.updateBatch(tableShards, tableDefVO, voList,
					dynaSqlVO, perBatchSize);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			List<T> voListTmp = tableShard.getRouteResult() == null ? voList
					: (List<T>) tableShard.getRouteResult();
			DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getUpdateBatchSql(dbType, tableDefVO,
							(List<AbstractVO>) voListTmp, dynaSqlVO,
							tableShard.getTableName());
			JdbcTemplate jdbcTemplate = this.getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			if (dynaSqlResultVO.getUsePreStatement()) {
				BatchPreparedStatementSetter batchPreparedStatementSetter = new VOBatchPreparedStatementSetter(
						dynaSqlResultVO.getPreStatementParams(),
						dynaSqlResultVO.getPreStatementParamJdbcTypes(),
						typeHandlerRegistry, voListTmp.size());
				VOBatchPreparedStatementCallback batchPreparedStatementCallback = new VOBatchPreparedStatementCallback(
						batchPreparedStatementSetter, perBatchSize);
				// int[] commondResult =
				// jdbcTemplate.batchUpdate(dynaSqlResultVO.getSql(),
				// batchPreparedStatementSetter);
				int[] commondResult = jdbcTemplate.execute(
						dynaSqlResultVO.getSql(),
						batchPreparedStatementCallback);
				updateCount += JdbcUtil.CountBatchUpdateRows(commondResult);
			} else {
				VOBatchUpdateStatementCallback batchUpdateStatementCallback = new VOBatchUpdateStatementCallback(
						dynaSqlResultVO.getBatchSqls(), perBatchSize);
				// int[] commondResult =
				// jdbcTemplate.batchUpdate(dynaSqlResultVO.getBatchSqls());
				int[] commondResult = jdbcTemplate
						.execute(batchUpdateStatementCallback);
				updateCount += JdbcUtil.CountBatchUpdateRows(commondResult);
			}
		}
		logger.debug("affected row count : " + updateCount);
		return updateCount;
	}
	
	/**
	 * 
	 * @Title: executeUpate
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql
	 *            sql语句
	 * @param preStatementParam
	 *            sql预编译参数
	 * @param preStatementParamType
	 *            sql预编译参数jdbc类别
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, final Object[] preStatementParam,
			final int[] preStatementParamType, String tableName,
			String dataSourceId, String ruleName, String dataSourcePoolName) {
		return executeUpate(sql, preStatementParam, preStatementParamType, tableName,
				dataSourceId, ruleName, dataSourcePoolName, true);
	}

	/**
	 * 
	 * @Title: executeUpate
	 * @Description: 更新,执行insert、update及delete语句
	 * @param sql
	 *            sql语句
	 * @param preStatementParam
	 *            sql预编译参数
	 * @param preStatementParamType
	 *            sql预编译参数jdbc类别
	 * @param tableName
	 *            数据库表名, 用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourceId
	 *            执行sql的数据源ID, 如果不为空则直接返回该数据源ID,否则按配置的路由规则查找数据源,
	 *            如果没有路由到数据源则返回默认数据源
	 * @param ruleName
	 *            规则名 对应路由规则配置文件 <rule>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @param dataSourcePoolName
	 *            数据源池名 对应路由规则配置文件
	 *            <dataSourcePool>节点的name属性,用于数据库切分情况下按照配置的路由规则寻找数据源
	 * @return
	 * @return int 更新结果
	 * @throws
	 */
	public int executeUpate(String sql, final Object[] preStatementParam,
			final int[] preStatementParamType, String tableName,
			String dataSourceId, String ruleName, String dataSourcePoolName, boolean isAddTS) {
		ObjectUtil.validNotNull(sql, "sql is required.");
		//适配oracle数据库，处理sql转换
		//记录转换前sql
		String tmpSql = sql;
		//获取满足数据库的sql语句
		sql = getDBMatchedSql(sql);
		int updateResult = 0;
		Map<String, Object> paramMap = null;
		if (preStatementParam != null) {
			paramMap = new HashMap<String, Object>();
			paramMap.put("preStatementParam", preStatementParam);
			if (preStatementParamType != null)
				paramMap.put("preStatementParamType", preStatementParamType);
		}

		TableDefVO tableDefVO = null;
		if (ObjectUtil.isNotNull(tableName))
			tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);		
		
		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, paramMap);
		if (multTaskMode && tableShards.size() > 1)
			return multTaskSupport.executeUpate(tableShards, sql,
					preStatementParam, preStatementParamType);
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			//判断isAddTS值是否需要添加更新ts
			if(isAddTS) {
				if(tableDefVO==null) {
					throw new HDRuntimeException("表定义不存在:"+tableName);
				}
				sql = getSqlBuilderManager().getSqlBuilder(dbType).addTS(sql,tableDefVO);
			}
			Map<String, Object> paramMapTmp = tableShard.getRouteResult() == null ? null
					: (Map<String, Object>) tableShard.getRouteResult();
			Object[] preStatementParamTmp = paramMapTmp == null ? preStatementParam
					: (Object[]) paramMapTmp.get("preStatementParam");
			int[] preStatementParamTypeTmp = paramMapTmp == null ? preStatementParamType
					: (int[]) paramMapTmp.get("preStatementParamType");
			//检查是否包含clob类型字段
			boolean isDealClob = false;
			//当属于oracle数据库环境时，检查clob字段值是否超长
			//oracle处理clob写入，当字符串长度超过4000时，需要转换为clob处理。
			ClobInfoVO clobInfoVO = null;
			if(dbType.equals(DBType.ORACLE.getCode())){
				//获取clob数据
				clobInfoVO = ClobUtil.getClobInfo(tableName, sql);
				isDealClob = clobInfoVO.isDealClob();
			}
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			if(!isDealClob){
				if (preStatementParamTmp == null)
					updateResult += jdbcTemplate.update(sql);
				else if (preStatementParamTypeTmp == null)
					updateResult += jdbcTemplate.update(sql, preStatementParamTmp);
				else
					updateResult += jdbcTemplate.update(sql, preStatementParamTmp,
							preStatementParamTypeTmp);
			}else{
				//更新
				int result = ClobUtil.updateClobData(jdbcTemplate, clobInfoVO, dataSourceIdTmp, 
						sql, preStatementParamTmp,
						preStatementParamTypeTmp);
				updateResult += result;
			}
		}
		logger.debug("affected row count : " + updateResult);
		return updateResult;
	}
	
	/**
	 * 
	 * @Title: getVOPkColValue
	 * @Description: 获取实体对象的主键值
	 * @param tableDefVO
	 * @param vo
	 * @return
	 * @return Object
	 * @throws
	 */
	public Object getVOPkColValue(TableDefVO tableDefVO, AbstractVO vo) {
		ObjectUtil.validNotNull(vo, "vo is required.");
		if (tableDefVO == null) {
			String tableName = vo.getTableName();
			tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		}
		String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO
				.getPkColumnVO().getColcode() : null;
		Object pkValue = null;
		if (ObjectUtil.isNotNull(pkColName))
			pkValue = vo.get(pkColName.toLowerCase());
		//当pkValue类型不为Long时，转换成Long类型
		if(pkValue != null && !(pkValue instanceof Long)){
			pkValue = ConvertUtils.convert(pkValue, Long.class);
		}
		return pkValue; 
	}

	/**
	 * 
	 * @Title: setVOPkColValue
	 * @Description: 设置主键值
	 * @param vo
	 *            实体对象
	 * @param pkValue
	 *            主键值
	 * @return void
	 * @throws
	 */
	public void setVOPkColValue(AbstractVO vo, Object pkValue,
			TableDefVO tableDefVO) {
		if (vo == null)
			return;
		String tableName = vo.getTableName();
		if (tableDefVO == null)
			tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		if (!ObjectUtil.isNotNull(getVOPkColValue(tableDefVO, vo))) {
			String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO
					.getPkColumnVO().getColcode() : null;
			vo.set(pkColName, pkValue);
		}
	}

	public TableDefVO getTableDefVO(String tableName) {
		return tableDefService.queryDetailedTableByTbname(tableName);
	}

	/**
	 * 
	 * @Title: getVOPkColName
	 * @Description: 获取表的主键字段名
	 * @param tableName
	 * @return
	 * @return String
	 * @throws
	 */
	public String getVOPkColName(String tableName) {
		if (!ObjectUtil.isNotNull(tableName))
			return null;
		final TableDefVO tableDefVO = tableDefService
				.queryDetailedTableByTbname(tableName);
		if (tableDefVO == null)
			return null;
		return tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO()
				.getColcode() : null;
	}

	/**
	 * 
	 * @Title: createTempTableAndInsertData
	 * @Description: 创建临时表及往临时表插入数据
	 * @param dynaSqlResultVO
	 *            动态生成sql的结果封装 用于获取临时表信息
	 * @param dataSourceId
	 *            数据源ID
	 * @return void
	 * @throws
	 */
	public void createTempTableAndInsertData(DynaSqlResultVO dynaSqlResultVO,
			String dataSourceId) {
		if (dynaSqlResultVO == null)
			return;
		List<String> tempTableNames = dynaSqlResultVO.getTempTableNames();
		if (tempTableNames != null) {
			for (int i = 0; i < tempTableNames.size(); i++) {
				String tempTableName = tempTableNames.get(i);
				String[][] columns = dynaSqlResultVO.getTempTableColumns().get(
						i);
				List<Object> tempTableData = dynaSqlResultVO
						.getTempTableDataList().get(i);
				tempTableDao.createAndInsertData(tempTableName, columns,
						tempTableData, dataSourceId);
			}
		}
	}

	public DataSourceManager getDataSourceManager() {
		return dataSourceManager;
	}

	public void setDataSourceManager(DataSourceManager dataSourceManager) {
		this.dataSourceManager = dataSourceManager;
	}

	public ShardingRouter getShardingRouter() {
		return shardingRouter;
	}

	public void setShardingRouter(ShardingRouter shardingRouter) {
		this.shardingRouter = shardingRouter;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public String getDefaultDataSourceId() {
		return defaultDataSourceId;
	}

	public void setDefaultDataSourceId(String defaultDataSourceId) {
		this.defaultDataSourceId = defaultDataSourceId;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public SqlBuilderManager getSqlBuilderManager() {
		return sqlBuilderManager;
	}

	public void setSqlBuilderManager(SqlBuilderManager sqlBuilderManager) {
		this.sqlBuilderManager = sqlBuilderManager;
	}

	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return typeHandlerRegistry;
	}

	public void setTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public boolean getMultTaskMode() {
		return multTaskMode;
	}

	public void setMultTaskMode(boolean multTaskMode) {
		this.multTaskMode = multTaskMode;
	}

	public JdbcTemplateManager getJdbcTemplateManager() {
		return jdbcTemplateManager;
	}

	public void setJdbcTemplateManager(JdbcTemplateManager jdbcTemplateManager) {
		this.jdbcTemplateManager = jdbcTemplateManager;
	}

	/**
	 * setDefaultValue:(平台统一设置的字段值). <br/>
	 * date: 2015年10月9日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 * @param tableDefVO
	 */
	public void setDefaultValue(AbstractVO vo) {
	}
	
	public <T extends AbstractVO> List<TableColumnVO> getTableColVoList(String catalog,String schema,String tableName,Long parentid) {
		ObjectUtil.validNotNull(tableName, "sql is required.");
		Set<TableShard> tableShards = this.route(null, null,
				tableName, null, null, null);
		List<TableColumnVO> tableColVoList = new ArrayList<TableColumnVO>();
		
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			JdbcTemplate jdbcTemplate = getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			//首先检查表是否存在
			String chkTableSql = sqlBuilderManager.getChkTableSql(dbType, tableName);
			Boolean isExistTable = (Boolean) jdbcTemplate.execute(chkTableSql, new PreparedStatementCallback<Object>(){

				@Override
				public Object doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ResultSet rs=ps.executeQuery();
					Boolean isExistTable = Boolean.FALSE;
					while(rs.next()) {
						// 达梦返回了数量 mysql不进入此处
						int count = rs.getInt(1);
						isExistTable = (count > 0);
					}
					return isExistTable;
				}
				
			});
			if(!isExistTable.booleanValue())
				continue;
			//获取列sql
			String colSql = sqlBuilderManager.getColSqlByTable(dbType, tableName);			
			//执行sql，查询该表已定义的列数据
			Map<String,String> colMap = (Map<String, String>) jdbcTemplate.execute(colSql, new PreparedStatementCallback<Object>(){

				@Override
				public Object doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ResultSet rs=ps.executeQuery();
					Map<String,String> valMap=new HashMap<String, String>();
					while(rs.next())
						valMap.put(rs.getString("Field"), rs.getString("Type"));
					return valMap;
				}
				
			});
			//这里执行上面语句的原因是，下面的语句获取的数据类型不对。依据上面的type，重置下列类型。
			ConnectionCallbackMetaData<TableColumnVO> connectionCallbackMetaData = new ConnectionCallbackMetaData<TableColumnVO>(dbType,catalog,schema,tableName,parentid);
			connectionCallbackMetaData.setColMap(colMap);
			tableColVoList = jdbcTemplate.execute(connectionCallbackMetaData);
		}
		return tableColVoList;
	}
	private Map<String,String> getDbColMap(String tableName){
		Map<String,String> colMap = new HashMap<String, String>();
		try {
			String dbType = getDbType(tableName);
			String colSql = sqlBuilderManager.getColSqlByTable(dbType, tableName);
			List<Map<String,String>> colMapList = executeQuery(colSql, null,
					null, new ResultSetExtractor<Map<String,String>>() {

						@Override
						public Map<String,String> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							Map<String,String> valMap=new HashMap<String, String>();
							while(rs.next())
								valMap.put(rs.getString("Field"), rs.getString("Type"));
							return valMap;
						}

					}, tableName,null,null,null);
			if(colMapList.size()!=0)
				colMap = colMapList.get(0);
		} catch (HDException e) {
			logger.error(e.getMessage());
		}
		return colMap;
	}
	public int executeBatchUpdate(String sql,final Object... objs)
	{
		int updateCount = 0;
		String tableName = objs[0].toString();
		Set<TableShard> tableShards = this.route(null, null,
				tableName, null, null, null);
		TableDefVO tableDefVO = null;
		if (ObjectUtil.isNotNull(tableName))
			tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		final String[] colArray = objs[2].toString().split(",");
		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			sql = getSqlBuilderManager().getSqlBuilder(dbType).addTS(sql, tableDefVO);
			JdbcTemplate jdbcTemplate = this.getJdbcTemplateManager()
					.getJdbcTemplate(dataSourceIdTmp);
			jdbcTemplate.execute(sql, new PreparedStatementCallback<Object>(){
				@Override
				public Object doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					for(Object obj: (List)objs[1])
					{
						int parameterIndex = 1;
						for(String colName : colArray){
							colName = colName.trim();
							Object colValue = ((AbstractVO)obj).get(colName.toLowerCase());
							ps.setObject(parameterIndex, colValue);
							parameterIndex++;
						}
						ps.addBatch();
					}
					int count[] = ps.executeBatch();
					return JdbcUtil.CountBatchUpdateRows(count);
				}
				}
			);
		}
		return updateCount;
	}
	
	public int getCount(String tableName, DynaSqlVO dynaSqlVO,
			String dataSourceId, String ruleName, String dataSourcePoolName)
	{
		int rows = 0;
		TableDefVO tableDefVO = null;
		if (ObjectUtil.isNotNull(tableName))
			tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);		
		Set<TableShard> tableShards = this.route(tableDefVO, dataSourceId,
				tableName, ruleName, dataSourcePoolName, dynaSqlVO);

		for (TableShard tableShard : tableShards) {
			String dataSourceIdTmp = tableShard.getDataSourceId();
			String dbType = getDataSourceManager().getDbType(dataSourceIdTmp);
			DynaSqlVO dynaSqlVOTmp = tableShard.getRouteResult() == null ? dynaSqlVO
					: (DynaSqlVO) tableShard.getRouteResult();
			final DynaSqlResultVO dynaSqlResultVO = getSqlBuilderManager()
					.getSelectSql(dbType, tableDefVO, dynaSqlVOTmp,
							tableShard.getTableName());
			PreparedStatementSetter preparedStatementSetter = new VOPreparedStatementSetter(
					dynaSqlResultVO.getPreStatementParams(),
					dynaSqlResultVO.getPreStatementParamJdbcTypes(),
					typeHandlerRegistry);			
			if (dynaSqlVO != null)
			{
				if(ObjectUtil.isNotNull(dynaSqlVO.getPage()))
						{
					rows += this.queryCount(
							dynaSqlResultVO.getSelectCountSql(), dataSourceIdTmp,
							preparedStatementSetter);
						}else{
							rows += this.queryCount(
									dynaSqlResultVO.getSelectCountSql(), dataSourceIdTmp,
									preparedStatementSetter);
						}
					

			}

		}
		return rows;
	}
}