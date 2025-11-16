package com.hayden.hap.common.button.service;

import com.hayden.hap.common.button.entity.ButtonPCVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.button.itf.IButtonPCService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VariableUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年2月6日
 */
@Service("buttonPCService")
public class ButtonPCServiceImpl implements IButtonPCService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ButtonPCServiceImpl.class);

	@Autowired
	private IBaseService baseService;
	
	/** 
	 *
	 * @see com.hayden.hap.common.button.itf.IButtonPCService#getButtonListByFunc(java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年2月6日
	 */
	@Override
	public List<AbstractVO> getButtonListByFunc(Long tenantid) {
		StringBuilder sql=new StringBuilder("select formbtn.btn_id,formbtn.btn_name,formbtn.btn_code,formbtn.btn_type,formbtn.btn_property,fun.func_code from ");
        sql.append("(select * from sy_func where func_isenable=1 and tenantid=").append(tenantid).append(") fun left join ");
        sql.append(" (select * from sy_form where  tenantid=").append(tenantid).append(") form on fun.func_info=form.form_code inner join ");
        sql.append(" (select * from sy_form_button where btn_isenable=1 and tenantid=").append(tenantid).append(") formbtn on form.formid=formbtn.formid ");
        sql.append("order by fun.func_code,formbtn.btn_order");
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        VOSet<AbstractVO> voset = baseService.executeQuery(sql.toString(), dynaSqlVO);
        List<AbstractVO> list = voset.getVoList();
        return list;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.button.itf.IButtonPCService#getBtnsByFormcode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年2月6日
	 */
	@Cacheable(value="SY_FORM_BUTTON",key="#formCode.concat('|').concat(#tenantid)")
	@Override
	public List<? extends ButtonVO> getBtnsByFormcode(String formCode, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("form_code", formCode);
		dynaSqlVO.addWhereParam("btn_isenable", SyConstant.SY_TRUE);
		
		VOSet<ButtonPCVO> voSet = baseService.query(new ButtonPCVO(), dynaSqlVO);
		return voSet.getVoList();
	}

	@Override
	public List<ButtonPCVO> filterByShowCtrl(List<ButtonPCVO> btnList, AbstractVO vo) throws HDException {
		List<ButtonPCVO> result = new ArrayList<>();
		
		for(ButtonPCVO btnVO : btnList) {
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
}
