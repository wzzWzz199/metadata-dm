package com.hayden.hap.common.utils.properties;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModulePropertiesUtil {

    private static String moduleCode;
    private static String moduleCodeIncludeStr;
    private static String productCode;

    public void init() {
        
    }

    public static String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        ModulePropertiesUtil.moduleCode = moduleCode;
    }

    public static Set<String> getModuleCodeInclude() {
    	Set<String> moduleCodeInclude=new LinkedHashSet<String>();
    	if (StringUtils.isNotBlank(ModulePropertiesUtil.moduleCodeIncludeStr)) {
            moduleCodeInclude.addAll(Arrays.asList(ModulePropertiesUtil.moduleCodeIncludeStr.split(",")));
        }
        return moduleCodeInclude;
    }


    public static String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        ModulePropertiesUtil.productCode = productCode;
    }

    public String getModuleCodeIncludeStr() {
        return moduleCodeIncludeStr;
    }

    public void setModuleCodeIncludeStr(String moduleCodeIncludeStr) {
        ModulePropertiesUtil.moduleCodeIncludeStr = moduleCodeIncludeStr;
    }

}
