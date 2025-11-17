package com.hayden.hap.common.form.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormMService;
import com.hayden.hap.common.form.itf.IFormPCService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/** 
 * @ClassName: FormServiceImpl 
 * @Description: 
 * @author LUYANYING
 * @date 2015年7月1日 下午2:21:42 
 * @version V1.0   
 *  
 */
@Service("formService")
public class FormServiceImpl implements IFormService{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FormServiceImpl.class);

	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IFormPCService formPCService;
	
	@Autowired
	private IFormMService formMService;
	
	@Autowired
	private IFormItemService formItemService;
	
	private static final Set<String> NOT_MOBILE_FORM = new HashSet<>();
	static {
		NOT_MOBILE_FORM.add("SY_PERMISSON_M");
		NOT_MOBILE_FORM.add("MGR_PERMISSION_M");
	}

	@Override
	public FormVO getFormVOByFunccode(String funccode, Long tenantid) {
		FuncVO funcVO = funcService.queryAndAssertByFunccode(funccode, tenantid);
		boolean isMobile = isMobileFormcode(funcVO.getFunc_info());
		if(isMobile)
			return formMService.getFormVOByFunccode(funccode, tenantid);
		return formPCService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
	}


	@Override
	public FormVO getFormVOByFormcode(String formcode, Long tenantid) {
		boolean isMobile = isMobileFormcode(formcode);
		if(isMobile)
			return formMService.getFormVOByFormcode(formcode, tenantid);
		return formPCService.getFormVOByFormcode(formcode, tenantid);
	}

	
	
	@Override
	public IAction getActionByFuncCode(String funcCode, Long tenantid) throws HDException {
		FuncVO funcVO = funcService.queryAndAssertByFunccode(funcCode, tenantid);
		boolean isMobile = isMobileFormcode(funcVO.getFunc_info());
		if(isMobile)
			return formMService.getActionByFormCode(funcVO.getFunc_info(), tenantid);
		return formPCService.getActionByFormCode(funcVO.getFunc_info(), tenantid);
	}

	@Override
	public IAction getActionByFormCode(String formCode, Long tenantid) throws HDException {
		boolean isMobile = isMobileFormcode(formCode);
		if(isMobile)
			return formMService.getActionByFormCode(formCode, tenantid);
		return formPCService.getActionByFormCode(formCode, tenantid);
	}

	
	
	/**
	 * 是否移动端表单编码
	 * @param formcode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月7日
	 */
	private boolean isMobileFormcode(String formcode) {
		if(NOT_MOBILE_FORM.contains(formcode))
			return false;
		if(formcode.endsWith("_M"))
			return true;
		return false;
	}
}
