package com.hayden.hap.common.form.itf;

import com.hayden.hap.common.form.entity.FormItemMVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月24日
 */
@IService("formItemMService")
public interface IFormItemMService {

	/**
	 * 获取自动编号的表单字段
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月18日
	 */
	public List<FormItemMVO> getAutoSerialNumberItem(String funcCode,Long tenantid);
	
	/**
	 * 根据表单编码查找表单字段，此方法有缓存
	 * @param formCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月19日
	 */
	public List<FormItemMVO> getFormItemsByFormcode(String formCode,Long tenantid);
	
	/**
	 * 根据功能编码查找表单字段
	 * @param funcCode
	 * @param tenantid
	 * @return
	 */
	public List<FormItemMVO> getFormItemsByFunccode(String funcCode,Long tenantid);
	
	/**
	 * 获取普通查询字段
	 * @param funccode
	 * @param parentFunccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<FormItemMVO> getCommonQueryItems(String funccode, String parentFunccode, Long tenantid);
	
	/**
	 * 获取对应关联功能字段不为空的字段
	 * @param funccode
	 * @param parentFunccode
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @date 2019年1月16日
	 */
	public List<? extends FormItemVO> getRelationFuncItemsByFormCode(String funccode, Long tenantid);
	
	/**
	 * 获取快速查询字段
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<FormItemMVO> getQuickQueryItems(String funccode, Long tenantid);
	
	/**
	 * 获取表列字段
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月6日
	 */
	public List<FormItemMVO> getGridItems(String funccode, Long tenantid);
	
	/**
	 * 根据功能编码获取列表编辑列字段
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月19日
	 */
	public List<FormItemMVO> getListEditFitems(String funcCode, Long tenantid);
	
	/**
	 * 根据功能编码 查表单批量编辑字段编码
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @date 2019年3月22日
	 */
	public List<String> getListBatchEditFitemsByFunc(String funcCode, Long tenantid);
	/**
	 * 根据功能编码 查表单批量编辑字段
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @date 2019年3月22日
	 */
	public List<FormItemVO> getListBatchEditFormFitemsByFunc(String funcCode, Long tenantid);
}
