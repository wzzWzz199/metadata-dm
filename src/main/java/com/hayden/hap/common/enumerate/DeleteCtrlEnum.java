package com.hayden.hap.common.enumerate;

/**
 * 删除控制枚举
 * @author zhangfeng
 * @date 2015年12月16日
 */
public enum DeleteCtrlEnum {

	DELETE("1","关联删除"),NO_CTRL("2","不控制"),NO_DELETE("3","禁止删除");
	
	private String code;
	
	private String name;
	
	private DeleteCtrlEnum(String code,String name) {
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
