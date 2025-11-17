package com.hayden.hap.common.enumerate;

/**
 * 停启用枚举
 * @author zhangfeng
 * @date 2015年5月29日
 */
public enum Useable implements IEnum{
	USEABLE("1","启用"),UNUSEABLE("0","禁用");
	
	private String code;
	private String name;
	
	private Useable(String code,String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public EnumEntity toEntity() {
		return new EnumEntity(this.code, this.name);
	}
}
