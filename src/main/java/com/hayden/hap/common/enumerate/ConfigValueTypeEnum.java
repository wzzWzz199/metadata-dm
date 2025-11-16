package com.hayden.hap.common.enumerate;

/**
 * 参数值类型枚举
 * @author zhangfeng
 * @date 2016年8月15日
 */
public enum ConfigValueTypeEnum implements IEnum {
	STRING("string","字符串"),
	NUMBER("number","数字"),
	BOOLEAN("boolean","布尔型"),
	REGULAR("regular","正则表达式"),
	EXT_CLASS("ext_class","扩展类"),;

	private String code;
	private String name;
	
	private ConfigValueTypeEnum(String code,String name) {
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

	@Override
	public EnumEntity toEntity() {
		return null;
	}

}
