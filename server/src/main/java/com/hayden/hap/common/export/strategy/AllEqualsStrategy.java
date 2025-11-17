//package com.hayden.hap.common.export.strategy;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.springframework.util.StringUtils;
//
//import com.hayden.hap.common.common.entity.AbstractVO;
//import com.hayden.hap.common.common.entity.VOSet;
//import com.hayden.hap.common.common.exception.HDException;
//import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
//import com.hayden.hap.common.db.util.ObjectUtil;
//import com.hayden.hap.common.export.itf.ILinkQueryStrategy;
//import com.hayden.hap.common.func.entity.FuncLinkItemVO;
//import com.hayden.hap.common.func.entity.FuncLinkVO;
//import com.hayden.hap.common.utils.SyConstant;
//
///**
// * 关联查询条件都是等于的策略，批量一次性查询，然后按条进行映射
// * 
// * @author liyan
// * @date 2017年6月28日
// */
//
//public class AllEqualsStrategy implements ILinkQueryStrategy{
//	
//	private Map<String, String> itemMap = new HashMap<>();
//	
//	@Override
//	public List<AbstractVO> getSubVoList(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO, Long tenantid) throws HDException{
//		DynaSqlVO dynaSqlVO =  getWhereCauseStrategy(voList, funcLinkVO).get(0);
//		String subFuncCode = funcLinkVO.getSub_func_code();
//		String subTableName = funcService.getOperaTableNameOfFunc(subFuncCode);
//		if (!SyConstant.NONTENANTID_FUNC.contains(subFuncCode)) {
//			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
//		}
//		//一次查询得到所有的子表vo
//		VOSet<AbstractVO> voSet = baseService.query(subTableName, dynaSqlVO);
//		return voSet.getVoList();
//	}
//	
//	/**
//	 * 得到主子表对应的主表vo
//	 * @param voList
//	 * @param funcLinkVO
//	 * @param tenantid
//	 * @return
//	 * @throws HDException 
//	 * @author liyan
//	 * @date 2017年7月17日
//	 */
//	@Override
//	public List<? extends AbstractVO> getParentAndSubVoList(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO, Long tenantid) throws HDException{
//		List<AbstractVO> subList = getSubVoList(voList, funcLinkVO, tenantid);
//		String subFuncCode = funcLinkVO.getSub_func_code();
//		if(null != subList && subList.size()>0){//如果有值进行主子表对应
//			for(AbstractVO parentVo: voList){
//				int subVoListSize = 0;
//				for(AbstractVO subVo: subList){
//					Boolean isEquals = true;
//					for(Entry<String, String> entry:itemMap.entrySet()){
//						if(!parentVo.get(entry.getKey()).equals(subVo.get(entry.getValue()))){
//							isEquals = false;
//							break;
//						}
//					}
//					if(isEquals){
//						@SuppressWarnings("unchecked")
//						List<AbstractVO> subVoList = (List<AbstractVO>) parentVo.get(subFuncCode+"subVoList");
//						if(null != subVoList){
//							subVoList.add(subVo);
//							subVoListSize++;
//						//	perentVo.set(subFuncCode+"VoListSize",Integer.parseInt(perentVo.get(subFuncCode+"VoListSize").toString())+1);
//						}else{
//							subVoList = new ArrayList<>();
//							subVoList.add(subVo);
//							parentVo.set(subFuncCode+"subVoList", subVoList);
//							subVoListSize++;
//						//	perentVo.set(subFuncCode+"VoListSize", 1);
//						}
//						subList.remove(subVo);
//					}
//				}
//				if(null == parentVo.get("subVoListSize") || Integer.parseInt(parentVo.get("subVoListSize").toString())<subVoListSize) {
//					parentVo.set("subVoListSize", subVoListSize);
//				}
//			}
//		}
//		return voList;
//	}
//
//	@Override
//	public List<DynaSqlVO> getWhereCauseStrategy(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO) throws HDException {
//		DynaSqlVO dynaSqlVO =  constructWhereclauseForLinkQuery(voList, funcLinkVO);
//		List<DynaSqlVO> list = new ArrayList<>();
//		list.add(dynaSqlVO);
//		return list;
//	}
//
//	/**
//	 * 为关联查询构造查询条件
//	 * @param voList 主表volist
//	 * @param funcLinkVO 主子表关联关系
//	 * @return 
//	 * @author liyan
//	 * @throws HDException 
//	 * @date 2017年7月14日
//	 */
//	private DynaSqlVO constructWhereclauseForLinkQuery(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO) throws HDException {
//		DynaSqlVO dynaSqlVO = new DynaSqlVO();
//		
//		boolean isWhere = false;
//		
//		Map<String,Boolean> isStringMap = new HashMap<>();
//		StringBuilder whereSB = new StringBuilder();
//		boolean isOutFirst = true;
//		for(AbstractVO vo : voList) {
//			if(isOutFirst) {
//				whereSB.append(" ( ");
//			}else {
//				whereSB.append(" or ");
//			}
//			
//			isOutFirst = false;
//			
//			boolean isInnerFirst = true;
//			for(FuncLinkItemVO linkItemVO : funcLinkVO.getLinkItems()) {				
//				if(ObjectUtil.isTrue(linkItemVO.getLitem_iswhere())) {
//					if(ObjectUtil.isTrue(linkItemVO.getLitem_isvalue())) {
//						itemMap.put(linkItemVO.getLitem_main_field(), linkItemVO.getLitem_sub_field());
//						if(isInnerFirst) {
//							whereSB.append(" ( ");						
//						}else {
//							whereSB.append(" and ");
//						}
//						isInnerFirst = false;
//						
//						whereSB.append(linkItemVO.getLitem_sub_field());
//						whereSB.append(" = ");
//						
//						Boolean isString = isStringMap.get(linkItemVO.getLitem_main_field());
//						Object value = vo.get(linkItemVO.getLitem_main_field());
//						if(isString==null) {
//							if(value instanceof String) {
//								isStringMap.put(linkItemVO.getLitem_main_field(), Boolean.TRUE);
//								appendSingleQuotes(whereSB, value);
//							}else {
//								isStringMap.put(linkItemVO.getLitem_main_field(), Boolean.FALSE);
//								whereSB.append(value);
//							}
//						}else if(isString){
//							appendSingleQuotes(whereSB, value);
//						}else {
//							whereSB.append(value);
//						}	
//						isWhere = true;
//					}	
//				}
//			}
//			if(!isInnerFirst) {
//				whereSB.append(" ) ");
//			}
//		}
//		if(!isOutFirst) {
//			whereSB.append(" ) ");
//		}		
//		
//		if(isWhere) {//如果没有关联字段作为条件，则不处理
//			dynaSqlVO.addWhereClause(whereSB.toString());
//		}
//		if(StringUtils.hasLength(funcLinkVO.getLink_where())) {
//			dynaSqlVO.addWhereClause(funcLinkVO.getLink_where());
//		}
//		return dynaSqlVO;
//	}
//	
//	private void appendSingleQuotes(StringBuilder sb,Object value) {
//		sb.append(" '");
//		sb.append(value);
//		sb.append("' ");
//	}
//	
//
//}
