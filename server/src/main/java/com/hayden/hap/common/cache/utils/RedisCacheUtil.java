package com.hayden.hap.common.cache.utils;

import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description
 * @Author suntaiming
 * @Date 2022/4/22 14:35
 **/
public class RedisCacheUtil {
    /**
     *  针对redisCacheName编码：拼接项目标识
     * @param projectIdentify
     * @param cacheName
     * @return: java.lang.String
     * @Author: suntaiming
     * @Date: 2022/4/22 14:46
     */
    public static String encodeRedisCacheName(String projectIdentify, String cacheName){
        if(StringUtils.isBlank(projectIdentify)){
            projectIdentify = SyConstant.TENANT_HD_CODE;
        }
        return projectIdentify + "_" + cacheName;
    }

    /**
     * 针对redisCacheName解码：去掉项目标识
     * @param projectIdentify
     * @param cacheName
     * @return: java.lang.String
     * @Author: suntaiming
     * @Date: 2022/4/22 14:46
     */
    public static String decodeRedisCacheName(String projectIdentify, String cacheName){
        if(StringUtils.isBlank(projectIdentify) && cacheName.startsWith(projectIdentify)){
            String decodeStr = cacheName.substring(projectIdentify.length() + 1);
            return decodeStr;
        }

        return cacheName;
    }

}
