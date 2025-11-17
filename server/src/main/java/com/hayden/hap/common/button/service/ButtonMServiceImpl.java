package com.hayden.hap.common.button.service;

import com.hayden.hap.common.button.entity.ButtonMVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.button.itf.IButtonMService;
import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.form.entity.FormMVO;
import com.hayden.hap.common.form.itf.IFormMService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
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
import java.util.Set;

/**
 * 
 * @author zhangfeng
 * @date 2018年2月6日
 */
@Service("buttonMService")
public class ButtonMServiceImpl implements IButtonMService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ButtonMServiceImpl.class);

	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFormMService formMService;

	@Override
	public List<AbstractVO> getButtonListByFunc(Long tenantid) {
		StringBuilder sql = new StringBuilder(
				"select formbtn.btn_id,formbtn.btn_name,formbtn.btn_code,formbtn.btn_type,fun.func_code from ");
		sql.append("(select * from sy_func_mobile where func_isenable=1 and tenantid=").append(tenantid)
				.append(") fun left join ");
		sql.append(" (select * from sy_form_mobile where  tenantid=").append(tenantid)
				.append(") form on fun.func_info=form.form_code inner join ");
		sql.append(" (select * from sy_form_button_mobile where btn_isenable=1 and tenantid=").append(tenantid)
				.append(") formbtn on form.formid=formbtn.formid ");
		sql.append("order by fun.func_code,formbtn.btn_order");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		VOSet<AbstractVO> voset = baseService.executeQuery(sql.toString(), dynaSqlVO);
		List<AbstractVO> list = voset.getVoList();
		return list;
	}

	@Override
	public List<ButtonMVO> getButtonListByFuncCodes(Set<String> funcCodes, Long tenantid) {
		List<ButtonMVO> result = new ArrayList<ButtonMVO>();
		if (funcCodes == null || funcCodes.size() == 0)
			return result;

		String funcCodeStr = "'" + String.join("','", funcCodes) + "'";

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	sy_func_mobile.func_code,sy_func_mobile.modulecode,sy_form_button_mobile.* ");
		sql.append(" FROM ");
		sql.append(" sy_func_mobile ");
		sql.append(" INNER JOIN sy_form_button_mobile ");
		sql.append(" ON sy_func_mobile.func_info = sy_form_button_mobile.form_code ");
		sql.append("    AND sy_func_mobile.tenantid = sy_form_button_mobile.tenantid");
		sql.append(" WHERE sy_func_mobile.func_code IN (" + funcCodeStr + ") ");
		sql.append("       AND sy_func_mobile.tenantid =  " + tenantid);

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		VOSet<AbstractVO> voset = baseService.executeQuery(sql.toString(), dynaSqlVO);

		List<AbstractVO> list = voset.getVoList();
		for (AbstractVO abstractVO : list) {
			ButtonMVO vo = new ButtonMVO();
			vo.set("func_code", abstractVO.getString("func_code"));
			vo.set("modulecode", abstractVO.getString("modulecode"));
			vo.setBtn_id(abstractVO.getLong("btn_id", null));
			vo.setFormid(abstractVO.getLong("formid", null));
			vo.setForm_code(abstractVO.getString("form_code"));
			vo.setBtn_code(abstractVO.getString("btn_code"));
			vo.setBtn_name(abstractVO.getString("btn_name"));
			vo.setBtn_order(abstractVO.getInt("btn_order", 0));
			vo.setBtn_show_control(abstractVO.getString("btn_show_control"));
			vo.setBtn_type(abstractVO.getString("btn_type"));
			vo.setBtn_isenable(abstractVO.getInt("btn_isenable", null));
			vo.setBtn_inputconfig(abstractVO.getString("btn_inputconfig"));
			vo.setBtn_img(abstractVO.getString("btn_img"));
			vo.setBtn_group(abstractVO.getString("btn_group"));
			vo.setBtn_history(abstractVO.getInt("btn_history", null));
			vo.setBtn_param(abstractVO.getString("btn_param"));
			vo.setBtn_comment(abstractVO.getString("btn_comment"));
			result.add(vo);
		}
		return result;
	}

	@Cacheable(value = CacheConstant.CACHE_FORM_BUTTON_MOBILE, key = "#formCode.concat('|').concat(#tenantid)")
	@Override
	public List<ButtonMVO> getBtnsByFormcode(String formCode, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("form_code", formCode);
		dynaSqlVO.addWhereParam("btn_isenable", SyConstant.SY_TRUE);

		VOSet<ButtonMVO> voSet = baseService.query(new ButtonMVO(), dynaSqlVO);
		return voSet.getVoList();
	}

	@Override
	public List<ButtonMVO> filterByShowCtrl(List<ButtonMVO> btnList, AbstractVO vo) throws HDException {
		List<ButtonMVO> result = new ArrayList<>();

		for (ButtonMVO btnVO : btnList) {
			String btnShowControl = btnVO.getBtn_show_control();
			if (StringUtils.isEmpty(btnShowControl)) {
				result.add(btnVO);
			} else {
				boolean validateResult;
				try {
					validateResult = VariableUtils.replaceAllParamAndEval(btnShowControl, vo);
				} catch (ScriptException e) {
					logger.error(e.getMessage(), e);
					throw new HDException("按钮显示状态控制表达式错误：" + btnShowControl);
				}
				if (validateResult) {
					result.add(btnVO);
				}
			}
		}

		return result;
	}

	@Override
	public ButtonVO getBtnByCode(String funcCode, String btnCode, Long tenantid) throws HDException {
		if(StringUtils.isBlank(btnCode)) {
			throw new HDException("按钮编码空");
		}
		
		FormMVO formMVO = formMService.getFormVOByFunccode(funcCode, tenantid);
		if(formMVO==null) {
			throw new HDException("根据功能编码查无表单："+funcCode);
		}
		
		IButtonMService thisService = AppServiceHelper.findBean(IButtonMService.class);
		List<? extends ButtonVO> btns = thisService.getBtnsByFormcode(formMVO.getForm_code(), tenantid);
		if(ObjectUtil.isEmpty(btns))
			return null;
		
		for(ButtonVO buttonVO : btns) {
			if(btnCode.equals(buttonVO.getBtn_code())) {
				return buttonVO;
			}
		}
		
		return null;
	}

}
