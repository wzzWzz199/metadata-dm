package com.hayden.hap.common.enumerate;

/**
 * 移动端列表显示类别枚举
 * @author zhangfeng
 * @date 2018年5月11日
 */
public enum ListShowTypeEnum {

	HEADER_TYPE("header_type","表头分类"),
	HEADER_TITLE("header_title","表头标题"),
	HEADER_STATUS("header_status","表头状态"),
	CONTENT("content","列表内容"),
	BLANK("blank","不显示");
	
	private String code;
	private String name;
	
	private ListShowTypeEnum(String code,String name) {
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
