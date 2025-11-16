package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.formmgr.entity.ListDataVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.spring.service.IService;

/**
 * 只读状态计算服务
 * @author zhangfeng
 * @date 2017年4月24日
 */
@IService("readonlyService")
public interface IReadonlyService {

	/**
	 * 获取只读状态
	 * @param funcVO 功能VO
	 * @param businessVO 业务VO
	 * @param settedReadonlyStatus 已设置的只读状态
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月24日
	 */
	boolean getReadonlyStatus(FuncVO funcVO, AbstractVO businessVO, boolean settedReadonlyStatus) throws HDException;
	
	/**
	 * 获取只读状态
	 * @param funcVO
	 * @param businessVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月24日
	 */
	boolean getReadonlyStatus(FuncVO funcVO, AbstractVO businessVO) throws HDException;
	
	/**
	 * 包装只读状态
	 * @param funcVO
	 * @param voSet
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月24日
	 */
	ListDataVO wrapReadonlyStatus(FuncVO funcVO, VOSet<? extends AbstractVO> voSet, Long tenantid) throws HDException;
}
