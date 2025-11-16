package com.hayden.hap.common.form.conver.mobile.typeconver;

import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.form.conver.mobile.DefaultMConverter;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月7日
 */
public class TextMConverter extends DefaultMConverter {

	@Override
	public String getInputElement() {
		return ElementTypeEnum.TEXTAREA.getCode();
	}
	
	@Override
	public Integer getCardColumn() {
		return 2;
	}
}
