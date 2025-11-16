package com.hayden.hap.common.utils.properties;

import com.alibaba.druid.support.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
/**
 * 内网配置工具类
 * @ClassName InnerNetworkMappingProperties
 * @description 
 * @author yinibnchen
 * @date 2019年3月22日 上午10:34:37
 */
public class InnerNetworkMappingProperties {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InnerNetworkMappingProperties.class);
	private static Properties mappings = null;
	private static String moduleInnerMapping;
	public void init(){
		if (StringUtils.isBlank(moduleInnerMapping)){
			try {
				mappings = PropertiesLoaderUtils
						.loadAllProperties("conf/common/inner-network-mapping.properties");
			} catch (IOException e) {
				LOGGER.error("加载module-domain-mapping.properties失败");
			}
		}
	}
	public static String getUrlPreffix(String moduleCode){
		if (StringUtils.isNotBlank(moduleInnerMapping)){
			try {
				Map<String,String>  mappings_temp = (Map<String,String>) JSONUtils.parse(moduleInnerMapping);
				mappings = new Properties();
				for (Map.Entry<String, String> m : mappings_temp.entrySet()){
					mappings.setProperty(m.getKey(), m.getValue());
				}
			} catch (Exception e) {
				LOGGER.error("json转换moduleInnerMapping失败");
			}
		}
	    if(mappings.containsKey(moduleCode)){
	        return mappings.getProperty(moduleCode);
	    }
	    return null;
	}
	
	@Value("${urlinnermapping:}")
	public void setModuleInnerMapping(String moduleInnerMapping) {
		this.moduleInnerMapping = moduleInnerMapping;
	}
	
}
