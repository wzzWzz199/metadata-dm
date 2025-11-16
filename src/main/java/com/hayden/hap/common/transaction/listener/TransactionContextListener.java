package com.hayden.hap.common.transaction.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 实现事务注解的配置优于声明配置。
 * 当同时满足两种配置时，以注解的配置参数为准
 * @author wangyi
 * @date 2018年3月20日
 */
@Component
public class TransactionContextListener implements
		ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//获取txAdvice配置的事务拦截器
		TransactionInterceptor interceptor = (TransactionInterceptor) event.getApplicationContext().getBean("txAdvice");
		//把注解的事务解析类放在前面
		interceptor.setTransactionAttributeSources(
                new TransactionAttributeSource[] { new AnnotationTransactionAttributeSource(), interceptor.getTransactionAttributeSource() }
        );

	}

}
