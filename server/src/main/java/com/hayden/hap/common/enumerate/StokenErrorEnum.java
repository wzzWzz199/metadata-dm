package com.hayden.hap.common.enumerate;

/**
 * stoken的错误信息,与constants编号保持不重复
 * update by wushuangyang 2016-11-10,比较为过时的，此类已被com.hayden.hap.sy.login.message.MobileResponseStatusCodeEnum取代
 * @author lianghua
 * @date 2016年4月25日
 */
@Deprecated
public enum StokenErrorEnum {
	EMPTY(100,"stoken为空");
	
	private int code;
	private String name;
	
	private StokenErrorEnum(int code,String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
