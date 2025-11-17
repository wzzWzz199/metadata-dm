package com.hayden.hap.common.func.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.func.entity.FuncLinkDataVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月16日
 */
@IService("funcLinkDataService")
public interface IFuncLinkDataService {

	/**
	 * 关联更新
	 * @param vo
	 * @param funcCode 
	 * @author zhangfeng
	 * @date 2016年12月29日
	 */
	public void linkUpdate(AbstractVO vo,String funcCode,Long tenantid);
	
	/**
	 * 关联更新
	 * @param list
	 * @param funcCode 
	 * @author zhangfeng
	 * @date 2016年12月29日
	 */
	public void linkUpdate(List<? extends AbstractVO> list,String funcCode,Long tenantid);
	
	/**
	 * 关联删除
	 * @param pk
	 * @param funcCode
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月29日
	 */
	public void linkDelete(Long pk,String funcCode,Long tenantid)throws HDException;
	
	/**
	 * 关联删除
	 * @param list
	 * @param funcCode
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月29日
	 */
	public void linkDelete(Collection<Long> list,String funcCode,Long tenantid)throws HDException;
	
	@Deprecated
	/**
	 * 根据功能编码获取关联数据
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月29日
	 */
	public List<FuncLinkDataVO> getFuncLinkDatasByFunccode(String funcCode,Long tenantid);
	
	/**
	 * 根据功能编码获取关联数据
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月29日
	 */
	public List<FuncLinkDataVO> getFuncLinkDatasByFunccode_Cache(String funcCode,Long tenantid);
}
