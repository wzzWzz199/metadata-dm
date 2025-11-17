/**
 * Project Name:hap-sy
 * File Name:SerialNumUtils.java
 * Package Name:com.hayden.hap.sy.utils
 * Date:2016年4月28日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.utils;

import com.hayden.hap.dbop.db.keyGen.itf.ISerialGeneratorService;
import com.hayden.hap.common.spring.service.AppServiceHelper;

/**
 * ClassName:SerialNumUtils ().<br/>
 * Date:     2016年4月28日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class SerialNumUtils {
	public static Long getAutoNum(String serialCode){
		ISerialGeneratorService serialGeneratorService = AppServiceHelper.findBean(ISerialGeneratorService.class, "serialGeneratorService");
		return Long.parseLong(serialGeneratorService.generate(serialCode));
	}
}

