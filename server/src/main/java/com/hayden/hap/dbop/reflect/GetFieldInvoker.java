package com.hayden.hap.dbop.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
/**
 * @ClassName: GetFieldInvoker
 * @Description:
 * @author LUYANYING
 * @date 2015年4月21日 下午3:36:10
 * @version V1.0
 * 
 */
public class GetFieldInvoker implements Invoker {
	private Field field;

	public GetFieldInvoker(Field field) {
		this.field = field;
	}

	public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
		return field.get(target);
	}

	public Class<?> getType() {
		return field.getType();
	}
}