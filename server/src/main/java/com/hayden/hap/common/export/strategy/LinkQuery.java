//package com.hayden.hap.common.export.strategy;
//
//import java.util.List;
//
//import com.hayden.hap.common.common.entity.AbstractVO;
//import com.hayden.hap.common.common.exception.HDException;
//import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
//import com.hayden.hap.common.export.itf.ILinkQueryStrategy;
//import com.hayden.hap.common.func.entity.FuncLinkVO;
//
///**
// * 导入导出 关联查询类
// * 
// * @author liyan
// * @date 2017年7月14日
// */
//public class LinkQuery {
//
//	private ILinkQueryStrategy strategy = null;
//	
//	public LinkQuery(ILinkQueryStrategy strategy){
//		this.strategy = strategy;
//	}
//	
//	public void setLinkQueryStrategy(ILinkQueryStrategy strategy){
//		this.strategy = strategy;
//	}
//	
//	
//	public List<? extends AbstractVO> getSubVoList(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO, Long tenantid) throws HDException{
//		return strategy.getSubVoList(voList, funcLinkVO, tenantid);
//	}
//	
//	public List<? extends AbstractVO> getParentAndSubVoList(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO, Long tenantid) throws HDException{
//		return strategy.getParentAndSubVoList(voList, funcLinkVO, tenantid);
//	}
//		
//	public List<DynaSqlVO> getWhereCause(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO) throws HDException{
//		return strategy.getWhereCauseStrategy(voList, funcLinkVO);
//	}
//	
//}
