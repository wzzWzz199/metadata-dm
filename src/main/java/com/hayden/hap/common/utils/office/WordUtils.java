/**
 * Project Name:hap-sy
 * File Name:WordUtils.java
 * Package Name:com.hayden.hap.sy.utils.office
 * Date:2016年5月13日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.utils.office;

import com.hayden.hap.common.common.exception.HDException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * ClassName:WordUtils ().<br/>
 * Date:     2016年5月13日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class WordUtils {
	public static final Logger logger = LoggerFactory
			.getLogger(ExcelUtils.class);
	public static void main(String args[]) throws HDException {
		FileInputStream in = null;
		FileOutputStream fos = null;
		try {
			String path = "f:/tmp";
			File file1 = new File(path, "二维码测试.jpg");
			in = new FileInputStream(file1);
			fos = new FileOutputStream("f:/tmp/二维码.docx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
	}

	private static void setDocFormat(HttpServletResponse response,
			String fileType,String fileName) throws HDException{

		try {
			response.reset();
			// 设置头信息,内容处理的方式,attachment以附件的形式打开,就是进行下载,并设置下载文件的命名
			if(fileType.equalsIgnoreCase("DOC")){
				response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName+".doc", "UTF-8") );
				response.setContentType("application/msword; charset=utf-8");
			}else{
				response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName+".docx", "UTF-8") );
				response.setContentType("application/msword; charset=utf-8");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new HDException(e.getMessage(),e);
		}
	}
	public static void writeToDoc(List<ByteArrayOutputStream> baoList,HttpServletResponse response,
			String fileType,String fileName)
			throws HDException {
		
		setDocFormat(response,fileType,fileName);
		
		OutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new HDException(e.getMessage(),e);
		}
		
		XWPFDocument doc = new XWPFDocument(); 
		try {
			for (ByteArrayOutputStream bao : baoList) {
				doc.addPictureData(bao.toByteArray(),Document.PICTURE_TYPE_JPEG);
			}
			doc.write(out);
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
			throw new HDException("操作doc异常!");
		} catch (InvalidFormatException e) {
			logger.error(e.getMessage(), e);
			throw new HDException("操作doc异常!");
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

