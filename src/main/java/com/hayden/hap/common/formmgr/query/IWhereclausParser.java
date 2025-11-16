package com.hayden.hap.common.formmgr.query;

import com.hayden.hap.common.form.entity.FormItemVO;

/**
 * where条件解析器接口
 * @author zhangfeng
 * @date 2017年11月10日
 */
public interface IWhereclausParser {

	/**
	 * 解析字段查询条件
	 * @param fieldVO
	 * @param oldValue
	 * @param queryMethodStr
	 * @param key
	 * @return 
	 * @author zhangfeng
	 * @date 2017年11月10日
	 */
	String parse(FormItemVO fieldVO,Object oldValue,String queryMethodStr,String key);
}
