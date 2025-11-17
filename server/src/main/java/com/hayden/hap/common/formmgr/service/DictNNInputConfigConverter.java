package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.formmgr.entity.InputConfigVO;
import com.hayden.hap.common.formmgr.itf.IInputConfigConverter;
import com.hayden.hap.common.serial.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 字典无（有）名称输入设定转换器
 * @author zhangfeng
 * @date 2016年8月30日
 */
public class DictNNInputConfigConverter implements IInputConfigConverter {

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
			throw new HDException("输入类型为字典无（有）名称，但没有配置输入设定");
		}
		
		InputConfigVO vo = new InputConfigVO();
		
		String[] inputArr = inputConfig.split(",");
		vo.setDictCode(inputArr[0]);
		
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
			String isLoadForMobileStr = inputArr[6];
			boolean isLoadForMobile = "1".equals(isLoadForMobileStr);
			vo.setLoadForMobile(isLoadForMobile);
		}
		
		if(inputArr.length>=8) {
			int start = StringUtils.ordinalIndexOf(inputConfig, ",", 7);
			String dataStr = StringUtils.substring(inputConfig, start+1);
			if(StringUtils.isNotEmpty(dataStr)) {
				List<DictDataWarperVO> list = JsonUtils.parseArray(dataStr, DictDataWarperVO.class);
				vo.setDictData(list);
			}
		}
		
		return vo;
	}

}
