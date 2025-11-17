package com.hayden.hap.common.enumerate;

/**
 * 审核状态枚举
 * @author zhangfeng
 * @date 2016年7月22日
 */
public enum AuditStateEnum implements IEnum {
	/**  公共*/
	TO_AUDIT ("0","待审核"),
	AUDITING ("1","审核中"),
	AUDITED("2","已审核"),
	/**   简版*/
	AUDIT_NOT_PASSED("3","审核未通过"),
	/**  高级版*/ 
	SUSPEND ("4","挂起"),
	INVALID("5","已作废");

	private String code;
	private String name;
	
	private AuditStateEnum(String code, String name) {
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
