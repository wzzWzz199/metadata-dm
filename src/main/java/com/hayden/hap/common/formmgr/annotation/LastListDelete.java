package com.hayden.hap.common.formmgr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列表删除最后操作
 * 可以获取的参数 ：ListDataVO listDataVO,FormParamVO formParamVO,Collection<Long> primaryKeys
 * @author zhangfeng
 * @date 2018年1月10日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LastListDelete {

}
