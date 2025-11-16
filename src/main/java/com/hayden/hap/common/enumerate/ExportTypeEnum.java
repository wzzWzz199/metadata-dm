package com.hayden.hap.common.enumerate;

/**
 * 导出模板类型枚举
 * @author zhangfeng
 * @date 2016年7月22日
 */
public enum ExportTypeEnum implements IEnum {
	EXCEL2003("0","xls"),
	EXCEL2007("1","xlsx"),
	TXT("2","txt");

	private String code;
	private String name;
	
	private ExportTypeEnum(String code,String name) {
		this.code = code;
		this.name = name;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public EnumEntity toEntity() {
		// TODO Auto-generated method stub
		return null;
	}

}
