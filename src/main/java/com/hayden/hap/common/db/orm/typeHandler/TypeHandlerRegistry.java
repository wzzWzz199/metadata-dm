package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.util.ClassSearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 
 * @ClassName: TypeHandlerRegistry
 * @Description: TypeHandler注册类 暂时不支持自定义的TypeHandler
 * @author LUYANYING
 * @date 2015年4月17日 下午5:35:39
 * @version V1.0
 * 
 */
public final class TypeHandlerRegistry {
	private static final Logger logger = LoggerFactory.getLogger(TypeHandlerRegistry.class);

	private static final Map<Class<?>, Class<?>> reversePrimitiveMap = new HashMap<Class<?>, Class<?>>() {
		private static final long serialVersionUID = 1L;
		{
			put(Byte.class, byte.class);
			put(Short.class, short.class);
			put(Integer.class, int.class);
			put(Long.class, long.class);
			put(Float.class, float.class);
			put(Double.class, double.class);
			put(Boolean.class, boolean.class);
			put(Character.class, char.class);
		}
	};

	private final Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMap = new EnumMap<JdbcType, TypeHandler<?>>(JdbcType.class);
	//一个javaType会对应多个jdbcType，TypeHandler注册到jdbcType
	private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new HashMap<Type, Map<JdbcType, TypeHandler<?>>>();
	private final TypeHandler<Object> UNKNOWN_TYPE_HANDLER = new UnknownTypeHandler(this);
	private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<Class<?>, TypeHandler<?>>();

	/**
	 * 注册基本的TypeHandler
	 */
	public TypeHandlerRegistry() {
		register(Boolean.class, new BooleanTypeHandler());
		register(boolean.class, new BooleanTypeHandler());
		register(JdbcType.BOOLEAN, new BooleanTypeHandler());
		register(JdbcType.BIT, new BooleanTypeHandler());

		register(Byte.class, new ByteTypeHandler());
		register(byte.class, new ByteTypeHandler());
		register(JdbcType.TINYINT, new ByteTypeHandler());

		register(Short.class, new ShortTypeHandler());
		register(short.class, new ShortTypeHandler());
		register(JdbcType.SMALLINT, new ShortTypeHandler());

		register(Integer.class, new IntegerTypeHandler());
		register(int.class, new IntegerTypeHandler());
		register(JdbcType.INTEGER, new IntegerTypeHandler());

		register(Long.class, new LongTypeHandler());
		register(long.class, new LongTypeHandler());

		register(Float.class, new FloatTypeHandler());
		register(float.class, new FloatTypeHandler());
		register(JdbcType.FLOAT, new FloatTypeHandler());

		register(Double.class, new DoubleTypeHandler());
		register(double.class, new DoubleTypeHandler());
		register(JdbcType.DOUBLE, new DoubleTypeHandler());

		register(String.class, new StringTypeHandler());
		register(String.class, JdbcType.CHAR, new StringTypeHandler());
		register(String.class, JdbcType.CLOB, new ClobTypeHandler());
		register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
		register(String.class, JdbcType.LONGVARCHAR, new ClobTypeHandler());
		register(String.class, JdbcType.NVARCHAR, new NStringTypeHandler());
		register(String.class, JdbcType.NCHAR, new NStringTypeHandler());
		register(String.class, JdbcType.NCLOB, new NClobTypeHandler());
		register(JdbcType.CHAR, new StringTypeHandler());
		register(JdbcType.VARCHAR, new StringTypeHandler());
		register(JdbcType.CLOB, new ClobTypeHandler());
		register(JdbcType.LONGVARCHAR, new ClobTypeHandler());
		register(JdbcType.NVARCHAR, new NStringTypeHandler());
		register(JdbcType.NCHAR, new NStringTypeHandler());
		register(JdbcType.NCLOB, new NClobTypeHandler());
		//oracleClob
		register(oracle.jdbc.OracleClob.class, JdbcType.CLOB, new ClobTypeHandler());
		//添加string处理
		register(oracle.jdbc.OracleClob.class, JdbcType.TEXT, new StringTypeHandler());
		register(oracle.sql.TIMESTAMP.class, JdbcType.TIMESTAMP, new StringTypeHandler());

		register(Object.class, JdbcType.ARRAY, new ArrayTypeHandler());
		register(JdbcType.ARRAY, new ArrayTypeHandler());

		register(BigInteger.class, new BigIntegerTypeHandler());
		register(JdbcType.BIGINT, new LongTypeHandler());

		register(BigDecimal.class, new BigDecimalTypeHandler());
		register(JdbcType.REAL, new BigDecimalTypeHandler());
		register(JdbcType.DECIMAL, new BigDecimalTypeHandler());
		register(JdbcType.NUMERIC, new BigDecimalTypeHandler());

		register(Byte[].class, new ByteObjectArrayTypeHandler());
		register(Byte[].class, JdbcType.BLOB, new BlobByteObjectArrayTypeHandler());
		register(Byte[].class, JdbcType.LONGVARBINARY, new BlobByteObjectArrayTypeHandler());
		register(byte[].class, new ByteArrayTypeHandler());
		register(byte[].class, JdbcType.BLOB, new BlobTypeHandler());
		register(byte[].class, JdbcType.LONGVARBINARY, new BlobTypeHandler());
		register(JdbcType.LONGVARBINARY, new BlobTypeHandler());
		register(JdbcType.BLOB, new BlobTypeHandler());

		register(Object.class, UNKNOWN_TYPE_HANDLER);
		register(Object.class, JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);
		register(JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);

		register(Date.class, new DateTypeHandler());
		register(Date.class, JdbcType.DATE, new DateOnlyTypeHandler());
		register(Date.class, JdbcType.TIME, new TimeOnlyTypeHandler());
		register(JdbcType.TIMESTAMP, new DateTypeHandler());
		register(JdbcType.DATE, new DateOnlyTypeHandler());
		register(JdbcType.TIME, new TimeOnlyTypeHandler());

		register(java.sql.Date.class, new SqlDateTypeHandler());
		register(java.sql.Time.class, new SqlTimeTypeHandler());
		register(java.sql.Timestamp.class, new SqlTimestampTypeHandler());

		register(Character.class, new CharacterTypeHandler());
		register(char.class, new CharacterTypeHandler());
		//添加text类型的映射
		register(String.class, JdbcType.TEXT, new ClobTypeHandler());
	}

	public boolean hasTypeHandler(Class<?> javaType) {
		return hasTypeHandler(javaType, null);
	}

	public boolean hasTypeHandler(TypeReference<?> javaTypeReference) {
		return hasTypeHandler(javaTypeReference, null);
	}

	public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
		return javaType != null && getTypeHandler((Type) javaType, jdbcType) != null;
	}

	public boolean hasTypeHandler(TypeReference<?> javaTypeReference, JdbcType jdbcType) {
		return javaTypeReference != null && getTypeHandler(javaTypeReference, jdbcType) != null;
	}

	public TypeHandler<?> getMappingTypeHandler(Class<? extends TypeHandler<?>> handlerType) {
		return allTypeHandlersMap.get(handlerType);
	}

	
	
	// <!--  getTypeHandler  --->
	public <T> TypeHandler<T> getTypeHandler(Class<T> type) {
		return getTypeHandler((Type) type, null);
	}

	public <T> TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference) {
		return getTypeHandler(javaTypeReference, null);
	}

	public TypeHandler<?> getTypeHandler(JdbcType jdbcType) {
		return jdbcTypeHandlerMap.get(jdbcType);
	}

	public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
		return getTypeHandler((Type) type, jdbcType);
	}

	public <T> TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference, JdbcType jdbcType) {
		return getTypeHandler(javaTypeReference.getRawType(), jdbcType);
	}

	private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
		Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = typeHandlerMap.get(type);
		TypeHandler<?> handler = null;
		if (jdbcHandlerMap != null) {
			handler = jdbcHandlerMap.get(jdbcType);
			if (handler == null) {
				handler = jdbcHandlerMap.get(null);
			}
		}
		if (handler == null && type != null && type instanceof Class && Enum.class.isAssignableFrom((Class<?>) type)) {
			handler = new EnumTypeHandler((Class<?>) type);
		}
		@SuppressWarnings("unchecked")
		TypeHandler<T> returned = (TypeHandler<T>) handler;
		return returned;
	}

	public TypeHandler<Object> getUnknownTypeHandler() {
		return UNKNOWN_TYPE_HANDLER;
	}
	
	
	
	// <!--  register  by typeHandler instance--->

	/**
	 * 
	 * @Title: register 
	 * @Description: 按jdbcType注册TypeHandler
	 * @param jdbcType
	 * @param handler
	 * @return void
	 * @throws
	 */
	public void register(JdbcType jdbcType, TypeHandler<?> handler) {
		jdbcTypeHandlerMap.put(jdbcType, handler);
	}
	/**
	 * 
	 * @Title: register 
	 * @Description: 只配置了typeHandler, 没有配置jdbcType 或者javaType 
	 * @param typeHandler
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T> void register(TypeHandler<T> typeHandler) {
		boolean mappedTypeFound = false;
		//在自定义typeHandler的时候，可以加上注解MappedTypes 去指定关联的javaType 因此，此处需要扫描MappedTypes注解
		MappedTypes mappedTypes = typeHandler.getClass().getAnnotation(MappedTypes.class);
		if (mappedTypes != null) {
			for (Class<?> handledType : mappedTypes.value()) {
				//进行注册并设置mappedTypeFound变量为true
				register(handledType, typeHandler);
				mappedTypeFound = true;
			}
		}
		if (!mappedTypeFound && typeHandler instanceof TypeReference) {
			try {
				TypeReference<T> typeReference = (TypeReference<T>) typeHandler;
				register(typeReference.getRawType(), typeHandler);
				mappedTypeFound = true;
			} catch (Throwable t) {
				logger.debug(t.getMessage());
			}
		}
		//最后没找到则放弃注册
		if (!mappedTypeFound) {
			register((Class<T>) null, typeHandler);
		}
	}
	/**
	 * 
	 * @Title: register 
	 * @Description: 按javaType注册typeHandler
	 * @param javaType
	 * @param typeHandler
	 * @return void
	 * @throws
	 */
	public <T> void register(Class<T> javaType, TypeHandler<? extends T> typeHandler) {
		register((Type) javaType, typeHandler);
	}

	/**
	 * 
	 * @Title: register 
	 * @Description: 按javaType注册TypeHandler，扫描TypeHandler的MappedJdbcTypes注解，如果有则按扫描到的jdbcType注册，否则注册时jdbcType为null
	 * @param javaType
	 * @param typeHandler
	 * @return void
	 * @throws
	 */
	private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
		MappedJdbcTypes mappedJdbcTypes = typeHandler.getClass().getAnnotation(MappedJdbcTypes.class);
		if (mappedJdbcTypes != null) {
			for (JdbcType handledJdbcType : mappedJdbcTypes.value()) {
				register(javaType, handledJdbcType, typeHandler);
			}
			if (mappedJdbcTypes.includeNullJdbcType()) {
				register(javaType, null, typeHandler);
			}
		} else {
			register(javaType, null, typeHandler);
		}
	}

//	public <T> void register(TypeReference<T> javaTypeReference, TypeHandler<? extends T> handler) {
//		register(javaTypeReference.getRawType(), handler);
//	}

	/**
	 * 
	 * @Title: register 
	 * @Description: 按javaType、jdbcType注册TypeHandler，如果当前添加的是属于Byte、Long等类型，将其对应的基本类型也进行注册
	 * @param javaType
	 * @param jdbcType
	 * @param handler
	 * @return void
	 * @throws
	 */
	public <T> void register(Class<T> type, JdbcType jdbcType, TypeHandler<? extends T> handler) {
		register((Type) type, jdbcType, handler);
	}

	/**
	 * 
	 * @Title: register 
	 * @Description: 按javaType、jdbcType注册TypeHandler，如果当前添加的是属于Byte、Long等类型，将其对应的基本类型也进行注册
	 * @param javaType
	 * @param jdbcType
	 * @param handler
	 * @return void
	 * @throws
	 */
	private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
		if (javaType != null) {
			Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaType);
			if (map == null) {
				map = new HashMap<JdbcType, TypeHandler<?>>();
				typeHandlerMap.put(javaType, map);
			}
			map.put(jdbcType, handler);
			
			//如果当前添加的是属于Byte、Long等类型，将其对应的基本类型也进行注册
			if (reversePrimitiveMap.containsKey(javaType)) {
				register(reversePrimitiveMap.get(javaType), jdbcType, handler);
			}
		}
		allTypeHandlersMap.put(handler.getClass(), handler);
	}
	


	// <!--  register  by typeHandler class--->
	/**
	 * 
	 * @Title: register 
	 * @Description: 构造TypeHandler实例并按typeHandler注册TypeHandler
	 * @param typeHandlerClass
	 * @return void
	 * @throws
	 */
	public void register(Class<?> typeHandlerClass) {
		boolean mappedTypeFound = false;
		MappedTypes mappedTypes = typeHandlerClass.getAnnotation(MappedTypes.class);
		if (mappedTypes != null) {
			for (Class<?> javaTypeClass : mappedTypes.value()) {
				register(javaTypeClass, typeHandlerClass);
				mappedTypeFound = true;
			}
		}
		if (!mappedTypeFound) {
			register(getInstance(null, typeHandlerClass));
		}
	}


	/**
	 * 
	 * @Title: register 
	 * @Description: 构造TypeHandler实例并按javaType注册TypeHandler
	 * @param javaTypeClass
	 * @param typeHandlerClass
	 * @return void
	 * @throws
	 */
	public void register(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
		register(javaTypeClass, getInstance(javaTypeClass, typeHandlerClass));
	}

	/**
	 * 
	 * @Title: register 
	 * @Description: 构造TypeHandler实例并按javaType、jdbcType注册TypeHandler
	 * @param javaTypeClass
	 * @param jdbcType
	 * @param typeHandlerClass
	 * @return void
	 * @throws
	 */
	public void register(Class<?> javaTypeClass, JdbcType jdbcType, Class<?> typeHandlerClass) {
		register(javaTypeClass, jdbcType, getInstance(javaTypeClass, typeHandlerClass));
	}


	/**
	 * 
	 * @Title: getInstance 
	 * @Description: 构造TypeHandler实例
	 * @param javaTypeClass
	 * @param typeHandlerClass
	 * @return
	 * @return TypeHandler<T>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
		if (javaTypeClass != null) {
			try {
				Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
				return (TypeHandler<T>) c.newInstance(javaTypeClass);
			} catch (NoSuchMethodException e) {
				logger.debug(e.getMessage());
			} catch (Exception e) {
				throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
			}
		}
		try {
			Constructor<?> c = typeHandlerClass.getConstructor();
			return (TypeHandler<T>) c.newInstance();
		} catch (Exception e) {
			throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
		}
	}

	/**
	 * 
	 * @Title: register 
	 * @Description: 根据指定的pacakge去扫描自定义的typeHander，然后注册
	 * @param packageName
	 * @return void
	 * @throws
	 */
	public void register(String packageName) {
		ClassSearchUtil<Class<?>> resolverUtil = new ClassSearchUtil<Class<?>>();
		resolverUtil.find(new ClassSearchUtil.IsAClassMatcher(TypeHandler.class), packageName);
		Set<Class<? extends Class<?>>> handlerSet = resolverUtil.getClasses();
		for (Class<?> type : handlerSet) {
			//忽略内部类、接口、抽象类
			if (!type.isAnonymousClass() && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
				register(type);
			}
		}
	}

	/**
	 * 
	 * @Title: getTypeHandlers 
	 * @Description: 返回所有TypeHandler
	 * @return
	 * @return Collection<TypeHandler<?>>
	 * @throws
	 */
	public Collection<TypeHandler<?>> getTypeHandlers() {
		return Collections.unmodifiableCollection(allTypeHandlersMap.values());
	}

}
