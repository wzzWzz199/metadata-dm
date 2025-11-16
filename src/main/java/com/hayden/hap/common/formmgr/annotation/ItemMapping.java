package com.hayden.hap.common.formmgr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询选择字段映射
 * @author zhangfeng
 * @date 2017年10月10日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemMapping {

	/**
	 * 映射的查询选择字段编码，级联的查询选择，按从底到顶的顺序用逗号隔开
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月10日
	 */
	String value();
}
