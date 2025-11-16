package com.hayden.hap.common.utils.properties;


/**
 * sy_app.properties 工具类
 * 
 * @author lengzy
 * @date 2015年12月18日
 */
public class CasPropertiesUtil {

    private static Boolean isUseCAS; // 是否使用CAS

    private static String casServerUrlPrefix; // #CAS单点登录服务器路径

//    private static String casService; // 处理CAS回调请求的URL

    /**
     * 是否使用单点登录
     * 
     * @return
     * @author lengzy
     * @date 2015年12月18日
     */
    public static Boolean getIsUseCAS() {
        return isUseCAS;
    }

    /**
     * CAS单点登录服务器路径
     * 
     * @return
     * @author lengzy
     * @date 2015年12月18日
     */
    public static String getCasServerUrlPrefix() {
        return casServerUrlPrefix;
    }

    /**
     * 处理CAS回调请求的URL
     * 
     * @return
     * @author lengzy
     * @date 2015年12月18日
     */
//    public static String getCasService() {
//        return casService;
//    }

    public void setIsUseCAS(Boolean isUseCAS) {
        CasPropertiesUtil.isUseCAS = isUseCAS;
    }

    public void setCasServerUrlPrefix(String casServerUrlPrefix) {
        CasPropertiesUtil.casServerUrlPrefix = casServerUrlPrefix;
    }

//    public void setCasService(String casService) {
//        CasPropertiesUtil.casService = casService;
//    }

}
