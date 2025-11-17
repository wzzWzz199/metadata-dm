package com.hayden.hap.common.enumerate;

public enum ClientTypeEnum {
	PC("0","PC"),MOBILE("1","MOBILE");
	private String id;
	private String name;
	private ClientTypeEnum(String id,String name){
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
