package com.hayden.hap.common.excel.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.message.Message;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IExcelTemplateAction {

	/**
	 * 批量更新前处理
	 * @param excelTemplateVO
	 * @param VOlist 
	 * @date 2015年11月10日
	 */
	//List<Message> beforeMegerBatch(FormParamVO formParamVO,ExcelTemplateVO excelTemplateVO,List<? extends AbstractVO> VOlist) throws HDException;
	
	
	/**
	 * 批量更新后处理
	 * @param excelTemplateVO
	 * @param VOlist 
	 * @date 2015年11月10日
	 */
	//List<Message> afterMegerBatch(FormParamVO formParamVO,ExcelTemplateVO excelTemplateVO,List<? extends AbstractVO> VOlist) throws HDException;
	
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
	List<Message> beforeMegerBatch(FormParamVO formParamVO,ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap) throws HDException;
	
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
	List<Message> afterDbBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap,
			Map<String, List<AbstractVO>> funcToVoInsertListMap,
			Map<String, List<AbstractVO>> funcToVoAllListMap) throws HDException;
	
	/**
	 * 批量更新后处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList
	 * @param mainAndSubMap 主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月20日
	 */
	List<Message> afterMegerBatch(FormParamVO formParamVO,ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap) throws HDException;


	/**
	 * @Description: 导出导入模板时，导入模板列配置了查询选择，当需要添加过滤时，实现该方法。
	 * 不能在查询选择中添加where值
	 * @author: wangyi
	 * @date: 2018年8月14日
	 */
	QueryselectorInputConfigVO beforeQry4ImportItemQry(
			ExcelTemplateItemVO excelTemplateItemVO,
			QueryselectorInputConfigVO queryselectorInputConfigVO,
			List<? extends AbstractVO> exportVoList) throws HDException;
	
}
