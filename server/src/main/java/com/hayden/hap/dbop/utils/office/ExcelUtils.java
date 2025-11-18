/**
 * Project Name:hap-sy
 * File Name:ExcelUtils.java
 * Package Name:com.hayden.hap.sy.utils.office
 * Date:2016年5月4日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
 */

package com.hayden.hap.dbop.utils.office;

import com.hayden.hap.dbop.exception.HDException;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * ClassName:ExcelUtils ().<br/>
 * Date: 2016年5月4日 <br/>
 * 
 * @author ZhangJie
 * @version
 * @see
 */
public class ExcelUtils {
	public static final Logger logger = LoggerFactory
			.getLogger(ExcelUtils.class);

	public static void main(String args[]) throws HDException {
		FileInputStream in = null;
		FileOutputStream fos = null;
		try {
			String path = "f:/tmp";
			File file1 = new File(path, "二维码测试2.jpg");
			in = new FileInputStream(file1);
			fos = new FileOutputStream("f:/tmp/二维码.xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
		ExcelUtils.writeToXls(in,fos);
	}

	public static void writeToXls(FileInputStream byteArrayOut,OutputStream fos)
			throws HDException {
		XSSFWorkbook wb = null;
		wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("系统生成页");
//		sheet.setDefaultRowHeight((short)(12.75*XSSFShape.PIXEL_DPI/XSSFShape.POINT_DPI));
		sheet.setDefaultRowHeightInPoints((float)12.75*XSSFShape.PIXEL_DPI/XSSFShape.POINT_DPI);
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,
				 0,0, 0 ,0);
		try {
			drawing.createPicture(anchor, wb.addPicture(byteArrayOut,
					XSSFWorkbook.PICTURE_TYPE_JPEG)).resize();
			wb.write(fos);
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
			throw new HDException("操作xls异常!");
		}

	}
	
	private static void setXlsFormat(HttpServletRequest request, HttpServletResponse response,
			String fileType,String fileName) throws HDException{

		try {
			response.reset();
			// 设置头信息,内容处理的方式,attachment以附件的形式打开,就是进行下载,并设置下载文件的命名
			String suf = ".xls";
			if(fileType.equalsIgnoreCase("XLS")){
				suf=".xls";
				response.setContentType("application/vnd.ms-excel; charset=utf-8");
			}else{
				suf=".xlsx";
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=utf-8");
			}
			String fileNameTemp = fileName+suf;
			String agent = request.getHeader("user-agent");
			if(agent.contains("Firefox")) 
				fileNameTemp = new String(fileNameTemp.getBytes("UTF-8"), "ISO8859-1");
			else
				fileNameTemp = URLEncoder.encode(fileNameTemp, "UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" +fileNameTemp);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new HDException(e.getMessage(),e);
		}
	}
	public static void writeToXls(List<ByteArrayOutputStream> baoList,HttpServletRequest request, HttpServletResponse response,
			String fileType,String fileName)
			throws HDException {
		
		setXlsFormat(request,response,fileType,fileName);
		
		OutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new HDException(e.getMessage(),e);
		}
		XSSFWorkbook wb = null;
		wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("系统生成页");
		sheet.setDefaultRowHeightInPoints((float)12.75*XSSFShape.PIXEL_DPI/XSSFShape.POINT_DPI);
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = null;
		try {
			int i=0;
			int extHeight = 0;
			int pageRows = 40;//一页的行数
			int imgHeigth = 0;//图片的高
			int rowCount = 3;//每行放几个图片
			int startRow = 0;//图片左上角（起始）cell的行，纵坐标
			int startCol = 0;//图片左上角（起始）cell的列，横坐标
			int endRow = 0;//图片右下角（结束）cell的行，纵坐标
			int endCol = 0;//图片右下角（结束）cell的列，横坐标
			int rowNum = 0;//图片所在页面的行数
			for (ByteArrayOutputStream bao : baoList) {
				if(i>0&&i%rowCount==0){
					if(pageRows-endRow%pageRows<=imgHeigth){
						startRow = endRow+(pageRows-endRow%pageRows);
					}else{
						startRow = endRow+1;
					}
					startCol = 0;
				}
				anchor = new XSSFClientAnchor(0, 0, 0, 0,
						startCol, startRow,  0,0);
				drawing.createPicture(anchor, wb.addPicture(bao.toByteArray(),XSSFWorkbook.PICTURE_TYPE_JPEG)).resize();
//				drawing.createPicture(anchor, wb.addPicture(bao.toByteArray(),XSSFWorkbook.PICTURE_TYPE_JPEG)).resize();
//				drawing.createPicture(anchor, wb.addPicture(bao.toByteArray(),XSSFWorkbook.PICTURE_TYPE_JPEG)).resize();
				endCol = anchor.getCol2();
				endRow = anchor.getRow2();
				if(i==0){
					imgHeigth = endRow;
				}
				startCol = endCol+1;
				sheet.setAutobreaks(false);
				if(i!=0&&(i%12==0)){
					sheet.setRowBreak(startRow-2);
				}				
				i++;
			}
			wb.write(out);
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
			throw new HDException("操作xls异常!");
		}
		
	}
	
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}
	
	public static byte[] toByteArray(OutputStream out) throws IOException {
		byte[] buffer = new byte[10240];
		out.write(buffer);
		out.flush();
		out.close();
		return buffer;
	}
}
