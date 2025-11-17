package com.hayden.hap.common.enumerate;

import java.io.Serializable;

/**
 * 枚举接口
 * @author zhangfeng
 * @date 2015年5月29日
 */
public interface IEnum extends Serializable {

	/**
	 * 获取枚举对象的code
	 * @return 
	 * @author zhangfeng
	 * @date 2015年5月29日
	 */
	String getCode();
	
	/**
	 * 获取枚举对象名称
	 * @return 
	 * @author zhangfeng
	 * @date 2015年5月29日
	 */
	String getName();
	
	EnumEntity toEntity();
}
