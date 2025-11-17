package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.billcode.entity.BillCodeHeader;
import com.hayden.hap.common.billcode.entity.BillCodeInputConfigVO;
import com.hayden.hap.common.billcode.entity.BillCodeRow;
import com.hayden.hap.common.billcode.entity.BillcodeSegVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.serial.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理输入设定工具类
 * @author zhangfeng
 * @date 2016年8月30日
 */
public class InputConfigUtils {
	
	/**
	 * 获取字典的输入设定对象
	 * @param inputConfig
	 * @return
	 * @throws HDException
	 */
	public static DictInputConfigVO getDictInputConfigVO(String inputConfig) 
			throws HDException {
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		return JsonUtils.parse(inputConfig, DictInputConfigVO.class);
	}
	
	/**
	 * 获取查询选择的输入设定对象
	 * @param inputConfig
	 * @return
	 * @throws HDException
	 */
	public static QueryselectorInputConfigVO getQueryselectorInputConfigVO(String inputConfig) 
			throws HDException{
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		return JsonUtils.parse(inputConfig, QueryselectorInputConfigVO.class);
	}
	
	/**
	 * 获取自动编码的输入设定对象
	 * @param inputConfig
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月12日
	 */
	public static BillCodeInputConfigVO getBillCodeInputConfigVO(String inputConfig) {
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		String[] rows = inputConfig.split("~");
		String[] headers = rows[0].split(",");
		BillCodeHeader header = new BillCodeHeader();
		header.setPrefix(Integer.parseInt(headers[0]));
		header.setApplyPrefix(ObjectUtil.isTrue(Integer.parseInt(headers[1])));
		
		List<BillCodeRow> body = new ArrayList<>();
		for(int i=1; i<rows.length; i++) {
			String[] rowArr = rows[i].split(",");
			BillCodeRow row = new BillCodeRow();
			row.setType(rowArr[0]);
			row.setFitem(rowArr[1]);
			row.setConstFlag(rowArr[2]);
			row.setLength(rowArr[3]);
			row.setStart(rowArr[4]);
			row.setBase(rowArr[5]);
			row.setView(rowArr[6]);
			body.add(row);
		}
		
		BillCodeInputConfigVO inputConfigVO = new BillCodeInputConfigVO();
		inputConfigVO.setHeader(header);
		inputConfigVO.setBody(body);
		
		return inputConfigVO;
	}
	
	/**
	 * 获取日期的输入设定
	 * @param inputConfig
	 * @return
	 * @throws HDException
	 */
	public static DateInputConfigVO getDateInputConfigVO(String inputConfig) 
			throws HDException{
		if(StringUtils.isEmpty(inputConfig))
			return null;
		
		return JsonUtils.parse(inputConfig, DateInputConfigVO.class);
	}
	
	/**
	 * 获取自动编号输入设定的段VO集合
	 * @param inputConfig
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月6日
	 */
	public static List<BillcodeSegVO> getBillcodeSegVOList(String inputConfig) {
		if(StringUtils.isEmpty(inputConfig)) {
			return new ArrayList<>();
		}
		
		List<BillcodeSegVO> segList = new ArrayList<BillcodeSegVO>();
		String[] rows = inputConfig.split("~");
		
		for (int i = 0; i < rows.length; i++) {
			String[] fields = rows[i].split(",");
			BillcodeSegVO vo = new BillcodeSegVO();
			if(fields.length>0) {
				vo.setType(fields[0]);
			}			
			if(fields.length>1) {
				vo.setItem(fields[1]);
			}			
			if(fields.length>2) {
				vo.setConstant(fields[2]);
			}			
			if(fields.length>3){
				vo.setLength(fields[3]);
			}
			if(fields.length>4){
				vo.setStart(fields[4]);			
			}
			if(fields.length>5){
				vo.setBase(fields[5]);
			}
			if(fields.length>6){
				vo.setIsView(fields[6]);
			}
			segList.add(vo);
		}
		return segList;
	}
}
