package com.hayden.hap.common.enumerate;

/**
 * 元素类型枚举
 * @author zhangfeng
 * @date 2015年12月25日
 */
public enum ElementTypeEnum {
	HIDDEN("0","隐藏字段"),INPUT("1","输入框"),COMBOBOX("2","下拉框"),
	DATE("3","日期"),RADIO("4","单选框"),TEXTAREA("5","文本域"),
	CHECKBOX("6","复选框"),FILE("7","文件上传"),IMG("8","图片"),
	GROUP("9","分组框"),NAVTAB("10","标签页"),LABLE("11","标签"),
	PHOTO("13","照片"),FAST_FILE("16","新附件"),FAST_IMG("17","新图片");;
	
	private String code;
	
	private String name;
	
	private ElementTypeEnum(String code,String name) {
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
