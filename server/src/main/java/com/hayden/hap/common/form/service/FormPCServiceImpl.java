package com.hayden.hap.common.form.service;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.form.entity.FormPCVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormPCService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncPCService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.ActionUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月24日
 */
@Service("formPCService")
public class FormPCServiceImpl implements IFormPCService {
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFuncPCService funcPCService;
	
	@Autowired
	private IFormItemService formItemService;
	
	
	@Override
	public FormPCVO getFormVOByFunccode(String funccode, Long tenantid) {
		FuncVO funcVO = funcPCService.queryByFunccode(funccode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码:"+funccode+"没有找到对应功能"+"|"+tenantid);

		IFormPCService formService = AppServiceHelper.findBean(IFormPCService.class);
		FormPCVO formVO = formService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		return formVO;
	}
	
	
	/**
	 * 根据表单编码查表单对象,此方法有缓存
	 * @param formcode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月19日
	 */
	@Override
	@Cacheable(value="SY_FORM",key="#formcode.concat('|').concat(#tenantid)")
	public FormPCVO getFormVOByFormcode(String formcode,Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("FORM_CODE", formcode);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR,tenantid);
		
		VOSet<FormPCVO> voSet = baseService.query(FormPCVO.class, dynaSqlVO);
		if(ObjectUtil.isNotEmpty(voSet.getVoList())) {
			return voSet.getVoList().get(0);
		}
		return null;
	}	
		
	@Override
	public IAction getActionByFuncCode(String funcCode, Long tenantid) throws HDException {
		FuncVO funcVO = funcPCService.queryByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(funcVO, "获取功能为空，根据功能编码："+funcCode+"|"+tenantid);
		if(StringUtils.isEmpty(funcVO.getFunc_info())) {
			return null;
		}
		IFormService formService = AppServiceHelper.findBean(IFormService.class);
		FormVO vo = formService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		ObjectUtil.validNotNull(vo, "获取表单为空，根据表单编码："+funcVO.getFunc_info()+"|"+tenantid);		
		return ActionUtils.getActionByClassName(vo.getExtends_class());
	}

	@Override
	public IAction getActionByFormCode(String formCode, Long tenantid) throws HDException {
		IFormService formService = AppServiceHelper.findBean(IFormService.class);
		FormVO vo = formService.getFormVOByFormcode(formCode, tenantid);
		return ActionUtils.getActionByClassName(vo.getExtends_class());
	}

	

}
