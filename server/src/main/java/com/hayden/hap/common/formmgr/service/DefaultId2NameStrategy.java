package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IId2NameStrategy;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * 默认查询选择返名称策略<br/>
 * 通过查询对应的表来实现
 * @author zhangfeng
 * @date 2016年9月9日
 */
public class DefaultId2NameStrategy implements IId2NameStrategy {

	/** 
	 * 查询选择，单选时候给名称字段赋值<br/>
	 * 逻辑：根据业务VO拿到查询选择功能的主键，再根据主键获取查询选择的VO，最后根据输入设定给对应字段赋值
	 * @see com.hayden.hap.common.formmgr.itf.IId2NameStrategy#assignName4single(com.hayden.hap.common.common.entity.AbstractVO, com.hayden.hap.common.formmgr.entity.Id2NameVO)
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	@Override
	public void assignName4single(AbstractVO abstractVO, Id2NameVO id2NameVO) {
		QueryselectorInputConfigVO inputConfigVO = id2NameVO.getInputConfigVO();
		String tableName = id2NameVO.getTableName();
		Long tenantid = id2NameVO.getTenantid();
		String uniqueColName = id2NameVO.getUniqueColName();
		
		String uniqueValue = abstractVO.getString(inputConfigVO.getMap().get(uniqueColName));
		
		if(StringUtils.isEmpty(uniqueValue)) {
			return;
		}
				
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam(uniqueColName, uniqueValue);
		dynaSqlVO.addWhereClause(id2NameVO.getWhere());
		VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);
		if(!ObjectUtil.isNotEmpty(voset.getVoList())) {
			return;
		}
		for(Entry<String, String> entry : inputConfigVO.getMap().entrySet()) {
			abstractVO.set(entry.getValue(), voset.getVoList().get(0).get(entry.getKey()));
		}
	}

	/** 
	 * 查询选择，单选时候批量给名称字段赋值<br/>
	 * 逻辑:拿到业务VO集合里所有查询选择功能的主键，根据这些主键查询到对应的VO集合，业务VO根据外建匹配对应的查询VO，赋值各个属性
	 * @see com.hayden.hap.common.formmgr.itf.IId2NameStrategy#assignName4single(java.util.List, com.hayden.hap.common.formmgr.entity.Id2NameVO)
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	@Override
	public void assignName4single(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO) {
		QueryselectorInputConfigVO inputConfigVO = id2NameVO.getInputConfigVO();
		String tableName = id2NameVO.getTableName();
		Long tenantid = id2NameVO.getTenantid();
		String uniqueColName = id2NameVO.getUniqueColName();
		
		Set<String> uniqueValues = new HashSet<>();
		for(AbstractVO abstractVO : abstractVOs) {
			String uniqueValue = abstractVO.getString(inputConfigVO.getMap().get(uniqueColName));
			if(StringUtils.isEmpty(uniqueValue))
				continue;
			uniqueValues.add(uniqueValue);
		}
		
		
		if(uniqueValues.size()==0) {
			return;
		}
		
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam(uniqueColName, uniqueValues);
		dynaSqlVO.addWhereClause(id2NameVO.getWhere());
		VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);
		
		for(AbstractVO abstractVO : abstractVOs) {
			for(AbstractVO selectorVO:voset.getVoList()) {
				if(selectorVO.getString(uniqueColName).equals(abstractVO.getString(inputConfigVO.getMap().get(uniqueColName)))) {
					for(Entry<String, String> entry : inputConfigVO.getMap().entrySet()) {
						abstractVO.set(entry.getValue(), selectorVO.get(entry.getKey()));
					}
					break;
				}
			}					
		}

	}

	/** 
	 * 查询选择，多选选时候给名称字段赋值
	 * 逻辑：根据业务VO拿到查询选择功能的主键集合，再根据主键集合获取查询选择的VO集合，最后根据输入设定给对应字段赋值
	 * @see com.hayden.hap.common.formmgr.itf.IId2NameStrategy#assignName4multiple(com.hayden.hap.common.common.entity.AbstractVO, com.hayden.hap.common.formmgr.entity.Id2NameVO)
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	@Override
	public void assignName4multiple(AbstractVO abstractVO, Id2NameVO id2NameVO) {
		QueryselectorInputConfigVO inputConfigVO = id2NameVO.getInputConfigVO();
		String tableName = id2NameVO.getTableName();
		String uniqueColName = id2NameVO.getUniqueColName();
		Long tenantid = id2NameVO.getTenantid();
		String split=inputConfigVO.getSplit();
		String pkValueStr = abstractVO.getString(inputConfigVO.getMap().get(uniqueColName));
		
		if(StringUtils.isEmpty(pkValueStr)) {
			return;
		}
		
		String[] pkValueArr = pkValueStr.split(split);
		List<String> pkValueList = new ArrayList<>();
		for(String pkValue : pkValueArr) {
			pkValueList.add(pkValue);
		}
		
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam(uniqueColName, pkValueList);
		dynaSqlVO.addWhereClause(id2NameVO.getWhere());
		VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);

		if(!ObjectUtil.isNotEmpty(voset.getVoList())) {
			return;
		}
		
		List<AbstractVO> voList = new ArrayList<>();
		for(String pk : pkValueList) {
			boolean isAdd = false;
			for(AbstractVO vo : voset.getVoList()) {
				if(pk.equals(vo.getString(uniqueColName))) {
					voList.add(vo);
					isAdd = true;
					break;
				}
			}
			if(!isAdd) {
				voList.add(null);
			}
		}
		
		for(Entry<String, String> entry : inputConfigVO.getMap().entrySet()) {
			StringBuilder valueBuilder = new StringBuilder();
			for(AbstractVO vo : voList) {
				if(vo!=null) {
					valueBuilder.append(vo.get(entry.getKey()));
				}
				valueBuilder.append(split);
			}
			if(valueBuilder.length()>0)
				valueBuilder.deleteCharAt(valueBuilder.length()-split.length());
			abstractVO.set(entry.getValue(), valueBuilder.toString());
		}

	}

	/** 
	 * 查询选择，多选选时候批量给名称字段赋值
	 * 逻辑:拿到业务VO集合里所有查询选择功能的主键，根据这些主键查询到对应的VO集合，业务VO根据外建匹配对应的查询VO集合，赋值各个属性
	 * @see com.hayden.hap.common.formmgr.itf.IId2NameStrategy#assignName4multiple(java.util.List, com.hayden.hap.common.formmgr.entity.Id2NameVO)
	 * @author zhangfeng
	 * @date 2016年9月9日
	 */
	@Override
	public void assignName4multiple(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO) {
		QueryselectorInputConfigVO inputConfigVO = id2NameVO.getInputConfigVO();
		String tableName = id2NameVO.getTableName();
		String uniqueColName = id2NameVO.getUniqueColName();
		Long tenantid = id2NameVO.getTenantid();
		String split=inputConfigVO.getSplit();
		String foreignKey = inputConfigVO.getMap().get(uniqueColName);
		Set<String> uniqueValueSet = new HashSet<>();
		for(AbstractVO abstractVO : abstractVOs) {
			String uniqueValueStr = abstractVO.getString(foreignKey);
			if(StringUtils.isNotEmpty(uniqueValueStr)) {
				String[] uniqueValueStrArr = uniqueValueStr.split(split);
				for(String uniqueValue : uniqueValueStrArr) {
					uniqueValueSet.add(uniqueValue);
				}
			}
		}
		
		if(!ObjectUtil.isNotEmpty(uniqueValueSet)) {
			return ;
		}
		
		IBaseService baseService = AppServiceHelper.findBean(IBaseService.class);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam(uniqueColName, uniqueValueSet);
		dynaSqlVO.addWhereClause(id2NameVO.getWhere());
		VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);

		if(!ObjectUtil.isNotEmpty(voset.getVoList())) {
			return;
		}		
		
		Map<String,AbstractVO> idStr2VOMap = new HashMap<>();
		for(AbstractVO vo : voset.getVoList()) {
			idStr2VOMap.put(vo.getString(uniqueColName),vo);
		}
		
		for(AbstractVO vo : abstractVOs) {
			String foreignKeys = vo.getString(foreignKey);
			if(StringUtils.isEmpty(foreignKeys))
				continue;
			
			String[] foreignKeyArr = foreignKeys.split(split);
			List<AbstractVO> queryResultVO = new ArrayList<>();//存储当前VO所关联的查询选择VO集合
			for(String foreign : foreignKeyArr) {
				queryResultVO.add(idStr2VOMap.get(foreign));
			}
			
			if(!ObjectUtil.isNotEmpty(queryResultVO)) //如果没有匹配到任何记录，不处理
				continue;
			
			for(Entry<String, String> entry : inputConfigVO.getMap().entrySet()) {
				StringBuilder valueBuilder = new StringBuilder();
				for(AbstractVO foreignVO : queryResultVO) {
					if(foreignVO!=null) {
						valueBuilder.append(foreignVO.get(entry.getKey()));
					}
					valueBuilder.append(split);
				}
				if(valueBuilder.length()>0)
					valueBuilder.deleteCharAt(valueBuilder.length()-1);
				vo.set(entry.getValue(), valueBuilder.toString());
			}
		}
	}

}
