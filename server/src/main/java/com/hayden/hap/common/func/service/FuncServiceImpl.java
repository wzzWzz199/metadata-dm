package com.hayden.hap.common.func.service;

import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.form.entity.FormPCVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.func.entity.FuncMVO;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IBaseFuncService;
import com.hayden.hap.common.func.itf.IFuncMService;
import com.hayden.hap.common.func.itf.IFuncPCService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.role.itf.IRoleService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VOCollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

/**
 * @ClassName: FuncServiceImpl
 * @Description:
 * @author LUYANYING
 * @date 2015年6月30日 下午1:57:09
 * @version V1.0
 * 
 */
@Service("funcService")
public class FuncServiceImpl implements IFuncService {
	
//	private static final Logger logger = LoggerFactory.getLogger(FuncServiceImpl.class);

	@Autowired
	private IFormService formService;
	
	@Autowired
	private IFuncMService funcMService;
	
	@Autowired
	private IFuncPCService funcPCService;
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	private static final Set<String> NOT_MOBILE_FUNC = new HashSet<>();
	static {
		NOT_MOBILE_FUNC.add("SY_PERMISSION_M");
		NOT_MOBILE_FUNC.add("MGR_PERMISSION_M");
	}

	@Override
	public FuncVO queryByFunccode(String funccode, Long tenantid) {
		IBaseFuncService service = getBaseFuncService(funccode);
		return service.queryByFunccode(funccode, tenantid);
	}


	

	@Override	
	public List<Long> getTenantIdsByFuncCode(String funcCode){
		IBaseFuncService service = getBaseFuncService(funcCode);		
		return service.getTenantIdsByFuncCode(funcCode);
	}

	/**
	 * 根据功能名查表名（暂且）
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月26日
	 */
	@Override
	public String getQueryTableNameOfFunc(String funcCode,Long tenantid) {
		IBaseFuncService service = getBaseFuncService(funcCode);
		return service.getQueryTableNameOfFunc(funcCode, tenantid);
	}

	/**
	 * 根据功能名查操作表表名
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月30日
	 */	
	@Override
	public String getOperaTableNameOfFunc(String funcCode,Long tenantid) {
		IBaseFuncService service = getBaseFuncService(funcCode);
		return service.getOperaTableNameOfFunc(funcCode, tenantid);
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


	@Override
	public boolean isReadonlyFunc(FuncVO funcVO) {
		if (funcVO.getFunc_readonly() != null
				&& SyConstant.SY_TRUE == funcVO.getFunc_readonly()) {
			return true;
		}
		// TODO 处理只读表达式
		return false;
	}

	

	/**
	 * 获取绑定源pc功能编码,如果没有配置源pc功能，则返回自己,否则返回最顶端的源功能
	 *
	 * @see com.hayden.hap.common.func.itf.IFuncService#getSourcePCFunccode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年11月8日
	 */
	@Override
	public String getSourcePCFunccode(String funcCode, Long tenantid) {
		FuncVO funcVO = getThisService().queryAndAssertByFunccode(funcCode, tenantid);
		
		String func_pc_code = funcVO.getFunc_pc_code();
		if(StringUtils.isNotEmpty(func_pc_code)){
			if(func_pc_code.equals(funcCode))//防止死循环
				return funcCode;
			return getSourcePCFunccode(func_pc_code, tenantid);
		}
		return funcCode;
	}

	/**
	 * 获取对应移动端功能编码，如果没有配置移动端功能，则返回自己
	 *
	 * @see com.hayden.hap.common.func.itf.IFuncService#getMobileFunccode(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2016年11月8日
	 */
	@Override
	public String getMobileFunccode(String funcCode, Long tenantid) {
		FuncVO funcVO = getThisService().queryByFunccode(funcCode, tenantid);
		if(StringUtils.isNotEmpty(funcVO.getFunc_mobile_code()))
			return funcVO.getFunc_mobile_code();

		return funcCode;
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

	@Override
	public boolean isMFunc(String funcCode) {
		if(NOT_MOBILE_FUNC.contains(funcCode))
			return false;
		
		if(funcCode.endsWith(MOBILE_SUFFIX))
			return true;
		
		return false;
	}

	@Override
	public boolean isPCFunc(String funcCode) {
		return !isMFunc(funcCode);
	}
	
	
	private IBaseFuncService getBaseFuncService(String funcCode) {
		if(isMFunc(funcCode))
			return funcMService;
		return funcPCService;
	}

	/**
	 * 根据功能编码集合获取功能编码对应表编码map
	 * hse使用
	 * @param funccodes
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月2日
	 */
	public Map<String,String> getFunccode2OperaTablesMap(List<String> funccodes, Long tenantid) {
		
		DynaSqlVO queryFunccodeDS = new DynaSqlVO();
		List<String> funcColumns = new ArrayList<>();
		funcColumns.add("func_info");
		funcColumns.add("func_code");
		
		queryFunccodeDS.setSqlColumnList(funcColumns);
		queryFunccodeDS.addWhereParam(SyConstant.TENANT_STR, tenantid);
		queryFunccodeDS.addWhereParam("func_type", FuncTypeEnum.FORM.getId());
		queryFunccodeDS.addWhereParam("func_code", funccodes);
		List<FuncVO> funcVOs = baseService.query(FuncVO.class, queryFunccodeDS).getVoList();
		if(ObjectUtil.isEmpty(funcVOs))
			return new HashMap<>();
		
		Map<String,FuncVO> code2FuncVOMap = VOCollectionUtils.groupedByProp(funcVOs, "func_code", String.class);
		List<String> formcodes = VOCollectionUtils.getPropList(funcVOs, "func_info", String.class);
		
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
		for(Entry<String,FuncVO> entry : code2FuncVOMap.entrySet()) {
			FuncVO funcVO = entry.getValue();
			String form_code = funcVO.getFunc_info();
			FormPCVO formVO = code2FormVOMap.get(form_code);
			String tablecode = formVO!=null?formVO.getOpera_table_code():null;
			result.put(entry.getKey(), tablecode);
		}
		return result;
	}
}
