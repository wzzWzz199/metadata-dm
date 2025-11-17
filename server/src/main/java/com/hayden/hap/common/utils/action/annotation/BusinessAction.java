package com.hayden.hap.common.utils.action.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName BusinessAction
 * @Description 业务扩展类注解
 * @Author zhangfeng
 * @Date 2020-04-13 18:27
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessAction {

    /**
     * 支持的项目（为哪个项目服务的）
     * @return
     * @author zhangfeng
     * @date 2020-04-13 18:27
     */
    String supportProject();

    /**
     * 扩展哪个类
     * @return
     */
    Class extendClass();
}
