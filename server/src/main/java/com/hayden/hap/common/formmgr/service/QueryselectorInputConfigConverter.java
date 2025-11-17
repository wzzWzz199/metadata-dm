package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.formmgr.entity.InputConfigVO;
import com.hayden.hap.common.formmgr.itf.IInputConfigConverter;
import org.apache.commons.lang.StringUtils;

/**
 * 查询选择输入设定转换器
 * @author zhangfeng
 * @date 2016年8月30日
 */
public class QueryselectorInputConfigConverter implements IInputConfigConverter {

	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IInputConfigConverter#conver(java.lang.String)
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年8月30日
	 */
	@Override
	public InputConfigVO conver(String inputConfig) throws HDException {
		if(StringUtils.isEmpty(inputConfig)) {
			throw new HDException("输入类型为查询选择，但没有配置输入设定");
		}
		
		InputConfigVO vo = new InputConfigVO();
		
		String[] inputArr = inputConfig.split(",");
		vo.setFuncCode(inputArr[0]);
		
		if(inputArr.length>=2) {
			vo.setFormElements(inputArr[1].split("~"));
		}
		
		if(inputArr.length>=3) {
			vo.setSelects(inputArr[2].split("~"));
		}
		
		if(inputArr.length>=4) {
			vo.setRecallFunc(inputArr[3]);
		}
		
		if(inputArr.length>=5) {
			vo.setSign(inputArr[4]);
		}
		
		if(inputArr.length>=6) {
			vo.setExtWhere(inputArr[5]);
		}
		
		if(inputArr.length>=7) {
			String isId2NameStr = inputArr[6];
			boolean isId2Name = "1".equals(isId2NameStr);
			vo.setId2Name(isId2Name);
		}
		return vo;
	}

}
