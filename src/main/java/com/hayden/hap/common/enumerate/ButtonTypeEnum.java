package com.hayden.hap.common.enumerate;

/**
 * 按钮操作组
 * @author zhangfeng
 * @date 2015年12月11日
 */
public enum ButtonTypeEnum {
	LIST("list","列表"),CARD("card","卡片"),VBTN("vbtn","虚拟按钮"),URL("url","超链接url");
	
	private String code;
	private String name;
	
	private ButtonTypeEnum(String code,String name) {
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
