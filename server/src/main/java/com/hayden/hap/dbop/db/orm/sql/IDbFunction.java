/**
 * Project Name:hap-sy
 * File Name:IDbFunction.java
 * Package Name:com.hayden.hap.sy.db.orm.sql
 * Date:2016年5月13日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.dbop.db.orm.sql;

import java.util.Date;

/**
 * ClassName:IDbFunction ().<br/>
 * Date:     2016年5月13日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public interface IDbFunction {

	/**
	 * getSyDate:(数据库系统时间). <br/>
	 * date: 2016年5月13日 <br/>
	 *
	 * @author ZhangJie
	 * @return
	 */
	public String getSyDate();
	
	/**
	 * to_date:(格式化时间). <br/>
	 * date: 2016年5月13日 <br/>
	 *
	 * @author ZhangJie
	 * @param datetime
	 * @param format
	 * @return
	 */
	public String to_date(Date datetime,String format);
	
	/**
	 * to_date:(格式化时间). <br/>
	 * date: 2016年5月13日 <br/>
	 *
	 * @author ZhangJie
	 * @param datetime
	 * @param format
	 * @return
	 */
	public String to_date(String datetime,String format);
	
	/**
	 * getAddDaysSql:(增加天数). <br/>
	 * date: 2016年5月13日 <br/>
	 *
	 * @author ZhangJie
	 * @param datetime
	 * @param colname
	 * @param days
	 * @return
	 */
	public String getAddDaysSql(String datetime, String colname,int days);
	/**
	 * getSubDaysSql:(减少天数). <br/>
	 * date: 2016年5月13日 <br/>
	 *
	 * @author ZhangJie
	 * @param datetime
	 * @param colname
	 * @param days
	 * @return
	 */
	public String getSubDaysSql(String datetime, String colname,int days);
	
	/**getSubDaysSql:(减少年数). <br/>
	 * @param datetime
	 * @param colname
	 * @param years
	 * @return
	 */
	public String getSubYearsSql(String datetime, String colname, int years);
}

