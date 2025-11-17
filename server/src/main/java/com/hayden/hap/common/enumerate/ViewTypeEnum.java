package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2017年4月25日
 */
public enum ViewTypeEnum implements IEnum{
	LIST(1,"列表"),CARD(2,"卡片");
	
	private Integer code;
	private String name;
	
	private ViewTypeEnum(Integer code, String name) {
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
