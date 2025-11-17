package com.hayden.hap.common.db.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 公共字段
 * 
 * @author haocs
 * @date 2019年8月27日
 */
public interface TablePublicCols {
	
	
	
	public static Set<String> publicCols = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(PUBLIC_COL_VER);
			add(PUBLIC_COL_CREATED_BY);
			add(PUBLIC_COL_CREATED_DT);
			add(PUBLIC_COL_UPDATED_BY);
			add(PUBLIC_COL_UPDATED_DT);
			add(PUBLIC_COL_DF);
		}
	};
	/**
	 * 
	* 乐观锁版本 
	 */
	public static final String PUBLIC_COL_VER = "ver";
	/**
	 * 创建者(登录帐号) 
	 */
	public static final String PUBLIC_COL_CREATED_BY = "created_by";
	/**
	 * 创建时间
	 */
	public static final String PUBLIC_COL_CREATED_DT = "created_dt";
	/**
	 * 最后更新者(登录帐号)
	 */
	public static final String PUBLIC_COL_UPDATED_BY = "updated_by";
	/**
	 * 最后更新时间
	 */
	public static final String PUBLIC_COL_UPDATED_DT = "updated_dt";
	/**
	 * 逻辑删除标识
	 */
	public static final String PUBLIC_COL_DF = "df";
	
	
	
}
