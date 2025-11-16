package com.hayden.hap.common.utils.properties;

/**
 * 环境配置
 * @author zhangfeng
 * @date 2018年4月10日
 */
public class EnvPropertiesUtil {

	private static Boolean isPrivate = false;

	public static Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		EnvPropertiesUtil.isPrivate = isPrivate;
	}
	
	public static Boolean getIsPublic() {
		return !isPrivate;
	}
}
