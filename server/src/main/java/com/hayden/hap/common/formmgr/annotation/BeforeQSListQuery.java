package com.hayden.hap.common.formmgr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询选择查询前操作
 * 可以获取的参数：FormParamVO formParamVO, String funcCode, String itemCode, Long tenantid
 * @author zhangfeng
 * @date 2017年10月10日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeQSListQuery {

}
