package com.hayden.hap.common.db.util;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.reflect.ClassInfo;
import com.hayden.hap.common.reflect.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: ReflectUtil 
 * @Description: 反射工具类
 * @author LUYANYING
 * @date 2015年4月14日 上午10:19:31 
 * @version V1.0   
 *  
 */
public class ReflectUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

	/**
	 * 
	 * @Title: invokePublicMethod 
	 * @Description: 反射调用对象的public方法，包括自身的所有public方法，和从基类继承的、从接口实现的所有public方法
	 * @param entity
	 * @param claz
	 * @param methodName
	 * @param param
	 * @param paramvalue
	 * @return
	 * @throws Exception
	 * @return Object
	 * @throws
	 */
	public static Object invokePublicMethod(Object entity, Class<? extends Object> claz,String methodName,Class[] param,Object[] paramvalue) throws Exception {
		Method method = claz.getMethod(methodName,param);
        method.setAccessible(true);
        return method.invoke(entity, paramvalue);
    }
	/**
	 * 
	 * @Title: invokeDeclaredMethod 
	 * @Description: 反射调用对象的指定声明方法，类自身声明的所有方法，包含public、protected和private方法
	 * @param entity
	 * @param claz
	 * @param methodName
	 * @param param
	 * @param paramvalue
	 * @return
	 * @throws Exception
	 * @return Object
	 * @throws
	 */
	public static Object invokeDeclaredMethod(Object entity, Class<? extends Object> claz,String methodName,Class[] param,Object[] paramvalue) throws Exception {
		Method method = claz.getDeclaredMethod(methodName,param);
        method.setAccessible(true);
        return method.invoke(entity, paramvalue);
    }
	/**
	 * 
	 * @Title: getSuperClassGenricType 
	 * @Description: 获得泛型类的具体类型
	 * @param clazz
	 * @return
	 * @return Class
	 * @throws
	 */
	public static Class<?> getSuperClassGenricType(Class<? extends Object> clazz){
		try {			
			Object genericClz=clazz.getGenericSuperclass();
			
			if(genericClz instanceof ParameterizedType) {				
				return (Class<?>) ((ParameterizedType)genericClz).getActualTypeArguments()[0];
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	/**
	 * java反射bean的get方法
	 * @param objectClass
	 * @param fieldName
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月3日
	 */
	@SuppressWarnings("unchecked")     

	public static Method getGetMethod(Class objectClass, String fieldName) {     
	    StringBuffer sb = new StringBuffer();     
	    sb.append("get");     
	    sb.append(fieldName.substring(0, 1).toLowerCase());     
	    sb.append(fieldName.substring(1));     

	    try {
			return objectClass.getMethod(sb.toString());
		} catch (NoSuchMethodException e1) {
			return null;
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	    
	       

	    return null;     
	}     

	     

	/**
	 * java反射bean的set方法
	 * @param objectClass
	 * @param fieldName
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月3日
	 */
	@SuppressWarnings("unchecked")     
	public static Method getSetMethod(Class objectClass, String fieldName) {     
	    try {     
	        Class[] parameterTypes = new Class[1];     
	        Field field = objectClass.getField(fieldName);     
	        parameterTypes[0] = field.getType();     
	        StringBuffer sb = new StringBuffer();     
	        sb.append("set");     
	        sb.append(fieldName.substring(0, 1).toLowerCase());     
	        sb.append(fieldName.substring(1));     
	        Method method = objectClass.getMethod(sb.toString(), parameterTypes);     
	        return method;     
	    } catch (Exception e) {     
	    	logger.error("",e);    
	    }     
	    return null;     
	}     

	     

 
	/**
	 * 执行set方法 
	 * @param o 执行对象   
	 * @param fieldName 属性
	 * @param value 值 
	 * @author zhangfeng
	 * @date 2015年11月3日
	 */

	public static void invokeSet(Object o, String fieldName, Object value) {     
	    Method method = getSetMethod(o.getClass(), fieldName);     
	    try {     
	        method.invoke(o, new Object[] { value });     
	    } catch (Exception e) {     
	        logger.error("",e);
	    }     
	}     

	     

   
	/**
	 * 执行get方法  
	 * @param o 执行对象
	 * @param fieldName 属性
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月3日
	 */
	public static Object invokeGet(Object o, String fieldName) {     
	    Method method = getGetMethod(o.getClass(), fieldName);     
	    try {     
	        return method.invoke(o, new Object[0]);     
	    } catch (Exception e) {     
	    	logger.error("",e);     
	    }     
	    return null;     
	}
	
	/**
	 * 批量执行get方法
	 * @param objs 要执行对象集合
	 * @param fieldName 属性名
	 * @param clazz 需要的返回类型
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2015年11月3日
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> invokeGetBatch(List<?> objs,String fieldName,Class<T> clazz) throws HDException {
		if(objs==null)return null;
		if(objs.size()==0)return new ArrayList<T>();
		
		List<T> result = new ArrayList<T>();
		Method method = getGetMethod(objs.get(0).getClass(), fieldName); 
		
		Class<?> entityClass = objs.get(0).getClass();
		ClassInfo classInfo = ClassInfo.forClass(entityClass);
		String propertyName = classInfo.findPropertyName(fieldName);
		if (classInfo.hasGetter(propertyName)) {
			Invoker invoker = classInfo.getGetInvoker(propertyName);
			for(Object obj:objs) {
				try {
					T t = (T)invoker.invoke(obj, null);
					result.add(t);
				} catch (Exception e) {
					throw new HDException(e);
				}				
			}
		}else {
			try {
				method = objs.get(0).getClass().getMethod("getFromMap",String.class);
				for(Object obj:objs) {
					try {
						T t = (T)method.invoke(obj, fieldName);
						result.add(t);
					} catch (Exception e) {
						throw new HDException(e);
					}				
				}
			} catch (Exception e) {
				throw new HDException(e);
			} 			
		}
		
//		if(method==null && BaseVO.class.isAssignableFrom(entityClass)) {
//			method = getGetMethod(objs.get(0).getClass(), "getFromMap");
//			for(Object obj:objs) {
//				try {
//					T t = (T)method.invoke(obj, fieldName);
//					result.add(t);
//				} catch (Exception e) {
//					logger.error("",e);
//				}				
//			}
//		}else {
//			for(Object obj:objs) {
//				try {
//					T t = (T)method.invoke(obj, new Object[0]);
//					result.add(t);
//				} catch (Exception e) {
//					logger.error("",e);
//				}				
//			}
//		}		
		
		return result;
	}
}
