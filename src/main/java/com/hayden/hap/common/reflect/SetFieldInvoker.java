package com.hayden.hap.common.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName: SetFieldInvoker
 * @Description:
 * @author LUYANYING
 * @date 2015年4月21日 下午3:38:09
 * @version V1.0
 * 
 */
public class SetFieldInvoker implements Invoker {
	private Field field;

	public SetFieldInvoker(Field field) {
		this.field = field;
	}

	public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
		field.set(target, args[0]);
		return null;
	}

	public Class<?> getType() {
		return field.getType();
	}
}
