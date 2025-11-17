package com.hayden.hap.common.enumerate;

/**
 * 移动端按钮类别枚举
 * @author zhangfeng
 * @date 2018年5月21日
 */
public enum ButtonTypeMEnum {

LIST_ADD("list_add","列表+"),LIST_PRESS("list_press","列表长按"),VBTN("vbtn","虚拟按钮"),
CARD_TOP("card_top","卡片顶部"),CARD_BOTTOM("card_bottom","卡片底部");
	
	private String code;
	private String name;
	
	private ButtonTypeMEnum(String code,String name) {
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
