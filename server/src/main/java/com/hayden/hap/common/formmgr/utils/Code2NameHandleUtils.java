package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.formmgr.service.DictNNHandler;
import com.hayden.hap.common.formmgr.service.QuerySelectHandler;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年4月20日
 */
public class Code2NameHandleUtils {

	/**
	 * 字典无名称编码返名称
	 * @param abstractVO
	 * @param itemVO 
	 * @author zhangfeng
	 * @date 2016年4月20日
	 */
	public static AbstractVO handleDictNN(AbstractVO abstractVO,FormItemVO itemVO, Long tenantid) {
		DictNNHandler dictNNHandler = AppServiceHelper.findBean(DictNNHandler.class);
		dictNNHandler.code2Name(abstractVO, itemVO, tenantid);
		return abstractVO;
	}

	/**
	 * 字典无名称编码返名称
	 * @param list
	 * @param itemVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月31日
	 */
	public static List<? extends AbstractVO> handleDictNN(List<? extends AbstractVO> list,FormItemVO itemVO, Long tenantid) {
		DictNNHandler dictNNHandler = AppServiceHelper.findBean(DictNNHandler.class);
		dictNNHandler.code2Name(list, itemVO, tenantid);
		return list;
	}

	/**
	 * 获取字典名称
	 * @param inputConfig
	 * @param dictDataCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月12日
	 */
	public static String getDictName(String inputConfig, String dictDataCode, Long tenantid, boolean isMulti) throws HDException {
		DictNNHandler dictNNHandler = AppServiceHelper.findBean(DictNNHandler.class);
		return dictNNHandler.getDictName(inputConfig, dictDataCode, tenantid, isMulti);
	}

	/**
	 * 查询选择主键返名称
	 * @param abstractVO
	 * @param itemVO
	 * @param tenantid 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年4月20日
	 */
	public static AbstractVO handleQuerySelector(AbstractVO abstractVO,FormItemVO itemVO,Long tenantid) throws HDException {
		QuerySelectHandler querySelectHandler = AppServiceHelper.findBean(QuerySelectHandler.class);
		querySelectHandler.id2name(abstractVO, itemVO, tenantid);
		return abstractVO;
	}

	/**
	 * 查询选择返名称
	 * @param abstractVO
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月13日
	 */
	public static AbstractVO handleQuerySelector(AbstractVO abstractVO, String funcCode,Long tenantid) throws HDException {
		IFormItemService formItemService = AppServiceHelper.findBean(IFormItemService.class);
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);

		QuerySelectHandler querySelectHandler = AppServiceHelper.findBean(QuerySelectHandler.class);		
		for(FormItemVO itemVO : formItemVOs) {
			querySelectHandler.id2name(abstractVO, itemVO, tenantid);
		}
		return abstractVO;
	}

	/**
	 * 查询选择返名称
	 * @param abstractVOs
	 * @param itemVO
	 * @param tenantid 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年4月20日
	 */
	public static List<? extends AbstractVO> handleQuerySelector(List<? extends AbstractVO> abstractVOs,
			FormItemVO itemVO,Long tenantid) throws HDException {
		QuerySelectHandler querySelectHandler = AppServiceHelper.findBean(QuerySelectHandler.class);
		querySelectHandler.id2name(abstractVOs, itemVO, tenantid);
		return abstractVOs;
	}

	/**
	 * 查询选择返名称
	 * @param abstractVOs
	 * @param itemVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年12月1日
	 */
	public static List<? extends AbstractVO> handleQuerySelector(List<? extends AbstractVO> abstractVOs,
			FormItemVO itemVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		if(!SyConstant.TENANT_HD.equals(tenantid)) {
			throw new HDException("非海顿管理员不能调用此方法");
		}

		QuerySelectHandler querySelectHandler = AppServiceHelper.findBean(QuerySelectHandler.class);
		querySelectHandler.id2name(abstractVOs, itemVO, tenantid);
		return abstractVOs;
	}

	/**
	 * 查询选择返名称
	 * @param abstractVOs
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年12月1日
	 */
	public static List<? extends AbstractVO> handleQuerySelector(List<? extends AbstractVO> abstractVOs,
			String funcCode,Long tenantid) throws HDException {

		IFormItemService formItemService = AppServiceHelper.findBean(IFormItemService.class);
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);

		QuerySelectHandler querySelectHandler = AppServiceHelper.findBean(QuerySelectHandler.class);		
		for(FormItemVO itemVO : formItemVOs) {
			querySelectHandler.id2name(abstractVOs, itemVO, tenantid);
		}

		return abstractVOs;
	}

	/**
	 * 同时处理字典和查询选择
	 * @param abstractVO
	 * @param itemVO
	 * @param tenantid 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年4月20日
	 */
	public static AbstractVO handleQueryselectorAndDictNN(AbstractVO abstractVO,FormItemVO itemVO,Long tenantid) throws HDException {
		handleDictNN(abstractVO, itemVO, tenantid);
		handleQuerySelector(abstractVO, itemVO, tenantid);
		return abstractVO;
	}

	/**
	 * 同时处理字典和查询选择
	 * @param abstractVO
	 * @param funcCode
	 * @param tenantid 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年4月20日
	 */
	public static AbstractVO handleQueryselectorAndDictNN(AbstractVO abstractVO,String funcCode,Long tenantid) throws HDException {
		IFormItemService formItemService = AppServiceHelper.findBean(IFormItemService.class);
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);

		DictNNHandler dictNNHandler = AppServiceHelper.findBean(DictNNHandler.class);
		QuerySelectHandler querySelectHandler = AppServiceHelper.findBean(QuerySelectHandler.class);

		for(FormItemVO itemVO : formItemVOs) {
			dictNNHandler.code2Name(abstractVO, itemVO, tenantid);
			querySelectHandler.id2name(abstractVO, itemVO, tenantid);
		}
		return abstractVO;
	}
}
