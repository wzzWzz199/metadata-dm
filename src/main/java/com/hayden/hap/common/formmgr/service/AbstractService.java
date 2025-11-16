/**
 * Project Name:hap-sy
 * File Name:AbstractService.java
 * Package Name:com.hayden.hap.sy.formmgr.service
 * Date:2016年1月16日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.billcode.itf.IBillCodeService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.CommonVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVoConstants;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.form.service.DefaultTableCallback;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.formmgr.itf.ICardFormService;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.utils.Code2NameHandleUtils;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.utils.DictUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.Map.Entry;


/**
 * ClassName:AbstractService ().<br/>
 * Date:     2016年1月16日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public abstract class AbstractService{
	@Autowired
	protected IBaseService baseService;
	@Autowired
	protected IFuncService funcService;
	
	@Autowired
	protected IFuncLinkService funcLinkService;
	
	@Autowired
	protected IFormService formService;
	@Autowired
	protected IListFormService listFormService;
	
	@Autowired
	protected IFormItemService formItemService;
	
	@Autowired
	protected ITableDefService tableDefService;
	
	@Autowired
	protected ICardFormService cardFormService;	
	
	@Autowired
	protected IBillCodeService billCodeService;
	
	protected CommonVO getSelectColumns(String funcCode,String pk,String headTable)
	{
		List<String> sqlColumnList = new ArrayList<String>();//启用的列
		List<FormItemVO> handleItems = new ArrayList<FormItemVO>();//字典无名称
		Long tenantid = CurrentEnvUtils.getTenantId();
		List<? extends FormItemVO> IFormItemVOList = formItemService
				.getFormItemsByFunccode(funcCode, tenantid);
		for(FormItemVO IFormItemVO:IFormItemVOList)
		{
			if(ObjectUtil.isTrue(IFormItemVO.getFitem_isenable()))
			{
				sqlColumnList.add(IFormItemVO.getFitem_code());
				if(InputTypeEnum.DICT_NEW.getCode().equals(IFormItemVO.getFitem_input_type())) {
					handleItems.add(IFormItemVO);
				}					
			}
		}
		DynaSqlVO dynaSqlVO =  new DynaSqlVO();
		dynaSqlVO.setSqlColumnList(sqlColumnList);
		Map<String,Object> whereParamMap = new HashMap<String,Object>();
		String pkColName = tableDefService.getPkColName(new DefaultTableCallback(headTable));
		whereParamMap.put(pkColName, pk);
		whereParamMap.put(TableColumnVoConstants.TENANTID,CurrentEnvUtils.getTenantId());
		dynaSqlVO.setWhereParamMap(whereParamMap);
		VOSet<CommonVO> voSet = baseService.query(new CommonVO(headTable), dynaSqlVO);// 得到headVo的对象
		for(FormItemVO itemVO:handleItems) {
			for(AbstractVO abstractVO : voSet.getVoList()) {
				Code2NameHandleUtils.handleDictNN(abstractVO, itemVO, tenantid);
			}
		}		
		CommonVO vo = voSet.getVO(0);
		return vo;
	}
	
	protected Map<String, List<DictDataWarperVO>> getDictVos(Map<String,String> dictDataMap) throws HDException {
		Map<String, List<DictDataWarperVO>> dictvos = new HashMap<String, List<DictDataWarperVO>>();
		for(Entry<String, String> entry : dictDataMap.entrySet()) {
			String inputConfigStr = entry.getValue();
			DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(inputConfigStr);
			if(dictInputConfigVO==null) {//没配输入设定
				continue;
			}
			if(!dictInputConfigVO.isIsload4m()) {//移动端不加载
				continue;
			}
			if(dictInputConfigVO.getDictdata()!=null) {//字典数据直接配在输入设定里
				dictvos.put(entry.getKey(), dictInputConfigVO.getDictdata());
				continue;
			}
			
			List<DictDataWarperVO> dictDataWarperVoList = DictUtils.getDictData(dictInputConfigVO.getDictcode());
			dictvos.put(entry.getKey(), dictDataWarperVoList);
		}
		return dictvos;
	}
	
	public LinkedHashMap<String, List<CommonVO>> getBodyVos(CommonVO vo,
			LinkedHashMap<String, Map<String, String>> linkMap) {
		Long tenantid = CurrentEnvUtils.getTenantId();
		LinkedHashMap<String, List<CommonVO>> bodyvos = new LinkedHashMap<String, List<CommonVO>>();
		Iterator<Entry<String, Map<String, String>>> iter = linkMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, Map<String, String>> entery = iter.next();
			String subFuncCode = entery.getKey();
			DynaSqlVO dynaSqlVO = new DynaSqlVO();// 查询条件
			Map<String, String> map = entery.getValue();
			Iterator<Entry<String, String>> itemIter = map.entrySet()
					.iterator();
			while (itemIter.hasNext()) {
				Entry<String, String> itemEntry = itemIter.next();
				String mainItemCode = itemEntry.getKey();
				String subItemCode = itemEntry.getValue();
				dynaSqlVO.addWhereParam(subItemCode, vo.get(mainItemCode));
			}
			//功能下表单的查询表
//			FormVO formVo = formService.getFormVOByFunccode(subFuncCode, ThreadLocalUtils.getLong(SyConstant.TENANT_STR));
			String queryTable = funcService
					.getQueryTableNameOfFunc(subFuncCode, tenantid);
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR,
					CurrentEnvUtils.getTenantId());// Where条件
			// selectColumns
			List<String> sqlColumnList = new ArrayList<String>();
			List<? extends FormItemVO> IFormItemVOList = formItemService
					.getFormItemsByFunccode(subFuncCode, CurrentEnvUtils.getTenantId());
			for (FormItemVO IFormItemVO : IFormItemVOList) {
				sqlColumnList.add(IFormItemVO.getFitem_code());//表单属性定义
			}
			dynaSqlVO.setSqlColumnList(sqlColumnList);
			VOSet<CommonVO> voSet = baseService.query(new CommonVO(queryTable), dynaSqlVO);
			bodyvos.put(subFuncCode, voSet.getVoList());
		}
		return bodyvos;
	}
	


	/**
	 * generalKeyOfDict:(生成字典项的编码，解析JSON对象时,通过表和字段能够找到对应的字典). <br/>
	 * date: 2015年12月25日 <br/>
	 *
	 * @author ZhangJie
	 * @param queryTable
	 * @param fitem_code
	 * @return
	 */
	protected String generalKeyOfDict(String queryTable,String fitem_code)
	{
		return queryTable+fitem_code;
	}
	
	/**
	 * getMapofLinkItem:(根据功能编码得到数据表关联关系). <br/>
	 * date: 2015年12月24日 <br/>
	 *
	 * @author ZhangJie
	 * @param funcCode
	 *            功能编码
	 * @return
	 */
	public LinkedHashMap<String, Map<String, String>> getMapofLinkItem(
			String funcCode) {
		LinkedHashMap<String, Map<String, String>> linkMap = new LinkedHashMap<String, Map<String, String>>();
		List<FuncLinkVO> funclinkList = funcLinkService.getFuncLink(funcCode,CurrentEnvUtils.getTenantId());
		for (FuncLinkVO funcLink : funclinkList) {
			List<FuncLinkItemVO> itemList = funcLink.getLinkItems();
			for (FuncLinkItemVO funcLinkItem : itemList) {
				String subFuncCode = funcLinkItem.getSub_func_code();
				String mainField = funcLinkItem.getLitem_main_field();
				String subField = funcLinkItem.getLitem_sub_field();
				if (linkMap.containsKey(subFuncCode)) {
					linkMap.get(subFuncCode).put(mainField, subField);
				} else {
					Map<String, String> map = new HashMap<String, String>();
					map.put(mainField, subField);
					linkMap.put(subFuncCode, map);
				}
			}
		}
		return linkMap;
	}	
	
	
	protected void saveBodyVos(Map<String, List<CommonVO>> bodyvos,Map<String,Object> headReturns,Long tenantid)
	{
		Iterator<Entry<String, List<CommonVO>>> iter = bodyvos.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, List<CommonVO>> entry = iter.next();
			String funcCode = entry.getKey();
/*			FormVO formVo = formService.getFormVOByFunccode(funcCode, tenantid);
			String tableName = formVo.getOpera_table_code();//操作表			
*/			List<CommonVO> voList = entry.getValue();
			if(voList==null || voList.isEmpty())
				continue;
			List<CommonVO> deleteVoList = new ArrayList<CommonVO>();
			List<CommonVO> updateVoList = new ArrayList<CommonVO>();
			List<CommonVO> insertVoList = new ArrayList<CommonVO>();
			//查询列
			DynaSqlVO dynaSqlVo = new DynaSqlVO();
			if(voList!=null&&voList.size()>0)
			{
				List<String> sqlColumnList = new ArrayList<String>();
				Map<String,Object> colMap = voList.get(0).getColumnValues();
				Iterator<Entry<String, Object>> iterCols = colMap.entrySet().iterator();
				while(iterCols.hasNext())
				{
					Entry<String, Object> entryCols = iterCols.next();
					sqlColumnList.add(entryCols.getKey());
				}
				dynaSqlVo.setSqlColumnList(sqlColumnList);
			}
			//
			String headFuncCode = String.valueOf(headReturns.get(HEADFUNCCODE));
			LinkedHashMap<String, Map<String, String>> linkedItem = getMapofLinkItem(headFuncCode);
			Map<String,String> linkedMap = linkedItem.get(funcCode);
			for(CommonVO commonVo:voList)
			{
//				commonVo.setTableName(tableName);
				Object pkValue = baseService.getVOPkColValue(commonVo);
				if(ObjectUtil.isNotNull(pkValue))
				{
					if(commonVo.getDataStatus()==SyConstant.DATA_STATS_DELETED)
					{
						//删除
						deleteVoList.add(commonVo);
					}else
					{
						//修改
						updateVoList.add(commonVo);
					}
				}else
				{
					//新增
					Iterator<Entry<String, String>> iterLinkCols = linkedMap.entrySet().iterator();
					while (iterLinkCols.hasNext()) {
						Entry<String, String> entryCol = iterLinkCols.next();
						commonVo.set(entryCol.getValue(), ((CommonVO)headReturns.get(VO)).getString(entryCol.getKey()));
					}					
					insertVoList.add(commonVo);
				}
			}
			if(deleteVoList.size()>0)
			{
				baseService.deleteBatch(deleteVoList);
				//TODO(ACTION)
			}
			if(updateVoList.size()>0)
			{
				baseService.updateBatch(updateVoList,dynaSqlVo);
				//TODO(ACTION)				
			}
			if(insertVoList.size()>0)
			{
				//TODO(ACTION)				
				baseService.insertBatch(insertVoList);
			}			
		}
	}
	
	
	protected Map<String,Object> saveHeadVo(Map<String, CommonVO> headMap,Long tenantid) throws HDException
	{
		//返回值类型
		Map<String,Object> headReturns = new HashMap<String,Object>();
		String headFuncCode = "";
		CommonVO headVo = new CommonVO();
		Iterator<Entry<String, CommonVO>> headIter = headMap.entrySet().iterator();
		if(headIter.hasNext())
		{
			Entry<String, CommonVO> entryHead = headIter.next();
			headFuncCode = entryHead.getKey();
			headVo = entryHead.getValue();
		}
		
		List<Message> beforErrorList = null;
		IAction action = formService.getActionByFuncCode(headFuncCode, tenantid);
		if(action!=null) {
			beforErrorList = action.beforeCardSave(null, headVo);
		}
		boolean isAdd = false;
		Integer dataStatus = headVo.getDataStatus();
/*		FormVO formVo = formService.getFormVOByFunccode(headFuncCode, tenantid);
		String tableName = formVo.getOpera_table_code();//操作表
		headVo.setTableName(tableName);*/
		Object pkValue = baseService.getVOPkColValue(headVo);
		headVo.set(SyConstant.TENANT_STR, tenantid);
		if(ObjectUtil.isNotNull(pkValue))
		{
			//有主键，保存操作
			if(dataStatus==SyConstant.DATA_STATS_DELETED)
			{
				baseService.deleteByPKAndTenantid(headVo,String.valueOf(pkValue), tenantid);
			}else
			{
				DynaSqlVO dynaSqlVo = new DynaSqlVO();
				List<String> sqlColumnList = new ArrayList<String>();
				Map<String,Object> colMap = headVo.getColumnValues();
				Iterator<Entry<String, Object>> iter = colMap.entrySet().iterator();
				while(iter.hasNext())
				{
					Entry<String, Object> entry = iter.next();
					sqlColumnList.add(entry.getKey());
				}
				dynaSqlVo.setSqlColumnList(sqlColumnList);
				baseService.update(headVo,dynaSqlVo);				
			}
			headReturns.put(VO, headVo);
		}else
		{
			//无主键，新建记录
			CommonVO commonVo = baseService.insert(headVo);
			headReturns.put(VO, commonVo);
			pkValue = baseService.getVOPkColValue(commonVo);
			isAdd = true;
		}
		headReturns.put(TABLENAME, headVo.getTableName());
		
		headReturns.put(HEADFUNCCODE, headFuncCode);
		//TODO(需要补充action执行方法)
		if(action!=null) {
			beforErrorList = action.afterCardSave(null, headVo, isAdd);
		}		
		return headReturns;
	}	
	
	private static String TABLENAME = "tableName";
	private static String VO = "vo";
	private static String HEADFUNCCODE = "headFuncCode";	

}

