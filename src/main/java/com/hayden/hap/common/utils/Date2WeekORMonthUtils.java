package com.hayden.hap.common.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * 
* Copyright: Copyright (c) 2019 北京海顿中科技术有限公司
* 
* @ClassName: Date2WeekORMonthUtils.java
* @Description: 计算当前日期是一年中的第几周，第几月
*
* @version: v3
* @author: 王振军
* @date: 2019年6月27日 上午11:51:54 
*
 */
public class Date2WeekORMonthUtils {
	
	/**
	 * 
	* @Function: Date2WeekORMonthUtils.java
	* @Description: 计算当前是第几周
	*
	* @param:@return
	* @return：int
	* @throws：异常描述
	*
	* @version: v3
	* @author: 王振军
	* @date: 2019年6月27日 下午1:18:17 
	 */
	public static int getWeekNumber(){
		Date date=new Date();
		Calendar calendar = Calendar.getInstance();
		//美国是以周日为每周的第一天 现把周一设成第一天
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * 
	* @Function: Date2WeekORMonthUtils.java
	* @Description: 计算当前是第几月
	*
	* @param:@return
	* @return：int
	* @throws：异常描述
	*
	* @version: v3
	* @author: 王振军
	* @date: 2019年6月27日 下午1:18:48 
	*
	 */
	public static int getMonthNumber() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * 
	* @Function: Date2WeekORMonthUtils.java
	* @Description: 计算当前年份
	*
	* @param:@return
	* @return：int
	* @throws：异常描述
	*
	* @version: v3
	* @author: 王振军
	* @date: 2019年6月27日 下午1:18:48 
	*
	 */
	public static int getYearNumber() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}
	
	
	public static void main(String[] args) {
		Date date=new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天

		calendar.setTime(date);

		System.out.println("当前周为"+calendar.get(Calendar.WEEK_OF_YEAR));


		Calendar cal = Calendar.getInstance();
		int m = cal.get(Calendar.MONTH) + 1;

		System.out.println("当前月份为"+m);
		
		
		System.out.println("当前年份为"+cal.get(Calendar.YEAR));
	}

}
