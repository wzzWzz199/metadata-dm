package com.hayden.hap.common.reflect;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ClassInfo
 * @Description:
 * @author LUYANYING
 * @date 2015年4月21日 下午2:49:27
 * @version V1.0
 * 
 */
public class ClassInfo {
	private static boolean classCacheEnabled = true;
	private static final Map<Class<?>, ClassInfo> classInfoMap = new ConcurrentHashMap<Class<?>, ClassInfo>();

	private Class<?> type;
	private Map<String, Invoker> setMethods = new HashMap<String, Invoker>();
	private Map<String, Invoker> getMethods = new HashMap<String, Invoker>();
	private Map<String, Class<?>> setTypes = new HashMap<String, Class<?>>();
	private Map<String, Class<?>> getTypes = new HashMap<String, Class<?>>();
	private Constructor<?> defaultConstructor;

	private Map<String, String> caseInsensitivePropertyMap = new HashMap<String, String>();

	private ClassInfo(Class<?> clazz) {
		type = clazz;
		addDefaultConstructor(clazz);
		addGetMethods(clazz);
		addSetMethods(clazz);
		addFields(clazz);
		String[] readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
		String[] writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
		for (String propName : readablePropertyNames) {
			caseInsensitivePropertyMap.put(propName.toLowerCase(Locale.ENGLISH), propName);
		}
		for (String propName : writeablePropertyNames) {
			caseInsensitivePropertyMap.put(propName.toLowerCase(Locale.ENGLISH), propName);
		}
	}

	private void addDefaultConstructor(Class<?> clazz) {
		Constructor<?>[] consts = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : consts) {
			if (constructor.getParameterTypes().length == 0) {
				if (canAccessPrivateMethods()) {
					try {
						constructor.setAccessible(true);
					} catch (Exception e) {
						
					}
				}
				if (constructor.isAccessible()) {
					this.defaultConstructor = constructor;
				}
			}
		}
	}

	private void addGetMethods(Class<?> cls) {
		Map<String, List<Method>> conflictingGetters = new HashMap<String, List<Method>>();
		Method[] methods = getClassMethods(cls);
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("get") && name.length() > 3) {
				if (method.getParameterTypes().length == 0) {
					name = methodToProperty(name);
					addMethodConflict(conflictingGetters, name, method);
				}
			} else if (name.startsWith("is") && name.length() > 2) {
				if (method.getParameterTypes().length == 0) {
					name = methodToProperty(name);
					addMethodConflict(conflictingGetters, name, method);
				}
			}
		}
		resolveGetterConflicts(conflictingGetters);
	}

	private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
		for (String propName : conflictingGetters.keySet()) {
			List<Method> getters = conflictingGetters.get(propName);
			Iterator<Method> iterator = getters.iterator();
			Method firstMethod = iterator.next();
			if (getters.size() == 1) {
				addGetMethod(propName, firstMethod);
			} else {
				Method getter = firstMethod;
				Class<?> getterType = firstMethod.getReturnType();
				while (iterator.hasNext()) {
					Method method = iterator.next();
					Class<?> methodType = method.getReturnType();
					if (methodType.equals(getterType)) {
						throw new ReflectionException("Illegal overloaded getter method with ambiguous type for property " + propName + " in class "
								+ firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans "
								+ "specification and can cause unpredicatble results.");
					} else if (methodType.isAssignableFrom(getterType)) {
						// OK getter type is descendant
					} else if (getterType.isAssignableFrom(methodType)) {
						getter = method;
						getterType = methodType;
					} else {
						throw new ReflectionException("Illegal overloaded getter method with ambiguous type for property " + propName + " in class "
								+ firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans "
								+ "specification and can cause unpredicatble results.");
					}
				}
				addGetMethod(propName, getter);
			}
		}
	}

	private void addGetMethod(String name, Method method) {
		if (isValidPropertyName(name)) {
			getMethods.put(name, new MethodInvoker(method));
			getTypes.put(name, method.getReturnType());
		}
	}

	private void addSetMethods(Class<?> cls) {
		Map<String, List<Method>> conflictingSetters = new HashMap<String, List<Method>>();
		Method[] methods = getClassMethods(cls);
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3) {
				if (method.getParameterTypes().length == 1) {
					name = methodToProperty(name);
					addMethodConflict(conflictingSetters, name, method);
				}
			}
		}
		resolveSetterConflicts(conflictingSetters);
	}

	private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
		List<Method> list = conflictingMethods.get(name);
		if (list == null) {
			list = new ArrayList<Method>();
			conflictingMethods.put(name, list);
		}
		list.add(method);
	}

	private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
		for (String propName : conflictingSetters.keySet()) {
			List<Method> setters = conflictingSetters.get(propName);
			Method firstMethod = setters.get(0);
			if (setters.size() == 1) {
				addSetMethod(propName, firstMethod);
			} else {
				Class<?> expectedType = getTypes.get(propName);
				if (expectedType == null) {
					throw new ReflectionException("Illegal overloaded setter method with ambiguous type for property " + propName + " in class "
							+ firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans "
							+ "specification and can cause unpredicatble results.");
				} else {
					Iterator<Method> methods = setters.iterator();
					Method setter = null;
					while (methods.hasNext()) {
						Method method = methods.next();
						if (method.getParameterTypes().length == 1 && expectedType.equals(method.getParameterTypes()[0])) {
							setter = method;
							break;
						}
					}
					if (setter == null) {
						throw new ReflectionException("Illegal overloaded setter method with ambiguous type for property " + propName + " in class "
								+ firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans "
								+ "specification and can cause unpredicatble results.");
					}
					addSetMethod(propName, setter);
				}
			}
		}
	}

	private void addSetMethod(String name, Method method) {
		if (isValidPropertyName(name)) {
			setMethods.put(name, new MethodInvoker(method));
			setTypes.put(name, method.getParameterTypes()[0]);
		}
	}

	/**
	 * 
	 * @Title: addFields 
	 * @Description: 获取类的属性信息
	 * @param clazz
	 * @return void
	 * @throws
	 */
	private void addFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (canAccessPrivateMethods()) {
				try {
					field.setAccessible(true);
				} catch (Exception e) {
				}
			}
			if (field.isAccessible()) {
				if (!setMethods.containsKey(field.getName())) {
					//忽略final static类型的属性
					int modifiers = field.getModifiers();
					if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
						addSetField(field);
					}
				}
				if (!getMethods.containsKey(field.getName())) {
					addGetField(field);
				}
			}
		}
		if (clazz.getSuperclass() != null) {
			addFields(clazz.getSuperclass());
		}
	}

	private void addSetField(Field field) {
		if (isValidPropertyName(field.getName())) {
			setMethods.put(field.getName(), new SetFieldInvoker(field));
			setTypes.put(field.getName(), field.getType());
		}
	}

	private void addGetField(Field field) {
		if (isValidPropertyName(field.getName())) {
			getMethods.put(field.getName(), new GetFieldInvoker(field));
			getTypes.put(field.getName(), field.getType());
		}
	}

	private boolean isValidPropertyName(String name) {
		return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
	}

	/**
	 * 
	 * @Title: getClassMethods 
	 * @Description: 返回类的所有方法，包括private方法
	 * @param cls
	 * @return
	 * @return Method[]
	 * @throws
	 */
	private Method[] getClassMethods(Class<?> cls) {
		HashMap<String, Method> uniqueMethods = new HashMap<String, Method>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

			//检查接口方法，因为currentClass可能是抽象类
			Class<?>[] interfaces = currentClass.getInterfaces();
			for (Class<?> anInterface : interfaces) {
				addUniqueMethods(uniqueMethods, anInterface.getMethods());
			}

			currentClass = currentClass.getSuperclass();
		}

		Collection<Method> methods = uniqueMethods.values();

		return methods.toArray(new Method[methods.size()]);
	}

	private void addUniqueMethods(HashMap<String, Method> uniqueMethods, Method[] methods) {
		for (Method currentMethod : methods) {
			if (!currentMethod.isBridge()) {
				String signature = getSignature(currentMethod);
				if (!uniqueMethods.containsKey(signature)) {
					if (canAccessPrivateMethods()) {
						try {
							currentMethod.setAccessible(true);
						} catch (Exception e) {
							
						}
					}

					uniqueMethods.put(signature, currentMethod);
				}
			}
		}
	}

	/**
	 * 
	 * @Title: getSignature 
	 * @Description: 返回方法的签名
	 * @param method
	 * @return
	 * @return String
	 * @throws
	 */
	private String getSignature(Method method) {
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		if (returnType != null) {
			sb.append(returnType.getName()).append('#');
		}
		sb.append(method.getName());
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0) {
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(parameters[i].getName());
		}
		return sb.toString();
	}

	/**
	 * 
	 * @Title: canAccessPrivateMethods 
	 * @Description: 检查是否能反射调用私有方法
	 * @return
	 * @return boolean
	 * @throws
	 */
	private static boolean canAccessPrivateMethods() {
		try {
			SecurityManager securityManager = System.getSecurityManager();
			if (null != securityManager) {
				securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
			}
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @Title: getType 
	 * @Description: 返回类
	 * @return
	 * @return Class<?>
	 * @throws
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * 
	 * @Title: getDefaultConstructor 
	 * @Description: 获取默认构造函数
	 * @return
	 * @return Constructor<?>
	 * @throws
	 */
	public Constructor<?> getDefaultConstructor() {
		if (defaultConstructor != null) {
			return defaultConstructor;
		} else {
			throw new ReflectionException("There is no default constructor for " + type);
		}
	}

	/**
	 * 
	 * @Title: getSetInvoker 
	 * @Description: 返回属性setter
	 * @param propertyName
	 * @return
	 * @return Invoker
	 * @throws
	 */
	public Invoker getSetInvoker(String propertyName) {
		Invoker method = setMethods.get(propertyName);
		if (method == null) {
			throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
		}
		return method;
	}

	/**
	 * 
	 * @Title: getGetInvoker 
	 * @Description: 返回属性getter
	 * @param propertyName
	 * @return
	 * @return Invoker
	 * @throws
	 */
	public Invoker getGetInvoker(String propertyName) {
		Invoker method = getMethods.get(propertyName);
		if (method == null) {
			throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
		}
		return method;
	}

	/**
	 * 
	 * @Title: getSetterType 
	 * @Description: 返回属性set方法参数Class
	 * @param propertyName
	 * @return
	 * @return Class<?>
	 * @throws
	 */
	public Class<?> getSetterType(String propertyName) {
		Class<?> clazz = setTypes.get(propertyName);
		if (clazz == null) {
			throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
		}
		return clazz;
	}

	/**
	 * 
	 * @Title: getGetterType 
	 * @Description: 返回属性get方法的返回值Class
	 * @param propertyName
	 * @return
	 * @return Class<?>
	 * @throws
	 */
	public Class<?> getGetterType(String propertyName) {
		Class<?> clazz = getTypes.get(propertyName);
		if (clazz == null) {
			throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
		}
		return clazz;
	}


	/**
	 * 
	 * @Title: hasSetter 
	 * @Description: 检查类是否提供设置propertyName对应属性的方法比如setter方法，如果没有setter方法，是否能尝试通过字段反射
	 * @param propertyName
	 * @return
	 * @return boolean
	 * @throws
	 */
	public boolean hasSetter(String propertyName) {
		return setMethods.keySet().contains(propertyName);
	}

	/***
	 * 
	 * @Title: hasGetter 
	 * @Description: 检查类是否提供读取propertyName对应属性的方法比如getter方法，如果没有getter方法，是否能尝试通过字段反射
	 * @param propertyName
	 * @return
	 * @return boolean
	 * @throws
	 */
	public boolean hasGetter(String propertyName) {
		return getMethods.keySet().contains(propertyName);
	}

	/**
	 * 
	 * @Title: findPropertyName 
	 * @Description: 返回类的属性名
	 * @param name
	 * @return
	 * @return String
	 * @throws
	 */
	public String findPropertyName(String name) {
		return caseInsensitivePropertyMap.get(name.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * 
	 * @Title: forClass 
	 * @Description: 获取Class的信息对象 ClassInfo实例
	 * @param clazz
	 * @return
	 * @return ClassInfo
	 * @throws
	 */
	public static ClassInfo forClass(Class<?> clazz) {
		if (classCacheEnabled) {
			ClassInfo cached = classInfoMap.get(clazz);
			if (cached == null) {
				cached = new ClassInfo(clazz);
				classInfoMap.put(clazz, cached);
			}
			return cached;
		} else {
			return new ClassInfo(clazz);
		}
	}
	/**
	 * 
	 * @Title: newInstance 
	 * @Description: 通过类的默认构造函数实例化
	 * @param clazz
	 * @return
	 * @return T
	 * @throws
	 */
	public static<T> T newInstance(Class<T> clazz) {
		try {
			return (T) ClassInfo.forClass(clazz).getDefaultConstructor().newInstance(null);
		} catch (InstantiationException e) {
			throw new ReflectionException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @Title: setClassCacheEnabled 
	 * @Description: 设置是否使用缓存类的信息
	 * @param classCacheEnabled
	 * @return void
	 * @throws
	 */
	public static void setClassCacheEnabled(boolean classCacheEnabled) {
		ClassInfo.classCacheEnabled = classCacheEnabled;
	}

	public static boolean isClassCacheEnabled() {
		return classCacheEnabled;
	}

	/**
	 * 
	 * @Title: methodToProperty 
	 * @Description: 把set、get、is等方法名转为属性名
	 * @param name
	 * @return
	 * @return String
	 * @throws
	 */
	private String methodToProperty(String name) {
		if (name.startsWith("is")) {
			name = name.substring(2);
		} else if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		} else {
			throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
		}

		if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
			name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
		}

		return name;
	}

	public Map<String, String> getCaseInsensitivePropertyMap() {
		return caseInsensitivePropertyMap;
	}
}
