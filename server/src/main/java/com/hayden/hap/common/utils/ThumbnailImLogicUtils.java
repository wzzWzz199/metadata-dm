package com.hayden.hap.common.utils;

import com.hayden.hap.common.attach.entity.AttachDfsVO;
import com.hayden.hap.common.attach.entity.AttachParamVO;
import com.hayden.hap.common.attach.itf.IAttachConstants;
import com.hayden.hap.common.attach.server.FastDfsServiceImpl;
import com.hayden.hap.dbop.exception.HDException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 
 * @author haocs
 * @date 2019年8月14日
 */
public class ThumbnailImLogicUtils {

	private static final Logger logger = LoggerFactory.getLogger(FastDfsServiceImpl.class);
	
	/**
	 * 图片缩略图
	 * @param dfs
	 * @param input
	 * @param fileType
	 * @param fileName
	 * @return
	 * @throws HDException
	 * @throws IOException 
	 * @author haocs
	 * @date 2019年8月9日
	 */
	public static byte[] logicImgThumbnail(AttachDfsVO dfs,AttachParamVO attachParamVO,String fileName) throws HDException, IOException {
		// 设置缩略图
		BufferedImage img =thumbnailImg(attachParamVO.getMultipartFile().getInputStream());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(img, IAttachConstants.ATTACH_PNG, os); 
		return  os.toByteArray();
	}
	/**
	 * 文档缩略图
	 * @param dfs
	 * @param input
	 * @param fileType
	 * @param fileName
	 * @return
	 * @throws HDException
	 * @throws IOException 
	 * @author haocs
	 * @date 2019年8月9日
	 */
	public static byte[] logicDocThumbnail(AttachDfsVO dfs, AttachParamVO attachParamVO,
			String fileName) throws HDException {
 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			InputStream  inputStream = attachParamVO.getMultipartFile().getInputStream();
			InputStream pdfGenerateImg = PdfThumbnailUtils.pdfGenerateImg(inputStream, fileName);
			if (pdfGenerateImg == null) {
				throw new HDException("生成文档封面图失败!");
			}
			// 设置缩略图
			BufferedImage img = thumbnailImg(pdfGenerateImg);
			ImageIO.write(img, IAttachConstants.ATTACH_PNG, os);
		} catch (IOException e) {
			logger.error("PDF生成图异常!",e.getMessage());
		}
		return  os.toByteArray();
	}
	/**
	 * 设置缩略图宽高
	 * @param pdfGenerateImg 
	 * @author haocs
	 * @throws HDException 
	 * @date 2019年8月12日
	 */
	private static BufferedImage thumbnailImg(InputStream pdfGenerateImg) throws HDException {

		try {

			BufferedImage img = ImageIO.read(pdfGenerateImg);
			int width = img.getWidth();
			int height = img.getHeight();
			int available = pdfGenerateImg.available();
			Builder<BufferedImage>  builder = null ;
			if ((available>0 && available >= IAttachConstants.ATTACH_SIZE) 
					|| width > IAttachConstants.FASTDFS_WIDTH 
					|| height>IAttachConstants.FASTDFS_HEIGHT) {
				builder  = Thumbnails.of(img).size(IAttachConstants.FASTDFS_WIDTH, IAttachConstants.FASTDFS_HEIGHT);
			}else {
				builder = Thumbnails.of(img).size(width, height);
			}
			return builder.outputFormat(IAttachConstants.ATTACH_PNG).asBufferedImage();
		} catch (IOException e) {
			logger.error("设置缩略图异常!",e);
			throw new HDException("设置缩略图尺寸异常!");
		}
	}
	/**
	 * 视频缩略图
	 * @param dfs
	 * @param input
	 * @param fileType
	 * @param fileName
	 * @return
	 * @throws HDException
	 * @throws IOException 
	 * @author haocs
	 * @date 2019年8月9日
	 */
	public static byte[] logicVideoThumbnail(AttachDfsVO dfs,AttachParamVO attachParamVO,String fileName) throws HDException{
		 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			InputStream previewPic = VideoUtils.getPreviewPic(attachParamVO.getMultipartFile().getInputStream(),fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase());
			if(previewPic==null) return null;
			BufferedImage img = thumbnailImg(previewPic);
			ImageIO.write(img, IAttachConstants.ATTACH_PNG, os);
		} catch (IOException e) {
			logger.error("视频缩略图异常！",e);
			throw new HDException("视频缩略图异常！");
		}
		return os.toByteArray();
	}
}
