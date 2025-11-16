package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName: MappedJdbcTypes 
 * @Description: 自定义TypeHandler的注解，参数为JdbcType，形如:@MappedJdbcTypes(JdbcType.NUMERIC)
 * @author LUYANYING
 * @date 2015年4月17日 下午5:15:11 
 * @version V1.0   
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappedJdbcTypes {
	public JdbcType[] value();
	boolean includeNullJdbcType() default false;
}
