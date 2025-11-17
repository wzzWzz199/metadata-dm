package com.hayden.hap.common.form.conver.mobile.typeconver;

import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.form.conver.mobile.DefaultMConverter;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月7日
 */
public class DatetimeMConverter extends DefaultMConverter {

	@Override
	public String getDataType() {
		return DataTypeEnum.DATE.getCode();
	}
	
	@Override
	public String getInputElement() {
		return ElementTypeEnum.DATE.getCode();
	}
}
