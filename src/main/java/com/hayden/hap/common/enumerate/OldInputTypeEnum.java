package com.hayden.hap.common.enumerate;

/**
 * 已过期的输入类型
 * @author zhangfeng
 * @date 2017年3月30日
 */
public enum OldInputTypeEnum {

	DICT("4","字典编码"),DICTNN("5","字典无名称"),DICTWN("6","字典有名称");
	private String code;
	private String name;

	private OldInputTypeEnum(String code,String name) {
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
