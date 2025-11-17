package com.hayden.hap.common.form.conver.pc.codeconver;

import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.FitemTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.conver.pc.DefaultConverter;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormPCVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户id字段转换器
 * @author zhangfeng
 * @date 2017年3月6日
 */
public class TenantidConverter extends DefaultConverter {

	@Override
	public String getInputElement() {
		return ElementTypeEnum.HIDDEN.getCode();
	}
	
	@Override
	public String getDataType() {
		return DataTypeEnum.NUMBER.getCode();
	}
	
	/**
	 * 管理租户表单创建“租户id”、“租户名称”字段，否则一个都不创建
	 *
	 * @see com.hayden.hap.common.form.conver.pc.DefaultConverter#getItems()
	 * @author zhangfeng
	 * @date 2018年5月11日
	 */
	@Override
	public List<FormItemPCVO> getItems() {
		List<FormItemPCVO> tenantidItems = new ArrayList<>();
		
		FormPCVO formVO = super.getFormVO();
		boolean isNeedTenantName = ObjectUtil.isTrue(formVO.getIs_mgr_tenant_data());
		if(isNeedTenantName) {//如果是管理其它租户数据的表单，那么需要添加个租户名称的字段
			tenantidItems = super.getItems();
			
			TenantnameConverter nameConverter = new TenantnameConverter();
			nameConverter.setColumnVO(super.getColumnVO());
			nameConverter.setFormVO(super.getFormVO());
			List<FormItemPCVO> tenantnameItems = nameConverter.getItems();
			tenantidItems.addAll(tenantnameItems);
		}
		return tenantidItems;
	}
	
	private class TenantnameConverter extends DefaultConverter {
		
		@Override
		public String getCode() {
			return "tenant_name";
		}
		
		@Override
		public String getInputType() {
			return InputTypeEnum.QUERY_SELECT.getCode();
		}
		
		@Override
		public String getType() {
			return FitemTypeEnum.CUSTOM.getCode();
		}
		
		@Override
		public String getInputConfig() {
			return "{\"funccode\":\"SY_TENANT\",\"map\":{\"tenantpk\":\"tenantid\",\"tenantname\":\"tenant_name\"},\"isid2name\":true}";
		}
	}
}
