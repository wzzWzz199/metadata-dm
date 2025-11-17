package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.button.itf.IButtonService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormQueryItemVO;
import com.hayden.hap.common.form.entity.FormQueryVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.action.FormQueryAction;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.formmgr.itf.ICardFormService;
import com.hayden.hap.common.formmgr.itf.IFormQueryService;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.utils.Code2NameHandleUtils;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.BaseSettingUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
//import com.hayden.hap.sy.formmgr.entity.ReturnResult;

/**
 * 
 * @author zhangfeng
 * @date 2016年3月18日
 */
@Service("formQueryService")
public class FormQueryServiceImpl implements IFormQueryService {

	@Autowired
	private IListFormService listFormService;
	
	@Autowired
	private ICardFormService cardFormService;
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IFormItemService formItemService;
	
	@Autowired
	private IButtonService buttonService;
	
	private static final int PAGE_SIZE = 20;
	
//	private static final String FORM_CODE = "SY_FORM_QUERY";
	
	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IFormQueryService#listQuery(com.hayden.hap.common.formmgr.entity.FormParamVO)
	 * @author zhangfeng
	 * @date 2016年3月18日
	 */
	@Override
	public VOSet<? extends AbstractVO> listQuery(FormParamVO formParamVO,String fromFuncCode)
			throws HDException {
		Long tenantId = CurrentEnvUtils.getTenantId();

		ReqParamVO param = formParamVO.getReqParamVO();
		DynaSqlVO dynaSqlVO = listFormService.buildWhereClause(formParamVO);
		
		dynaSqlVO.addWhereParam("func_code", fromFuncCode);
		
		if(param.getPage()!=null) {
			int page = param.getPage();
			int pageSize = param.getRows()!=0?param.getRows():PAGE_SIZE;
			dynaSqlVO.createPage(page, pageSize);
		}

		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantId);
		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(),tenantId);
		
		if(StringUtils.hasLength(formVO.getQuick_order())) {
			if(!StringUtils.hasLength(dynaSqlVO.getOrderByClause())) {
				dynaSqlVO.setOrderByClause(formVO.getQuick_order());
			}			
		}
		
		VOSet<? extends AbstractVO> voSet = null;		
		voSet = baseService.query(funcService.getQueryTableNameOfFunc(formParamVO.getFuncCode(), tenantId), dynaSqlVO);		
		
		for(FormItemVO itemVO : formItemVOs) {
			if(InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) {
				Code2NameHandleUtils.handleQuerySelector(voSet.getVoList(), itemVO, tenantId);
			}			
		}

		return voSet;
	}


	@Override
	public ReturnResult<FormQueryVO> save(FormParamVO formParamVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		String voJson = formParamVO.getDataBody();
		FormQueryVO formQueryVO = JsonUtils.parse(voJson, FormQueryVO.class);
		BaseSettingUtils.setCU_TPD(formQueryVO);
		handleFormQuerySQL(formQueryVO, tenantid);
		
		IAction action = new FormQueryAction();
		action.beforeCardSave(formParamVO, formQueryVO);
		
		if(formQueryVO.getFquery_order()==null) {
			formQueryVO.setFquery_order(1);
		}
		
		ReturnResult<FormQueryVO> returnResult = new ReturnResult<>();
		
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("func_code", formQueryVO.getFunc_code());
		dynaSqlVO.addWhereParam("fquery_name", formQueryVO.getFquery_name());
		int count = baseService.getCount(formQueryVO.getTableName(), dynaSqlVO);
		if(count>0) {
			Message resultMessage = new Message("查询策略名字已存在",MessageLevel.ERROR);

			returnResult = new ReturnResult<>(null, resultMessage);
			return returnResult;
		}
		
		FormQueryVO vo = baseService.insert(formQueryVO);
		
		action.afterCardSave(formParamVO, vo, true);
		
		
		returnResult.setData(vo);
		return returnResult;
	}
	
	private void handleFormQuerySQL(FormQueryVO formQueryVO,Long tenantid) {
		List<FormQueryItemVO> items = formQueryVO.getFquery_sql_arr();
//		List<FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formQueryVO.getFunc_code(), tenantid);
//		StringBuilder sb = new StringBuilder();
//		for(FormQueryItemVO item:items) {
//			for(FormItemVO formItemVO : formItemVOs) {
//				if(formItemVO.getFitem_code().equals(item.getFormItem())) {
//					sb.append(" ");
//					sb.append(item.getLogicRel());
//					sb.append(" ");
//					sb.append(parerWhereClaus(formItemVO,item.getValue(),item.getQueryMethod(),item.getFormItem()));
//					
//					break;
//				}
//			}
//		}
//		if(sb.length()>0) {
//			sb.insert(0, "( 1=1 ");
//			sb.append(")");
//		}
		
		String sql = handleQuerySQL(formQueryVO.getFunc_code(), items, tenantid);
		formQueryVO.setFquery_sql(sql);
	}
	
	@Override
	public String handleQuerySQL(String funcCode,List<FormQueryItemVO> items,Long tenantid) {
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		StringBuilder sb = new StringBuilder();
		for(FormQueryItemVO item:items) {
			for(FormItemVO formItemVO : formItemVOs) {
				if(formItemVO.getFitem_code().equals(item.getFormItem())) {
					sb.append(" ");
					sb.append(item.getLogicRel());
					sb.append(" ");
					sb.append(parerWhereClaus(formItemVO,item.getValue(),item.getQueryMethod(),item.getFormItem()));
					
					break;
				}
			}
		}
		if(sb.length()>0) {
			if(sb.indexOf(" and")==0) {
				sb.delete(0, 4);//去掉第一个and
			}else {
				sb.delete(0, 3);//去掉第一个or
			}
			sb.insert(0, "( ");
			sb.append(")");
		}
		return sb.toString();
	}
	
	private String parerWhereClaus(FormItemVO fieldVO,Object oldValue,String queryMethodStr,String key)
	{
		boolean isNum = true;
		String value = oldValue+"";
		if(!"2".equals(fieldVO.getFitem_data_type()))
		{
			value = "'"+oldValue+"'";
			isNum = false;
		}
		
		if("eq".equals(queryMethodStr)) {
	
			return key+ " = " + value;
		}else if("gt".equals(queryMethodStr)) {
			return key+ " > " + value;
		}else if("ge".equals(queryMethodStr) || "range".equals(queryMethodStr)) {
			return key+ " >= " + value;
		}else if("lt".equals(queryMethodStr)) {
			return key+ " < " + value;
		}else if("le".equals(queryMethodStr)) {
			return key+ " <= " + value;
		}else if("not_eq".equals(queryMethodStr)) {
			return key+ " != " + value;
		}else if("in".equals(queryMethodStr)) {
			if(String.valueOf(value).indexOf(",")>=0)
			{
				if(isNum)
				{
					return key+ " in (" + value+")";
				}else
				{
					return key+ " in (" + String.valueOf(value).replaceAll(",", "','")+")";
				}
			}else
			{
				return key+ " = " + value;
			}
			
		}else if("not_in".equals(queryMethodStr)) {
			if(String.valueOf(value).indexOf(",")>=0)
			{
				if(isNum)
				{
					return key+ " not in (" + value+")";
				}else
				{
					return key+ " not in (" + String.valueOf(value).replaceAll(",", "','")+")";
				}
			}else
			{
				return key+ " != " + value;
			}
		}else if("anywhere".equals(queryMethodStr)) {
			return key+ " like '%" + oldValue+ "%'";
		}else if("start".equals(queryMethodStr)) {
			return key+ " like '" + oldValue+ "%'";
		}else if("end".equals(queryMethodStr)) {
			return key+ " like '%" + oldValue +"'";
		}else
			return key+ " = " + value;
	}


	@Override
	public List<FormQueryVO> getFormQueryByFunc(String funcCode, Long userid,
			Long tenantid) {
		IFormQueryService formQueryService = AppServiceHelper.findBean(IFormQueryService.class);
		List<FormQueryVO> all = formQueryService.getFormQueryByFunc(funcCode, tenantid);
		
		List<FormQueryVO> result = new ArrayList<>();
		
		FormQueryVO queryVO = new FormQueryVO();
		queryVO.setFquery_id(SyConstant.FORM_QUERY_ALL_ID);
		queryVO.setFquery_name("全部数据");
		queryVO.setFquery_isdefault(0);
		result.add(queryVO);
		
		for(FormQueryVO formQueryVO : all) {
			if(userid.equals(formQueryVO.getCreated_by())) {
				result.add(formQueryVO);
			}/*else if(ObjectUtil.isTrue(formQueryVO.getFquery_ispublic())) {
				result.add(formQueryVO);
			}*/
		}
		
		return result;
	}
	
	/**
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IFormQueryService#getFormQueryByFunc(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年4月1日
	 */
	@Override
	@Cacheable(value="SY_FORM_QUERY",key="#funcCode.concat('|').concat(#tenantid)")
	public List<FormQueryVO> getFormQueryByFunc(String funcCode,Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam("tenantid", tenantid);
		dynaSqlVO.setOrderByClause(" created_dt desc ");
		VOSet<FormQueryVO> voset = baseService.query(FormQueryVO.class, dynaSqlVO);
		return voset.getVoList();
	}


	@Override
	public String getFormQuerySql(String funcCode, Long tenantid,
			Long formQueryId) {
		IFormQueryService formQueryService = AppServiceHelper.findBean(IFormQueryService.class);
		List<FormQueryVO> all = formQueryService.getFormQueryByFunc(funcCode, tenantid);
		
		for(FormQueryVO formQueryVO : all) {
			if(formQueryVO.getFquery_id().equals(formQueryId)) {
				return formQueryVO.getFquery_sql();
			}
		}
		
		return "";
	}


//	@Override
//	public List<ButtonVO> getListViewBtns(Long tenantid) {
//		FormVO formVO = formService.queryByFormcode(FORM_CODE, tenantid);
//		
//		return buttonService.getButtonList(formVO.getFormid(), SyConstant.BTN_TYPE_LIST, tenantid);
//	}
//
//
//	@Override
//	public List<ButtonVO> getCardViewBtns(Long tenantid) {
//		FormVO formVO = formService.queryByFormcode(FORM_CODE, tenantid);
//		return buttonService.getButtonList(formVO.getFormid(), SyConstant.BTN_TYPE_CARD, tenantid);
//	}
}
