package com.hayden.hap.common.form.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.itf.IDictDataService;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.enumerate.DictType;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.express.entity.ReversePolishNotationVO;
import com.hayden.hap.common.express.itf.IExpressService;
import com.hayden.hap.common.form.entity.*;
import com.hayden.hap.common.form.itf.IFormDynamicItemService;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.constant.QuerySelectConstant;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.MetaData;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IQuerySelectorService;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.service.MetaDataStructUtils;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.CloneUtils;
import com.hayden.hap.common.utils.ListSortUtil;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月24日
 */
@Service("formItemPCService")
public class FormItemPCServiceImpl implements IFormItemPCService {

	@Autowired
	private IFormService formService;

	@Autowired
	private IBaseService baseService;

	@Autowired
	private IFuncService funcService;

	@Autowired
	private IFuncLinkService funcLinkService;
	
	@Autowired
	private IQuerySelectorService querySelectorService;

	@Autowired
	@Qualifier("simpleExpressService")
	private IExpressService expressService;

	@Autowired
	private IFormDynamicItemService formDynamicItemService;

	@Autowired
	private IDictDataService dictDataService;

	@Autowired
	private IDictService dictService;
	private static final Logger logger = LoggerFactory.getLogger(FormItemPCServiceImpl.class);
	/** 
	 *
	 * @see com.hayden.hap.common.form.itf.IFormItemService#getAutoSerialNumberItem(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2015年12月18日
	 */
	@Override
	public List<FormItemPCVO> getAutoSerialNumberItem(String funcCode,
			Long tenantid) {
		
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> list = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		List<FormItemPCVO> result = new ArrayList<>();

		for(FormItemPCVO itemVO:list) {
			if(InputTypeEnum.SERIAL_NUMBER_AUTO.getCode().equals(itemVO.getFitem_input_type())) {
				result.add(itemVO);
			}
		}

		return result;
	}

	@Override
	@Cacheable(value="SY_FORM_ITEM",key="#formCode.concat('|').concat(#tenantid)")
	public List<FormItemPCVO> getFormItemsByFormcode(String formCode, Long tenantid) {
		FormVO formVO = formService.getFormVOByFormcode(formCode, tenantid);
		ObjectUtil.validNotNull(formVO, "根据表单编码："+formCode+",查找表单vo为空。|"+tenantid);

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("FORMID", formVO.getFormid());
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("fitem_isenable", SyConstant.SY_TRUE);
		dynaSqlVO.setOrderByClause(" FITEM_ORDER asc ");

		VOSet<FormItemPCVO> voSet = baseService.query(FormItemPCVO.class, dynaSqlVO);
		return voSet.getVoList();
	}

	@Override
	public List<FormItemPCVO> getFormItemsByFormcode(String formCode, AbstractVO vo) {
		//通用校验，各种条件不符合的话，直接返回null或者formitems;
		if (vo == null) {
			return null;
		}

		Long tenantid=vo.getLong("tenantid");

		FormVO formVO = formService.getFormVOByFormcode(formCode, tenantid);
		ObjectUtil.validNotNull(formVO, "根据表单编码：" + formCode + ",查找表单vo为空。|" + tenantid);

		if (formVO.getIsDynamicConfig().equals(0)) {
			return this.getFormItemsByFormcode(formCode, tenantid);
		}

		//定义中间变量
		HashMap<String,FormItemPCVO> itemMap=new HashMap<>();
		LinkedHashSet<FormItemPCVO> hideItems=new LinkedHashSet<>();
		LinkedHashMap<String,LinkedHashMap<String,LinkedHashSet<FormItemPCVO>>> orderMap=new LinkedHashMap<>();

		HashMap<String, Object> params = new HashMap<>();
		List<String> conditioncodes = new ArrayList<>();

		List<FormItemPCVO> result=new ArrayList<>();

		//得到条件数据
		List<FormConditionVO> formConditionVOS=formDynamicItemService.getFormDynamicConfig(formCode,tenantid);

		if (formConditionVOS != null) {
			//组装中间变量
			this.packageTempData(formCode,tenantid,formConditionVOS,vo,itemMap,orderMap,hideItems,params,conditioncodes);
			//结果中加入隐藏字段
			LinkedHashMap<String,LinkedHashSet<FormItemPCVO>> hideItemMap=new LinkedHashMap<>();
			hideItemMap.put("formhideItems",hideItems);
			orderMap.put("formhideItems",hideItemMap);
			//组装符合条件的明细数据
			List<FormConditionDetailVO> trueFormConditionDetailVOS=getMatchFormConditionDetails(formCode,conditioncodes,params,tenantid);
			//对数据进行排序,全局按照父级在前子集在后的规则有序
			formDynamicItemService.orderFormConditionDetailVOS(trueFormConditionDetailVOS);
			//字段合并
			for (FormConditionDetailVO formConditionDetailVO : trueFormConditionDetailVOS) {
				try {
					List<HashMap> fitems= JsonUtils.parseArray(formConditionDetailVO.getFitem_info(),HashMap.class);
					for (HashMap dynaItem : fitems) {
						String fitemCode = dynaItem.get("fitem_code").toString();
						if (itemMap.containsKey(fitemCode)) {
							FormItemPCVO formItemPCVO = itemMap.get(fitemCode);

							for(Object key:dynaItem.keySet()){
								formItemPCVO.set(key.toString(),dynaItem.get(key));
							}

							orderMap.get(formConditionDetailVO.getGroup_code())
									.get(formConditionDetailVO.getCondition_code())
									.add(formItemPCVO);
						}
					}

				} catch (HDException e) {
					e.printStackTrace();
				}
			}
			//结果字段组装
			this.getResult(orderMap,result);
		}
		return result;
	}

	@Override
	public List<FormItemPCVO> getFormItemsByFunccode(String funcCode,
			Long tenantid) {
		FuncVO funcVO = funcService.queryByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码："+funcCode+",查找功能为空|"+tenantid);

		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		return formItemService.getFormItemsByFormcode(funcVO.getFunc_info(), tenantid);
	}

	@Override
	public List<FormItemPCVO> getFormItemsByFunccode(String funcCode, AbstractVO vo) {
		if (vo == null) {
			return null;
		}

		FuncVO funcVO = funcService.queryByFunccode(funcCode, vo.getLong("tenantid"));
		ObjectUtil.validNotNull(funcVO, "根据功能编码："+funcCode+",查找功能为空|"+vo.getLong("tenantid"));
		return this.getFormItemsByFormcode(funcVO.getFunc_info(), vo);
	}


	@Override
	public List<FormItemPCVO> getCommonQueryItems(String funccode, String parentFunccode, Long tenantid) {
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);
		List<FormItemPCVO> result = new ArrayList<>();
		if(StringUtils.isEmpty(parentFunccode)) {
			for(FormItemPCVO itemVO : all) {
				if(ObjectUtil.isTrue(itemVO.getFitem_com_query())) {
					result.add(itemVO);
				}
			}
		}else {
			List<FuncLinkVO> list = funcLinkService.getFuncLink(parentFunccode, funccode, tenantid);
			FuncLinkVO funcLinkVO = list.get(0);
			if(funcLinkVO!=null && ObjectUtil.isTrue(funcLinkVO.getLink_is_showquery())) {
				for(FormItemPCVO itemVO : all) {
					if(ObjectUtil.isTrue(itemVO.getFitem_com_query())) {
						result.add(itemVO);
					}
				}
			}
		}
		return CloneUtils.cloneObj(result);
	}

	@Override
	public List<FormItemPCVO> getQuickQueryItems(String funccode, Long tenantid) {
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);
		List<FormItemPCVO> result = new ArrayList<>();
		for(FormItemPCVO itemVO : all) {
			if(ObjectUtil.isTrue(itemVO.getFitem_quick_query())) {
				result.add(itemVO);
			}
		}
		return CloneUtils.cloneObj(result);
	}

	@Override
	public List<FormItemPCVO> getGridItems(String funccode, Long tenantid) {
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> all = formItemService.getFormItemsByFunccode(funccode, tenantid);		
		
		List<FormItemPCVO> result = new ArrayList<>();
		for(FormItemPCVO itemVO : all) {
			if(ObjectUtil.isTrue(itemVO.getFitem_show_list())) {
				result.add(itemVO);
			}
		}
		ListSortUtil.sort(result, "fitem_column_order", "asc");
		return CloneUtils.cloneObj(result);
	}

	/**
	 * 根据功能编码获取列表编辑列字段
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月19日
	 */
	@Override
	public List<FormItemPCVO> getListEditFitems(String funcCode, Long tenantid) {
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> fieldVOs = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		
		List<FormItemPCVO> result = new ArrayList<>();
		for(FormItemPCVO fieldVO:fieldVOs) {
			if(ObjectUtil.isTrue(fieldVO.getFitem_batch())) {
				if(ElementTypeEnum.FILE.getCode().equals(fieldVO.getFitem_input_element())
						|| ElementTypeEnum.IMG.getCode().equals(fieldVO.getFitem_input_element())) {
					continue;//文件上传的字段不用更新
				}
				//TODO 查询选择、字典改变的其它字段	
				result.add(fieldVO);
			}
		}
		return result;
	}

	/**
	 * 表单字段查询选择
	 * @param formParamVO
	 * @param bizFuncCode//需要查询表单数据的业务功能编码
	 * @param //item2FuncMap
	 * @param userid
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2018年3月23日
	 */
	@Override
	public ReturnResult<?> queryItemSelector(FormParamVO formParamVO, String bizFuncCode,
			Long userid, Long tenantid) throws HDException {
		
		String operaType = formParamVO.getRequest().getParameter("isGetMetaData");
		if(QuerySelectConstant.LIST_QUERY.equals(operaType)) {//查询选择列表查询
			FuncVO funcVo = funcService.queryByFunccode(bizFuncCode, tenantid);
			String form_code = funcVo.getFunc_info();
			List<FormItemPCVO> itemList = getFormItemsByFormcode(form_code, tenantid);
			//获取名称输入值
			if(StringUtils.isNotEmpty(formParamVO.getReqParamVO().getQueryParam())){
				Map queryParamMap = JsonUtils.parse(formParamVO.getReqParamVO().getQueryParam(), Map.class);
				if(ObjectUtil.isNotEmpty(queryParamMap)){
					String fitemValue = (String) queryParamMap.get("fitem_name");
					if(StringUtils.isNotEmpty(fitemValue)){
						List<FormItemPCVO> filterItemList = new ArrayList<FormItemPCVO>();
						for(FormItemPCVO item:itemList){
							if(item.getFitem_name().contains(fitemValue)){
								filterItemList.add(item);
							}
						}
						itemList = filterItemList;
					}
				}
			}
			
			VOSet<AbstractVO> voSet = new VOSet<>();
			List<AbstractVO> voList = new ArrayList<>();
			for(FormItemPCVO vo: itemList){
				AbstractVO abstractVO = new BaseVO();
				abstractVO.set("fitem_code", vo.getFitem_code());
				abstractVO.set("fitem_name", vo.getFitem_name());
				voList.add(abstractVO);
			}
			voSet.setVoList(voList);
			return new ReturnResult<>(voSet);
		}else if(QuerySelectConstant.GET_METADATA.equals(operaType)) {//查询选择结构数据
		    String[][] columns = {{"fitem_code","编码"},{"fitem_name","名称"}};
			MetaData metaData = MetaDataStructUtils.constructMetaData(columns, "表单字段");
			metaData.getFuncVO().set("func_opensearch", 1);
			metaData.getFormItemVOs().get(1).setFitem_quick_query(1);
			List<FormItemPCVO> queryItemVOs = new ArrayList<>();
			FormItemPCVO queryItem = metaData.getFormItemVOs().get(1);
			queryItem.setFitem_input_element_query(ElementTypeEnum.INPUT.getCode());
			queryItem.setFitem_input_type_query(InputTypeEnum.MANUAL.getCode());
			queryItemVOs.add(queryItem);
			metaData.setQueryItemVOs(queryItemVOs);
			return new ReturnResult<>(metaData);
		}
		
		throw new HDException("请求参数错误，没有标记请求数据类型.");
	}

	@Override
	public List<String> getListBatchEditFitemsByFunc(String funcCode, Long tenantid) {
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> list = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		return this.getListBatchEditFitemsByItemVO(list);
	}

	public List<String> getListBatchEditFitemsByItemVO(List<? extends FormItemVO> itemVOs) {
		List<String> fitemBatchEditList = new ArrayList<>();
		for (FormItemVO formItemVO : itemVOs) {
			Integer fitem_batch_edit = formItemVO.getFitem_batch_edit();
			if(fitem_batch_edit!=null && SyConstant.SY_TRUE == fitem_batch_edit) {
				fitemBatchEditList.add(formItemVO.getFitem_code());
			}
		}
		return fitemBatchEditList;
	}

	@Override
	public List<FormItemVO> getListBatchEditFormFitemsByFunc(String funcCode, Long tenantid) {
		IFormItemPCService formItemService = AppServiceHelper.findBean(IFormItemPCService.class);
		List<FormItemPCVO> list = formItemService.getFormItemsByFunccode(funcCode, tenantid);
		List<FormItemVO> fitemBatchEditList = new ArrayList<>();
		for (FormItemVO formItemVO : list) {
			Integer fitem_batch_edit = formItemVO.getFitem_batch_edit();
			if(fitem_batch_edit!=null && SyConstant.SY_TRUE == fitem_batch_edit) {
				fitemBatchEditList.add(formItemVO);
			}
		}
		return fitemBatchEditList;
	}

	@Override
	public List<FormItemPCVO> getDynaExportFormItemsByFormCode(String formCode, AbstractVO vo) {
		Long starttime=System.currentTimeMillis();
		//通用校验，各种条件不符合的话，直接返回null或者formitems;
		if (vo == null) {
			return null;
		}

		Long tenantid=vo.getLong("tenantid");

		FormVO formVO = formService.getFormVOByFormcode(formCode, tenantid);
		ObjectUtil.validNotNull(formVO, "根据表单编码：" + formCode + ",查找表单vo为空。|" + tenantid);

		if (formVO.getIsDynamicConfig().equals(0)) {
			return this.getDisplayFormItemsByFormcode(formCode, tenantid);
		}

		//定义中间变量
		HashMap<String,FormItemPCVO> itemMap=new HashMap<>();
		LinkedHashMap<String,LinkedHashMap<String,LinkedHashSet<FormItemPCVO>>> orderMap=new LinkedHashMap<>();

		HashMap<String, Object> params = new HashMap<>();
		List<String> conditioncodes = new ArrayList<>();

		List<FormItemPCVO> result=new ArrayList<>();

		//得到条件数据
		List<FormConditionVO> formConditionVOS=formDynamicItemService.getFormDynamicConfig(formCode,tenantid);

		if (formConditionVOS != null) {
			//组装中间变量
			this.packageTempData(formCode,tenantid,formConditionVOS,vo,itemMap,orderMap,null,params,conditioncodes);
			//取得导出相关字段，包含本层级及下级
			List<FormConditionDetailVO> trueFormConditionDetailVOS=getDynaExportConditionDetails(formCode,conditioncodes,params,tenantid);
			//字段合并
			for (FormConditionDetailVO formConditionDetailVO : trueFormConditionDetailVOS) {
				try {
					List<HashMap> fitems= JsonUtils.parseArray(formConditionDetailVO.getFitem_info(),HashMap.class);
					for (HashMap dynaItem : fitems) {
						String fitemCode = dynaItem.get("fitem_code").toString();
						if (itemMap.containsKey(fitemCode)) {
							FormItemPCVO formItemPCVO = itemMap.get(fitemCode);
							for(Object key:dynaItem.keySet()){
								//这里只覆盖名称属性
								if("fitem_name".equals(key)){
									formItemPCVO.set(key.toString(),dynaItem.get(key));
									break;
								}
							}
							orderMap.get(formConditionDetailVO.getGroup_code())
									.get(formConditionDetailVO.getCondition_code())
									.add(itemMap.get(fitemCode));
						}
					}

				} catch (HDException e) {
					e.printStackTrace();
				}
			}
			//结果字段组装
			this.getResult(orderMap,result);
		}
		return result;
	}

	@Override
	public List<FormItemPCVO> getDynaExportFormItemsByFuncCode(String funcCode, AbstractVO vo) {
		if (vo == null) {
			return null;
		}

		FuncVO funcVO = funcService.queryByFunccode(funcCode, vo.getLong("tenantid"));
		ObjectUtil.validNotNull(funcVO, "根据功能编码："+funcCode+",查找功能为空|"+vo.getLong("tenantid"));
		return this.getDynaExportFormItemsByFormCode(funcVO.getFunc_info(),vo);
	}

	@Override
	@Cacheable(value = "SY_FORM_ITEM", key = "#formCode.concat('|').concat(#tenantid).concat('|displayitem')")
	public List<FormItemPCVO> getDisplayFormItemsByFormcode(String formCode, Long tenantid) {
		List<FormItemPCVO> result = new ArrayList<>();
		List<FormItemPCVO> formItemPCVOS = this.getFormItemsByFormcode(formCode, tenantid);
		formItemPCVOS.forEach(x -> {
			if (!x.getFitem_input_element().equals("0") &&
					!x.getFitem_input_element().equals("11") &&
					!x.getFitem_input_element().equals("9") &&
					!x.getFitem_input_element().equals("8") &&
					!x.getFitem_input_element().equals("7") &&
					!x.getFitem_input_element().equals("17") &&
					!x.getFitem_input_element().equals("16") &&
					!x.getFitem_input_element().equals("13") &&
					!x.getFitem_input_element().equals("10")) {
				result.add(x);
			}
		});
		return result;
	}

	private void getResult(LinkedHashMap<String,LinkedHashMap<String,LinkedHashSet<FormItemPCVO>>> orderMap,List<FormItemPCVO> result){
		int index=0;
		for(String group:orderMap.keySet()){
			for(String condition:orderMap.get(group).keySet()){
				for(FormItemPCVO formItemPCVO:orderMap.get(group).get(condition)){
					formItemPCVO.setFitem_column_order(index++);
					result.add(formItemPCVO);
				}
			}
		}
	}

	/**
	 * 取得符合条件的数据
	 * @param formCode
	 * @param conditioncodes
	 * @param params
	 * @param tenantid
	 * @return
	 */
	private List<FormConditionDetailVO> getMatchFormConditionDetails(String formCode, List<String> conditioncodes, Map params, Long tenantid){
		//组装符合条件的明细数据
		List<FormConditionDetailVO> formConditionDetailVOS = formDynamicItemService.getFormDynamicConfigDetailInfo(formCode, conditioncodes, tenantid);
		List<FormConditionDetailVO> trueFormConditionDetailVOS = new ArrayList<>();
		for (FormConditionDetailVO formConditionDetailVO : formConditionDetailVOS) {
			if ((Boolean) expressService.eval(formConditionDetailVO.getCondition_info(), params)) {
				trueFormConditionDetailVOS.add(formConditionDetailVO);
			}
		}

		return trueFormConditionDetailVOS;
	}

	private void getMatchFormConditionDetails(HashSet<FormConditionDetailVO> trueFormConditionDetailVOS, List<FormConditionDetailVO> formConditionDetailVOS, List<ReversePolishNotationVO> reversePolishNotationVOS, Map params) {
		for (int i = 0; i < reversePolishNotationVOS.size(); i++) {
			if ((Boolean) expressService.eval(reversePolishNotationVOS.get(i), params)) {
				if (!trueFormConditionDetailVOS.contains(formConditionDetailVOS.get(i))) {
					trueFormConditionDetailVOS.add(formConditionDetailVOS.get(i));
				}
			}
		}
	}

	/**
	 * 得到导出动态数据，获得条件父子级数据
	 * @param formCode
	 * @param conditioncodes
	 * @param params
	 * @param tenantid
	 * @return
	 */
	private List<FormConditionDetailVO> getDynaExportConditionDetails(String formCode, List<String> conditioncodes, Map params, Long tenantid) {
		List<FormConditionDetailVO> formConditionDetailVOS = formDynamicItemService.getFormDynamicConfigDetailInfo(formCode, conditioncodes, tenantid);

		HashSet<FormConditionDetailVO> trueFormConditionDetailSet = new HashSet<>();
		List<ReversePolishNotationVO> reversePolishNotationVOS=new ArrayList<>();

		for (FormConditionDetailVO formConditionDetailVO : formConditionDetailVOS) {
			reversePolishNotationVOS.add(expressService.parseExpress(formConditionDetailVO.getCondition_info()));
		}

		List<FormItemPCVO> dictVOs=getDictFormItemByFormcode(formCode,tenantid);

		for(FormItemPCVO formItemPCVO:dictVOs){
			String dictItemCode=formItemPCVO.getFitem_code();
			if(params.containsKey(dictItemCode)){
				try {
					Map atomParams=new HashMap();
					String dictDataCode=params.get(dictItemCode).toString();
					atomParams.put(dictItemCode,params.get(dictItemCode));

					DictInputConfigVO dictInputConfigVO=InputConfigUtils.getDictInputConfigVO(formItemPCVO.getFitem_input_config());
					String dictCode=dictInputConfigVO.getDictcode();

					DictVO dictVO=dictService.getDictByCode_Cache(dictCode,tenantid);
					if(DictType.TREE.getCode().equals(dictVO.getDict_type())){
						List<DictDataVO> dictDataVOS=dictDataService.getDictData(dictCode,tenantid);
						DictDataVO vtop=new DictDataVO();
						vtop.setChildren(dictDataVOS);

						vtop=this.getTopData(vtop,dictDataCode);

						this.getDictDataMatchFormConditionDetails(vtop,trueFormConditionDetailSet,formConditionDetailVOS,reversePolishNotationVOS,params,dictItemCode);
					}else {
						this.getMatchFormConditionDetails(trueFormConditionDetailSet,formConditionDetailVOS,reversePolishNotationVOS,atomParams);
					}
				} catch (HDException e) {
					e.printStackTrace();
				}
			}
		}
		//组装符合条件的明细数据
		List<FormConditionDetailVO> trueFormConditionDetailVOS = new ArrayList<>(trueFormConditionDetailSet);
		formDynamicItemService.orderFormConditionDetailVOS(trueFormConditionDetailVOS);
		return trueFormConditionDetailVOS;
	}

	private void getDictDataMatchFormConditionDetails(DictDataVO dictDataVO,HashSet<FormConditionDetailVO> trueFormConditionDetailVOS,List<FormConditionDetailVO> formConditionDetailVOS,List<ReversePolishNotationVO> reversePolishNotationVOS,Map params,String key){

		this.getMatchFormConditionDetails(trueFormConditionDetailVOS,formConditionDetailVOS,reversePolishNotationVOS,params);

		if(dictDataVO.getChildren()!=null&&dictDataVO.getChildren().size()>0){
			for(DictDataVO child:dictDataVO.getChildren()){
				params.put(key,child.getDict_data_code());
				getDictDataMatchFormConditionDetails(child,trueFormConditionDetailVOS,formConditionDetailVOS,reversePolishNotationVOS,params,key);
			}
		}
	}

	private DictDataVO getTopData(DictDataVO dictDataVO,String dictDataCode){
		if(dictDataVO.getDict_data_code()!=null&&dictDataVO.getDict_data_code().equals(dictDataCode)){
			return dictDataVO;
		}
		for(DictDataVO child:dictDataVO.getChildren()){
			DictDataVO result=this.getTopData(child,dictDataCode);
			if(result!=null){
				return result;
			}
		}
		return null;
	}

	/**
	 * 组装中间变量数据
	 * @param formCode
	 * @param tenantid
	 * @param formConditionVOS
	 * @param vo
	 * @param itemMap
	 * @param orderMap
	 * @param hideItems
	 * @param params
	 * @param conditioncodes
	 */
	private void packageTempData(String formCode,Long tenantid,List<FormConditionVO> formConditionVOS, AbstractVO vo,
								 HashMap<String,FormItemPCVO> itemMap,
								 LinkedHashMap<String,LinkedHashMap<String,LinkedHashSet<FormItemPCVO>>> orderMap,LinkedHashSet<FormItemPCVO> hideItems,
								 HashMap<String, Object> params,List<String> conditioncodes){
		List<FormItemPCVO> formitems=this.getFormItemsByFormcode(formCode,tenantid);

		//遍历原始表单，得到字段编码-itemvo的map,以及隐藏字段集合，以及排序map
		orderMap.put("none",new LinkedHashMap<>());
		for(FormItemPCVO formItemPCVO:formitems){
			itemMap.put(formItemPCVO.getFitem_code(),formItemPCVO);
			if(formItemPCVO.getFitem_input_element().equals("0")&&hideItems!=null){
				hideItems.add(formItemPCVO);
			}

			if(formItemPCVO.getFitem_input_element().equals("9")){
				orderMap.put(formItemPCVO.getFitem_code(),new LinkedHashMap<>());
			}
		}

		//遍历条件，得到参数map,以及有效的条件编码集合，以及组装总体顺序中的条件顺序
		for (FormConditionVO formConditionVO : formConditionVOS) {
			if (vo.get(formConditionVO.getCondition_code()) != null) {
				params.put(formConditionVO.getCondition_code(), vo.get(formConditionVO.getCondition_code()));
				conditioncodes.add(formConditionVO.getCondition_code());
			}else if(formConditionVO.getCondition_code().equals("none")) {
				conditioncodes.add(formConditionVO.getCondition_code());
			}

			for(String key:orderMap.keySet()){
				orderMap.get(key).put(formConditionVO.getCondition_code(),new LinkedHashSet<>());
			}
		}
	}


	@Cacheable(value="SY_FORM_ITEM",key="#formCode.concat('|').concat(#tenantid).concat('|dictitem')")
	public List<FormItemPCVO> getDictFormItemByFormcode(String formCode, Long tenantid) {
		FormVO formVO = formService.getFormVOByFormcode(formCode, tenantid);
		ObjectUtil.validNotNull(formVO, "根据表单编码："+formCode+",查找表单vo为空。|"+tenantid);

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("FORMID", formVO.getFormid());
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("fitem_isenable", SyConstant.SY_TRUE);
		dynaSqlVO.addWhereParam("fitem_input_type",9);

		VOSet<FormItemPCVO> voSet = baseService.query(FormItemPCVO.class, dynaSqlVO);
		return voSet.getVoList();
	}

}
