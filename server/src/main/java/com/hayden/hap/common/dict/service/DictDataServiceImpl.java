package com.hayden.hap.common.dict.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.entity.DictVersionWarper;
import com.hayden.hap.common.dict.itf.IDictDataService;
import com.hayden.hap.common.dict.itf.IDictService;
import com.hayden.hap.common.enumerate.DictType;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.enumerate.ProductFlagEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.form.service.DefaultTableCallback;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.ICardFormService;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.orgpermission.entity.OrgPermissionDTVO;
import com.hayden.hap.common.orgpermission.itf.IOrgPermissionMaintainService;
import com.hayden.hap.common.orgpermission.itf.IOrgPermissionService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.DictUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VOCollectionUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月7日
 */
@Service("dictDataService")
public class DictDataServiceImpl implements IDictDataService{

	private static final Logger logger = LoggerFactory.getLogger(DictDataServiceImpl.class);
	
	@Autowired
	private IBaseService baseService;

	@Autowired
	private IDictService dictService;

	@Autowired
	private IListFormService listFormService;

	@Autowired
	private ITableDefService tableDefService;

	@Autowired
	private IFormService formService;

	@Autowired
	private ICardFormService cardFormService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IOrgPermissionService orgPermissionService;
	
	@Autowired
	private IOrgPermissionMaintainService orgPermissionMaintainService;

//	/**
//	 * 树形字典虚拟顶点的id
//	 */
	private static final Long TOP_NODE_ID = null;
	
	private static final String TOP_NODE_CODE = null;
	
	@Override
	@Cacheable(value="SY_DICT_DATA",key="#dictCode.concat('|').concat(#tenantid)") 
	public List<DictDataVO> getDictData(String dictCode, Long tenantid) {

		DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
		if(dictVO==null) {
			return null;
		}

		if(DictType.LIST.getCode().equals(dictVO.getDict_type())) {//列表字典
			if(SyConstant.INNER_DICT_TABLE.equals(dictVO.getDict_t_table().toLowerCase())) {//内部字典
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("dictid", dictVO.getDictid());
				dynaSqlVO.addWhereParam("tenantid", tenantid);
				
				if(StringUtils.hasLength(dictVO.getDict_where())) {
					dynaSqlVO.addWhereClause(dictVO.getDict_where());
				}
				
				if(StringUtils.hasLength(dictVO.getDict_f_isenable())) {
					dynaSqlVO.addWhereParam(dictVO.getDict_f_isenable(), SyConstant.SY_TRUE);
				}
				
				dynaSqlVO.setOrderByClause(" dict_data_order ");
				
				VOSet<DictDataVO> dictDataVoSet = baseService.query(DictDataVO.class, dynaSqlVO);
				return dictDataVoSet.getVoList();
			}else {
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("tenantId", tenantid);
				
				if(StringUtils.hasLength(dictVO.getDict_where())) {
					dynaSqlVO.addWhereClause(dictVO.getDict_where());
				}
				
				if(StringUtils.hasLength(dictVO.getDict_f_isenable())) {
					dynaSqlVO.addWhereParam(dictVO.getDict_f_isenable(), SyConstant.SY_TRUE);
				}
				
				if(StringUtils.hasLength(dictVO.getDict_f_order())) {
					dynaSqlVO.setOrderByClause(dictVO.getDict_f_order());
				}
				
				AbstractVO queryVO = new BaseVO(dictVO.getDict_t_table());
				VOSet<AbstractVO> voSet = baseService.query(queryVO, dynaSqlVO);
				
				return businessVO2DictDataVO(dictVO, dictVO.getDict_t_table(), voSet.getVoList());
			}
		}else {
			return getTreeData(dictVO,null,true);
		}

	}

	@Override
	@Cacheable(value="SY_DICT_DATA",key="#dictCode.concat('|').concat(#tenantid).concat(#extWhere)")
	public List<DictDataVO> getDictData(String dictCode, Long tenantid,
			String extWhere) {
		DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
		if(dictVO==null) {
			return null;
		}

		if(DictType.LIST.getCode().equals(dictVO.getDict_type())) {//列表字典
			if(SyConstant.INNER_DICT_TABLE.equals(dictVO.getDict_t_table())) {//内部字典
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("dictid", dictVO.getDictid());
				dynaSqlVO.addWhereParam("tenantid", tenantid);
				dynaSqlVO.setWhereClause(extWhere);
				
				if(StringUtils.hasLength(dictVO.getDict_where())) {
					dynaSqlVO.addWhereClause(dictVO.getDict_where());
				}
				VOSet<DictDataVO> dictDataVoSet = baseService.query(DictDataVO.class, dynaSqlVO);
				return dictDataVoSet.getVoList();
			}else {
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("tenantId", tenantid);
				dynaSqlVO.setWhereClause(extWhere);
				
				if(StringUtils.hasLength(dictVO.getDict_where())) {
					dynaSqlVO.addWhereClause(dictVO.getDict_where());
				}
				VOSet<AbstractVO> voSet = baseService.query(dictVO.getDict_t_table(), dynaSqlVO);
				
				return businessVO2DictDataVO(dictVO, dictVO.getDict_t_table(), voSet.getVoList());
			}
		}else {
			return getTreeData(dictVO,extWhere,true);
		}

	}

	@Override
	public List<DictDataVO> getDictDataWithTopnode(String dictCode,
			Long tenantid) {
		IDictDataService dictDataService = AppServiceHelper.findBean(IDictDataService.class);
		List<DictDataVO> list = dictDataService.getDictData(dictCode, tenantid);
		
		DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
		if(dictVO==null) {
			return null;
		}

//		if(DictType.LIST.getCode().equals(dictVO.getDict_type())) {//给列表字典加顶点，因为树形字典已经加过顶点了...
			DictDataVO topNode = new DictDataVO();
			topNode.setDict_data_name(dictVO.getDict_name());
			topNode.setDictdataid(TOP_NODE_ID);
			topNode.setDict_data_code(TOP_NODE_CODE);
			topNode.setChildren(list);
			
			List<DictDataVO> result = new ArrayList<DictDataVO>();
			result.add(topNode);
			return result;
//		}
//		
//		return list;
	}
	
	

	/**
	 * 业务对象转换为字典数据对象
	 * @param dictVO
	 * @param dictTableName
	 * @param businessVOs
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月8日
	 */
	@Override
	public List<DictDataVO> businessVO2DictDataVO(DictVO dictVO,String dictTableName,List<AbstractVO> businessVOs) {
		String pkColumn = tableDefService.getPkColName(new DefaultTableCallback(dictTableName));

		String codeField = dictVO.getDict_f_code();
		String nameField = dictVO.getDict_f_name();
		String orderField = dictVO.getDict_f_order();
		String layerField = dictVO.getDict_f_layer();
		String parentField = dictVO.getDict_f_parent();
		String isEnableField = dictVO.getDict_f_isenable();

		List<DictDataVO> dictDataList = new ArrayList<DictDataVO>();
		for(AbstractVO abstractVO : businessVOs) {
			DictDataVO dictDataVO = new DictDataVO();
			dictDataVO.setDictdataid(abstractVO.getLong(pkColumn));
			dictDataVO.setDict_data_code(abstractVO.getString(codeField));
			dictDataVO.setDict_data_name(abstractVO.getString(nameField));
			dictDataVO.set("product_flag",ProductFlagEnum.PROJECT.getIntCode());
			if(orderField!=null)
				dictDataVO.setDict_data_order(abstractVO.getString(orderField));
			
			if(layerField!=null)
				dictDataVO.setDict_data_layer(abstractVO.getInt(layerField));
			
			if(parentField!=null)
				dictDataVO.setDict_data_parent(abstractVO.getLong(parentField));
			
			if(isEnableField!=null)
				dictDataVO.setDict_data_isenable(abstractVO.getInt(isEnableField));
			dictDataList.add(dictDataVO);
		}

		return dictDataList;
	}
	

	/**
	 * 根据字典查树形字典数据
	 * @param dictVO 字典对象
	 * @param extWhere 额外条件
	 * @param isEnableFlag 是否只查启用的数据-
	 * @author zhangfeng
	 * @date 2016年1月11日
	 */
	public List<DictDataVO> getTreeData(DictVO dictVO, String extWhere, boolean isEnableFlag) {
		List<DictDataVO> list = queryAll(dictVO,null, extWhere, isEnableFlag);
		
		String topName = dictVO.getDict_name();
		
		if(StringUtils.hasLength(dictVO.getDict_f_parent())) {			
			return castParentTreeData(list,topName);
		}
		return castGeneralTreeData(list,topName);
	}
	/**
	 * 根据字典查树形字典数据
	 * @param dictVO 字典对象
	 * @param extWhere 额外条件
	 * @param isEnableFlag 是否只查启用的数据-
	 * @author zhangfeng
	 * @date 2016年1月11日
	 */
	public List<DictDataVO> getTreeData(DictVO dictVO,String funcCode, String extWhere, boolean isEnableFlag) {
		List<DictDataVO> list = queryAll(dictVO,funcCode, extWhere, isEnableFlag);
		
		String topName = dictVO.getDict_name();
		
		if(StringUtils.hasLength(dictVO.getDict_f_parent())) {			
			return castParentTreeData(list,topName);
		}
		return castGeneralTreeData(list,topName);
	}

	private List<DictDataVO> queryAll(DictVO dictVO,String funcCode, String extWhere, boolean isEnableFlag) {
//		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(CurrentEnvUtils.getTenantId());
		Long currentDataTenantid = dictVO.getTenantid();
		
		String dictTableName = dictVO.getDict_t_table();
		ObjectUtil.validNotNull(dictTableName, "字典没有配置字典数据表");

		if(SyConstant.INNER_DICT_TABLE.equals(dictVO.getDict_t_table().toLowerCase())) {
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam("dictid", dictVO.getDictid());
			dynaSqlVO.addWhereParam("tenantid",currentDataTenantid);
			dynaSqlVO.setWhereClause(extWhere);
			
			if(isEnableFlag && StringUtils.hasLength(dictVO.getDict_f_isenable())) {
				dynaSqlVO.addWhereParam(dictVO.getDict_f_isenable(), SyConstant.SY_TRUE);
			}
			
			if(StringUtils.hasLength(dictVO.getDict_f_order())) {
				dynaSqlVO.setOrderByClause(dictVO.getDict_f_order());
			}
			
			VOSet<DictDataVO> voset = baseService.query(DictDataVO.class, dynaSqlVO);
			return voset.getVoList();
		}
		
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("tenantid",currentDataTenantid);
		dynaSqlVO.setWhereClause(extWhere);
		
		if(StringUtils.hasLength(dictVO.getDict_where())) {
			dynaSqlVO.addWhereClause(dictVO.getDict_where());
		}
		
		if(isEnableFlag && StringUtils.hasLength(dictVO.getDict_f_isenable())) {
			dynaSqlVO.addWhereParam(dictVO.getDict_f_isenable(), SyConstant.SY_TRUE);
		}
		
		if(StringUtils.hasLength(dictVO.getDict_f_order())) {
			dynaSqlVO.setOrderByClause(dictVO.getDict_f_order());
		}else {
			String pkcolumn = tableDefService.getPkColName(dictTableName);
			dynaSqlVO.setOrderByClause(pkcolumn + " asc");
		}
		boolean isDataPowerFilters = false;
		if(SyConstant.SY_TRUE == dictVO.getDict_data_flag()  
				&& org.apache.commons.lang3.StringUtils.isNotBlank(funcCode)
				&& "sy_org".equalsIgnoreCase(dictTableName)) {
			
			isDataPowerFilters = true;
			//数据权限校验
			OrgPermissionDTVO dto = new OrgPermissionDTVO();
			dto.setFuncCode(funcCode);
			dto.setOrgid(CurrentEnvUtils.getOrgId());
			dto.setUserid(CurrentEnvUtils.getUserId());
			dto.setTenantid(CurrentEnvUtils.getTenantId());
			try {
				orgPermissionService.applyOrgPermission(dynaSqlVO, dto);
			} catch (HDException e) {
				logger.error("字典数据权限匹配异常!",e);
			}

			for(Map.Entry<String, String> entry : SyConstant.ORGPERMISSION_COL_MAP.entrySet()) 			{
				if (dynaSqlVO.getWhereParamMap().containsKey(entry.getValue()))	{
					Object orgList = dynaSqlVO.getWhereParamMap().get(entry.getValue());
					dynaSqlVO.removeWhereParam(entry.getValue());
					dynaSqlVO.addWhereParam("orgid",orgList);
				}
			}
		}
		AbstractVO queryVO = new BaseVO(dictTableName);
		VOSet<AbstractVO> voSet = baseService.query(queryVO, dynaSqlVO);
		if(isDataPowerFilters) {
			this.dataPowerFilters(voSet);
		}
		return businessVO2DictDataVO(dictVO, dictTableName, voSet.getVoList());
	}
	/**
	 * 数据权限过滤数据
	 * @param condition_codes
	 * @param voSet
	 * @param orgId_curr 
	 * @author haocs
	 * @date 2019年11月1日
	 */
	private VOSet<AbstractVO> dataPowerFilters(VOSet<AbstractVO> voSet) { 
		
		Map<Long, AbstractVO> orgMap = VOCollectionUtils.groupedByProp(voSet.getVoList(), SyConstant.ORGID,Long.class);
		
		for (AbstractVO abstractVO : voSet.getVoList()) {
			Long parentid = abstractVO.getLong("parentid",null);
			if(parentid!=null && !orgMap.containsKey(parentid)) {
				abstractVO.set("parentid",null);
			}
		}
		return voSet;
	}
	/**
	 * 根据parentId的方式来组装树结构
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月11日
	 */
	private List<DictDataVO> castParentTreeData(List<DictDataVO> dataList, String topNodeName) {
		List<DictDataVO> dictTreeData = new ArrayList<DictDataVO>();
		for(DictDataVO data:dataList) {//组装字典层级
			if(data.getDict_data_parent()==null) {
				data.setChildren(assemble(dataList, data.getDictdataid()));
				dictTreeData.add(data);
			}
		}
		
//		DictDataVO topNode = new DictDataVO();
//		topNode.setDict_data_name(topNodeName);
//		topNode.setDictdataid(TOP_NODE_ID);
//		topNode.setDict_data_code(TOP_NODE_CODE);
//		topNode.setChildren(dictTreeData);
//		
//		List<DictDataVO> result = new ArrayList<DictDataVO>();
//		result.add(topNode);
		
		return dictTreeData;
	}
	
	
	private List<DictDataVO> assemble(List<DictDataVO> list,Long parentId) {
		List<DictDataVO> result = new ArrayList<DictDataVO>();
		
		for(DictDataVO data:list) {
			if(parentId.equals(data.getDict_data_parent())) {
					data.setChildren(assemble(list,data.getDictdataid()));			
				result.add(data);
			}
		}
		
		return result;
	}
	
	private List<DictDataVO> castGeneralTreeData(List<DictDataVO> dataList,String topNodeName) {
		Map<String,Integer> layerMap = getMinAndMaxLayer(dataList);
		int min = layerMap.get("MIN");
		int max = layerMap.get("MAX");
		
		List<DictDataVO> temp = new ArrayList<DictDataVO>();
		for(DictDataVO data:dataList) {
			//TODO 可以不用层级字段，直接使用编码规则构造
			if(data.getDict_data_layer() == min) {
				if(min<max) {
					data.setChildren(assemble(dataList,min+1,max,data.getDict_data_code()));
				}
				temp.add(data);
			}
		}
		
//		DictDataVO topNode = new DictDataVO();
//		topNode.setDict_data_name(topNodeName);
//		topNode.setDictdataid(TOP_NODE_ID);
//		topNode.setDict_data_code(TOP_NODE_CODE);
//		topNode.setChildren(temp);
//		
//		List<DictDataVO> result = new ArrayList<DictDataVO>();
//		result.add(topNode);
		
//		return result;
		return temp;
	}
	
	private Map<String,Integer> getMinAndMaxLayer(List<DictDataVO> list) {
		int min = 100;
		int max = 0;
		for(DictDataVO data : list) {
			int layer = data.getDict_data_layer();
			if(layer > max) {
				max = layer;
			}
			if(layer < min) {
				min = layer;
			}
		}
		
		Map<String,Integer> map = new HashMap<String, Integer>();
		map.put("MAX", max);
		map.put("MIN", min);
		return map;
	}
	
	/**
	 * 组装层级结构(树形)
	 * @param dataList
	 * @return 
	 * @author zhangfeng
	 * @date 2014年12月2日
	 */
	private List<DictDataVO> assemble(List<DictDataVO> dataList, int startlayer, int maxlayer, String parentCode) {
		List<DictDataVO> result = new ArrayList<DictDataVO>();
		
		for(DictDataVO data:dataList) {
			if(data.getDict_data_layer()==startlayer && data.getDict_data_code().indexOf(parentCode)==0) {
				if(startlayer < maxlayer) {
					data.setChildren(assemble(dataList,startlayer+1,maxlayer,data.getDict_data_code()));
				}				
				result.add(data);
			}
		}
		
		return result;
	}

	/**
	 * 
	 *
	 * @see com.hayden.hap.common.dict.itf.IDictDataService#addTopnode(java.util.List, java.lang.String, java.lang.String)
	 * @author zhangfeng
	 * @date 2016年9月1日
	 */
	@Override
	public List<DictDataWarperVO> addTopnode(List<DictDataWarperVO> list, String itemCode, String itemName) {
		DictDataWarperVO topNode = new DictDataWarperVO();
		topNode.setName(itemName);
		topNode.setDictdataid(TOP_NODE_ID);
		topNode.setCode(TOP_NODE_CODE);
		topNode.setChildren(list);
		
		List<DictDataWarperVO> result = new ArrayList<DictDataWarperVO>();
		result.add(topNode);
		return result;
//		return list;
	}

	@Override
	public List<DictVersionWarper> getDictVersionWarpers(List<DictVersionWarper> list, Long tenantid) {
		if(ObjectUtil.isEmpty(list))
			return list;
		
		IDictDataService service = AppServiceHelper.findBean(IDictDataService.class);
		
		for(DictVersionWarper versionWarper : list) {
			versionWarper.setStatus(DictVersionWarper.STATUS_UPDATED);
			versionWarper.setTs("0");
			versionWarper.setTenantid(tenantid);
			
			List<DictDataVO> dataVOs = service.getDictData(versionWarper.getCode(), tenantid);
			if(ObjectUtil.isEmpty(dataVOs))
				continue;
			
			List<DictDataWarperVO> warperVOs = new ArrayList<>();
			for(DictDataVO dataVO : dataVOs) {
				DictDataWarperVO warperVO = new DictDataWarperVO(dataVO);
				warperVOs.add(warperVO);
			}
			versionWarper.setDictData(warperVOs);
		}
		
		return list;
	}	
	
	@Override
	public Map<String,List<DictDataWarperVO>> getDictMap(List<? extends FormItemVO> itemVOs, String funcTree, Long tenantid) 
			throws HDException {
		Map<String,List<DictDataWarperVO>> map = new HashMap<>();
		
		for(FormItemVO itemVO : itemVOs) {
			if(InputTypeEnum.DICT_NEW.getCode().equals(itemVO.getFitem_input_type())) {
				String inputConfigStr = itemVO.getFitem_input_config();
				DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfigStr);
				if(dictInputConfigVO==null)
					continue;
				String dictCode = dictInputConfigVO.getDictcode();
				List<DictDataWarperVO> list = DictUtils.getDictData(dictInputConfigVO, tenantid);
				map.put(dictCode, list);
			}else if(!StringUtils.isEmpty(funcTree)) {
				
				String[] arr = funcTree.split(",");
				for(String itemCode : arr) {
					if(!itemVO.getFitem_code().equals(itemCode)) {
						continue;
					}					
					String inputConfigStr = itemVO.getFitem_input_config();
					if(StringUtils.isEmpty(inputConfigStr)) {
						throw new HDException("全息查询字段没有配置字典");
					}
					DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfigStr);
					if(dictInputConfigVO==null)
						continue;
					String dictCode = dictInputConfigVO.getDictcode();
					List<DictDataWarperVO> list = DictUtils.getDictData(dictInputConfigVO, tenantid);
					map.put(dictCode, list);
				}								
			}
		}
		return map;
	}
	
	
	@Override
	public Map<String,List<DictDataWarperVO>> getDictMap(List<? extends FormItemVO> itemVOs, Long tenantid) 
			throws HDException {
		Map<String,List<DictDataWarperVO>> map = new HashMap<>();
		
		for(FormItemVO itemVO : itemVOs) {
			if(InputTypeEnum.DICT_NEW.getCode().equals(itemVO.getFitem_input_type())) {
				String inputConfigStr = itemVO.getFitem_input_config();
				DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfigStr);
				if(dictInputConfigVO==null)
					continue;
				String dictCode = dictInputConfigVO.getDictcode();
				List<DictDataWarperVO> list = DictUtils.getDictData(dictInputConfigVO, tenantid);
				map.put(dictCode, list);
			}
		}
		return map;
	}

	@Override
	public List<DictDataVO> getDictDataWithTopnode(String funcCode,String dictCode, Long tenantid) {
		IDictDataService dictDataService = AppServiceHelper.findBean(IDictDataService.class);
		List<DictDataVO> list = dictDataService.getDictData(funcCode,dictCode, tenantid);
		
		DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
		if(dictVO==null) {
			return null;
		}

//		if(DictType.LIST.getCode().equals(dictVO.getDict_type())) {//给列表字典加顶点，因为树形字典已经加过顶点了...
			DictDataVO topNode = new DictDataVO();
			topNode.setDict_data_name(dictVO.getDict_name());
			topNode.setDictdataid(TOP_NODE_ID);
			topNode.setDict_data_code(TOP_NODE_CODE);
			topNode.setChildren(list);
			
			List<DictDataVO> result = new ArrayList<DictDataVO>();
			result.add(topNode);
			return result;
//		}
//		
//		return list;
	}

	@Override
	public List<DictDataVO> getDictData(String funcCode, String dictCode, Long tenantid) {

		DictVO dictVO = dictService.getDictByCode_Cache(dictCode, tenantid);
		if(dictVO==null) {
			return null;
		}

		if(DictType.LIST.getCode().equals(dictVO.getDict_type())) {//列表字典
			if(SyConstant.INNER_DICT_TABLE.equals(dictVO.getDict_t_table().toLowerCase())) {//内部字典
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("dictid", dictVO.getDictid());
				dynaSqlVO.addWhereParam("tenantid", tenantid);
				
				if(StringUtils.hasLength(dictVO.getDict_where())) {
					dynaSqlVO.addWhereClause(dictVO.getDict_where());
				}
				
				if(StringUtils.hasLength(dictVO.getDict_f_isenable())) {
					dynaSqlVO.addWhereParam(dictVO.getDict_f_isenable(), SyConstant.SY_TRUE);
				}
				
				dynaSqlVO.setOrderByClause(" dict_data_order ");
				
				VOSet<DictDataVO> dictDataVoSet = baseService.query(DictDataVO.class, dynaSqlVO);
				return dictDataVoSet.getVoList();
			}else {
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("tenantId", tenantid);
				
				if(StringUtils.hasLength(dictVO.getDict_where())) {
					dynaSqlVO.addWhereClause(dictVO.getDict_where());
				}
				
				if(StringUtils.hasLength(dictVO.getDict_f_isenable())) {
					dynaSqlVO.addWhereParam(dictVO.getDict_f_isenable(), SyConstant.SY_TRUE);
				}
				
				if(StringUtils.hasLength(dictVO.getDict_f_order())) {
					dynaSqlVO.setOrderByClause(dictVO.getDict_f_order());
				}
				
				AbstractVO queryVO = new BaseVO(dictVO.getDict_t_table());
				VOSet<AbstractVO> voSet = baseService.query(queryVO, dynaSqlVO);
				
				return businessVO2DictDataVO(dictVO, dictVO.getDict_t_table(), voSet.getVoList());
			}
		}else {
			return getTreeData(dictVO,funcCode,null,true);
		}

	}

}
