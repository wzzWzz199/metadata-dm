package com.hayden.hap.common.form.conver.mobile.typeconver;

import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.conver.mobile.DefaultMConverter;
import com.hayden.hap.common.form.conver.mobile.IMConverter;
import com.hayden.hap.common.form.conver.mobile.NumberMConverter;
import com.hayden.hap.common.form.entity.FormItemMVO;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月6日
 */
public class TinyintMConverter extends NumberMConverter {
	
	@Override
	public List<FormItemMVO> getItems() {
		if(getLength().equals(1)) {
			IMConverter yesNoConverter = new YesNoConverter();
			yesNoConverter.setColumnVO(super.getColumnVO());
			yesNoConverter.setFormVO(super.getFormVO());
			return yesNoConverter.getItems();
		}
		return super.getItems();
	}
	
	/**
	 * “是否”转换器
	 * 
	 * @author zhangfeng
	 * @date 2017年4月11日
	 */
	private class YesNoConverter extends DefaultMConverter {
		
		@Override
		public String getDataType() {
			return DataTypeEnum.NUMBER.getCode();
		}
		
		@Override
		public String getInputElement() {
			return ElementTypeEnum.RADIO.getCode();
		}
		
		@Override
		public String getInputType() {
			return InputTypeEnum.DICT_NEW.getCode();
		}
		
		@Override
		public String getInputConfig() {
			return "{\"dictcode\":\"sy_yesno\"}";
		}
	}
}
