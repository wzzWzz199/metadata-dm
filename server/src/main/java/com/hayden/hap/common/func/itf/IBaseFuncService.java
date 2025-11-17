package com.hayden.hap.common.func.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.func.entity.FuncVO;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月31日
 */
public interface IBaseFuncService {

	/**
	 * 按功能编码查询功能记录，此方法有缓存
	 * @param funccode 功能编码
	 * @return
	 * @return FuncVO 功能记录
	 * @throws
	 */
	public FuncVO queryByFunccode(String funccode, Long tenantid);
	
	
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
	public FuncVO queryAndAssertByFunccode(String funccode, Long tenantid);
	
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

}
