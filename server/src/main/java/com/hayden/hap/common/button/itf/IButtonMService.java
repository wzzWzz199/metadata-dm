package com.hayden.hap.common.button.itf;

import com.hayden.hap.common.button.entity.ButtonMVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author zhangfeng
 * @date 2018年2月6日
 */
@IService("buttonMService")
public interface IButtonMService {

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
	 * 
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @author zhangfeng
	 * @date 2016年11月7日
	 */
	public List<? extends ButtonVO> getBtnsByFormcode(String formcode, Long tenantid);

	/**
	 * 根据显示状态过滤
	 * 
	 * @param btnList
	 * @param vo
	 * @return
	 * @throws HDException
	 * @author zhangfeng
	 * @date 2018年5月11日
	 */
	public List<ButtonMVO> filterByShowCtrl(List<ButtonMVO> btnList, AbstractVO vo) throws HDException;

	/**
	 * 根据功能编码获取表单按钮，返回 ButtonMVO基类的属性字典中增加属性 func_code、modulecode
	 * 
	 * @param btnList
	 * @param vo
	 * @return
	 * @throws HDException
	 * @author zhenjianting
	 * @date 2018年5月12日
	 */
	public List<ButtonMVO> getButtonListByFuncCodes(Set<String> funcCodes, Long tenantid);
	
	/**
	 * 跟按钮编码获取编码
	 * @param funcCode
	 * @param btnCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2019年1月30日
	 */
	public ButtonVO getBtnByCode(String funcCode, String btnCode, Long tenantid) throws HDException;
}
