package com.hayden.hap.common.export.way;

import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.formmgr.message.Message;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Excel2003Export {

	protected HttpServletResponse changeRes(HttpServletResponse response) {
		response.setContentType("application/vnd.ms-excel; charset=utf-8");
		return response;
	}

	public String getSuf() {
		return ".xls";
	}

	public Workbook getWorkbook() {
		return new HSSFWorkbook();
	}
	
	public List<Message> validColnum(int colnum) {
		List<Message> msgList = new ArrayList<>();
		if(colnum>255){
			Message message = new Message("导出列数不能超过255",MessageLevel.ERROR,MessageShowType.POPUP);
			msgList.add(message);
		}
		return msgList;
	}
	
	public InputStream getInputStream(Workbook wb) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		wb.write(outputStream);
		outputStream.flush();
		outputStream.close();
		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); 
		return inputStream;
	}
}
