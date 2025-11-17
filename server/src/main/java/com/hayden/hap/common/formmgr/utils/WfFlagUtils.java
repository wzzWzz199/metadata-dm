package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 判断是否支持审批流
 * @author 李艳
 * @date 2018年2月20日
 */
public class WfFlagUtils {
	
	private static String[] wfItems = {"wf_current_user", "wf_audit_state", "wf_create_user", "wf_instance",
			"wf_type", "wf_current_nodeid", "wf_audit_time"};

	/**
	 * 是否支持审批流
	 * @param formVO
	 * @return 
	 * @author liyan
	 * @date 2018年5月22日
	 */
	public static Boolean isSupportWf(FormVO formVO) {
		
		String tableName = formVO.getOpera_table_code();
		if(StringUtils.isEmpty(tableName)){
			return false;
		}
		Boolean supportWf = true;
		ITableDefService tableDefService = AppServiceHelper.findBean(ITableDefService.class);
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		if(null == tableDefVO){
			return false;
		}
		HashMap<String, Boolean> colcode2Has = new HashMap<>();
		for(TableColumnVO columnVO : tableDefVO.getColumnList()) {
			colcode2Has.put(columnVO.getColcode(), true);
		}
		for(String item: wfItems){
			if(null == colcode2Has.get(item)){
				supportWf = false;
				break;
			}
		}
		return supportWf;
	}
}
