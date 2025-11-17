package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.attach.IFastDfsService;
import com.hayden.hap.common.attach.itf.IAttachMethodService;
import com.hayden.hap.common.authz.button.itf.IButtonAuthzService;
import com.hayden.hap.common.billcode.itf.IBillCodeService;
import com.hayden.hap.common.button.entity.ButtonPCVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.button.itf.IButtonPCService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.db.util.ResourceUtil;
import com.hayden.hap.common.enumerate.*;
import com.hayden.hap.common.form.entity.FormItemCommentVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.action.ActionHandler;
import com.hayden.hap.common.formmgr.control.CardCtrlVO;
import com.hayden.hap.common.formmgr.entity.CardDataVO;
import com.hayden.hap.common.formmgr.entity.CardEditVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.*;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.formmgr.utils.Code2NameHandleUtils;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.formmgr.utils.ValidateUtils;
import com.hayden.hap.common.formmgr.utils.WfFlagUtils;
import com.hayden.hap.common.formual.itf.IFormualDBService;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkDataService;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.orgpermission.entity.OrgPermissionDTVO;
import com.hayden.hap.common.orgpermission.itf.IOrgPermissionService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.*;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import com.hayden.hap.wf.itf.IWfProcCurService;
import com.hayden.hap.wf.itf.IWorkflowButtonService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("cardFormService")
public class CardFormServiceImpl implements ICardFormService {
	
	public static final String USER_ID_STR = "userIdStr";
	
	private static final Logger logger = LoggerFactory.getLogger(CardFormServiceImpl.class);

	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private IFuncLinkService funcLinkService;
	
	@Autowired
	private IFuncLinkDataService funcLinkDataService;
	
	@Autowired
	private IFormItemService formItemService;
	
	@Autowired
	private IBillCodeService billCodeService;
	
	@Autowired(required=false)
	private IWorkflowButtonService workflowButtonService;
	
	@Autowired
	private IFuncTreeService funcTreeService;
	
	@Autowired
	private IOrgPermissionService orgPermissionService;
	
	@Autowired
	private IAttachMethodService attachMethodService;
	
	@Autowired
	private IDataCopyService dataCopyService;
	
	@Autowired
	private IFormualDBService formualDBService;
	
	@Autowired
	private IReadonlyService readonlyService;
	@Autowired
	private IFastDfsService fastDfsService;
	
	@Autowired
	private IButtonAuthzService buttonAuthzService;
	
	@Autowired(required=false)
	private IWfProcCurService wfProcCurService;
	
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	@Override
	@Transactional
	public ReturnResult<CardDataVO> save(FormParamVO formParamVO) throws HDException {	
		ReturnResult<CardDataVO> returnResult = new ReturnResult<>();
		
		Long tenantid = CurrentEnvUtils.getTenantId();
		Long userid = CurrentEnvUtils.getUserId();
		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);
		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantid);
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
				
		String tableName = funcService.getOperaTableNameOfFunc(formParamVO.getFuncCode(),tenantid);
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//操作表信息
		Class<? extends AbstractVO> voClass = this.getVOClass(tableDefVO);				
		AbstractVO vo = getSaveVO(formParamVO.getDataBody(), voClass, formItemVOs);
		vo.setTableName(tableName);

		//防止vo中tenantid值为空
		if(vo.getLong(SyConstant.TENANT_STR) == null){
			vo.setLong(SyConstant.TENANT_STR, tenantid);
		}
		//动态获取表单项 （新增）
		formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), vo);

		//作为被关联功能时的插入需要处理关联关系字段
		vo = assignFuncLinkItemValue(formParamVO, vo, tenantid);
		
		//对vo进行验证
		String pkColName = tableDefService.getPkColName(tableName);
		List<Message> validateResult = ValidateUtils.validate(vo, formItemVOs, tenantid);
		List<Message> validateAttachResult = ValidateUtils.validateAttachNotNull(formParamVO, vo, formItemVOs, pkColName, tenantid);
		validateResult.addAll(validateAttachResult);
		if(validateResult.size()>0) {//如果验证未通过，返回验证信息
			returnResult.setMessages(validateResult);
			return returnResult;
		}
				
		List<Message> beforErrorList = null;
		//附件字段 还原|添加
		attachMethodService.updateColByAttachKey(vo, formParamVO.getModuleCode(), formParamVO.getFuncCode(), vo.getLong(pkColName), tenantid,isEditStatus(vo));
		IAction action = formService.getActionByFuncCode(formParamVO.getFuncCode(), tenantid);
		if(action!=null) {
			beforErrorList = action.beforeCardSave(formParamVO, vo);
		}
		ExceptionHandlerUtils.handle(beforErrorList, formParamVO.getRequest());
		
		CardDataVO cardDataVO = new CardDataVO();
		boolean isAdd = false;
		if(isEditStatus(vo)) {
			if(needValidateConsistency(formVO)) {
				ValidateUtils.validateConsistency(vo);
			}
			vo = edit(formParamVO, vo, tenantid);
		}else {
			
			vo = insert(formParamVO, vo, tenantid);
			isAdd = true;				
		}
		
		List<Message> afterErrorList = null;
		if(action!=null) {
			afterErrorList = action.afterCardSave(formParamVO, vo, isAdd);
		}
		ExceptionHandlerUtils.handle(afterErrorList, formParamVO.getRequest());
		
		boolean readonly = readonlyService.getReadonlyStatus(funcVO, vo);
		Boolean isSupportWf = WfFlagUtils.isSupportWf(formVO);
		List<? extends ButtonVO> cardButtons = new ArrayList<>();
		if(isSupportWf) {
			if (isAdd) {
				//保存当前审批状态数据 yinbinchen
				wfProcCurService.saveWfProcCurVO(vo,funcVO, tenantid);
			}
			else{
				//获取当前审批状态数据 yinbinchen
				vo = wfProcCurService.getWfProcCurVO(vo,funcVO, tenantid);
			}
			boolean oldReadOnly = readonly;
			readonly = workflowButtonService.getReadonlyStatus(funcVO, vo, readonly);
			cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, vo, readonly);
			workflowButtonService.addWorkFlowButtons(cardButtons, formParamVO.getFuncCode(), vo, readonly, oldReadOnly);
			CardCtrlVO cardCtrlVO = workflowButtonService.getCardCtrlVOByNodeConfig(vo, formParamVO.getFuncCode(), tenantid, userid);
			cardDataVO.setCardCtrlVO(cardCtrlVO);
		} else {
			cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, vo, readonly);
		}
		
		cardDataVO.setBtnList(cardButtons);
		Code2NameHandleUtils.handleQuerySelector(vo, formParamVO.getFuncCode(), tenantid);
		formualDBService.processDBFormual(vo, formItemVOs, tenantid);
		// 保存后回显附件
		fastDfsService.updateColByAttachInfo(vo, formParamVO.getModuleCode(),formParamVO.getFuncCode(),  tenantid,true);
		cardDataVO.setReadonly(readonly);
		cardDataVO.setData(vo);
		ActionHandler.handleLastCardSave(action, cardDataVO, formParamVO, vo, isAdd);
		
		returnResult.setData(cardDataVO);
		if(!CollectionUtils.isEmpty(beforErrorList) || !CollectionUtils.isEmpty(afterErrorList)) {
			List<Message> list = new ArrayList<Message>();
			if(ObjectUtil.isNotEmpty(beforErrorList)) {
				list.addAll(beforErrorList);
			}
			if(ObjectUtil.isNotEmpty(afterErrorList)) {
				list.addAll(afterErrorList);
			}
			
			returnResult.setMessages(list);
		}
		
		DictUtils.evictByTablename(tableName, tenantid);
		return returnResult;
	}
	
	/**
	 * 获取要保存的VO对象
	 * @param json
	 * @param clazz
	 * @param formItemVOs
	 * @return
	 * @throws HDException 
	 * @author YT
	 * @date 2017年5月24日
	 */
	private <T> T getSaveVO(String json, Class<T> clazz, List<? extends FormItemVO> formItemVOs) throws HDException {
		T vo = null;
		try {
			vo = JsonUtils.parse(json, clazz);
		}catch(HDException e) {
			throwPropException(e,formItemVOs);
			throw e;
		}
		return vo;
	} 
	
	/**
	 * 是否需要一致性校验
	 * @param formVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	private boolean needValidateConsistency(FormVO formVO) {
		return ObjectUtil.isTrue(formVO.getIs_validate_consistency());
	}
	
	/**
	 * 是否修改状态
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	private boolean isEditStatus(AbstractVO vo) {
		return ObjectUtil.isNotNull(baseService.getVOPkColValue(vo));
	}
	
	/**
	 * 修改
	 * @param formParamVO
	 * @param vo
	 * @param tenantid
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	private AbstractVO edit(FormParamVO formParamVO, AbstractVO vo, Long tenantid) throws HDException {
		BaseSettingUtils.setTenantid(vo);
		BaseSettingUtils.setU_P(vo);
		BaseSettingUtils.setU_D(vo);
		//关联功能的关联更新
		funcLinkService.linkUpdate(vo, formParamVO.getFuncCode());
		//关联数据的关联更新
		funcLinkDataService.linkUpdate(vo, formParamVO.getFuncCode(),tenantid);
		int result = baseService.update(vo);
		if(result==0) {
			throw new HDException("记录不存在或已被删除");
		}
		return vo;
	}
	
	/**
	 * 新增
	 * @param formParamVO
	 * @param vo
	 * @param tenantid
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年5月24日
	 */
	private AbstractVO insert(FormParamVO formParamVO, AbstractVO vo, Long tenantid) throws HDException {
		//更新附件数量
		Map<String, Object> map = attachMethodService.updateAttachNum(formParamVO, vo);
		vo.set("wf_audit_state",AuditStateEnum.TO_AUDIT.getCode());	
		BaseSettingUtils.setTenantid(vo);
		BaseSettingUtils.setCU_P(vo);
		BaseSettingUtils.setCU_D(vo);
		vo.set("wf_create_user",vo.get("created_by"));	
		handleSerialNumberAuto(vo, formParamVO.getFuncCode());
		InnerCodeUtil.assignInnercode(vo, tenantid);
		baseService.insert(vo);
		//删除旧的附件记录，插入新的，（不直接更新是因为防止分库时业务记录创建日期处于俩个不同的库）
		attachMethodService.updateAttachVos(vo, map, formParamVO.getFuncCode(), tenantid);
		return vo;
	}
	
	/**
	 * 将特定json解析异常，转换成字段输入不正确异常
	 * @param e
	 * @param formItemVOs
	 * @throws HDException 
	 * @author YT
	 * @date 2017年5月24日
	 */
	private void throwPropException(HDException e, List<? extends FormItemVO> formItemVOs) throws HDException {
		if(e.getMessage()!=null && e.getMessage().startsWith(JsonUtils.PROPTIES_EXCETTION_PREFIX)) {
			String itemCode = e.getMessage().replace(JsonUtils.PROPTIES_EXCETTION_PREFIX, "");
			if(org.apache.commons.lang.StringUtils.isEmpty(itemCode))
				return;
			for(FormItemVO itemVO : formItemVOs) {
				if(itemCode.equals(itemVO.getFitem_code())) {
					String itemName = itemVO.getFitem_name();
					throw new HDException("字段["+itemName+"],输入格式不正确");
				}
			}
		}
	}

	/**
	 * 赋值关联字段属性值
	 * @param formParamVO
	 * @param vo
	 * @param tenantid
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月15日
	 */
	private AbstractVO assignFuncLinkItemValue(FormParamVO formParamVO, AbstractVO vo, Long tenantid) 
			throws HDException {
		if(StringUtils.isNotEmpty(formParamVO.getReqParamVO().getParentFuncCode())) {
			String parentFuncCode = formParamVO.getReqParamVO().getParentFuncCode();
			Long parentEntityId = formParamVO.getReqParamVO().getParentEntityId();

			if(StringUtils.isNotEmpty(parentFuncCode)) {
				List<FuncLinkVO> funcLinkList = funcLinkService.getFuncLink(parentFuncCode, 
						formParamVO.getFuncCode(),tenantid);
				if(ObjectUtil.isNotEmpty(funcLinkList)) {
					String parentTableName = funcService.getOperaTableNameOfFunc(parentFuncCode,tenantid);	
					Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
					AbstractVO parentVO = baseService.queryByPKAndTenantid(parentTableName, parentEntityId,currentDataTenantid);										
					if(parentVO==null) {
						FuncVO parentFuncVO = funcService.queryByFunccode(
								formParamVO.getReqParamVO().getParentFuncCode(), tenantid);
						throw new HDException("异常，由于"+parentFuncVO.getFunc_name()+"的记录不存在或已被删除|"+currentDataTenantid);
					}
					FuncLinkVO funcLink = funcLinkList.get(0);
					List<FuncLinkItemVO> linkItems = funcLink.getLinkItems();
					for(FuncLinkItemVO linkItem : linkItems) {
						if(ObjectUtil.isTrue(linkItem.getLitem_isvalue())) {
							if(ObjectUtil.isTrue(linkItem.getLitem_isconstant())) {
								vo.set(linkItem.getLitem_sub_field(), linkItem.getLitem_main_field());
							}else {
								vo.set(linkItem.getLitem_sub_field(), parentVO.get(linkItem.getLitem_main_field()));
							}							
						}
					}
				}
			}
		}
		return vo;
	}
	
	
	/**
	 * 获取卡片页面的业务对象：
	 * 首先从action的initCardView取,若取到了，则返回；
	 * 然后看IEditVOGetter是否为空，不为空则代表业务对象从外部传递进来了，则返回；
	 * 再通过功能编码、数据主键、租户id从数据库查找，查到则返回；
	 * 如果还没查到，则判定当前表单是不是管理租户型表单，若是，则不过滤租户id再去查，查到则返回；
	 * 如果还是没有，那么这条记录就可能是被删除了...抛个异常
	 * 
	 * @param action
	 * @param formParamVO
	 * @param formVO
	 * @param tenantid
	 * @param editVOGetter
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年8月26日
	 */
	private AbstractVO getEditVO(IAction action,FormParamVO formParamVO,FormVO formVO,
			Long tenantid,IEditVOGetter editVOGetter,OrgPermissionDTVO dto) throws HDException {
		ReqParamVO param = formParamVO.getReqParamVO();
		AbstractVO editVO = null;
		
		if(action!=null) {
			editVO = action.initCardVO(formParamVO);
		}		
			
		if(editVO!=null) {
			return editVO;
		}
		
		if(editVOGetter!=null) {//像外部字典数据，我就是从这里拿到的...
			editVO = editVOGetter.getEditVO();
			if(editVO.getTableName()==null) {
				editVO.setTableName(formVO.getOpera_table_code());
			}
		}else {
			String tableName = funcService.getQueryTableNameOfFunc(formParamVO.getFuncCode(),tenantid);
			String pkCol = tableDefService.getPkColName(tableName);
			
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			
			Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, currentDataTenantid);
			dynaSqlVO.addWhereParam(pkCol, param.getEditId());
			if (!StringUtils.equals("sy_org",tableName)){
				//系统bug,需过滤掉sy_org表，否则会重置where 条件
				orgPermissionService.applyOrgPermission(dynaSqlVO, dto);
			}
			VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);
			if(ObjectUtil.isNotEmpty(voset.getVoList())) {
				editVO = voset.getVoList().get(0);
			}
		}
		
		if(editVO==null) {
			throw new HDException("根据功能编码："+formParamVO.getFuncCode()+"，主键："+param.getEditId()+"没有查到记录，可能已被删除");
		}
		
		return editVO;
	}
	
	/**
	 * 处理各字段：<br/>
	 * 字典编码，将字典数据集合赋给字段<br/>
	 * 字典无名称、查询选择：给vo返名称操作<br/>
	 * @param formItemVOs
	 * @param editVO
	 * @param formVO
	 * @param tenantid
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年8月26日
	 */
	public void formitemHandle(List<? extends FormItemVO> formItemVOs,AbstractVO editVO,FormVO formVO,Long tenantid) throws HDException {
		for(FormItemVO fieldVO : formItemVOs) {
			if(InputTypeEnum.DICT_NEW.getCode().equals(fieldVO.getFitem_input_type())) {//字典编码，需要给字段提供字典数据
				if(fieldVO.getFitem_input_config()==null) {
					continue;
				}
				DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(fieldVO.getFitem_input_config());
				if(inputConfigVO.getDictdata()!=null) {//如果输入设定配置了字典数据
					fieldVO.setDictList(inputConfigVO.getDictdata());
				}else {
					fieldVO.setDictList(DictUtils.getDictData(inputConfigVO.getDictcode()));					
				}
				Code2NameHandleUtils.handleDictNN(editVO,fieldVO,tenantid);
			}
//			else if(InputTypeEnum.DICT_NEW.getCode().equals(fieldVO.getFitem_input_type())){//字典无名称，需要给编辑页面显示名称信息
//				Code2NameHandleUtils.handleDictNN(editVO,fieldVO,tenantid);
//			}
			else if(InputTypeEnum.QUERY_SELECT.getCode().equals(fieldVO.getFitem_input_type())) {//查询选择，需要带出一些其它字段信息
				Code2NameHandleUtils.handleQuerySelector(editVO, fieldVO,tenantid);
			}
		}
	}
	
	/**
	 * 计算只读表达式
	 * @param exp
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年6月30日
	 */
	@Override
	public boolean calculateReadonlyExpression(String exp, AbstractVO vo) throws HDException {
		if(!StringUtils.isNotEmpty(exp)) 
			return false;
		
		exp = VariableUtils.replaceFormItemParam(exp, vo);
		exp = VariableUtils.replaceSystemParam(exp);
		
		ScriptEngineManager factory = new ScriptEngineManager();  
		ScriptEngine engine = factory.getEngineByName("JavaScript"); 
		
		Object result;
		try {
			result = engine.eval(exp);
		} catch (ScriptException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e);
		}
		return Boolean.valueOf(result.toString());
	}
	
	
	@SuppressWarnings("unchecked")
	private Class<? extends AbstractVO> getVOClass(TableDefVO tableDefVO){
		ObjectUtil.validNotNull(tableDefVO, "tableDefVO is required.");
		if(ObjectUtil.isNotNull(tableDefVO.getClassname())){
			try {
				return (Class<? extends AbstractVO>) ResourceUtil.classForName(tableDefVO.getClassname());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
				return BaseVO.class;
			}
		}
		return BaseVO.class;
	}
	
	/**
	 * 处理自动编码
	 * @param t
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2015年12月18日
	 */
	private <T extends AbstractVO> T  handleSerialNumberAuto(T t,String funcCode) throws HDException {	
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<? extends FormItemVO> list = formItemService.getAutoSerialNumberItem(funcCode, tenantid);
		for(FormItemVO vo:list) {
			String value = billCodeService.generalDocNum(t, funcCode, vo.getFitem_code());
			t.set(vo.getFitem_code(), value);
		}
		return t;
	}
	
	/**
	 * 改变FormParamVO对象的reqParamVO属性
	 * @param formParamVO 
	 * @author zhangfeng
	 * @date 2015年11月17日
	 */
	@Override
	public void changeFormParamVO(FormParamVO formParamVO) {
		ReqParamVO reqParamVO = formParamVO.getReqParamVO();
		if(reqParamVO==null || StringUtils.isEmpty(reqParamVO.getRtnURL())) {
			return;
		}
		
		String rtnURL = reqParamVO.getRtnURL();
		
		ReqParamVO reqParamVO2 = url2ReqpParamVO(rtnURL);
		formParamVO.setReqParamVO(reqParamVO2);		
	}
	
	/**
	 * 根据URL构造ReqParamVO对象
	 * @param url
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月17日
	 */
	private ReqParamVO url2ReqpParamVO(String url) {
		int index = url.indexOf("?");
		if(index!=-1) {
			String content = url.substring(index+1);
			MultiMap<String> values = new MultiMap<String>();  
			UrlEncoded.decodeTo(content, values, "UTF-8", 1000);
			
			ReqParamVO newReqpParamVO = new ReqParamVO();
			String act = values.getString("act");
			newReqpParamVO.setAct(act);
			
			String page = values.getString("page");
			newReqpParamVO.setPage(page!=null?Integer.valueOf(page):null);
			
			String rows = values.getString("rows");
			newReqpParamVO.setRows(rows!=null?Integer.valueOf(rows):null);
			
			String parentEntityId = values.getString("parentEntityId");
			newReqpParamVO.setParentEntityId(StringUtils.isNotEmpty(parentEntityId)?Long.valueOf(parentEntityId):null);
			
			String parentFuncCode = values.getString("parentFuncCode");
			newReqpParamVO.setParentFuncCode(parentFuncCode);			
			
			String topFuncCode = values.getString("topFuncCode");
			newReqpParamVO.setTopFuncCode(topFuncCode);
			
			String topEntityId = values.getString("topEntityId");
			newReqpParamVO.setTopEntityId(StringUtils.isNotEmpty(topEntityId)?Long.valueOf(topEntityId):null);
			
			String isChildFunc = values.getString("isChildFunc");
			newReqpParamVO.setIsChildFunc(StringUtils.isNotEmpty(isChildFunc)?Integer.valueOf(isChildFunc):SyConstant.SY_FALSE);
			
			newReqpParamVO.setRtnURL(url);
			
			String editId = values.getString("editId");
			newReqpParamVO.setEditId(StringUtils.isNotEmpty(editId)?Long.valueOf(editId):null);
			
			String queryParam = values.getString("queryParam");
			newReqpParamVO.setQueryParam(queryParam);	
			
			String sidx = values.getString("sidx");
			newReqpParamVO.setSidx(sidx);
			
			String sord = values.getString("sord");
			newReqpParamVO.setSord(sord);
			
			String isForwardToCard = values.getString("isForwardToCard");
			newReqpParamVO.setIsForwardToCard(StringUtils.isNotEmpty(isForwardToCard)?Integer.valueOf(isForwardToCard):SyConstant.SY_FALSE);
			
			String formQueryId = values.getString("formQueryId");
			newReqpParamVO.setFormQueryId(StringUtils.isNotEmpty(formQueryId)?Integer.valueOf(formQueryId):0L);
			
			String formQuerySql = values.getString("formQuerySql");
			newReqpParamVO.setFormQuerySql(formQuerySql);
						
			return newReqpParamVO;
		}
		return new ReqParamVO();
	}

	@Override
	@Transactional
	public ReturnResult<CardDataVO> saveAndCopy(FormParamVO formParamVO) throws HDException {
		ReturnResult<CardDataVO> returnResult = save(formParamVO);
		
		if(returnResult.getStatus()!=Status.SUCCESS) {//如果保存方法有错误则直接返回了
			ReturnResult<CardDataVO>  result = new ReturnResult<>();
			result.setMessages(returnResult.getMessages());
			return result;
		}
		
		AbstractVO originalVO = ((CardDataVO) returnResult.getData()).getData();
		
		Long tenantid = CurrentEnvUtils.getTenantId();		
		return cardCopy(formParamVO, originalVO, tenantid);
	}

	@Override
	public AbstractVO assignDefaultValue(FormParamVO formParamVO,AbstractVO vo,Long tenantid) throws HDException {
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
		
		for(FormItemVO formItemVO : formItemVOs) {
			if(StringUtils.isNotEmpty(formItemVO.getFitem_card_default())) {		
				//照片默认值 前台做; add by haocs 2019年4月25日15:54:55
				if(ElementTypeEnum.PHOTO.getCode().equals(formItemVO.getFitem_input_element())){
					continue;
				}else if(vo.get(formItemVO.getFitem_code())==null) {					
										
					if(ElementTypeEnum.DATE.getCode().equals(formItemVO.getFitem_input_element()) 
							&& DataTypeEnum.STRING.getCode().equals(formItemVO.getFitem_data_type())) {
						String value = VariableUtils.replaceSystemParam(formItemVO.getFitem_card_default(), formItemVO);
						vo.set(formItemVO.getFitem_code(), value);
						continue;
					}
					
					Object value = VariableUtils.replaceSystemParam4Obj(formItemVO.getFitem_card_default(), formItemVO.getFitem_data_type());
					
					if(DataTypeEnum.DATE.getCode().equals(formItemVO.getFitem_data_type()) && !(value instanceof Date)) {
						try {
							Date dateValue = DateUtils.parseDate(value.toString(), DATE_TIME_PATTERN);
							vo.set(formItemVO.getFitem_code(), dateValue);
						} catch (ParseException e) {
							throw new HDException(e);
						} catch (NullPointerException e) {
							vo.set(formItemVO.getFitem_code(), null);
						}
						
					}else {
						vo.set(formItemVO.getFitem_code(), value);
					}
				}
			}
		}
		
		return vo;
	}

	@Override
	public CardDataVO getEditVO(FormParamVO formParamVO,IEditVOGetter editVOGetter, OrgPermissionDTVO dto, boolean readOnly) throws HDException {
		Long tenantid = dto.getTenantid();
		Long userid = dto.getUserid();
		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);
		FormVO formVO = formService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
	
		AbstractVO editVO = getEditVO(action, formParamVO, formVO, tenantid, editVOGetter, dto);		
		
		//如果非只读，计算下只读表达式
		if(!readOnly) {
			readOnly = calculateReadonlyExpression(funcVO.getFunc_readonly_exp(), editVO);
		}		

		if(action!=null) {
			action.beforeGetCardVO(editVO, formParamVO, readOnly);
		}
		
		editVO = code2NameForQuerySelectAndFormual(editVO, formVO.getForm_code(), tenantid);	
		
		if(action!=null) {
			action.afterGetCardVO(editVO, formParamVO, readOnly);
		}
		//根据输入类型 来区分附件处理方式
		//得到卡片页面的附件字段的附件数量		
//		attachService.getCardAttachNum(formParamVO.getFuncCode(), formParamVO.getModuleCode(), editVO, tenantid);
		fastDfsService.updateColByAttachInfo(editVO, formParamVO.getModuleCode(),formParamVO.getFuncCode(),  tenantid,false);
		if(action!=null) {
			readOnly = action.changeCardReadonly(editVO, formParamVO, readOnly);
		}
		CardDataVO cardDataVO = new CardDataVO();
		cardDataVO.setData(editVO);
		
		Boolean isSupportWf = WfFlagUtils.isSupportWf(formVO);
		List<? extends ButtonVO> cardButtons = new ArrayList<>();
		if(isSupportWf) {
			//获取当前审批状态数据 yinbinchen
			editVO = wfProcCurService.getWfProcCurVO(editVO, funcVO, tenantid);
			boolean oldReadOnly = readOnly;
			readOnly = workflowButtonService.getReadonlyStatus(funcVO, editVO, readOnly);
			cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, editVO, readOnly);
			workflowButtonService.addWorkFlowButtons(cardButtons, formParamVO.getFuncCode(), editVO, readOnly, oldReadOnly);
			CardCtrlVO cardCtrlVO = workflowButtonService.getCardCtrlVOByNodeConfig(editVO, formParamVO.getFuncCode(), tenantid, userid);
			cardDataVO.setCardCtrlVO(cardCtrlVO);
		} else {
			cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, editVO, readOnly);
		}
		cardDataVO.setBtnList(cardButtons);
		cardDataVO.setReadonly(readOnly);
		
		ActionHandler.handleLastGetCardVO(action, cardDataVO, formParamVO, editVO);
		return cardDataVO;
	}

	

	@Override
	public CardDataVO getAddVO(FormParamVO formParamVO, OrgPermissionDTVO dto) throws HDException {
		Long tenantid = dto.getTenantid();
		Long userid = dto.getUserid();
		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);
		FormVO formVO = formService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
		
		final AbstractVO vo = new BaseVO();
		assignDefaultValue(formParamVO, vo, tenantid);
		AbstractVO editVO = getEditVO(action, formParamVO, formVO, tenantid, new IEditVOGetter() {
			
			@Override
			public AbstractVO getEditVO() {
				return vo;
			}
		}, dto);
		
		if(action!=null) {
			action.beforeGetCardVO(editVO, formParamVO, false);
		}
		
		editVO = assignFuncLinkItemValue(formParamVO, editVO, tenantid);
		editVO = funcTreeService.assignFunctreeParam(editVO, formParamVO, tenantid);
		editVO.set(SyConstant.TENANT_STR, TenantUtil.getCurrentDataTenantid(tenantid));
		editVO = code2NameForQuerySelectAndFormual(editVO, formVO.getForm_code(), tenantid);
		
		if(action!=null) {
			action.afterGetCardVO(editVO, formParamVO, false);
		}
		
//		Boolean isSupportWf = WfFlagUtils.isSupportWf(formVO);	
		List<? extends ButtonVO> cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, editVO, false);
////		List<ButtonCacheVO> cardButtons = buttonService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, false);
		CardDataVO cardDataVO = new CardDataVO();
//		if(isSupportWf) {
//			workflowButtonService.addWorkFlowButtons(cardButtons, formParamVO.getFuncCode(), editVO, false);
//			CardCtrlVO cardCtrlVO = workflowButtonService.getCardCtrlVOByNodeConfig(editVO, formParamVO.getFuncCode(), tenantid, userid);
//			cardDataVO.setCardCtrlVO(cardCtrlVO);
//		}
		if(ObjectUtil.isNotEmpty(cardButtons)) {
			for(int i=cardButtons.size()-1; i>=0; i--) {
				ButtonVO btn = cardButtons.get(i);
				if("cardSave".equals(btn.getBtn_code()) || "saveBack".equals(btn.getBtn_code())) {
					continue;
				}
				cardButtons.remove(i);
			}
		}
		
		cardDataVO.setData(editVO);
		cardDataVO.setBtnList(cardButtons);
		ActionHandler.handleLastGetCardVO(action, cardDataVO, formParamVO, editVO);
		return cardDataVO;
	}
	
	@Override
	public AbstractVO code2NameForQuerySelectAndFormual(AbstractVO editVO, String formCode, Long tenantid) throws HDException {
		List<? extends FormItemVO> items = formItemService.getFormItemsByFormcode(formCode, tenantid);
		for(FormItemVO itemVO : items) {
			if(InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) {
				Code2NameHandleUtils.handleQuerySelector(editVO, itemVO, tenantid);
			}
		}
		formualDBService.processDBFormual(editVO, items, tenantid);
		return editVO;
	}

	/**
	 * 得到复制出来新的vo
	 *
	 * @see com.hayden.hap.common.formmgr.itf.ICardFormService#getCopyVO(com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @date 2016年12月1日
	 */
	@Override
	public <T extends AbstractVO> T getCopyVO(T t) {
		String pkColName = tableDefService.getPkColName(t.getTableName());
		t.set(pkColName, null);
		return t;
	}
	
	/**
	 * 为模板保存提供的一个保存方法，起一个新事物，独立回滚
	 * @throws HDException 
	 * @author liyan
	 * @date 2016年11月24日
	 */
	@Override
	public ReturnResult<CardDataVO> save__RequiresNew(FormParamVO formParamVO) throws HDException {
		ReturnResult<CardDataVO> result = save(formParamVO);
		return result;
		
	}

	@Override
	@Transactional
	public ReturnResult<CardDataVO> cardCopy(FormParamVO formParamVO, AbstractVO originalVO, Long tenantid) throws HDException {

		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantid);
		ObjectUtil.validNotNull(formVO, "根据功能编码："+formParamVO.getFuncCode()+",没有找到表单对象|"+tenantid);

		IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
		//目前前端不支持平台级按钮自定义参数
		String changeItemCodes = formParamVO.getRequest().getParameter(CARD_COPY_CHANGE_PARAM);
		//后端获取平台级按钮参数
		if (StringUtils.isEmpty(changeItemCodes)) {
			IButtonPCService buttonService = (IButtonPCService) AppServiceHelper.findBean("buttonPCService");
			List<ButtonPCVO> buttons = (List<ButtonPCVO>) buttonService.getBtnsByFormcode(formParamVO.getFuncCode(), tenantid);
			for (ButtonPCVO buttonPCVO : buttons) {
				if (buttonPCVO.getBtn_code().equals("saveAndCopy")) {
					String param = buttonPCVO.getBtn_param();
					if (!StringUtils.isEmpty(param)) {
						changeItemCodes = param.split("=")[1];
					}
				}
			}
		}
		if(action==null && StringUtils.isEmpty(changeItemCodes)) {
			throw new HDException("复制操作需要action支持或者在按钮中配置改变字段...");
		}
		
		String tableName = formVO.getOpera_table_code();
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		String classPath = tableDefVO.getClassname();
		AbstractVO abstractVO = null;
		try {
			Class<?> clazz = Class.forName(classPath);
			abstractVO = (AbstractVO)clazz.newInstance();
		}catch(Exception e) {
			throw new HDException(e);
		}

		abstractVO.setTableName(tableName);
		String pkColName = tableDefVO.getPkColumnVO().getColcode();

		if(originalVO==null) {
			new ReturnResult<CardEditVO>(null,new Message("该记录不存在或已被删除...",MessageLevel.ERROR));
		}

		List<AbstractVO> list = new ArrayList<>();
		list.add(originalVO);

		//如果按钮配置了改变哪些字段，则通过配置改变
		if(StringUtils.isNotEmpty(changeItemCodes)) {
			changeItemByCopy(originalVO,changeItemCodes);
		}

		if(action!=null) {
			List<Message> errorList = action.beforeCardCopy(formParamVO, originalVO);
			ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());
		}

		dataCopyService.setNullPrimaryKey(originalVO, pkColName);
		
		//对vo进行验证
		//List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
		//防止vo中tenantid值为空
		if(originalVO.getLong(SyConstant.TENANT_STR) == null){
			originalVO.set(SyConstant.TENANT_STR, tenantid);
		}
		//动态获取表单字段
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), originalVO);

		List<Message> validateResult = ValidateUtils.validate(originalVO, formItemVOs, tenantid);
		if(validateResult.size()>0) {//如果验证未通过，返回验证信息
			return new ReturnResult<CardDataVO>(null,validateResult);
		}

		VOSet<AbstractVO> afterVoSet = baseService.insertBatch(list);
		dataCopyService.linkCopy(formParamVO.getFuncCode(), tenantid, afterVoSet.getVoList());

		if(action!=null) {
			List<Message> errorList = action.afterCardCopy(formParamVO, originalVO);
			ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());
		}

		CardDataVO cardEditVO = new CardDataVO();
		cardEditVO.setData(afterVoSet.getVoList().get(0));
		Boolean isSupportWf = WfFlagUtils.isSupportWf(formVO);
		List<? extends ButtonVO> cardButtons = new ArrayList<>();
		AbstractVO editVO = afterVoSet.getVoList().get(0);
		Long userid = CurrentEnvUtils.getUserId();
		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);
		boolean readOnly = false;
		if(isSupportWf) {
			//获取当前审批状态数据 yinbinchen
			editVO  = wfProcCurService.getWfProcCurVO(editVO, funcVO, tenantid);
			boolean oldReadOnly = readOnly;
			readOnly = workflowButtonService.getReadonlyStatus(funcVO, editVO, readOnly);
			cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, editVO, readOnly);
			workflowButtonService.addWorkFlowButtons(cardButtons, formParamVO.getFuncCode(), editVO, readOnly, oldReadOnly);
			CardCtrlVO cardCtrlVO = workflowButtonService.getCardCtrlVOByNodeConfig(editVO, formParamVO.getFuncCode(), tenantid, userid);
			cardEditVO.setCardCtrlVO(cardCtrlVO);
		} else {
			cardButtons = buttonAuthzService.getCardButtons(userid, formParamVO.getFuncCode(), tenantid, editVO, readOnly);
		}
		cardEditVO.setBtnList(cardButtons);
//		cardEditVO.setRefreshVO(formParamVO.getRefreshVO());
		Message message = new Message("已复制成功");
		ActionHandler.handleLastGetCardVO(action, cardEditVO, formParamVO, afterVoSet.getVoList().get(0));
		return new ReturnResult<CardDataVO>(cardEditVO,message);
	}

	private void changeItemByCopy(AbstractVO vo, String changeItemCodes) {
		if(!StringUtils.isEmpty(changeItemCodes)) {
			String[] changeItemCodeArr = changeItemCodes.split(",");
			for(String changeItemCode : changeItemCodeArr) {
				String value = vo.getString(changeItemCode);
				if(StringUtils.isNotEmpty(value)) {
					vo.set(changeItemCode,value+"(复制)");
				}
			}
		}
	}

	@Override
	public String getHelpInfo(String funccode, String fitemcode, Long tenantid) {
		if(StringUtils.isEmpty(fitemcode)) {
			return null;
		}
		
		if(!SyConstant.TENANT_HD.equals(tenantid))
			return null;
		
		List<? extends FormItemVO> list = formItemService.getFormItemsByFunccode(funccode, tenantid);
		FormItemVO itemVO = null;
		for(FormItemVO temp : list) {
			if(fitemcode.equals(temp.getFitem_code())) {
				itemVO = temp;
			}
		}
		
		if(itemVO==null) {
			return null;
		}
		
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("form_item_id", itemVO.getFitem_id());
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		
		VOSet<FormItemCommentVO> voset = baseService.query(new FormItemCommentVO(), dynaSqlVO);
		if(ObjectUtil.isNotEmpty(voset.getVoList())) {
			return voset.getVoList().get(0).getContent();
		}
		
		return null;
	}
	
}
