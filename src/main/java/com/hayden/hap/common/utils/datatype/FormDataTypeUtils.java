/**
 * Project Name:hap-sy
 * File Name:TableDefToForm.java
 * Package Name:com.hayden.hap.sy.db.util
 * Date:2015年12月16日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.utils.datatype;

import com.hayden.hap.common.db.util.TableDefDataType;
import com.hayden.hap.common.form.entity.FormDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:TableDefToForm (根据表定义的字段数据类型映射到).<br/>
 * Date:     2015年12月16日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class FormDataTypeUtils {
	public static Integer getDataType(String tableDefDataType) {
		return formMap.get(tableDefDataType);
	}
	//表定义使用类型名称，根据mysql类型定义
	private static Map<String, Integer> formMap = new HashMap<String,Integer>();
	static{
		/** mysql **/
		//数字
		formMap.put(TableDefDataType.TINYINT.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.SMALLINT.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.MEDIUMINT.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.INT.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.INTEGER.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.BIGINT.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.FLOAT.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.DOUBLE.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.DECIMAL.getTypeName(), FormDataType.NUM.getNum());
		formMap.put(TableDefDataType.BIT.getTypeName(), FormDataType.NUM.getNum());
		//字符
		formMap.put(TableDefDataType.CHAR.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.VARCHAR.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.TINYBLOB.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.TINYTEXT.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.BLOB.getTypeName(), FormDataType.TEXT.getNum());
		formMap.put(TableDefDataType.TEXT.getTypeName(), FormDataType.TEXT.getNum());
		formMap.put(TableDefDataType.MEDIUMBLOB.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.MEDIUMTEXT.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.LOGNGBLOB.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.LONGTEXT.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.VARBINARY.getTypeName(), FormDataType.CHAR.getNum());
		formMap.put(TableDefDataType.BINARY.getTypeName(), FormDataType.CHAR.getNum());		
		//日期
		formMap.put(TableDefDataType.DATE.getTypeName(), FormDataType.DATETIME.getNum());
		formMap.put(TableDefDataType.YEAR.getTypeName(), FormDataType.DATETIME.getNum());
		formMap.put(TableDefDataType.DATETIME.getTypeName(), FormDataType.DATETIME.getNum());
		formMap.put(TableDefDataType.TIMESTAMP.getTypeName(), FormDataType.DATETIME.getNum());
		//复合
/*		formMap.put(TableDefDataType.ENUM.getTypeName(), FormDataType);
		formMap.put(TableDefDataType.SET.getTypeName(), FormDataType);*/
	}
}

