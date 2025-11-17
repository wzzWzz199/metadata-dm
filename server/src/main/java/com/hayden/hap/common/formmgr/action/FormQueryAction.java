package com.hayden.hap.common.formmgr.action;

import com.hayden.hap.common.cache.action.CacheHelperAction;
import com.hayden.hap.common.cache.itf.IActionCacheHelper;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.form.entity.FormQueryVO;
import com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年3月24日
 */
@SuppressWarnings("unchecked")
@Component("formQueryAction")
public class FormQueryAction extends CacheHelperAction{

	@Autowired
	private IBaseService baseService;
	
	@Override
	public List<Message> beforeCardSave(FormParamVO formParamVO,
			AbstractVO vo) throws HDException {
		HttpServletRequest request = formParamVO.getRequest();
		FormQueryVO formQueryVO = (FormQueryVO)vo;
		
		Long userid = CurrentEnvUtils.getUserId();
		if(formQueryVO.getFquery_id()!=null && !userid.equals(formQueryVO.getCreated_by())) {
			Message resultMessage = new Message("您不能修改他人创建的查询策略:"+formQueryVO.getFquery_name(),MessageLevel.ERROR);
//			resultMessage.setErrorMessageLevel(MessageLevel.ERROR);
//			resultMessage.setMessage("您不能修改他人创建的查询策略:"+formQueryVO.getFquery_name());
			
			List<Message> result = new ArrayList<>();
			result.add(resultMessage);
			return result;
		}
		
		formQueryVO.setFunc_code(request.getAttribute(SyConstant.FORM_QUERY_FUNC_FIELD).toString());
		return super.beforeCardSave(formParamVO, vo);
	}

	@Override
	protected IActionCacheHelper createActionCacheHelper() {
		return new FormQueryActionCacheHelper();
	} 
	
	@Override
	public List<Message> beforeListUpdate(
			FormParamVO formParamVO, BatchUpdateParamVO batchUpdateParamVO)
			throws HDException {
		
		List<Message> result = validateUser(batchUpdateParamVO.getVoList());
		
		if(ObjectUtil.isNotEmpty(result)) {
			return result;
		}
		
		return super.beforeListUpdate(formParamVO, batchUpdateParamVO);
	}
	
	private List<Message> validateUser(List<? extends AbstractVO> list) {
		List<Message> result = new ArrayList<>();
		Long userid = CurrentEnvUtils.getUserId();
		for(AbstractVO vo : list) {
			FormQueryVO formQueryVO = (FormQueryVO)vo;
			if(!formQueryVO.getCreated_by().equals(userid)) {
				Message resultMessage = new Message("您不能修改或删除他人创建的查询策略:"+formQueryVO.getFquery_name(),MessageLevel.ERROR);
//				resultMessage.set(MessageLevel.ERROR);
//				resultMessage.setMessage("您不能修改或删除他人创建的查询策略:"+formQueryVO.getFquery_name());				
				
				result.add(resultMessage);
			}
		}
		return result;
	} 
	
	@Override
	public List<Message> beforeListDeleteBatch(
			FormParamVO formParamVO, Collection<Long> primaryKeys)
			throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<FormQueryVO> list = baseService.queryByPKsOfLongAndTenantid(new FormQueryVO(), primaryKeys,tenantid).getVoList();
		List<Message> result = validateUser(list);
		
		if(ObjectUtil.isNotEmpty(result)) {
			return result;
		}
		
		return super.beforeListDeleteBatch(formParamVO, primaryKeys);
	}
}
