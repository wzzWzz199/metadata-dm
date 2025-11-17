package com.hayden.hap.common.utils;

import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zhangfeng
 * @date 2016年1月20日
 */
public class ActionUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ActionUtils.class);

	public static <T extends IAction> T getActionByClass(Class<T> clazz) {
		Component component = clazz.getAnnotation(Component.class);
		if(component==null) {//如果没有spring的
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			}
		}
		String value = component.value();
		return AppServiceHelper.findBean(clazz, value);
	}
	
	@SuppressWarnings("unchecked")
	public static IAction getActionByClassName(String className) {
		if(className==null || "".equals(className))
			return null;
		Class<? extends IAction> clazz = null;
		try {
			clazz = (Class<? extends IAction>) Class.forName(className);
			return getActionByClass(clazz);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
