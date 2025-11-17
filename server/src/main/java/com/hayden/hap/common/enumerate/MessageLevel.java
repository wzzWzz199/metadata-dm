package com.hayden.hap.common.enumerate;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月16日
 */
public enum MessageLevel implements IEnum{
	/**
	 * 错误级别;
	 */
	ERROR("error","错误级别"),
	/**
	 * 警告级别;
	 */
	WARN("warn","告警级别"),
	/**
	 * 信息级别;
	 */
	INFO("info","提示级别");
	
	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String name;
	
	private MessageLevel(String code,String name) {
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
		EnumEntity enumEntity = new EnumEntity(code, name);
		return enumEntity;
	}

}
