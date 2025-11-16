package com.hayden.hap.common.enumerate;

/**
 * 按钮操作组
 * @author zhangfeng
 * @date 2015年12月11日
 */
public enum OperaGroupEnum {
	ADD("add","增加"),DEL("del","删除"),MODIFY("modify","修改"),QUERY("query","查询"),OTHER("other","其它");
	
	private String code;
	private String name;
	
	private OperaGroupEnum(String code,String name) {
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
