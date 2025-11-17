package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2017年9月27日
 */
public enum TextAlignEnum implements IEnum {
	LEFT("left","左对齐"), CENTER("center","居中对齐"), RIGHT("right","右对齐");

	private String code;
	
	private String name;
	
	private TextAlignEnum(String code,String name) {
		this.code = code;
		this.name = name;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public EnumEntity toEntity() {
		EnumEntity entity = new EnumEntity(code, name);
		return entity;
	}

}
