package com.hayden.hap.common.enumerate;

/**
 * 租户类别枚举
 * @author zhangfeng
 * @date 2016年6月24日
 */
public enum TenantCategoryEnum implements IEnum {

	COMMON("common","普通租户"),MANAGER("manager","管理租户");
	
	private String code;
	private String value;
	
	private TenantCategoryEnum(String code,String value) {
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
