package com.hayden.hap.common.enumerate;

/**
 * 红包状态枚举类
 * 
 * @author liyan
 * @date 2017年8月24日
 */
public enum RedpacketStatusEnum implements IEnum {
	SENDING ("SENDING","发放中"),
	SENT ("SENT","已发放待领取"),
	FAILED("FAILED","发放失败"),
	RECEIVED ("RECEIVED","已领取"),
	RFUND_ING ("RFUND_ING","退款中"),
	REFUND("REFUND","已退款");

	private String code;
	private String name;
	
	private RedpacketStatusEnum(String code,String name) {
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
