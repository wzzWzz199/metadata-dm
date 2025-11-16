package com.hayden.hap.common.db.orm.sql;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SqlBuilder
 * @Description:
 * @author LUYANYING
 * @date 2015年3月23日 上午10:49:48
 * @version V1.0
 * 
 */
public interface SqlBuilder {
	static final String RS_COLUMN = "totalCount";
	static final String SQL_END_DELIMITER = ";";
	
	/**
	 * 
	 * @Title: getSelectSql 
	 * @Description: 生成select查询语句
	 * @param tableDefVO 表的信息 
	 * @param dynaSqlVO 用于获取查询条件、order by、group by等语句、查询的字段列表、分页信息以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getSelectSql(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String sqlTableName);
	
	/**
	 * 
	 * @Title: getSelectLimitSql 
	 * @Description: 返回分页的select语句
	 * @param sql select语句
	 * @param offset 查询从第几行开始
	 * @param limit 每页记录数
	 * @return
	 * @return String
	 * @throws
	 */
	public String getSelectLimitSql(String sql, int offset, int limit);
	/**
	 * 
	 * @Title: getSelectLimitSql 
	 * @Description: 返回分页的select语句
	 * @param sql select语句
	 * @param paramMap 值参数,用户后续优化分页性能时使用,传入主键列、表名等数据
	 * @param offset 查询从第几行开始
	 * @param limit 每页记录数
	 * @return
	 * @return String
	 * @throws
	 */
	public String getSelectLimitSql(String sql,Map<String,String> paramMap, int offset, int limit);
	/**
	 * 
	 * @Title: getSelectCountSql 
	 * @Description: 返回 select Count语句
	 * @param params 参数,传递sql语句信息,包括表名、查询条件和是否锁表等
	 * @return
	 * @return String select Count语句
	 * @throws
	 */
	public String getSelectCountSql(String[] params); 

	/**
	 * 
	 * @Title: getInsertSql 
	 * @Description: 生成insert语句
	 * @param tableDefVO 表的信息 
	 * @param vo 实体VO对象
	 * @param dynaSqlVO 这里用于获取insert的字段列表以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getInsertSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName);

	/**
	 * 
	 * @Title: getInsertBatchSql 
	 * @Description: 生成insert语句 用于批量新增
	 * @param tableDefVO 表的信息
	 * @param voList 实体VO对象集合
	 * @param dynaSqlVO 这里用于获取insert的字段列表以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getInsertBatchSql(TableDefVO tableDefVO, List<AbstractVO> voList, DynaSqlVO dynaSqlVO, String sqlTableName);

	/**
	 * 
	 * @Title: getUpdateSql 
	 * @Description: 生成update语句
	 * @param tableDefVO 表的信息
	 * @param vo 保存的实体对象,用于获取字段新值
	 * @param dynaSqlVO 用于获取更新字段、更新条件以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getUpdateSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName);
	
	/**
	 * 
	 * @Title: getUpdateBatchSql 
	 * @Description: 生成update语句 批量更新
	 * @param tableDefVO 表的信息
	 * @param voList 更新的实体对象集合,实体对象主键值不能为空
	 * @param dynaSqlVO 用于获取更新字段以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getUpdateBatchSql(TableDefVO tableDefVO, List<AbstractVO> voList, DynaSqlVO dynaSqlVO, String sqlTableName);

	/**
	 * 
	 * @Title: getDeleteSql 
	 * @Description: 生成delete语句
	 * @param vo 需要删除的实体对象,如果实体对象不为空，则实体对象主键值不能为空
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取删除条件以及是否开启预编译
	 * @param sqlTableName sql表名,用于生成sql,如果为空则使用tableDefVO的表名,主要用于数据库水平切分时同一个表在每个数据源表名不一样情况
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	public DynaSqlResultVO getDeleteSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName);
	
	/**
	 * 
	 * @Title: getDialectSql 
	 * @Description: 翻译sql
	 * @param sql 待翻译的sql语句 
	 * @param dynaSqlVO 用于获取分页信息
	 * @return
	 * @return String 翻译后的sql语句
	 * @throws
	 */
	public String getDialectSql(String sql, DynaSqlVO dynaSqlVO);
	
	/**
	 * 
	 * @Title: getCreateTempTableSql 
	 * @Description: 获取创建临时表sql
	 * @param tempTableName 临时表名
	 * @param columInfoStr 字段信息 形如:id VARCHAR(50),name VARCHAR(100)...
	 * @return
	 * @return String 创建临时表sql语句
	 * @throws
	 */
	public String getCreateTempTableSql(String tempTableName, String[] columInfoStr);
	/**
	 * 
	 * @Title: getDropTempTableSql 
	 * @Description: 获取删除临时表sql
	 * @param tempTableName 临时表名
	 * @return
	 * @return String 删除临时表sql
	 * @throws
	 */
	public String getDropTempTableSql(String tempTableName);
	
	/**
	 * 
	 * @Title: getCreateTableSql 
	 * @Description: 获取建表语句
	 * @param tableDefVO 数据库表信息
	 * @return
	 * @return String 建表语句
	 * @throws
	 */
	public List<String> getCreateTableSql(TableDefVO tableDefVO);
	
	/**
	 * 
	 * @Title: getDropTableSql 
	 * @Description: 获取删表语句
	 * @param tableNme 表名
	 * @return
	 * @return String drop table sql语句
	 * @throws
	 */
	public String getDropTableSql(String tableNme);
	
	/**
	 * createTableDefFromDb:(根据现有表生成表定义数据). <br/>
	 * date: 2015年11月26日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableName
	 * @return
	 */
	public String createTableDefFromDb(String tableName);
	
	/**@desc 增加默认字段
	 * @param sql
	 * @return
	 */
	public String addTS(String sql,TableDefVO tableDefVO);
	
	/**@desc 依据TableColumnVO获取alter sql，用于pdm中修改列信息后同步执行到数据库，包括新增和修改列，删除暂时没有加
	 * @param table
	 * @param tableColumnVOList
	 * @return
	 */
	public List<String> getAddColByTableColVO(String table,List<TableColumnVO> tableColumnVOList);
	
	public List<String> getUpdateColByTableColVO(String table,List<TableColumnVO> tableColumnVOList);
	
	/**@desc 参数tableColumnVOList包含pdm中涉及pk的列
	 * @param table
	 * @param tableColumnVOList
	 * @return
	 */
	public List<String> getUpdatePkByTableColVO(String table,List<TableColumnVO> tableColumnVOList);
	
	public String getColSqlByTable(String table);
	
	/**
	 * @param table
	 * @return 返回检测表是否存在的sql
	 * @author wangyi
	 * @date 2017年5月16日
	 */
	public String getChkTableSql(String table);
	
	/**
	 * 处理where条件。录入时基于mysql语法，这里转换生成适用于oracle的语句。
	 * @user wangyi
	 * @param whereSql
	 * @return String
	 */
	public String getSupportedWhereSql(String whereSql);
	
	/**
	 * 获取含有中文的字段排序语句
	 * @param column 列名
	 * @param order 排序方式 asc 或者 desc
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月7日
	 */
	public String getOrderBySqlForZh_cn(String column, String order);
}
