package com.hayden.hap.common.enumerate;

public enum FuncTypeEnum {
	FORM("form","表单"),MODEL("model","模板定义"),CUSTOM("custom","自定义"),URL("url","URL"),REPORT("report","报表");
	private String id;
	private String name;
	private FuncTypeEnum(String id,String name){
		this.id=id;
		this.name=name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
