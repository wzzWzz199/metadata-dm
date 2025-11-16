package com.hayden.hap.common.formmgr.query.strategy;

import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.org.entity.OrgVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VOCollectionUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


 
/**
 * 部门查询 当前部门以及所有子级
 * 
 * @author haocs
 * @date 2019年11月6日
 */
public class OrgIdAndChildrensStrategy implements IQueryStrategy {

	@Override
	public String getQueryWhere(String fitem_code, String value) {
		
		IBaseService baseService = (IBaseService) AppServiceHelper.findBean("baseService");
		
		String sb = "";
		if(StringUtils.isBlank(value)) {
			return sb;
		}
		Long tenantId = CurrentEnvUtils.getTenantId();
		OrgVO org = baseService.queryByPKAndTenantid(new OrgVO(), Long.parseLong(value), tenantId);
		
		String innercode = org.getInnercode();
		
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam(SyConstant.TENANT_STR, tenantId);
		sql.addWhereClause(" innercode like '"+innercode+"%'");
		List<OrgVO> voList = baseService.query(new OrgVO(), sql).getVoList();
		if(voList==null || voList.isEmpty()) {
			return sb;
		}
		List<Long> orgIds = VOCollectionUtils.getPropList(voList, SyConstant.ORGID, Long.class);
		if(orgIds==null || orgIds.isEmpty()) {
			return sb;
		}
		return DBSqlUtil.getConditionSql(fitem_code, orgIds,true);
	}

}
