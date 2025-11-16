package com.hayden.hap.common.button.itf;

import com.hayden.hap.common.button.entity.ButtonCacheVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

@IService("buttonService")
public interface IButtonService {

	/**
	 * 关联功能、表单及按钮查询，目的是查到功能与按钮的对应关系
	 * 
	 * @return
	 * @author lianghua
	 * @date 2015年11月18日
	 */
	public List<AbstractVO> getButtonListByFunc(Long tenantid);	
	
	/**
	 * 根据表单编码获取按钮
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年11月7日
	 */
	public List<? extends ButtonVO> getBtnsByFormcode(String formcode, Long tenantid);
	
	/**
	 * 根据显示状态控制，过滤掉不显示的按钮
	 * @param btnList 过滤前按钮集合
	 * @param vo 业务vo
	 * @return
	 * @throws HDException
	 * @author zhangfeng
	 * @date 2016年8月23日
	 */
	public List<ButtonCacheVO> filterByShowCtrl(List<ButtonCacheVO> btnList,
			AbstractVO vo) throws HDException;
		
	
	/**
	 * 根据只读状态过滤按钮
	 * @param buttons
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public List<ButtonCacheVO> filterByReadonly(List<ButtonCacheVO> buttons, boolean readonly) throws HDException;
}
