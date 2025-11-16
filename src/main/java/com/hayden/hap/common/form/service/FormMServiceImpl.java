package com.hayden.hap.common.form.service;

import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.form.entity.FormMVO;
import com.hayden.hap.common.form.itf.IFormMService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncMService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.ActionUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 移动端表单服务
 * @author zhangfeng
 * @date 2018年1月25日
 */
@Service("formMService")
public class FormMServiceImpl implements IFormMService {
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFuncMService funcMService;
	
	@Autowired
	private IFormService formService;

	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormMService#getFormVOByFunccode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	@Override
	public FormMVO getFormVOByFunccode(String funccode, Long tenantid) {
		FuncVO funcVO = funcMService.queryByFunccode(funccode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码:"+funccode+"没有找到对应功能"+"|"+tenantid);

		IFormMService formService = AppServiceHelper.findBean(IFormMService.class);
		FormMVO formVO = formService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		return formVO;
	}


	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormMService#getFormVOByFormcode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	@Override
	@Cacheable(value=CacheConstant.CACHE_FORM_MOBILE,key="#formcode.concat('|').concat(#tenantid)")
	public FormMVO getFormVOByFormcode(String formcode, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("FORM_CODE", formcode);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR,tenantid);
		
		VOSet<FormMVO> voSet = baseService.query(FormMVO.class, dynaSqlVO);
		if(ObjectUtil.isNotEmpty(voSet.getVoList())) {
			return voSet.getVoList().get(0);
		}
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormMService#getActionByFuncCode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	@Override
	public IAction getActionByFuncCode(String funcCode, Long tenantid) throws HDException {
		FuncVO funcVO = funcMService.queryByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(funcVO, "获取功能为空，根据功能编码："+funcCode+"|"+tenantid);
		if(StringUtils.isEmpty(funcVO.getFunc_info())) {
			return null;
		}
		IFormMService formService = AppServiceHelper.findBean(IFormMService.class);
		FormMVO vo = formService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		ObjectUtil.validNotNull(vo, "获取表单为空，根据表单编码："+funcVO.getFunc_info()+"|"+tenantid);		
		return ActionUtils.getActionByClassName(vo.getExtends_class());
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormMService#getActionByFormCode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	@Override
	public IAction getActionByFormCode(String formCode, Long tenantid) throws HDException {
		IFormMService formService = AppServiceHelper.findBean(IFormMService.class);
		FormMVO vo = formService.getFormVOByFormcode(formCode, tenantid);
		return ActionUtils.getActionByClassName(vo.getExtends_class());
	}

}
