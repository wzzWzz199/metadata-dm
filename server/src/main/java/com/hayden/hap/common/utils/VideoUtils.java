package com.hayden.hap.common.utils;

import com.hayden.hap.common.attach.itf.IAttachConstants;
import com.hayden.hap.dbop.exception.HDException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 视频工具类 
 * 
 * @author 甄建廷
 * @date 2018年6月8日
 */
public class VideoUtils {

	private static final Logger logger = LoggerFactory.getLogger(VideoUtils.class);

	public static Set<String> videoExt = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(".avi");
			add(".mp4");
			add(".flv");
			add(".mkv");
			add(".mpg");
			add(".wmv");
			add(".rmvb");
			add(".mov");
			add(".3gp");
		}
	};

	/**
	 * 截取视频缩略图,支持avi、mp4、flv、mkv、mpg、wmv、rmvb、mov、3gp等视频文件
	 * 
	 * @param videoPath
	 *            视频路径 可以是本地路径或网络路径
	 * @return 返回缩略图临时文件地址 {user.home}/temp/thumbnail/xxxxx.jpg
	 * @author zhenjianting
	 * @date 2018年6月8日
	 */
	public static String getPreviewPic(String videoPath) {
		FFmpegFrameGrabber ff;
		File targetFile = null;
		try {
			ff = FFmpegFrameGrabber.createDefault(videoPath);
			BufferedImage img = getPreviewPic(ff);

			String thumbnailPath = System.getProperty("user.home") + "/temp/thumbnail/" + UUID.randomUUID() + ".jpg";
			targetFile = new File(thumbnailPath);
			if (!targetFile.getParentFile().exists())
				targetFile.getParentFile().mkdirs();
			if (!targetFile.exists())
				targetFile.createNewFile();
			ImageIO.write(img, "jpg", targetFile);
			return thumbnailPath;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (targetFile != null)
				targetFile.delete();
			return null;
		}
	}

	/**
	 * 截取视频缩略图,支持avi、mp4、flv、mkv、mpg、wmv、rmvb、mov、3gp等视频文件
	 * 
	 * @param is
	 *            视频流
	 * @return 返回缩略图文件流
	 * @author zhenjianting
	 * @date 2018年6月8日
	 */
	public static InputStream getPreviewPic(InputStream is, String ext) {
		try {
			String videoPath = System.getProperty("user.home") + "/temp/video/" + UUID.randomUUID() + ext;
			inputStreamToFile(is, videoPath);
			String img = getPreviewPic(videoPath);
			return new FileInputStream(img);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static void inputStreamToFile(InputStream ins, String videoPath) {
		try {
			File videoFile = new File(videoPath);
			if (!videoFile.getParentFile().exists())
				videoFile.getParentFile().mkdirs();
			if (!videoFile.exists())
				videoFile.createNewFile();
			OutputStream os = new FileOutputStream(videoFile);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static BufferedImage getPreviewPic(FFmpegFrameGrabber ff)
			throws org.bytedeco.javacv.FrameGrabber.Exception {
		
		ff.start();
		int lenght = ff.getLengthInFrames();
		int i = 0;
		Frame f = null;
		while (i < lenght) {
			f = ff.grabFrame();
			if ((i > 10) && (f.image != null)) {
				break;
			}
			i++;
		}
		int owidth = f.imageWidth;
		int oheight = f.imageHeight;
		int width = 800;
		int height = (int) (((double) width / owidth) * oheight);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Java2DFrameConverter converter = new Java2DFrameConverter();
		BufferedImage image = converter.getBufferedImage(f);
		bi.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

		ff.stop();
		
		return bi;
	}
	/**
	 * 第一帧原图
	 * @param ff
	 * @return
	 * @throws org.bytedeco.javacv.FrameGrabber.Exception 
	 * @author haocs
	 * @date 2019年8月12日
	 */
	private static BufferedImage getPreviewBySourcePic(FFmpegFrameGrabber ff)
			throws org.bytedeco.javacv.FrameGrabber.Exception {
		
		ff.start();
		int lenght = ff.getLengthInFrames();
		int i = 0;
		Frame f = null;
		while (i < lenght) {
			// 过滤前10帧，避免出现全黑的图片 
			f = ff.grabFrame();
			if(i> 10 && f.image!=null) {
				break;
			}
			i++;
		}
		int width = f.imageWidth;
		int height = f.imageHeight;
		if(width <=0 || height <=0) return null; 
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Java2DFrameConverter converter = new Java2DFrameConverter();
		BufferedImage image = converter.getBufferedImage(f);
		bi.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
		ff.stop();
		return bi;
	}
	/**
	 * 
	 * @param videoPath
	 * @return 
	 * @author haocs
	 * @throws HDException 
	 * @throws FileNotFoundException 
	 * @date 2019年8月13日
	 */
	public static InputStream getPreviewPicByThumbnail(InputStream inputStream) throws HDException, FileNotFoundException {
		FFmpegFrameGrabber ff = null;
		File targetFile = null;
		String thumbnailPath = null;
		try {
			ff = new FFmpegFrameGrabber(inputStream);
			BufferedImage img = getPreviewBySourcePic(ff);
			if(img==null) return null;

			thumbnailPath = System.getProperty("user.home") + "/temp/thumbnail/" + UUID.randomUUID() + ".png";
			targetFile = new File(thumbnailPath);
			if (!targetFile.getParentFile().exists())
				targetFile.getParentFile().mkdirs();
			if (!targetFile.exists())
				targetFile.createNewFile();
			ImageIO.write(img, IAttachConstants.ATTACH_PNG, targetFile);
			return new FileInputStream(thumbnailPath);
		} catch (Exception e) {
			if (targetFile != null)
				targetFile.delete();
			throw new HDException("生成缩略图失败!",e);
		}finally {
			if(ff!=null)
				try {
					ff.close();
				} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
					throw new HDException("关闭错误!",e);
				}
		}
	}
	public static void main(String[] args) throws IOException, HDException {

		File file = new File("C:\\Users\\haocs\\Desktop\\TODAY\\bgi\\VID_20200319_111615.mp4");
		FileInputStream input = new FileInputStream(file);
		
		getPreviewPicByThumbnail(input);
		
	}
}
