package com.hayden.hap.common.export.way;

import com.hayden.hap.common.formmgr.message.Message;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 其他模板 中 excel2003的 导出方式
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public class TemplateExcel2003ExportWay extends TemplateExportWay{
	/**
	 * Logger for this class
	 */
	//private static final Logger logger = LoggerFactory.getLogger(TemplateExcel2003ExportWay.class);

	Excel2003Export excel2003Export = new Excel2003Export();
//	protected TemplateExcel2003ExportWay(String exportWay, String fileType) {
//		super(exportWay, fileType);
//	}


	@Override
	protected HttpServletResponse changeRes(HttpServletResponse response) {
		return excel2003Export.changeRes(response);
	}

	@Override
	public String getSuf() {
		return excel2003Export.getSuf();
	}

	@Override
	protected Workbook getWorkbook() {
		return excel2003Export.getWorkbook();
	}

	@Override
	protected List<Message> validColnum(int colnum) {
		return excel2003Export.validColnum(colnum);
	}

	@Override
	protected InputStream getInputStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException {
		return excel2003Export.getInputStream(wb);
	}

	
	
}
