package com.hayden.hap.common.formmgr.query.strategy;


/**
 * 查询策略
 * @author zhangfeng
 * @date 2018年8月23日
 */
public interface IQueryStrategy {

	/**
	 * 
	 * @param fitem_code
	 * @param value
	 * @return 
	 * @author zhangfeng
	 * @date 2018年8月23日
	 */
	public String getQueryWhere(String fitem_code, String value) ;
	
}
