package com.hayden.hap.common.func.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.Collection;
import java.util.List;

@IService("funcLinkService")
public interface IFuncLinkService {
	/**
	 * 得到所有启用状态的func_link数据
	 * @return 
	 * @author lianghua
	 * @date 2015年11月18日
	 */
	public List<FuncLinkVO> getEnableFuncLinkByTenant(Long tenantid);
	
	/**
	 * 根据父功能编码查关联功能
	 * @param parentFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月3日
	 */
	public List<FuncLinkVO> getFuncLink(String mainFuncCode,Long tenantid);
	
	/**
	 * 根据父功能编码查关联功能
	 * @param mainFuncCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<FuncLinkVO> getFuncLink_RequiresNew(String mainFuncCode,Long tenantid);
	
	/**
	 * 根据状态表达式过滤掉不显示的，只留下需要显示的
	 * @param all
	 * @param vo
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年7月1日
	 */
	public List<FuncLinkVO> filterByExpress(List<FuncLinkVO> all,AbstractVO vo) throws HDException;
	
	/**
	 * 根据父子功能编码查关联功能
	 * @param mainFuncCode
	 * @param subFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月4日
	 */
	public List<FuncLinkVO> getFuncLink(String mainFuncCode,String subFuncCode,Long tenantid);
	
	/**
	 * 根据父功能编码查在主卡片显示的关联功能
	 * @param mainFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月25日
	 */
	public List<FuncLinkVO> getFuncLinkInCard(List<FuncLinkVO> list);
	
	/**
	 * 根据父功能编码查不在主卡片显示的关联功能
	 * @param mainFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月25日
	 */
	public List<FuncLinkVO> getFuncLinkOutCard(List<FuncLinkVO> list);

	public void linkUpdate(AbstractVO vo,String funcCode) throws HDException;
	
	public void linkUpdate(List<? extends AbstractVO> list,String funcCode)throws HDException;
	
//	public void linkDelete(Long pk,String funcCode)throws HDException;
	
	public void linkDelete(Collection<Long> pks,String funcCode)throws HDException;

	/**
	 * 根据子功能编码得到主功能编码
	 * @param subFuncCode
	 * @param tenantid
	 * @return 
	 * @author liyan
	 * @date 2017年7月12日
	 */
	public String getMainFuncCode(String subFuncCode, Long tenantid);

	/**
	 * 根据父功能编码获取子功能
	 * @param parentFuncCode 父功能编码集合
	 * @param tenantid 租户ID
	 * @return vo
	 */
	List<FuncLinkVO> listVOByParentFuncCode(Collection<String> parentFuncCode, Long tenantid);
}
