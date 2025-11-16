package com.hayden.hap.common.utils.action.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName BusinessActionItem
 * @Description 扩展操作注解
 * @Author zhangfeng
 * @Date 2020-04-13 18:27
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessActionItem {

    /**
     * 动作code
     * @return
     */
    String actionCode();
}
