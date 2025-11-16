package com.hayden.hap.common.utils.properties;

public class RqReportPropertiesUtil {
	private static String rquestUrl;
	private static int token;
	public void init() {
	}
	public static String getRquestUrl() {
		return rquestUrl;
	}
	public void setRquestUrl(String rquestUrl) {
		RqReportPropertiesUtil.rquestUrl = rquestUrl;
	}
	public static int getToken() {
		return token;
	}
	public void setToken(int token) {
		RqReportPropertiesUtil.token = token;
	}
}
