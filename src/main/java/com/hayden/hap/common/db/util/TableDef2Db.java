/**
 * Project Name:hap-sy
 * File Name:DataTypeMapperForDb.java
 * Package Name:com.hayden.hap.sy.db.orm.sql
 * Date:2015年12月7日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.db.util;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:DataTypeMapperForDb ().<br/>
 * Date:     2015年12月7日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public final class TableDef2Db {

	public static String getDbTypeName(String dbType,String tableDefDataType) {
		return tableDef.get(dbType).get(tableDefDataType);
	}
	//表定义使用类型名称，根据mysql类型定义
	public static Map<String, Map<String, String>> tableDef = new HashMap<String,Map<String,String>>();
	private static Map<String, String> mysqlMap = new HashMap<String,String>();
	private static Map<String, String> oracleMap = new HashMap<String,String>();
	private static Map<String, String> dmMap = new HashMap<String,String>();
	static{
		/** mysql **/
		//数字
		mysqlMap.put(TableDefDataType.TINYINT.getTypeName(), TableDefDataType.TINYINT.getTypeName());
		mysqlMap.put(TableDefDataType.SMALLINT.getTypeName(), TableDefDataType.SMALLINT.getTypeName());
		mysqlMap.put(TableDefDataType.MEDIUMINT.getTypeName(), TableDefDataType.MEDIUMINT.getTypeName());
		mysqlMap.put(TableDefDataType.INT.getTypeName(), TableDefDataType.INT.getTypeName());
		mysqlMap.put(TableDefDataType.INTEGER.getTypeName(), TableDefDataType.INTEGER.getTypeName());
		mysqlMap.put(TableDefDataType.BIGINT.getTypeName(), TableDefDataType.BIGINT.getTypeName());
		mysqlMap.put(TableDefDataType.FLOAT.getTypeName(), TableDefDataType.FLOAT.getTypeName());
		mysqlMap.put(TableDefDataType.DOUBLE.getTypeName(), TableDefDataType.DOUBLE.getTypeName());
		mysqlMap.put(TableDefDataType.DECIMAL.getTypeName(), TableDefDataType.DECIMAL.getTypeName());
		mysqlMap.put(TableDefDataType.BIT.getTypeName(), TableDefDataType.BIT.getTypeName());
		//字符
		mysqlMap.put(TableDefDataType.CHAR.getTypeName(), TableDefDataType.CHAR.getTypeName());
		mysqlMap.put(TableDefDataType.VARCHAR.getTypeName(), TableDefDataType.VARCHAR.getTypeName());
		mysqlMap.put(TableDefDataType.TINYBLOB.getTypeName(), TableDefDataType.TINYBLOB.getTypeName());
		mysqlMap.put(TableDefDataType.TINYTEXT.getTypeName(), TableDefDataType.TINYTEXT.getTypeName());
		mysqlMap.put(TableDefDataType.BLOB.getTypeName(), TableDefDataType.BLOB.getTypeName());
		mysqlMap.put(TableDefDataType.TEXT.getTypeName(), TableDefDataType.TEXT.getTypeName());
		mysqlMap.put(TableDefDataType.MEDIUMBLOB.getTypeName(), TableDefDataType.MEDIUMBLOB.getTypeName());
		mysqlMap.put(TableDefDataType.MEDIUMTEXT.getTypeName(), TableDefDataType.MEDIUMTEXT.getTypeName());
		mysqlMap.put(TableDefDataType.LOGNGBLOB.getTypeName(), TableDefDataType.LOGNGBLOB.getTypeName());
		mysqlMap.put(TableDefDataType.LONGTEXT.getTypeName(), TableDefDataType.LONGTEXT.getTypeName());
		mysqlMap.put(TableDefDataType.VARBINARY.getTypeName(), TableDefDataType.VARBINARY.getTypeName());
		mysqlMap.put(TableDefDataType.BINARY.getTypeName(), TableDefDataType.BINARY.getTypeName());		
		//日期
		mysqlMap.put(TableDefDataType.DATE.getTypeName(), TableDefDataType.DATE.getTypeName());
		mysqlMap.put(TableDefDataType.YEAR.getTypeName(), TableDefDataType.YEAR.getTypeName());
		mysqlMap.put(TableDefDataType.DATETIME.getTypeName(), TableDefDataType.DATETIME.getTypeName());
		mysqlMap.put(TableDefDataType.TIMESTAMP.getTypeName(), TableDefDataType.TIMESTAMP.getTypeName());
		//复合
		mysqlMap.put(TableDefDataType.ENUM.getTypeName(), TableDefDataType.ENUM.getTypeName());
		mysqlMap.put(TableDefDataType.SET.getTypeName(), TableDefDataType.SET.getTypeName());
		/** oracle **/
		//数字
		oracleMap.put(TableDefDataType.TINYINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.SMALLINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.MEDIUMINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.INT.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.INTEGER.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.BIGINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.FLOAT.getTypeName(), IOracleDbTypeConstants.FLOAT);
		oracleMap.put(TableDefDataType.DOUBLE.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.DECIMAL.getTypeName(), IOracleDbTypeConstants.NUMBER);
		oracleMap.put(TableDefDataType.BIT.getTypeName(), IOracleDbTypeConstants.NUMBER);
		//字符
		oracleMap.put(TableDefDataType.CHAR.getTypeName(), IOracleDbTypeConstants.CHAR);
		oracleMap.put(TableDefDataType.VARCHAR.getTypeName(), IOracleDbTypeConstants.VARCHAR2);
		oracleMap.put(TableDefDataType.TINYBLOB.getTypeName(), IOracleDbTypeConstants.BLOB);
		oracleMap.put(TableDefDataType.TINYTEXT.getTypeName(), IOracleDbTypeConstants.CLOB);
		oracleMap.put(TableDefDataType.BLOB.getTypeName(), IOracleDbTypeConstants.BLOB);
		oracleMap.put(TableDefDataType.TEXT.getTypeName(), IOracleDbTypeConstants.CLOB);
		oracleMap.put(TableDefDataType.MEDIUMBLOB.getTypeName(), IOracleDbTypeConstants.CLOB);
		oracleMap.put(TableDefDataType.MEDIUMTEXT.getTypeName(), IOracleDbTypeConstants.BLOB);
		oracleMap.put(TableDefDataType.LOGNGBLOB.getTypeName(), IOracleDbTypeConstants.CLOB);
		oracleMap.put(TableDefDataType.LONGTEXT.getTypeName(), IOracleDbTypeConstants.CLOB);
		oracleMap.put(TableDefDataType.VARBINARY.getTypeName(), IOracleDbTypeConstants.VARCHAR);
		oracleMap.put(TableDefDataType.BINARY.getTypeName(), IOracleDbTypeConstants.CHAR);		
		//日期
		oracleMap.put(TableDefDataType.DATE.getTypeName(), IOracleDbTypeConstants.DATE);
		oracleMap.put(TableDefDataType.YEAR.getTypeName(), IOracleDbTypeConstants.DATE);
		oracleMap.put(TableDefDataType.DATETIME.getTypeName(), IOracleDbTypeConstants.DATE);
		oracleMap.put(TableDefDataType.TIMESTAMP.getTypeName(), IOracleDbTypeConstants.TIMESTAMP);
		//复合
		oracleMap.put(TableDefDataType.ENUM.getTypeName(), IOracleDbTypeConstants.NONEMAPPER);
		oracleMap.put(TableDefDataType.SET.getTypeName(), IOracleDbTypeConstants.NONEMAPPER);

        dmMap.put(TableDefDataType.TINYINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.SMALLINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.MEDIUMINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.INT.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.INTEGER.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.BIGINT.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.FLOAT.getTypeName(), IOracleDbTypeConstants.FLOAT);
        dmMap.put(TableDefDataType.DOUBLE.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.DECIMAL.getTypeName(), IOracleDbTypeConstants.NUMBER);
        dmMap.put(TableDefDataType.BIT.getTypeName(), IOracleDbTypeConstants.NUMBER);
        //字符
        dmMap.put(TableDefDataType.CHAR.getTypeName(), IOracleDbTypeConstants.CHAR);
        dmMap.put(TableDefDataType.VARCHAR.getTypeName(), IOracleDbTypeConstants.VARCHAR2);
        dmMap.put(TableDefDataType.TINYBLOB.getTypeName(), IOracleDbTypeConstants.BLOB);
        dmMap.put(TableDefDataType.TINYTEXT.getTypeName(), IOracleDbTypeConstants.CLOB);
        dmMap.put(TableDefDataType.BLOB.getTypeName(), IOracleDbTypeConstants.BLOB);
        dmMap.put(TableDefDataType.TEXT.getTypeName(), IOracleDbTypeConstants.CLOB);
        dmMap.put(TableDefDataType.MEDIUMBLOB.getTypeName(), IOracleDbTypeConstants.CLOB);
        dmMap.put(TableDefDataType.MEDIUMTEXT.getTypeName(), IOracleDbTypeConstants.BLOB);
        dmMap.put(TableDefDataType.LOGNGBLOB.getTypeName(), IOracleDbTypeConstants.CLOB);
        dmMap.put(TableDefDataType.LONGTEXT.getTypeName(), IOracleDbTypeConstants.CLOB);
        dmMap.put(TableDefDataType.VARBINARY.getTypeName(), IOracleDbTypeConstants.VARCHAR);
        dmMap.put(TableDefDataType.BINARY.getTypeName(), IOracleDbTypeConstants.CHAR);
        //日期
        dmMap.put(TableDefDataType.DATE.getTypeName(), IOracleDbTypeConstants.DATE);
        dmMap.put(TableDefDataType.YEAR.getTypeName(), IOracleDbTypeConstants.DATE);
        dmMap.put(TableDefDataType.DATETIME.getTypeName(), "DATETIME");
        dmMap.put(TableDefDataType.TIMESTAMP.getTypeName(), IOracleDbTypeConstants.TIMESTAMP);
        //复合
        dmMap.put(TableDefDataType.ENUM.getTypeName(), IOracleDbTypeConstants.NONEMAPPER);
        dmMap.put(TableDefDataType.SET.getTypeName(), IOracleDbTypeConstants.NONEMAPPER);

		/** 装入到容器 */
		tableDef.put(DBType.MYSQL.getCode(), mysqlMap);
		tableDef.put(DBType.ORACLE.getCode(), oracleMap);
		tableDef.put(DBType.DM.getCode(), dmMap);
	}
	
}

