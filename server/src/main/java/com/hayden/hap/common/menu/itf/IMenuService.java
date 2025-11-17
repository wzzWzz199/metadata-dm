package com.hayden.hap.common.menu.itf;

import com.hayden.hap.common.menu.entity.MenuVO;

import java.util.Collection;
import java.util.List;

/**
 * 菜单服务
 * 
 * @author zhangfeng
 * @date 2018年2月8日
 */
public interface IMenuService {
	
	/**
	 * 根据租户得到该租户下的所有启用菜单
	 * @return 
	 * @author lianghua
	 * @date 2015年11月17日
	 */
	public List<MenuVO> getEnableMenuListByTenant(Long tenantid,String clientType);
	
	/**
	 * 根据租户得到该租户下的所有菜单(包含启用与未启用的)
	 * @author wushuangyang
	 * @param tenantid
	 * @param clientType
	 * @param isBuyFilter
	 * @date 2016-06-08 17:11:00
	 */
	public List<MenuVO> getAllMenuListByTenant(Long tenantid,String clientType);

	/**
	 * 根据菜单编码集合查找对应的菜单VO
	 * @param menuCodes 菜单编码集合
	 * @param tenantid 租户ID
	 * @return 菜单VO集合
	 * @author zhaishaofeng
	 * @date 2020年1月7日
	 */
	List<MenuVO> listMenuVOByCodeAndTenantId(Collection<String> menuCodes, Long tenantid);
	
}
