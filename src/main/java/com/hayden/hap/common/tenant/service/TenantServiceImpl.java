package com.hayden.hap.common.tenant.service;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.entity.TenantVO;
import com.hayden.hap.common.tenant.entity.TenantWrapVO;
import com.hayden.hap.common.tenant.itf.ITenantService;
import com.hayden.hap.common.user.entity.UserVO;
import com.hayden.hap.common.user.itf.IUserService;
import com.hayden.hap.common.utils.CacheUtils;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("tenantService")
public class TenantServiceImpl implements ITenantService {
	
	@Autowired
	private IBaseService baseService;
	
	@Override
	@Cacheable(value="tenantCache",key="#tenantCode")
	public TenantVO getTenantByCode(String tenantCode) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		TenantVO tenantVO=new TenantVO();
		StringBuffer whereStringBuffer = new StringBuffer("tenantcode='");
		whereStringBuffer.append(tenantCode);
		whereStringBuffer.append("'");
		dynaSqlVO.setWhereClause(whereStringBuffer.toString());
		VOSet<TenantVO> voSet=baseService.query(tenantVO, dynaSqlVO);
		tenantVO = (TenantVO) voSet.getVO(0);
		return tenantVO;
	}
	@Override
	@Cacheable(value="tenantCache",key="#tenantId")
	public TenantVO getTenantById(Long tenantId) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("tenantpk", tenantId);
		VOSet<TenantVO> voSet=baseService.query(TenantVO.class, dynaSqlVO);
		return voSet.getVO(0);
	}
	@Override
	public TenantVO getEnableTenantById(Long tenantId){
		TenantVO tenantVO= ((ITenantService)AppServiceHelper.findBean("tenantService")).getTenantById(tenantId);
		if(tenantVO!=null&&tenantVO.getIsenable().intValue()==SyConstant.SY_TRUE)
			return tenantVO;
		else
			return null;
	}
	
	@Override
	public TenantVO getEnableTenantByCode(String tenantCode){
		TenantVO tenantVO= ((ITenantService)AppServiceHelper.findBean("tenantService")).getTenantByCode(tenantCode);
		if(tenantVO!=null&&tenantVO.getIsenable().intValue()==SyConstant.SY_TRUE)
			return tenantVO;
		else
			return null;
	}
	
	

	@Override
	public List<TenantVO> getTenantList(Integer isdeploy) {
		TenantVO tenantVO = new TenantVO();
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("isdeploy", isdeploy);
		VOSet<TenantVO> voSet = baseService.query(tenantVO, dynaSqlVO);
		return voSet.getVoList();
	}

	@Override
	public List<Long> getTenantPkList(Integer isdeploy) {
		List<TenantVO> tenantList = this.getTenantList(isdeploy);
		if(tenantList != null){
			List<Long> tenantPkList = new ArrayList<Long>();
			for(TenantVO tv : tenantList){
				tenantPkList.add(tv.getTenantpk());
			}
			return tenantPkList;
		}
		return null;
	}

	
	@Override
	public UserVO getAdminUserOfTenant(Long tenantid) {
		@SuppressWarnings("deprecation")
		TenantVO tenantVO = baseService.queryByPK(new TenantVO(), tenantid);
		IUserService userService = 	AppServiceHelper.findBean(IUserService.class, "userService");
		UserVO userVO = userService.findUser(tenantVO.getAdminuser(), tenantVO.getTenantpk());
		return userVO;
	}
	@Override
	public void deleteTenantById(Long tenantId){
		TenantVO vo= ((ITenantService)AppServiceHelper.findBean("tenantService")).getTenantById(tenantId);
		if(vo!=null)
		{
			this.baseService.delete(vo);
			CacheUtils.getInstance().evict("tenantCache", tenantId);
			CacheUtils.getInstance().evict("tenantCache", vo.getTenantcode());
		}
	}
	/**
	 * 修改租户并清除对应缓存
	 * @param tenant
	 * @param dynaSqlVO 
	 * @author wushuangyang
	 * @date 2016年11月10日
	 */
	@Override
	@Caching(evict={
			@CacheEvict(value="tenantCache",key="#tenant.tenantpk"),
			@CacheEvict(value="tenantCache",key="#tenant.tenantcode")
	})
	public void updateTenantAndEvictCache(TenantVO tenant,DynaSqlVO dynaSqlVO){
		if(dynaSqlVO==null)
		{
			dynaSqlVO=new DynaSqlVO();
		}
		this.baseService.update(tenant, dynaSqlVO);
	}
	
	
	/**
	 * 删除租户并清除对应缓存
	 * @param tenant
	 * @author wushuangyang
	 * @date 2016年11月10日
	 */
	@Override
	@Caching(evict={
			@CacheEvict(value="tenantCache",key="#tenant.tenantpk"),
			@CacheEvict(value="tenantCache",key="#tenant.tenantcode")
	})
	public void deleteTenantAndEvictCache(TenantVO tenant){
		this.baseService.delete(tenant);
	}
	
	@Override
	public List<TenantWrapVO> getTenantsByCuserExcludeExp(String tenantids) {
		List<TenantWrapVO> result = new ArrayList<>();
		if(StringUtils.isEmpty(tenantids)) {
			return result;
		}
		
//		TenantWrapVO experience = getExperienceTenant();		
//		if(tenantids.equals(experience.getTenantid().toString()))//体验租户排除掉
//			return result;
		
		String[] tenantidArr = tenantids.split(",");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("tenantpk", tenantidArr);
		dynaSqlVO.setOrderByClause(" tenantpk asc");
		VOSet<TenantVO> voset = baseService.query(TenantVO.class, dynaSqlVO);	
		List<TenantVO> tenantvos = voset.getVoList();
		if(ObjectUtil.isNotEmpty(tenantvos)) {
			for(TenantVO tenantVO : tenantvos) {
				if(ObjectUtil.isTrue(tenantVO.getIs_experience())) {
					continue;
				}
				result.add(new TenantWrapVO(tenantVO));
			}
		}
		return result;
	}
	
	/**
	 * 根据公海用户的所属租户字符串，获取租户信息
	 * @param tenantids
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月14日
	 */
	@Override
	public List<TenantWrapVO> getTenantsByCuser(String tenantids) {
		List<TenantWrapVO> result = new ArrayList<>();
		if(StringUtils.isEmpty(tenantids))
			return result;
		
		String[] tenantidArr = tenantids.split(",");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("tenantpk", tenantidArr);
		dynaSqlVO.setOrderByClause(" tenantpk asc");
		VOSet<TenantVO> voset = baseService.query(TenantVO.class, dynaSqlVO);	
		List<TenantVO> tenantvos = voset.getVoList();
		if(ObjectUtil.isNotEmpty(tenantvos)) {
			for(TenantVO tenantVO : tenantvos) {				
				result.add(new TenantWrapVO(tenantVO));
			}
		}
		return result;
	}
	
	@Override
	public TenantWrapVO getSuitableTenant(List<TenantWrapVO> tenantList, Long designatedTenantid) {
		if(ObjectUtil.isEmpty(tenantList)) {//如果租户列表为空，返回空
			return null;
		}
		
		if(tenantList.size()==1) {//租户列表就一个的话就返回它了
			return tenantList.get(0);
		}
		
		if(designatedTenantid==null) {//没有指定租户，则返回最后那个，一般来说最后那个是他最想要的
			return tenantList.get(tenantList.size()-1);
		}
		
		TenantWrapVO designatedExp = null;
		for(TenantWrapVO wrapVO : tenantList) {//从当前列表查找指定的
			if(designatedTenantid.equals(wrapVO.getTenantid())) {
				if(ObjectUtil.isTrue(wrapVO.getIsExperience())) {//如果指定的是体验租户，先标记
					designatedExp = wrapVO;
				}else {
					return wrapVO;
				}				
			}
		}
		
		if(designatedExp!=null && tenantList.size()>1) {//如果指定了体验租户，但拥有其它租户，返回正常租户
			for(TenantWrapVO wrapVO : tenantList) {
				if(!ObjectUtil.isTrue(wrapVO.getIsExperience())) {
					return wrapVO;
				}
			}
		}
		
		if(designatedExp!=null) {//如果没有正常租户，则就是体验租户了
			return designatedExp;
		}
		
		//如果没有找到指定的还是返回最后那个
		return tenantList.get(tenantList.size()-1);
	}
	
	@Override
	public TenantWrapVO getExperienceTenant() {		
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("is_experience", SyConstant.SY_TRUE);
		dynaSqlVO.addWhereParam("isenable", SyConstant.SY_TRUE);
		
		List<TenantVO> list = baseService.query(TenantVO.class, dynaSqlVO).getVoList();
		if(ObjectUtil.isEmpty(list)) {
			throw new HDRuntimeException("没有预置体验租户");
		}
		return new TenantWrapVO(list.get(0));
	}
	
	/**
	 * 是否体验租户
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年7月4日
	 */
	@Override
	public Boolean isExperienceTenant(Long tenantid) {
		ITenantService thisService = AppServiceHelper.findBean(ITenantService.class);
		TenantVO tenantVO = thisService.getTenantById(tenantid);
		return ObjectUtil.isTrue(tenantVO.getIs_experience());
	}

	@Override
	public List<TenantVO> getTenantIsDeploy() {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("isdeploy", SyConstant.SY_TRUE);
		dynaSqlVO.addWhereParam("isenable", SyConstant.SY_TRUE);
		dynaSqlVO.addWhereClause("tenantpk not in (1,2000000000002)");
		List<TenantVO> list = baseService.query(TenantVO.class, dynaSqlVO).getVoList();
		if(ObjectUtil.isEmpty(list)) {
			throw new HDRuntimeException("没有租户信息");
		}

		return list;
	}
}
