package com.hayden.hap.common.excel.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.utils.tuple.TwoTuple;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * @author hayden
 *
 */
public interface IExcelVOMapper {

	/**
	 * 下载模板
	 * @param excelTemplateVO
	 * @param excelTemplateItemVOs
	 * @return
	 * @throws HDException
	 */
	public InputStream exportExcel(ExcelTemplateVO excelTemplateVO,
			List<ExcelTemplateItemVO> excelTemplateItemVOs) throws HDException;
	
	/**
	 * 导入模板 转成结果对象
	 * @param is 输入流
	 * @param extensionName 扩展名
	 * @param excelTemplateVO 模板对象
	 * @param excelTemplateItemVOs 模板字段对象
	 * @return 
	 * @throws HDException
	 */
	public ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> exportListFromExcel(InputStream is,
			String extensionName, ExcelTemplateVO excelTemplateVO,
			List<ExcelTemplateItemVO> excelTemplateItemVOs) throws HDException ;


	/**
	 * 由模板对应的Sheet导出至List
	 * @param workbook
	 * @param excelTemplateVO
	 * @param excelTemplateItemVOs
	 * @return: com.hayden.hap.common.formmgr.message.ReturnResult<com.hayden.hap.common.utils.tuple.TwoTuple<java.util.List<com.hayden.hap.common.common.entity.AbstractVO>,java.util.LinkedHashMap<java.lang.String,java.util.LinkedHashMap<com.hayden.hap.common.common.entity.AbstractVO,java.util.List<com.hayden.hap.common.common.entity.AbstractVO>>>>>
	 * @Author: suntaiming
	 * @Date: 2021/6/10 15:35
	 */
	ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> exportListFromExcel(
			Workbook workbook, ExcelTemplateVO excelTemplateVO, List<ExcelTemplateItemVO> excelTemplateItemVOs)
			throws HDException ;


	/**
	 * 由模板对应的Sheet导出至List
	 * @param formParamVO
	 * @param workbook
	 * @param tenantid
	 * @return: com.hayden.hap.common.formmgr.message.ReturnResult<com.hayden.hap.common.utils.tuple.TwoTuple<java.util.List<com.hayden.hap.common.common.entity.AbstractVO>,java.util.LinkedHashMap<java.lang.String,java.util.LinkedHashMap<com.hayden.hap.common.common.entity.AbstractVO,java.util.List<com.hayden.hap.common.common.entity.AbstractVO>>>>>
	 * @Author: suntaiming
	 * @Date: 2021/6/10 16:11
	 */
	ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> exportListFromExcelOfForm(
			FormParamVO formParamVO, Workbook workbook, Long tenantid) throws HDException ;
}
