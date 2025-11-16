package com.hayden.hap.common.func.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Map;

/** 
 * @ClassName: IFuncService 
 * @Description: 
 * @author LUYANYING
 * @date 2015年6月30日 下午1:56:19 
 * @version V1.0   
 *  
 */
@IService("funcService")
public interface IFuncService {
	
	/**
	 * 移动端功能后缀
	 */
	public static final String MOBILE_SUFFIX = "_M";
	
	/**
	 * 按功能编码查询功能记录，此方法有缓存
	 * @param funccode 功能编码
	 * @return
	 * @return FuncVO 功能记录
	 * @throws
	 */
	public FuncVO queryByFunccode(String funccode,Long tenantid);
	
	
	/**
	 * 根据功能编码查找功能，并断言是否存在
	 * 如果不存在则抛出海顿运行时异常
	 * @param funccode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	public FuncVO queryAndAssertByFunccode(String funccode,Long tenantid);
	
	
	
	/**
	 * 根据功能编码查询购买了此功能的租户id集合
	 * @param funcCode
	 * @return 
	 * @author wushuangyang
	 * @date 2016年11月7日
	 */
	public List<Long> getTenantIdsByFuncCode(String funcCode);
	
	/**
	 * 根据功能名查表名（暂且）
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月26日
	 */
	public String getQueryTableNameOfFunc(String funcCode, Long tenantid);
	
	/**
	 * 根据功能名查操作表表名
	 * @param funcCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年10月30日
	 */
	public String getOperaTableNameOfFunc(String funcCode, Long tenantid);
	
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
	 * 获取绑定源pc功能编码,如果没有配置源pc功能，则返回自己
	 * @param funcCode
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年11月8日
	 */
	public String getSourcePCFunccode(String funcCode, Long tenantid);
	
	/**
	 * 获取对应移动端功能编码，如果没有配置移动端功能，则返回自己
	 * @param funcCode
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年11月8日
	 */
	public String getMobileFunccode(String funcCode, Long tenantid);


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
	 * 是否移动端功能
	 * @param funcVO
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	boolean isMFunc(FuncVO funcVO);
	
	/**
	 * 是否PC端功能
	 * @param funcVO
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	boolean isPCFunc(FuncVO funcVO);
	
	/**
	 * 是否移动端功能
	 * @param funcVO
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	boolean isMFunc(String funcCode);
	
	/**
	 * 是否PC端功能
	 * @param funcVO
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月25日
	 */
	boolean isPCFunc(String funcCode);
	
	/**
	 * 根据功能编码集合获取功能编码对应表编码map
	 * hse使用
	 * @param funccodes
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月2日
	 */
	public Map<String,String> getFunccode2OperaTablesMap(List<String> funccodes, Long tenantid);
}
