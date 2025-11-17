package com.hayden.hap.dbop.db.orm.sql;

import com.hayden.hap.dbop.itf.IBaseService;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.dbop.db.util.DBType;
import com.hayden.hap.dbop.db.util.IOracleDbTypeConstants;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.dbop.db.util.TableDefDataType;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import oracle.sql.CLOB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @ClassName: OracleSqlBuilder 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月23日 上午10:50:22 
 * @version V1.0   
 *  
 */
public class OracleSqlBuilder extends AbstractSqlBuilder implements SqlBuilder {
	private static final Logger logger = LoggerFactory.getLogger(OracleSqlBuilder.class);
	@Override
	public String getSelectLimitSql(String sql,Map<String,String> paramMap, int offset, int limit) {
		sql = sql.trim();
		boolean isForUpdate = false;
		if ( sql.toLowerCase().endsWith(" for update") ) {
			sql = sql.substring( 0, sql.length()-11 );
			isForUpdate = true;
		}
		if(offset<0){
		    offset=0;
		}
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");

		pagingSelect.append(sql);
		
		pagingSelect.append(" ) row_  where rownum <= "+(offset + limit)+") where  rownum_ > "+(offset)+"");

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
				return "to_date('" + ObjectUtil.getDateFormatString((Date) paramValue) + "','yyyy-MM-dd HH24:mi:ss')";
			else if(jdbcType.getTypeCode() == JdbcType.DATE.getTypeCode())
				return "to_date('" + ObjectUtil.getDateOnlyFormatString((Date) paramValue) + "','yyyy-MM-dd')";
			else if(jdbcType.getTypeCode() == JdbcType.TIME.getTypeCode())
				return "to_date('" + ObjectUtil.getTimeOnlyFormatString((Date) paramValue) + "','HH24:mi:ss')";
			return "";
		}
		return "";
	}

	@Override
	public String getDialectGroupConcatSql(String sql) {
		if(sql==null || "".equals(sql))
			return sql;
        sql = sql.replaceAll("group_concat\\(", "WMSYS.WM_CONCAT(");
		return sql;
	}

	@Override
	public String getDialectStrToDateSql(String sql) {
		if(sql==null || "".equals(sql))
			return sql;
		String regex="(STR_TO_DATE\\([0-9a-zA-Z_.,\\?\\-'%\\s:]+\\))";  
        Pattern pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher=pattern.matcher(sql);
        String newSql = new String(sql);
        while(matcher.find()){
            String strToDateString = sql.substring(matcher.start(), matcher.end());
            String argsString = strToDateString.substring(strToDateString.indexOf("str_to_date(")+"str_to_date(".length(), strToDateString.indexOf(")")).trim();
            String[] argsStrArray = argsString.split(",");
            String dateFormateString = "";
            if("'%Y-%m-%d'".equalsIgnoreCase(argsStrArray[1]))
            	dateFormateString = "yyyy-mm-dd";
            if("'%Y-%m-%d %H:%i:%s'".equalsIgnoreCase(argsStrArray[1]))
            	dateFormateString = "yyyy-MM-dd HH24:mi:ss";
            String decodeString = "TO_DATE("+argsStrArray[0]+",'"+dateFormateString+"')";
            newSql = newSql.replace(strToDateString, decodeString);
        }
        return newSql;
	}

	@Override
	public String getCreateTempTableSql(String tempTableName, String[] columInfoStr) {
		StringBuffer sb = new StringBuffer();
		sb.append("create global temporary table " + tempTableName + " (");
		for(String columnInfo : columInfoStr){
			sb.append(columnInfo + ",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(") ON COMMIT PRESERVE ROWS");
		return sb.toString();
	}

	@Override
	public String getDropTempTableSql(String tempTableName) {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP GLOBAL TEMPORARY TABLE " + tempTableName);
		return sb.toString();
	}

	public String getDDLDataTypeAndLength(TableColumnVO columnVO){
		String colDataType = columnVO.getColtype();
		Integer length = columnVO.getCollen();
		String dataType = getDataTypeOfDbType(DBType.ORACLE.getCode(), colDataType);
		if(colDataType.equalsIgnoreCase(TableDefDataType.DOUBLE.getTypeName())||
				colDataType.equalsIgnoreCase(TableDefDataType.DECIMAL.getTypeName()))
			return dataType + "(" + length + ","+columnVO.getColscale()+")";
		if(dataType.equalsIgnoreCase("DATETIME") 
				|| dataType.equalsIgnoreCase("DATE")
				||dataType.equalsIgnoreCase(IOracleDbTypeConstants.CLOB) )
			return dataType ;
		else if(dataType.equalsIgnoreCase("VARCHAR") 
				|| dataType.equalsIgnoreCase("VARCHAR2"))
			return dataType + "(" + length + " CHAR)";
		return dataType + "(" + length + ")";
	}
	@Override
	public List<String> getCreateTableSql(TableDefVO tableDefVO) {
		//封装返回sql集合
		List<String> createTableSqlList = new ArrayList<String>();
		//注释语句
		List<String> commentSqlList = new ArrayList<String>();
		ObjectUtil.validNotNull(tableDefVO, "tableDefVO is required.");
		StringBuffer sb = new StringBuffer();
		String tableName = tableDefVO.getTable_code().toLowerCase();
		List<TableColumnVO> columnVOList = tableDefVO.getColumnList();
		ObjectUtil.validNotNull(columnVOList, "table[" + tableName + "] has no any columns.");
		sb.append("CREATE TABLE " + tableName + "\n(");
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
			sb.append(" \n\t" + colCode.toLowerCase() + " " + this.getDDLDataTypeAndLength(columnVO));
			if(ObjectUtil.isNotNull(defaultValue))
				sb.append(" DEFAULT '" + defaultValue + "'");
			if(columnVO.getIsnotnull()==SyConstant.SY_TRUE){
				sb.append(" NOT NULL");
			}
			sb.append(",");
			if(ObjectUtil.isNotNull(comment)){
				commentSqlList.add(" COMMENT ON COLUMN " + tableName + "." + colCode + " IS '" + comment + "'");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("\n)");
		createTableSqlList.add(sb.toString());
		for(String commentSql:commentSqlList){
			createTableSqlList.add(commentSql);
		}
		String pkColCode = tableDefVO.getPkColumnVO() != null? tableDefVO.getPkColumnVO().getColcode() : null;
		if(ObjectUtil.isNotNull(pkColCode)){
			String constraintName = getPKConstraintName(tableName, pkColCode).toUpperCase();
			createTableSqlList.add("ALTER TABLE " + tableName + " ADD CONSTRAINT " + constraintName + " PRIMARY KEY (" + pkColCode.toLowerCase() + ")");
		}
		return createTableSqlList;
	}
	/**
	 * 
	 * @Title: getPKConstraintName 
	 * @Description: 返回主键字段索引名称
	 * @param tableName 表名
	 * @param pkColName 主键字段名
	 * @return
	 * @return String 主键字段索引名称
	 * @throws
	 */
	private String getPKConstraintName(String tableName, String pkColName){
		int colLen = pkColName.length();
		if(colLen>=26)
			return "PK_"+pkColName.substring(0,colLen>27?27:colLen);
		else{
			String temp = tableName.substring(tableName.indexOf("_")+1);
			if(temp.length()>26-colLen)
				return "PK_"+temp.substring(0,26-colLen)+"_"+pkColName;
			return "PK_"+temp+"_"+pkColName;
		}
	}

	@Override
	public String getDropTableSql(String tableNme) {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP TABLE " + tableNme);
		return sb.toString();
	}

	@Override
	public String createTableDefFromDb(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 将oracle的clob数据转换为string
	 * @param table_name 表名称
	 * @param table_pk 表主键字段
	 * @param clob_field 表clob字段，多个时封装list传递
	 * @param data 表相应行的主键值
	 * @return
	 */
	public Map<String,String> getClob2String(final String table_name,String table_pk,List<String> clob_fields,final Long data){
		if(!ObjectUtil.isNotNull(table_name) ||  
				!ObjectUtil.isNotEmpty(clob_fields) || !ObjectUtil.isNotNull(data))
			return null;
		//读取内容：
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		//查询clob字段
		final String clob_field=clob_fields.toString().substring(1, clob_fields.toString().length()-1);
		//使用主键获取数据，data值转换为长整型
		StringBuilder sql= new StringBuilder("select ").append(clob_field).append(" from ").append(table_name).
				append(" where ").append(table_pk).append(" = ").append(data).append(" for update");
		List<Map<String,String>> clobStringMap = baseService.executeQuery(sql.toString(), null, null, new ResultSetExtractor<Map<String,String>>() {

			@Override
			public Map<String,String> extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				ResultSetMetaData rsmd = rs.getMetaData();
				while (rs.next()) {
					Map<String,String> valueMap = new HashMap<String,String>();
					for(int i=0;i<rsmd.getColumnCount();i++){
						oracle.sql.CLOB clob = null;
						Reader is = null;
						BufferedReader br = null;
						try {
							//使用druid做数据库连接池时，需要转换处理
							if(rs.getClob(i+1) instanceof com.alibaba.druid.proxy.jdbc.ClobProxyImpl){
						          com.alibaba.druid.proxy.jdbc.ClobProxyImpl impl = (com.alibaba.druid.proxy.jdbc.ClobProxyImpl)rs.getClob(i+1);
						          clob = (CLOB) impl.getRawClob(); // 获取原生的这个 Clob
						    }else
						    	clob = (CLOB) rs.getClob(i+1);
							StringBuilder content = new StringBuilder();
							if(clob != null && clob.length()!=0){
							    is = clob.getCharacterStream();
							    br = new BufferedReader(is);
							    String str = br.readLine();
							    //是否添加换行符
							    boolean isAddChangeLine=false;
							    while(str != null){
							    	if(isAddChangeLine)
							    		content.append("\n");
							    	content.append(str);
							        str = br.readLine();
							        if(str != null)
							        	isAddChangeLine=true;
							    }
							    if(content.length()!=0)
							    	valueMap.put(rsmd.getColumnName(i+1), content.toString());
							}
						} catch (Exception e) {
							logger.error("oracle表[{}]字段[{}]，主键数据[{}]由clob转换string时发生错误！",table_name,clob_field,data);
						} finally{
							if(br!=null)
								try { br.close(); } catch (IOException e) {}
							if(is!=null)
								try { is.close(); } catch (IOException e) {}
							if(clob!=null)
								try { clob.close(); } catch (SQLException e) {}
						}
					}
					return valueMap;
				}
				return null;
			}
			
		}, null);
		if(clobStringMap==null)
			return null;
		return clobStringMap.get(0);
	}

	@Override
	public List<String> getAddColByTableColVO(String table,
			List<TableColumnVO> tableColumnVOList) {
		String alterPrefix = "ALTER TABLE "+table.toLowerCase();
		List<String> sqlList = new ArrayList<String>();
		for(TableColumnVO tableColumnVO:tableColumnVOList){
			sqlList.add(generateAlterSql(alterPrefix,tableColumnVO,true));
			//注释
			String commentSql = generateCommentSql(tableColumnVO);
			if(StringUtils.isNotEmpty(commentSql)){
				sqlList.add(commentSql);
			}
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
			//注释
			String commentSql = generateCommentSql(tableColumnVO);
			if(StringUtils.isNotEmpty(commentSql)){
				sqlList.add(commentSql);
			}
		}
		return sqlList;
	}
	private String generateAlterSql(String alterPrefix,TableColumnVO tableColumnVO,boolean isAdd){
		StringBuilder sql=new StringBuilder(alterPrefix);
		if(isAdd)
			sql.append(" ADD ");
		else
			sql.append(" MODIFY ");
		//获取表定义中列类型定义
		String colType = tableColumnVO.getOra_coltype();
		Integer colLen = tableColumnVO.getCollen();
		Integer colScale = tableColumnVO.getColscale();		
		String pdmColType = colType;		
		if(colLen!=null && colLen.intValue()!=0){
			pdmColType=pdmColType+"("+colLen;
			if(colScale!=null && colScale.intValue()!=0){
				pdmColType=pdmColType+","+colScale;
			}else{
				//这里判断下是否是字符串类型
				if(colType.equalsIgnoreCase("VARCHAR2") 
						|| colType.equalsIgnoreCase("CHAR"))
					pdmColType = pdmColType + " CHAR";
			}
			pdmColType=pdmColType+")";
		}
		sql.append(tableColumnVO.getColcode().toLowerCase()).append(" ").append(pdmColType);
		
		//默认值
		String coldefault = tableColumnVO.getColdefault();
		//新增时当有默认值时，才需要加上default。
		//修改时默认添加，有值时赋值，没有值时设置为null
		if(isAdd){
			if(!StringUtils.isEmpty(coldefault)){
				sql.append(" ").append("DEFAULT ");
				if(colType.toLowerCase().startsWith("char")||colType.toLowerCase().startsWith("varchar"))
					sql.append("'").append(coldefault).append("'");
				else
					sql.append(coldefault);				
			}
		}else{
			sql.append(" ").append("DEFAULT ");
			if(!StringUtils.isEmpty(coldefault)){
				if(colType.toLowerCase().startsWith("char")||colType.toLowerCase().startsWith("varchar"))
					sql.append("'").append(coldefault).append("'");
				else
					sql.append(coldefault);				
			}else{
				sql.append("NULL");
			}
		}
		
		//非空属性
		//oracle生成sql时，如果前后修改值没有变化，不能拼装sql。
		//整型比较，判断是否进行了非空值的修改。
		if(tableColumnVO.getIsnotnull()!=null){
			if(isAdd){
				if(tableColumnVO.getIsnotnull()==SyConstant.SY_TRUE)
					sql.append(" ").append("NOT NULL");
			}else{
				if(tableColumnVO.get("original_isnotnull")!=null && tableColumnVO.getIsnotnull().intValue()!=
						((Integer)tableColumnVO.get("original_isnotnull")).intValue()){
					if(tableColumnVO.getIsnotnull()==SyConstant.SY_TRUE)
						sql.append(" ").append("NOT NULL");
					else
						sql.append(" ").append("NULL");
				}
			}			
		}
		
		return sql.toString();
	}
	private String generateCommentSql(TableColumnVO tableColumnVO){
		StringBuilder sql=new StringBuilder();
		//注释
		String comment = null;
		if(StringUtils.isNotEmpty(tableColumnVO.getColname())){
			if(StringUtils.isNotEmpty(tableColumnVO.getColdesc())){
				//判断如果注释和名称相同时，不拼写名称注释串。拼写格式为:[名称]注释。
				if(!tableColumnVO.getColname().equals(tableColumnVO.getColdesc())){
					comment = "["+tableColumnVO.getColname()+"]"+tableColumnVO.getColdesc();
				}else{
					comment = tableColumnVO.getColdesc();
				}				
			}else{
				comment = tableColumnVO.getColname();
			}
		}
		if(StringUtils.isNotEmpty(comment)){
			sql.append("COMMENT ON COLUMN ").append(tableColumnVO.getTable_code().toLowerCase()).append(".").
				append(tableColumnVO.getColcode()).append(" IS '").append(comment).append("'");
		}
		return sql.toString();
	}

	@Override
	public List<String> getUpdatePkByTableColVO(String table,
			List<TableColumnVO> tableColumnVOList) {
		StringBuilder pkConstraintName = new StringBuilder("pk_").append(table); 
		StringBuilder pkColSql = new StringBuilder(); 
		for(TableColumnVO tableColumnVO:tableColumnVOList){
			pkColSql.append(tableColumnVO.getColcode()).append(",");
			pkConstraintName.append("_").append(tableColumnVO.getColcode());
		}
		String alterPrefix = "ALTER TABLE "+table.toLowerCase();
		//删除索引
		StringBuilder dropSqlStr = new StringBuilder();
		dropSqlStr.append(alterPrefix).append(" DROP constraint ").
			append(pkConstraintName).append(" cascade;");
		//添加索引
		StringBuilder addSqlStr = new StringBuilder();
		addSqlStr.append(alterPrefix).append(" ADD constraint ").
			append(pkConstraintName).append(" primary key (").
			append(pkColSql.toString().substring(0, pkColSql.toString().length()-1)).append(")");
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(dropSqlStr.toString());
		sqlList.add(addSqlStr.toString());
		return sqlList;
	}

	@Override
	public String getColSqlByTable(String table) {
		StringBuilder sql = new StringBuilder();
		sql.append("select a.COLUMN_NAME Field,").
			append("case when a.DATA_TYPE='DATE' then a.DATA_TYPE ").
			append("when instr(a.DATA_TYPE, 'TIMESTAMP') > 0  then 'TIMESTAMP' ").
			append("when a.DATA_PRECISION is null then a.DATA_TYPE||'('||a.DATA_LENGTH||')' ").
			append("when a.DATA_SCALE is null or a.DATA_SCALE=0 then a.DATA_TYPE||'('||a.DATA_PRECISION||')' ").
			append("else a.DATA_TYPE||'('||a.DATA_PRECISION||','||a.DATA_SCALE||')' end Type ").
			append("from USER_tab_columns a where a.table_name = '").append(table).append("' or a.table_name = '").
			append(table.toUpperCase()).append("'");
		return sql.toString();
	}

	@Override
	public String getSupportedWhereSql(String whereSql) {
		//目前支持cast函数的使用
		Pattern pattern = Pattern.compile("CAST\\(gennext AS ([a-zA-Z0-9(),]+)\\)",Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(whereSql);
        if (matcher.find()) {
        	String dataType = matcher.group(1);
        	if(dataType!=null){
        		if(dataType.equalsIgnoreCase("SIGNED"))
        			whereSql = matcher.replaceAll("CAST\\(gennext AS NUMBER\\)");
        	}        	
        }
		return whereSql;
	}

	@Override
	public String getChkTableSql(String table) {
		return "select count(*) from user_tables where table_name = '"+table+"' or table_name='"+table.toUpperCase()+"'";
	}

	@Override
	public String getOrderBySqlForZh_cn(String column, String order) {
		return "nlssort("+column+",'NLS_SORT=SCHINESE_PINYIN_M') " + order;
	}
	
	/** 
	 * 主要是处理colName为日期、paramValue为字符串值时，需要将字符串转换为日期。使用to_date函数
	 * @see AbstractSqlBuilder#getColValueSqlString(TableDefVO, java.lang.String, java.lang.Object)
	 * @author wangyi
	 * @date 2017年7月6日
	 */
	public String getColValueSqlString(TableDefVO tableDefVO, String colName, Object paramValue){
		//判断paramValue值不为空，且为字符串类型变量
		if(ObjectUtil.isNotNull(paramValue)&&
				paramValue instanceof String){
			//获取列定义类型
			TableColumnVO columnVO = getColumnVO(tableDefVO, colName);
			if(columnVO!=null){
				//依据列定义类型，生成to_date格式字符串
				String colType = columnVO.getColtype();
				if(!StringUtils.isEmpty(colType)){
					//转换格式
					String format = null;
					if(colType.equals(JdbcType.DATETIME.getTypeName())){
						format = "yyyy-MM-dd HH24:mi:ss";
					}else if(colType.equals(JdbcType.DATE.getTypeName())){
						format = "yyyy-MM-dd";
					}
					//不支持time，改为使用char
					/*else if(colType.equals(JdbcType.TIME.getTypeCode())){
						format = "HH24:mi:ss";
					}*/
					//不属于上面格式时，不支持转换
					if(!StringUtils.isEmpty(format))
						return "to_date('" + paramValue + "','"+ format +"')";
				}
			}
			//oracle数据库，对单引号进行转义
			return "'" + String.valueOf(paramValue).replace("'", "''") + "'";
		}
		return super.getColValueSqlString(tableDefVO, colName, paramValue);
	}
	
}
