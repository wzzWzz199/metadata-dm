package com.hayden.hap.common.form.utils;

/**
 * 表单工具类
 * @author zhangfeng
 * @date 2018年2月9日
 */
public class FormUtils {

	/**
	 * 是否移动端表单
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月9日
	 */
	public static boolean isMobileForm(String formCode) {
		return formCode.endsWith("_M");
	}
	
	/**
	 * 是否PC表单
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月9日
	 */
	public static boolean isPCForm(String formCode) {
		return !isMobileForm(formCode);
	}
}
