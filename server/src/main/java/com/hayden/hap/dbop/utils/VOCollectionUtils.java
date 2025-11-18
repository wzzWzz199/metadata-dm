package com.hayden.hap.dbop.utils;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.dbop.reflect.ClassInfo;
import com.hayden.hap.dbop.reflect.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 
 * @author zhangfeng
 * @date 2018年2月2日
 */
public class VOCollectionUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VOCollectionUtils.class);

	/**
	 * 获取vo集合的某个属性组成的集合
	 * @param list
	 * @param prop
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月2日
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getPropList(List<? extends AbstractVO> list, String prop, Class<T> clazz) {
		List<T> result = new ArrayList<>();
		
		if(ObjectUtil.isEmpty(list))
			return result;
		
		Class<? extends AbstractVO> cla = list.get(0).getClass();		
		ClassInfo classInfo = ClassInfo.forClass(cla);
		String propertyName = classInfo.findPropertyName(prop.toLowerCase());
		if (classInfo.hasGetter(propertyName)) {
			Invoker invoker = classInfo.getGetInvoker(propertyName);
			for(AbstractVO vo : list) {
				T value;
				try {
					value = (T)invoker.invoke(vo,null);
					
				} catch (IllegalAccessException | InvocationTargetException e) {
					value = (T)vo.getFromColumnValues(prop);
				}
				result.add(value);
			}
			return result;
		}else {
			for(AbstractVO vo : list) {
				T value = (T)vo.getFromColumnValues(prop);
				result.add(value);
			}
		}		
		return result;
	}
	/**
	 * 获取vo集合的某个属性组成的集合
	 * @param list
	 * @param prop
	 * @param clazz
	 * @return 
	 * @author haocs
	 * @date 2020年4月7日
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getPropSet(List<? extends AbstractVO> list, String prop, Class<T> clazz) {
		Set<T> result = new HashSet<>();
		
		if(ObjectUtil.isEmpty(list))
			return result;
		
		Class<? extends AbstractVO> cla = list.get(0).getClass();		
		ClassInfo classInfo = ClassInfo.forClass(cla);
		String propertyName = classInfo.findPropertyName(prop.toLowerCase());
		if (classInfo.hasGetter(propertyName)) {
			Invoker invoker = classInfo.getGetInvoker(propertyName);
			for(AbstractVO vo : list) {
				T value;
				try {
					value = (T)invoker.invoke(vo,null);
					
				} catch (IllegalAccessException | InvocationTargetException e) {
					value = (T)vo.getFromColumnValues(prop);
				}
				result.add(value);
			}
			return result;
		}else {
			for(AbstractVO vo : list) {
				T value = (T)vo.getFromColumnValues(prop);
				result.add(value);
			}
		}		
		return result;
	}
	
	public static <T> List<T> getPropListNotNull(List<? extends AbstractVO> list, String prop, Class<T> clazz) {
		List<T> result = new ArrayList<>();
		
		if(ObjectUtil.isEmpty(list))
			return result;
		
		Class<? extends AbstractVO> cla = list.get(0).getClass();		
		ClassInfo classInfo = ClassInfo.forClass(cla);
		String propertyName = classInfo.findPropertyName(prop.toLowerCase());
		if (classInfo.hasGetter(propertyName)) {
			Invoker invoker = classInfo.getGetInvoker(propertyName);
			for(AbstractVO vo : list) {
				T value;
				try {
					value = (T)invoker.invoke(vo,null);
					
				} catch (IllegalAccessException | InvocationTargetException e) {
					value = (T)vo.getFromColumnValues(prop);
				}
				if(value==null)
					continue;
				result.add(value);
			}
			return result;
		}else {
			for(AbstractVO vo : list) {
				T value = (T)vo.getFromColumnValues(prop);
				if(value==null)
					continue;
				result.add(value);
			}
		}		
		return result;
	}
	
	/**
	 * 按属性分组
	 * @param list
	 * @param prop
	 * @param clazz
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月2日
	 */
	@SuppressWarnings("unchecked")
	public static <K,V extends AbstractVO> Map<K,V> groupedByProp(List<V> list, String prop, Class<K> clazz) {
		Map<K,V> result = new LinkedHashMap<>();
		
		if(ObjectUtil.isEmpty(list))
			return result;
		
		for(V vo : list) {			
			K value = (K)vo.get(prop);
			//强转为指定类型
			if (clazz.equals(Long.class)){
				value = (K)vo.getLong(prop);
			}
			result.put(value, vo);
		}
		return result;
	}
	/**
	 * 
	 * @param list
	 * @param prop
	 * @param clazz
	 * @return 
	 * @author haocs
	 * @date 2019年4月18日
	 */
	@SuppressWarnings("unchecked")
	public static <K,V extends AbstractVO> Map<K,List<V>> groupedListByProp(List<V> list, String prop, Class<K> clazz) {
		Map<K,List<V>> result = new HashMap<>();
		
		if(ObjectUtil.isEmpty(list))
			return result;
		
		for(V vo : list) {			
			K value = (K)vo.get(prop);
			List<V> valueList = result.get(value);
			if(valueList==null) {
				valueList = new ArrayList<>();
				result.put(value, valueList);
			}
			valueList.add(vo);
		}
		return result;
	}
}
