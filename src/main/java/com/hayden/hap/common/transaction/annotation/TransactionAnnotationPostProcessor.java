package com.hayden.hap.common.transaction.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;

/**
 * 对事务注解添加默认的回滚、不回滚异常
 * 
 * @author wangyi
 * @date 2018年3月20日
 */

@Component
public class TransactionAnnotationPostProcessor implements BeanFactoryPostProcessor{
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
			throws BeansException {
		//获取AnnotationTransactionAttributeSource的bean名称
		String name = AnnotationTransactionAttributeSource.class.getName() + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR + "0";
		BeanDefinition bd = factory.getBeanDefinition(name);
		//自定义处理事务属性类
		bd.setBeanClassName(CustomAnnotationTransactionAttributeSource.class.getName());
	}
}
