package com.hayden.hap.common.form.service;

import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.db.tableDef.itf.ITableInfoCallback;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.spring.service.AppServiceHelper;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月21日
 */
public class DefaultTableCallback implements ITableInfoCallback {
	
	private String tableName;
	
	
	public DefaultTableCallback(String tableName) {
		this.tableName = tableName;
	}
	
	public DefaultTableCallback(String funcCode,String formCode,Long tenantid) {
		if(tenantid==null) {
			throw new HDRuntimeException("租户id为空");
		}
		
		if(funcCode!=null) {
			IFormService formService = (IFormService)AppServiceHelper.findBean("formService");
			FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
			
			if(formVO==null) {				
				throw new HDRuntimeException("没有找到表单，根据功能编码："+funcCode);
			}
			tableName = formVO.getOpera_table_code();
			return;
		}
		
		if(formCode!=null) {
			IFormService formService = (IFormService)AppServiceHelper.findBean("formService");
			FormVO formVO = formService.getFormVOByFormcode(formCode, tenantid);
			
			if(formVO==null) {
				throw new HDRuntimeException("没有找到表单，根据表单编码："+formCode);
			}
			
			tableName = formVO.getOpera_table_code();
			return;
		}
		
		throw new HDRuntimeException("功能编码、表单编码至少得存在一个");
	}
	

	@Override
	public String getTableName() {		
		return tableName;
	}

}
