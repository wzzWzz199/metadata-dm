package com.hayden.hap.common.enumerate;

/**
 * 输入类型枚举
 * @author zhangfeng
 * @date 2015年12月18日
 */
public enum InputTypeEnum {
	AUTO("1","自动"),MANUAL("2","手输"),QUERY_SELECT("3","查询选择"),
//	DICT("4","字典编码"),DICTNN("5","字典无名称"),DICTWN("6","字典有名称"),
	SERIAL_NUMBER_AUTO("7","自动编号"),SERIAL_NUMBER_MANUAL("8","手动编号"),
	DICT_NEW("9","字典");
	
	private String code;
	private String name;
	
	private InputTypeEnum(String code,String name) {
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
