package com.hayden.hap.common.spring.service;

import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.db.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


/**
 * AppServiceHelper
 * 
 * @author lengzy
 * @date 2015年3月4日
 */
public class AppServiceHelper {

	private static final Logger logger = LoggerFactory.getLogger(AppServiceHelper.class);
	
	private static ApplicationContext applicationContext;
	
	private static String hostCatalinaHome;
	//本机IP
	private static String hostIP;
	//tomcat路径
	private static String tomcatCatalinaHome;
	
	public static String getHostCatalinaHome() {
		return hostCatalinaHome;
	}

	public static void setHostCatalinaHome(String hostCatalinaHome) {
		AppServiceHelper.hostCatalinaHome = hostCatalinaHome;
	}

	public static String getHostIP() {
		return hostIP;
	}

	public static void setHostIP(String hostIP) {
		AppServiceHelper.hostIP = hostIP;
	}

	public static String getTomcatCatalinaHome() {
		return tomcatCatalinaHome;
	}

	public static void setTomcatCatalinaHome(String tomcatCatalinaHome) {
		AppServiceHelper.tomcatCatalinaHome = tomcatCatalinaHome;
	}
	
	public static void setApplicationContext(ApplicationContext appCtxIn){
		applicationContext=appCtxIn;
	}
	
	public static Object findBean(String beanId) throws HDRuntimeException {
		Object service = null;
		try {
			service = applicationContext.getBean(beanId);
		} catch (NoSuchBeanDefinitionException ex) {
			throw new HDRuntimeException("no such bean for["+beanId+"]", ex);
		} catch (BeansException ex) {
			throw new HDRuntimeException("bean exception for["+beanId+"]", ex);
		}
		return service;
	}
	
	public static <T> Map<String, T> findBeansOfType(Class<T> type) throws HDRuntimeException {
		Map<String, T> beans = null;
		try {
			beans = applicationContext.getBeansOfType(type);
		} catch (BeansException ex) {
			throw new HDRuntimeException("bean exception for["+type+"]", ex);
		}
		return beans;
	}

	public static <T> List<T> findBeans(Class<T> type) {
		Map<String, T> beans = null;
		try {
			beans = applicationContext.getBeansOfType(type);
		} catch (BeansException ex) {
			throw new HDRuntimeException("bean exception for["+type+"]", ex);
		}
		
		List<T> result = new ArrayList<>();
		for(Entry<String, T> entry : beans.entrySet()) {
			T t = entry.getValue();
			result.add(t);
		}
		
		return result;
	}
	
	public static Class<?> getType(String beanId){
		return applicationContext.getType(beanId);
	}
	
	
	public static String getMessage(String key, Object[] params, Locale locale) {
	    if(locale==null){
	        locale=new Locale("zh_CN");
	    }
	    String i18n="";
	    try {
            i18n=applicationContext.getMessage(key, params, locale);
        } catch (NoSuchMessageException e) {
            logger.error("i18n definition for ["+key+"] not found in properties file.",e);
        }
		return i18n;
	}

	public static String getMessageDirect(String key, Object[] params,
			Locale locale) {
		if (locale == null) {
			locale = new Locale("zh_CN");
		}
		return applicationContext.getMessage(key, params, locale);
	}

	@SuppressWarnings("unchecked")
	public static <T> T findBean(Class<T> clazz,String beanId) {
		return (T)findBean(beanId);
	}
	
	/**
	 * 使用此方法，你的接口必须定义IService注解，值为实现类的Service注解值
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月21日
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findBean(Class<T> clazz) {
		IService service = clazz.getAnnotation(IService.class);
		ObjectUtil.validNotNull(service, "接口没有IService注解，请添加，或使用其它findBean方法来查找");
		
		String serviceName = service.value();
		return (T)findBean(serviceName);
	}

	/**
	 * spring的原生getBean接口
	 * 没有找到或者找到多个bean都会抛出异常
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2018年8月31日
	 */
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
}
