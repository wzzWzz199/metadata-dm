/**
 * Project Name:hap-sy
 * File Name:ICommonVOService.java
 * Package Name:com.hayden.hap.sy.common.itf
 * Date:2016年1月16日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.MainExtVO;

/**
 * ClassName:ICommonVOService ().<br/>
 * Date:     2016年1月16日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public interface ICommonVOService {

	/**
	 * getCardInfo:(根据功能编码获得卡片页的数据信息). <br/>
	 * date: 2015年12月23日 <br/>
	 *
	 * @author ZhangJie
	 * @param funcCode
	 * @return
	 */
	public MainExtVO getCardInfo(String funcCode,String pk,boolean getLinkedFunc);
}

