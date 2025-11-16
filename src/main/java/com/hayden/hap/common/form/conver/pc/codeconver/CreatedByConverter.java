package com.hayden.hap.common.form.conver.pc.codeconver;

import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.FitemTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.conver.pc.DefaultConverter;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.utils.SyConstant;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月6日
 */
public class CreatedByConverter extends DefaultConverter {

	@Override
	public String getInputElement() {
		return ElementTypeEnum.HIDDEN.getCode();
	}
	
	@Override
	public String getDataType() {
		return DataTypeEnum.NUMBER.getCode();
	}
	
	@Override
	public List<FormItemPCVO> getItems() {
		List<FormItemPCVO> tenantidItems = super.getItems();
		
		CreatedByNameConverter createdByNameConverter = new CreatedByNameConverter();
		createdByNameConverter.setColumnVO(super.getColumnVO());
		createdByNameConverter.setFormVO(super.getFormVO());
		List<FormItemPCVO> createdByNameItems = createdByNameConverter.getItems();
		tenantidItems.addAll(createdByNameItems);
		return tenantidItems;
	}
	
	private class CreatedByNameConverter extends DefaultConverter {
		@Override
		public String getCode() {
			return "created_by_name";
		}
		
		@Override
		public String getInputType() {
			return InputTypeEnum.QUERY_SELECT.getCode();
		}
		
		@Override
		public Integer getReadonly() {
			return SyConstant.SY_TRUE;
		}
		
		@Override
		public String getType() {
			return FitemTypeEnum.CUSTOM.getCode();
		}
		
		@Override
		public String getInputConfig() {
			return "{\"funccode\":\"SY_USER\",\"map\":{\"userid\":\"created_by\",\"username\":\"created_by_name\"},\"isid2name\":true}";
		}
	}
}
