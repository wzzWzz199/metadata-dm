//package com.hayden.hap.common.export.strategy;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//
//import com.hayden.hap.common.common.entity.AbstractVO;
//import com.hayden.hap.common.common.entity.VOSet;
//import com.hayden.hap.common.common.exception.HDException;
//import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
//import com.hayden.hap.common.export.itf.ILinkQueryStrategy;
//import com.hayden.hap.common.func.entity.FuncLinkVO;
//import com.hayden.hap.common.utils.SyConstant;
//
///**
// * 关联查询条件有 不是等于 的策略，一条一条查询，效率低
// * 
// * @author liyan
// * @date 2017年6月28日
// */
//
//public class NotAllEqualsStrategy implements ILinkQueryStrategy{
//	
//	
//	@Override
//	public List<? extends AbstractVO> getSubVoList(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO, Long tenantid) throws HDException{
//		List<AbstractVO> subList = new ArrayList<>();
//		List<DynaSqlVO> dynaSqlVOList =  getWhereCauseStrategy(voList, funcLinkVO);
//		String subFuncCode = funcLinkVO.getSub_func_code();
//		String subTableName = funcService.getOperaTableNameOfFunc(subFuncCode);
//		Boolean hasTenantid = false;
//		if (!SyConstant.NONTENANTID_FUNC.contains(subFuncCode)) {
//			hasTenantid = true;
//		}
//		for(DynaSqlVO dynaSqlVO: dynaSqlVOList){
//			if(hasTenantid){
//				dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
//			}
//			VOSet<AbstractVO> voSet = baseService.query(subTableName, dynaSqlVO);
//			//构造主子孙表每条数据的对应关系，主表一个vo1对应子表一个list和子表list的length字段sunListSize
//			if(null != voSet && voSet.getVoList().size()>0){
//				subList.addAll(voSet.getVoList());
//			}	
//		}
//		return subList;
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
//		List<DynaSqlVO> dynaSqlVOList =  getWhereCauseStrategy(voList, funcLinkVO);
//		String subFuncCode = funcLinkVO.getSub_func_code();
//		String subTableName = funcService.getOperaTableNameOfFunc(subFuncCode);
//		Boolean hasTenantid = false;
//		if (!SyConstant.NONTENANTID_FUNC.contains(subFuncCode)) {
//			hasTenantid = true;
//		}
//		int i = 0;
//		for(AbstractVO parentVO: voList){
//			DynaSqlVO dynaSqlVO = dynaSqlVOList.get(i++);
//			if(hasTenantid){
//				dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
//			}
//			VOSet<AbstractVO> voSet = baseService.query(subTableName, dynaSqlVO);
//			//构造主子孙表每条数据的对应关系，主表一个vo1对应子表一个list和子表list的length字段sunListSize
//			if(null != voSet && voSet.getVoList().size()>0){
//				parentVO.set(subFuncCode+"subVoList", voSet.getVoList());
//				if(null == parentVO.get("subVoListSize") || Integer.parseInt(parentVO.get("subVoListSize").toString())<voSet.getVoList().size()) {
//					parentVO.set("subVoListSize", voSet.getVoList().size());
//				}
//			}	
//		}
//		return voList;
//	}
//	
//
//	@Override
//	public List<DynaSqlVO> getWhereCauseStrategy(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO) {
//		List<DynaSqlVO> list = new ArrayList<>();
//		DynaSqlVO dynaSqlVO = new DynaSqlVO();
//		for(AbstractVO parentVO: voList){
//			String whereCause = listFormService.parseFunclinkClause(funcLinkVO, parentVO);
//			dynaSqlVO.addWhereClause(whereCause);
//			if(StringUtils.isNotEmpty(funcLinkVO.getLink_where())) {
//				dynaSqlVO.addWhereClause(funcLinkVO.getLink_where());
//			}
//			list.add(dynaSqlVO);
//		}
//		return list;
//	}
//
//	
//
//}
