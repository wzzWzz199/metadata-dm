package com.hayden.hap.common.enumerate;

/**
 * 工作流类型枚举
 * @author zhangfeng
 * @date 2016年7月22日
 */
public enum WorkflowTypeEnum implements IEnum {
	NOTHING("0","空"),
	STD("1","简版"),
	PRO("2","高级版"),
	FORBIDDEN("3","禁用审批流"),
	CONCURRENCYPRO("4","高级并发版");
	private String code;
	private String name;
	
	private WorkflowTypeEnum(String code,String name) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
