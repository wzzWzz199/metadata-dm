/**
 * 
 */
package com.hayden.hap.common.formmgr.inputconfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import java.util.Map;

/**
 * @author zhangfeng
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class QueryselectorInputConfigVO {
	
	/**
	 * 功能编码
	 */
	private String funccode;
	
	/**
	 * 映射关系，键为所选功能的字段，值为当前表单字段
	 */
	private Map<String,String> map;
	
	/**
	 * 是否多选
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean ismulti = false;
	
	/**
	 * 回调
	 */
	private String callback;
	
	/**
	 * where条件
	 */
	private String where;
	
	/**
	 * 是否id返名称
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean isid2name = false;
	
	/**
	 * 唯一字段
	 */
	private String unique;
	
	/**
	 * 检测where条件必填忽略的字段
	 * 逗号分隔多个字段
	 */
	private String ignore ;
	
	/**
	 * 排除不返名称字段
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> exclusions;
	
	/**
	 * 是否可选可输入
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean isinput = false;
	
	/**
	 * 分隔符
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String split = ",";
	
	/**
	 * 表名(输入设定里并没有这个字段，只是作为数据传输对象时候使用)
	 */
	@JsonIgnore
	private String tableName;
	
	/**
	 * 查询选择返名称的特定条件
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String where4name;

	public String getFunccode() {
		return funccode;
	}

	public void setFunccode(String funccode) {
		this.funccode = funccode;
	}


	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public boolean isIsmulti() {
		return ismulti;
	}

	public void setIsmulti(boolean ismulti) {
		this.ismulti = ismulti;
	}

	public boolean isIsid2name() {
		return isid2name;
	}

	public void setIsid2name(boolean isid2name) {
		this.isid2name = isid2name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIgnore() {
		return ignore;
	}

	public void setIgnore(String ignore) {
		this.ignore = ignore;
	}

	public boolean isIsinput() {
		return isinput;
	}

	public void setIsinput(boolean isinput) {
		this.isinput = isinput;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public List<String> getExclusions() {
		return exclusions;
	}

	public void setExclusions(List<String> exclusions) {
		this.exclusions = exclusions;
	}
	
	public String getWhere4name() {
		return where4name;
	}

	public void setWhere4name(String where4name) {
		this.where4name = where4name;
	}
}
