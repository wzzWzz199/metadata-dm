/**
 * Project Name:hap-sy
 * File Name:FormDataType.java
 * Package Name:com.hayden.hap.sy.form.entity
 * Date:2016年4月12日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.form.entity;


/**
 * ClassName:FormDataType ().<br/>
 * Date:     2016年4月12日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public enum FormDataType {
	CHAR(1,"CHAR","字符串"),
	NUM(2,"NUM","数字"),
	TEXT(3,"TEXT","大文本"),
	DATETIME(4,"DATETIME","日期");
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	private Integer num;
	private String code;
	private String desc;
	FormDataType(Integer num,String code,String desc){
		this.num = num;
		this.code = code;
		this.desc = desc;
	}
}

