package com.hayden.hap.common.utils.table.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 表名
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public String value();
	
	public String desc() default "";
}
