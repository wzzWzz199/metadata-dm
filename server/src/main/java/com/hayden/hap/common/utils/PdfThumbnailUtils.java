package com.hayden.hap.common.utils;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;



/**
 * PDF 缩略图 
 * 
 * @author haocs
 * @date 2019年8月12日
 */
public class PdfThumbnailUtils {

	private static final Logger logger = LoggerFactory.getLogger(PdfThumbnailUtils.class);
	
	public static void main(String[] args) throws IOException, PDFException, PDFSecurityException, InterruptedException {
//		pdfrender();
	   ArrayList<String> list = new ArrayList<>();
	   list.add("11-领科云基于Mesos和Docker的企业级移动应用实践分享");
	   list.add("12-惠普基于Kubernetes的容器私有云平台实践");
	   list.add("2019年中国人工智能产业研究报告");
	   list.add("2019年中国智慧城市发展报告");
	   list.add("20161108144356");
	   list.add("20180812210748");
		for (String string : list) {
			
			System.out.println(pdfGenerateImg("C:\\Users\\haocs\\Desktop\\TODAY\\bgi\\"+string+".pdf"));
		}
	}
	public static final String FILETYPE_JPG = "jpg";
	public static final String FILETYPE_PNG = "png";
	public static final String SUFF_IMAGE = "." + FILETYPE_JPG;
	public static final String SUFF_PNG_IMAGE = "." + FILETYPE_PNG;

	
	
	/**
	 * 将指定pdf文件的首页转换图片
	 * @param filepath 原文件路径
	 * @return 
	 * @author haocs
	 * @date 2019年8月12日
	 */
	public static String pdfGenerateImg(String filepath)
	{
		// ICEpdf document class
		Document document = null;

		float rotation = 0f;

		String imagepath = System.getProperty("user.home") + "/temp/" + UUID.randomUUID() + ".png";
		try {
			document = new Document();
			document.setFile(filepath);

			BufferedImage img = (BufferedImage) document.getPageImage(0, GraphicsRenderingHints.SCREEN,
					Page.BOUNDARY_CROPBOX, rotation, 1f);
			Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(FILETYPE_PNG);
			ImageWriter writer = (ImageWriter) iter.next();
			File outFile = new File(imagepath);
			outFile.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(outFile);
			ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
			writer.setOutput(outImage);
			writer.write(new IIOImage(img, null, null));
			if(outFile.exists()) return imagepath;
		} catch (Exception e) {
			logger.error("PDF保存图片异常!",e);
		}
		return null;
	}
	/**
	 * 将指定pdf文件的首页转换图片
	 * @param filepath 原文件路径
	 * @return 
	 * @author haocs
	 * @date 2019年8月12日
	 */
	public static InputStream pdfGenerateImg(InputStream stream,String fileName)
	{
		// ICEpdf document class
		Document document = null;
		
		float rotation = 0f;
		try {
			document = new Document();
			document.setInputStream(stream, fileName);
			String imagepath = System.getProperty("user.home") + "/temp/" + UUID.randomUUID() + ".png";
			
			BufferedImage img = (BufferedImage) document.getPageImage(0, GraphicsRenderingHints.SCREEN,
					Page.BOUNDARY_CROPBOX, rotation, 1f);
			Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(FILETYPE_PNG);
			ImageWriter writer = (ImageWriter) iter.next();
			File outFile = new File(imagepath);
			outFile.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(outFile);
			ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
			writer.setOutput(outImage);
			writer.write(new IIOImage(img, null, null));
			if(outFile.exists()) return new FileInputStream(imagepath);
		} catch (Exception e) {
			logger.error("PDF保存图片异常!",e);
		}
		return null;
	}
	/**
	 * 将指定pdf文件的首页转换图片
	 * @param filepath 原文件路径
	 * @param zoom 缩放比例 0-1  
	 * @return 
	 * @author haocs
	 * @date 2019年8月12日
	 */
	public static String pdfGenerateImg(String filepath,float zoom)
	{
		// ICEpdf document class
		Document document = null;
		
		float rotation = 0f;
		
		String imagepath = System.getProperty("user.home") + "/temp/" + UUID.randomUUID() + ".png";
		try {
			document = new Document();
			document.setFile(filepath);
			
			BufferedImage img = (BufferedImage) document.getPageImage(0, GraphicsRenderingHints.SCREEN,
					Page.BOUNDARY_CROPBOX, rotation, zoom);
			Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(FILETYPE_PNG);
			ImageWriter writer = (ImageWriter) iter.next();
			File outFile = new File(imagepath);
			outFile.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(outFile);
			ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
			writer.setOutput(outImage);
			writer.write(new IIOImage(img, null, null));
			if(outFile.exists()) return imagepath;
		} catch (Exception e) {
			logger.error("PDF保存图片异常!",e);
		}
		return null;
	}

}
