package com.hayden.hap.common.enumerate;

public enum PermissionTypeEnum {
	MENU("1","menu"),FUNC("2","func"),BUTTON("3","button"),BTNGROUP("4","btngroup"),PERMPACKAGE("5","permpackage");
	private String id;
	private String name;
	private PermissionTypeEnum(String id,String name){
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
