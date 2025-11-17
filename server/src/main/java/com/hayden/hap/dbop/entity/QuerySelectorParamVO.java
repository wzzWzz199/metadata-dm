package com.hayden.hap.dbop.entity;

import java.io.Serializable;

/**
 * 查询选择的参数vo
 * @author zhangfeng
 * @date 2015年11月23日
 */
public class QuerySelectorParamVO implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 从哪个表单元素触发的查询选择
	 */
	private String fromEle;
	
	/**
	 * 取数的字段编码（多个以~分隔）
	 */
	private String selects;
	
	/**
	 * 单选（single）多选（multi）
	 */
	private String multi;
	
	/**
	 * 额外条件
	 */
	private String extWhere;

	public String getFromEle() {
		return fromEle;
	}

	public void setFromEle(String fromEle) {
		this.fromEle = fromEle;
	}

	public String getSelects() {
		return selects;
	}

	public void setSelects(String selects) {
		this.selects = selects;
	}

	public String getMulti() {
		return multi;
	}

	public void setMulti(String multi) {
		this.multi = multi;
	}

	public String getExtWhere() {
		return extWhere;
	}

	public void setExtWhere(String extWhere) {
		this.extWhere = extWhere;
	}
}
