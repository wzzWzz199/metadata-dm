package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2018年5月3日
 */
public enum OrgTypeEnum {

	ORG("org","部门"),
	CON("con","承包商");

	private String code;
	private String name;
	
	private OrgTypeEnum(String code,String name) {
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
