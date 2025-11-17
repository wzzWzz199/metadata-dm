package com.hayden.hap.common.db.orm.sql;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.db.orm.exception.DBNotSupportedException;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.DBType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** 
 * @ClassName: SqlBuilderManager 
 * @Description: SqlBuilder管理器,根据数据库类别选择对应的SqlBuilder
 * @author LUYANYING
 * @date 2015年3月24日 上午11:56:10 
 * @version V1.0   
 *  
 */
public class SqlBuilderManager {
	
	private Map<String, SqlBuilder> sqlBuilderMap = new HashMap<String, SqlBuilder>();
	{
		sqlBuilderMap.put(DBType.MYSQL.getCode(), new MySqlSqlBuilder());
		sqlBuilderMap.put(DBType.ORACLE.getCode(), new OracleSqlBuilder());
		sqlBuilderMap.put(DBType.DM.getCode(), new DMSqlBuilder());
	}
	
	public SqlBuilder getSqlBuilder(String dbType){
		if(dbType == null)
			throw new DBNotSupportedException("database ["+dbType+"] isn't supported");
		if(sqlBuilderMap.containsKey(dbType)){
			return sqlBuilderMap.get(dbType);
		}else
			throw new DBNotSupportedException("database ["+dbType+"] isn't supported");
	}
	
	public String getSelectCountsSql(String dbType, TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getSelectCountSql(new String[]{""});
	} 
	
	/**
	 * 
	 * @Title: getSelectSql 
	 * @Description: 生成select查询语句
	 * @param dbType 数据库类别
	 * @param tableDefVO 表的信息 
	 * @param dynaSqlVO 用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getSelectSql(String dbType, TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getSelectSql(tableDefVO, dynaSqlVO, sqlTableName);
	}

	/**
	 * 
	 * @Title: getInsertSql 
	 * @Description: 生成insert语句
	 * @param dbType 数据库类别
	 * @param tableDefVO 表的信息 
	 * @param vo 实体VO对象
	 * @param dynaSqlVO 这里用于获取insert的字段列表以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getInsertSql(String dbType, TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getInsertSql(tableDefVO, vo, dynaSqlVO, sqlTableName);
	}

	/**
	 * 
	 * @Title: getInsertBatchSql 
	 * @Description: 生成insert语句 用于批量新增
	 * @param dbType 数据库类别
	 * @param tableDefVO 表的信息
	 * @param voList 实体VO对象集合
	 * @param dynaSqlVO 这里用于获取insert的字段列表以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getInsertBatchSql(String dbType, TableDefVO tableDefVO, List<AbstractVO> voList, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getInsertBatchSql(tableDefVO, voList, dynaSqlVO, sqlTableName);
	}
	
	/**
	 * 
	 * @Title: getUpdateSql 
	 * @Description: 生成update语句
	 * @param dbType 数据库类别
	 * @param tableDefVO 表的信息
	 * @param vo 保存的实体对象,用于获取字段新值
	 * @param dynaSqlVO 用于获取更新字段、更新条件以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getUpdateSql(String dbType, TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getUpdateSql(tableDefVO, vo, dynaSqlVO, sqlTableName);
	}
	
	/**
	 * 
	 * @Title: getUpdateBatchSql 
	 * @Description: 生成update语句 批量更新
	 * @param dbType 数据库类别
	 * @param tableDefVO 表的信息
	 * @param voList 更新的实体对象集合,实体对象主键值不能为空
	 * @param dynaSqlVO 用于获取更新字段以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getUpdateBatchSql(String dbType, TableDefVO tableDefVO, List<AbstractVO> voList, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getUpdateBatchSql(tableDefVO, voList, dynaSqlVO, sqlTableName);
	}
	
	/**
	 * 
	 * @Title: getDeleteSql 
	 * @Description: 生成delete语句
	 * @param dbType 数据库类别
	 * @param vo 需要删除的实体对象,如果实体对象不为空，则实体对象主键值不能为空
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取删除条件以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getDeleteSql(String dbType, TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName) {
		return getSqlBuilder(dbType).getDeleteSql(tableDefVO, vo, dynaSqlVO, sqlTableName);
	}
	
	/**
	 * 
	 * @Title: getDialectSql 
	 * @Description: 翻译sql
	 * @param dbType 数据库类别
	 * @param sql sql语句
	 * @param dynaSqlVO 用于获取分页信息
	 * @return
	 * @return String dbType对应的sql语句
	 * @throws
	 */
	public String getDialectSql(String dbType, String sql, DynaSqlVO dynaSqlVO){
		return getSqlBuilder(dbType).getDialectSql(sql, dynaSqlVO);
	}
	
	/**
	 * 
	 * @Title: getCreateTempTableSql 
	 * @Description: 获取创建临时表sql
	 * @param dbType 数据库类别
	 * @param tempTableName 临时表名
	 * @param columInfoStr 字段信息 形如:id VARCHAR(50),name VARCHAR(100)...
	 * @return
	 * @return String 创建临时表sql语句
	 * @throws
	 */
	public String getCreateTempTableSql(String dbType, String tempTableName, String[] columInfoStr){
		return getSqlBuilder(dbType).getCreateTempTableSql(tempTableName, columInfoStr);
	}
	/**
	 * 
	 * @Title: getDropTempTableSql 
	 * @Description: 获取删除临时表sql
	 * @param dbType 数据库类别
	 * @param tempTableName 临时表名
	 * @return
	 * @return String 删除临时表sql
	 * @throws
	 */
	public String getDropTempTableSql(String dbType, String tempTableName){
		return getSqlBuilder(dbType).getDropTempTableSql(tempTableName);
	}
	
	/**
	 * 
	 * @Title: getCreateTableSql 
	 * @Description: 获取建表语句
	 * @param dbType 数据库类别
	 * @param tableDefVO 数据库表信息
	 * @return
	 * @return String 建表语句
	 * @throws
	 */
	public List<String> getCreateTableSql(String dbType, TableDefVO tableDefVO){
		return getSqlBuilder(dbType).getCreateTableSql(tableDefVO); 
	}
	
	/**
	 * 
	 * @Title: getDropTableSql 
	 * @Description: 获取删表语句
	 * @param dbType 数据库类别
	 * @param tableNme 表名
	 * @return
	 * @return String drop table sql语句
	 * @throws
	 */
	public String getDropTableSql(String dbType, String tableNme){
		return getSqlBuilder(dbType).getDropTableSql(tableNme);
	}
	
	public String addTS(String dbType,String sql,TableDefVO tableDefVO){
		return getSqlBuilder(dbType).addTS(sql,tableDefVO);
	}
	
	public List<String> getAddColByTableColVO(String dbType,String table,List<TableColumnVO> tableColumnVOList){
		return getSqlBuilder(dbType).getAddColByTableColVO(table,tableColumnVOList);
	}
	
	public List<String> getUpdateColByTableColVO(String dbType,String table,List<TableColumnVO> tableColumnVOList){
		return getSqlBuilder(dbType).getUpdateColByTableColVO(table,tableColumnVOList);
	}
	
	public List<String> getUpdatePkByTableColVO(String dbType,String table,List<TableColumnVO> tableColumnVOList){
		return getSqlBuilder(dbType).getUpdatePkByTableColVO(table,tableColumnVOList);
	}
	
	/**
	 * @param dbType
	 * @param table
	 * @return 返回查询数据库表定义的列的sql
	 * @author wangyi
	 * @date 2017年5月16日
	 */
	public String getColSqlByTable(String dbType,String table){
		return getSqlBuilder(dbType).getColSqlByTable(table);
	}
	
	/**
	 * @param dbType
	 * @param table
	 * @return 返回检测表是否存在sql
	 * @author wangyi
	 * @date 2017年5月16日
	 */
	public String getChkTableSql(String dbType,String table){
		return getSqlBuilder(dbType).getChkTableSql(table);
	}
}
