package com.hayden.hap.common.form.conver.mobile.typeconver;

import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.form.conver.mobile.DefaultMConverter;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月7日
 */
public class DecimalMConverter extends DefaultMConverter {

	@Override
	public String getDataType() {
		return DataTypeEnum.NUMBER.getCode();
	}
	
	@Override
	public Integer getLength() {
		return getColumnVO().getCollen();
	}
	
	@Override
	public String getValueRegexp() {
		return "^(\\-?)[0-9]+(\\.[0-9]{1,"+getColumnVO().getColscale()+"})?$";
	}
	
	@Override
	public String getValueRegexpMsg() {
		return "请输入最多"+getColumnVO().getColscale()+"位小数的数值";
	}
}
