package com.hayden.hap.common.form.conver.mobile;

import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.TextAlignEnum;

/**
 * 
 * @author zhangfeng
 * @date 2018年5月11日
 */
public class NumberMConverter extends DefaultMConverter {

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
		return "^(\\-?)[0-9]{0,"+getColumnVO().getCollen()+"}+(\\.[0-9]{"+getColumnVO().getColscale()+"})?$";
	}
	
	@Override
	public String getValueRegexpMsg() {
		return "只能输入数字";
	}
	
	@Override
	public String getTextAlign() {
		return TextAlignEnum.RIGHT.getCode();
	}
}
