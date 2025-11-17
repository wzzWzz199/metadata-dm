package com.hayden.hap.dbop.db.orm.typeHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 
 * @ClassName: TypeReference
 * @Description: 泛型指引类，获得泛型指引的类型
 * @author LUYANYING
 * @date 2015年4月17日 下午4:59:54
 * @version V1.0
 * 
 * @param <T>
 */
public abstract class TypeReference<T> {

	private final Type rawType;

	protected TypeReference() {
		rawType = getSuperclassTypeParameter(getClass());
	}

	Type getSuperclassTypeParameter(Class<?> clazz) {
		Type genericSuperclass = clazz.getGenericSuperclass();
		if (genericSuperclass instanceof Class) {
			// try to climb up the hierarchy until meet something useful
			if (TypeReference.class != genericSuperclass) {
				return getSuperclassTypeParameter(clazz.getSuperclass());
			}

			throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
					+ "Remove the extension or add a type parameter to it.");
		}

		Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
		if (rawType instanceof ParameterizedType) {
			rawType = ((ParameterizedType) rawType).getRawType();
		}

		return rawType;
	}

	public final Type getRawType() {
		return rawType;
	}

	@Override
	public String toString() {
		return rawType.toString();
	}

}
