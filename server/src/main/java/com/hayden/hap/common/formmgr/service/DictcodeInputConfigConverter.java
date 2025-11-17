package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.formmgr.entity.InputConfigVO;
import com.hayden.hap.common.formmgr.itf.IInputConfigConverter;
import com.hayden.hap.common.serial.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 字典编码输入设定转换器
 * 
 * @author zhangfeng
 * @date 2016年8月30日
 */
public class DictcodeInputConfigConverter implements IInputConfigConverter{

	@Override
	public InputConfigVO conver(String inputConfig) throws HDException {
		if(StringUtils.isEmpty(inputConfig)) {
			throw new HDException("输入类型为字典编码，但没有配置输入设定");
		}
		
		InputConfigVO vo = new InputConfigVO();
		
		String[] inputArr = inputConfig.split(",");
		vo.setDictCode(inputArr[0]);
		
		if(inputArr.length>=2) {
			vo.setRecallFunc(inputArr[1]);
		}
		
		if(inputArr.length>=3) {
			vo.setExtWhere(inputArr[2]);
		}
		
		if(inputArr.length>=4) {
			String isLoadForMobileStr = inputArr[3];
			boolean isLoadForMobile = !"0".equals(isLoadForMobileStr);
			vo.setLoadForMobile(isLoadForMobile);
		}else {
			vo.setLoadForMobile(true);
		}
		
		if(inputArr.length>=5) {
			int start = StringUtils.ordinalIndexOf(inputConfig, ",", 4);
			String dataStr = StringUtils.substring(inputConfig, start+1);
			if(StringUtils.isNotEmpty(dataStr)) {
				List<DictDataWarperVO> list = JsonUtils.parseArray(dataStr, DictDataWarperVO.class);
				vo.setDictData(list);
			}
		}
		
		return vo;
	}

}
