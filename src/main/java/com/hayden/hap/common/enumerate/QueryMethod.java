package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2015年9月21日
 */
public enum QueryMethod implements IEnum{
	EQ("eq","等于"),GT("gt","大于"),GE("ge","大于等于"),
	LT("lt","小于"),LE("le","小于等于"),NOT_EQ("not_eq","不等于"),
	IN("in","包含"),NOT_IN("not_in","不包含"),
	ANYWHERE("anywhere","like(前后都有%)"),START("start","like(%在后)"),END("end","%在前"),
	ALL("all","全文检索"),RANGE("range","范围查询");
	
	private String code;
	private String value;
	
	private QueryMethod(String code,String value) {
		this.code = code;
		this.value = value;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getName() {
		return value;
	}

	@Override
	public EnumEntity toEntity() {
		return new EnumEntity(this.code, this.value);
	}

}
