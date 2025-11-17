package com.hayden.hap.common.utils;

import com.hayden.hap.dbop.entity.AbstractVO;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;


public class ListSortUtil {
	
	public static <T> void sort(List<T> targetList, final String sortField,
			final String sortMode) {		
		Collections.sort(targetList, new Comparator<T>() {
			Class<?>[] types = {};
			Object[] args = {};
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(T obj1, T obj2) {
				int retVal = 0;
				try {
					// 首字母转大写
					String newStr = sortField.substring(0, 1).toUpperCase()
							+ sortField.replaceFirst("\\w", "");
					String methodStr = "get" + newStr;

					Method method1 = obj1.getClass().getMethod(methodStr,types);
					Method method2 = obj2.getClass().getMethod(methodStr,types);
					Comparable value1 = (Comparable)method1.invoke(obj1, args);
					Comparable value2 = (Comparable)method2.invoke(obj2, args);
					if(null == value1 || null == value2){
						return retVal;
					}
					if (sortMode != null && "desc".equals(sortMode)) {
						retVal = value2.compareTo(value1); // 倒序
					} else {
						retVal = value1.compareTo(value2); // 正序
					}
				} catch (Exception e) {
					throw new RuntimeException();
				}
				return retVal;
			}
		});
	}
	
	/**
	 * 根据父code排序，父功能在前，子功能在后
	 * @param targetList
	 * @param parentidField 父功能字段，可重复
	 * @param idField 子功能字段
	 * @return 
	 * @author liyan
	 * @date 2017年7月21日
	 */
	public static <T extends AbstractVO> List<T> sortByFuncCode(List<T> targetList, 
			final String parentidField, final String idField) {
		List<T> tempList = new ArrayList<>();
		Map<Object,T> id2objMap = new HashMap<>();
		Map<Object,T> existMap = new HashMap<>();
		Map<Object,List<T>> doubleMap = new HashMap<>();
		for(T t : targetList) {
			Object id = t.get(idField);
			if(t.get(parentidField)==null) {
				tempList.add(t);
				existMap.put(id, t);
			}else if(id2objMap.get(id)!=null){
				if(null == doubleMap.get(id)){
					List<T> list = new ArrayList<>();
					doubleMap.put(id, list);
				}
				doubleMap.get(id).add(t);
			}else {
				id2objMap.put(id, t);
			}
		}
		
		for(Entry<Object,T> entry : id2objMap.entrySet()) {
			T t = entry.getValue();
			insert(tempList, id2objMap, existMap, t, parentidField, idField);
		}
		
		
		targetList.clear();
		targetList.addAll(tempList);
		
		for(int i=0;i<targetList.size();i++) {
			T t = targetList.get(i);
			Object id = t.get(idField);
			if(doubleMap.get(id)!=null) {
				for(T t1: doubleMap.get(id)){
					targetList.add(i++, t1);	
				}
			}
		}
		
		return targetList;
	}
	
	
	/**
	 * 根据父id排序，父节点在前，子节点在后
	 * @param targetList
	 * @param parentidField 
	 * @author zhangfeng
	 * @date 2017年7月10日
	 */
	public static <T extends AbstractVO> List<T> sortByParent(List<T> targetList, 
			final String parentidField, final String idField) {
		List<T> tempList = new ArrayList<>();
		Map<Object,T> id2objMap = new HashMap<>();
		Map<Object,T> existMap = new HashMap<>();
		Map<Object,T> doubleMap = new HashMap<>();
		for(T t : targetList) {
			Object id = t.get(idField);
			if(t.get(parentidField)==null) {
				tempList.add(t);
				existMap.put(id, t);
			}else if(id2objMap.get(id)!=null){
				doubleMap.put(id, t);
			}else {
				id2objMap.put(id, t);
			}
		}
		
		for(Entry<Object,T> entry : id2objMap.entrySet()) {
			T t = entry.getValue();
			insert(tempList, id2objMap, existMap, t, parentidField, idField);
		}
		
		
		targetList.clear();
		targetList.addAll(tempList);
		
		for(int i=0;i<tempList.size();i++) {
			T t = tempList.get(i);
			Object id = t.get(idField);
			T doubleEle = doubleMap.get(id);
			if(doubleEle!=null) {
				targetList.add(i, doubleEle);
			}
		}
		
		return targetList;
	}
	
	private static <T extends AbstractVO> void insert(List<T> tempList, 
			Map<Object,T> id2objMap,
			Map<Object,T> existMap, T t, 
			String parentidField, String idField) {
		Object id = t.get(idField);
		if(existMap.get(id)!=null) {
			return;
		}
		
		Object parentid = t.get(parentidField);
		T parentFormAll = id2objMap.get(parentid);
		if(parentFormAll==null) {
			tempList.add(t);
			existMap.put(t.get(idField), t);
			return;
		}
		
		T parentFormExist = existMap.get(parentid);
		if(parentFormExist!=null) {
			tempList.add(t);
			existMap.put(id, t);
			return;
		}else {
			insert(tempList, id2objMap, existMap, parentFormAll, parentidField, idField);
			tempList.add(t);
			existMap.put(id, t);
			return;
		}
	}
	
}
