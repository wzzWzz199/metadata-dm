package com.hayden.hap.common.export.way;

import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.formmgr.message.Message;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Excel2007Export {
	
	public String getSuf() {
		return ".xlsx";
	}
	
	public HttpServletResponse changeRes(HttpServletResponse response) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=utf-8");
		return response;
	}
	
	public Workbook getWorkbook() {
		return new XSSFWorkbook();
	}

	public List<Message> validColnum(int colnum) {
		List<Message> msgList = new ArrayList<>();
		if(colnum>16383){
			Message message = new Message("导出列数不能超过16383",MessageLevel.ERROR,MessageShowType.POPUP);
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
	public ByteArrayOutputStream getStream(Workbook wb) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		wb.write(outputStream);
		outputStream.flush();
		outputStream.close();
		return outputStream;
	}
}
