package com.hayden.hap.common.func.service;

import com.hayden.hap.common.common.entity.CommonVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.form.entity.FormPCVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncPCService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.role.entity.RoleFuncVO;
import com.hayden.hap.common.role.itf.IRoleService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VOCollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月30日
 */
@Service("funcPCService")
public class FuncPCServiceImpl implements IFuncPCService {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(FuncServiceImpl.class);

	@Autowired
	private IFormService formService;
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private ITableDefService tableDefService;

	@Override
	@Cacheable(value = "SY_FUNC", key = "#funccode.concat('|').concat(#tenantid)")
	public FuncVO queryByFunccode(String funccode, Long tenantid) {

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funccode);
		dynaSqlVO.addWhereParam("tenantid", tenantid);
		VOSet<FuncPCVO> voSet = baseService.query(FuncPCVO.class, dynaSqlVO);
		if (ObjectUtil.isNotEmpty(voSet.getVoList()))
			return voSet.getVO(0);
		return null;
	}

//	@Override
//	public List<FuncPCVO> getFuncListByUserId(Long userId, Long tenantId) {
//		List<UserRoleVO> userRoleList = roleService.getRolesByUserId(userId,
//				tenantId);
//		StringBuilder sql = new StringBuilder();
//		if (userRoleList == null || userRoleList.isEmpty()) {
//			return null;
//		}
//		for (UserRoleVO userRole : userRoleList) {
//			sql.append(
//					"select distinct func_code from sy_role_func where roleid=")
//					.append(userRole.getRoleid()).append(" and tenantid=")
//					.append(tenantId).append(" union all ");
//		}
//		sql.delete(sql.lastIndexOf(" union all "), sql.length());
//		List<AbstractVO> voList = this.baseService.query("sy_role_func",
//				new DynaSqlVO()).getVoList();
//		Set<String> funcCodeSet = new HashSet<String>();
//		for (AbstractVO vo : voList) {
//			funcCodeSet.add(vo.getString("func_code"));
//		}
//		DynaSqlVO dynaSqlVO = new DynaSqlVO();
//		dynaSqlVO.addWhereParam("func_code", funcCodeSet);
//		dynaSqlVO.addWhereParam("tenantid", tenantId);
//		return this.baseService.query(FuncPCVO.class, dynaSqlVO).getVoList();
//
//	}

	@Override	
	public List<Long> getTenantIdsByFuncCode(String funcCode){
		List<Long> tenantIds=new ArrayList<Long>();
		DynaSqlVO dynaSqlVO=new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam("func_isbuy", SyConstant.SY_TRUE);

		List<String> sqlColumnList = new ArrayList<>();
		sqlColumnList.add(SyConstant.TENANT_STR);
		dynaSqlVO.setSqlColumnList(sqlColumnList);

		List<FuncPCVO> funcList=this.baseService.query(FuncPCVO.class, dynaSqlVO).getVoList();
		for(FuncVO func : funcList)
		{
			tenantIds.add(func.getTenantid());
		}
		tenantIds.remove(SyConstant.TENANT_HD);
		return tenantIds;
	}

	/**
	 * 根据功能名查表名（暂且）
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月26日
	 */
	public String getQueryTableNameOfFunc(String funcCode,Long tenantid) {
		FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(formVO,
				"No form has been defined for the function[" + funcCode + "].");
		String tableName = formVO.getQuery_table_code();
		ObjectUtil.validNotNull(tableName, "Can't find table of the function["
				+ funcCode + "].");
		return tableName;
	}

	/**
	 * 根据功能名查操作表表名
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月30日
	 */	
	public String getOperaTableNameOfFunc(String funcCode,Long tenantid) {
		FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(formVO,
				"No form has been defined for the function[" + funcCode + "].");
		String tableName = formVO.getOpera_table_code();
		ObjectUtil.validNotNull(tableName, "Can't find table of the function["
				+ funcCode + "].");
		return tableName;
	}

	/**
	 * 
	 * 
	 * @see com.hayden.hap.common.func.itf.IFuncService#getEnableFuncByTenant()
	 * @author lianghua
	 * @date 2015年11月17日
	 */
	@Override
	public List<? extends FuncVO> getEnableFuncByTenant(Long tenantid) {
		FuncVO funcvo = new FuncPCVO();
		List<String> columList = new ArrayList<String>();
		StringBuffer whereStringBuffer = new StringBuffer("tenantid=");
		whereStringBuffer.append(tenantid);
		whereStringBuffer.append(" and func_isenable=1");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setSqlColumnList(columList);
		dynaSqlVO.setWhereClause(whereStringBuffer.toString());
		VOSet<FuncVO> voset = baseService.query(funcvo, dynaSqlVO);
		List<FuncVO> list = voset.getVoList();
		return list;
	}

	/**
	 * 
	 *
	 * @see com.hayden.hap.common.func.itf.IFuncService#getRoleFuncList(java.lang.Long)
	 * @author lianghua
	 * @date 2015年12月9日
	 */
	@Cacheable(value = "roleAndFuncCache", key = "#tenantid+'_'+#roleid")
	@Override
	public List<RoleFuncVO> getRoleFuncList(Long roleid, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("SELECT rf.* FROM ");
		sqlSb.append("	(SELECT f.* FROM sy_role_func f ");
		sqlSb.append("		inner join (select * from sy_role sr where sr.isenable=1 and sr.tenantid={0,number,#}) r on f.roleid=r.roleid  ");
		sqlSb.append("   WHERE f.tenantid={0,number,#} AND f.roleid={1,number,#}");
		sqlSb.append("	) rf ");
		sqlSb.append("	INNER JOIN (SELECT * FROM sy_func WHERE tenantid={0,number,#} AND func_isbuy=1 AND func_isenable=1) fun ");
		sqlSb.append("	ON fun.func_code=rf.func_code AND fun.tenantid=rf.tenantid ");

		String sql = MessageFormat.format(sqlSb.toString(), tenantid, roleid);
		
		VOSet<CommonVO> voset = baseService.executeQuery(CommonVO.class,
				sql.toString(), dynaSqlVO, new Object[] {}, new int[] {},
				"sy_role_func");
		List<CommonVO> list = voset.getVoList();
		List<RoleFuncVO> roleFuncList = new ArrayList<RoleFuncVO>();
		try {
			for (CommonVO abvo : list) {
				RoleFuncVO roleFunc = new RoleFuncVO();
//				BeanUtils.copyProperties(roleFunc, abvo);
//				BeanUtils.copyProperties(roleFunc, abvo.getColumnValues());
				BeanUtils.copyProperties(abvo, roleFunc);
				BeanUtils.copyProperties(abvo.getColumnValues(), roleFunc);
				roleFuncList.add(roleFunc);
			}
		} catch (Exception e) {
			logger.error("复制属性值到对象出错", e);
		}
		return roleFuncList;
	}


	@Override
	public boolean isReadonlyFunc(FuncVO funcVO) {
		if (funcVO.getFunc_readonly() != null
				&& SyConstant.SY_TRUE == funcVO.getFunc_readonly())
			return true;

		// TODO 处理只读表达式
		return false;
	}

//	/**
//	 * 
//	 *
//	 * @see com.hayden.hap.common.func.itf.IFuncService#getNoValidateFunc(java.lang.Long)
//	 * @author lianghua
//	 * @date 2016年4月13日
//	 */
//	@Cacheable(value = "noValidateFuncCache", key = "#tenantid")
//	@Override
//	public List<NoValidateFuncVO> getNoValidateFunc(Long tenantid) {
//		DynaSqlVO dynaSqlVO = new DynaSqlVO();
//		StringBuffer sb = new StringBuffer("func_isenable=1");
//		sb.append(" and (func_acl_flag=0 or func_action_flag=0)");
//		sb.append(" and tenantid=");
//		sb.append(tenantid);
//		sb.append(" and func_isbuy=1");
//		List<String> sqlColumnList = new ArrayList<String>();
//		sqlColumnList.add("modulecode");
//		sqlColumnList.add("func_code");
//		sqlColumnList.add("func_acl_flag");
//		sqlColumnList.add("func_action_flag");
//		dynaSqlVO.setWhereClause(sb.toString());
//		dynaSqlVO.setSqlColumnList(sqlColumnList);
//		VOSet<NoValidateFuncVO> voset = baseService.query(
//				new NoValidateFuncVO(), dynaSqlVO);
//		List<NoValidateFuncVO> list = null;
//		if (voset != null) {
//			list = voset.getVoList();
//		}
//		return list;
//	}


	private IFuncService getThisService() {
		IFuncService funcService = AppServiceHelper.findBean(IFuncService.class);
		return funcService;
	}

	
	@Override
	public String getModuleCodeByFuncCode(String moduleCode, String funcCode, Long tenantid){
		FuncVO funcVO = getThisService().queryByFunccode(funcCode, tenantid);
		if(null != funcVO){
			return funcVO.getModulecode();
		}
		return moduleCode;	
	}
	
//	@Override
//	public Boolean hasFuncPermission(String funcCode, Long tenantid, Long userid){
//		//如果不存在不较验func权限的功能，那么首先较验func权限
//		List<UserRoleVO> userRoleList = roleService.getRolesByUserId(userid, tenantid);
//		List<RoleFuncVO> rolefunclist = new ArrayList<RoleFuncVO>();
//		for(UserRoleVO userRoleVO:userRoleList){
//			rolefunclist.addAll(getThisService().getRoleFuncList(userRoleVO.getRoleid(), tenantid));
//		}
//		for(RoleFuncVO rolefuncvo:rolefunclist){
//			if(funcCode!=null && funcCode.equals(rolefuncvo.getFunc_code())){
//				return true;
//			}
//		}
//		return false;
//	}

	@Override
	public FuncVO queryAndAssertByFunccode(String funccode, Long tenantid) {
		FuncVO funcVO = queryByFunccode(funccode, tenantid);
		ObjectUtil.validNotNull(funcVO, "根据功能编码:"+funccode+"没有找到对应功能"+"|"+tenantid);
		return funcVO;
	}

	/**
	 * 根据功能编码集合获取功能编码对应表编码map
	 * hse使用
	 * @param funccodes
	 * @param tenantid
	 * @return
	 * @author zhangfeng
	 * @date 2018年2月2日
	 * @author zhangshaofeng
	 * @date 2020年1月3日
	 */
	@Override
	public Map<String,String> getFunccode2OperaTablesMap(List<String> funccodes, Long tenantid) {

		DynaSqlVO queryFunccodeDS = new DynaSqlVO();
		List<String> funcColumns = new ArrayList<>();
		funcColumns.add("func_info");
		funcColumns.add("func_code");

		queryFunccodeDS.setSqlColumnList(funcColumns);
		queryFunccodeDS.addWhereParam(SyConstant.TENANT_STR, tenantid);
		queryFunccodeDS.addWhereParam("func_type", FuncTypeEnum.FORM.getId());
		queryFunccodeDS.addWhereParam("func_code", funccodes);
		List<FuncPCVO> funcPCVOs = baseService.query(FuncPCVO.class, queryFunccodeDS).getVoList();
		if(ObjectUtil.isEmpty(funcPCVOs))
			return new HashMap<>();

		Map<String,FuncPCVO> code2FuncVOMap = VOCollectionUtils.groupedByProp(funcPCVOs, "func_code", String.class);
		List<String> formcodes = VOCollectionUtils.getPropList(funcPCVOs, "func_info", String.class);

		DynaSqlVO queryOperatableDS = new DynaSqlVO();
		List<String> formColumns = new ArrayList<>();
		formColumns.add("opera_table_code");
		formColumns.add("form_code");

		queryOperatableDS.setSqlColumnList(formColumns);
		queryOperatableDS.addWhereParam(SyConstant.TENANT_STR, tenantid);
		queryOperatableDS.addWhereParam("form_code", formcodes);
		List<FormPCVO> formVOs = baseService.query(FormPCVO.class, queryOperatableDS).getVoList();
		if(ObjectUtil.isEmpty(formVOs))
			return new HashMap<>();

		Map<String,FormPCVO> code2FormVOMap = VOCollectionUtils.groupedByProp(formVOs, "form_code", String.class);

		Map<String,String> result = new HashMap<>();
		for(Map.Entry<String,FuncPCVO> entry : code2FuncVOMap.entrySet()) {
			FuncVO funcVO = entry.getValue();
			String form_code = funcVO.getFunc_info();
			FormPCVO formVO = code2FormVOMap.get(form_code);
			String tablecode = formVO!=null?formVO.getOpera_table_code():null;
			result.put(entry.getKey(), tablecode);
		}
		return result;
	}

	@Override
	public List<FuncPCVO> listFuncVOByCodeAndTenantId(Collection<String> funcCodes, Long tenantid) {
		String conditionSql = DBSqlUtil.getConditionSql("func_code", funcCodes, true);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereClause(conditionSql);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		VOSet<FuncPCVO> voSet = baseService.query(FuncPCVO.class, dynaSqlVO);
		return voSet.getVoList();
	}
}
