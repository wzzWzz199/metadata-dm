package com.hayden.hap.common.utils;

import com.hayden.hap.dbop.entity.AbstractVO;

/**
 * 
 * @author zhangfeng
 * @date 2016年6月2日
 */
public class OriginalInfoUtils {

	/**
	 * 保存原始属性信息到新属性，新属性名在原属性名基础上加后缀
	 */
	public static final String ORIGINAL_SUFFIX = "__old";
	
	/**
	 * 保存指定属性名的原始属性值信息
	 * @param columns
	 * @param vo 
	 * @author zhangfeng
	 * @date 2016年6月2日
	 */
	public static void saveOriginalInfo(String[] columns, AbstractVO vo) {
		if(vo==null) 
			return;
		
		for(String column : columns) {
			vo.set(column+ORIGINAL_SUFFIX, vo.get(column));
		}
	}
	
	/**
	 * 保存指定属性名的原始属性值信息
	 * @param vo
	 * @param columns 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	public static void saveOriginalInfo(AbstractVO vo, String... columns) {
		if(vo==null) 
			return;
		
		for(String column : columns) {
			vo.set(column+ORIGINAL_SUFFIX, vo.get(column));
		}
	}
	
	/**
	 * 获取指定属性名的原始属性值信息
	 * @param column
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2016年6月2日
	 */
	public static Object getOriginalInfo(String column, AbstractVO vo) {
		String propertyName = column+ORIGINAL_SUFFIX;
		if(vo.hasProperty(propertyName)) {
			return vo.get(propertyName);
		}
		return vo.get(column);
	}
}
