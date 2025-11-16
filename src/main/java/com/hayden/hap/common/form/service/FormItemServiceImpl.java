package com.hayden.hap.common.form.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemMService;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.form.utils.FormUtils;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.func.utils.FuncUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月18日
 */
@Service("formItemService")
public class FormItemServiceImpl implements IFormItemService {

	@Autowired
	private IFormService formService;

	@Autowired
	private IBaseService baseService;

	@Autowired
	private IFuncService funcService;

	@Autowired
	private IFuncLinkService funcLinkService;
	
	@Autowired
	private IFormItemPCService formItemPCService;
	
	@Autowired
	private IFormItemMService formItemMService;
	
	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormItemService#getAutoSerialNumberItem(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2015年12月18日
	 */
	
	@Override
	public List<? extends FormItemVO> getAutoSerialNumberItem(String funcCode,
			Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getAutoSerialNumberItem(funcCode, tenantid);
		return formItemPCService.getAutoSerialNumberItem(funcCode, tenantid);
	}

	
	@Override
	public List<? extends FormItemVO> getFormItemsByFormcode(String formCode,
			Long tenantid) {
		if(FormUtils.isMobileForm(formCode))
			return formItemMService.getFormItemsByFormcode(formCode, tenantid);
		return formItemPCService.getFormItemsByFormcode(formCode, tenantid);
	}

	@Override
	public List<? extends FormItemVO> getFormItemsByFormcode(String formCode, AbstractVO vo) {
		if(FormUtils.isPCForm(formCode))
			return formItemPCService.getFormItemsByFormcode(formCode,vo);
		return formItemMService.getFormItemsByFormcode(formCode, vo.getLong("tenantid"));
	}


	@Override
	public List<? extends FormItemVO> getFormItemsByFunccode(String funcCode,
			Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getFormItemsByFunccode(funcCode, tenantid);
		return formItemPCService.getFormItemsByFunccode(funcCode, tenantid);
	}

	@Override
	public List<? extends FormItemVO> getFormItemsByFunccode(String funcCode, AbstractVO vo) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getFormItemsByFunccode(funcCode, vo.getLong("tenantid"));
		return formItemPCService.getFormItemsByFunccode(funcCode, vo);
	}

	@Override
	public List<? extends FormItemVO> getCommonQueryItems(String funcCode, String parentFunccode, Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getCommonQueryItems(funcCode, parentFunccode, tenantid);
		return formItemPCService.getCommonQueryItems(funcCode, parentFunccode, tenantid);
	}

	
	@Override
	public List<? extends FormItemVO> getQuickQueryItems(String funcCode, Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getQuickQueryItems(funcCode, tenantid);
		return formItemPCService.getQuickQueryItems(funcCode, tenantid);
	}

	
	@Override
	public List<? extends FormItemVO> getGridItems(String funcCode, Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getGridItems(funcCode, tenantid);
			
		return formItemPCService.getGridItems(funcCode, tenantid);
	}

	/**
	 * 根据功能编码获取列表编辑列字段
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月19日
	 */
	@Override
	public List<? extends FormItemVO> getListEditFitems(String funcCode, Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getListEditFitems(funcCode, tenantid);
		return formItemPCService.getListEditFitems(funcCode, tenantid);
	}
	
	@Override
	public List<String> getListEditFitemNames(List<? extends FormItemVO> list) {		
		List<String> result = new ArrayList<String>();		
		for(FormItemVO formItemVO : list) {
			result.add(formItemVO.getFitem_code());
		}
		return result;
	}


	@Override
	public List<String> getListBatchEditFitemsByFunc(String funcCode, Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getListBatchEditFitemsByFunc(funcCode,  tenantid);
		return formItemPCService.getListBatchEditFitemsByFunc( funcCode,  tenantid);
	}


	@Override
	public List<FormItemVO> getListBatchEditFormFitemsByFunc(String funcCode, Long tenantid) {
		if(FuncUtils.isMobileFunc(funcCode))
			return formItemMService.getListBatchEditFormFitemsByFunc(funcCode,  tenantid);
		return formItemPCService.getListBatchEditFormFitemsByFunc( funcCode,  tenantid);
	}
}
