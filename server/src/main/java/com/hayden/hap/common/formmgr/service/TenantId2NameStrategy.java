package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.entity.TenantVO;
import com.hayden.hap.common.tenant.itf.ITenantService;
import com.hayden.hap.common.utils.SyConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年9月12日
 */
public class TenantId2NameStrategy extends AbstractId2NameStrategy {

	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.service.AbstractId2NameStrategy#getVOList(java.lang.String, java.util.Collection, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年9月12日
	 */
	@Override
	protected List<? extends AbstractVO> getVOList(String tableName,String uniqueColName,
			Collection<String> uniqueValues,Long tenantid) {
		if(!"tenantpk".equals(uniqueColName)) {//不是租户id字段的，不处理
			return new ArrayList<>();
		}
		ITenantService tenantService = AppServiceHelper.findBean(ITenantService.class); 
		List<TenantVO> result = new ArrayList<>();
		TenantVO tenantVO = tenantService.getEnableTenantById(tenantid);
		result.add(tenantVO);
		return result;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.service.AbstractId2NameStrategy#getVO(java.lang.String, java.lang.Long, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年9月12日
	 */
	@Override
	protected AbstractVO getVO(String tableName,String uniqueColName,String uniqueValue,Long tenantid) {
		if(!"tenantpk".equals(uniqueColName)) {
			throw new HDRuntimeException("不支持的租户返名称");
		}
		Long uniqueLongValue = Long.parseLong(uniqueValue);
		if(!SyConstant.TENANT_HD.equals(tenantid) && !tenantid.equals(uniqueLongValue)) {
			throw new HDRuntimeException("不支持的租户权限");
		}
		ITenantService tenantService = AppServiceHelper.findBean(ITenantService.class); 
		return tenantService.getEnableTenantById(uniqueLongValue);
	}

	/**
	 * 租户这里，采用默认策略更优（因为没有批量的缓存...）
	 *
	 * @see com.hayden.hap.common.formmgr.service.AbstractId2NameStrategy#assignName4single(java.util.List, com.hayden.hap.common.formmgr.entity.Id2NameVO)
	 * @author zhangfeng
	 * @date 2016年9月13日
	 */
	@Override
	public void assignName4single(List<? extends AbstractVO> abstractVOs, Id2NameVO id2NameVO) {
		new DefaultId2NameStrategy().assignName4single(abstractVOs, id2NameVO);;
	}
}
