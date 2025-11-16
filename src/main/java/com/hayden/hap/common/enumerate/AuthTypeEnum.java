package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2017年8月3日
 */
public enum AuthTypeEnum {

	DEFAULT("default","平台默认验证"),LDAP("ldap","LDAP验证");
	
	private String code;
	private String name;
	
	private AuthTypeEnum(String code,String name) {
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
