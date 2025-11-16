package com.hayden.hap.common.func.service;

import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.form.entity.FormMVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormMService;
import com.hayden.hap.common.func.entity.FuncMVO;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncMService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.role.itf.IRoleService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VOCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月30日
 */
@Service("funcMService")
public class FuncMServiceImpl implements IFuncMService {

	@Autowired
	private IFormMService formMService;
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private ITableDefService tableDefService;

	@Override
	@Cacheable(value = CacheConstant.CACHE_FUNC_MOBILE, key = "#funccode.concat('|').concat(#tenantid)")
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public FuncMVO queryByFunccode(String funccode, Long tenantid) {

		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funccode);
		dynaSqlVO.addWhereParam("tenantid", tenantid);
		VOSet<FuncMVO> voSet = baseService.query(FuncMVO.class, dynaSqlVO);
		if (ObjectUtil.isNotEmpty(voSet.getVoList()))
			return voSet.getVO(0);
		return null;
	}

	@Override	
	public List<Long> getTenantIdsByFuncCode(String funcCode){
		List<Long> tenantIds=new ArrayList<Long>();
		DynaSqlVO dynaSqlVO=new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam("func_isbuy", SyConstant.SY_TRUE);

		List<String> sqlColumnList = new ArrayList<>();
		sqlColumnList.add(SyConstant.TENANT_STR);
		dynaSqlVO.setSqlColumnList(sqlColumnList);

		List<FuncMVO> funcList=this.baseService.query(FuncMVO.class, dynaSqlVO).getVoList();
		for(FuncVO func : funcList) {
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
		FormVO formVO = formMService.getFormVOByFunccode(funcCode, tenantid);
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
		FormVO formVO = formMService.getFormVOByFunccode(funcCode, tenantid);
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
	public List<FuncMVO> getEnableFuncByTenant(Long tenantid) {
		List<String> columList = new ArrayList<String>();
		StringBuffer whereStringBuffer = new StringBuffer("tenantid=");
		whereStringBuffer.append(tenantid);
		whereStringBuffer.append(" and func_isenable=1");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setSqlColumnList(columList);
		dynaSqlVO.setWhereClause(whereStringBuffer.toString());
		VOSet<FuncMVO> voset = baseService.query(FuncMVO.class, dynaSqlVO);
		List<FuncMVO> list = voset.getVoList();
		return list;
	}


	@Override
	public boolean isReadonlyFunc(FuncVO funcVO) {
		if (funcVO.getFunc_readonly() != null
				&& SyConstant.SY_TRUE == funcVO.getFunc_readonly())
			return true;

		// TODO 处理只读表达式
		return false;
	}


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

	@Override
	public boolean isMFunc(FuncVO funcVO) {
		if(funcVO instanceof FuncMVO)
			return true;
		return false;
	}

	@Override
	public boolean isPCFunc(FuncVO funcVO) {
		if(funcVO instanceof FuncPCVO)
			return true;
		return false;
	}

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
		List<FuncMVO> funcMVOs = baseService.query(FuncMVO.class, queryFunccodeDS).getVoList();
		if(ObjectUtil.isEmpty(funcMVOs))
			return new HashMap<>();

		Map<String,FuncMVO> code2FuncVOMap = VOCollectionUtils.groupedByProp(funcMVOs, "func_code", String.class);
		List<String> formcodes = VOCollectionUtils.getPropList(funcMVOs, "func_info", String.class);

		DynaSqlVO queryOperatableDS = new DynaSqlVO();
		List<String> formColumns = new ArrayList<>();
		formColumns.add("opera_table_code");
		formColumns.add("form_code");

		queryOperatableDS.setSqlColumnList(formColumns);
		queryOperatableDS.addWhereParam(SyConstant.TENANT_STR, tenantid);
		queryOperatableDS.addWhereParam("form_code", formcodes);
		List<FormMVO> formMVOs = baseService.query(FormMVO.class, queryOperatableDS).getVoList();
		if(ObjectUtil.isEmpty(formMVOs))
			return new HashMap<>();

		Map<String, FormMVO> code2FormVOMap = VOCollectionUtils.groupedByProp(formMVOs, "form_code", String.class);

		Map<String,String> result = new HashMap<>();
		for(Map.Entry<String,FuncMVO> entry : code2FuncVOMap.entrySet()) {
			FuncVO funcVO = entry.getValue();
			String form_code = funcVO.getFunc_info();
			FormMVO formMVO = code2FormVOMap.get(form_code);
			String tablecode = formMVO!=null?formMVO.getOpera_table_code():null;
			result.put(entry.getKey(), tablecode);
		}
		return result;
	}

	@Override
	public List<FuncMVO> listFuncVOByCodeAndTenantId(Collection<String> funcCodes, Long tenantid) {
		String conditionSql = DBSqlUtil.getConditionSql("func_code", funcCodes, true);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereClause(conditionSql);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		VOSet<FuncMVO> voSet = baseService.query(FuncMVO.class, dynaSqlVO);
		return voSet.getVoList();
	}
}
