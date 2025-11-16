package com.hayden.hap.common.utils.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * JS版本工具类
 * 获得JS、CSS的版本号，从Jenkins的 ${SVN_REVISION} 变量获得，实际为SVN的版本号。
 * 
 * @author lengzy
 * @date 2016年1月22日
 */
public class JsVerPropertiesUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsVerPropertiesUtil.class);
	private static String js_ver;
	private static Properties mappings = null;
	public void init() {
		try {
			mappings = PropertiesLoaderUtils
					.loadAllProperties("conf/common/js-ver-conf.properties");
			js_ver = mappings.getProperty("js_ver");
			if (js_ver==null) {
				mappings = PropertiesLoaderUtils
						.loadAllProperties("conf/common-m/js-ver-conf.properties");
				js_ver = mappings.getProperty("ver_js");
			}
			
		} catch (IOException e) {
		}
	}

	public static String getJs_ver() {
		return js_ver;
	}

	public void setJs_ver(String js_ver) {
		JsVerPropertiesUtil.js_ver = js_ver;
	}
	
	

}
