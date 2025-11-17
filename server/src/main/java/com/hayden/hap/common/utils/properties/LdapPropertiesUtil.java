package com.hayden.hap.common.utils.properties;

/**
 * 
 * @author zhangfeng
 * @date 2017年8月3日
 */
public class LdapPropertiesUtil {

	private static String url;
	
	private static String baseDn;
	
	private static String user;
	
	private static String password;

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		LdapPropertiesUtil.url = url;
	}

	public static String getBaseDn() {
		return baseDn;
	}

	public static void setBaseDn(String baseDn) {
		LdapPropertiesUtil.baseDn = baseDn;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		LdapPropertiesUtil.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		LdapPropertiesUtil.password = password;
	}
}
