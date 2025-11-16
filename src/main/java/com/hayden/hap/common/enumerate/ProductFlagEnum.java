package com.hayden.hap.common.enumerate;

/**
 * 产品项目标识枚举
 * @author zhangfeng
 * @date 2016年5月31日
 */
public enum ProductFlagEnum implements IEnum {
	PRODUCT(1,"产品"),PROJECT(2,"项目");

	private Integer code;
	private String name;
	
	private ProductFlagEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public Integer getIntCode() {
		return code;
	}
	
	@Override
	public String getCode() {
		return code+"";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public EnumEntity toEntity() {
		return null;
	}

}
