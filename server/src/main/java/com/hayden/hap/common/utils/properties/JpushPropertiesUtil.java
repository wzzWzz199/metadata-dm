package com.hayden.hap.common.utils.properties;

import com.alibaba.druid.support.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class JpushPropertiesUtil {
	private static Logger LOGGER=LoggerFactory.getLogger(JpushPropertiesUtil.class);
    private static String httpProxy_host = null;
    private static String httpProxy_port;
    private static String httpProxy_username;
    private static String httpProxy_password;
    private static String keySecret;
    private static Map<String, String> keyMap;

    public void init(){
		if (StringUtils.isBlank(this.keySecret)){
			try {
				Properties config = PropertiesLoaderUtils
						.loadAllProperties("conf/common/jpush-conf.properties");
				httpProxy_host = config.getProperty("jpush.httpProxy.host");
				config.remove("jpush.httpProxy.host");
				httpProxy_port = config.getProperty("jpush.httpProxy.port");
				config.remove("jpush.httpProxy.port");
				httpProxy_username = config.getProperty("jpush.httpProxy.username");
				config.remove("jpush.httpProxy.username");
				httpProxy_password = config.getProperty("jpush.httpProxy.password");
				config.remove("jpush.httpProxy.password");
				Set keys = config.keySet();
				keyMap = new HashMap<String, String>();
				for (Object key : keys) {
					keyMap.put(key.toString(), config.getProperty(key.toString()));
				}
			} catch (IOException e) {
				LOGGER.error("初始化极光推送配置出错",e);
			}
		}
		
	}

    public static String getHttpProxy_host() {
        return httpProxy_host;
    }

    public static String getHttpProxy_port() {
        return httpProxy_port;
    }

    public static String getHttpProxy_username() {
        return httpProxy_username;
    }

    public static String getHttpProxy_password() {
        return httpProxy_password;
    }

    public static Map<String, String> getKeyMap() {
    	if (StringUtils.isNotBlank(keySecret)){
    		try {
    			keyMap = (Map<String,String>) JSONUtils.parse(keySecret);
    		} catch (Exception e) {
    			LOGGER.error("json转换keySecret失败");
    		}
		}
        return keyMap;
    }
    
	@Value("${keySecret:}")
	public void setKeySecret(String keySecret) {
		JpushPropertiesUtil.keySecret = keySecret;
	}

	public static void setHttpProxy_host(String httpProxy_host) {
		JpushPropertiesUtil.httpProxy_host = httpProxy_host;
	}

	public static void setHttpProxy_port(String httpProxy_port) {
		JpushPropertiesUtil.httpProxy_port = httpProxy_port;
	}

	public static void setHttpProxy_username(String httpProxy_username) {
		JpushPropertiesUtil.httpProxy_username = httpProxy_username;
	}

	public static void setHttpProxy_password(String httpProxy_password) {
		JpushPropertiesUtil.httpProxy_password = httpProxy_password;
	}
}
