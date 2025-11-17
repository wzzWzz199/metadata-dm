package com.hayden.hap.dbop.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: Invoker
 * @Description:
 * @author LUYANYING
 * @date 2015年4月21日 下午3:35:04
 * @version V1.0
 * 
 */
public interface Invoker {
	Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

	Class<?> getType();
}
