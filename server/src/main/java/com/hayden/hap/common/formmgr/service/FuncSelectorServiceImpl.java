package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.itf.IFuncSelectorService;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.formmgr.itf.IMetaDataService;
import com.hayden.hap.common.formmgr.itf.IQuerySelectorService;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.func.itf.IFuncService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2017年1月19日
 */
@Service("funcSelectorService")
public class FuncSelectorServiceImpl implements IFuncSelectorService {

	@Autowired
	private IMetaDataService metaDataService;
	
	@Autowired
	private IListFormService listFormService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IQuerySelectorService querySelectorService;
	
	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IFuncSelectorService#getFuncSelectorMetaData(com.hayden.hap.common.formmgr.entity.FormParamVO, java.lang.Long, java.lang.Long)
	 * @author zhangfeng
	 * @date 2017年1月19日
	 */
	@Override
	public ReturnResult<?> getFuncSelectorMetaData(FormParamVO formParamVO, Long userid, Long tenantid, 
			Map<String,String> item2FuncMap) throws HDException {
		if(formParamVO.getFitemCode()==null) {
			String fitemCode = formParamVO.getRequest().getParameter("fitemCode");
			formParamVO.setFitemCode(fitemCode);
		}
		HttpServletRequest request = formParamVO.getRequest();
		if(request != null) {
			String extWhere = request.getParameter("extWhere");
			if(StringUtils.isNotEmpty(extWhere)&& StringUtils.isEmpty(formParamVO.getExtWhere())) {
				formParamVO.setExtWhere(extWhere);
			}
		}
		
		String funccodeSource = getSourceFunccode(request);
		formParamVO.setQuerySelector(true);
		formParamVO.setFuncCodeSource(funccodeSource);
		
		return querySelectorService.querySelector(formParamVO, item2FuncMap, userid, tenantid);
	}
	
	@Override
	public ReturnResult<?> getFuncSelectorMetaData(FormParamVO formParamVO, Long userid, Long tenantid) throws HDException {
		HttpServletRequest request = formParamVO.getRequest();
		String btnCode = getBtncode(request);
		String funccodeSource = getSourceFunccode(request);
		Map<String,String> item2FuncMap = new HashMap<>();
		item2FuncMap.put(btnCode, formParamVO.getFuncCode());
		
		formParamVO.setQuerySelector(true);
		formParamVO.setFuncCodeSource(funccodeSource);
		
		return getFuncSelectorMetaData(formParamVO, userid, tenantid, item2FuncMap);
		
	}
	
	/**
	 * 获取字段编码
	 * @param request
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月28日
	 */
	private String getBtncode(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String[] arr = uri.split("/");
		String btnCode = arr[arr.length-1];
		return btnCode;
	}
	
	/**
	 * 获取来源功能编码
	 * @param request
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月28日
	 */
	private String getSourceFunccode(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String[] arr = uri.split("/");
		String btnCode = arr[arr.length-2];
		return btnCode;
	}
}
