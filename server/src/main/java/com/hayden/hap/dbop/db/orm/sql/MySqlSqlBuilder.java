package com.hayden.hap.dbop.db.orm.sql;

import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: MySqlSqlBuilder 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月23日 上午10:51:17 
 * @version V1.0   
 *  
 */
public class MySqlSqlBuilder extends AbstractSqlBuilder implements SqlBuilder {
	public static final String COUNT_AS_RESULTSET = "countTable";
	
	@Override
	public String getSelectLimitSql(String sql, Map<String,String> paramMap, int offset, int limit) {
		sql = sql.trim();
		boolean isForUpdate = false;
		if ( sql.toLowerCase().endsWith(" for update") ) {
			sql = sql.substring( 0, sql.length()-11 );
			isForUpdate = true;
		}
		if(offset<0){
		    offset=0;
		}
		
		StringBuffer pagingSelect = new StringBuffer();
		if(ObjectUtil.isNotNull(paramMap)){
			String tableName=paramMap.get("tableName");
			String pkColName=paramMap.get("pkColName");
			//考虑数据量大时,分页查询最后面数据时较慢,采用下面方式优化
			//拼装主表数据 主要是修改查询条件中的主键字段,添加表名(解决字段重复,多个表中都有)
			if(sql.substring(sql.indexOf("select")+6, sql.indexOf("from")).indexOf((tableName+"."+pkColName).toLowerCase())!=-1)
				pagingSelect.append("select ").
					append(sql.substring(sql.indexOf("select")+6, sql.indexOf("from")).trim()).
					append(" from ").append(tableName);
			else
				pagingSelect.append("select ").
					append(sql.substring(sql.indexOf("select")+6, sql.indexOf("from")).trim().
							replaceFirst("\\b"+pkColName+"\\b", tableName+"."+pkColName)).
					append(" from ").append(tableName);
			//拼装分页获取主键数据
			String aliasField = "pkField";
			pagingSelect.append(" , (select ").append(pkColName).append(" as ").append(aliasField).append(" ").append(sql.substring(sql.indexOf("from"))).
				append(" limit ").append(offset).append(" , ").append(limit).append(") tmp");
			//拼装表关联数据
			pagingSelect.append(" where tmp.").append(aliasField).append("=").
				append(tableName).append(".").append(pkColName);
			//外层添加排序
			int orderByIndex = sql.indexOf("order by");
			if(orderByIndex!=-1){
				String orderByInfo = sql.substring(orderByIndex);
				pagingSelect.append(" ").append(orderByInfo);
			}
			
		}else{
			pagingSelect.append(sql);
			
			pagingSelect.append(" limit "+(offset)+" , "+(limit));
		}		

		if ( isForUpdate ) {
			pagingSelect.append( " for update" );
		}
		
		return pagingSelect.toString();
	}

	@Override
	public String getColDateValueString(Object paramValue, JdbcType jdbcType) {
		if(paramValue instanceof Date){
			//datetime的TypeCode匹配了date类型，需要增加下typename的校验
			if(jdbcType == null || (jdbcType.getTypeCode() == JdbcType.TIMESTAMP.getTypeCode()
									||jdbcType.getTypeName().equalsIgnoreCase(JdbcType.DATETIME.getTypeName())))
				return "str_to_date('" + ObjectUtil.getDateFormatString((Date)paramValue) + "','%Y-%m-%d %H:%i:%s')";
			else if(jdbcType.getTypeCode() == JdbcType.DATE.getTypeCode())
				return "str_to_date('" + ObjectUtil.getDateOnlyFormatString((Date)paramValue) + "','%Y-%m-%d')";
			else if(jdbcType.getTypeCode() == JdbcType.TIME.getTypeCode())
				return "str_to_date('" + ObjectUtil.getTimeOnlyFormatString((Date)paramValue) + "','%H:%i:%s')";
			return "";
		}
		return "";
	}

	@Override
	public String getDialectGroupConcatSql(String sql) {
		return sql;
	}

	@Override
	public String getDialectStrToDateSql(String sql) {
		return sql;
	}

	@Override
	public String getCreateTempTableSql(String tempTableName, String[] columInfoStr) {
		StringBuffer sb = new StringBuffer();
		sb.append("create temporary table " + tempTableName + " (");
		for(String columnInfo : columInfoStr){
			sb.append(columnInfo + ",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getDropTempTableSql(String tempTableName) {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP TEMPORARY TABLE IF EXISTS " + tempTableName);
		return sb.toString();
	}

	@Override
	public List<String> getCreateTableSql(TableDefVO tableDefVO) {
		ObjectUtil.validNotNull(tableDefVO, "tableDefVO is required.");
		StringBuffer sb = new StringBuffer();
		String tableName = tableDefVO.getTable_code();
		List<TableColumnVO> columnVOList = tableDefVO.getColumnList();
		ObjectUtil.validNotNull(columnVOList, "table[" + tableName + "] has no any columns.");
		sb.append("CREATE TABLE " + tableName.toLowerCase() + "(\n");
		for(TableColumnVO columnVO : columnVOList){
			String colCode = columnVO.getColcode();
			String defaultValue = columnVO.getColdefault();
			//String comment = ObjectUtil.isNotNull(columnVO.getColdesc())?columnVO.getColdesc() :columnVO.getColname();
			String comment = null;
			if(StringUtils.isNotEmpty(columnVO.getColname())){
				if(StringUtils.isNotEmpty(columnVO.getColdesc())){
					//判断如果注释和名称相同时，不拼写名称注释串。拼写格式为:[名称]注释。
					if(!columnVO.getColname().equals(columnVO.getColdesc())){
						comment = "["+columnVO.getColname()+"]"+columnVO.getColdesc();
					}else{
						comment = columnVO.getColdesc();
					}				
				}else{
					comment = columnVO.getColname();
				}
			}
			sb.append(" \t" + colCode.toLowerCase() + " " + this.getDDLDataTypeAndLength(columnVO));
			if(columnVO.getIsnotnull()==SyConstant.SY_TRUE){
				sb.append(" NOT NULL");
			}
			if(ObjectUtil.isNotNull(defaultValue))
				sb.append(" DEFAULT '" + defaultValue + "'");
			if(ObjectUtil.isNotNull(comment))
				sb.append(" COMMENT '" + comment + "'");
			sb.append(",\n");
		}
		String pkColcode = tableDefVO.getPkColumnVO() != null? tableDefVO.getPkColumnVO().getColcode() : null;
		if(ObjectUtil.isNotNull(pkColcode))
			sb.append(" \tPRIMARY KEY (" + pkColcode.toLowerCase() + ")");
		else
			sb.deleteCharAt(sb.length()-2);
		//删除后面设置，数据库执行时，druid检查报错，建库时可以指定数据库的设置
		//sb.append(" \n) \nENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin; ");
		sb.append(" \n); ");
		//封装sql，返回集合对象
		List<String> sqls = new ArrayList<String>();
		sqls.add(sb.toString());
		return sqls;
	}

	@Override
	public String getDropTableSql(String tableNme) {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP TABLE IF EXISTS " + tableNme);
		return sb.toString();
	}

	@Override
	public String createTableDefFromDb(String tableName) {
		StringBuilder sqlTableDef = new StringBuilder();
		// TODO 查询TABLE_DEF是否注册表信息
		
		// TODO 根据表名查询表DLL信息
		TableColumnVO tbColVo = new TableColumnVO();
		
		// TODO 按表定义规则生成sql语句
		
		// TODO 返回sql语句
		return sqlTableDef.toString();
		
	}

	@Override
	public List<String> getAddColByTableColVO(String table,
			List<TableColumnVO> tableColumnVOList) {
		String alterPrefix = "ALTER TABLE "+table.toLowerCase();
		List<String> sqlList = new ArrayList<String>();
		for(TableColumnVO tableColumnVO:tableColumnVOList){
			sqlList.add(generateAlterSql(alterPrefix,tableColumnVO,true));
		}
		return sqlList;
	}

	@Override
	public List<String> getUpdateColByTableColVO(String table,
			List<TableColumnVO> tableColumnVOList) {
		String alterPrefix = "ALTER TABLE "+table.toLowerCase();
		List<String> sqlList = new ArrayList<String>();
		for(TableColumnVO tableColumnVO:tableColumnVOList){
			sqlList.add(generateAlterSql(alterPrefix,tableColumnVO,false));
		}
		return sqlList;
	}
	private String generateAlterSql(String alterPrefix,TableColumnVO tableColumnVO,boolean isAdd){
		StringBuilder sql=new StringBuilder(alterPrefix);
		if(isAdd)
			sql.append(" ADD COLUMN ");
		else
			sql.append(" MODIFY COLUMN ");
		//获取pdm中列类型定义
		String colType = tableColumnVO.getColtype();
		Integer colLen = tableColumnVO.getCollen();
		Integer colScale = tableColumnVO.getColscale();		
		String pdmColType = colType;		
		if(colLen!=null && colLen.intValue()!=0){
			pdmColType=pdmColType+"("+colLen;
			if(colScale!=null && colScale.intValue()!=0){
				pdmColType=pdmColType+","+colScale;
			}
			pdmColType=pdmColType+")";
		}
		sql.append(tableColumnVO.getColcode().toLowerCase()).append(" ").append(pdmColType);
		//unsigned
		//zerofill
		//默认值
		String coldefault = tableColumnVO.getColdefault();
		if(!StringUtils.isEmpty(coldefault)){
			sql.append(" ").append("DEFAULT ");
			if(colType.toLowerCase().startsWith("char")||colType.toLowerCase().startsWith("varchar"))
				sql.append("'").append(coldefault).append("'");
			else
				sql.append(coldefault);				
		}
		//非空属性
		if(tableColumnVO.getIsnotnull()==SyConstant.SY_TRUE)
			sql.append(" ").append("NOT NULL");
		else
			sql.append(" ").append("NULL");
		//自增	
		if(tableColumnVO.getIsautoinc()==SyConstant.SY_TRUE){
			sql.append(" ").append("AUTO_INCREMENT");
		}
		//注释
		if(StringUtils.isNotEmpty(tableColumnVO.getColname())){
			if(StringUtils.isNotEmpty(tableColumnVO.getColdesc())){
				//判断如果注释和名称相同时，不拼写名称注释串。拼写格式为:[名称]注释。
				if(!tableColumnVO.getColname().equals(tableColumnVO.getColdesc())){
					sql.append(" ").append("COMMENT '[").append(tableColumnVO.getColname()).append("]").append(tableColumnVO.getColdesc()).append("'");
				}else{
					sql.append(" ").append("COMMENT '").append(tableColumnVO.getColdesc()).append("'");
				}				
			}else{
				sql.append(" ").append("COMMENT '").append(tableColumnVO.getColname()).append("'");
			}
		}
		
		//位置
		
		return sql.toString();
	}

	@Override
	public List<String> getUpdatePkByTableColVO(String table,
			List<TableColumnVO> tableColumnVOList) {
		StringBuilder pkColSql = new StringBuilder(); 
		for(TableColumnVO tableColumnVO:tableColumnVOList){
			pkColSql.append(tableColumnVO.getColcode()).append(",");
		}
		String alterPrefix = "ALTER TABLE "+table.toLowerCase();
		StringBuilder sqlStr = new StringBuilder();
		sqlStr.append(alterPrefix).append(" DROP PRIMARY KEY,ADD PRIMARY KEY (").
			append(pkColSql.toString().substring(0, pkColSql.toString().length()-1)).
			append(")");
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(sqlStr.toString());
		return sqlList;
	}

	@Override
	public String getColSqlByTable(String table) {
		return "SHOW FULL COLUMNS FROM "+table;
	}

	@Override
	public String getSupportedWhereSql(String whereSql) {
		return whereSql;
	}

	@Override
	public String getChkTableSql(String table) {
		return "show TABLES like '"+table+"'";
	}

	@Override
	public String getOrderBySqlForZh_cn(String column, String order) {
		return "CONVERT("+column+" USING gbk) "+ order;
	}
}
