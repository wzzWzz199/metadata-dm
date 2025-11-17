package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;

import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2017年1月19日
 */
@IService("funcSelectorService")
public interface IFuncSelectorService {

	/**
	 * 获取功能选择的结构数据或者查询数据
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年1月19日
	 */
	public ReturnResult<?> getFuncSelectorMetaData(FormParamVO formParamVO, Long userid, Long tenantid, 
			Map<String,String> item2FuncMap) throws HDException;
	
	public ReturnResult<?> getFuncSelectorMetaData(FormParamVO formParamVO, Long userid, Long tenantid) 
			throws HDException;
}
