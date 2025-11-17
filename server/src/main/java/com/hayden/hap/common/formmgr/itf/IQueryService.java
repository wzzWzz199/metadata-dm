package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.spring.service.IService;

/**
 * 查询服务
 * @author zhangfeng
 * @date 2018年8月23日
 */
@IService("queryService")
public interface IQueryService {

	/**
	 * 解析查询条件
	 * @param queryString
	 * @return 
	 * @author zhangfeng
	 * @date 2015年9月21日
	 */
	public String parseQueryStr(String queryString, String funcCode) throws HDException;	
	
	/**
	 * 解析查询条件
	 * @param fieldVO
	 * @param oldValue
	 * @param queryMethodStr
	 * @param key
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月13日
	 */
	public String parseWhereClaus(FormItemVO fieldVO, Object oldValue,String queryMethodStr,String key);
}
