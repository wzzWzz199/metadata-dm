package com.hayden.hap.dbop.db.orm.typeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName: MappedTypes 
 * @Description: 自定义TypeHandler的注解，参数为Java Class，形如:@MappedJdbcTypes(java.lang.Long.Class)
 * @author LUYANYING
 * @date 2015年4月17日 下午5:18:00 
 * @version V1.0   
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MappedTypes {
	public Class<?>[] value();
}
