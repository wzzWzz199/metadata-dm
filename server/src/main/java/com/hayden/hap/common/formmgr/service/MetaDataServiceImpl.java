package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.authz.button.itf.IButtonAuthzPCService;
import com.hayden.hap.common.button.entity.ButtonPCVO;
import com.hayden.hap.common.button.itf.IButtonPCService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.itf.IDictDataService;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.enumerate.ViewTypeEnum;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.export.entity.ExportTemplateVO;
import com.hayden.hap.common.export.itf.IExportService;
import com.hayden.hap.common.form.entity.*;
import com.hayden.hap.common.form.itf.IFormDynamicItemService;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.form.itf.IFormPCService;
import com.hayden.hap.common.formmgr.entity.*;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.*;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.formmgr.utils.WfFlagUtils;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncPCService;
import com.hayden.hap.common.orgpermission.entity.OrgPermissionDTVO;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.CloneUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VariableUtils;
import com.hayden.hap.common.utils.form.FormItemUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import com.hayden.hap.wf.itf.IWorkflowButtonService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("metaDataService")
public class MetaDataServiceImpl implements IMetaDataService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MetaDataServiceImpl.class);

	/**
	 * 不需要列表数据
	 */
	private static final boolean NOT_NEED_LIST_DATA = false;
	/**
	 * 是子功能
	 */
	private static final boolean IS_CHILD = true;
	/**
	 * 不是子功能
	 */
	private static final boolean NOT_CHILD = false;
	
	@Autowired
	private IFuncPCService funcPCService;
	
	@Autowired
	private IFormPCService formPCService;
	
	@Autowired
	private IFuncLinkService funcLinkService;
	
	@Autowired
	private IFormItemPCService formItemPCService;
	
	@Autowired
	private IButtonPCService buttonPCService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	@Autowired
	private IFormQueryService formQueryService;
	
	@Autowired
	private IListFormService listFormService;
	
	@Autowired
	private IExportService exportService;
	
	@Autowired
	private ICardFormService cardFormService;
	
	@Autowired
	private IQuerySelectorService querySelectorService;
	
	@Autowired
	private IDictDataService dictDataService;
	
	@Autowired
	private IButtonAuthzPCService buttonAuthzPCService;
	
	@Autowired(required=false)
	private IWorkflowButtonService workflowButtonService;
	
	@Autowired
	private IDictService dictService;

	@Autowired
	private IFormDynamicItemService formDynamicItemService;

	/**
	 * 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IMetaDataService#getMetaData(com.hayden.hap.common.formmgr.entity.FormParamVO, boolean)
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@Override
	public MetaData getMetaData(FormParamVO formParamVO, boolean needListData) throws HDException {
		return getMetaData(formParamVO, null, needListData, NOT_CHILD);
	}

	/**
	 * 获取结构数据（自递归）
	 * @param formParamVO
	 * @param parentFunccode
	 * @param needListData
	 * @param isChild
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private MetaData getMetaData(FormParamVO formParamVO, String parentFunccode, boolean needListData, boolean isChild) throws HDException {		
		long start = System.currentTimeMillis();
		
		Long tenantid = CurrentEnvUtils.getTenantId();
		Long userId = CurrentEnvUtils.getUserId();
		String funcCode = formParamVO.getFuncCode();
		FuncVO funcVO = funcPCService.queryByFunccode(funcCode, tenantid);

		MetaData metaData = new MetaData();	
		metaData.setFuncVO(funcVO);
		if(!(FuncTypeEnum.FORM.getId().equals(funcVO.getFunc_type())||FuncTypeEnum.REPORT.getId().equals(funcVO.getFunc_type()))) {
			//不是表单类型功能或报表功能，直接返回空
			return metaData;
		}
		FormPCVO formVO = (FormPCVO)formPCService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);

		formVO.setList_bgcolor(VariableUtils.replaceSystemParam(formVO.getList_bgcolor()));
		funcVO.set("func_list_bgcolor", VariableUtils.replaceSystemParam(funcVO.getString("func_list_bgcolor")));
		IAction action = formPCService.getActionByFuncCode(funcCode, tenantid);
		if(action!=null) {
			action.beforeGetMetaData(formParamVO);
		}
						
		if(StringUtils.isNotEmpty(formVO.getOpera_table_code())) {
			String pkColName = tableDefService.getPkColName(formVO.getOpera_table_code());
			metaData.setPkColName(pkColName);
		}
		List<? extends FormItemVO> tempItemVOs = formItemPCService.getFormItemsByFormcode(formVO.getForm_code(), tenantid);
		List<? extends FormItemVO> itemVOs = CloneUtils.cloneObj(tempItemVOs);
		// 添加 是否为批量编辑字段集合 add by haocs 2019年3月22日
//		List<String> fitem_batch_editList = formItemPCService.getListBatchEditFitemsByItemVO(tempItemVOs);
//		metaData.setBatchEditFormItems(fitem_batch_editList);
		//将所有说明信息置空
		FormItemUtils.changeValue(itemVOs, FormItemUtils.ALL_ITEMS, "fitem_comment", null);
		FormItemUtils.replaceSystemParam(itemVOs);
		List<? extends FormItemVO> queryItemVOs = formItemPCService.getCommonQueryItems(funcCode, parentFunccode, tenantid);
		FormItemUtils.replaceSystemParam(queryItemVOs);		
		
		List<ButtonPCVO> buttonCacheVOs = buttonAuthzPCService.getButtonListByUser(funcCode, userId, tenantid);
		Boolean isSupportWf = WfFlagUtils.isSupportWf(formVO);	
		if(isSupportWf) {
			workflowButtonService.getWfButtonList(buttonCacheVOs, funcCode, userId, tenantid,ButtonPCVO.class);
		}	
		List<FormQueryVO> formQueryList = formQueryService.getFormQueryByFunc(funcCode, userId, tenantid);
		
		String funcTree = funcVO instanceof FuncPCVO?((FuncPCVO)funcVO).getFunc_tree():null;
		Map<String,List<DictDataWarperVO>> map = dictDataService.getDictMap(itemVOs, funcTree, tenantid);
		
		List<MetaData> childrenMetaDatas = new ArrayList<>();
		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink_RequiresNew(funcCode, tenantid);
		
		for(FuncLinkVO funcLinkVO : funcLinkVOs) {
			FormParamVO childFormParamVO = new FormParamVO();
			childFormParamVO.setFuncCode(funcLinkVO.getSub_func_code());
			MetaData child = getMetaData(childFormParamVO, funcCode, NOT_NEED_LIST_DATA, IS_CHILD);
			FuncVO childFuncVO = funcPCService.queryByFunccode(funcLinkVO.getSub_func_code(), tenantid);
			childrenMetaDatas.add(child);
		}
		
		if(needListData && FuncTypeEnum.FORM.getId().equals(funcVO.getFunc_type())) {
			long queryStart = System.currentTimeMillis();
			VOSet<? extends AbstractVO> voset = getListData(formParamVO,tenantid);
			metaData.setVoset(new ListDataVO(voset));
			long queryEnd = System.currentTimeMillis();
			if(logger.isDebugEnabled()) {
				logger.debug("功能："+funcCode+"获取列表数据时长："+(queryEnd-queryStart));
			}
		}
		
		if(needListData && ViewTypeEnum.CARD.getIntCode().equals(funcVO.getFunc_layout())) {
			if(metaData.getVoset()!=null 
					&& metaData.getVoset().getVoList()!=null 
					&& metaData.getVoset().getVoList().size()==1) {
				AbstractVO vo = metaData.getVoset().getVoList().get(0);
				Long editId = vo.getLong(metaData.getPkColName());
				formParamVO.getReqParamVO().setEditId(editId);
				
				OrgPermissionDTVO dto = new OrgPermissionDTVO();
				dto.setFuncCode(formParamVO.getFuncCode());
				dto.setOrgid(CurrentEnvUtils.getOrgId());
				dto.setUserid(CurrentEnvUtils.getUserId());
				dto.setTenantid(CurrentEnvUtils.getTenantId());
				formParamVO.getReqParamVO().setEditId(editId);
				
				//TODO... 查看or编辑？
				boolean readonly = false;
				
				CardDataVO cardEditVO = cardFormService.getEditVO(formParamVO, null, dto, readonly);
				metaData.setCardEditVO(cardEditVO);
				metaData.setVoset(null);
			}
		}
		
		Boolean isTreeMaintenance = listFormService.isTreeMaintenance(funcCode, tenantid);
		List<ExportTemplateVO> exportQueryList = exportService.getExportTempByCode(funcCode, tenantid);
		List<ExcelTemplateVO> importQueryList = exportService.getImportTempByCode(funcCode, tenantid);
		
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		AbstractVO queryDefaultVO = listFormService.getQueryDefaultVO(itemVOs, currentDataTenantid);
		
		metaData.setExportQueryList(exportQueryList);
		metaData.setImportQueryList(importQueryList);
		metaData.setFuncVO(funcVO);
		metaData.setFormVO(formVO);
		metaData.setFormItemVOs((List<FormItemPCVO>)(List)itemVOs);
		metaData.setQueryItemVOs((List<FormItemPCVO>)(List)queryItemVOs);
		metaData.setButtons(buttonCacheVOs);
		metaData.setDictMap(map);
		metaData.setChildren(childrenMetaDatas);
		metaData.setFuncLinkVOs(funcLinkVOs);
		metaData.setFormQueryVOs(formQueryList);
		metaData.setIsTreeMaintenance(isTreeMaintenance);
		metaData.setQueryDefaultVO(queryDefaultVO);
		metaData.setIsOperationTree(isTreeMaintenance);
		handleTreeToDataField(metaData);
		handleTreeDefaultSelectedNode(metaData);
		if(formVO.getIsDynamicConfig().equals(1)){
			formDynamicItemService.packageMetaData(metaData,formVO.getForm_code(),tenantid);
			//将字段动态配置详情信息json转换成map
			for (FormConditionDetailVO conditionDetailVO : metaData.getConditionDetail()) {
			    String fitem_info = conditionDetailVO.getFitem_info();
				List<FormItemPCVO> formItemVOS = JsonUtils.parseArray(fitem_info, FormItemPCVO.class);
				Map<String, List<DictDataWarperVO>> dictMap = dictDataService.getDictMap(formItemVOS, tenantid);
				metaData.getDictMap().putAll(dictMap);
				List<Map> fitems = JsonUtils.parseArray(fitem_info, Map.class);
				conditionDetailVO.set("fitems",fitems);
				conditionDetailVO.setFitem_info(null);
			}
			//将表单字段fitem_order 初始化成0 前端直接根据动态字段配置的顺序进行展示
			for (FormItemVO itemVO: metaData.getFormItemVOs()) {
				itemVO.setFitem_order(0);
			}
		}
		if(action!=null) {
			action.afterGetMetaData(formParamVO,metaData);
		}
		long end = System.currentTimeMillis();
		if(logger.isDebugEnabled()) {
			logger.debug("功能："+funcCode+"获取结构数据时长："+(end-start));
		}
		return metaData;
	}
	/**
	  * @author yinbinchen
	 * @param metaData
	 * @throws HDException 
	 * @description 处理全息查询树默认选中的节点
	 */
	private void handleTreeDefaultSelectedNode(MetaData metaData) throws HDException {
		Map<String,String> treeDefaultSelectNode = new HashMap<String,String>();
		FuncPCVO funcVO = (FuncPCVO) metaData.getFuncVO();
		//配置了全息查询树及树默认选中节点的值
		if(StringUtils.isNotEmpty(funcVO.getFunc_tree())&&StringUtils.isNotEmpty(funcVO.getFunc_tree_default())) {
			String[] fitemCodes = funcVO.getFunc_tree().split(",");
			String[] fitemCodesDefauleValues =funcVO.getFunc_tree_default().split(","); 
			List<? extends FormItemVO> formItemVOs = metaData.getFormItemVOs();
			out:for (int i = 0; i < fitemCodesDefauleValues.length; i++) {
				try {
					  for(FormItemVO formItemVO : formItemVOs) {
						if(fitemCodes[i].equals(formItemVO.getFitem_code())) {//找到对应的表单字段
							String inputConfig = formItemVO.getFitem_input_config();
							if(StringUtils.isNotEmpty(inputConfig)) {//如果该表单字段配置了输入设定
								DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
								String dictCode = inputConfigVO.getDictcode();
								DictVO dictVO = dictService.getDictByCode_Cache(dictCode, CurrentEnvUtils.getTenantId());

								FormVO formVO = metaData.getFormVO();
								if(dictVO==null || formVO==null) {//如果没找到字典或表单，判定不是树维护
									continue out;
								}
								String fitemCodesDefauleValue = fitemCodesDefauleValues[i];
								fitemCodesDefauleValue = VariableUtils.replaceSystemParam4Obj(fitemCodesDefauleValue, formItemVO.getFitem_data_type()).toString();
								List<DictDataVO> dictDatas = dictDataService.getDictData(dictCode, CurrentEnvUtils.getTenantId());
								for (DictDataVO dictDataVO : dictDatas) {
									if (StringUtils.isNotEmpty(fitemCodesDefauleValue)) {
										if (dictDataVO.getDict_data_code().equals(fitemCodesDefauleValue)) {
											treeDefaultSelectNode.put(fitemCodes[i], dictDataVO.getDictdataid().toString());
											continue out;
										}
										else if (dictDataVO.getDictdataid().toString().equals(fitemCodesDefauleValue)) {
											treeDefaultSelectNode.put(fitemCodes[i], dictDataVO.getDictdataid().toString());
											continue out;
										}
										else if (dictDataVO.getDict_data_name().equals(fitemCodesDefauleValue)) {
											treeDefaultSelectNode.put(fitemCodes[i], dictDataVO.getDictdataid().toString());
											continue out;
										}
									}
								}
							}
							continue out;
						}
					  }	
				} catch (Exception e) {
					throw new HDException("全息查询字段默认值配置与全息查询字段未对应", e);
				}
			}
			metaData.setDefaultfilter(treeDefaultSelectNode);
		}
		
	}

	/**
	 * @author yinbinchen
	 * @param metaData
	 * @throws HDException 
	 * @description 处理全息查询树主键字段与父级字段
	 */
	private void handleTreeToDataField(MetaData metaData) throws HDException {
		Map<String,TreeToDataField> treeToDataField = new HashMap<String,TreeToDataField>();
		FuncPCVO funcVO = (FuncPCVO) metaData.getFuncVO();
		if("SY_DICT_DATA".equals(funcVO.getFunc_code().toUpperCase()) 
				|| "MGR_DICT_DATA".equals(funcVO.getFunc_code().toUpperCase())) {//字典数据功能都是树维护
			treeToDataField.put("dict_data_code", new TreeToDataField("dictdataid","dict_data_parent"));
			metaData.setIsOperationTree(true);
			metaData.setTreeToDataFiled(treeToDataField);
			return;
		}
		if (metaData.getIsOperationTree()) {
			if(StringUtils.isNotEmpty(funcVO.getFunc_tree())) {//如果配置了全息查询字段
				String[] fitemCodes = funcVO.getFunc_tree().split(",");
				List<? extends FormItemVO> formItemVOs = metaData.getFormItemVOs();
				out:for(String fitemCode : fitemCodes) {
					for(FormItemVO formItemVO : formItemVOs) {
						if(fitemCode.equals(formItemVO.getFitem_code())) {//找到对应的表单字段
							String inputConfig = formItemVO.getFitem_input_config();
							if(StringUtils.isNotEmpty(inputConfig)) {//如果该表单字段配置了输入设定
								DictInputConfigVO inputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfig);
								String dictCode = inputConfigVO.getDictcode();
								DictVO dictVO = dictService.getDictByCode_Cache(dictCode, CurrentEnvUtils.getTenantId());

								FormVO formVO = metaData.getFormVO();
								if(dictVO==null || formVO==null) {//如果没找到字典或表单，判定不是树维护
									continue out;
								}

								//如果两个表名相同，则是树的维护啦
								if(dictVO.getDict_t_table().toLowerCase().equals(formVO.getOpera_table_code().toLowerCase())) {
									treeToDataField.put(fitemCode, new TreeToDataField("dictdataid",dictVO.getDict_f_parent()));
								}
							}
							continue out;
						}
					}
				}
			}
			metaData.setTreeToDataFiled(treeToDataField);
		}
	}

	/**
	 * 获取查询选择结构数据
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IMetaDataService#(com.hayden.hap.common.formmgr.entity.FormParamVO, java.lang.Long, java.lang.Long, java.lang.String)
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@Override
	public MetaData getQuerySelectorMetaData(FormParamVO formParamVO,Long tenantid, Long userId) throws HDException {	
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		String funcCode = formParamVO.getFuncCode();
		FormVO querySelectFormVO = formPCService.getFormVOByFunccode(funcCode, tenantid);
		String tableName = querySelectFormVO.getOpera_table_code();
		if(SyConstant.NONTENANTID_TABLE.get(tableName)!=null) {
			currentDataTenantid = tenantid;
		}
		
		return getQuerySelectorMetaData(funcCode, currentDataTenantid, userId);
	}

	/**
	 * 获取查询选择结构数据
	 * @param funcCode
	 * @param currentDataTenantid
	 * @param userid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MetaData getQuerySelectorMetaData(String funcCode, Long currentDataTenantid, Long userid) 
			throws HDException {
		if(SyConstant.NONTENANTID_FUNC.contains(funcCode)) {
			currentDataTenantid = SyConstant.TENANT_HD;
		}
		FuncVO funcVO = funcPCService.queryByFunccode(funcCode, currentDataTenantid);
		ObjectUtil.validNotNull(funcVO, "租户"+currentDataTenantid+"没有功能："+funcCode);
		FormVO formVO = formPCService.getFormVOByFormcode(funcVO.getFunc_info(), currentDataTenantid);
		ObjectUtil.validNotNull(formVO, "租户"+currentDataTenantid+"没有表单："+funcVO.getFunc_info());
		String pkColName = tableDefService.getPkColName(formVO.getOpera_table_code());
		List<? extends FormItemVO> fitemVOs = formItemPCService.getFormItemsByFormcode(formVO.getForm_code(), currentDataTenantid);
		List<? extends FormItemVO> queryItemVOs = formItemPCService.getQuickQueryItems(funcCode, currentDataTenantid);
		List<FormQueryVO> formQueryList = formQueryService.getFormQueryByFunc(funcCode, userid, currentDataTenantid);
		
		String funcTree = funcVO instanceof FuncPCVO?((FuncPCVO)funcVO).getFunc_tree():null;
		Map<String,List<DictDataWarperVO>> map = dictDataService.getDictMap(fitemVOs, funcTree, currentDataTenantid);		
		
		MetaData metaData = new MetaData();
		metaData.setFuncVO(funcVO);
		metaData.setFormVO((FormPCVO)formVO);
		metaData.setFormItemVOs((List<FormItemPCVO>)(List)fitemVOs);
		metaData.setQueryItemVOs((List<FormItemPCVO>)(List)queryItemVOs);
		metaData.setDictMap(map);
		metaData.setPkColName(pkColName);
		metaData.setSourceFuncVO(funcVO);
		metaData.setFormQueryVOs(formQueryList);
		handleTreeDefaultSelectedNode(metaData);

		if(formVO.getIsDynamicConfig().equals(1)){
			formDynamicItemService.packageMetaData(metaData,formVO.getForm_code(),currentDataTenantid);
			//将字段动态配置详情信息json转换成map
			for (FormConditionDetailVO conditionDetailVO : metaData.getConditionDetail()) {
				String fitem_info = conditionDetailVO.getFitem_info();
				List<FormItemPCVO> formItemVOS = JsonUtils.parseArray(fitem_info, FormItemPCVO.class);
				Map<String, List<DictDataWarperVO>> dictMap = dictDataService.getDictMap(formItemVOS, currentDataTenantid);
				metaData.getDictMap().putAll(dictMap);
				List<Map> fitems = JsonUtils.parseArray(fitem_info, Map.class);
				conditionDetailVO.set("fitems",fitems);
				conditionDetailVO.setFitem_info(null);
			}
			//将表单字段fitem_order 初始化成0 前端直接根据动态字段配置的顺序进行展示
			for (FormItemVO itemVO: metaData.getFormItemVOs()) {
				itemVO.setFitem_order(0);
			}
		}

		return metaData;
	}
	
	/**
	 * 获取初始列表数据
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IMetaDataService#getListData(com.hayden.hap.common.formmgr.entity.FormParamVO, java.lang.Long)
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@Override
	public VOSet<? extends AbstractVO> getListData(FormParamVO formParamVO,Long tenantid) throws HDException {
				
		ReqParamVO param = formParamVO.getReqParamVO();
		param.setPage(1);
		
		//每页大小
		FormVO formVO = formPCService.getFormVOByFunccode(formParamVO.getFuncCode(), tenantid);
		param.setRows(formVO.getPage_num());
		
		//查询默认值
		List<FormItemPCVO> itemVOs = formItemPCService.getCommonQueryItems(formParamVO.getFuncCode(), param.getParentFuncCode(), tenantid);		
		Map<String,String> queryParamMap = new HashMap<>();
		for(FormItemPCVO itemVO : itemVOs) {
			if(StringUtils.isNotEmpty(itemVO.getFitem_query_default())) {
				String queryDefault = itemVO.getFitem_query_default();
				if(VariableUtils.hasSysParam(queryDefault)) {
					queryDefault = VariableUtils.replaceSystemParam(queryDefault);
				}
				queryParamMap.put(itemVO.getFitem_code(), queryDefault);
			}
		}
		param.setQueryParam(JsonUtils.writeValueAsString(queryParamMap));		
		param.setTenantid(SyConstant.TENANT_HD);
		
		formParamVO.setReqParamVO(param);
		VOSet<? extends AbstractVO> voSet = listFormService.listQuery(formParamVO);
		return voSet;
	}

	/**
	 * 获取字典数据映射，键为字典编码、值为字典数据集合
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IMetaDataService#getDictMap(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@Override
	public Map<String, List<DictDataWarperVO>> getDictMap(String funcCode,Long tenantid)
			throws HDException {
		FuncVO funcVO = funcPCService.queryByFunccode(funcCode, tenantid);
		FormVO formVO = formPCService.getFormVOByFormcode(funcVO.getFunc_info(), tenantid);
		List<? extends FormItemVO> itemVOs = formItemPCService.getFormItemsByFormcode(formVO.getForm_code(), tenantid);	
		
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		String funcTree = funcVO instanceof FuncPCVO?((FuncPCVO)funcVO).getFunc_tree():null;
		return dictDataService.getDictMap(itemVOs, funcTree, currentDataTenantid);
	}

	/**
	 * 获取功能功能选择结构数据
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IMetaDataService#getFuncSelectorMetaData(com.hayden.hap.common.formmgr.entity.FormParamVO, java.lang.Long, java.lang.Long)
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	@Override
	public MetaData getFuncSelectorMetaData(FormParamVO formParamVO, Long tenantid, Long userid) throws HDException {
		String funcCode = formParamVO.getFuncCode();		
		return getQuerySelectorMetaData(funcCode, tenantid, userid);
	}

	/**
	 * 获取跳转页面结构数据
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IMetaDataService#(com.hayden.hap.common.formmgr.entity.FormParamVO, boolean)
	 * @author zhangfeng
	 * @date 2017年4月25日
	 */
	@Override
	public MetaData getMetaDataForPopPage(FormParamVO formParamVO) throws HDException {
		MetaData metaData = getMetaData(formParamVO, null, NOT_NEED_LIST_DATA, NOT_CHILD);
		
		OrgPermissionDTVO dto = new OrgPermissionDTVO();
		dto.setFuncCode(formParamVO.getFuncCode());
		dto.setOrgid(CurrentEnvUtils.getOrgId());
		dto.setUserid(CurrentEnvUtils.getUserId());
		dto.setTenantid(CurrentEnvUtils.getTenantId());
		
		//TODO... 查看or编辑？
		boolean readonly = false;
		FuncVO funcVO = funcPCService.queryByFunccode(formParamVO.getFuncCode(), CurrentEnvUtils.getTenantId());
		if(funcVO!=null) {
			readonly = ObjectUtil.isTrue(funcVO.getFunc_readonly());
		}
		
		CardDataVO cardEditVO = cardFormService.getEditVO(formParamVO, null, dto, readonly);
		metaData.setCardEditVO(cardEditVO);
		metaData.setViewType(ViewTypeEnum.CARD.getIntCode());
		return metaData;
	}
	
	/**
	 * 根据列定义，构造简易结构数据
	 * @param columns
	 * @return 
	 * @author zhangfeng
	 * @date 2017年6月13日
	 */
	public MetaData constructMetaData(String[][] columns) {
		return null;
	}
}
