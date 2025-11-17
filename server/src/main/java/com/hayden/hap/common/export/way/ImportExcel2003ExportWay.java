package com.hayden.hap.common.export.way;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 导入模板 中 excel2003的 导出方式
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public class ImportExcel2003ExportWay extends ImportExportWay{
	/**
	 * Logger for this class
	 */
	//private static final Logger logger = LoggerFactory.getLogger(ImportExcel2003ExportWay.class);

	Excel2003Export excel2003Export = new Excel2003Export();
//	protected ImportExcel2003ExportWay(String exportWay, String fileType) {
//		super(exportWay, fileType);
//	}


	@Override
	public HttpServletResponse changeRes(HttpServletResponse response) {
		return excel2003Export.changeRes(response);
	}

	@Override
	public String getSuf() {
		return excel2003Export.getSuf();
	}

	@Override
	public Workbook getWorkbook() {
		return excel2003Export.getWorkbook();
	}


	@Override
	protected ClientAnchor getClientAnchor() {
		return new HSSFClientAnchor();
	}

	@Override
	protected RichTextString getRichTextString(String value) {
		return new HSSFRichTextString(value);
	}

	@Override
	protected InputStream getInputStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException {
		return excel2003Export.getInputStream(wb);
	}
	
}
