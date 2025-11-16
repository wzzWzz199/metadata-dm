package com.hayden.hap.common.form.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月24日
 */
@IService("formItemPCService")
public interface IFormItemPCService {

	/**
	 * 获取自动编号的表单字段
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月18日
	 */
	public List<FormItemPCVO> getAutoSerialNumberItem(String funcCode,Long tenantid);
	
	/**
	 * 根据表单编码查找表单字段，此方法有缓存
	 * @param formCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月19日
	 */
	public List<FormItemPCVO> getFormItemsByFormcode(String formCode,Long tenantid);

	/**
	 * 根据表单编码及业务数据动态查找表单字段
	 * @param formCode
	 * @param vo
	 * @return
	 */
	List<FormItemPCVO> getFormItemsByFormcode(String formCode, AbstractVO vo);
	
	/**
	 * 根据功能编码查找表单字段
	 * @param funcCode
	 * @param tenantid
	 * @return
	 */
	public List<FormItemPCVO> getFormItemsByFunccode(String funcCode,Long tenantid);

	/**
	 * 根据功能编码及业务数据动态查找表单字段
	 * @param funcCode
	 * @param vo
	 * @return
	 */
	List<FormItemPCVO> getFormItemsByFunccode(String funcCode,AbstractVO vo);
	
	/**
	 * 获取普通查询字段
	 * @param funccode
	 * @param parentFunccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<FormItemPCVO> getCommonQueryItems(String funccode, String parentFunccode, Long tenantid);
	
	/**
	 * 获取快速查询字段
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<FormItemPCVO> getQuickQueryItems(String funccode, Long tenantid);
	
	/**
	 * 获取表列字段
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月6日
	 */
	public List<FormItemPCVO> getGridItems(String funccode, Long tenantid);
	
	/**
	 * 根据功能编码获取列表编辑列字段
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月19日
	 */
	public List<FormItemPCVO> getListEditFitems(String funcCode, Long tenantid);

	/**
	 * 表单字段查询选择
	 * @param formParamVO
	 * @param item2FuncMap
	 * @param userid
	 * @param tenantid
	 * @return 
	 * @author liyan
	 * @throws HDException 
	 * @date 2018年3月23日
	 */
	public ReturnResult<?> queryItemSelector(FormParamVO formParamVO,
			String bizFuncCode, Long userid, Long tenantid) throws HDException;
	
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


	List<FormItemPCVO> getDynaExportFormItemsByFormCode(String formCode,AbstractVO vo);

	List<FormItemPCVO> getDynaExportFormItemsByFuncCode(String funcCode,AbstractVO vo);

	List<FormItemPCVO> getDisplayFormItemsByFormcode(String formCode,Long tenantid);
}
