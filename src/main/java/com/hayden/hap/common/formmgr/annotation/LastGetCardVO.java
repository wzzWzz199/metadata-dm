package com.hayden.hap.common.formmgr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 获取卡片数据最后操作
 * 可以获取的参数：FormParamVO,CardDataVO,业务vo
 * @author zhangfeng
 * @date 2017年10月31日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LastGetCardVO {

}
