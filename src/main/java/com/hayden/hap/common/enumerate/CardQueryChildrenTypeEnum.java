package com.hayden.hap.common.enumerate;

/**
 * 
 * 卡片查询子功能数据类型
 * @date 2019年01月16日
 * @author haocs
 */
public enum CardQueryChildrenTypeEnum {
	
	FITEM_RELATION_FUNC("fitem_relation_func","对应关联功能"),
	LINK_IS_LAZELOAD("link_is_lazeload","是否读写关联"),
	SHOW_CHILDREN_NUM("show_children_num","是否在主卡片 显示子功能数据总数");
	
	
	
	private String code;
	private String name;
	
	private CardQueryChildrenTypeEnum(String code,String name) {
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
