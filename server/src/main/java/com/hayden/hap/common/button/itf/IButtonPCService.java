package com.hayden.hap.common.button.itf;

import com.hayden.hap.common.button.entity.ButtonPCVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年2月6日
 */
@IService("buttonPCService")
public interface IButtonPCService {

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
	public List<? extends ButtonVO> getBtnsByFormcode(String formCode, Long tenantid);
	
	/**
	 * 根据显示状态过滤
	 * @param btnList
	 * @param vo
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年5月11日
	 */
	public List<ButtonPCVO> filterByShowCtrl(List<ButtonPCVO> btnList,AbstractVO vo) throws HDException;
}
