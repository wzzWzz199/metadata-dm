package com.hayden.hap.common.utils.variable.itf;

import com.hayden.hap.common.common.exception.HDException;

import java.util.List;

/**
 * 系统变量函数接口
 * @author zhangfeng
 * @date 2018年7月9日
 */
public interface IVarFunction {

	/**
	 * 支持的函数名 
	 * @return 
	 * @author zhangfeng
	 * @date 2018年7月9日
	 */
	String supportFunction();
	
	/**
	 * 获取系统变量值
	 * @param params
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年7月9日
	 */
	Object getValue(List<String> params) throws HDException;
	
	/**
	 * 获取系统变量的字符串值
	 * @param params
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	String getStringValue(List<String> params) throws HDException;
}
