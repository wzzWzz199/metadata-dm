package com.hayden.hap.common.formmgr.action;

import com.hayden.hap.common.cache.action.AbstractActionCacheHelper;
import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.form.entity.FormQueryVO;

/**
 * 
 * @author zhangfeng
 * @date 2016年4月1日
 */
public class FormQueryActionCacheHelper extends AbstractActionCacheHelper<FormQueryVO>{

	@Override
	protected String getCacheName() {
		return CacheConstant.CACHE_FORM_QUERY;
	}

	@Override
	protected String getCacheField() {		
		return "func_code";
	}

}
