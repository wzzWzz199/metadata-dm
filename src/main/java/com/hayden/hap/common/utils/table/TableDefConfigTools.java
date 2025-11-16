/**
 * Project Name:hap-sy
 * File Name:TableDefConfigTools.java
 * Package Name:com.hayden.hap.sy.utils.table
 * Date:2015年12月15日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.utils.table;

import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassName:TableDefConfigTools ().<br/>
 * Date:     2015年12月15日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class TableDefConfigTools {

	@Autowired
	ITableDefService tableDefService;
	
	@Autowired
	IBaseService baseService;
	
	private TableDefConfigTools()
	{
	}
	
	public static TableDefConfigTools getInstance()
	{
		return new TableDefConfigTools();
	}
	
	public static void config2TableDef(String catalog,String schema,String tableName)
	{
		TableDefConfigTools tdct = TableDefConfigTools.getInstance();
		Long pkObj = TableDefConfigTools.getInstance().tableDefService.getTableDefPKOfTable(tableName);
		if(pkObj==null)
		{
			//获得表定义信息
			//新建注册信息到TABLE_DEF
			TableDefVO vo = tdct.tableDefService.insert(tableName);
			pkObj = vo.getTabledefid();
		}
	}
	
}

