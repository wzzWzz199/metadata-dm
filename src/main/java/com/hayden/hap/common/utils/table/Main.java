package com.hayden.hap.common.utils.table;

import com.hayden.hap.common.form.entity.TestDate;
import com.hayden.hap.common.utils.table.itf.IClassToTable;

public class Main {
	//视图
	private static final String VIEW = "com.hayden.hap.sy.utils.table.impl.ClassToTableForDBView";
	
	//表
	private static final String TABLE = "com.hayden.hap.sy.utils.table.impl.ClassToTableForDB";
	
	//注解
	private static final String ANNOTATION = "com.hayden.hap.sy.utils.table.impl.ClassToTableForAnnotation";

	public static void main(String[] args) throws Exception {
	
		IClassToTable classToTable = getOpera(VIEW);
		classToTable.insertTableDef(TestDate.class);
		classToTable.insertColumnTable(TestDate.class);		
	}
	
	/**
	 * 
	 * @param type 根据你想要的操作类型选择对应的常量：视图、表或注解
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月19日
	 */
	private static IClassToTable getOpera(String type) {
		try {
			Class<?> clazz = Class.forName(type);
			IClassToTable classToTable = (IClassToTable) clazz.newInstance();
			return classToTable;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
