package com.hayden.hap.common.formmgr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hayden.hap.common.attach.itf.IAttachService;
import com.hayden.hap.common.authz.button.itf.IButtonAuthzService;
import com.hayden.hap.common.common.entity.*;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.ShouldBeCatchException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.orm.sql.SqlBuilder;
import com.hayden.hap.common.db.orm.sql.SqlBuilderFactory;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.db.util.ReflectUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.entity.TreeNodeVO;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.enumerate.*;
import com.hayden.hap.common.form.entity.*;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.action.ActionHandler;
import com.hayden.hap.common.formmgr.entity.*;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.*;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.utils.Code2NameHandleUtils;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.formmgr.utils.ValidateUtils;
import com.hayden.hap.common.formual.itf.IFormualDBService;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkDataService;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.func.itf.IQueryChildrenService;
import com.hayden.hap.common.orgpermission.entity.OrgPermissionDTVO;
import com.hayden.hap.common.orgpermission.itf.IOrgPermissionService;
import com.hayden.hap.common.security.util.WebUtils;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.*;
import com.hayden.hap.common.utils.properties.RqReportPropertiesUtil;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import sun.misc.BASE64Decoder;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月3日
 */
@Service("listFormService")
public class ListFormServiceImpl implements IListFormService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ListFormServiceImpl.class);	

	protected final static int PAGE_SIZE = 20;

	/**
	 * 需要分页查询
	 */
	private final static boolean NEED_PAGE = true;

	/**
	 * 不需要分页查询
	 */
	private final static boolean NOT_NEED_PAGE = false;

	/**
	 * 需要字典返名称
	 */
	private final static boolean NEED_CODE_TO_NAME = true;

	/**
	 * 不需要字典返名称
	 */
	private final static boolean NOT_NEED_CODE_TO_NAME = false;

	@Autowired
	private IFormService formService;

	@Autowired
	private IFormItemService formItemService;

	@Autowired
	private IFormItemPCService formItemPCService;
	
	@Autowired
	private IFuncService funcService;

	@Autowired
	private IFuncLinkService funcLinkService;

	@Autowired
	private ITableDefService tableDefService;

	@Autowired
	private IBaseService baseService;

	@Autowired
	private IFuncLinkDataService funcLinkDataService;

	@Autowired
	private IDictService dictService;

	@Autowired
	private IFormQueryService formQueryService;

	@Autowired
	private IOrgPermissionService orgPermissionService;

	@Autowired
	private IFuncTreeService funcTreeService;

	@Autowired
	private IAttachService attachService;

	@Autowired
	private IDataCopyService dataCopyService;

	@Autowired
	private IQueryChildrenService queryChildrenService;

	@Autowired
	private IFormualDBService formualDBService;

	@Autowired
	private IButtonAuthzService buttonAuthzService;

	@Autowired
	private IQueryService queryService;

	/**
	 * 判定是否是树的维护
	 * 判定依据为：功能对应表单的操作表等于全息查询字段配置的字典的数据表
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年2月19日
	 */
	@Override
	public boolean isTreeMaintenance(String funcCode,Long tenantid) throws HDException {
		if("SY_DICT_DATA".equals(funcCode.toUpperCase()) 
				|| "MGR_DICT_DATA".equals(funcCode.toUpperCase())) {//字典数据功能都是树维护
			return true;
		}

		FuncPCVO funcVO = (FuncPCVO) funcService.queryByFunccode(funcCode, tenantid);
		if(StringUtils.isNotEmpty(funcVO.getFunc_tree())) {//如果配置了全息查询字段
			String[] fitemCodes = funcVO.getFunc_tree().split(",");
			List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
			out:for(String fitemCode : fitemCodes) {
				for(FormItemVO formItemVO : formItemVOs) {
					if(fitemCode.equals(formItemVO.getFitem_code())) {//找到对应的表单字段
						String inputConfig = formItemVO.getFitem_input_config();
						if(StringUtils.isNotEmpty(inputConfig)) {//如果该表单字段配置了输入设定
							DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
							String dictCode = inputConfigVO.getDictcode();
							DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);

							FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
							if(dictVO==null || formVO==null) {//如果没找到字典或表单，判定不是树维护
								continue out;
							}

							//如果两个表名相同，则是树的维护啦
							if(dictVO.getDict_t_table().toLowerCase().equals(formVO.getOpera_table_code().toLowerCase())) {
								return true;
							}
						}
						continue out;
					}
				}
			}
		}
		return false;
	}

	@Override
	@Transactional
	public ReturnResult<ListDataVO> listUpdate(FormParamVO formParamVO) throws HDException {
		ReturnResult<ListDataVO> returnMessage = new ReturnResult<>();
		Long tenantid = CurrentEnvUtils.getTenantId();

		String tableName = funcService.getOperaTableNameOfFunc(formParamVO.getFuncCode(),tenantid);
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//查询表信息
		Class<? extends AbstractVO> voClass = tableDefService.getVOClass(tableDefVO);

		String pkName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		ObjectUtil.validNotNull(pkName, "table:"+tableName+"的主键列没有找到");		

		List<? extends FormItemVO> editItems = formItemService.getListEditFitems(formParamVO.getFuncCode(), tenantid);
		List<String> sqlColumnList = formItemService.getListEditFitemNames(editItems);				
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setSqlColumnList(sqlColumnList);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);

		List<? extends AbstractVO> voList;
		try {
			voList= JsonUtils.parseArray(formParamVO.getDataBody(), voClass);
		}catch(HDException e) {
			throwPropException(e,editItems);
			throw e;
		}

		List updateVOlist=new ArrayList();
		List insetVOList=new ArrayList();

		BaseSettingUtils.setU_TPD(voList);
		if(voList.size()>0) {
			for(AbstractVO abstractVO : voList) {
				if(abstractVO.getLong(pkName)!=null){
					updateVOlist.add(abstractVO);
				}else {
					insetVOList.add(abstractVO);
				}
				abstractVO.setTableName(tableName);
			}
		}



		List<Message> validateResult = ValidateUtils.validate(voList, editItems,tenantid);
		if(validateResult.size()>0) {//如果验证未通过，返回验证信息
			returnMessage.setMessages(validateResult);
			return returnMessage;
		}

		BatchUpdateParamVO batchUpdateParamVO = new BatchUpdateParamVO();
		batchUpdateParamVO.setDynaSqlVO(dynaSqlVO);
		batchUpdateParamVO.setPkName(pkName);
		batchUpdateParamVO.setVoList(voList);

		List<Message> errorList = null;

		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantid);
		IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
		if(action!=null) {
			errorList = action.beforeListUpdate(formParamVO, batchUpdateParamVO);
		}
		ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());

		if(ObjectUtil.isTrue(formVO.getIs_validate_consistency())) {
			boolean consistencyResult = baseService.validateConsistency(voList);
			if(!consistencyResult) {
				throw new HDException("数据状态已变更，请刷新");
			}
		}

		//关联功能的关联更新
		funcLinkService.linkUpdate(updateVOlist, formParamVO.getFuncCode());
		//关联数据的关联更新
		funcLinkDataService.linkUpdate(updateVOlist, formParamVO.getFuncCode(),tenantid);

		if(ObjectUtil.isNotEmpty(updateVOlist)){
			baseService.updateBatch(updateVOlist, dynaSqlVO);
		}

		if(ObjectUtil.isNotEmpty(insetVOList)){
			dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
			baseService.insertBatch(insetVOList, dynaSqlVO);
		}

		List<Long> pks = ReflectUtil.invokeGetBatch(voList, pkName.toLowerCase(),Long.class);
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		List<? extends AbstractVO> result = baseService.queryByPKsOfLongAndTenantid(voList.get(0), pks, currentDataTenantid).getVoList();

		//将修改后查询的结果赋值给batchUpdateParamVO，以供action操作；因为原先的list里的属性不全
		batchUpdateParamVO.setVoList(result);
		if(action!=null) {
			errorList = action.afterListUpdate(formParamVO, batchUpdateParamVO);
		}
		ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());
		ListDataVO listDataVO = new ListDataVO();
		listDataVO.setVoList(result);
		listDataVO.setRefreshVO(formParamVO.getRefreshVO());
		returnMessage.setData(listDataVO);

		DictUtils.evictByTablename(tableName, currentDataTenantid);
		ActionHandler.handleLastListUpdate(action, listDataVO, formParamVO);
		return returnMessage;
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

	@Override
	public  VOSet<? extends AbstractVO> listQuery(FormParamVO formParamVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		return query(formParamVO, NEED_PAGE, NOT_NEED_CODE_TO_NAME, tenantid);
	}

	@Override
	public  VOSet<? extends AbstractVO> listQuery4Mobile(FormParamVO formParamVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		return query(formParamVO, NEED_PAGE, NEED_CODE_TO_NAME, tenantid);
	}

	@Override
	public  VOSet<? extends AbstractVO> listQuery4querySelect(FormParamVO formParamVO) throws HDException {		
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(CurrentEnvUtils.getTenantId());
		if(SyConstant.NONTENANTID_FUNC.contains(formParamVO.getFuncCode())) {
			currentDataTenantid = SyConstant.TENANT_HD;
		}

		return query(formParamVO, NEED_PAGE, NOT_NEED_CODE_TO_NAME, currentDataTenantid);
	}


	/**
	 * 构造列表查询的DynaSqlVO
	 * @param formParamVO
	 * @param formVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年9月26日
	 */
	private DynaSqlVO getListQueryDynaSqlVO(FormParamVO formParamVO,FormVO formVO,List<? extends FormItemVO> formItemVOs,Long tenantid) throws HDException {
		ReqParamVO param = formParamVO.getReqParamVO();

		//所有的按主键升序排列
		String pkColumn = tableDefService.getPkColName(formVO.getQuery_table_code());

		DynaSqlVO dynaSqlVO = null;
		if(ObjectUtil.isNotEmpty(formParamVO.getPkValues())) {
			dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam(pkColumn, formParamVO.getPkValues());
			String orderByClause = buildOrderByClause(formParamVO, formVO, formItemVOs, pkColumn);
			dynaSqlVO.setOrderByClause(orderByClause);
		}else {		
			dynaSqlVO = buildWhereClause(formParamVO,tenantid);
			String orderByClause = buildOrderByClause(formParamVO, formVO, formItemVOs, pkColumn);
			dynaSqlVO.setOrderByClause(orderByClause);
			if(param.getPage()!=null)
			{
				int page = param.getPage();
				if(page < 1)
					page = 1;
				int pageSize = param.getRows()!=null?param.getRows():PAGE_SIZE;
				dynaSqlVO.createPage(page, pageSize);
			}			
		}
		return dynaSqlVO;
	}

	private VOSet<? extends AbstractVO> listQuery(DynaSqlVO dynaSqlVO,FormParamVO formParamVO,
			List<? extends FormItemVO> formItemVOs) throws HDException {
		VOSet<? extends AbstractVO> voSet = null;
		Long tenantid = CurrentEnvUtils.getTenantId();
		String queryTable = funcService.getQueryTableNameOfFunc(formParamVO.getFuncCode(),tenantid);

		voSet = baseService.query(queryTable, dynaSqlVO);
		//TODO 以后重查逻辑将写入底层实现
		if(!ObjectUtil.isNotEmpty(voSet.getVoList()) && voSet.getPage()!=null && voSet.getPage().getPageNo()>1) {				
			int totalPage = voSet.getPage().getTotalPages();
			dynaSqlVO.getPage().setPageNo(totalPage);
			voSet = baseService.query(queryTable, dynaSqlVO);
		}

		return voSet;
	}

	private String getPKorder(String orderByClause) {
		String asc = " asc";
		String desc = " desc";
		if(!StringUtils.isNotEmpty(orderByClause)) 
			return asc;

		if(orderByClause.toLowerCase().indexOf(desc)>0) 
			return desc;

		return asc;
	}

	@Override
	@Transactional
	public ReturnResult<ListDataVO> listDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);

		IAction action = formService.getActionByFuncCode(formParamVO.getFuncCode(), tenantid);
		String operaTable = funcService.getOperaTableNameOfFunc(formParamVO.getFuncCode(), tenantid);
		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);

		List<BaseVO> voList = null;
		boolean isNeedQueryVO = isNeedQueryVO(funcVO);
		if(isNeedQueryVO) {			
			VOSet<BaseVO> voset = baseService.queryByPKsOfLongAndTenantid(new BaseVO(operaTable), primaryKeys, currentDataTenantid);
			voList = voset.getVoList();

			boolean isNotAllowDelete = isNotAllowDelete(funcVO,operaTable,voList,tenantid);
			if(isNotAllowDelete) {
				throw new HDException("您要删除的数据中包含不允许删除的记录，删除失败");
			}
		}	

		List<Message> beforErrorList = null;

		if(action!=null) {
			beforErrorList = action.beforeListDeleteBatch(formParamVO,primaryKeys);
		}
		ExceptionHandlerUtils.handle(beforErrorList, formParamVO.getRequest());

		//删除附件
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
		boolean hasAttach = false;
		for(FormItemVO itemVO : formItemVOs) {//如果有附件再去，不然就不去了
			if(ElementTypeEnum.FILE.getCode().equals(itemVO.getFitem_input_element()) || ElementTypeEnum.IMG.getCode().equals(itemVO.getFitem_input_element())){
				hasAttach = true;
				break;
			}			
		}

		if(hasAttach) {
			if(!ObjectUtil.isNotEmpty(voList)){
				VOSet<BaseVO> voset = baseService.queryByPKsOfLongAndTenantid(new BaseVO(operaTable), primaryKeys, currentDataTenantid);
				voList = voset.getVoList();
			}
			TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(operaTable);// 操作表信息
			String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO
					.getPkColumnVO().getColcode() : null;
					attachService.deleteRecordAttach(formParamVO, voList, pkColName, tenantid);
		}

		//关联功能的关联删除
		funcLinkService.linkDelete(primaryKeys, formParamVO.getFuncCode());
		//关联数据的关联删除
		funcLinkDataService.linkDelete(primaryKeys, formParamVO.getFuncCode(),tenantid);


		baseService.deleteByPKsOfLongAndTenantid(operaTable, primaryKeys,currentDataTenantid);

		List<Message> afterErrorList = null;
		if(action!=null) {
			afterErrorList = action.afterListDeleteBatch(formParamVO,primaryKeys);
		}
		ExceptionHandlerUtils.handle(afterErrorList, formParamVO.getRequest());


		ReturnResult<ListDataVO> returnResult = new ReturnResult<>();
		ListDataVO listDataVO = new ListDataVO();
		returnResult.setData(listDataVO);
		if(!CollectionUtils.isEmpty(beforErrorList) || !CollectionUtils.isEmpty(afterErrorList) ) {
			List<Message> list = new ArrayList<Message>();
			if(beforErrorList!=null && !beforErrorList.isEmpty()) {
				list.addAll(beforErrorList);
			}
			if(afterErrorList!=null && !afterErrorList.isEmpty()) {
				list.addAll(afterErrorList);
			}
			returnResult.setMessages(list);
		}

		DictUtils.evictByTablename(operaTable, tenantid);

		ActionHandler.handleLastListDelete(action, listDataVO, formParamVO, primaryKeys);
		return returnResult;
	}

	/**
	 * 列表做批量删除动作时候，先判定是否需要把业务VO给查出来<br/>
	 * 如果功能里配置了是否允许删除表达式，而且表达式中含有字段变量，则需要查<br/>
	 * 如果功能里配置了工作流，则需要查<br/>
	 * 其它不用查<br/>
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月17日
	 */
	private boolean isNeedQueryVO(FuncVO funcVO) {
		String notAllowDelExp = funcVO.getFunc_delete_exp();
		if(VariableUtils.hasFitemParam(notAllowDelExp))
			return true;

		if(!WorkflowTypeEnum.FORBIDDEN.getCode().equals(funcVO.getFunc_wf_link_proctype())) 
			return true;

		return false;
	}

	/**
	 * 计算不能删除表达式，判定是否不允许被删除
	 * @param formParamVO
	 * @param primaryKeys
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年7月1日
	 */
	private boolean isNotAllowDelete(FuncVO funcVO,String operaTable,List<BaseVO> voList,
			Long tenantid) throws HDException {
		if(ObjectUtil.isEmpty(voList))
			return false;
		String notAllowDelExp = funcVO.getFunc_delete_exp();
		if(StringUtils.isNotEmpty(notAllowDelExp)) {
			notAllowDelExp = VariableUtils.replaceSystemParam(notAllowDelExp);

			if(VariableUtils.hasFitemParam(notAllowDelExp)) {
				for(BaseVO baseVO : voList) {
					notAllowDelExp = VariableUtils.replaceFormItemParam(notAllowDelExp, baseVO);
					boolean evalResult;
					try {
						evalResult = JavaScriptExpressUtils.eval(notAllowDelExp);
					} catch (ScriptException e) {
						logger.error(e.getMessage(), e);
						throw new HDException("是否允许删除表达式错误："+notAllowDelExp);
					}
					if(evalResult) {
						return evalResult;
					}
				}
			}else {
				boolean evalResult;
				try {
					evalResult = JavaScriptExpressUtils.eval(notAllowDelExp);
				} catch (ScriptException e) {
					logger.error(e.getMessage(), e);
					throw new HDException("是否允许删除表达式错误："+notAllowDelExp);
				}
				if(evalResult) {
					return evalResult;
				}
			}
		}

		if(!WorkflowTypeEnum.FORBIDDEN.getCode().equals(funcVO.getFunc_wf_link_proctype())) {
			for(BaseVO baseVO : voList) {
				//审核中，不让删除；
				if(AuditStateEnum.AUDITING.getCode().equals(baseVO.get("wf_audit_state"))) {
					return true;
				}
				//审核结束
				if(AuditStateEnum.AUDITED.getCode().equals(baseVO.get("wf_audit_state"))) {
					if(!AuditedAllowOperEnum.DELETE.getCode().equals(funcVO.getFunc_wf_finish_allow())
							&& !AuditedAllowOperEnum.MODIFY_AND_DELETE.getCode().equals(funcVO.getFunc_wf_finish_allow())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public VOSet<? extends AbstractVO> queryAll(FormParamVO formParamVO)
			throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		return query(formParamVO, NOT_NEED_PAGE, false, tenantid);
	}

	/**
	 * 查询
	 * @param formParamVO
	 * @param isNeedPage 是否需要分页查
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年11月9日
	 */
	private VOSet<? extends AbstractVO> query(FormParamVO formParamVO, boolean isNeedPage, boolean isCode2Name, Long tenantid) throws HDException {
		IAction action = formService.getActionByFuncCode(formParamVO.getFuncCode(), tenantid);
		if(action!=null) {
			action.beforeListQuery(formParamVO);
		}

		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(),tenantid);
		if(StringUtils.isEmpty(formVO.getOpera_table_code())) {
			if(action!=null) {
				try {
					return action.changedListQuery(new DynaSqlVO(), formParamVO);
				} catch (ShouldBeCatchException e) {
					throw new HDException("没有操作表，就应该重写数据查询控制器");
				}
			}
			throw new HDException("没有操作表，就应该重写数据查询控制器");
		}		

		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);

		DynaSqlVO dynaSqlVO = getListQueryDynaSqlVO(formParamVO, formVO, formItemVOs,tenantid);
		if(!isNeedPage) {
			dynaSqlVO.setPage(null);
		}

		boolean isNeedOrgPermission = !(action!=null && !action.isNeedOrgPermission(formParamVO));
		if(isNeedOrgPermission) {
			OrgPermissionDTVO dto = new OrgPermissionDTVO();
			dto.setFuncCode(formParamVO.getFuncCode());
			dto.setTenantid(tenantid);	
			//TODO... 管理租户的情况(解决方案：管理租户应该通过action控制使部门权限失效)
			dto.setUserid(CurrentEnvUtils.getUserId());
			dto.setOrgid(CurrentEnvUtils.getOrgId());
			String orgPermissionClause = orgPermissionService.getOrgidsStr4OrgPermission(dto);
			if(action!=null) {
				orgPermissionClause = action.changeOrgPermissionClause(orgPermissionClause, formParamVO);
			}
			dynaSqlVO.addWhereClause(orgPermissionClause);
		}

		VOSet<? extends AbstractVO> voSet = null;

		if(action!=null) {
			//尝试调用action里的改变查询接口
			try {
				voSet = action.changedListQuery(dynaSqlVO, formParamVO);
			} catch (ShouldBeCatchException e) {
				//抛出了ShouldBeCatchException异常，说明并没有重写此方法，需要走平台查询
				voSet = listQuery(dynaSqlVO, formParamVO, formItemVOs);
			}
			//对于要在列表页面显示的附件进行预先查询并放入columvolist里，同时查出列表上要显示的附件数量
			attachService.getListAttachVOsAndNum(voSet,formParamVO,formItemVOs, tenantid);
			action.afterListQuery(formParamVO,voSet);
		}else {
			//没有action，当然走平台查询了
			voSet = listQuery(dynaSqlVO, formParamVO, formItemVOs);
			//对于要在列表页面显示的附件进行预先查询并放入columvolist里，同时查出列表上要显示的附件数量
			attachService.getListAttachVOsAndNum(voSet,formParamVO,formItemVOs, tenantid);
		}	

		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		for(FormItemVO itemVO : formItemVOs) {
			if(InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) {
				Code2NameHandleUtils.handleQuerySelector(voSet.getVoList(), itemVO, currentDataTenantid);				
			}

			if(isCode2Name && InputTypeEnum.DICT_NEW.getCode().equals(itemVO.getFitem_input_type())) {
				Code2NameHandleUtils.handleDictNN(voSet.getVoList(), itemVO, currentDataTenantid);
			}
		}	
		formualDBService.processDBFormual(voSet.getVoList(), formItemVOs, currentDataTenantid);
		return voSet;
	}



	@Override
	public DynaSqlVO buildWhereClause(FormParamVO formParamVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		return buildWhereClause(formParamVO, tenantid);
	}

	private DynaSqlVO buildWhereClause(FormParamVO formParamVO, Long tenantId) throws HDException {
		ReqParamVO param = formParamVO.getReqParamVO();
		DynaSqlVO dynaSqlVO = new DynaSqlVO();

		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantId);
		String formWhereClause = formVO.getWhere_str();
		//处理表单过滤条件的系统变量		
		if(StringUtils.isNotEmpty(formWhereClause)) {
			formWhereClause = VariableUtils.replaceSystemParam(formWhereClause);
			dynaSqlVO.addWhereClause(formWhereClause);
		}

		FuncVO funcVO = funcService.queryByFunccode(formParamVO.getFuncCode(), tenantId);
		if(StringUtils.isNotEmpty(funcVO.getFunc_where())) {
			String funcWhereClause = VariableUtils.replaceSystemParam(funcVO.getFunc_where());
			dynaSqlVO.addWhereClause(funcWhereClause);
		}

		if(StringUtils.isNotEmpty(formParamVO.getExtWhere())) {//像查询选择这样的场景是可能有额外条件的
			dynaSqlVO.addWhereClause(formParamVO.getExtWhere());
		}

		//优先级排序：高级查询>查询模板>普通查询
		if(StringUtils.isNotEmpty(param.getFormQuerySql())) {//高级查询条件
			String sqlJson;
			try {
				sqlJson = new String(new BASE64Decoder().decodeBuffer(param.getFormQuerySql()));	
				sqlJson = EscapeUtils.unescape(sqlJson);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new HDException(e);
			}
			List<FormQueryItemVO> formQueryItemVOs = JsonUtils.parseArray(sqlJson, FormQueryItemVO.class);
			String formQuerySql = formQueryService.handleQuerySQL(formParamVO.getFuncCode(), formQueryItemVOs, tenantId);
			dynaSqlVO.addWhereClause(formQuerySql);
		}else if(param.getFormQueryId()!=null && param.getFormQueryId()!=SyConstant.FORM_QUERY_ALL_ID) {//查询模板
			String formQuerySql = formQueryService.getFormQuerySql(formParamVO.getFuncCode(), tenantId, param.getFormQueryId());
			dynaSqlVO.addWhereClause(formQuerySql);
		}else if(StringUtils.isNotEmpty(param.getQueryParam())) {//处理普通查询条件
			logger.debug("查询条件："+param.getQueryParam());

			String queryStr = queryService.parseQueryStr(param.getQueryParam(),formParamVO.getFuncCode());

			if(StringUtils.isNotEmpty(queryStr)) {
				dynaSqlVO.addWhereClause(queryStr);
			}
		}

		if(StringUtils.isNotEmpty(param.getFuncTreeQueryParam())) {

			//			String queryStr = formService.parseFuncTreeQueryStr(param.getFuncTreeQueryParam(), formParamVO.getFuncCode());
			//TODO... 考虑管理租户表单情况
			Long tenantid = CurrentEnvUtils.getTenantId();
			List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), tenantid);
			String queryStr = funcTreeService.parseFuncTreeQueryStr(param.getFuncTreeQueryParam(),
					formParamVO.getFuncCode(), formItemVOs, tenantid);
			logger.debug("全息查询条件："+queryStr);

			if(StringUtils.isNotEmpty(queryStr)) {
				dynaSqlVO.addWhereClause(queryStr);
			}
		}

		if(StringUtils.isNotEmpty(param.getParentFuncCode())) {//处理关联关系 			
			List<FuncLinkVO> funcLinkList = funcLinkService.getFuncLink(param.getParentFuncCode(), formParamVO.getFuncCode(),tenantId);

			if(ObjectUtil.isNotEmpty(funcLinkList)) {
				String parentTableName = funcService.getOperaTableNameOfFunc(param.getParentFuncCode(),tenantId);			
				Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantId);
				AbstractVO parentVO = baseService.queryByPKAndTenantid(parentTableName, param.getParentEntityId(),currentDataTenantid);				

				if(parentVO==null) {
					throw new HDException("列表查询异常，由于父功能："+param.getParentFuncCode()+",记录id:"+param.getParentEntityId()+",不存在或已被删除");
				}
				FuncLinkVO funcLink = funcLinkList.get(0);
				dynaSqlVO.addWhereClause(queryChildrenService.parseFunclinkClause(funcLink.getLinkItems(), parentVO));

				if(StringUtils.isNotEmpty(funcLink.getLink_where())) {
					dynaSqlVO.addWhereClause(funcLink.getLink_where());
				}
			}
		}

		if(!(ObjectUtil.isTrue(formVO.getIs_mgr_tenant_data()) && SyConstant.TENANT_HD.equals(tenantId))) {
			dynaSqlVO.addWhereParam("tenantId", tenantId);
		}

		return dynaSqlVO;
	}

	/**
	 * 构造排序字符串
	 * @param formParamVO
	 * @param formItemVOs
	 * @return 
	 * @author zhangfeng
	 * @date 2016年11月9日
	 */
	private String buildOrderByClause(FormParamVO formParamVO,FormVO formVO, List<? extends FormItemVO> formItemVOs, String pkColumn) {
		StringBuilder result = new StringBuilder();
		ReqParamVO param = formParamVO.getReqParamVO();
		if(StringUtils.isNotEmpty(param.getSidx())) {//处理：
			String[] orderStrs = param.getSidx().split(",");
			for(String orderStr : orderStrs) {
				if(StringUtils.isEmpty(orderStr))
					continue;

				String[] orderConfig = orderStr.split(" ");
				String orderItem = orderConfig[0];
				String orderType = orderConfig.length>1?orderConfig[1]:"desc";

				FormItemVO orderItemVO = null;
				for(FormItemVO itemVO : formItemVOs) {
					if(orderItem.equals(itemVO.getFitem_code())) {
						orderItemVO = itemVO;
						break;
					}
				}

				if(orderItemVO==null) {//没有匹配到对应字段
					continue;
				}
				boolean orderable = FitemTypeEnum.TABLE.getCode().equals(orderItemVO.getFitem_type()) 
						|| FitemTypeEnum.VIEW.getCode().equals(orderItemVO.getFitem_type());
				if(!orderable)
					continue;

				//如果排序字段是字符串或大文本，需要处理中文排序问题
				if(orderItemVO!=null && 
						(DataTypeEnum.STRING.getCode().equals(orderItemVO.getFitem_data_type())
								|| DataTypeEnum.TEXT.getCode().equals(orderItemVO.getFitem_data_type()))) {
					SqlBuilder sqlBuilder = SqlBuilderFactory.createSqlBuilder();
					String orderSql = sqlBuilder.getOrderBySqlForZh_cn(orderItem, orderType);
					result.append(orderSql);					
				}else {
					result.append(orderItem + " " + orderType);
				}
				result.append(", ");
			}
			if(result.length()>1) {
				result.deleteCharAt(result.length()-1);
				result.deleteCharAt(result.length()-1);
			}
		}

		String formOrder = getFormOrder(formParamVO, formVO);		
		if(StringUtils.isNotEmpty(formOrder)) {//如果表单上配置了排序，列表上没有传递排序方式，则走表单的排序
			if(!StringUtils.isNotEmpty(result.toString())) {
				result.append(formOrder);
			}			
		}

		if(!StringUtils.isNotEmpty(result.toString())) {//没有任何排序，则按主键升序
			result.append(pkColumn+" asc");
		}else {//有排序，则添加主键的对应排序
			String pkOrder = getPKorder(result.toString());
			result.append(", "+pkColumn+pkOrder);
		}

		return result.toString();
	}

	private String getFormOrder(FormParamVO formParamVO, FormVO formVO) {//获取表单配置的排序
		boolean isQuerySelector = formParamVO.isQuerySelector();
		if(isQuerySelector) {
			return formVO.getQuick_order();
		}
		return formVO.getCommon_order();
	}

	@Override
	public List<String> sqlColumnList(List<? extends FormItemVO> formItemVOs)
			throws HDException {
		List<String> sqlColumnList = new ArrayList<String>();
		for (FormItemVO formItemVo : formItemVOs) {
			//表单字典项定义
			if (((FormItemPCVO)formItemVo).getFitem_show_list()==SyConstant.SY_TRUE) {
				sqlColumnList.add(formItemVo.getFitem_code());//表单属性定义
			}
		}
		return sqlColumnList;
	}

	@Override
	public List<DictDataWarperVO> getDictTree(FormParamVO formParamVO) {

		String itemCodes = ((FuncPCVO)formParamVO.getFuncVO()).getFunc_tree();
		List<DictDataWarperVO> result = new ArrayList<>();

		if (StringUtils.isNotEmpty(itemCodes)) {
			for (String itemCode : itemCodes.split(",")) {
				// String itemCode = itemCodes.split(",")[0];

				Long tenantid = CurrentEnvUtils.getTenantId();
				List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(),
						tenantid);

				for (FormItemVO formItemVO : formItemVOs) {
					if (itemCode.equals(formItemVO.getFitem_code())) {
						String inputconfig = formItemVO.getFitem_input_config();
						String[] configs = inputconfig.split(",");
						String dictCode = configs[0];

						result.addAll(DictUtils.getDictDataWithTopnode(dictCode));
					}
				}
			}
		}

		return result;
	}

	@Override
	public List<TreeNodeVO> getTreeData(FormParamVO formParamVO) throws HDException {

		String itemCodes = ((FuncPCVO)formParamVO.getFuncVO()).getFunc_tree();
		List<TreeNodeVO> result = new ArrayList<>();

		if (StringUtils.isNotEmpty(itemCodes)) {
			Long tenantid = CurrentEnvUtils.getTenantId();
			List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(),tenantid);
			
			for (String itemCode : itemCodes.split(",")) {


				for (FormItemVO formItemVO : formItemVOs) {
					if (itemCode.equals(formItemVO.getFitem_code())) {
						String inputconfig = formItemVO.getFitem_input_config();
						DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputconfig);

						String dictcode = inputConfigVO.getDictcode();
						if(!StringUtils.isEmpty(dictcode) && (dictcode.startsWith("sy_org") || dictcode.startsWith("SY_ORG"))) {
							result.addAll(DictUtils.getTreeDataWithTopnode(inputConfigVO,formParamVO.getFuncCode(),itemCode,formItemVO.getFitem_name()));
						}else {
							result.addAll(DictUtils.getTreeDataWithTopnode(inputConfigVO,itemCode,formItemVO.getFitem_name()));
						}
					}
				}
			}
		}

		return result;
	}

	@Override
	public AbstractVO getQueryDefaultVO(List<? extends FormItemVO> itemVOs, Long currentDataTenantid) throws HDException {
		AbstractVO vo = new PureVO();
		//TODO... 配置了两个查询方式的
		for(FormItemVO itemVO : itemVOs) {
			if(StringUtils.isNotEmpty(itemVO.getFitem_query_default())) {
				vo.set(itemVO.getFitem_code(), itemVO.getFitem_query_default());
			}
		}

		for(FormItemVO itemVO : itemVOs) {
			if(InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) {
				Code2NameHandleUtils.handleQuerySelector(vo, itemVO, currentDataTenantid);				
			}
		}	
		return vo;
	}

	@Override
	@Transactional
	public ReturnResult<CardEditVO> listCopy(FormParamVO formParamVO, Long primaryKey, Long tenantid) throws HDException {
		if(primaryKey==null)
			return new ReturnResult<>(null,new Message("请先勾选要复制的记录",MessageLevel.ERROR));

		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantid);
		ObjectUtil.validNotNull(formVO, "根据功能编码："+formParamVO.getFuncCode()+",没有找到表单对象|"+tenantid);

		IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
		if(action==null) {
			throw new HDException("复制操作需要action支持...");
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

		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		AbstractVO originalVO = baseService.queryByPKAndTenantid(abstractVO, primaryKey, currentDataTenantid);

		if(originalVO==null) {
			new ReturnResult<>(new Message("该记录不存在或已被删除...",MessageLevel.ERROR));
		}

		List<AbstractVO> list = new ArrayList<>();
		list.add(originalVO);

		List<Message> errorList = action.beforeListCopy(formParamVO, list);
		ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());

		dataCopyService.setNullPrimaryKey(originalVO, pkColName);

		//对vo进行验证
		List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(formParamVO.getFuncCode(), originalVO);
		List<Message> validateResult = ValidateUtils.validate(originalVO, formItemVOs, tenantid);
		if(validateResult.size()>0) {//如果验证未通过，返回验证信息
			return new ReturnResult<>(null,validateResult);
		}

		VOSet<AbstractVO> afterVoSet = baseService.insertBatch(list);
		dataCopyService.linkCopy(formParamVO.getFuncCode(), tenantid, afterVoSet.getVoList());

		errorList = action.afterListCopy(formParamVO, afterVoSet.getVoList());
		ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());

		CardEditVO cardEditVO = new CardEditVO();
		cardEditVO.setData(afterVoSet.getVoList().get(0));
		Message message = new Message("已复制成功");
		//		cardEditVO.setRefreshVO(new ListRefreshVO());
		return new ReturnResult<>(cardEditVO,message);
	}

	@Override
	public ModelAndView shorReport(FormParamVO formParamVO) throws HDException {
		ReqParamVO param = formParamVO.getReqParamVO();
		HttpServletRequest request = formParamVO.getRequest();
		String funcCode = formParamVO.getFuncCode();

		String queryStr=param.getQueryParam();
//		queryStr=URLDecoder.decode(URLDecoder.decode(queryStr, "utf-8"),"utf-8");
		param.setQueryParam(queryStr);
		Long tenantId = CurrentEnvUtils.getTenantId();

		IAction action = formService.getActionByFuncCode(funcCode, tenantId);
		if(action!=null) {
			action.beforeListQuery(formParamVO);
		}
		String uuid = UuidUtils.getUuid()+RandomStringUtils.randomAlphabetic(30);
		RedisTemplate redisTemplate_rq = (RedisTemplate)AppServiceHelper.findBean("redisTemplate_rq");
		Map<String,Object> userMap= new HashMap<String,Object>();
		userMap.put(SyConstant.USERID_STR, CurrentEnvUtils.getUserId());
		userMap.put(SyConstant.TENANT_STR,CurrentEnvUtils.getTenantId());
		userMap.put(SyConstant.USERNAME_STR,CurrentEnvUtils.getUserName());
		userMap.put(SyConstant.ORGNAME,CurrentEnvUtils.getOrgName());
		userMap.put(SyConstant.ORGID,CurrentEnvUtils.getOrgId());
		userMap.put("dataPermission",orgPermissionService.getOrgids4OrgPermission(CurrentEnvUtils.getUserId(),CurrentEnvUtils.getOrgId(),CurrentEnvUtils.getTenantId(),funcCode));
		redisTemplate_rq.opsForValue().set(uuid, userMap,RqReportPropertiesUtil.getToken(),TimeUnit.SECONDS);
		
		//TODO...以下三行为日志信息，有待删除
		JedisConnectionFactory factory = (JedisConnectionFactory)redisTemplate_rq.getConnectionFactory();
		logger.error("redis信息,adress:{}:{}/{}",factory.getHostName(),factory.getPort(),factory.getDatabase());
		logger.error("放入了uuid:"+uuid+",过期时间"+RqReportPropertiesUtil.getToken()+"秒");
		
		FuncVO funcVO = funcService.queryByFunccode(funcCode,tenantId);
		String queryJSON=formParamVO.getReqParamVO().getQueryParam();
		queryJSON=StringUtils.isBlank(queryJSON) ? "{}" : queryJSON;
		Map<String,Object> model=JSON.parseObject(queryJSON, Map.class);
		List<? extends FormItemVO> itemVOs = formItemService.getFormItemsByFunccode(funcCode, tenantId);
		List<Message> validateResult = ValidateUtils.validateQueryNotnull(itemVOs, model);
		if(validateResult.size()>0) {
			String errorStr = "";
			for(Message message : validateResult) {
				if (StringUtils.isEmpty(errorStr)) {
					errorStr += message.getMessage();
				}else {
					errorStr += ","+message.getMessage();
				}
			}
			throw new HDException(errorStr);
		}
		model.put("uuid", uuid);
		model.put(SyConstant.PCCONTEXT, request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath());
		String printViewPath ="";
		String reportURL = WebUtils.changeConfIp(request, RqReportPropertiesUtil.getRquestUrl(),"rqreport.rquestUrl");
		printViewPath =reportURL+"/"+funcVO.getModulecode()+"/"+funcVO.getFunc_code()+"/reportCommonShow";
		if(StringUtils.isNotBlank(formParamVO.getRpx())){
			model.put("rpx", formParamVO.getRpx()+".rpx");
		}
		else if(StringUtils.isBlank(funcVO.getFunc_param())){
			model.put("rpx", funcVO.getFunc_code()+".rpx");
		}
		else{
			printViewPath =reportURL+"/"+funcVO.getFunc_param();
		}
		RedirectView redirectView = new RedirectView(printViewPath);
		return new ModelAndView(redirectView,model);
	}
	@Override
	public ReturnResult<List<? extends AbstractVO>> batchEditByFItemConfig(FormParamVO formParamVO, Collection<Long> primaryKeys,
			AbstractVO vo, Long tenantid) throws HDException {

		ReturnResult<List<? extends AbstractVO>> returnMessage = new ReturnResult<>();
		Map<String, Object> batchEditFitems = vo.getColumnValues();
		if(batchEditFitems==null || batchEditFitems.isEmpty()){
			List<Message> messages = new ArrayList<>();
			messages.add(new Message("没有要更新的数据!",MessageLevel.ERROR));
			returnMessage.setMessages(messages);
			return returnMessage;
		}
		String funcCode = formParamVO.getFuncCode();
		String tableName = funcService.getOperaTableNameOfFunc(funcCode,tenantid);
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//查询表信息

		String pkName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
		ObjectUtil.validNotNull(pkName, "table:"+tableName+"的主键列没有找到");		

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);

		List<BaseVO> voList = baseService.queryByPKsOfLongAndTenantid(new BaseVO(tableName), primaryKeys, tenantid).getVoList();
		 
		BaseSettingUtils.setU_TPD(voList);
		if(voList.size()>0) {
			for(AbstractVO abstractVO : voList) {
				abstractVO.setTableName(tableName);
			}
		}
		List<FormItemVO> listBatchEditFormFitemsByFunc = formItemPCService.getListBatchEditFormFitemsByFunc(funcCode, tenantid);
		
		List<Message> validateResult = ValidateUtils.validate(voList, listBatchEditFormFitemsByFunc,tenantid);
		if(validateResult.size()>0) {//如果验证未通过，返回验证信息
			returnMessage.setMessages(validateResult);
			return returnMessage;
		}
		//只更新允许批量编辑字段;
		List<String> listCol = this.getUpdateItems(vo, voList);
		dynaSqlVO.setSqlColumnList(listCol);
		
		BatchUpdateParamVO batchUpdateParamVO = new BatchUpdateParamVO();
		batchUpdateParamVO.setDynaSqlVO(dynaSqlVO);
		batchUpdateParamVO.setPkName(pkName);
		batchUpdateParamVO.setVoList(voList);

		List<Message> errorList = null;

		FormVO formVO = formService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantid);
		IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
		if(action!=null) {
			errorList = action.beforeListUpdate(formParamVO, batchUpdateParamVO);
		}
		ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());

		if(ObjectUtil.isTrue(formVO.getIs_validate_consistency())) {
			boolean consistencyResult = baseService.validateConsistency(voList);
			if(!consistencyResult) {
				throw new HDException("数据状态已变更，请刷新");
			}
		}

		//关联功能的关联更新
		funcLinkService.linkUpdate(voList, formParamVO.getFuncCode());
		//关联数据的关联更新
		funcLinkDataService.linkUpdate(voList, formParamVO.getFuncCode(),tenantid);

		baseService.updateBatch(voList, dynaSqlVO);

		List<Long> pks = ReflectUtil.invokeGetBatch(voList, pkName.toLowerCase(),Long.class);
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		List<? extends AbstractVO> result = baseService.queryByPKsOfLongAndTenantid(voList.get(0), pks, currentDataTenantid).getVoList();

		//将修改后查询的结果赋值给batchUpdateParamVO，以供action操作；因为原先的list里的属性不全
		batchUpdateParamVO.setVoList(result);
		if(action!=null) {
			errorList = action.afterListUpdate(formParamVO, batchUpdateParamVO);
		}
		ExceptionHandlerUtils.handle(errorList, formParamVO.getRequest());

		returnMessage.setData(result);

		DictUtils.evictByTablename(tableName, currentDataTenantid);
		return returnMessage;
	}

	@Override
	public ReturnResult<List<? extends AbstractVO>> batchEdit(FormParamVO formParamVO, Collection<Long> primaryKeys,
			String code, Object value, Long tenantid) throws HDException {
		CommonVO com = new CommonVO();
		com.set(code, value);
		return this.batchEditByFItemConfig(formParamVO, primaryKeys, com, tenantid);
	}
	/**
	 * 获取批量编辑字段code
	 * 更新允许批量编辑字段;
	 * @param vo
	 * @param voList
	 * @return 
	 * @author haocs
	 * @date 2019年3月22日
	 */
	private List<String> getUpdateItems(AbstractVO vo,List<BaseVO> voList){
		Map<String, Object> fitemConfigMap = vo.getColumnValues();
		List<String> listCol = new ArrayList<>();
		if (!fitemConfigMap.isEmpty()) {
			Set<String> keySet = fitemConfigMap.keySet();
			for (String key : keySet) {

				for (BaseVO baseVO : voList) {

					Object value = fitemConfigMap.get(key);
					if (value != null) {
						listCol.add(key);
						baseVO.set(key, value);
						break;
					}
				}
			}
		}
		return listCol;
	}

	@Override
	public VOSet<? extends AbstractVO> listQuery(FormParamVO formParamVO,
			boolean ispage, boolean isCodetoName) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		return query(formParamVO, ispage, isCodetoName, tenantid);
	}

	@Override
	public ReturnResult<AbstractVO> addQueryStrategy(FormParamVO formParamVO) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		FormQueryVO formQueryVO = new FormQueryVO(); 
		String dataBody = formParamVO.getDataBody();
		String funcCode = formParamVO.getFuncCode();
		ReturnResult<AbstractVO>  result = new ReturnResult<>();
		result.setData(formQueryVO);
		if(org.apache.commons.lang3.StringUtils.isBlank(dataBody)) {
			return result;
		}
		
		JSONObject parse = (JSONObject) com.alibaba.fastjson.JSONObject.parse(dataBody);
		String name = (String) parse.get("name");
		
		JSONObject param = (JSONObject) parse.get("param");
		if(org.apache.commons.lang3.StringUtils.isBlank(name)) {
			throw new HDException("查询策略名称不能为空!");
		}
		if(param==null || org.apache.commons.lang3.StringUtils.isBlank(param.toJSONString())) {
			return result;
		}
		String jsonString = param.toJSONString();
		
		// 解析json
		// 拼接SQL 
		// 保存数据库
		if(StringUtils.isNotEmpty(dataBody)) {
//			DynaSqlVO sql = new DynaSqlVO();
//			sql.addWhereParam("func_code", funcCode);
//			sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
//			sql.setOrderByClause(" fquery_order desc ");
//			List<FormQueryVO> voList = baseService.query(new FormQueryVO(), sql).getVoList();
//			if(voList==null || voList.isEmpty()) {
			formQueryVO.setFquery_order(SyConstant.SY_TRUE);
//			}else {
//				FormQueryVO formQuery = voList.get(0);
//				formQueryVO.setFquery_order(formQuery.getFquery_order()+1);
//			}
			formQueryVO.setTenantid(tenantid);
			formQueryVO.setFquery_sql(jsonString);
			formQueryVO.setFunc_code(funcCode);
			formQueryVO.setFquery_ispublic(SyConstant.SY_FALSE);
			formQueryVO.setFquery_isdefault(SyConstant.SY_FALSE);
			formQueryVO.setFquery_name(name);
			formQueryVO = baseService.insert(formQueryVO);
//			@Cacheable(value="SY_FORM_QUERY",key="#funcCode.concat('|').concat(#tenantid)")
			result.setMessage("添加成功");
			result.setData(formQueryVO);
		}
		CacheUtils.getInstance().evict("SY_FORM_QUERY", funcCode+SyConstant.CACHE_SEPARATOR+tenantid);
		return result;
	}

	@Override
	public void delQueryStrategy(Collection<Long> primaryKeys,FormParamVO formParamVO) throws HDException {
		
		if(primaryKeys==null || primaryKeys.isEmpty()) {
			return; 
		}
		String conditionSql = DBSqlUtil.getConditionSql("fquery_id", primaryKeys, true);
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereClause(conditionSql);
		sql.addWhereParam(SyConstant.TENANT_STR, CurrentEnvUtils.getTenantId());
		List<FormQueryVO> voList = baseService.query(new FormQueryVO(), sql).getVoList();
		
		if(voList==null || voList.isEmpty()) {
			return ;
		}
		baseService.deleteBatch(voList);
		CacheUtils.getInstance().evict("SY_FORM_QUERY", formParamVO.getFuncCode()+SyConstant.CACHE_SEPARATOR+CurrentEnvUtils.getTenantId());
	}

}
