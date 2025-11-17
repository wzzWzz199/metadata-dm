package com.hayden.hap.common.utils.properties;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author zhangfeng
 * @date 2016年4月22日
 */
public class AttachPropertiesUtil {

	private static String default_upload_path;
	private static String type;
	private static String username;
	private static String password;
	private static String ip;
	private static String socketip;
	private static String socketport;
	private void init() {
	}

	public static String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		AttachPropertiesUtil.password = password;
	}
	public static String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		AttachPropertiesUtil.username = username;
	}

	public static String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		AttachPropertiesUtil.ip = ip;
	}
	
	public static String getDefault_upload_path() {
		return default_upload_path;
	}

	public void setDefault_upload_path(String default_upload_path) {
		AttachPropertiesUtil.default_upload_path = default_upload_path;
	}
	
	public static String getType() {
		return type;
	}
	
	public void setType(String type) {
		AttachPropertiesUtil.type = type;
	}
	
	public static String getSocketip() {
		return socketip;
	}

	public void setSocketip(String socketip) {
		AttachPropertiesUtil.socketip = socketip;
	}

	public static String getSocketport() {
		return socketport;
	}

	public void setSocketport(String socketport) {
		AttachPropertiesUtil.socketport = socketport;
	}

	/**
	 * 替换路径参数
	 * 
	 * @param funcCode 功能编码
	 * @param colcode  字段编码
	 * @param path 原始路径
	 */
	public static String replaceParam4path(String funcCode, String colcode,String path,Long tenantid) {
		path = path.replace("{funccode}", funcCode);
		if (null != colcode) {
			path = path.replace("{colcode}", colcode);
		} else {
			path = path.replace("{colcode}", "");
		}
//		Long tenantid = CurrentEnvUtils.getTenantId();
		path = path.replace("{tenantid}", tenantid+"");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
		if(month.length()==1){
			month = "0" + month;
		}
		path = path.replace("{year}", year);
		path = path.replace("{month}", month);
		return path;
	}
}
