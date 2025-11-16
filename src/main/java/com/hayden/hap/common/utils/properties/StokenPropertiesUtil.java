package com.hayden.hap.common.utils.properties;

public class StokenPropertiesUtil {
	private static int stoken_timeout;
	private static int stoken_timeout_temp;

	public void init() {
	}

	public static int getStoken_timeout() {
		return stoken_timeout;
	}

	public void setStoken_timeout(int stoken_timeout) {
		StokenPropertiesUtil.stoken_timeout = stoken_timeout;
	}

	public static int getStoken_timeout_temp() {
		return stoken_timeout_temp;
	}

	public void setStoken_timeout_temp(int stoken_timeout_temp) {
		StokenPropertiesUtil.stoken_timeout_temp = stoken_timeout_temp;
	}

}
