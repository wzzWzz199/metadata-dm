package com.hayden.hap.common.func.itf;

import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.role.entity.RoleFuncVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月30日
 */
@IService("funcPCService")
public interface IFuncPCService extends IBaseFuncService {
	
	/**
	 * 根据功能编码查询购买了此功能的租户id集合
	 * @param funcCode
	 * @return 
	 * @author wushuangyang
	 * @date 2016年11月7日
	 */
	public List<Long> getTenantIdsByFuncCode(String funcCode);
	
	
	/**
	 * 得到所有启用状态的func
	 * @return 
	 * @author lianghua
	 * @date 2015年11月17日
	 */
	public List<? extends FuncVO> getEnableFuncByTenant(Long tenantid);
		
	/**
	 * 判断功能是否只读
	 * @param funcVO
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月11日
	 */
	public boolean isReadonlyFunc(FuncVO funcVO);
	
	
	/**
	 * 根据角色得到rolefunc
	 * @param roleid
	 * @param tenantid
	 * @return 
	 * @author lianghua
	 * @date 
	 */
	
	public List<RoleFuncVO> getRoleFuncList(Long roleid,Long tenantid);
	

	/**
	 * 得到功能对应的模块
	 * @param moduleCode
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author liyan
	 * @date 2017年8月14日
	 */
	public String getModuleCodeByFuncCode(String moduleCode, String funcCode,
			Long tenantid);

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
	public Map<String,String> getFunccode2OperaTablesMap(List<String> funccodes, Long tenantid);

	/**
	 * 根据功能编码集合查找对应的功能VO
	 * @param funcCodes 功能编码集合
	 * @param tenantid 租户ID
	 * @return 功能集合
	 * @author zhaishaofeng
	 * @date 2020年1月7日
	 */
	List<FuncPCVO> listFuncVOByCodeAndTenantId(Collection<String> funcCodes, Long tenantid);

}
