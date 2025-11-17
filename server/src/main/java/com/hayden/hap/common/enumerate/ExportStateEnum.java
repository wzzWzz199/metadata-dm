package com.hayden.hap.common.enumerate;

/**
 * 导出状态
 * @author haocs
 *
 */
public enum ExportStateEnum {

	INIT("init","初始"),PROCE("proce","执行中"),COMPLE("comple","完成"),ERROR("error","失败"),DEFAULT("init","初始");

	private String code;
	private String name;

	private ExportStateEnum(String code,String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
