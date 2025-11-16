package com.hayden.hap.common.enumerate;

/**
 * 审批结束允许的操作枚举类
 * @author zhangfeng
 * @date 2016年7月22日
 */
public enum AuditedAllowOperEnum implements IEnum {
	NOTHING("0","无"),
	MODIFY("1","修改"),
	DELETE("2","删除"),
	MODIFY_AND_DELETE("3","修改和删除");

	private String code;
	private String name;
	
	private AuditedAllowOperEnum(String code,String name) {
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
