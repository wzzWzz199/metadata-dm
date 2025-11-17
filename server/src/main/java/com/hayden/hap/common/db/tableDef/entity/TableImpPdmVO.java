/**
 * Project Name:hap-sy
 * File Name:CommonVO.java
 * Package Name:com.hayden.hap.sy.common.entity
 * Date:2015年11月23日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.db.tableDef.entity;

import com.hayden.hap.common.common.entity.BaseVO;

import java.util.List;

/**
 * ClassName:CommonVO ().<br/>
 * Date:     2015年11月23日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class TableImpPdmVO extends BaseVO {

	private List<String> tabledata;

	public List<String> getTabledata() {
		return tabledata;
	}

	public void setTabledata(List<String> tabledata) {
		this.tabledata = tabledata;
	}

	 

	 
}

