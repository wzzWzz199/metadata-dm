package com.hayden.hap.common.utils.template;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.db.util.ResourceUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.ExportTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.utils.ListUtil;
import com.hayden.hap.common.utils.SyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板utils
 * 
 * @author liyan
 * @date 2017年7月7日
 */
public class TemplateUtils {

	private static final Logger logger = LoggerFactory.getLogger(TemplateUtils.class);
	/**
	 * 获得全部字典数据
	 * @param dictDataVoList
	 * @return 
	 * @author liyan
	 * @date 2017年7月7日
	 */
	public static List<DictDataWarperVO> getAllDataList(List<DictDataWarperVO> dictDataVoList) {
		List<DictDataWarperVO> allDataList = new ArrayList<>();
		for(int i=0;i<dictDataVoList.size();i++ ){
			DictDataWarperVO dictDataVo = dictDataVoList.get(i);
			List<DictDataWarperVO> childrenList = dictDataVo.getChildren();
			allDataList.add(dictDataVo);
			if(ObjectUtil.isNotEmpty(childrenList)){
				allDataList.addAll(getAllDataList(childrenList));
			}
		}
		return allDataList;
	}


	/**
	 * form表单VO 转换为 导入模板VO
	 * @param formVO
	 * @param func_code
	 * @param file_type
	 * @return: com.hayden.hap.common.excel.entity.ExcelTemplateVO
	 * @Author: suntaiming
	 * @Date: 2021/6/9 11:05
	 */
	public static ExcelTemplateVO transformExcelTemplateVO(FormVO formVO, String func_code, String file_type) throws HDException {
		ExcelTemplateVO excelTemplateVO = new ExcelTemplateVO();
		excelTemplateVO.setTemp_code(formVO.getForm_code());
		excelTemplateVO.setTemp_name(formVO.getForm_name());
		excelTemplateVO.setFunc_code(func_code);
		excelTemplateVO.setFile_type(file_type);
		if(StringUtils.isEmpty(file_type)){
			file_type = formVO.getString("file_type");
			if(StringUtils.isEmpty(file_type)){
				file_type = ExportTypeEnum.EXCEL2007.getName();
			}
		}
		excelTemplateVO.setSmarker("1"); //第一行开始
		excelTemplateVO.setIsupdate(String.valueOf(SyConstant.SY_TRUE));
		excelTemplateVO.setIs_import_linklist(SyConstant.SY_FALSE);
		excelTemplateVO.setTenantid(formVO.getTenantid());
		excelTemplateVO.setCreated_dt(formVO.getCreated_dt());
		excelTemplateVO.setCreated_by(formVO.getCreated_by());
		excelTemplateVO.setUpdated_dt(formVO.getUpdated_dt());
		excelTemplateVO.setUpdated_by(formVO.getUpdated_by());
		return excelTemplateVO;
	}

	/**
	 * formitem 表单项VO 转换为 导入模板字段VO
	 * @param formItemVOS
	 * @param funcTableDefVO
	 * @param func_code
	 * @return: java.util.List<com.hayden.hap.common.excel.entity.ExcelTemplateItemVO>
	 * @Author: suntaiming
	 * @Date: 2021/6/9 11:06
	 */
	public static List<ExcelTemplateItemVO> transformExcelTemplateItemVOs(List<? extends FormItemVO> formItemVOS, TableDefVO funcTableDefVO, String func_code) throws HDException{
		List<ExcelTemplateItemVO> excelTemplateItemVOS = new ArrayList<>();
		if(formItemVOS == null || formItemVOS.isEmpty()){
			return excelTemplateItemVOS;
		}
		List<TableColumnVO> columnList = funcTableDefVO.getColumnList();
		Integer index = 1;
		for (FormItemVO formItemVO : formItemVOS){
			//附件等类型不在导入导出范围
			if(!isNeed(formItemVO) ){
				continue;
			}

			formItemVO.setFitem_order(index);
			index ++;

			TableColumnVO columnVO = ListUtil.first(columnList, (x)->{ return x.getColcode().equals(formItemVO.getFitem_code());});
			ExcelTemplateItemVO excelTemplateItemVO = transformExcelTemplateItemVO(formItemVO, columnVO, func_code);
			excelTemplateItemVOS.add(excelTemplateItemVO);
		}

		return excelTemplateItemVOS;
	}

	private static boolean isNeed(FormItemVO formItemVO){
		String fitem_input_element = formItemVO.getFitem_input_element();
		if(ElementTypeEnum.FILE.getCode().equals(fitem_input_element)
				||ElementTypeEnum.IMG.getCode().equals(fitem_input_element)
				||ElementTypeEnum.GROUP.getCode().equals(fitem_input_element)
				||ElementTypeEnum.NAVTAB.getCode().equals(fitem_input_element)
				||ElementTypeEnum.LABLE.getCode().equals(fitem_input_element)
				||ElementTypeEnum.PHOTO.getCode().equals(fitem_input_element)
				||ElementTypeEnum.FAST_FILE.getCode().equals(fitem_input_element)
				||ElementTypeEnum.FAST_IMG.getCode().equals(fitem_input_element)
				||ElementTypeEnum.HIDDEN.getCode().equals(fitem_input_element)){
			return false;
		}

		Integer fitem_readonly = formItemVO.getFitem_readonly();
		if(SyConstant.SY_TRUE == fitem_readonly){
			return false;
		}


		return true;
	}



	/**
	 * formitem 表单项VO 转换为 导入模板字段VO
	 *
	 * @param formItemVO
	 * @param tableColumnVO
	 * @param func_code
	 * @return: com.hayden.hap.common.excel.entity.ExcelTemplateItemVO
	 * @Author: suntaiming
	 * @Date: 2021/6/9 11:08
	 */
	private static ExcelTemplateItemVO transformExcelTemplateItemVO(FormItemVO formItemVO, TableColumnVO tableColumnVO, String func_code) throws HDException{
		ExcelTemplateItemVO excelTemplateItemVO = new ExcelTemplateItemVO();
		excelTemplateItemVO.setItem_code(formItemVO.getFitem_code());
		excelTemplateItemVO.setItem_name(formItemVO.getFitem_name());
		excelTemplateItemVO.setFunc_code(func_code);
		excelTemplateItemVO.setColumn_no(String.valueOf(formItemVO.getFitem_order()));
		excelTemplateItemVO.setColumn_title(formItemVO.getFitem_name());

		excelTemplateItemVO.setItem_len(String.valueOf(formItemVO.getFitem_length()));
		excelTemplateItemVO.setItem_precision("0"); //字段精度 默认0
		excelTemplateItemVO.setDefault_value(formItemVO.getFitem_card_default());

		excelTemplateItemVO.setIs_notnull(formItemVO.getFitem_notnull());
		excelTemplateItemVO.setIs_mandatory(SyConstant.SY_FALSE);
		excelTemplateItemVO.setExcel_cell_type("dropdown");
		excelTemplateItemVO.setIs_newlable_show(SyConstant.SY_FALSE);
		excelTemplateItemVO.setIs_enable(SyConstant.SY_TRUE);
		excelTemplateItemVO.setTenantid(formItemVO.getTenantid());

		//是否主键 -》 根据表单字段是否配置的唯一组且必须是第一组
		if("1".equals(formItemVO.getFitem_unique_group())){
			excelTemplateItemVO.setIs_pk(SyConstant.SY_TRUE);
		}else {
			excelTemplateItemVO.setIs_pk(SyConstant.SY_FALSE);
		}

		//字段类型 -》除了表单配置日期 特殊处理 ，其他取表定义中字段类型
		String fitem_input_element = formItemVO.getFitem_input_element();
		if(ElementTypeEnum.DATE.getCode().equals(fitem_input_element)){
			excelTemplateItemVO.setItem_type("DATETIME");
		}else if (tableColumnVO != null){
			excelTemplateItemVO.setItem_type(tableColumnVO.getColtype()); //字段类型 -》取表定义中字段类型
		}else {
			excelTemplateItemVO.setItem_type("VARCHAR");
		}


		//字典输入设定和查询选择输入设定 -》 1.表单字段 输入类型为字典则输入设定赋值给字典输入设定，2.表单字段输入类型为查询选择则输入设定赋值给模板字段的查询选择输入设定
		//3.元素类型为日期 把输入设定赋值给 字典输入设定
		String fitem_input_type = formItemVO.getFitem_input_type();
		if(InputTypeEnum.DICT_NEW.getCode().equals(fitem_input_type)){
			excelTemplateItemVO.setDict(formItemVO.getFitem_input_config());
		}else if (InputTypeEnum.QUERY_SELECT.getCode().equals(fitem_input_type)){
			excelTemplateItemVO.setQuery_sel(formItemVO.getFitem_input_config());
		}

		/*else if (ElementTypeEnum.DATE.getCode().equals(fitem_input_element)){
			excelTemplateItemVO.setDict(formItemVO.getFitem_input_config());
		}*/

		return excelTemplateItemVO;

	}


	/**
	 * 根据表定义获取对应的class， 如果class不存在 则返 BaseVO.class
	 * @param tableDefVO
	 * @return: java.lang.Class<? extends com.hayden.hap.common.common.entity.AbstractVO>
	 * @Author: suntaiming
	 * @Date: 2021/6/9 11:13
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends AbstractVO> getVOClass(TableDefVO tableDefVO) {
		ObjectUtil.validNotNull(tableDefVO, "tableDefVO is required.");
		if (ObjectUtil.isNotNull(tableDefVO.getClassname())) {
			try {
				return (Class<? extends AbstractVO>) ResourceUtil.classForName(tableDefVO.getClassname());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
				return BaseVO.class;
			}
		}
		return BaseVO.class;
	}
	
}
