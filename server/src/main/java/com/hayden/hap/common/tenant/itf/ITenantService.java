package com.hayden.hap.common.tenant.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.spring.service.IService;
import com.hayden.hap.common.tenant.entity.TenantVO;
import com.hayden.hap.common.tenant.entity.TenantWrapVO;
import com.hayden.hap.common.user.entity.UserVO;

import java.util.List;

@IService("tenantService")
public interface ITenantService {
	/**
	 * 根据租户code得到租户对象
	 * @return 
	 * @author lianghua
	 * @date 2015年11月11日
	 */
	public TenantVO getTenantByCode(String tenantCode);
	
	/**
	 * 根据租户id获取租户信息，并放入缓存中
	 * @param tenantId
	 * @return 
	 * @author wushuangyang
	 * @date 2016年9月6日
	 */
	public TenantVO getTenantById(Long tenantId);
	
	/**
	 * 根据租户id获取启用的租户信息，并间接放入缓存
	 * @param tenantId
	 * @return 
	 * @author wushuangyang
	 * @date 2016年9月6日
	 */
	public TenantVO getEnableTenantById(Long tenantId);
	
	/**
	 * 根据租户code得到有效的租户
	 * @param tenantCode
	 * @return 
	 * @author lianghua
	 * @date 2015年11月12日
	 */
	public TenantVO getEnableTenantByCode(String tenantCode);
	
	/**
	 * 通过isdeploy标记，获取租户列表
	 * @param isdeploy
	 * @return
	 */
	public List<TenantVO> getTenantList(Integer isdeploy);
	
	/**
	 * 通过isdeploy标记，获取租户表主键列表
	 * @param isdeploy
	 * @return
	 */
	public List<Long> getTenantPkList(Integer isdeploy);
	
	/**
	 * 获得租户下的管理员用户
	 * @param tenangid
	 * @return
	 * @throws HDException
	 */
	public UserVO getAdminUserOfTenant(Long tenantid);
	
	/**
	 * 根据租户id删除租户
	 * @param tenantId 
	 * @author wushuangyang
	 * @date 2016年9月6日
	 */
	public void deleteTenantById(Long tenantId);
	
	/**
	 * 修改租户并清除对应缓存
	 * @param tenant
	 * @param dynaSqlVO 
	 * @author wushuangyang
	 * @date 2016年11月10日
	 */
	public void updateTenantAndEvictCache(TenantVO tenant, DynaSqlVO dynaSqlVO);
	
	/**
	 * 删除租户并清除对应缓存
	 * @param tenant
	 * @author wushuangyang
	 * @date 2016年11月10日
	 */
	public void deleteTenantAndEvictCache(TenantVO tenant);
	
	/**
	 * 根据公海用户的所属租户字符串，获取除体验租户外的其它所有租户信息
	 * @param tenantids
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月14日
	 */
	List<TenantWrapVO> getTenantsByCuserExcludeExp(String tenantids);
	
	/**
	 * 根据公海用户的所属租户字符串，获取租户信息
	 * @param tenantids
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月14日
	 */
	List<TenantWrapVO> getTenantsByCuser(String tenantids);
	
	/**
	 * 获取合适的租户信息，如果指定了租户id就返回特定的租户
	 * @param tenantList
	 * @param designatedTenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月14日
	 */
	TenantWrapVO getSuitableTenant(List<TenantWrapVO> tenantList, Long designatedTenantid);
	
	/**
	 * 获取体验租户
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月14日
	 */
	TenantWrapVO getExperienceTenant();
	
	/**
	 * 是否体验租户
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年7月4日
	 */
	Boolean isExperienceTenant(Long tenantid);

	List<TenantVO> getTenantIsDeploy();

}
