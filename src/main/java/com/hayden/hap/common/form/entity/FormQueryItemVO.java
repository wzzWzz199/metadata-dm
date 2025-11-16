package com.hayden.hap.common.form.entity;

/**
 * 
 * @author zhangfeng
 * @date 2016年3月29日
 */
public class FormQueryItemVO {

	private String logicRel;
	private String formItem;
	private String queryMethod;
	private String value;
	
	public String getLogicRel() {
		return logicRel;
	}
	public void setLogicRel(String logicRel) {
		this.logicRel = logicRel;
	}
	public String getFormItem() {
		return formItem;
	}
	public void setFormItem(String formItem) {
		this.formItem = formItem;
	}
	public String getQueryMethod() {
		return queryMethod;
	}
	public void setQueryMethod(String queryMethod) {
		this.queryMethod = queryMethod;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
