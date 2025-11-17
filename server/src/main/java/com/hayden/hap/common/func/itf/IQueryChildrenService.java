package com.hayden.hap.common.func.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2017年7月18日
 */
@IService("queryChildrenService")
public interface IQueryChildrenService {

	<T extends AbstractVO> String getWhereStr(List<T> parentList,FuncLinkVO linkVO,Long tenantid) throws HDException;
	
	/**
	 * 解析关联功能条件
	 * @param funcLink
	 * @param parentVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年7月14日
	 */
	String parseFunclinkClause(List<FuncLinkItemVO> linkItems,AbstractVO parentVO);
	
	/**
	 * 根据父功能数据获取子功能数据
	 * @param parentList
	 * @param linkVO
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	<T extends AbstractVO> List<T> getChildren(List<T> parentList,FuncLinkVO linkVO,Long tenantid) throws HDException;
	
	/**
	 * 根据父功能数据获取子功能数据
	 * @param parentList
	 * @param linkVO
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	<T extends AbstractVO> List<T> getChildren4Export(List<T> parentList,FuncLinkVO linkVO,Long tenantid) throws HDException;
	
	/**
	 * 匹配父子关系（键为父vo，值为子vo集合）
	 * @param parentList
	 * @param children
	 * @param linkVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月18日
	 */
	<T extends AbstractVO> Map<T,List<? extends AbstractVO>> matchChildren(List<T> parentList, List<? extends AbstractVO> children, FuncLinkVO linkVO);
}
