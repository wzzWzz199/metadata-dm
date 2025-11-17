package com.hayden.hap.common.enumerate;

/**
 * 租户用户状态
 * @author zhangfeng
 * @date 2018年3月13日
 */
public enum CuserTenantStatusEnum {

	ENROLLMENT("enrollment","入职"), RESIGNATION("resignation","离职");
	
	private String code;
	private String name;
	
	private CuserTenantStatusEnum(String code, String name) {
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
