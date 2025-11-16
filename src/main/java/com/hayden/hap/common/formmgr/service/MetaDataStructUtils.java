package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormPCVO;
import com.hayden.hap.common.formmgr.entity.MetaData;
import com.hayden.hap.common.func.entity.FuncPCVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.utils.SyConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年6月13日
 */
public class MetaDataStructUtils {

	/**
	 * 构造简易的结构数据
	 * @param columns 列定义，如：{{"code","编码"},{"name","名称"}}
	 * @param title 标题
	 * @return 
	 * @author zhangfeng
	 * @date 2017年6月13日
	 */
	public static MetaData constructMetaData(String[][] columns, String title) {
		List<FormItemPCVO> formItemVOs = new ArrayList<>();
		for(String[] column : columns) {
			FormItemPCVO itemVO = new FormItemPCVO();
			itemVO.setFitem_code(column[0]);
			itemVO.setFitem_name(column[1]);
			itemVO.setFitem_input_element(ElementTypeEnum.INPUT.getCode());
			itemVO.setFitem_input_type(InputTypeEnum.MANUAL.getCode());
			itemVO.setFitem_show_list(SyConstant.SY_TRUE);
			itemVO.setFitem_quick_list(SyConstant.SY_TRUE);
			formItemVOs.add(itemVO);
		}
		MetaData result = new MetaData();
		FuncVO funcVO = new FuncPCVO();
		funcVO.setFunc_type(FuncTypeEnum.FORM.getId());
		funcVO.setFunc_name(title);
		result.setFormItemVOs(formItemVOs);
		result.setFuncVO(funcVO);
		result.setFormVO(new FormPCVO());
		result.setQueryItemVOs(new ArrayList<FormItemPCVO>());
		return result;
	}
	
}
