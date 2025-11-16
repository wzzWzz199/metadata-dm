package com.hayden.hap.common.button.service;

import com.hayden.hap.common.button.entity.ButtonCacheVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.button.itf.IButtonService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.ButtonTypeEnum;
import com.hayden.hap.common.enumerate.OperaGroupEnum;
import com.hayden.hap.common.utils.VariableUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

@Service("buttonService")
public class ButtonServiceImpl implements IButtonService {

	private static final Logger logger = LoggerFactory.getLogger(ButtonServiceImpl.class);

	/**
	 * 
	 *
	 * @see com.hayden.hap.common.button.itf.IButtonService#getButtonListByFunc()
	 * @author lianghua
	 * @date 2015年11月18日
	 */
	@Override
	public List<AbstractVO> getButtonListByFunc(Long tenantid) {
		throw new HDRuntimeException("请调用指定的客户端类型服务");
	}
	
	/**
	 * 根据显示状态控制，过滤掉不显示的按钮
	 *
	 * @see com.hayden.hap.common.button.itf.IButtonService#filterByShowCtrl(java.util.List, com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @date 2016年8月23日
	 */
	@Override
	public List<ButtonCacheVO> filterByShowCtrl(List<ButtonCacheVO> btnList,AbstractVO vo) throws HDException {
		List<ButtonCacheVO> result = new ArrayList<>();
		
		for(ButtonCacheVO btnVO : btnList) {
			String btnShowControl = btnVO.getBtn_show_control();
			if(StringUtils.isEmpty(btnShowControl)) {
				result.add(btnVO);
			}else {				
				boolean validateResult;
				try {
					validateResult = VariableUtils.replaceAllParamAndEval(btnShowControl, vo);
				} catch (ScriptException e) {
					logger.error(e.getMessage(), e);
					throw new HDException("按钮显示状态控制表达式错误："+btnShowControl);
				}
				if(validateResult) {
					result.add(btnVO);
				}	
			}
		}
		
		return result;
	}

	/**
	 * 根据表单编码获取表单按钮
	 *
	 * @see com.hayden.hap.common.button.itf.IButtonService#getBtnsByFormcode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年11月7日
	 */
	@Override
	public List<ButtonVO> getBtnsByFormcode(String formCode, Long tenantid) {		
		throw new HDRuntimeException("请调用指定的客户端类型服务");
	}

	

	@Override
	public List<ButtonCacheVO> filterByReadonly(List<ButtonCacheVO> buttons, boolean readonly) throws HDException {
		if(ObjectUtil.isEmpty(buttons))
			return buttons;
		
		List<ButtonCacheVO> result = new ArrayList<>();
		for(ButtonCacheVO vo : buttons) {
			if(!ButtonTypeEnum.CARD.getCode().equals(vo.getBtn_type()))
				continue;
			if(readonly && !OperaGroupEnum.QUERY.getCode().equals(vo.getBtn_group()))
				continue;			
			result.add(vo);
		}
		return result;
	}
}
