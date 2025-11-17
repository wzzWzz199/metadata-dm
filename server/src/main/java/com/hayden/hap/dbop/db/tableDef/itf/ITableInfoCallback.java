package com.hayden.hap.dbop.db.tableDef.itf;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月21日
 */
@FunctionalInterface
public interface ITableInfoCallback {

	/**
	 * 获取表名
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月21日
	 */
	public String getTableName();
}
