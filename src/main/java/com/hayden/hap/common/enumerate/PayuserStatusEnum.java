package com.hayden.hap.common.enumerate;

/**
 * 提现状态枚举
 * 
 * @author liyan
 * @date 2017年8月24日
 */
public enum PayuserStatusEnum implements IEnum {
	SUCCESS ("payuser_success","提现成功"),
	FAIL ("payuser_fail","提现失败");
	
	private String code;
	private String name;
	
	private PayuserStatusEnum(String code,String name) {
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
		return null;
	}
}
