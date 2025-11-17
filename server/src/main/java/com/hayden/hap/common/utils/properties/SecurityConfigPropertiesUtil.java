package com.hayden.hap.common.utils.properties;

/**
 * 
 * @author zhangfeng
 * @date 2018年10月9日
 */
public class SecurityConfigPropertiesUtil {

	/**
	 * 是否防御csrf攻击，默认否，通过配置文件可修改为true
	 */
	private static boolean defenseCsrf = false;

	public static boolean isDefenseCsrf() {
		return defenseCsrf;
	}

	public static void setDefenseCsrf(boolean defenseCsrf) {
		SecurityConfigPropertiesUtil.defenseCsrf = defenseCsrf;
	}
}
