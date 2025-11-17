package com.hayden.hap.common.excel.action;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.excel.itf.IExcelTemplateAction;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.message.Message;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelTemplateBaseAction implements IExcelTemplateAction{

	/**
	 * 批量更新前处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList 主表volist
	 * @param mainAndSubMap 主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月20日
	 */
	@Override
	public List<Message> beforeMegerBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 对excel转换的vo进行插入更新赋主键等处理后，但是再插入数据库之前做的一些业务操作
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList 处理前的主表list 
	 * @param mainAndSubMap 处理前的主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @param funcToVoInsertListMap  处理后的每个功能需要插入的list
	 * @param funcToVoAllListMap 处理后的每个功能需要处理的list，包括插入和更新
	 * @return 
	 * @author liyan
	 * @date 2018年5月23日
	 */
	@Override
	public List<Message> afterDbBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap,
			Map<String, List<AbstractVO>> funcToVoInsertListMap,
			Map<String, List<AbstractVO>> funcToVoAllListMap) throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 批量更新后处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList
	 * @param mainAndSubMap主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月20日
	 */
	@Override
	public List<Message> afterMegerBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap)
			throws HDException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryselectorInputConfigVO beforeQry4ImportItemQry(
			ExcelTemplateItemVO excelTemplateItemVO,
			QueryselectorInputConfigVO queryselectorInputConfigVO,
			List<? extends AbstractVO> exportVoList) throws HDException {
		return queryselectorInputConfigVO;
	}

}
