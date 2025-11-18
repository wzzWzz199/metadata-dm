/**
 * Project Name:hap-sy
 * File Name:EnumFaultCode.java
 * Package Name:com.hayden.hap.sy.utils
 * Date:2016年11月8日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.dbop.utils;
/**
 * ClassName:EnumFaultCode ().<br/>
 * Date:     2016年11月8日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public enum EnumFaultCode {

	NO_RECORD("no_record","数据记录不存在!");
	private String code;
	private String name;
	private EnumFaultCode(String code,String name){
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
}

