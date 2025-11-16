package com.hayden.hap.common.utils.properties;


/**
 * emq消息推送，配置参数
 * @author wangyi
 * @date 2017年5月8日
 */
public class EmqPropertiesUtil {
	
	/**
	 * 是否启用emq
	 */
	private static String emqFlag;
	
	/**
	 * 别名，当存在多个可用emq服务器，使用别名标识
	 */
	private static String alias;
	
	/**
	 * 连接emq服务器uri
	 */
	private static String broker;
	
	/**
	 * 登录认证用户名
	 */
	private static String userName;
	
	/**
	 * 登录认证密码
	 */
	private static String password;

	public static String getEmqFlag() {
		return emqFlag;
	}

	public void setEmqFlag(String emqFlag) {
		EmqPropertiesUtil.emqFlag = emqFlag;
	}

	public static String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		EmqPropertiesUtil.alias = alias;
	}

	public static String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		EmqPropertiesUtil.broker = broker;
	}

	public static String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		EmqPropertiesUtil.userName = userName;
	}

	public static String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		EmqPropertiesUtil.password = password;
	}
    
}
