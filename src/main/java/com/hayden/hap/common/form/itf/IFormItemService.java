package com.hayden.hap.common.form.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月18日
 */
@IService("formItemService")
public interface IFormItemService {

	/**
	 * 获取自动编号的表单字段
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月18日
	 */
	public List<? extends FormItemVO> getAutoSerialNumberItem(String funcCode,Long tenantid);
	
	/**
	 * 根据表单编码查找表单字段，此方法有缓存
	 * @param formCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月19日
	 */
	public List<? extends FormItemVO> getFormItemsByFormcode(String formCode,Long tenantid);

	/**
	 * 根据表单编码及业务数据动态查找表单字段
	 * @param formCode
	 * @param vo
	 * @return
	 */
	List<? extends FormItemVO> getFormItemsByFormcode(String formCode, AbstractVO vo);
	
	/**
	 * 根据功能编码查找表单字段
	 * @param funcCode
	 * @param tenantid
	 * @return
	 */
	public List<? extends FormItemVO> getFormItemsByFunccode(String funcCode,Long tenantid);


	/**
	 * 根据功能编码及业务数据动态查找表单字段
	 * @param funcCode
	 * @param vo
	 * @return
	 */
	List<? extends FormItemVO> getFormItemsByFunccode(String funcCode,AbstractVO vo);
		
	/**
	 * 获取普通查询字段
	 * @param funccode
	 * @param parentFunccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<? extends FormItemVO> getCommonQueryItems(String funccode, String parentFunccode, Long tenantid);
	
	/**
	 * 获取快速查询字段
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<? extends FormItemVO> getQuickQueryItems(String funccode, Long tenantid);
	
	/**
	 * 获取表列字段
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月6日
	 */
	public List<? extends FormItemVO> getGridItems(String funccode, Long tenantid);
	
	/**
	 * 根据功能编码获取列表编辑列字段
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月19日
	 */
	public List<? extends FormItemVO> getListEditFitems(String funcCode, Long tenantid);
	
	/**
	 * 根据功能编码获取列表编辑列字段名
	 * 主要为列表编辑保存提供修改参数
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月30日
	 */
	public List<String> getListEditFitemNames(List<? extends FormItemVO> list);
	
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
