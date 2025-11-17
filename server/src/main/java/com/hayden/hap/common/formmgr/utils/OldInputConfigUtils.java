package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.serial.VOObjectMapper;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * 过期的输入设定工具类（为1.0升级2.0用）
 * @author zhangfeng
 * @date 2017年3月29日
 */
public class OldInputConfigUtils {

	public static DateInputConfigVO getDateInputConfigVO(String inputConfig) {
		if(StringUtils.isEmpty(inputConfig))
			return null;
	
		String[] arr = inputConfig.split(",");
		
		DateInputConfigVO dateInputConfigVO = new DateInputConfigVO();
		if("date".equalsIgnoreCase(arr[0])) {				
			dateInputConfigVO.setDatetype("YD");
		}else if("time".equalsIgnoreCase(arr[0])) {
			dateInputConfigVO.setDatetype("HS");
		}else if("month".equalsIgnoreCase(arr[0])) {
			dateInputConfigVO.setDatetype("YM");
		}
		
		if(arr.length>1) {
			dateInputConfigVO.setCallback(arr[1]);
		}
		
		if(arr.length>2) {
			dateInputConfigVO.setFormat(arr[2]);
		}
		return dateInputConfigVO;
	}
	
	public static DictInputConfigVO castDict(String inputConfig) throws HDException {
		//dictCode, formElements, selects, recallFun, sign ,extWhere ,isLoadForMobile,dictData
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		String[] arr = inputConfig.split(",");
		DictInputConfigVO vo = new DictInputConfigVO();
		vo.setDictcode(arr[0]);
		
		if(arr.length>2) {
			String[] formeles = arr[1].split("~");
			String[] selects = arr[2].split("~");
			Map<String,String> map = new HashMap<>();
			for(int i=0;i<selects.length;i++) {
				map.put(selects[i], formeles[i]);
			}
			vo.setMap(map);
		}
		
		if(arr.length>3) {
			vo.setCallback(arr[3]);
		}
		
		if(arr.length>4) {
			boolean sign = "multi".equals(arr[4]);
			vo.setIsmulti(sign);
		}
		
		if(arr.length>6) {
			boolean isLoadForMobile = "1".equals(arr[6]);
			vo.setIsload4m(isLoadForMobile);
		}
		
		if(arr.length>7) {
			String dictData = arr[7];
			List<DictDataWarperVO> dictDatas = JsonUtils.parseArray(dictData, DictDataWarperVO.class);
			vo.setDictdata(dictDatas);
		}
		
		return vo;
	}
	
	public static DictInputConfigVO castDictCode(String inputConfig) throws HDException {
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		//dictCode, recallFunc, extWhere, isLoadForMobile,dictData
		String[] arr = inputConfig.split(",");
		DictInputConfigVO vo = new DictInputConfigVO();
		vo.setDictcode(arr[0]);
		
		if(arr.length>1) {
			vo.setCallback(arr[1]);
		}
		
		if(arr.length>3) {
			boolean isLoadForMobile = "1".equals(arr[3]);
			vo.setIsload4m(isLoadForMobile);
		}
		
		if(arr.length>4) {
			String dictData = arr[4];
			List<DictDataWarperVO> dictDatas = JsonUtils.parseArray(dictData, DictDataWarperVO.class);
			vo.setDictdata(dictDatas);
		}
		
		return vo;
	}
	

	
	public static QueryselectorInputConfigVO castQuerySelector(String inputConfig) {
		//func_code, formElements, selects, recallFun, sign ,extWhere,isId2Name
		//func_code, formElements, selects, recallFun, sign ,extWhere,isId2Name
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		String[] arr = inputConfig.split(",");
		QueryselectorInputConfigVO vo = new QueryselectorInputConfigVO();
		vo.setFunccode(arr[0]);
		
		if(arr.length>2) {
			String[] formeles = arr[1].split("~");
			String[] selects = arr[2].split("~");
			Map<String,String> map = new IdentityHashMap<>();
			for(int i=0;i<selects.length;i++) {
				map.put(selects[i], formeles[i]);
			}
			vo.setMap(map);
		}
		
		if(arr.length>3) {
			vo.setCallback(arr[3]);
			
		}
		
		if(arr.length>4) {
			boolean sign = "multi".equals(arr[4]);
			vo.setIsmulti(sign);
		}
		
		if(arr.length>5) {
			vo.setWhere(arr[5]);
		}
		
		if(arr.length>6) {
			boolean isid2name = "1".equals(arr[6]);
			vo.setIsid2name(isid2name);
		}
		
		return vo;
	}
	
	
	public static void main(String[] args) throws IOException, HDException {
//		QueryselectorInputConfigVO vo = castQuerySelector("SY_USER,queryname2~query2,username~userid,,multi");
		DictInputConfigVO vo = castDict("module,modulecode~modulecode__name,code~name,,multi");
		VOObjectMapper mapper = new VOObjectMapper();
		
		String s = mapper.writeValueAsString(vo);
		System.err.println(s);
	
		DictInputConfigVO t = mapper.readValue(s,DictInputConfigVO.class);
		
		String s2 = mapper.writeValueAsString(t);
		System.err.println(s2);
	}
}
