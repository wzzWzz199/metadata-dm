package com.hayden.hap.common.utils.variable.impl;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.utils.ConfigUtils;
import com.hayden.hap.common.utils.variable.itf.IVarFunction;

import java.util.List;

/**
 * 系统参数变量函数<br/>
 * 格式：config(code,type)<br/>
 * 实例：导出附件最大数量  @f:config('sy_attachExportMaxNum','manager')@<br/>
 * 参数说明：<br/>
 * code:参数编码
 * type:manager/common;代表获取管理租户级别参数或者普通租户级别参数; 没有这个参数时候，默认为普通租户级别
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class ConfigVarFunction implements IVarFunction {

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.itf.IVarFunction#supportFunction()
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	public String supportFunction() {
		return "config";
	}

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.itf.IVarFunction#getValue(java.util.List)
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	public Object getValue(List<String> params) throws HDException {
		if(ObjectUtil.isEmpty(params))
			throw new HDException("参数函数的系统变量没有参数");
		
		String type = "common";
		if(params.size()>=2)
			type = params.get(1);
		
		if(!("common".equals(type) || "manager".equals(type)))
			throw new HDException("参数函数的类型不正确");
		
		String code = params.get(0);
		if("common".equals(type))
			return ConfigUtils.getValueOfCommon(code);
		
		return ConfigUtils.getValueOfAdmin(code);
	}

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.itf.IVarFunction#getStringValue(java.util.List)
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	public String getStringValue(List<String> params) throws HDException {
		if(ObjectUtil.isEmpty(params))
			throw new HDException("参数函数的系统变量没有参数");
		
		String type = "common";
		if(params.size()>=2)
			type = params.get(1);
		
		if(!("common".equals(type) || "manager".equals(type)))
			throw new HDException("参数函数的类型不正确");
		
		String code = params.get(0);
		if("common".equals(type))
			return ConfigUtils.getValueOfCommon(code,String.class);
		
		return ConfigUtils.getValueOfAdmin(code,String.class);
	}

}
