package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.formmgr.entity.InputConfigVO;

/**
 * 输入设定转换器
 * 
 * @author zhangfeng
 * @date 2016年8月30日
 */
public interface IInputConfigConverter {

	/**
	 * 将输入设定字符串转换为InputConfigVO
	 * @param inputConfig
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月30日
	 */
	public InputConfigVO conver(String inputConfig) throws HDException ;
}
