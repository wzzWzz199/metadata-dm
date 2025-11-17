package com.hayden.hap.dbop.db.orm.sql;

import com.hayden.hap.dbop.db.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: DynaSqlResultVO 
 * @Description: 动态生成SQL结果 包含sql语句、预编译参数值、预编译参数jdbc类型
 * @author LUYANYING
 * @date 2015年4月16日 下午7:32:42 
 * @version V1.0   
 *  
 */
public class DynaSqlResultVO {
	private String sql; //sql语句
	private boolean usePreStatement = false; //是否开启预编译
	private List<Object> preStatementParams; //预编译参数值
	private List<Integer> preStatementParamJdbcTypes; //预编译参数jdbc类型
	private String[] batchSqls = null;  //批量更新时在非预编译情况下的sql语句
	
	private List<String> tempTableNames = null;
	private List<String[][]> tempTableColumns = null;
	private List<List<Object>> tempTableDataList = null;
	private List<String> tempTableSqls = null;
	
	private String selectCountSql;//分页时count sql 语句 用来统计总行数
	
	public DynaSqlResultVO (String sql, boolean usePreStatement, List<Object> preStatementParams, List<Integer> preStatementParamJdbcTypes){
		this.sql = sql;
		this.usePreStatement = usePreStatement;
		this.preStatementParams = preStatementParams;
		this.preStatementParamJdbcTypes = preStatementParamJdbcTypes;
	}
	public DynaSqlResultVO (String[] batchSqls, boolean usePreStatement){
		this.batchSqls = batchSqls;
		this.usePreStatement = usePreStatement;
	}
	public DynaSqlResultVO(){
		
	}
	
	/**
	 * 
	 * @Title: addTempTableInfo 
	 * @Description: 添加临时表信息
	 * @param tempTableName 临时表名
	 * @param columns 临时表字段信息
	 * @param tempTableData 数据
	 * @return void
	 * @throws
	 */
	public void addTempTableInfo(String tempTableName, String[][] columns, List<Object> tempTableData){
		if(tempTableNames == null)
			tempTableNames = new ArrayList<String>();
		if(tempTableColumns == null)
			tempTableColumns = new ArrayList<String[][]>();
		if(tempTableDataList == null)
			tempTableDataList = new ArrayList<List<Object>>();
		tempTableNames.add(tempTableName);
		tempTableColumns.add(columns);
		tempTableDataList.add(tempTableData);
	}
	/**
	 * 
	 * @Title: addTempTableInfo 
	 * @Description: 添加临时表信息
	 * @param dynaSqlResultVO
	 * @return void
	 * @throws
	 */
	public void addTempTableInfo(DynaSqlResultVO dynaSqlResultVO){
		if(ObjectUtil.isNotEmpty(dynaSqlResultVO.getTempTableNames())){
			if(tempTableNames == null)
				tempTableNames = new ArrayList<String>();
			tempTableNames.addAll(dynaSqlResultVO.getTempTableNames());
		}
		if(ObjectUtil.isNotEmpty(dynaSqlResultVO.getTempTableColumns())){
			if(tempTableColumns == null)
				tempTableColumns = new ArrayList<String[][]>();
			tempTableColumns.addAll(dynaSqlResultVO.getTempTableColumns());
		}
		if(ObjectUtil.isNotEmpty(dynaSqlResultVO.getTempTableDataList())){
			if(tempTableDataList == null)
				tempTableDataList = new ArrayList<List<Object>>();
			tempTableDataList.addAll(dynaSqlResultVO.getTempTableDataList());
		}
		if(ObjectUtil.isNotEmpty(dynaSqlResultVO.getTempTableSqls())){
			if(tempTableSqls == null)
				tempTableSqls = new ArrayList<String>();
			tempTableSqls.addAll(dynaSqlResultVO.getTempTableSqls());
		}
	}
	
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public boolean getUsePreStatement() {
		return usePreStatement;
	}
	public void setUsePreStatement(boolean usePreStatement) {
		this.usePreStatement = usePreStatement;
	}
	public List<Object> getPreStatementParams() {
		return preStatementParams;
	}
	public void setPreStatementParams(List<Object> preStatementParams) {
		this.preStatementParams = preStatementParams;
	}
	public List<Integer> getPreStatementParamJdbcTypes() {
		return preStatementParamJdbcTypes;
	}
	public void setPreStatementParamJdbcTypes(List<Integer> preStatementParamJdbcTypes) {
		this.preStatementParamJdbcTypes = preStatementParamJdbcTypes;
	}
	public String[] getBatchSqls() {
		return batchSqls;
	}
	public void setBatchSqls(String[] batchSqls) {
		this.batchSqls = batchSqls;
	}
	public List<String> getTempTableNames() {
		return tempTableNames;
	}
	public void setTempTableNames(List<String> tempTableNames) {
		this.tempTableNames = tempTableNames;
	}
	public List<String[][]> getTempTableColumns() {
		return tempTableColumns;
	}
	public void setTempTableColumns(List<String[][]> tempTableColumns) {
		this.tempTableColumns = tempTableColumns;
	}
	public List<List<Object>> getTempTableDataList() {
		return tempTableDataList;
	}
	public void setTempTableDataList(List<List<Object>> tempTableDataList) {
		this.tempTableDataList = tempTableDataList;
	}
	public String getSelectCountSql() {
		return selectCountSql;
	}
	public void setSelectCountSql(String selectCountSql) {
		this.selectCountSql = selectCountSql;
	}
	public List<String> getTempTableSqls() {
		return tempTableSqls;
	}
	public void setTempTableSqls(List<String> tempTableSqls) {
		this.tempTableSqls = tempTableSqls;
	}
}
