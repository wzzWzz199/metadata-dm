package com.hayden.hap.common.func.utils;

import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;

/**
 * 功能工具类
 * @author zhangfeng
 * @date 2018年2月9日
 */
public class FuncUtils {

	/**
	 * 是否移动端功能
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月9日
	 */
	public static boolean isMobileFunc(String funcCode) {
		return funcCode.endsWith("_M");
	}
	
	/**
	 * 是否PC功能
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月9日
	 */
	public static boolean isPCFunc(String funcCode) {
		return !isMobileFunc(funcCode);
	}
	
	/**
	 * 断言该功能是否管理租户的功能，当是管理租户的功能时，验证参数的租户id是否为空
	 * @param paramVO
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月15日
	 */
	public static boolean assertAndValidateMgrTenant(ReqParamVO paramVO, String funcCode) 
			throws HDException {
		Long sessionTenantid = CurrentEnvUtils.getTenantId();
		
		IFormService formService = AppServiceHelper.findBean(IFormService.class);
		FormVO formVO = formService.getFormVOByFunccode(funcCode, sessionTenantid);
		if(ObjectUtil.isTrue(formVO.getIs_mgr_tenant_data())) {
			if(paramVO.getTenantid()==null) 
				throw new HDException("租户参数为空异常...");
			return true;
		}
		
		paramVO.setTenantid(sessionTenantid);
		return false;
	}
}
