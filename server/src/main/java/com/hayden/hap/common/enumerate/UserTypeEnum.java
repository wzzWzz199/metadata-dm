package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2018年4月27日
 */
public enum UserTypeEnum {
	STAFF("staff","员工"),
	CON("con","承包商人员");

	private String code;
	private String name;
	
	private UserTypeEnum(String code,String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
