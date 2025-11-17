/**
 * Project Name:hap-sy
 * File Name:MySqlDbFunction.java
 * Package Name:com.hayden.hap.sy.db.orm.sql
 * Date:2016年5月13日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.dbop.db.orm.sql;

import com.hayden.hap.dbop.db.util.ObjectUtil;

import java.util.Date;

/**
 * ClassName:MySqlDbFunction ().<br/>
 * Date:     2016年5月13日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class MySqlDbFunction extends AbstractDbFunction implements IDbFunction {

	@Override
	public String getSyDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String to_date(Date datetime, String format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String to_date(String datetime, String format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAddDaysSql(String datetime, String colname,int days) {
		if(ObjectUtil.isNotNull(colname)){
			return "DATE_ADD('"+datetime+"', INTERVAL "+ colname +" DAY)";
		}else
		{
			return "DATE_ADD('"+datetime+"', INTERVAL "+ days +" DAY)";
		}
		
	}

	@Override
	public String getSubDaysSql(String datetime, String colname, int days) {
		if(ObjectUtil.isNotNull(colname)){
			return "date_sub('"+datetime+"', INTERVAL "+ colname +" DAY)";
		}else
		{
			return "date_sub('"+datetime+"', INTERVAL "+ days +" DAY)";
		}		
	}
	
	@Override
	public String getSubYearsSql(String datetime, String colname, int years) {
		if(ObjectUtil.isNotNull(colname)){
			return "date_sub('"+datetime+"', INTERVAL "+ colname +" YEAR)";
		}else
		{
			return "date_sub('"+datetime+"', INTERVAL "+ years +" YEAR)";
		}		
	}

}

