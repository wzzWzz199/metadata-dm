package com.hayden.hap.common.form.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.spring.service.IService;

/** 
 * @ClassName: IFormService 
 * @Description: 
 * @author LUYANYING
 * @date 2015年7月1日 下午2:21:13 
 * @version V1.0   
 *  
 */
@IService("formService")
public interface IFormService {
	
	
	/**
	 * 根据功能编码获取表单对象
	 * @param funccode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月21日
	 */
	public FormVO getFormVOByFunccode(String funccode, Long tenantid);
	
	/**
	 * 根据表单编码查表单对象,此方法有缓存
	 * @param formcode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月19日
	 */
	public FormVO getFormVOByFormcode(String formcode, Long tenantid);
	
	
	

	
	
	/**
	 * 根据功能编码获取action
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月22日
	 */
	public IAction getActionByFuncCode(String funcCode, Long tenantid) throws HDException;
	
	/**
	 * 根据表单编码获取action
	 * @param formCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月22日
	 */
	public IAction getActionByFormCode(String formCode, Long tenantid) throws HDException;
		
	
}
