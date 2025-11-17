package com.hayden.hap.common.utils;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;

import java.lang.reflect.Method;
import java.util.UUID;

public class DbEnvUtils {
	/**
	 * 得到用户id
	 * @return
	 * @throws StokenException 
	 * @author lianghua
	 * @throws HDException 
	 * @throws NumberFormatException 
	 * @date 2016年4月25日
	 */
	public static Long getUserId() throws   HDRuntimeException{
		return (Long) getObject("getUserId");
	}
	/**
	 * 得到用户名称
	 * @return
	 * @throws StokenException 
	 * @author lianghua
	 * @throws HDException 
	 * @date 2016年4月25日
	 */
	public static String getUserName() throws  RuntimeException{
		return (String) getObject("getUserName");
	}
	
	/**
	 * 得到租户id
	 * @return
	 * @throws StokenException 
	 * @author lianghua
	 * @throws HDException 
	 * @date 2016年4月25日
	 */
	public static Long getTenantId() throws  HDRuntimeException{
		return (Long) getObject("getTenantId");
	}
	
	/**
	 * 获取相关数据缓存
	 * @param methodName
	 * @return 
	 * @author wangyi
	 * @date 2017年11月6日
	 */
	private static Object getObject(String methodName){
		return getObject(methodName, null);
	}
	/**
	 * 添加默认值，当相关工程没有引用CurrentEnvUtils类时，返回默认值。
	 * @param methodName
	 * @param defaultValue
	 * @return
	 * @throws HDRuntimeException 
	 * @author wangyi
	 * @date 2017年11月6日
	 */
	private static Object getObject(String methodName, Object defaultValue) throws HDRuntimeException{
		try {
			Class currentEnvUtils = Class.forName("com.hayden.hap.common.utils.session.CurrentEnvUtils");
			Method method = currentEnvUtils.getMethod(methodName);
			Object value = method.invoke(currentEnvUtils);
			return value;
		} catch (HDRuntimeException e){
			throw e;
		} catch (Exception e) {
			return defaultValue;
		} 
	}

	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
}
