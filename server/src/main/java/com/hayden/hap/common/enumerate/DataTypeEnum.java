package com.hayden.hap.common.enumerate;

public enum DataTypeEnum implements IEnum {
	STRING("1","字符串"),NUMBER("2","数字"),TEXT("3","大文本"),DATE("4","日期");
	
	private String id;
	
	private String tname;
	
	private DataTypeEnum(String id,String tname) {
		this.id = id;
		this.tname = tname;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
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
