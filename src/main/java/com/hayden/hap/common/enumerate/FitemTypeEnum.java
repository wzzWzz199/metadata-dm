package com.hayden.hap.common.enumerate;

public enum FitemTypeEnum implements IEnum {
	TABLE("1","表字段"),
	VIEW("2","视图字段"),
	PARAM("3","参数字段"),
	CUSTOM("4","自定义字段");
	
	private String id;
	
	private String tname;
	
	private FitemTypeEnum(String id,String tname) {
		this.id = id;
		this.tname = tname;
	}

	
	@Override
	public String getCode() {
		return id;
	}

	@Override
	public String getName() {
		return tname;
	}

	@Override
	public EnumEntity toEntity() {
		return new EnumEntity(this.id, this.tname);
	}
}
