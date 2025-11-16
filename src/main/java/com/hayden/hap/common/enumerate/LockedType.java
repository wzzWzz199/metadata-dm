package com.hayden.hap.common.enumerate;

/**
 * 用户锁定类型枚举
 * @author zhangfeng
 * @date 2018年3月19日
 */
public enum LockedType {

	AUTO_LOCK("auto","自动锁定"),
	MANUAL_LOCK("manual","手动锁定");
	
	private String code;
	private String name;
	
	private LockedType(String code,String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}
}
