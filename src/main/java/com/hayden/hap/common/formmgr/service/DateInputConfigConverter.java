package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.formmgr.entity.InputConfigVO;
import com.hayden.hap.common.formmgr.itf.IInputConfigConverter;
import org.apache.commons.lang.StringUtils;

/**
 * 日期输入设定转换器
 * @author zhangfeng
 * @date 2016年8月30日
 */
public class DateInputConfigConverter implements IInputConfigConverter {

	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IInputConfigConverter#conver(java.lang.String)
	 * @author zhangfeng
	 * @date 2016年8月30日
	 */
	@Override
	public InputConfigVO conver(String inputConfig) {
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		String[] array = inputConfig.split(",");
		InputConfigVO configVO = new InputConfigVO();
		configVO.setDateType(array[0]);
		
		if(array.length>1)
			configVO.setRecallFunc(array[1]); 
		
		if(array.length>2)
			configVO.setDateFmt(array[2]);
		
		return configVO;
	}

}
