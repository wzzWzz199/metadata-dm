package com.hayden.hap.common.enumerate;


public enum ConditionExpressEnum {
	SELFORG("selfOrg","本部门"),SUBORG("subOrg","本级及下级所有部门"),FIRSTLEVELORG("firstLevelOrg","第一级及下级部门"),SECONDLEVELORG("secondLevelOrg","第二级及下级部门"),THIRDLEVELORG("thirdLevelOrg","第三级及下级部门"),FORTHLEVELORG("forthLevelOrg","第四级及下级部门"),PARENTORG("parentOrg","父部门"),PARENTSUBORG("parentSubOrg","父部门及下级所有部门");;
	private String id;
	private String name;
	private ConditionExpressEnum(String id,String name){
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
