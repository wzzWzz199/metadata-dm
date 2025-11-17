package com.hayden.hap.common.formmgr.query.strategy;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.role.entity.UserRoleVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


 
/**
 * 部门查询 当前部门以及所有子级
 * 
 * @author haocs
 * @date 2019年11月6日
 */
public class Role2UsersStrategy implements IQueryStrategy {

	@Override
	public String getQueryWhere(String fitem_code, String value) {
		
		IBaseService baseService = (IBaseService) AppServiceHelper.findBean("baseService");
		
		String sb = "";
		if(StringUtils.isBlank(value)) {
			return sb;
		}
		Long tenantId = CurrentEnvUtils.getTenantId();
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam(fitem_code, value);
		sql.addWhereParam(SyConstant.TENANT_STR, tenantId);
		VOSet<UserRoleVO> roleUsers = baseService.query(UserRoleVO.class, sql);
		if( roleUsers.getVoList()==null ||  roleUsers.getVoList().isEmpty()) {
			return sb;
		}
		List<Long> userids = new ArrayList<Long>();
		for (UserRoleVO userRole : roleUsers.getVoList()) {
			userids.add(userRole.getUserid());
		}
		if(userids==null || userids.isEmpty()) {
			return sb;
		}
		return DBSqlUtil.getConditionSql("userid", userids,true);
	}

}
