package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.org.entity.OrgVO;
import com.hayden.hap.common.org.itf.IOrgService;
import com.hayden.hap.common.spring.service.AppServiceHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年9月12日
 */
public class OrgId2NameStrategy extends AbstractId2NameStrategy {

	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.service.AbstractId2NameStrategy#getVOList(java.lang.String, java.util.Collection, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年9月12日
	 */
	@Override
	protected List<? extends AbstractVO> getVOList(String tableName,String uniqueColName,
			Collection<String> uniqueValues,Long tenantid) {
		IOrgService orgService = AppServiceHelper.findBean(IOrgService.class);
		List<OrgVO> orgList = orgService.findOrgs(tenantid);
		
		List<OrgVO> result = new ArrayList<>();
		for(OrgVO orgVO : orgList) {
			if(uniqueValues.contains(orgVO.getString(uniqueColName))) {
				result.add(orgVO);
			}
		}
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
		IOrgService orgService = AppServiceHelper.findBean(IOrgService.class);
		List<OrgVO> orgList = orgService.findOrgs(tenantid);
		
		for(OrgVO orgVO : orgList) {
			if(uniqueValue.equals(orgVO.getString(uniqueColName))) {
				return orgVO;
			}
		}
		return null;
	}

}
