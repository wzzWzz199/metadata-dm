/**
 * Project Name:hap-sy
 * File Name:TableDefDataType.java
 * Package Name:com.hayden.hap.sy.db.util
 * Date:2015年12月7日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.db.util;


/**
 * ClassName:TableDefDataType (表定义数据类型).<br/>
 * Date:     2015年12月7日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public enum TableDefDataType {
	//数字类型
	TINYINT(100,"TINYINT"),
	SMALLINT(101,"SMALLINT"),
	MEDIUMINT(102,"MEDIUMINT"),
	INT(103,"INT"),
	INTEGER(104,"INTEGER"),
	BIGINT(105,"BIGINT"),
	FLOAT(106,"FLOAT"),
	DOUBLE(107,"DOUBLE"),
	DECIMAL(108,"DECIMAL"),
	BIT(109,"BIT"),
	//字符类型
	CHAR(200,"CHAR"),       
	VARCHAR(201,"VARCHAR"),    
	TINYBLOB(202,"TINYBLOB"),   
	TINYTEXT(203,"TINYTEXT"),     
	BLOB(204,"BLOB"),         
	TEXT(205,"TEXT"),         
	MEDIUMBLOB(206,"MEDIUMBLOB"),   
	MEDIUMTEXT(207,"MEDIUMTEXT"),   
	LOGNGBLOB(208,"LOGNGBLOB"),    
	LONGTEXT(209,"LONGTEXT"),    
	VARBINARY(210,"VARBINARY"),
	BINARY(211,"BINARY"),
	//日期类型
	DATE(300,"DATE"),
	TIME(301,"TIME"),
	YEAR(302,"YEAR"),
	DATETIME(303,"DATETIME"),
	TIMESTAMP(304,"TIMESTAMP"),
	//复合类型
	ENUM(400,"ENUM"),
	SET(401,"SET");
	private final int typeCode;
	private final String typeName;
	
	public int getTypeCode() {
		return typeCode;
	}

	public String getTypeName() {
		return typeName;
	}
	private TableDefDataType(int typeCode,String typeName){
		this.typeCode = typeCode;
		this.typeName = typeName;
	};
}

