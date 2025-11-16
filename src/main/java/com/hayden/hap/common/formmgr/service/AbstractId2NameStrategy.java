package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IId2NameStrategy;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * id返名称策略抽象类<br/>
 * 对于一些查询选择返名称利用缓存进行优化的功能，比如用户、部门等等，那么可以继承这个策略，只需要实现根据主键（集合）来获取对应VO（集合）就可以了
 * @author zhangfeng
 * @date 2016年9月9日
 */
public abstract class AbstractId2NameStrategy implements IId2NameStrategy {

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
		
		AbstractVO selectorVO = getVO(tableName, uniqueColName, uniqueValue, tenantid);
		if(selectorVO==null)
			return;
		
		exclude(inputConfigVO.getMap(), inputConfigVO.getExclusions());
		
		for(Entry<String, String> entry : inputConfigVO.getMap().entrySet()) {
			abstractVO.set(entry.getValue(), selectorVO.get(entry.getKey()));
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
		
		List<? extends AbstractVO> voList = getVOList(tableName, uniqueColName, uniqueValues, tenantid);
		
		exclude(inputConfigVO.getMap(), inputConfigVO.getExclusions());
		
		for(AbstractVO abstractVO : abstractVOs) {
			for(AbstractVO selectorVO:voList) {
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
		
		String pkValueStr = abstractVO.getString(inputConfigVO.getMap().get(uniqueColName));
		
		if(StringUtils.isEmpty(pkValueStr)) {
			return;
		}
		
		String[] pkValueArr = pkValueStr.split(",");
		List<String> uniqueValueList = new ArrayList<>();
		for(String pkValue : pkValueArr) {
			uniqueValueList.add(pkValue);
		}

		List<? extends AbstractVO> abstractVOs = getVOList(tableName, uniqueColName, uniqueValueList, tenantid);
		
		if(!ObjectUtil.isNotEmpty(abstractVOs)) {
			return;
		}
		
		List<AbstractVO> voList = new ArrayList<>();
		for(String pk : uniqueValueList) {
			boolean isAdd = false;
			for(AbstractVO vo : abstractVOs) {
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
		
		exclude(inputConfigVO.getMap(), inputConfigVO.getExclusions());
		
		for(Entry<String, String> entry : inputConfigVO.getMap().entrySet()) {
			StringBuilder valueBuilder = new StringBuilder();
			for(AbstractVO vo : voList) {
				if(vo!=null) {
					valueBuilder.append(vo.get(entry.getKey()));
				}
				valueBuilder.append(",");
			}
			if(valueBuilder.length()>0)
				valueBuilder.deleteCharAt(valueBuilder.length()-1);
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
		
		String foreignKey = inputConfigVO.getMap().get(uniqueColName);
		Set<String> uniqueValueSet = new HashSet<>();
		for(AbstractVO abstractVO : abstractVOs) {
			String uniqueValueStr = abstractVO.getString(foreignKey);
			if(StringUtils.isNotEmpty(uniqueValueStr)) {
				String[] uniqueValueStrArr = uniqueValueStr.split(",");
				for(String uniqueValue : uniqueValueStrArr) {
					uniqueValueSet.add(uniqueValue);
				}
			}
		}
		
		if(!ObjectUtil.isNotEmpty(uniqueValueSet)) {
			return ;
		}

		List<? extends AbstractVO> voList = getVOList(tableName, uniqueColName, uniqueValueSet, tenantid);
		
		if(!ObjectUtil.isNotEmpty(voList)) {
			return;
		}		
		
		Map<String,AbstractVO> idStr2VOMap = new HashMap<>();
		for(AbstractVO vo : voList) {
			idStr2VOMap.put(vo.getString(uniqueColName),vo);
		}
		
		exclude(inputConfigVO.getMap(), inputConfigVO.getExclusions());
		
		for(AbstractVO vo : abstractVOs) {
			String foreignKeys = vo.getString(foreignKey);
			if(StringUtils.isEmpty(foreignKeys))
				continue;
			
			String[] foreignKeyArr = foreignKeys.split(",");
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
					valueBuilder.append(",");
				}
				if(valueBuilder.length()>0)
					valueBuilder.deleteCharAt(valueBuilder.length()-1);
				vo.set(entry.getValue(), valueBuilder.toString());
			}
		}
	}
	
	/**
	 * 排除不需要返名称的字段
	 * @param map
	 * @param exclusions
	 * @return 
	 * @author zhangfeng
	 * @date 2018年12月4日
	 */
	private Map<String, String> exclude(Map<String, String> map, List<String> exclusions) {
		if(ObjectUtil.isEmpty(exclusions))
			return map;
		
		for(String exclusion : exclusions) {
			if(exclusion==null)
				continue;
			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, String> entry = it.next();
				if(exclusion.equals(entry.getValue())) {
					it.remove();//使用迭代器的remove()方法删除元素
				}
			}
		}
		
		return map;
	}

	/**
	 * 根据查询选择功能的主键集合获取VO记录集合（子类来实现这个动作）
	 * @param tableName
	 * @param pks
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年11月7日
	 */
	protected abstract List<? extends AbstractVO> getVOList(String tableName,String uniqueColName,
			Collection<String> uniqueValues,Long tenantid);
	
	/**
	 * 根据查询选择功能的主键获取VO记录（子类来实现这个动作）
	 * @param tableName
	 * @param pk
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年11月7日
	 */
	protected abstract AbstractVO getVO(String tableName,String uniqueColName,String uniqueValue,Long tenantid);
}
