package com.hayden.hap.common.form.conver.pc.typeconver;

import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.form.conver.pc.DefaultConverter;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月7日
 */
public class DatetimeConverter extends DefaultConverter {

	@Override
	public String getDataType() {
		return DataTypeEnum.DATE.getCode();
	}
	
	@Override
	public String getInputElement() {
		return ElementTypeEnum.DATE.getCode();
	}
}
