package com.hayden.hap.common.enumerate;

/**
 * 导出方式枚举
 * @author liyan
 * @date 2017年6月28日
 */
public enum ExportWayType{
	
	EXCEL("excel","Excel(2007)"),
	TXT("txt","txt(TAB间隔)"),
	IMPORT("import","导入"),
	TEMPLATE("template","其他模板(导出模板)"),
	FORM("form", "表单")
	;

	private String code;
	
	private String name;
	
//	EXCEL("1","excel"),TXT("2","txt"),IMPORT("3","import"),TEMPLATE("4","template");
//	private ExportWayType(String code,String name) {
//		this.code = code;
//		this.name = name;
//	}

	private ExportWayType(String code,String name) {
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
