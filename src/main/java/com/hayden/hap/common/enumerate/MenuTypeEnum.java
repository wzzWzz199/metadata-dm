package com.hayden.hap.common.enumerate;

public enum MenuTypeEnum {
	PARENTMENU("parentmenu","父菜单"),FUNC("func","功能"),URL("url","url"),REPORT("report","报表");
	private String id;
	private String name;
	private MenuTypeEnum(String id,String name){
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
