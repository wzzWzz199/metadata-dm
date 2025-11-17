package com.hayden.hap.common.utils;

import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * 其它直接依赖common-db，不依赖common模块时，使用该类。
 * 将AppServiceHelper类设置上下文，用于后续获取bean。
 * 需要将该类在xml中注册下，在tomcat时调用初始化。
 * @author wangyi
 * @date 2017年11月6日
 */
@Component
public class ApplicationContextUtils extends WebApplicationObjectSupport {
	private static ApplicationContext applicationContext = null;

	@Override
	protected void initApplicationContext(ApplicationContext context) {
		super.initApplicationContext(context);
		if (applicationContext == null) {
			applicationContext = context;
			AppServiceHelper.setApplicationContext(applicationContext);
		}
	}
}
