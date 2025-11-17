package com.hayden.hap.dbop.db.orm.sql;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.dbop.db.tableDef.entity.TableColumnVoConstants;
import com.hayden.hap.dbop.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.*;
import com.hayden.hap.common.utils.DbEnvUtils;
import com.hayden.hap.common.utils.ModuleDataSrcUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.properties.MycatPropertiesUtil;
import com.hayden.hap.dbop.db.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @ClassName: AbstractSqlBuilder 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月27日 下午4:58:09 
 * @version V1.0   
 *  
 */
public abstract class AbstractSqlBuilder implements SqlBuilder {
	/**
	 * 
	 * @Title: getSqlColumns 
	 * @Description: 获取sql字段列表
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取查询、新增、修改等字段列表
	 * @param actionCode 
	 *				select： 							 租户、时间戳
	 *				update:不更新主键列；		update*、租户、时间戳、平台字段处理；
	 *				insert：create*字段处理；	update*、租户、时间戳、平台字段处理；
	 * @return
	 * @return String sql字段列表
	 * @throws
	 */
	private String getSqlColumns(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, Integer actionCode){
		StringBuffer sb = new StringBuffer();
		
		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		
		//引入联合主键列
		List<String> pkColNameList=getPkColNameList(tableDefVO);
		
		if(dynaSqlVO != null && ObjectUtil.isNotEmpty(dynaSqlVO.getSqlColumnList())){
			//支持引入不更新列，过滤完成其他字段的更新
			boolean useSqlColumnList = dynaSqlVO.getUseSqlColumnList();
			
			if(useSqlColumnList){
				//特殊处理，支持update更新时，如果配置了不更新平台字段，则set中不用进行添加更新列。
				if(actionCode==SyConstant.DATA_STATS_UPDATED&&dynaSqlVO.getUpdateAssisFields()){
					//平台字段处理
					getOtherCols(getCols(actionCode), tableDefVO, dynaSqlVO);
				}				
				for(String column : dynaSqlVO.getSqlColumnList()){
					//pkColNameList.contains(column.toLowerCase()) 联合主键可以进行部分字段更新
					if(ObjectUtil.isNotNull(pkColName) && isUpdate(actionCode) && (pkColNameList.size()==1 && pkColName.equalsIgnoreCase(column)))
						continue ;
					//判断属性是否注册
					if(isHaveCol(tableDefVO, column))
					{
						sb.append(" " + column.toLowerCase() + ",");
					}
				}
			}else{				
				//定义list，实现contains时忽略大小写
				List<String> sqlColumnList=new ArrayList<String>();				
				for(String value:dynaSqlVO.getSqlColumnList()){
					sqlColumnList.add(value.trim().toLowerCase());
				}
				
				if(tableDefVO != null && ObjectUtil.isNotEmpty(tableDefVO.getColumnList())){
					for(TableColumnVO tableColumnVO : tableDefVO.getColumnList()){
						if((ObjectUtil.isNotNull(pkColName) && isUpdate(actionCode) && (pkColNameList.size()==1 && pkColName.equalsIgnoreCase(tableColumnVO.getColcode()))) || 
								sqlColumnList.contains(tableColumnVO.getColcode().toLowerCase()))//判断如果包含列则需要过滤列，不进行后续更新
							continue ;
						sb.append(" " + tableColumnVO.getColcode().toLowerCase() + ",");
					}
				}
			}
			
		}else if(tableDefVO != null && ObjectUtil.isNotEmpty(tableDefVO.getColumnList())){
			for(TableColumnVO tableColumnVO : tableDefVO.getColumnList()){
				if(ObjectUtil.isNotNull(pkColName) && isUpdate(actionCode) && (pkColNameList.size()==1 && pkColName.equalsIgnoreCase(tableColumnVO.getColcode())))
					continue ;
				sb.append(" " + tableColumnVO.getColcode().toLowerCase() + ",");
			}
		}
	
		
		
		String sqlColumns = sb.toString();
		//引入sqlColumns非空校验，不为空时进行截取
		if(sqlColumns.length()!=0)
			sqlColumns = sqlColumns.substring(0, sqlColumns.length()-1);
		
		return sqlColumns;
	}
	
	private void getOtherCols(String[] hapCols, TableDefVO tableDefVO,DynaSqlVO dynaSqlVO){
		for (String col : hapCols) {
			boolean isHave = false;
			for(String column : dynaSqlVO.getSqlColumnList()){
				if(col.equalsIgnoreCase(column))
				{
					isHave = true;
					break;
				}
			}
			if(!isHave)
			{
				if(isHaveCol(tableDefVO, col))
				{
					dynaSqlVO.getSqlColumnList().add(col);
				}
			}
		}
	}
	
	private String[] getCols(Integer actionCode)
	{
		String[] insertCols = new String[]{"created_dt","created_by","created_by_name",
											"updated_dt","updated_by","updated_by_name",
											"tenantid","ts","innercode"}; 
		String[] updateCols = new String[]{	"updated_dt","updated_by","updated_by_name","ts"}; 
		String[] selectCols = new String[]{"ts"};
		if(isInsert(actionCode))
		{
			return insertCols;
		}else if(isUpdate(actionCode))
		{
			return updateCols;
		}else if(isSelect(actionCode))
		{
			return selectCols;
		}
		return null;
	}
	
	private Boolean isInsert(Integer actionCode)
	{
		if(actionCode==SyConstant.DATA_STATS_ADDED)
		{
			return true;
		}else
			return false;
	}
	private Boolean isSelect(Integer actionCode)
	{
		if(actionCode==SyConstant.DATA_STATS_DEFAULT)
		{
			return true;
		}else
			return false;
	}
	private Boolean isUpdate(Integer actionCode)
	{
		if(actionCode==SyConstant.DATA_STATS_UPDATED)
		{
			return true;
		}else
			return false;
	}
	
	/**
	 * isHaveCol:(表定义是否含有此列). <br/>
	 * date: 2016年1月18日 <br/>
	 *
	 * @author ZhangJie
	 * @param tableDefVO
	 * @param column
	 * @return
	 */
	private Boolean isHaveCol(TableDefVO tableDefVO,String column)
	{
		boolean isHave = false;
		List<TableColumnVO> columns = tableDefVO.getColumnList();
		for(TableColumnVO tableColVo:columns)
		{
			String colName = tableColVo.getColcode();
			if(colName.equalsIgnoreCase(column))
			{
				isHave = true;
				return isHave;
			}
		}
		return isHave;
	}
	/**
	 * 
	 * @Title: getSqlWhere 
	 * @Description: 用于从DynaSqlVO获取where语句
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取where条件参数、where子句用于拼接sql的where语句以及是否开启预编译
	 * @return
	 * @return DynaSqlResultVO  动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getSqlWhere(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO,AbstractVO vo){
		return getSqlWhere(tableDefVO, dynaSqlVO, vo, null);
	}
	/**
	 * 
	 * @Title: getSqlWhere 
	 * @Description: 用于从DynaSqlVO获取where语句
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取where条件参数、where子句用于拼接sql的where语句以及是否开启预编译
	 * @param sqlType 用于区分增删改查，这里当为查询时，特殊处理下临时表的逻辑优化
	 * @return
	 * @return DynaSqlResultVO  动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getSqlWhere(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO,AbstractVO vo,Integer sqlType){
		if(dynaSqlVO == null)
			return new DynaSqlResultVO();
		//增加平台字段租户id的条件过滤
		String tenantidWhere = "";
		if(vo!=null&&vo.getLong(TableColumnVoConstants.TENANTID)!=null
				&&!SyConstant.NONTENANTID_TABLE.containsKey(vo.getTableName().toLowerCase()))
		{
			tenantidWhere = " and "+TableColumnVoConstants.TENANTID+" =" + vo.getLong(TableColumnVoConstants.TENANTID)+" ";
		}
		Map<String, Object> whereParamMap = dynaSqlVO.getWhereParamMap();
		String sqlWhere = "";
		StringBuffer sb = new StringBuffer();
		
		if(dynaSqlVO.getWhereByKey() ){
			ObjectUtil.validNotNull(tableDefVO.getPkColumnVO(), "sqlBuilder.getSqlWhere: [" + tableDefVO.getTable_code() + "] no pk column.");
			String pkColName = tableDefVO.getPkColumnVO().getColcode();
			Object pkValue = whereParamMap.get(pkColName);
			DynaSqlResultVO paramDynaSqlResultVO = null;
			if(dynaSqlVO.getUsePreStatement()){
				paramDynaSqlResultVO = this.getPrepareSqlWhereByParam(tableDefVO, dynaSqlVO, pkColName, pkValue);
			}else {
				paramDynaSqlResultVO = this.getNonPrepareSqlWhereByParam(tableDefVO, dynaSqlVO, pkColName, pkValue);
			}
			if(ObjectUtil.isNotNull(paramDynaSqlResultVO.getSql()))
				sb.append(" where " + paramDynaSqlResultVO.getSql() + " ").append(tenantidWhere);
			
			//存在联合主键时处理逻辑
			List<String> pkColNameList=getPkColNameList(tableDefVO);
			for(String tmpPkColName:pkColNameList){
				//过滤当前主键
				if(tmpPkColName.equals(pkColName))
					continue;
				//获取返回结果vo
				DynaSqlResultVO tmpParamDynaSqlResultVO=getParamDynaSqlResultVO(tableDefVO, dynaSqlVO, tmpPkColName, whereParamMap.get(tmpPkColName));
				//如果sql存在添加
				if(ObjectUtil.isNotNull(tmpParamDynaSqlResultVO.getSql()))
					sb.append(" and " + tmpParamDynaSqlResultVO.getSql());
				//判断预处理参数，如果存在需要加入
				if(tmpParamDynaSqlResultVO.getPreStatementParams()!=null){
					if(paramDynaSqlResultVO.getPreStatementParams()==null){
						paramDynaSqlResultVO.setPreStatementParams(tmpParamDynaSqlResultVO.getPreStatementParams());
					}else{
						paramDynaSqlResultVO.getPreStatementParams().addAll(tmpParamDynaSqlResultVO.getPreStatementParams());
					}
				}				
				if(tmpParamDynaSqlResultVO.getPreStatementParamJdbcTypes()!=null){
					if(paramDynaSqlResultVO.getPreStatementParamJdbcTypes()==null){
						paramDynaSqlResultVO.setPreStatementParamJdbcTypes(tmpParamDynaSqlResultVO.getPreStatementParamJdbcTypes());
					}else{
						paramDynaSqlResultVO.getPreStatementParamJdbcTypes().addAll(tmpParamDynaSqlResultVO.getPreStatementParamJdbcTypes());
					}
				}
			}
			
			paramDynaSqlResultVO.setSql(sb.toString());
			return paramDynaSqlResultVO;
		}
		
		List<Object> preStatementParamList = new ArrayList<Object>();
		List<Integer> preStatementParamJdbcTypeList = new ArrayList<Integer>();
		DynaSqlResultVO dynaSqlResultVO = new DynaSqlResultVO(sqlWhere, dynaSqlVO.getUsePreStatement(), preStatementParamList, preStatementParamJdbcTypeList);
		//sb.append("1=1");
		
		if(ObjectUtil.isNotNull(dynaSqlVO.getWhereClause()) ){
			String whereClause = dynaSqlVO.getWhereClause();
			whereClause = getSupportedWhereSql(whereClause);
			if(ObjectUtil.isNotNull(sb.toString().trim()))
				sb.append(" and (" + whereClause + ")");
			else
				sb.append("(").append(whereClause).append(")");
		}
		
		if(ObjectUtil.isNotEmpty(whereParamMap)){
			Iterator<String> iterator = whereParamMap.keySet().iterator();
			while(iterator.hasNext()){
				String colName = iterator.next();
				Object paramValue = whereParamMap.get(colName);
				if(SyConstant.NONTENANTID_TABLE.containsKey(tableDefVO.getTable_code())
						&&TableColumnVoConstants.TENANTID.equalsIgnoreCase(colName))
				{
					continue;
				}
				DynaSqlResultVO paramDynaSqlResultVO = null;
				if(dynaSqlVO.getUsePreStatement()){
					paramDynaSqlResultVO = this.getPrepareSqlWhereByParam(tableDefVO, dynaSqlVO, colName, paramValue);
					if(paramDynaSqlResultVO.getPreStatementParams() != null)
						preStatementParamList.addAll(paramDynaSqlResultVO.getPreStatementParams());
					if(paramDynaSqlResultVO.getPreStatementParamJdbcTypes() != null)
						preStatementParamJdbcTypeList.addAll(paramDynaSqlResultVO.getPreStatementParamJdbcTypes());
				}else {
					paramDynaSqlResultVO = this.getNonPrepareSqlWhereByParam(tableDefVO, dynaSqlVO, colName, paramValue);
				}
				
				if(ObjectUtil.isNotNull(paramDynaSqlResultVO.getSql())){					
					if(sqlType==SyConstant.DATA_STATS_DEFAULT && ObjectUtil.isNotEmpty(paramDynaSqlResultVO.getTempTableDataList()) && 
							paramDynaSqlResultVO.getTempTableDataList().get(0).size() >  DBConstants.IN_QUERY_VALUE_MAX){
						List<String> tempTableSqls = new ArrayList<String>();
						tempTableSqls.add(paramDynaSqlResultVO.getTempTableNames().get(0) +"." + colName +"=" + tableDefVO.getTable_code() +"." + colName);
						paramDynaSqlResultVO.setTempTableSqls(tempTableSqls);
					}else{
						if(ObjectUtil.isNotNull(sb.toString().trim()))
							sb.append(" and " + paramDynaSqlResultVO.getSql() + " ");
						else
							sb.append(paramDynaSqlResultVO.getSql());
					}
				}
				dynaSqlResultVO.addTempTableInfo(paramDynaSqlResultVO);
			}
		}
		
		//如果没有条件拼写1=1
		if(sb.length()==0){
			sb.append("1=1");
		}
		sqlWhere = sb.toString();
		if(!sqlWhere.toLowerCase().contains(TableColumnVoConstants.TENANTID.toLowerCase()))
		{
			sqlWhere = sqlWhere+tenantidWhere;
		}
		if(ObjectUtil.isNotNull(sqlWhere.trim()))
			sqlWhere = " where " + sqlWhere + " ";
		
		dynaSqlResultVO.setSql(sqlWhere);
		return dynaSqlResultVO;
	}
	/**
	 * 
	 * @Title: getNonPrepareSqlWhereByParam 
	 * @Description: 非预编译下把where参数拼接到where语句中
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取是否开启预编译
	 * @param colName 字段名
	 * @param paramValue 参数值
	 * @return
	 * @return String 单个参数sql String 形如 sid='123'
	 * @throws
	 */
	private DynaSqlResultVO getNonPrepareSqlWhereByParam(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String colName, Object paramValue){
		if(ObjectUtil.isNotNull(paramValue) && (paramValue instanceof Object[] || paramValue instanceof List) ){
			return this.getInSqlWhere(tableDefVO, colName, paramValue, dynaSqlVO);
		}
		StringBuffer sb = new StringBuffer();
		/*if (paramValue == null){
			sb.append( "1=0");
			return new DynaSqlResultVO(sb.toString(), dynaSqlVO.getUsePreStatement(), null, null);
		}*/			
		if(!ObjectUtil.isNotNull(paramValue)){
			sb.append( colName + " is null");
		}else
			sb.append( colName + " = " + getColValueSqlString(tableDefVO, colName, paramValue));
		return new DynaSqlResultVO(sb.toString(), dynaSqlVO.getUsePreStatement(), null, null);
	}
	/**
	 * 
	 * @Title: getPrepareSqlWhereByParam 
	 * @Description: 预编译下把where参数拼接到where语句中。
	 * 添加了参数值的校验，如果为字符串，当为空串时，拼接field is null。当为null时，返回null，拼接1=0;后面发现这样写会存在问题，
	 * 前面传值时想获取为空的数据时传入的就是null，另外写成1=0会出现注入拦截异常。
	 * 当为数组或集合时，如果大小为0，则拼接field is null，如果为null，则拼接1=0。当有多个字段都涉及数组或集合时，如果是执行sql，则需要引入union连接。
	 * @param tableDefVO 表的信息
	 * @param dynaSqlVO 用于获取是否开启预编译
	 * @param colName 字段名
	 * @param paramValue 参数值
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getPrepareSqlWhereByParam(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String colName, Object paramValue){
		List<Object> preStatementParamList = null;
		List<Integer> preStatementParamJdbcTypeList = new ArrayList<Integer>();
		StringBuffer sb = new StringBuffer();
		/*if (paramValue == null){
			sb.append( "1=0");
			return new DynaSqlResultVO(sb.toString(), dynaSqlVO.getUsePreStatement(), preStatementParamList, null);
		}*/	
		if(!ObjectUtil.isNotNull(paramValue)){
			sb.append( colName + " is null");
		} else if(paramValue instanceof Object[] || paramValue instanceof Collection<?>){
			return this.getInSqlWhere(tableDefVO, colName, paramValue, dynaSqlVO);
		} else {
			preStatementParamList = new ArrayList<Object>();
			sb.append( colName + " = ?");
			preStatementParamList.add(paramValue);
			//获取col的数据类型
			preStatementParamJdbcTypeList = new ArrayList<Integer>();
			TableColumnVO columnVO = getColumnVO(tableDefVO, colName);
			JdbcType jdbcType = null;
			if(columnVO != null)
				jdbcType = JdbcType.forName(columnVO.getColtype());
			preStatementParamJdbcTypeList.add(jdbcType == null?null:jdbcType.getTypeCode());
		}
		return new DynaSqlResultVO(sb.toString(), dynaSqlVO.getUsePreStatement(), preStatementParamList, preStatementParamJdbcTypeList);
	}
	/**
	 * 
	 * @Title: getColValueSqlString 
	 * @Description: 把参数值转为sql String
	 * @param tableDefVO 表的信息
	 * @param colName 字段名
	 * @param paramValue 参数值
	 * @return
	 * @return String 单个参数sql String 形如 '123','2015-05-12'
	 * @throws
	 */
	public String getColValueSqlString(TableDefVO tableDefVO, String colName, Object paramValue){
		if(!ObjectUtil.isNotNull(paramValue))
			return "null";
		/*if (paramValue == null)
			return "null";
		//如果是空串将返回空串，之前是返回null
		if (paramValue instanceof String && "".equals(paramValue))
			return "''";*/
		
		TableColumnVO columnVO = getColumnVO(tableDefVO, colName);
		JdbcType jdbcType = null;
		if(columnVO != null)
			jdbcType = JdbcType.forName(columnVO.getColtype());
		
		if(paramValue instanceof String){
			//依据数据库，对单引号进行转义
			return "'" + String.valueOf(paramValue).replace("'", "\\'") + "'";
		}else if(paramValue instanceof Integer || paramValue instanceof Long || paramValue instanceof Double || paramValue instanceof Byte){
			return paramValue.toString();
		}else if(paramValue instanceof Boolean){
			return (((Boolean)paramValue).booleanValue()?"1" : "0");
		}else if(paramValue instanceof Date){
			return getColDateValueString(paramValue, jdbcType);
		}else
			return "'" + paramValue.toString() + "'";
		
	}
	/**
	 * 
	 * @Title: getTempTableSqlWhere 
	 * @Description: sql where使用临时表查询,获取临时表信息：临时表名、临时表字段、临时表数据
	 * @param tableDefVO 表的信息
	 * @param colName 字段名
	 * @param paramValue 参数值
	 * @param dynaSqlVO 用于获取是否开启预编译
	 * @return
	 * @return String 使用临时表查询的sql where语句
	 * @throws
	 */
	private DynaSqlResultVO getTempTableSqlWhere(TableDefVO tableDefVO, String colName, List<Object> paramValue, DynaSqlVO dynaSqlVO){
		String tempTableName = getTempTableName(tableDefVO.getTable_code(), colName);
		TableColumnVO columnVO = this.getColumnVO(tableDefVO, colName);
		ObjectUtil.validNotNull(columnVO, colName + "is not in table[" + tableDefVO.getTable_code() +"]");
		String[][] columns = new String[][]{{colName, columnVO.getColtype(), columnVO.getOra_coltype(), columnVO.getCollen()+""}};
		String tempTableSqlWhere = "EXISTS (select 1 from " + tempTableName +" where " + tempTableName +"." + colName +"=" + tableDefVO.getTable_code() +"." + colName +")";
		DynaSqlResultVO dynaSqlResultVO = new DynaSqlResultVO(tempTableSqlWhere, dynaSqlVO.getUsePreStatement(), null, null);
		dynaSqlResultVO.addTempTableInfo(tempTableName, columns, paramValue);
		return dynaSqlResultVO;
	}
	private String getTempTableName(String tableName, String colName){
		//启用mycat时，临时表命名规范进行了修改，T_ 加 表名
		//MycatPropertiesUtil mycatPropertiesUtil = (MycatPropertiesUtil)AppServiceHelper.findBean("mycatPropertiesUtil");
		if("y".equals(MycatPropertiesUtil.getMycatFlag())){
			return "T_"+tableName;
		}
		int colLen = colName.length();
		if(colLen>=27)
			return "T_"+colName.substring(0,colLen>28?28:colLen);
		else{
			String temp = tableName.substring(tableName.indexOf("_")+1);
			if(temp.length()>27-colLen)
				return "T_"+temp.substring(0,27-colLen)+"_"+colName;
			return "T_"+temp+"_"+colName;
		}
	}
	/**
	 * 
	 * @Title: getInSqlWhere 
	 * @Description: 获取 where的in语句
	 * @param tableDefVO 表的信息
	 * @param colName 字段名
	 * @param paramValue 参数值
	 * @param dynaSqlVO 用于获取是否开启预编译
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getInSqlWhere(TableDefVO tableDefVO, String colName, Object paramValue, DynaSqlVO dynaSqlVO){
		StringBuffer sb = new StringBuffer();
//		String inSql = "";
		List<Object> preStatementParamList = null;
		List<Integer> preStatementParamJdbcTypeList = null;
		if(paramValue instanceof Object[]){
			Object[] paramValueArray = (Object[])paramValue;
			//加入标识判断是否启用临时表
			boolean isEnableTemptable = DBConstants.IS_ENABLE_TEMPTABLE;
			//如果是mysql或dm，先不启用临时表
			if (DBType.MYSQL.getCode().equals(ModuleDataSrcUtils.getDbType())
					|| DBType.DM.getCode().equals(ModuleDataSrcUtils.getDbType())) {
				isEnableTemptable = false;
			}
			if(isEnableTemptable && paramValueArray.length > DBConstants.IN_QUERY_VALUE_MAX){
				return getTempTableSqlWhere(tableDefVO, colName, Arrays.asList(paramValueArray), dynaSqlVO);
			}else if(paramValueArray.length == 0){
				sb.append(" " + colName + " is null ");
			}else{
				//定义变量，记录参数个数
				int paramSize = 0;
				sb.append(" " + colName + " in (");
				for(Object temp : paramValueArray){
					if(paramSize%DBConstants.IN_QUERY_VALUE_MAX==0 && 
							paramSize/DBConstants.IN_QUERY_VALUE_MAX!=0){
						sb.deleteCharAt(sb.length()-1);
						sb.append(") or " + colName + " in (");
					}
					if(dynaSqlVO.getUsePreStatement()){
						sb.append(" ?,");
						if(preStatementParamList == null)
							preStatementParamList = new ArrayList<Object>();
						preStatementParamList.add(temp);
						if(preStatementParamJdbcTypeList == null)
							preStatementParamJdbcTypeList = new ArrayList<Integer>();
						TableColumnVO columnVO = getColumnVO(tableDefVO, colName);
						JdbcType jdbcType = null;
						if(columnVO != null)
							jdbcType = JdbcType.forName(columnVO.getColtype());
						preStatementParamJdbcTypeList.add(jdbcType == null?null:jdbcType.getTypeCode());
					}else
						sb.append(" " + getColValueSqlString(tableDefVO, colName, temp) + ",");
					paramSize++;
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(" ) ");
				//添加大括号，多个or连接时，值一起返回。
				if(sb.length()!=0)
					sb.insert(0, "(").append(")");
//				inSql = sb.toString();
			}
		}
/*		if(paramValue instanceof List){
			List paramValueList = (List)paramValue;
			if(paramValueList.size() > DBConstants.IN_QUERY_VALUE_MAX){
				return getTempTableSqlWhere(tableDefVO, colName, paramValueList, dynaSqlVO);
			}else if(paramValueList.size() == 0){
				sb.append(" " + colName + " is null ");
			}else{
				sb.append(" " + colName + " in (");
				for(Object temp : paramValueList){
					if(dynaSqlVO.getUsePreStatement()){
						sb.append(" ?,");
						if(preStatementParamList == null)
							preStatementParamList = new ArrayList<Object>();
						preStatementParamList.add(temp);
					}else
						sb.append(" " + getColValueSqlString(tableDefVO, colName, temp) + ",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(" ) ");
//				inSql = sb.toString();
			}
		}else */
		if(paramValue instanceof Collection){
			List paramValueList = new ArrayList();
			paramValueList.addAll((Collection)paramValue);
			//加入标识判断是否启用临时表
			boolean isEnableTemptable = DBConstants.IS_ENABLE_TEMPTABLE;
			//如果是mysql或dm，先不启用临时表
			if (DBType.MYSQL.getCode().equals(ModuleDataSrcUtils.getDbType())
					|| DBType.DM.getCode().equals(ModuleDataSrcUtils.getDbType())) {
				isEnableTemptable = false;
			}
			if(isEnableTemptable && paramValueList.size() > DBConstants.IN_QUERY_VALUE_MAX){
				return getTempTableSqlWhere(tableDefVO, colName, paramValueList, dynaSqlVO);
			}else if(paramValueList.size() == 0){
				sb.append(" " + colName + " is null ");
			}else{
				//定义变量，记录参数个数
				int paramSize = 0;
				sb.append(" " + colName + " in (");
				for(Object temp : paramValueList){
					if(paramSize%DBConstants.IN_QUERY_VALUE_MAX==0 && 
							paramSize/DBConstants.IN_QUERY_VALUE_MAX!=0){
						sb.deleteCharAt(sb.length()-1);
						sb.append(") or " + colName + " in (");
					}
					if(dynaSqlVO.getUsePreStatement()){
						sb.append(" ?,");
						if(preStatementParamList == null)
							preStatementParamList = new ArrayList<Object>();
						preStatementParamList.add(temp);
						if(preStatementParamJdbcTypeList == null)
							preStatementParamJdbcTypeList = new ArrayList<Integer>();
						TableColumnVO columnVO = getColumnVO(tableDefVO, colName);
						JdbcType jdbcType = null;
						if(columnVO != null)
							jdbcType = JdbcType.forName(columnVO.getColtype());
						preStatementParamJdbcTypeList.add(jdbcType == null?null:jdbcType.getTypeCode());
					}else
						sb.append(" " + getColValueSqlString(tableDefVO, colName, temp) + ",");
					paramSize++;
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(" ) ");
				//添加大括号，多个or连接时，值一起返回。
				if(sb.length()!=0)
					sb.insert(0, "(").append(")");
//				inSql = sb.toString();
			}
		}
		
		
		if(sb.toString().trim().length()==0){
			sb.append(" " + colName + " is null ");
		}
		return new DynaSqlResultVO(sb.toString(), dynaSqlVO.getUsePreStatement(), preStatementParamList, preStatementParamJdbcTypeList);
	}
	/**
	 * 
	 * @Title: getSqlOrderBy 
	 * @Description: 获取order by 语句
	 * @param dynaSqlVO 用于获取order by语句
	 * @return
	 * @return String order by 语句
	 * @throws
	 */
	private String getSqlOrderBy(DynaSqlVO dynaSqlVO){
		if(dynaSqlVO == null)
			return "";
		return ObjectUtil.isNotNull(dynaSqlVO.getOrderByClause())?" order by " + dynaSqlVO.getOrderByClause() : "";
	}
	/**
	 * 
	 * @Title: getSqlGroupBy 
	 * @Description: 获取group by 语句
	 * @param dynaSqlVO 用于获取group by 语句
	 * @return
	 * @return String group by 语句
	 * @throws
	 */
	private String getSqlGroupBy(DynaSqlVO dynaSqlVO){
		if(dynaSqlVO == null)
			return "";
		return ObjectUtil.isNotNull(dynaSqlVO.getGroupByClause())?" group by " + dynaSqlVO.getGroupByClause() : "";
	}
	/**
	 * 
	 * @Title: getSqlForUpdate 
	 * @Description: 获取查询for update语句
	 * @param dynaSqlVO 用于获取是否添加for update
	 * @return
	 * @return String 查询for update语句
	 * @throws
	 */
	private String getSqlForUpdate(DynaSqlVO dynaSqlVO){
		if(dynaSqlVO == null)
			return "";
		return dynaSqlVO.getSelectForUpdate()?" for update " : "";
	}
	
	@Override
	public DynaSqlResultVO getSelectSql(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO, String sqlTableName) {
		ObjectUtil.validNotNull(tableDefVO, "sqlBuilder.getSelectSql: tableDefVO is required.");
		String tableName = tableDefVO.getTable_code();
		ObjectUtil.validNotNull(tableName, "sqlBuilder.getSelectSql: tableName can't be found.");
		StringBuffer sb = new StringBuffer();
		sb.append(" select");
		String sqlColumns = getSqlColumns(tableDefVO, dynaSqlVO, SyConstant.DATA_STATS_DEFAULT);
		if(ObjectUtil.isNotNull(sqlColumns)){
			if(sqlColumns.indexOf(".")==-1)
				sb.append(sqlColumns.replaceAll("([a-zA-Z_0-9]+)", tableName.toLowerCase()+"."+"$1"));
			else
				sb.append(sqlColumns);
		}else
			sb.append(" *");
		tableName = ObjectUtil.isNotNull(sqlTableName)? sqlTableName : tableName;
		sb.append(" from " + tableName.toLowerCase());
		if(SyConstant.NONTENANTID_TABLE.containsKey(tableName.toLowerCase())
				&&dynaSqlVO.getWhereParamMap()!=null&&dynaSqlVO.getWhereParamMap().containsKey("tenantId"))
		{
			dynaSqlVO.getWhereParamMap().remove("tenantId");
		}
		DynaSqlResultVO dynaSqlResultVO = getSqlWhere(tableDefVO, dynaSqlVO,null,SyConstant.DATA_STATS_DEFAULT);
		//优化临时表查询逻辑，使用EXISTS时性能较慢，改为使用表关联
		List<String> tempTableSqls = dynaSqlResultVO.getTempTableSqls();
		StringBuffer conditionSql=new StringBuffer();
		if(ObjectUtil.isNotEmpty(tempTableSqls)){
			for(int i=0;i<tempTableSqls.size();i++){
				sb.append(" ,").append(tempTableSqls.get(i).substring(0, tempTableSqls.get(i).indexOf(".")));
				conditionSql.append(tempTableSqls.get(i));
				if(i!=tempTableSqls.size()-1){
					conditionSql.append(" and ");
				}
			}
		}		
		String sqlWhere = ObjectUtil.isNotNull(dynaSqlResultVO.getSql())?dynaSqlResultVO.getSql():"";
		String sqlOrderBy = getSqlOrderBy(dynaSqlVO);
		String sqlGroupBy = getSqlGroupBy(dynaSqlVO);
		String sqlForUpdate = getSqlForUpdate(dynaSqlVO);
		
		if(conditionSql.length()!=0){
			StringBuffer tmpSqlBuffer=new StringBuffer();
			if(sqlWhere.equals(" where 1=1 ")){
				tmpSqlBuffer.append(" where ");
			}else{	
				conditionSql.append(" and ");
				tmpSqlBuffer.append(sqlWhere);
			}	
			tmpSqlBuffer.insert(tmpSqlBuffer.indexOf("where")+6, conditionSql);
			sqlWhere=tmpSqlBuffer.toString();			
		}			
		
		sb.append(sqlWhere);
		sb.append(sqlGroupBy);
		sb.append(sqlOrderBy);
		sb.append(sqlForUpdate);
		
		String sql = sb.toString();
		//dynaSqlResultVO.setSelectCountSql(this.getSelectCountSql(sql));
		//考虑取合计行不涉及数据的展示，这里不加入分组排序等字段的影响	
		String selectCountSql= getSelectCountSql(new String[]{tableName.toLowerCase(),sqlWhere,sqlForUpdate});
		//处理了countSql和查询sql走从库
		//MycatPropertiesUtil mycatPropertiesUtil = (MycatPropertiesUtil)AppServiceHelper.findBean("mycatPropertiesUtil");
		boolean isEnableMycat = "y".equals(MycatPropertiesUtil.getMycatFlag());
		//启用mycat数据源后才进行读写分离的相关引用
		if(isEnableMycat&&dynaSqlVO.isReadSlave()){
			selectCountSql = getReadSlaveSql(selectCountSql);
		}
		String convertCountSql = supportOraConvert(selectCountSql);
		dynaSqlResultVO.setSelectCountSql(convertCountSql);
		
		if(dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage())){
			String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
			Map<String,String> paramMap=new HashMap<String,String>();
			paramMap.put("tableName", tableName);
			paramMap.put("pkColName", pkColName);
			sql = getSelectLimitSql(sql,paramMap,dynaSqlVO.getPage().getOffset(), dynaSqlVO.getPage().getLimit());
		}
		if(isEnableMycat&&dynaSqlVO.isReadSlave()){
			sql = getReadSlaveSql(sql);
		}
		String convertSql = supportOraConvert(sql);
		dynaSqlResultVO.setSql(convertSql);
		return dynaSqlResultVO;
	}
	//加上mycat走从注解，读写分离
	private String getReadSlaveSql(String sql){
		return "/*balance*/ " + sql;
	}
	public String getSelectCountSql(String[] params) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(1) AS ").append(RS_COLUMN).append(" FROM ");
		sb.append(params[0]);
		for(int i=1;i<params.length;i++){
			if(ObjectUtil.isNotNull(params[i]))
				sb.append(params[i]);
		}
		return sb.toString();
	}
	
	@Override
	public String getSelectLimitSql(String sql, int offset, int limit) {
		return getSelectLimitSql(sql,null,offset,limit);
	}
	/**
	 * 
	 * @Title: trim 
	 * @Description: 格式化sql语句 去掉空格及末尾的";"
	 * @param sql sql语句
	 * @return
	 * @return String 格式化后的sql语句
	 * @throws
	 */
	protected String trim(String sql) {
		sql = sql.trim();
		if (sql.endsWith(SQL_END_DELIMITER)) {
			sql = sql.substring(0, sql.length() - 1
					- SQL_END_DELIMITER.length());
		}
		return sql;
	}

	/**
	 * 
	 * @Title: getInsertSqlValues 
	 * @Description: 获取insert的values语句及参数(预编译情况下)
	 * @param tableDefVO 表的信息
	 * @param sqlColumns sql字段列表
	 * @param vo 实体对象
	 * @param dynaSqlVO 用于获取是否开启预编译
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getInsertSqlValues(TableDefVO tableDefVO, String sqlColumns, AbstractVO vo, DynaSqlVO dynaSqlVO){
		StringBuffer sb = new StringBuffer();
		List<Object> preStatementParamList = null;
		boolean usePreStatement = dynaSqlVO != null && !dynaSqlVO.getUsePreStatement()?false : true;
		List<Integer> preStatementParamJdbcTypeList = null;
		
		String[] sqlColumnArray = sqlColumns.split(",");
		for(String colName : sqlColumnArray){
			colName = colName.trim();
			if(ObjectUtil.isNotNull(colName)){
				Object colValue = setInsertColsValue(tableDefVO,vo, colName);
				if(usePreStatement){
					if(preStatementParamList == null)
						preStatementParamList = new ArrayList<Object>();
					sb.append(" ?,");
					preStatementParamList.add(colValue);
					if(preStatementParamJdbcTypeList == null)
						preStatementParamJdbcTypeList = new ArrayList<Integer>();
					TableColumnVO columnVO = this.getColumnVO(tableDefVO, colName);
					JdbcType jdbcType = null;
					if(columnVO != null)
						jdbcType = JdbcType.forName(columnVO.getColtype());
					preStatementParamJdbcTypeList.add(jdbcType == null?null:jdbcType.getTypeCode());
				}else{
					sb.append(" " + this.getColValueSqlString(tableDefVO, colName, colValue) + ",");
				}
			}
		}
		String valuesSql = sb.toString();
		if(ObjectUtil.isNotNull(valuesSql))
			valuesSql = valuesSql.substring(0, valuesSql.length()-1);
		return new DynaSqlResultVO(valuesSql, usePreStatement, preStatementParamList, preStatementParamJdbcTypeList);
	}
	
	/**
	 * setColsValue:(处理平台字段). <br/>
	 * date: 2016年3月17日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 * @param colName
	 * @return
	 */
	private Object setInsertColsValue(TableDefVO tableDefVO,AbstractVO vo, String colName)
	{
		Object colValue = null;
		if(colName.equalsIgnoreCase(TableColumnVoConstants.CREATED_BY)
				||colName.equalsIgnoreCase(TableColumnVoConstants.UPDATED_BY))
		{
			if(vo.getLong(colName)!=null&&vo.getLong(colName)!=0L)
			{
				colValue = vo.getLong(colName);
			}else
			{
				colValue = DbEnvUtils.getUserId();				
			}
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.CREATED_DT)
				||colName.equalsIgnoreCase(TableColumnVoConstants.UPDATED_DT))
		{
			colValue = vo.get(colName,new Date());
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.CREATED_BY_NAME)
				||colName.equalsIgnoreCase(TableColumnVoConstants.UPDATED_BY_NAME))
		{
//			AbstractVO userVO =	getUserVo(vo.getLong(colName.substring(0,10), CurrentEnvUtils.getUserId()), 
//					vo.getLong(SyConstant.TENANT_STR, CurrentEnvUtils.getTenantId()));
			if(ObjectUtil.isNotNull(vo.getString(colName)))
			{
				colValue = vo.getString(colName);
			}else
			{
				colValue = DbEnvUtils.getUserName();
			}
		}else if(colName.equalsIgnoreCase(SyConstant.TENANT_STR))
		{
			if(vo.getLong(colName)!=null&&vo.getLong(colName)!=0L)
			{
				colValue = vo.getLong(colName);
			}else
			{
				colValue = DbEnvUtils.getTenantId();
			}
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.TS))
		{
			colValue = System.currentTimeMillis();
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.UUID)){
			if(ObjectUtil.isNotNull(vo.getString(colName)))
			{
				colValue = vo.getString(colName);
			}else
			{
				colValue = DbEnvUtils.getUUID();
			}
		}
//		else if(colName.equalsIgnoreCase(TableColumnVoConstants.INNERCODE)){
//			//添加获取内部编码逻辑，支持升级时使用。当存在值时不需要再次获取。
//			colValue = vo.get(colName);
//			if(!ObjectUtil.isNotNull(colValue))
//				colValue = InnerCodeUtil.getInnerCode(vo,vo.getLong(SyConstant.TENANT_STR));
//		}
		else
		{
			colValue = vo.get(colName);
		}
		//判断是否为空，为空时取默认值设置（如有）
		colValue = getColDefaultVal(vo, tableDefVO, colName, colValue);
		vo.set(colName, colValue);
		return colValue;
	}

	private Object getColDefaultVal(AbstractVO vo, TableDefVO tableDefVO, String colName,
			Object colValue) {
		if(ObjectUtil.isNotNull(colValue))
			return colValue;
		TableColumnVO tableColumnVO = getColumnVO(tableDefVO, colName);
		String colDefault = tableColumnVO.getColdefault();
		if(StringUtils.isNotEmpty(colDefault)&&!colDefault.trim().equals("null")){
			//voset方法结合值定义类型对值进行转换
			vo.set(colName, colDefault);
			//返回转换后的值
			colValue = vo.get(colName);
		}
		return colValue;
	}
	
	
	/**
	 * setUpdateColsValue:(更新时处理字段). <br/>
	 * date: 2016年3月17日 <br/>
	 *
	 * @author ZhangJie
	 * @param vo
	 * @param colName
	 * @return
	 */
	private Object setUpdateColsValue(TableDefVO tableDefVO,AbstractVO vo, String colName)
	{
		Object colValue = null;
		if(colName.equalsIgnoreCase(TableColumnVoConstants.UPDATED_BY))
		{
			if(vo.getLong(colName)!=null&&vo.getLong(colName)!=0L)
			{
				colValue = vo.getLong(colName);
			}else
			{
				colValue = DbEnvUtils.getUserId();
			}
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.UPDATED_DT))
		{
			colValue = vo.get(colName,new Date());
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.UPDATED_BY_NAME))
		{
//			AbstractVO userVO =	getUserVo(vo.getLong(colName.substring(0,10), CurrentEnvUtils.getUserId()), 
//					vo.getLong(SyConstant.TENANT_STR, CurrentEnvUtils.getTenantId()));
			if(ObjectUtil.isNotNull(vo.getString(colName)))
			{
				colValue = vo.getString(colName);
			}else
			{
				colValue = DbEnvUtils.getUserName();
			}
		}else if(colName.equalsIgnoreCase(TableColumnVoConstants.TS))
		{
			colValue = System.currentTimeMillis();
			vo.setLong(TableColumnVoConstants.TS, Long.parseLong(String.valueOf(colValue)));
		}
//		else if(colName.equalsIgnoreCase(TableColumnVoConstants.INNERCODE)){
//			String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
//			AbstractVO dbVO = InnerCodeUtil.getDbVO(vo, vo.getLong(pkColName));
//			//
//			if(needToResetInnercode(dbVO, vo)){
//				//节点位置调整，更换了父子级关系
//				colValue = InnerCodeUtil.getInnerCode(vo,vo.getLong(SyConstant.TENANT_STR));
//			}else{
//				colValue = vo.get(colName);
//			}
//		}
		else
		{
			colValue = vo.get(colName);
		}
		//判断是否为空，为空时取默认值设置（如有）
		colValue = getColDefaultVal(vo, tableDefVO, colName, colValue);
		return colValue;
	}
	
	private boolean needToResetInnercode(AbstractVO dbVO,AbstractVO vo){
		boolean sign = false;
		if(dbVO!=null&&dbVO.getString(TableColumnVoConstants.INNERCODE)!=null&&dbVO.getString(TableColumnVoConstants.INNERCODE).equals(vo.getString(TableColumnVoConstants.INNERCODE))){
			if(vo.getLong(TableColumnVoConstants.PARENTID)!=null){
				if(dbVO.getLong(TableColumnVoConstants.PARENTID)!=null&&dbVO.getLong(TableColumnVoConstants.PARENTID).longValue()!=vo.getLong(TableColumnVoConstants.PARENTID).longValue()){
					sign = true;
				}else if(dbVO.getLong(TableColumnVoConstants.PARENTID)==null){
					sign = true;
				}
			}else if(vo.getLong(TableColumnVoConstants.PARENTID)==null){
				if(dbVO.getLong(TableColumnVoConstants.PARENTID)!=null){
					sign = true;
				}
			}
		}
		return sign;
	}
	
	/*private AbstractVO getUserVo(Long userId,Long tenantId) {
		IHapUserService defaultUserService = AppServiceHelper.findBean(IHapUserService.class,"hapUserService");
		if(defaultUserService==null)
		{
			throw new HDRuntimeException("需要实现接口"+IHapUserService.class.getName());
		}
		return defaultUserService.getUserVO(userId, tenantId);
	}*/
	
	@Override
	public DynaSqlResultVO getInsertSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName) {
		ObjectUtil.validNotNull(tableDefVO, "sqlBuilder.getInsertSql: tableDefVO is required.");
		ObjectUtil.validNotNull(vo, "sqlBuilder.getInsertSql: vo is required.");
		String tableName = tableDefVO.getTable_code();
		ObjectUtil.validNotNull(tableName, "sqlBuilder.getInsertSql: tableName can't be found.");
		StringBuffer sb = new StringBuffer();
		tableName = ObjectUtil.isNotNull(sqlTableName)? sqlTableName : tableName;
		sb.append(" insert into " + tableName.toLowerCase() + "( ");
		String sqlColumns = getSqlColumns(tableDefVO, dynaSqlVO, SyConstant.DATA_STATS_ADDED);
//		requiredIsNotNull(sqlColumns);
		ObjectUtil.validNotNull(sqlColumns, "sqlBuilder.getInsertSql: inserted column list is empty.");
//		sqlColumns = checkHapDefaultCols(tableDefVO,sqlColumns,vo);
		sb.append(sqlColumns);
		sb.append(" ) values ( ");
		DynaSqlResultVO dynaSqlResultVO = this.getInsertSqlValues(tableDefVO, sqlColumns, vo, dynaSqlVO);
		sb.append(dynaSqlResultVO.getSql());
		sb.append(" )");
		dynaSqlResultVO.setSql(sb.toString());
		return dynaSqlResultVO;
	}

	/**
	 * 
	 * @Title: getPrepareInsertBatchSqlValues 
	 * @Description: 获取批量insert的values语句及参数(预编译情况下)
	 * @param tableDefVO 表的信息
	 * @param sqlColumns sql字段列表
	 * @param voList 实体对象集合
	 * @param dynaSqlVO 用于获取是否开启预编译
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getPrepareInsertBatchSqlValues(TableDefVO tableDefVO, String sqlColumns, List<AbstractVO> voList, DynaSqlVO dynaSqlVO){
		StringBuffer sb = new StringBuffer();
		List<Object> preStatementParamList = null;
		List<Integer> preStatementParamJdbcTypeList = null;
		
		String[] sqlColumnArray = sqlColumns.split(",");
		sb.append(" values (");
		for(int i=0;i<voList.size();i++){
			AbstractVO vo = voList.get(i);
			for(String colName : sqlColumnArray){
				colName = colName.trim();
				if(ObjectUtil.isNotNull(colName)){
					if(i == 0)
						sb.append(" ?,");
					Object colValue = setInsertColsValue(tableDefVO,vo, colName);
					if(preStatementParamList == null)
						preStatementParamList = new ArrayList<Object>();
					preStatementParamList.add(colValue);
					if(i == 0){
						if(preStatementParamJdbcTypeList == null)
							preStatementParamJdbcTypeList = new ArrayList<Integer>();
						TableColumnVO columnVO = this.getColumnVO(tableDefVO, colName);
						JdbcType jdbcType = null;
						if(columnVO != null)
							jdbcType = JdbcType.forName(columnVO.getColtype());
						preStatementParamJdbcTypeList.add(jdbcType == null?null:jdbcType.getTypeCode());
					}
					
				}
			}
		}
		
		String valuesSql = sb.toString();
		valuesSql = valuesSql.substring(0, valuesSql.length()-1);
		valuesSql += ")";
		return new DynaSqlResultVO(valuesSql, true, preStatementParamList, preStatementParamJdbcTypeList);
	}
	
	/**
	 * 
	 * @Title: getNonPrepareInsertBatchSql 
	 * @Description: 获取非预编译情况下的批量插入sql语句
	 * @param tableDefVO 表的信息
	 * @param sqlColumns sql字段列表
	 * @param voList 实体对象集合
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	protected DynaSqlResultVO getNonPrepareInsertBatchSql(TableDefVO tableDefVO, String sqlColumns, List<AbstractVO> voList){
		String tableName = tableDefVO.getTable_code();
		int voSize = voList.size();
		String[] batchSqls = new String[voSize];
		String[] sqlColumnArray = sqlColumns.split(",");
		for(int m =0 ; m < voSize; m++){
			StringBuffer sb = new StringBuffer();
			AbstractVO vo = voList.get(m);
			sb.append(" insert into " + tableName.toLowerCase() + "( ");
			sb.append(sqlColumns);
			sb.append(" ) values(");
			for(int i=0;i<sqlColumnArray.length;i++){
				String colName = sqlColumnArray[i];
				colName = colName.trim();
				if(ObjectUtil.isNotNull(colName)){
					Object colValue = setInsertColsValue(tableDefVO,vo, colName);
					if(i == sqlColumnArray.length-1){
						sb.append( getColValueSqlString(tableDefVO, colName, colValue) + ")");
						break;
					}
					sb.append( getColValueSqlString(tableDefVO, colName, colValue) + ", ");
				}
			}
			batchSqls[m] = sb.toString();
		}
		return new DynaSqlResultVO(batchSqls, false);
	}
	@Override
	public DynaSqlResultVO getInsertBatchSql(TableDefVO tableDefVO, List<AbstractVO> voList, DynaSqlVO dynaSqlVO, String sqlTableName) {
		
		ObjectUtil.validNotNull(tableDefVO, "sqlBuilder.getInsertBatchSql: tableDefVO is required.");
		ObjectUtil.validIsTrue(!ObjectUtil.isNotEmpty(voList), "sqlBuilder.getInsertBatchSql: voList is empty.");
		String tableName = tableDefVO.getTable_code();
		ObjectUtil.validNotNull(tableName, "sqlBuilder.getInsertBatchSql: tableName can't be found.");
		String sqlColumns = getSqlColumns(tableDefVO, dynaSqlVO, SyConstant.DATA_STATS_ADDED);
		ObjectUtil.validNotNull(sqlColumns, "sqlBuilder.getInsertBatchSql: inserted column list is empty.");
		boolean usePreStatement = dynaSqlVO != null && !dynaSqlVO.getUsePreStatement()?false : true;
		if(!usePreStatement)
			return this.getNonPrepareInsertBatchSql(tableDefVO, sqlColumns, voList);
		StringBuffer sb = new StringBuffer();
		tableName = ObjectUtil.isNotNull(sqlTableName)? sqlTableName : tableName;
		sb.append(" insert into " + tableName.toLowerCase() + "( ");
		sb.append(sqlColumns);
		sb.append(" ) ");
		DynaSqlResultVO dynaSqlResultVO = this.getPrepareInsertBatchSqlValues(tableDefVO, sqlColumns, voList, dynaSqlVO);
		sb.append(dynaSqlResultVO.getSql());
		dynaSqlResultVO.setSql(sb.toString());
		return dynaSqlResultVO;
	}

	/**
	 * 
	 * @Title: getUpdateWhereSql 
	 * @Description: 获取update语句的where子句
	 * @param tableDefVO 表的信息
	 * @param vo 实体对象 如果vo包含主键值则更新单条记录,否则以vo为新值更新where查询出来的记录
	 * @param dynaSqlVO 用于获取where条件参数、where子句用于拼接sql的where语句以及是否开启预编译
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getUpdateWhereSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO){
		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		Object pkValue = getVOPkColValue(tableDefVO, vo);
		if(ObjectUtil.isNotNull(pkValue)){
			if(dynaSqlVO == null){
				dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.setUsePreStatement(true);
			}
			dynaSqlVO.addWhereParam(pkColName, pkValue);
			if(!ObjectUtil.isNotNull(vo.getLong(TableColumnVoConstants.TENANTID)))
			{
				dynaSqlVO.setWhereByKey(true);
			}else
			{
				if(SyConstant.NONTENANTID_TABLE.containsKey(vo.getTableName().toLowerCase()))
				{
					dynaSqlVO.getWhereParamMap().remove(TableColumnVoConstants.TENANTID.toLowerCase());
				}else
					dynaSqlVO.addWhereParam(TableColumnVoConstants.TENANTID, vo.getLong(TableColumnVoConstants.TENANTID));
			}
		}else
		{
			dynaSqlVO.setWhereClause(pkColName + " is null");
		}
		//存在联合主键时处理逻辑
		List<String> pkColNameList=getPkColNameList(tableDefVO);
		for(String tmpPkColName:pkColNameList){
			if(tmpPkColName.equals(pkColName))
				continue;
			Object tmpPkValue = vo.get(tmpPkColName.toLowerCase());
			if(ObjectUtil.isNotNull(tmpPkValue)){
				dynaSqlVO.addWhereParam(tmpPkColName, tmpPkValue);
			}else{
				String whereClause=dynaSqlVO.getWhereClause();
				if(ObjectUtil.isNotNull(whereClause)){
					dynaSqlVO.setWhereClause(whereClause + " and " + tmpPkColName + " is null");
				}else{
					dynaSqlVO.setWhereClause(tmpPkColName + " is null");
				}
			}
		}
		
		return getSqlWhere(tableDefVO, dynaSqlVO,vo);
	}
	//依据字段和值获取DynaSqlResultVO
	private DynaSqlResultVO getParamDynaSqlResultVO(TableDefVO tableDefVO, DynaSqlVO dynaSqlVO,
			String colName,Object value){
		DynaSqlResultVO paramDynaSqlResultVO = null;
		if(dynaSqlVO.getUsePreStatement()){
			paramDynaSqlResultVO = this.getPrepareSqlWhereByParam(tableDefVO, dynaSqlVO, colName, value);
		}else {
			paramDynaSqlResultVO = this.getNonPrepareSqlWhereByParam(tableDefVO, dynaSqlVO, colName, value);
		}
		return paramDynaSqlResultVO;
	}
	//当存在联合主键时获取所有涉及的主键字段
	private List<String> getPkColNameList(TableDefVO tableDefVO){
		List<String> pkColNameList=new ArrayList<String>();
		if(tableDefVO.getPkColumnVOList()!=null&&tableDefVO.getPkColumnVOList().size()>1){
			for(TableColumnVO tableColumnVO:tableDefVO.getPkColumnVOList()){
				pkColNameList.add(tableColumnVO.getColcode().toLowerCase());
			}
		}
		return pkColNameList;
	}
	/**
	 * 
	 * @Title: getPrepareUpdateSetSqlString 
	 * @Description: 获取预编译情况下update语句的set子句
	 * @param sqlColumns sql字段列表
	 * @return
	 * @return String set子句
	 * @throws
	 */
	private String getPrepareUpdateSetSqlString(String sqlColumns){
		StringBuffer sb = new StringBuffer();
		String[] colArray = sqlColumns.split(",");
		sb.append(" set");
		for(String colName : colArray){
			colName = colName.trim();
			sb.append(" " + colName + " = ?,");
		}
		int sbLength = sb.length();
		sb.delete(sbLength-1, sbLength);
		return sb.toString();
	}
	/**
	 * 
	 * @Title: getPrepareUpdateSetSqlParams 
	 * @Description: 获取预编译情况下update语句的set参数
	 * @param tableDefVO 表的信息
	 * @param sqlColumns sql的字段列表
	 * @param vo 实体对象
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getPrepareUpdateSetSqlParams(TableDefVO tableDefVO, String sqlColumns, AbstractVO vo){
		List<Object> preStatementParamList = null;
		List<Integer> preStatementParamJdbcTypeList = null;
		
		String[] colArray = sqlColumns.split(",");
		for(String colName : colArray){
			colName = colName.trim();
//			Object colValue = vo.get(colName.toLowerCase());
			Object colValue = setUpdateColsValue(tableDefVO,vo, colName);
			if(preStatementParamList == null)
				preStatementParamList = new ArrayList<Object>();
			preStatementParamList.add(colValue);
			if(preStatementParamJdbcTypeList == null)
				preStatementParamJdbcTypeList = new ArrayList<Integer>();
			TableColumnVO columnVO = this.getColumnVO(tableDefVO, colName);
			JdbcType jdbcType = null;
			if(columnVO != null){
				jdbcType = JdbcType.forName(columnVO.getColtype());
			}
			preStatementParamJdbcTypeList.add(jdbcType == null?null:jdbcType.getTypeCode());
		}
		return new DynaSqlResultVO("", true, preStatementParamList, preStatementParamJdbcTypeList);
	}
	
	/**
	 * 
	 * @Title: getNonPrepareUpdateSetSql 
	 * @Description: 获取非预编译情况下update语句的set子句
	 * @param tableDefVO 表的信息
	 * @param sqlColumns sql的字段列表
	 * @param vo 实体对象
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getNonPrepareUpdateSetSql(TableDefVO tableDefVO, String sqlColumns, AbstractVO vo){
		StringBuffer sb = new StringBuffer();
		String[] colArray = sqlColumns.split(",");
		sb.append(" set");
		for(String colName : colArray){
			colName = colName.trim();
//			Object colValue = vo.get(colName.toLowerCase());
			Object colValue = setUpdateColsValue(tableDefVO,vo, colName);
			sb.append(" " + colName + " = "+this.getColValueSqlString(tableDefVO, colName, colValue) + ",");
		}
		int sbLength = sb.length();
		sb.delete(sbLength-1, sbLength);
		return new DynaSqlResultVO(sb.toString(), false, null, null);
	}
	
	@Override
	public DynaSqlResultVO getUpdateSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName) {
		ObjectUtil.validNotNull(tableDefVO, "sqlBuilder.getUpdateSql: tableDefVO is required.");
		ObjectUtil.validNotNull(vo, "sqlBuilder.getUpdateSql: vo is required.");
		String tableName = tableDefVO.getTable_code();
		ObjectUtil.validNotNull(tableName, "sqlBuilder.getUpdateSql: tableName can't be found.");
		StringBuffer sb = new StringBuffer();
		tableName = ObjectUtil.isNotNull(sqlTableName)? sqlTableName : tableName;
		sb.append("update " + tableName.toLowerCase());
		String sqlColumns = getSqlColumns(tableDefVO, dynaSqlVO, SyConstant.DATA_STATS_UPDATED);
		//更新操作时，需要处理的平台字段 时间、人
//		sqlColumns = updateColsForHap(tableDefVO, vo, sqlColumns);
		ObjectUtil.validNotNull(sqlColumns, "sqlBuilder.getUpdateSql: updated column list is empty.");
		
		boolean usePreStatement = dynaSqlVO != null && !dynaSqlVO.getUsePreStatement()?false : true;
		DynaSqlResultVO dynaSqlResultVO = null;
		if(usePreStatement){
			dynaSqlResultVO = this.getPrepareUpdateSetSqlParams(tableDefVO, sqlColumns, vo);
			dynaSqlResultVO.setSql(this.getPrepareUpdateSetSqlString(sqlColumns));
		}else
			dynaSqlResultVO = this.getNonPrepareUpdateSetSql(tableDefVO, sqlColumns, vo);
		sb.append(dynaSqlResultVO.getSql());
		
		DynaSqlResultVO whereDynaSqlResultVO = getUpdateWhereSql(tableDefVO, vo, dynaSqlVO);
		String sqlWhere = ObjectUtil.isNotNull(whereDynaSqlResultVO.getSql())?whereDynaSqlResultVO.getSql():"";
		sb.append(sqlWhere);
		if(dynaSqlResultVO.getUsePreStatement()){
			if(ObjectUtil.isNotEmpty(whereDynaSqlResultVO.getPreStatementParams())){
				dynaSqlResultVO.getPreStatementParams().addAll(whereDynaSqlResultVO.getPreStatementParams());
				if(ObjectUtil.isNotEmpty(whereDynaSqlResultVO.getPreStatementParamJdbcTypes()))
					dynaSqlResultVO.getPreStatementParamJdbcTypes().addAll(whereDynaSqlResultVO.getPreStatementParamJdbcTypes());
				else{
					int paramSize = whereDynaSqlResultVO.getPreStatementParams().size();
					for(int i=0;i<paramSize;i++)
						dynaSqlResultVO.getPreStatementParamJdbcTypes().add(null);
				}
			}
		}
		dynaSqlResultVO.addTempTableInfo(whereDynaSqlResultVO);
		String convertSql = supportOraConvert(sb.toString());
		dynaSqlResultVO.setSql(convertSql);
		return dynaSqlResultVO;
	}
	
	/**
	 * 
	 * @Title: getNonPrepareUpdateBatchSql 
	 * @Description: 非预编译情况下获取批量update语句
	 * @param tableDefVO 表的信息
	 * @param sqlColumns sql的字段列表
	 * @param voList 实体对象集合
	 * @param dynaSqlVO 用于获取where条件参数、where子句用于拼接sql的where语句以及是否开启预编译
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	protected DynaSqlResultVO getNonPrepareUpdateBatchSql(TableDefVO tableDefVO, String sqlColumns, List<AbstractVO> voList, DynaSqlVO dynaSqlVO){
		String tableName = tableDefVO.getTable_code();
		int voSize = voList.size();
		String[] batchSqls = new String[voSize];
		for(int m =0 ; m < voSize; m++){
			StringBuffer sb = new StringBuffer();
			AbstractVO vo = voList.get(m);
//			vo.set(TableColumnVoConstants.UPDATED_DT, HapServerHelper.getCurrentDate());
			DynaSqlResultVO dynaSqlResultVO = this.getNonPrepareUpdateSetSql(tableDefVO, sqlColumns, vo);
			sb.append("update " + tableName.toLowerCase());
			sb.append(dynaSqlResultVO.getSql());
			DynaSqlResultVO whereDynaSqlResultVO = getUpdateWhereSql(tableDefVO, vo, dynaSqlVO);
			if(whereDynaSqlResultVO != null){
				String sqlWhere = ObjectUtil.isNotNull(whereDynaSqlResultVO.getSql())?whereDynaSqlResultVO.getSql():"";
				sb.append(sqlWhere);
			}
			batchSqls[m] = sb.toString();
		}
		return new DynaSqlResultVO(batchSqls, false);
	}
	
	@Override
	public DynaSqlResultVO getUpdateBatchSql(TableDefVO tableDefVO, List<AbstractVO> voList, DynaSqlVO dynaSqlVO, String sqlTableName) {
		ObjectUtil.validNotNull(tableDefVO, "sqlBuilder.getUpdateBatchSql: tableDefVO is required.");
		ObjectUtil.validIsTrue(!ObjectUtil.isNotEmpty(voList), "sqlBuilder.getUpdateBatchSql: voList is empty.");
		String tableName = tableDefVO.getTable_code();
		ObjectUtil.validNotNull(tableName, "sqlBuilder.getUpdateBatchSql: tableName can't be found.");
		String sqlColumns = getSqlColumns(tableDefVO, dynaSqlVO, SyConstant.DATA_STATS_UPDATED);
		//更新操作时，不需要处理的属性
//		sqlColumns = updateColsForHap(tableDefVO, null, sqlColumns);
		ObjectUtil.validNotNull(sqlColumns, "sqlBuilder.getUpdateBatchSql: updated column list is empty.");
		boolean usePreStatement = dynaSqlVO != null && !dynaSqlVO.getUsePreStatement()?false : true;
		if(!usePreStatement)
			return getNonPrepareUpdateBatchSql(tableDefVO, sqlColumns, voList, dynaSqlVO);
		StringBuffer sb = new StringBuffer();
		tableName = ObjectUtil.isNotNull(sqlTableName)? sqlTableName : tableName;
		sb.append("update " + tableName.toLowerCase());
		
		sb.append(this.getPrepareUpdateSetSqlString(sqlColumns));
		
		List<Object> preStatementParamList = null;
		List<Integer> preStatementParamJdbcTypeList = null;
		int voSize = voList.size();
		for(int m =0 ; m < voSize; m++){
			AbstractVO vo = voList.get(m);
			//在getUpdateWhereSql方法对以下三个属性进行赋值
//			vo.set(TableColumnVoConstants.UPDATED_DT, HapServerHelper.getCurrentDate());
//			vo.set(TableColumnVoConstants.UPDATED_BY, CurrentEnvUtils.getUserId());
//			vo.set(TableColumnVoConstants.UPDATED_BY_NAME, CurrentEnvUtils.getUserName());
			Object pkValue = getVOPkColValue(tableDefVO, vo);
			ObjectUtil.validNotNull(pkValue, "sqlBuilder.getUpdateBatchSql: In updateBatch, primary key column value can't be null.");
			
			if(preStatementParamList == null)
				preStatementParamList = new ArrayList<Object>();
			if(preStatementParamJdbcTypeList == null)
				preStatementParamJdbcTypeList = new ArrayList<Integer>();
			
			DynaSqlResultVO dynaSqlResultVO = this.getPrepareUpdateSetSqlParams(tableDefVO, sqlColumns, vo);
			preStatementParamList.addAll(dynaSqlResultVO.getPreStatementParams());
			preStatementParamJdbcTypeList.addAll(dynaSqlResultVO.getPreStatementParamJdbcTypes());
			
			DynaSqlResultVO whereDynaSqlResultVO = getUpdateWhereSql(tableDefVO, vo, null);
			if(m == 0){
				String sqlWhere = ObjectUtil.isNotNull(whereDynaSqlResultVO.getSql())?whereDynaSqlResultVO.getSql():"";
				sb.append(sqlWhere);
			}
			if(ObjectUtil.isNotEmpty(whereDynaSqlResultVO.getPreStatementParams())){
				preStatementParamList.addAll(whereDynaSqlResultVO.getPreStatementParams());
				if(m == 0){
					if(ObjectUtil.isNotEmpty(whereDynaSqlResultVO.getPreStatementParamJdbcTypes()))
						preStatementParamJdbcTypeList.addAll(whereDynaSqlResultVO.getPreStatementParamJdbcTypes());
					else{
						int paramSize = whereDynaSqlResultVO.getPreStatementParams().size();
						for(int i=0;i<paramSize;i++)
							preStatementParamJdbcTypeList.add(null);
					}
				}
			}
		}
		return new DynaSqlResultVO(sb.toString(), true, preStatementParamList, preStatementParamJdbcTypeList);
	}
	/**
	 * 
	 * @Title: getDeleteWhereSql 
	 * @Description: 获取delete的where子句
	 * @param tableDefVO 表的信息
	 * @param vo 实体对象  如果vo包含主键值则删除单条记录,否则删除where查询出来的记录
	 * @param dynaSqlVO 用于获取where条件参数、where子句用于拼接sql的where语句以及是否开启预编译
	 * @return
	 * @return DynaSqlResultVO 动态生成sql的结果封装 包括sql语句、预编译参数值、预编译参数jdbcType以及是否开启预编译等
	 * @throws
	 */
	private DynaSqlResultVO getDeleteWhereSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO){
		//按动态sql删除
		if(dynaSqlVO!=null&&dynaSqlVO.getWhereParamMap()!=null&&!dynaSqlVO.getWhereParamMap().isEmpty())
		{
			return getSqlWhere(tableDefVO, dynaSqlVO,vo);
		}
		//对象为空，使用表名和条件进行删除
		if(vo == null)
			return getSqlWhere(tableDefVO, dynaSqlVO,vo);
		/*		//按主键进行删除时deleteByPK，直接进行操作
		if(dynaSqlVO!=null&&dynaSqlVO.getWhereByKey())
		{
			return getSqlWhere(tableDefVO, dynaSqlVO);
		}*/

		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		Object pkValue = getVOPkColValue(tableDefVO, vo);
		if(ObjectUtil.isNotNull(pkValue)){
			if(dynaSqlVO == null){
				dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.setUsePreStatement(true);
			}
			dynaSqlVO.setWhereByKey(true);
			dynaSqlVO.addWhereParam(pkColName, pkValue);
		}
		return getSqlWhere(tableDefVO, dynaSqlVO,vo);
	}

	@Override
	public DynaSqlResultVO getDeleteSql(TableDefVO tableDefVO, AbstractVO vo, DynaSqlVO dynaSqlVO, String sqlTableName) {
		ObjectUtil.validNotNull(tableDefVO, "sqlBuilder.getDeleteSql: tableDefVO is required.");
		String tableName = tableDefVO.getTable_code();
		ObjectUtil.validNotNull(tableName, "sqlBuilder.getDeleteSql: tableName can't be found.");
		if(vo != null){
			Object pkValue = getVOPkColValue(tableDefVO, vo);
			if(dynaSqlVO==null)
			{
				ObjectUtil.validNotNull(pkValue, "sqlBuilder.getDeleteSql: In delete by vo, primary key column value of vo can't be null.");
			}else if(dynaSqlVO.getWhereByKey())
			{
				ObjectUtil.validNotNull(pkValue, "sqlBuilder.getDeleteSql: In delete by vo, primary key column value of vo can't be null.");
			}
		}
		
		StringBuffer sb = new StringBuffer();
		tableName = ObjectUtil.isNotNull(sqlTableName)? sqlTableName : tableName;
		sb.append("delete from " + tableName.toLowerCase());
		
		DynaSqlResultVO dynaSqlResultVO = getDeleteWhereSql(tableDefVO, vo, dynaSqlVO);
		String sqlWhere = ObjectUtil.isNotNull(dynaSqlResultVO.getSql())?dynaSqlResultVO.getSql():"";
		sb.append(sqlWhere);
		
		String convertSql = supportOraConvert(sb.toString());
		dynaSqlResultVO.setSql(convertSql);
		return dynaSqlResultVO;
	}
	
	/**
	 * @param whereClause
	 * @return 返回适配oracle数据库sql
	 * @author wangyi
	 * @date 2017年7月17日
	 */
	private String supportOraConvert(String whereClause){
		return DBSqlUtil.convertMysql2Other(whereClause, ModuleDataSrcUtils.getDbType());
	}
	
	/**
	 * 
	 * @Title: getColumnVO 
	 * @Description: 按字段名获取字段信息
	 * @param tableDefVO 表的信息
	 * @param colName 字段名
	 * @return
	 * @return TableColumnVO 字段信息
	 * @throws
	 */
	protected TableColumnVO getColumnVO(TableDefVO tableDefVO, String colName){
		if(tableDefVO == null || tableDefVO.getColumnList() == null)
			return null;
		for(TableColumnVO columnVO : tableDefVO.getColumnList()){
			if(colName.equalsIgnoreCase(columnVO.getColcode()))
				return columnVO;
		}
		return null;
	}
	/**
	 * 
	 * @Title: getVOPkColValue 
	 * @Description: 获取实体对象的主键值
	 * @param tableDefVO 表的信息
	 * @param vo 实体对象
	 * @return
	 * @return Object 主键值
	 * @throws
	 */
	private Object getVOPkColValue(TableDefVO tableDefVO, AbstractVO vo){
		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		Object pkValue = null;
		if(ObjectUtil.isNotNull(pkColName))
			pkValue = vo.get(pkColName.toLowerCase());
		return pkValue;
	}
	
	/**
	 * 
	 * @Title: getColDateValueString 
	 * @Description: 根据jdbc类别返回不同格式的时间字符串
	 * @param paramValue 时间
	 * @param jdbcType jdbc类别 根据jdbc类别返回不同格式的时间
	 * @return
	 * @return String 时间sql String 形如 str_to_date('2015-05-12','%Y-%m-%d')
	 * @throws
	 */
	public abstract String getColDateValueString(Object paramValue, JdbcType jdbcType);
	
	
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
	public String getDialectSql(String sql, DynaSqlVO dynaSqlVO){
		if(sql==null || "".equals(sql))
			return sql;
		String newSql = new String(sql);
		if(dynaSqlVO != null && ObjectUtil.isNotNull(dynaSqlVO.getPage()))
			newSql = getSelectLimitSql(sql, dynaSqlVO.getPage().getOffset(), dynaSqlVO.getPage().getLimit());
		newSql = this.getDialectGroupConcatSql(newSql);
		newSql = this.getDialectStrToDateSql(newSql);
		//MycatPropertiesUtil mycatPropertiesUtil = (MycatPropertiesUtil)AppServiceHelper.findBean("mycatPropertiesUtil");
		boolean isEnableMycat = "y".equals(MycatPropertiesUtil.getMycatFlag());
		//启用mycat数据源后才进行读写分离的相关引用
		if(null!=dynaSqlVO&&isEnableMycat&&dynaSqlVO.isReadSlave()){
			newSql = getReadSlaveSql(newSql);
		}
		return newSql;
	}
	/**
	 * 
	 * @Title: getDialectGroupConcatSql 
	 * @Description: 翻译sql语句中group_concat函数
	 * @param sql 待翻译的sql语句 
	 * @return
	 * @return String 翻译后的sql语句
	 * @throws
	 */
	public abstract String getDialectGroupConcatSql(String sql);
	/**
	 * 
	 * @Title: getDialectStrToDateSql 
	 * @Description: 翻译sql语句中str_to_date函数
	 * @param sql 待翻译的sql语句 
	 * @return
	 * @return String 翻译后的sql语句
	 * @throws
	 */
	public abstract String getDialectStrToDateSql(String sql);
	
	/**
	 * 
	 * @Title: getDDLDataTypeAndLength 
	 * @Description: 返回DDL语句中字段的数据类型语句形如VARCHAR(50)
	 * @param columnVO 字段信息
	 * @return
	 * @return String 字段的数据类型语句
	 * @throws
	 */
	public String getDDLDataTypeAndLength(TableColumnVO columnVO){
		String dataType = columnVO.getColtype();
		Integer length = columnVO.getCollen();
		if(dataType.equalsIgnoreCase(TableDefDataType.DATETIME.getTypeName())
				|| dataType.equalsIgnoreCase(TableDefDataType.DATE.getTypeName())
				||dataType.equalsIgnoreCase(TableDefDataType.TEXT.getTypeName())
				||dataType.equalsIgnoreCase(TableDefDataType.LONGTEXT.getTypeName())
				||dataType.equalsIgnoreCase(TableDefDataType.TINYTEXT.getTypeName())
				||dataType.equalsIgnoreCase(TableDefDataType.BLOB.getTypeName())
				||dataType.equalsIgnoreCase(TableDefDataType.TINYBLOB.getTypeName())
				||dataType.equalsIgnoreCase(TableDefDataType.LOGNGBLOB.getTypeName()))

		{
			return dataType ;
		}else if(dataType.equalsIgnoreCase(TableDefDataType.DOUBLE.getTypeName())||
				dataType.equalsIgnoreCase(TableDefDataType.DECIMAL.getTypeName()))
		{
			return dataType + "(" + length + ","+columnVO.getColscale()+")";
		}
		return dataType + "(" + length + ")";
	}
	
	/**
	 * @desc 不同数据库类型获得的字段数据类型解析
	 * @param dbType
	 * @param tableDefDataType
	 * @return
	 */
	public String getDataTypeOfDbType(String dbType,String tableDefDataType)
	{
		return TableDef2Db.getDbTypeName(dbType,tableDefDataType);
	}
	
	/**@desc 解析update sql 增加必要的字段ts
	 * @param sql
	 * @return
	 */
	@Override
	public String addTS(String sql,TableDefVO tableDefVO){
		if(isHaveCol(tableDefVO, "ts"))
		{
			String signStr = " set ";
			int indexPos = sql.indexOf(signStr);
			if(indexPos>"update".length()){
				//判断下是否包含ts
				Pattern tsPattern = Pattern.compile("\\s+ts\\s*=\\s*\\d+\\s*",Pattern.CASE_INSENSITIVE);
				Matcher matcher = tsPattern.matcher(sql);
				//包含ts时，直接返回sql
				if(matcher.find()) {
					return sql;
				}
				sql = sql.substring(0, indexPos+signStr.length())+" ts ="+System.currentTimeMillis()+"," + sql.substring(indexPos+signStr.length());
			}
		}
		return sql;
	}
	
}
