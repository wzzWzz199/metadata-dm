package com.hayden.hap.common.func.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.func.entity.FuncLinkMVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年5月10日
 */
@IService("funcLinkMService")
public interface IFuncLinkMService {

	/**
	 * 获取所有启用的关联功能新型
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年5月11日
	 */
	List<FuncLinkMVO> getEnableFuncLinkByTenant(Long tenantid);
	
	/**
	 * 根据父功能编码查关联功能
	 * @param parentFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月3日
	 */
	public List<FuncLinkMVO> getFuncLink(String mainFuncCode,Long tenantid);
	
	/**
	 * 根据父子功能编码查关联功能
	 * @param mainFuncCode
	 * @param subFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月4日
	 */
	public List<FuncLinkMVO> getFuncLink(String mainFuncCode,String subFuncCode,Long tenantid);
	
	/**
	 * 关联删除
	 * @param pks
	 * @param funcCode
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年5月12日
	 */
	public void linkDelete(Collection<Long> pks, String funcCode) throws HDException;
	
	public void linkUpdate(AbstractVO vo,String funcCode) throws HDException;
	
	public void linkUpdate(List<? extends AbstractVO> list,String funcCode)throws HDException;

	/**
	 * 根据父功能编码获取子功能
	 * @param parentFuncCode 父功能编码集合
	 * @param tenantid 租户ID
	 * @return vo
	 */
	List<FuncLinkMVO> listVOByParentFuncCode(Collection<String> parentFuncCode, Long tenantid);
}
