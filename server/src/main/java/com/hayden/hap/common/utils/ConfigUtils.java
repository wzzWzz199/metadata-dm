package com.hayden.hap.common.utils;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.config.itf.IConfigService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 参数util
 * @author zhangfeng
 * @date 2016年1月19日
 */
public class ConfigUtils {

	/**
	 * 根据参数编码获取普通租户级别参数值
	 * @param code
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月19日
	 */
	public static String getValueOfCommon(String code) {
		if(code==null) return null;
		
		Long tenantid = CurrentEnvUtils.getTenantId();
		IConfigService configService = AppServiceHelper.findBean(IConfigService.class);
		String valueStr = configService.getValueByCode(code, tenantid);
		if(StringUtils.isNotEmpty(valueStr) && VariableUtils.hasSysParam(valueStr)) {
			try {
				valueStr = VariableUtils.replaceSystemParam(valueStr);
			} catch (HDException e) {
				throw new HDRuntimeException(e);
			}
		}
		return valueStr;
	}
	
	/**
	 * 根据参数编码获取普通租户级别参数值
	 * @param code
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月19日
	 */
	public static String getValueOfCommon(String code, Long tenantid) {
		if(code==null) return null;
		
		IConfigService configService = AppServiceHelper.findBean(IConfigService.class);
		String valueStr = configService.getValueByCode(code, tenantid);
		if(StringUtils.isNotEmpty(valueStr) && VariableUtils.hasSysParam(valueStr)) {
			try {
				valueStr = VariableUtils.replaceSystemParam(valueStr);
			} catch (HDException e) {
				throw new HDRuntimeException(e);
			}
		}
		return valueStr;
	}
	
	/**
	 * 根据参数编码获取普通租户级别参数值
	 * @param code
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月19日
	 */
	public static <T> T getValueOfCommon(String code,Class<T> clazz) {
		if(code==null) return null;
		
		Long tenantid = CurrentEnvUtils.getTenantId();
		return getValue(code, tenantid, clazz);
	}
	
	/**
	 * 根据参数编码获取普通租户级别参数值
	 * @param code
	 * @return 
	 * @author zhangfeng
	 * @date 2016年1月19日
	 */
	public static <T> T getValueOfCommon(String code,Class<T> clazz, Long tenantid) {
		if(code==null) return null;
		
		return getValue(code, tenantid, clazz);
	}
	
	/**
	 * 根据参数编码获取管理级别参数值
	 * @param code
	 * @return 
	 * @author zhangfeng
	 * @date 2016年6月24日
	 */
	public static String getValueOfAdmin(String code) {
		if(code==null) return null;
		
		Long tenantid = SyConstant.TENANT_HD;
		IConfigService configService = AppServiceHelper.findBean(IConfigService.class);
		String valueStr = configService.getValueByCode(code, tenantid);
		if(StringUtils.isNotEmpty(valueStr) && VariableUtils.hasSysParam(valueStr)) {
			try {
				valueStr = VariableUtils.replaceSystemParam(valueStr);
			} catch (HDException e) {
				throw new HDRuntimeException(e);
			}
		}
		return valueStr;
	}
	
	/**
	 * 根据参数编码获取管理级别参数值
	 * @param code
	 * @return 
	 * @author zhangfeng
	 * @date 2016年6月24日
	 */
	public static <T> T getValueOfAdmin(String code,Class<T> clazz) {
		if(code==null) return null;
		
		Long tenantid = SyConstant.TENANT_HD;
		return getValue(code, tenantid, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(String code, Long tenantid, Class<T> clazz) {
		IConfigService configService = AppServiceHelper.findBean(IConfigService.class);
		String valueStr = configService.getValueByCode(code, tenantid);
		if(StringUtils.isEmpty(valueStr))
			return null;
		
		if(VariableUtils.hasSysParam(valueStr)) {
			try {
				valueStr = VariableUtils.replaceSystemParam(valueStr);
			} catch (HDException e) {
				throw new HDRuntimeException(e);
			}
		}
		
		Object obj = null;
		if(String.class.equals(clazz)) {
			obj = valueStr;
		}else if(Long.class.equals(clazz)) {
			obj = Long.parseLong(valueStr);
		}else if(Integer.class.equals(clazz)) {
			obj = Integer.parseInt(valueStr);
		}else if(Double.class.equals(clazz)) {
			obj = Double.parseDouble(valueStr);
		}else if(Float.class.equals(clazz)) {
			obj = Float.parseFloat(valueStr);
		}else if(Boolean.class.equals(clazz)) {
			if("1".equals(valueStr) || "y".equalsIgnoreCase(valueStr) 
					|| "true".equalsIgnoreCase(valueStr) || "yes".equalsIgnoreCase(valueStr)) 
				obj = true;
			else 
				obj = false;
		}else {
			throw new HDRuntimeException("不支持的参数值类型:"+clazz.getSimpleName());
		}
		return (T)obj;
	}
}
