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
 * 登录配置文件
 * 
 * @author wushuangyang
 * @date 2016年05月31日
 */

public class ModuleDomainMappingPropertiesUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ModuleDomainMappingPropertiesUtil.class);
	private static Properties mappings = null;	
	private static String moduleMapping;
	public void init(){
		if (StringUtils.isBlank(this.moduleMapping)){
			try {
				mappings = PropertiesLoaderUtils
						.loadAllProperties("conf/common/module-domain-mapping-conf.properties");
			} catch (IOException e) {
				LOGGER.error("加载module-domain-mapping-conf.properties失败");
			}
		}
	}
	
	public static String getUrlPreffix(String moduleCode){
		if (StringUtils.isNotBlank(moduleMapping)) {
			initMappingsFromApollo();
		}
	    if(mappings.containsKey(moduleCode)){
	        return (String) mappings.get(moduleCode);
	    }
	    return null;
	}
	
	public static Properties getMappings() {
		if (StringUtils.isNotBlank(moduleMapping)) {
			initMappingsFromApollo();
		}
		return mappings;
	}

	public String getModuleMapping() {
		return moduleMapping;
	}
	@Value("${modulemapping:}")
	public void setModuleMapping(String moduleMapping) {
		this.moduleMapping = moduleMapping;
	}

	public static void initMappingsFromApollo() {
		try {
			Map<String,String> mappings_temp = (Map<String,String>) JSONUtils.parse(moduleMapping);
			mappings = new Properties();
			for (Map.Entry<String, String> m : mappings_temp.entrySet()){
				mappings.setProperty(m.getKey(), m.getValue());
			}
		} catch (Exception e) {
			LOGGER.error("json转换modulemapping失败");
		}
	}
}
