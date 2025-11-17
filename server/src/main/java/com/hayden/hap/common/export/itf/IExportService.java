package com.hayden.hap.common.export.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.export.entity.ExportHandleRecordVO;
import com.hayden.hap.common.export.entity.ExportTemplateItemVO;
import com.hayden.hap.common.export.entity.ExportTemplateVO;
import com.hayden.hap.common.export.entity.WordVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@IService("exportServiceImpl")
public interface IExportService {

	/**
	 * 得到功能对应的模板
	 * 
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2016年11月7日
	 */
	public List<ExportTemplateVO> getExportTempByCode(String funcCode, Long tenantid) throws HDException;
	
	/**
	 * 得到功能的导入模板
	 * @param funcCode
	 * @param tenantid
	 * @return
	 */
	public List<ExcelTemplateVO> getImportTempByCode(String funcCode, Long tenantid) throws HDException;

	/**
	 * 导出
	 * 
	 * @param request
	 * @param response
	 * @param formParamVO
	 * @param type
	 * @param templateid 导出模板的ID
	 * @param importtemplateid 导入模板的ID
	 * @param exportids
	 * @param exportcountb
	 * @param exportcounte
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2016年11月7日
	 */
	public ReturnResult export(HttpServletRequest request, HttpServletResponse response, FormParamVO formParamVO,
			String type, Long templateid, Long importtemplateid, String exportids, int exportcountb, int exportcounte) throws HDException;

	/**
	 * 重置列表列
	 * 
	 * @param exportid
	 * @author liyan
	 * @throws HDException
	 * @date 2016年11月7日
	 */
	public void reset(Long exportid) throws HDException;

	/**
	 * 得到用户有权限的功能编码集
	 * 
	 * @param usrid
	 * @param tenantid
	 * @param is_sy
	 * @return
	 * @author liyan
	 * @date 2016年11月7日
	 */
	public List<String> getFuncodeByUserid(Long userid, Long tenantid, Boolean is_sy);

	/**
	 * 得到有导出按钮的功能
	 * 
	 * @param userHasList
	 * @return
	 * @author liyan
	 * @param tenantid
	 * @date 2016年11月7日
	 */
	public List<String> getHasButtonFunc(List<String> userHasList, Long tenantid);

	/**
	 * 校验导出行数在各版本下是否合法
	 * 
	 * @param exportcounte
	 * @param exportcountb
	 * @param templateid
	 * @param type
	 * @author liyan
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException
	 * @date 2016年11月7日
	 */
	public ReturnResult checkNum(String funcCode, int exportcounte, int exportcountb, Long templateid, String type,
			Long tenantid) throws HDException;

	/**
	 * 得到子列表列
	 * 
	 * @param func_code
	 * @param tenantid
	 * @param templateid
	 * @param product_flag
	 * @return
	 * @author liyan
	 * @throws HDException
	 * @date 2017年7月21日
	 */
	public List<ExportTemplateItemVO> getSubList(String func_code, Long tenantid, Long templateid, int product_flag)
			throws HDException;

	/**
	 * 创建模板列
	 * 
	 * @param tenantid
	 * @param main_func_code
	 * @param func_code
	 * @param exportid
	 * @param product_flag
	 * @param exceptItem
	 * @return
	 * @author liyan
	 * @throws HDException
	 * @date 2017年7月21日
	 */
	public List<ExportTemplateItemVO> creatItemVo(Long tenantid, String main_func_code, String func_code, Long exportid,
			Integer product_flag, String exceptItem) throws HDException;

	/**
	 * 生成word试卷
	 * @param response
	 * @param word 试卷内容
	 * @param tenantid
	 * @return 试卷临时路径
	 * @throws HDException 
	 * @author zhenjianting
	 * @date 2018年7月4日
	 */
	public String buildWordTestPaper(WordVO word, Long tenantid) throws HDException;
	
	/**
	 * 导出处理
	 * @param handle
	 * @param tenantid
	 * @return
	 * @throws HDException
	 */
	public void handleExportDataToFile(ExportHandleRecordVO handle, Long tenantid) throws HDException;
	
	/**
	 * 导出处理
	 * @param handle
	 * @return
	 * @throws HDException 
	 */
	public void exportToFastDfs(ExportHandleRecordVO handle ) throws HDException ;
	/**
	 * 查询
	 * @param funcCode
	 * @param moduleCode
	 * @param tenantId
	 * @param userId
	 * @return
	 * @throws HDException
	 */
	public ExportHandleRecordVO queryExportData(String funcCode,String moduleCode,Long tenantId,Long userId)throws HDException ;
	/**
	 * inster
	 * @param funcCode
	 * @param moduleCode
	 * @param tenantId
	 * @param userId
	 * @return
	 * @throws HDException
	 */
	public ExportHandleRecordVO insterExportData(ExportHandleRecordVO record)throws HDException ;
	/**
	 * 
	 * 查询按钮参数配置
	 * @param funcCode
	 * @param btnCode
	 * @param tenantid
	 * @author haocs
	 * @return
	 * @throws HDException
	 */
	public int getExportMaxNums(String funcCode,String btnCode,Long tenantid) throws HDException;
	/**
	 * 
	 * 获取导出最大数
	 * @param tenantid
	 * @author haocs
	 * @return
	 * @throws HDException
	 */
	public int getDefaultExportMaxNums(Long tenantid) throws HDException;

}
