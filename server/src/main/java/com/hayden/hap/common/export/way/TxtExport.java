package com.hayden.hap.common.export.way;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TxtExport {
	
	public HttpServletResponse changeRes(HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		return response;
	}

	public String getSuf() {
		return ".txt";
	}
	
	public InputStream getInputStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StringBuffer write = new StringBuffer();  
		String tab = "	";  
		if(!StringUtils.isEmpty(txt_separator)){
			tab = txt_separator; 
		}
		String enter = "\r\n"; 
		for (int i = 0; i < rownum; i++) {  
			Row row = sheet.getRow(i);
			for(int j = 0; j < colIndex; j++){
				Cell cell = row.getCell(j); 
				if(null!=cell){
					String str = getCellValue(cell)==null?"":getCellValue(cell).toString();
					write.append(str); 
				}		
				if(j==colIndex-1){
					write.append(""); 
				}else{
					write.append(tab); 
				}
			}
			write.append(enter);            
		}
		outputStream.write(write.toString().getBytes("UTF-8"));  
		outputStream.flush();  
		outputStream.close();  
		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); 
		 
		return inputStream;
	}
	public ByteArrayOutputStream getStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StringBuffer write = new StringBuffer();  
		String tab = "	";  
		if(!StringUtils.isEmpty(txt_separator)){
			tab = txt_separator; 
		}
		String enter = "\r\n"; 
		for (int i = 0; i < rownum; i++) {  
			Row row = sheet.getRow(i);
			for(int j = 0; j < colIndex; j++){
				Cell cell = row.getCell(j); 
				if(null!=cell){
					String str = getCellValue(cell)==null?"":getCellValue(cell).toString();
					write.append(str); 
				}		
				if(j==colIndex-1){
					write.append(""); 
				}else{
					write.append(tab); 
				}
			}
			write.append(enter);            
		}
		outputStream.write(write.toString().getBytes("UTF-8"));  
		outputStream.flush();  
		outputStream.close();  
		
		return outputStream;
	}
	
	/**
	 * 获得单元格的值
	 * @param cell
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	private Object getCellValue(Cell cell){
		Object unchecked_value = null;
		// 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
		// 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			unchecked_value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			// 这里的日期类型会被转换为数字类型，需要判别后区分处理
			if (DateUtil.isCellDateFormatted(cell)) unchecked_value = cell.getDateCellValue();
			else unchecked_value = cell.getNumericCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			unchecked_value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_ERROR:
			break;
		default:
			break;
		}
		return unchecked_value;
	}
	
}
