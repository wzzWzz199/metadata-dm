package com.hayden.hap.common.enumerate;

/**
 * 字段说明文档类型
 * @author zhangfeng
 * @date 2017年12月7日
 */
public enum ItemCommentType {

DEVLOP("devlop","开发说明文档"),HELP("help","用户帮助文档");
	
	private String code;
	private String name;
	
	private ItemCommentType(String code,String name) {
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
