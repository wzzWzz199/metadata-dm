package com.hayden.hap.common.excel.itf;

import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;


/**
 * 
 * @author hayden
 * @date 2016年4月12日
 */
@IService("excelTemplateService")
public interface IExcelTemplateService {

	/**
	 * 下载模板
	 * @param func_code 功能编码
	 * @param request  
	 * @param response
	 * @return ReturnResult
	 */
	public List<Message> exportTemplate(String func_code,HttpServletRequest request, HttpServletResponse response);

	/**
	 * 上传模板
	 * @param formParamVO 数据传输对象
	 * @param temp_code 模板编码
	 * @param is excel流
	 * @param extensionName 扩展名
	 * @param tenantid 
	 * @return ReturnResult 返回
	 * @throws HDException
	 */
	public ReturnResult<?> importTemplate(FormParamVO formParamVO, String temp_code, InputStream is, String extensionName, Long tenantid)
			throws HDException;

	/**
	 *
	 * @param formParamVO 数据传输对象
	 * @param is excel流
	 * @param extensionName 扩展名
	 * @return ReturnResult 返回
	 * @throws HDException
	 */
	public ReturnResult<?> readTemplateData(FormParamVO formParamVO, InputStream is, String extensionName)
			throws HDException;

	/**
	 * 得到导入模板及其模板明细
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年6月28日
	 */
	public TemplateVO getImportTempByCode(String funcCode, Long tenantid)
			throws HDException;

	/**
	 * 获取当前排序字段
	 * @param param
	 * @param tenantid
	 * @param funcCode
	 * @return 
	 * @author liyan
	 * @throws HDException 
	 * @date 2017年12月20日
	 */
	public List<ExcelTemplateItemVO> getAllItems(ReqParamVO param, Long tenantid,
			String funcCode) throws HDException;

	/**
	 * 更新排序顺序
	 * @param ids 
	 * @author liyan
	 * @throws HDException 
	 * @date 2017年12月20日
	 */
	public void updateOrder(List<Long> ids) throws HDException;
}
