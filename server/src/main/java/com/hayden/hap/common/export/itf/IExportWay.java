package com.hayden.hap.common.export.itf;

import com.hayden.hap.common.attach.itf.IAttachMethodService;
import com.hayden.hap.common.attach.itf.IAttachService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.dict.itf.IDictDataService;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * 导出方式接口
 * 
 * @author liyan
 * @date 2017年6月29日
 */
public interface IExportWay {

	IAttachMethodService attachMethodService = (IAttachMethodService) AppServiceHelper
			.findBean("attachMethodServiceImpl");
	IFuncService funcService = (IFuncService) AppServiceHelper.findBean("funcService");
	IBaseService baseService = (IBaseService) AppServiceHelper.findBean("baseService");
	ITableDefService tableDefService = (ITableDefService) AppServiceHelper.findBean("tableDefService");
	IFormItemPCService formItemPCService = AppServiceHelper.findBean(IFormItemPCService.class);
	IAttachService attachService = (IAttachService) AppServiceHelper.findBean("attachService");
	IDictDataService dictDataService = (IDictDataService) AppServiceHelper.findBean("dictDataService");

	public String getSuf();

	public HttpServletResponse resetHeader(HttpServletRequest request, HttpServletResponse response, String funcnama,
			String suf, TemplateVO... templateVO) throws UnsupportedEncodingException;


	HttpServletResponse resetHeader(HttpServletRequest request, HttpServletResponse response, String func_code, String funcnama,
										   String suf, Long tenantid, TemplateVO... templateVO) throws UnsupportedEncodingException;

	/**
	 * 导出
	 * 
	 * @param templateVO
	 * @param exportVoList
	 * @param formParamVO
	 * @param funcnama
	 * @param tenantid
	 * @param exportids
	 * @param exportcountb
	 * @param exportcounte
	 * @return
	 * @author liyan
	 * @throws HDException
	 * @date 2017年6月29日
	 */
	public ReturnResult<InputStream> getExportInputStream(TemplateVO templateVO,
			List<? extends AbstractVO> exportVoList, FormParamVO formParamVO, String funcnama, Long tenantid,
			String exportids, int exportcountb, int exportcounte) throws HDException;

	/**
	 * 导出
	 * @param wb  工作簿
	 * @param templateVO 导入/导出模板
	 * @param exportVoList 导出数据
	 * @param funcnama 功能名 创建主sheet
	 * @param funcCode 功能编码 创建 隐藏sheet
	 * @param tenantid
	 * @param exportcountb 开始行
	 * @param exportcounte 结束行
	 * @param intFirstRow 
	 * @return
	 * @throws HDException
	 */
	public Workbook getExportToWorkBook(Workbook wb, TemplateVO templateVO,
										List<? extends AbstractVO> exportVoList, String funcnama,String funcCode, Long tenantid,int exportcountb, int exportcounte, boolean intFirstRow,HashMap<String, String> dictmap) throws HDException;

}
