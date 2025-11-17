package com.hayden.hap.common.tenant.utils;

import com.hayden.hap.common.authc.util.SessionUtil;
import com.hayden.hap.common.common.exception.HDRuntimeException;
import com.hayden.hap.common.login.util.CookieUtil;
import com.hayden.hap.common.utils.RequestUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.thread.ThreadLocalUtils;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 租户工具类
 * 
 * @author lianghua
 * @date 2015年12月10日
 */

public class TenantUtil {
	/**
	 * 当前数据的租户id串（管理租户相关功能使用，作为线程变量的key）
	 */
	private static final String CURRENT_DATA_TENANT_STR = "CDTS";
	
	/**
	 * 当前数据所在租户的管理员用户id（管理租户相关功能使用，作为线程变量的key）
	 */
	private static final String CURRENT_DATA_USER_STR = "CDUS";
	
	/**
	 * 得到租户code，先从session中取值，如果取不到，从cookie中取
	 * @param request
	 * @return 
	 * @author lianghua
	 * @date 2015年12月10日
	 */
	public static String getTenantCode(HttpServletRequest request){
		String tenantCode = SessionUtil.getTenantCode();
		if(tenantCode==null){
			tenantCode = CookieUtil.getCookieValue(request, "tenantCode");
		}
		return tenantCode;
	}
	
	public static Long getTenantidFromRequest(HttpServletRequest request) {
		Map<String,String> headmap = RequestUtils.getHeadersInfo(request);
		String tidStr = headmap.get(SyConstant.TID);
		if(StringUtils.isEmpty(tidStr)) {
			tidStr = request.getParameter(SyConstant.TID);
		}
		
		if(StringUtils.isEmpty(tidStr))
			return null;
		
		return Long.parseLong(tidStr);
	}
	
	/**
	 * 获取当前数据的租户id
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月23日
	 */
	public static Long getCurrentDataTenantid(Long sessionTenantid) {
		Long currentDataTenantid = ThreadLocalUtils.getLong(CURRENT_DATA_TENANT_STR);
		if(currentDataTenantid==null)
			return sessionTenantid;
		//如果当前租户不是海顿，且数据租户和当前租户不一致，则认为是非法操作
		boolean illegalOpera = !SyConstant.TENANT_HD.equals(sessionTenantid) && !sessionTenantid.equals(currentDataTenantid);
		if(illegalOpera) {
			throw new HDRuntimeException("illegalOpera：st"+sessionTenantid+",dt"+currentDataTenantid);
		}
		return currentDataTenantid!=null?currentDataTenantid:sessionTenantid;
	}
	
	/**
	 * 设置当前数据的租户id
	 * @param currentDataTenantid 
	 * @author zhangfeng
	 * @date 2017年2月24日
	 */
	public static void setCurrentDataTenantid(Long currentDataTenantid) {
		ThreadLocalUtils.set(CURRENT_DATA_TENANT_STR, currentDataTenantid);
	}
	
	/**
	 * 获取当前数据所在租户的管理员用户id
	 * @param sessionUserid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月24日
	 */
	public static Long getCurrentDataUserid(Long sessionUserid) {
		Long currentDataUserid = ThreadLocalUtils.getLong(CURRENT_DATA_USER_STR);
		return currentDataUserid!=null?currentDataUserid:sessionUserid;
	}
	

	/**
	 * 设置当前数据所在租户的管理员用户id
	 * @param currentDataUserid 
	 * @author zhangfeng
	 * @date 2017年2月24日
	 */
	public static void setCurrentDataUserid(Long currentDataUserid) {
		ThreadLocalUtils.set(CURRENT_DATA_USER_STR, currentDataUserid);
	}
}
