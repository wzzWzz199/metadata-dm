package com.hayden.hap.common.form.service;

import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemMVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormMVO;
import com.hayden.hap.common.form.itf.IFormItemMService;
import com.hayden.hap.common.form.itf.IFormMService;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkMService;
import com.hayden.hap.common.func.itf.IFuncMService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.CloneUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月24日
 */
@Service("formItemMService")
public class FormItemMServiceImpl implements IFormItemMService {

	@Autowired
	private IFormMService formMService;
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFuncMService funcMService;
	
	@Autowired
	private IFuncLinkMService funcLinkMService;
	
	@Override
	public List<FormItemMVO> getAutoSerialNumberItem(String funcCode, Long tenantid) {
		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
		List<FormItemMVO> list = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		List<FormItemMVO> result = new ArrayList<>();

		for(FormItemMVO itemVO:list) {
			if(InputTypeEnum.SERIAL_NUMBER_AUTO.getCode().equals(itemVO.getFitem_input_type())) {
				result.add(itemVO);
			}
		}

		return result;
	}

	@Override
	@Cacheable(value=CacheConstant.CACHE_FORM_ITEM_MOBILE,key="#formCode.concat('|').concat(#tenantid)")
	public List<FormItemMVO> getFormItemsByFormcode(String formCode, Long tenantid) {
		FormMVO formVO = formMService.getFormVOByFormcode(formCode, tenantid);
		ObjectUtil.validNotNull(formVO, "根据表单编码："+formCode+",查找表单vo为空。|"+tenantid);

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("FORMID", formVO.getFormid());
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("fitem_isenable", SyConstant.SY_TRUE);
		dynaSqlVO.setOrderByClause(" FITEM_ORDER asc ");

		VOSet<FormItemMVO> voSet = baseService.query(FormItemMVO.class, dynaSqlVO);
		return voSet.getVoList();
	}


	@Override
	public List<FormItemMVO> getFormItemsByFunccode(String funcCode, Long tenantid) {
		FuncVO funcVO = funcMService.queryByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码："+funcCode+",查找功能为空|"+tenantid);

		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
		return formItemService.getFormItemsByFormcode(funcVO.getFunc_info(), tenantid);
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormItemMService#getCommonQueryItems(java.lang.String, java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月24日
	 */
	@Override
	public List<FormItemMVO> getCommonQueryItems(String funccode, String parentFunccode, Long tenantid) {
		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
		List<FormItemMVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);
		List<FormItemMVO> result = new ArrayList<>();
		if(StringUtils.isEmpty(parentFunccode)) {
			for(FormItemMVO itemVO : all) {
				if(ObjectUtil.isTrue(itemVO.getFitem_com_query())) {
					result.add(itemVO);
				}
			}
		}else {
//			List<FuncLinkMVO> list = funcLinkMService.getFuncLink(parentFunccode, funccode, tenantid);
//			FuncLinkMVO funcLinkVO = list.get(0);
//			if(funcLinkVO!=null && ObjectUtil.isTrue(funcLinkVO.getLink_is_showquery())) {
//				for(FormItemMVO itemVO : all) {
//					if(ObjectUtil.isTrue(itemVO.getFitem_com_query())) {
//						result.add(itemVO);
//					}
//				}
//			}
			return new ArrayList<>();
		}
		return CloneUtils.cloneObj(result);
//		return new ArrayList<>();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormItemMService#getQuickQueryItems(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月24日
	 */
	@Override
	public List<FormItemMVO> getQuickQueryItems(String funccode, Long tenantid) {
//		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
//		List<FormItemMVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);
//		List<FormItemMVO> result = new ArrayList<>();
//		for(FormItemMVO itemVO : all) {
//			if(ObjectUtil.isTrue(itemVO.getFitem_quick_query())) {
//				result.add(itemVO);
//			}
//		}
//		return CloneUtils.cloneObj(result);
		return new ArrayList<>();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormItemMService#getGridItems(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年1月24日
	 */
	@Override
	public List<FormItemMVO> getGridItems(String funccode, Long tenantid) {
//		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
//		List<FormItemMVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);		
//		
//		List<FormItemMVO> result = new ArrayList<>();
//		for(FormItemMVO itemVO : all) {
//			if(ObjectUtil.isTrue(itemVO.getFitem_show_list())) {
//				result.add(itemVO);
//			}
//		}
//		ListSortUtil.sort(result, "fitem_column_order", "asc");
//		return CloneUtils.cloneObj(result);
		return new ArrayList<>();
	}

	/**
	 * 根据功能编码获取列表编辑列字段
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月19日
	 */
	public List<FormItemMVO> getListEditFitems(String funcCode, Long tenantid) {
//		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
//		List<FormItemMVO> fieldVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
//		
//		List<FormItemMVO> result = new ArrayList<>();
//		for(FormItemMVO fieldVO:fieldVOs) {
//			if(ObjectUtil.isTrue(fieldVO.getFitem_batch())) {
//				if(ElementTypeEnum.FILE.getCode().equals(fieldVO.getFitem_input_element())
//						|| ElementTypeEnum.IMG.getCode().equals(fieldVO.getFitem_input_element())) {
//					continue;//文件上传的字段不用更新
//				}
//				//TODO 查询选择、字典改变的其它字段	
//				result.add(fieldVO);
//			}
//		}
//		return result;
		return new ArrayList<>();
	}

	@Override
	public List<? extends FormItemVO> getRelationFuncItemsByFormCode(String funccode, Long tenantid) {
		IFormItemMService formItemService = AppServiceHelper.findBean(IFormItemMService.class);
		List<FormItemMVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);
		List<FormItemMVO> result = new ArrayList<>();
			for(FormItemMVO itemVO : all) {
				if(ObjectUtil.isNotNull(itemVO.getFitem_relation_func())) {
					result.add(itemVO);
				}
			}
		return result;
	}

	@Override
	public List<String> getListBatchEditFitemsByFunc(String funcCode, Long tenantid) {
		return new ArrayList<>();
	}

	@Override
	public List<FormItemVO> getListBatchEditFormFitemsByFunc(String funcCode, Long tenantid) {
		return new ArrayList<>();
	}
}
