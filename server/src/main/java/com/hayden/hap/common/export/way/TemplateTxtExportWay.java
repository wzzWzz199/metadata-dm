package com.hayden.hap.common.export.way;

import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.formmgr.message.Message;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 其他模板 中 txt的 导出方式
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public class TemplateTxtExportWay extends TemplateExportWay{
	/**
	 * Logger for this class
	 */
	//private static final Logger logger = LoggerFactory.getLogger(TemplateTxtExportWay.class);

	TxtExport txtExport = new TxtExport();
//	protected TemplateTxtExportWay(String exportWay, String fileType) {
//		super(exportWay, fileType);
//	}


	@Override
	protected HttpServletResponse changeRes(HttpServletResponse response) {
		return txtExport.changeRes(response);
	}

	@Override
	public String getSuf() {
		return txtExport.getSuf();
	}
	
	@Override
	protected Workbook getWorkbook() {
		return new XSSFWorkbook();
	}

	@Override
	protected List<Message> validColnum(int colnum) {
		List<Message> msgList = new ArrayList<>();
		if(colnum>16383){
			Message message = new Message("导出列数不能超过16383",MessageLevel.ERROR,MessageShowType.POPUP);
			msgList.add(message);
		}
		return msgList;
	}
	
	@Override
	protected InputStream getInputStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException {
		return txtExport.getInputStream(wb, sheet, rownum, colIndex, txt_separator);
	}
	
}
