package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月16日
 */
public enum MessageShowType implements IEnum{
	/**
	 * 弹出形式
	 */
	POPUP("popup","弹出形式"),
	/**
	 * 文档流形式
	 */
	FLOW("flow","流形式");
	
	private static final long serialVersionUID = 1L;

	private String code;
	
	private String name;
	
	private MessageShowType(String code,String name) {
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
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public EnumEntity toEntity() {
		EnumEntity entity = new EnumEntity(code, name);
		return entity;
	}
	
}
