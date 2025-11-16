package com.hayden.hap.common.form.conver.pc.typeconver;

import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.form.conver.pc.DefaultConverter;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月7日
 */
public class TextConverter extends DefaultConverter {

	@Override
	public String getInputElement() {
		return ElementTypeEnum.TEXTAREA.getCode();
	}
	
	@Override
	public Integer getCardColumn() {
		return 2;
	}
}
