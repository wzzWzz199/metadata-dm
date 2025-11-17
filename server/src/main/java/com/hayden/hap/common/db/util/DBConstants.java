package com.hayden.hap.common.db.util;

/** 
 * @ClassName: DBConstants 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月16日 下午2:55:29 
 * @version V1.0   
 *  
 */
public class DBConstants {
	/**
	 * 是否启用临时表
	 */
	public static final boolean IS_ENABLE_TEMPTABLE = true;
	
	/**
	 * SQL中in参数个数最大值
	 */
	public static final int IN_QUERY_VALUE_MAX = 1000;
	
	/**
	 * 批量操作 批次记录数
	 */
	public static final int PER_BATCH_SIZE = 8000;
	
	/**
	 * clob字段，保存数据时string的长度限制。最大为4000字节
	 */
	public static final int CLOB_STRING_LENGTH = 1300;
}
