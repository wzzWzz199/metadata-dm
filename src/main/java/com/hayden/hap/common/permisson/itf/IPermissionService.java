package com.hayden.hap.common.permisson.itf;

import com.hayden.hap.common.permisson.entity.PermissionUrlVO;

import java.util.List;
import java.util.Map;

public interface IPermissionService {
	
	
	
	/**
	 * 
	 * @param tenantid
	 * @return 
	 * @author lianghua
	 * @date 2016年4月14日
	 */
	public Map<String,List<PermissionUrlVO>> getUrlFuncAndButton(Long tenantid);
}
