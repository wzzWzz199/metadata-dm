package com.hayden.hap.common.formmgr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 列表批量修改保存最后操作
 * 可以获取的参数：ListDataVO listDataVO,FormParamVO formParamVO,
 * @author zhangfeng
 * @date 2019年9月30日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LastListUpdate {

}

