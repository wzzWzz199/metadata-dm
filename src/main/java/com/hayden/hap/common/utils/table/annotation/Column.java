package com.hayden.hap.common.utils.table.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	/**
	 * 字段类型
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public String type();
	
	/**
	 * 字段长度
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public int length();
	
	/**
	 * 是否允许为空
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public boolean allowNull() default true;
	
	/**
	 * 是否主键
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public boolean isPK() default false;
	
	/**
	 * 整形默认值
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public int defaultIntValue() default -100;
	
	/**
	 * 字符默认值
	 * @return 
	 * @author zhangfeng
	 * @date 2015年6月10日
	 */
	public String defaultStringValue() default "";
}
