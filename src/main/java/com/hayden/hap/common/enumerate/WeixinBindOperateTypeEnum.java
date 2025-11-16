package com.hayden.hap.common.enumerate;

/**
 * 微信绑定操作类型枚举类
 * 
 * @author liyan
 * @date 2017年10月24日
 */
public enum WeixinBindOperateTypeEnum implements IEnum {
	BIND ("bind","绑定"),
	UNBIND ("unbind","解绑");

	private String code;
	private String name;
	
	private WeixinBindOperateTypeEnum(String code,String name) {
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
