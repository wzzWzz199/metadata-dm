package com.hayden.hap.common.export.way;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 导入模板 中 excel2007的 导出方式
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public class ImportExcel2007ExportWay extends ImportExportWay{
	/**
	 * Logger for this class
	 */
	//private static final Logger logger = LoggerFactory.getLogger(ImportExcel2007ExportWay.class);

	Excel2007Export excel2007Export = new Excel2007Export();
//	protected ImportExcel2007ExportWay(String exportWay, String fileType) {
//		super(exportWay, fileType);
//	}

	@Override
	protected HttpServletResponse changeRes(HttpServletResponse response) {
		excel2007Export.changeRes(response);
		return response;
	}

	@Override
	public String getSuf() {
		return excel2007Export.getSuf();
	}

	@Override
	public Workbook getWorkbook() {
		return excel2007Export.getWorkbook();
	}

	@Override
	protected ClientAnchor getClientAnchor() {
		return new XSSFClientAnchor();
	}

	@Override
	protected RichTextString getRichTextString(String value) {
		return new XSSFRichTextString(value);
	}


	@Override
	protected InputStream getInputStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException {
		return excel2007Export.getInputStream(wb);
	}


	
}
