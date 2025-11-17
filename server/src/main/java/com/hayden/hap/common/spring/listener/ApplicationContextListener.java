package com.hayden.hap.common.spring.listener;

import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class ApplicationContextListener implements ServletContextListener {

	private static Logger logger = LoggerFactory
			.getLogger(ApplicationContextListener.class);

	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			String hostCatalinaHome = IpUtils.getCatalinaHome();
			String separatorChar = File.separator;
			String tomcatCatalinaHome = hostCatalinaHome.substring(hostCatalinaHome.lastIndexOf(separatorChar));
			ApplicationContext applicationContext = WebApplicationContextUtils
					.getWebApplicationContext(event.getServletContext());
			AppServiceHelper.setApplicationContext(applicationContext);
			AppServiceHelper.setHostCatalinaHome(hostCatalinaHome);
			AppServiceHelper.setTomcatCatalinaHome(tomcatCatalinaHome);
			AppServiceHelper.setHostIP(IpUtils.getIP());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
