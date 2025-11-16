package com.hayden.hap.common.enumerate;

/**
 * 单据号段类型枚举
 * @author zhangfeng
 * @date 2017年4月6日
 */
public enum BillcodeSegmentTypeEnum {
	GENERAL("1","普通"),YEAR("2","年"),YEAR_MONTH("3","年月"),YEAR_M_D("4","年月日"),
	SERIAL("5","流水号"),CONSTANT("6","常量"),VARIABLE("7","系统变量");
	
	private String code;
	private String name;
	
	private BillcodeSegmentTypeEnum(String code,String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
