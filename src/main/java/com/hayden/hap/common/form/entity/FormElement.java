package com.hayden.hap.common.form.entity;

import java.io.Serializable;

/**
 * 
 * @author zhangfeng
 * @date 2015年10月12日
 */
public class FormElement implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 表单元素id;
	 */
	private String id;

	/**
	 * 表单元素类型
	 */
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
