package com.hayden.hap.common.form.dao;

import com.hayden.hap.common.common.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

/** 
 * @ClassName: FormDaoImpl 
 * @Description: 
 * @author LUYANYING
 * @date 2015年7月1日 下午3:14:04 
 * @version V1.0   
 *  
 */
@Repository("formDao")
public class FormDaoImpl extends BaseDaoImpl {
//	@Deprecated
//	/**
//	 * 被formService.getFormVOByFunccode取代
//	 * @Title: queryByFunccode 
//	 * @Description: 按功能编码查询表单记录
//	 * @param funccode 功能编码
//	 * @return
//	 * @return FormVO 表单记录
//	 * @throws
//	 */
//	public FormPCVO queryByFunccode(String funccode,Long tenantId) {
//		StringBuffer sb = new StringBuffer();
//		sb.append("select t1.* from sy_form t1");
//		sb.append(" where exists(select 1 from sy_func t2 where t1.form_code = t2.func_info");
//		sb.append(" and t2.func_type='" + "form" + "' and t2.func_code='" + funccode + "' ");
//		
//		//非海顿的需要对租户进行过滤
//		if(!SyConstant.TENANT_HD.equals(tenantId)) {
//			sb.append(" and t1.TENANTID = ");
//			sb.append(tenantId);
//			sb.append(" and t2.TENANTID = ");
//			sb.append(tenantId);
//		}
//		sb.append(")");
//		
//		VOSet<FormPCVO> voSet = this.executeQuery(FormPCVO.class, sb.toString(), null);
//		if(voSet != null)
//			return voSet.getVO(0);
//		return null;
//	}
	
}
