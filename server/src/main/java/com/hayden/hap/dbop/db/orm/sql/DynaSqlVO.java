package com.hayden.hap.dbop.db.orm.sql;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: DynaSqlVO 
 * @Description: 
 * @author LUYANYING
 * @date 2015年3月20日 下午5:23:18 
 * @version V1.0   
 *  
 */
public class DynaSqlVO {
	
	private Map<String, Object> whereParamMap = null;
	
	private List<String> sqlColumnList = null;
	
	private String groupByClause = null; // 不需要写group by
	
	private String whereClause = null;
	
	private String orderByClause = null; // 不需要写order by
	
	private boolean usePreStatement = true;
	
	private boolean whereByKey = false;
	
	private boolean selectForUpdate = false;
	
	private boolean useSqlColumnList = true;//是否使用sqlColumnList中列作为更新字段，默认为ture表示使用；如果为false时，则将这些列作为不更新字段。

	private boolean updateAssisFields = true;//是否update时更新	以下字段"updated_dt","updated_by","updated_by_name","ts"，特殊支持。默认为true，表示需要更新，否则设置为false
	
	private boolean readSlave = false;//启用mycat后，查询是否从读库进行查询，默认为false
	
	private Page page = null;
	
	public Page createPage(int pageNo, int limit){
		page = new Page(pageNo, limit);
		return page;
	}
	public Page createPage(int pageNo, int limit, int totalRows){
		page = new Page(pageNo, limit, totalRows);
		return page;
	}
	
	public void addWhereParam(String colName, Object colValue){
		if(whereParamMap == null)
			whereParamMap = new HashMap<String, Object>();
		whereParamMap.put(colName, colValue);
	}
	public Object getWhereParam(String colName){
		if(whereParamMap == null)
			return null;
		return whereParamMap.get(colName);
	}
	public Object removeWhereParam(String colName){
		if(whereParamMap == null)
			return null;
		return whereParamMap.remove(colName);
	}
	public DynaSqlVO clone(){
		DynaSqlVO newObject = new DynaSqlVO();
		newObject.setGroupByClause(getGroupByClause());
		newObject.setOrderByClause(getOrderByClause());
		newObject.setPage(getPage());
		newObject.setSqlColumnList(getSqlColumnList());
		newObject.setUsePreStatement(getUsePreStatement());
		newObject.setWhereByKey(getWhereByKey());
		newObject.setWhereClause(getWhereClause());
		/*newObject.setWhereParamMap(new HashMap<String, Object>());
		newObject.getWhereParamMap().putAll(getWhereParamMap());*/
		newObject.setWhereParamMap(getWhereParamMap());
		return newObject;
	}
	
	
	public Map<String, Object> getWhereParamMap() {
		return whereParamMap;
	}
	public void setWhereParamMap(Map<String, Object> whereParamMap) {
		this.whereParamMap = whereParamMap;
	}
	
	public String getGroupByClause() {
		return groupByClause;
	}
	
	public void setGroupByClause(String groupByClause) {
		this.groupByClause = groupByClause;
	}
	public String getWhereClause() {
		return whereClause;
	}
	
	public void addWhereClause(String s) {
		if(StringUtils.isEmpty(s))
			return;
		if(StringUtils.hasLength(whereClause)) {
			whereClause += " and ";
			whereClause += s;
		}else {
			whereClause = s;
		}
	}
	
	public void addWhereClauseAtFirst(String s) {
		if(StringUtils.hasLength(whereClause)) {
			whereClause = s + " and " + whereClause;
		}else {
			whereClause = s;
		}
	}
	
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
	public String getOrderByClause() {
		return orderByClause;
	}
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}
	public boolean getUsePreStatement() {
		return usePreStatement;
	}
	public void setUsePreStatement(boolean usePreStatement) {
		this.usePreStatement = usePreStatement;
	}
	public boolean getWhereByKey() {
		return whereByKey;
	}
	public void setWhereByKey(boolean whereByKey) {
		this.whereByKey = whereByKey;
	}
	public List<String> getSqlColumnList() {
		return sqlColumnList;
	}
	public void setSqlColumnList(List<String> sqlColumnList) {
		this.sqlColumnList = sqlColumnList;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public boolean getSelectForUpdate() {
		return selectForUpdate;
	}
	public void setSelectForUpdate(boolean selectForUpdate) {
		this.selectForUpdate = selectForUpdate;
	}
	public boolean getUseSqlColumnList() {
		return useSqlColumnList;
	}
	
	/**
	 * 是否使用sqlColumnList中列作为更新字段，默认为ture表示使用；如果为false时，则将这些列作为不更新字段。
	 * @param useSqlColumnList 
	 * @author zhangfeng
	 * @date 2017年7月25日
	 */
	public void setUseSqlColumnList(boolean useSqlColumnList) {
		this.useSqlColumnList = useSqlColumnList;
	}
	public boolean isReadSlave() {
		return readSlave;
	}
	public void setReadSlave(boolean readSlave) {
		this.readSlave = readSlave;
	}
	public boolean getUpdateAssisFields() {
		return updateAssisFields;
	}
	public void setUpdateAssisFields(boolean updateAssisFields) {
		this.updateAssisFields = updateAssisFields;
	}

	public DynaSqlVO addSqlColumn(String column) {
		if(sqlColumnList==null)
			sqlColumnList = new ArrayList<>();
		
		sqlColumnList.add(column);
		return this;
	}
}
