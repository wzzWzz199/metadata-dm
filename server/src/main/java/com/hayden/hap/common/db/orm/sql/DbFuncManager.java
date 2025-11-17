/**
 * Project Name:hap-sy
 * File Name:DbFuncManager.java
 * Package Name:com.hayden.hap.sy.db.orm.sql
 * Date:2016年5月13日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.db.orm.sql;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.orm.exception.DBNotSupportedException;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.common.db.util.DBType;
import com.hayden.hap.common.spring.service.AppServiceHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:DbFuncManager ().<br/>
 * Date:     2016年5月13日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class DbFuncManager {
	private Map<String, IDbFunction> dbFuncMap = new HashMap<String, IDbFunction>();
	{
		dbFuncMap.put(DBType.MYSQL.getCode(), new MySqlDbFunction());
		dbFuncMap.put(DBType.ORACLE.getCode(), new OracleDbFunction());
	}
	
	public IDbFunction getDbFunc(String tbName) throws HDException{
		JdbcTemplateSupportDao jtsd = AppServiceHelper.findBean(JdbcTemplateSupportDao.class, "jdbcTemplateSupportDao");
		String dbType = jtsd.getDbType(tbName);
		if(dbType == null)
			throw new DBNotSupportedException("database ["+dbType+"] isn't supported");
		if(dbFuncMap.containsKey(dbType)){
			return dbFuncMap.get(dbType);
		}else
			throw new DBNotSupportedException("database ["+dbType+"] isn't supported");
	}
}

