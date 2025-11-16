package com.hayden.hap.common.utils.properties;


public class MycatPropertiesUtil {

	private static String mycatFlag;
    
	public static String getMycatFlag() {
		return mycatFlag;
	}

	public void setMycatFlag(String mycatFlag) {
		MycatPropertiesUtil.mycatFlag = mycatFlag;
	}

}
