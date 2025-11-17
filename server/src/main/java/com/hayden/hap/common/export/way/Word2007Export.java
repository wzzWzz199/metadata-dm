package com.hayden.hap.common.export.way;

import com.hayden.hap.common.attach.itf.IAttachMethodService;
import com.hayden.hap.common.export.entity.WordItemVO;
import com.hayden.hap.common.export.entity.WordVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Word2007Export {
	private static final Logger logger = LoggerFactory.getLogger(Word2007Export.class);

	private Long tenantid;
	private Long downImgTimeTotal = 0L;
	private Long zipImgTimeTotal = 0L;

	public Long getTenantid() {
		return tenantid;
	}

	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}

	public String buildWord(WordVO word) {
		long start = System.currentTimeMillis();
		String ret = System.getProperty("user.home") + "/temp/word/" + System.currentTimeMillis() + ".docx";
		try {
			word.fillItemsParent();
			XWPFDocument doc = new XWPFDocument();
			this.addNumbering(doc, word.getNumStartMap());
			List<WordItemVO> items = word.getItems();
			for (WordItemVO item : items) {
				this.addRun(doc, item);
			}
			File f = new File(ret);
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(ret);
			doc.write(out);
			out.close();
			doc.close();
			logger.info("导出word总耗时：" + (System.currentTimeMillis() - start) + ";下载图片耗时：" + downImgTimeTotal + ";压缩图片耗时："
					+ zipImgTimeTotal);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	public String buildWord(WordVO word,String name) {
		long start = System.currentTimeMillis();
		String ret = System.getProperty("user.home") + "/temp/word/" +name+"@"+ System.currentTimeMillis() + ".docx";
		try {
			word.fillItemsParent();
			XWPFDocument doc = new XWPFDocument();
			this.addNumbering(doc, word.getNumStartMap());
			List<WordItemVO> items = word.getItems();
			for (WordItemVO item : items) {
				this.addRun(doc, item);
			}
			File f = new File(ret);
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(ret);
			doc.write(out);
			out.close();
			doc.close();
			logger.info("导出word总耗时：" + (System.currentTimeMillis() - start) + ";下载图片耗时：" + downImgTimeTotal + ";压缩图片耗时："
					+ zipImgTimeTotal);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 
	 * @param doc
	 *            文档对象
	 * @param item
	 *            行模块参数
	 * @author zhenjianting
	 * @date 2018年7月4日
	 */
	public void addRun(XWPFDocument doc, WordItemVO item) {
		XWPFParagraph p = doc.createParagraph();
		XWPFRun r = p.createRun();

		// 设置对齐
		if (item.getAlignment() != null)
			p.setAlignment(ParagraphAlignment.valueOf(item.getAlignment()));

		// 设置缩进
		if (item.getLeft() != null)
			p.setIndentationLeft(item.getLeft());

		// 设置序号层级和序号id
		if (item.getNumLevel() != null && item.getNumTemplateId() != null)
			setNumPr(p, item.getNumLevel(), item.getNumTemplateId());

		// 设置大纲层级
		if (item.getOutline() != null)
			setOutlineLvl(p, item.getOutline());
		// 设置显示内容
		if (item.getText() != null)
			r.setText(item.getText());

		// 设置加粗
		if (item.getBold() != null)
			r.setBold(item.getBold());

		// 设置字体
		if (item.getFontFamily() != null)
			r.setFontFamily(item.getFontFamily());

		// 设置字号
		if (item.getFontSize() != null)
			r.setFontSize(item.getFontSize());

		// 设置图片
		if (item.getImg() != null) {
			if (item.getText() != null) {
				int left = getLeft(p);
				p = doc.createParagraph();
				r = p.createRun();
				p.setSpacingLineRule(LineSpacingRule.AT_LEAST);
				p.getCTP().getPPr().getSpacing().setLine(BigInteger.valueOf(0));
				p.getCTP().getPPr().getSpacing().setAfterLines(BigInteger.valueOf(50));
				p.setIndentationLeft(left + 150);
			} else {
				CTTextAlignment textAlignment = CTTextAlignment.Factory.newInstance();
				textAlignment.xsetVal(STTextAlignment.Factory.newValue("top"));
				p.getCTP().getPPr().setTextAlignment(textAlignment);
			}
			createPicture(doc, r, item.getImg(), item.getImgMaxWidth(), item.getImgMaxHeight(),
					Document.PICTURE_TYPE_JPEG);
		}
	}

	private int getLeft(XWPFParagraph p) {

		int left = p.getIndentationLeft();
		if (left == -1)
			left = 0;
		if (p.getCTP().getPPr() != null) {
			if (p.getCTP().getPPr().getNumPr() != null) {
				CTNumPr num = p.getCTP().getPPr().getNumPr();
				int numid = num.getNumId().getVal().intValue();
				int numlvl = num.getIlvl().getVal().intValue();
				XWPFAbstractNum aNUm = p.getDocument().getNumbering().getAbstractNum(BigInteger.valueOf(numid - 1));
				left += aNUm.getAbstractNum().getLvlArray(numlvl).getPPr().getInd().getLeft().intValue();
			}
		}
		return left;
	}

	private IAttachMethodService attachMethodService = null;

	private Image downloadImg(String img, Integer width, Integer height) {

		try {
			long start = System.currentTimeMillis();
			byte[] data = null;
			if (img.startsWith("/")) {
				if (img.contains("?") && img.contains("&")) {
					String[] arr = img.split("\\?");
					String[] arr1 = arr[1].split("&");
					String attachdataid = arr1[0].substring(arr1[0].indexOf("=") + 1);
					String createDate = arr1[2].substring(arr1[2].indexOf("=") + 1);
					long attid = Long.parseLong(attachdataid);
					if (attachMethodService == null)
						attachMethodService = (IAttachMethodService) AppServiceHelper
								.findBean("attachMethodServiceImpl");
					InputStream is = attachMethodService.getAttachInputStream(attid, tenantid, createDate);
					data = input2byte(is);
				} else {
					InputStream is = new FileInputStream(img);
					data = input2byte(is);
					is.close();
				}
			} else if (img.startsWith("http")) {
				URL url = new URL(img);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");
				InputStream is = conn.getInputStream();
				data = input2byte(is);
				is.close();
				conn.disconnect();
			} else if (img.substring(1).startsWith(":/") || img.substring(1).startsWith(":\\")) {
				InputStream is = new FileInputStream(img);
				data = input2byte(is);
				is.close();
			}
			downImgTimeTotal += (System.currentTimeMillis() - start);
			start = System.currentTimeMillis();
			// 图片压缩
			if (data != null) {
				int newWidth = 0, newHeight = 0;

				ByteArrayInputStream bis1 = new ByteArrayInputStream(data);
				BufferedImage image = ImageIO.read(bis1);
				int h = image.getHeight();
				int w = image.getWidth();
				bis1.close();
				image.flush();

				double scale = 1;
				if (height != null) {
					double wScale = (double) width / w;
					double hScale = (double) height / h;
					scale = wScale > hScale ? hScale : wScale;
				} else {
					scale = (double) width / w;
				}
				if (scale > 1)
					scale = 1;
				newWidth = (int) (w * scale);
				newHeight = (int) (h * scale);

				ByteArrayInputStream bis = new ByteArrayInputStream(data);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Thumbnails.of(bis).scale(scale).outputQuality(0.5f).toOutputStream(bos);
				data = bos.toByteArray();
				bis.close();
				bos.close();

				final int EMU = 9525;
				newWidth *= EMU;
				newHeight *= EMU;

				zipImgTimeTotal += (System.currentTimeMillis() - start);

				Image ret = new Image();
				ret.setWidth(newWidth);
				ret.setHeight(newHeight);

				ret.setData(data);
				return ret;
			}
		} catch (Exception e) {
			logger.error("未找到图片：" + img, e);
		}
		return null;
	}

	public static final byte[] input2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	/**
	 * 设置大纲级别，从1开始
	 * 
	 * @param p
	 *            XWPFParagraph
	 * @param lvl
	 *            大纲级别
	 */
	public void setOutlineLvl(XWPFParagraph p, int lvl) {

		CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
		indentNumber.setVal(BigInteger.valueOf(lvl));
		if (p.getCTP().getPPr() == null)
			p.getCTP().setPPr(CTPPr.Factory.newInstance());
		p.getCTP().getPPr().setOutlineLvl(indentNumber);

	}

	/**
	 * 设置自增序号
	 * 
	 * @param p
	 *            是XWPFParagraph
	 * @param ilvl
	 *            序号层级
	 * @param numId
	 *            需要的id
	 */
	public void setNumPr(XWPFParagraph p, int ilvl, int numId) {

		if (p.getCTP().getPPr() == null)
			p.getCTP().setPPr(CTPPr.Factory.newInstance());

		p.getCTP().getPPr().setNumPr(CTNumPr.Factory.newInstance());

		CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
		indentNumber.setVal(BigInteger.valueOf(ilvl));
		p.getCTP().getPPr().getNumPr().setIlvl(indentNumber);

		indentNumber = CTDecimalNumber.Factory.newInstance();
		indentNumber.setVal(BigInteger.valueOf(numId));
		p.getCTP().getPPr().getNumPr().setNumId(indentNumber);

	}

	/**
	 * 创建需要规则，第一层：一、二、三、 ，第二层：1。 2. 3. ,第三层：A、B、C
	 * 
	 * @param numbering
	 * @param numid
	 *            序号id
	 * @param start
	 *            开始序号
	 */
	public void addNumbering(XWPFDocument doc, Map<Integer, Integer[]> numStartMap) {
		XWPFNumbering numbering = doc.createNumbering();
		for (Entry<Integer, Integer[]> entry : numStartMap.entrySet()) {
			Integer _numid = entry.getKey();
			Integer[] starts = entry.getValue();
			BigInteger abstractnumid = BigInteger.valueOf(_numid - 1);
			BigInteger numid = BigInteger.valueOf(_numid);

			CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();

			addCTLvl(abstractNum, 0, starts[0], "chineseCountingThousand", "%1.");
			addCTLvl(abstractNum, 1, starts[1], "decimal", "%2.");
			addCTLvl(abstractNum, 2, starts[2], "upperLetter", "%3.");

			XWPFAbstractNum abs = new XWPFAbstractNum(abstractNum, numbering);
			abs.getAbstractNum().setAbstractNumId(abstractnumid);
			numbering.addAbstractNum(abs);

			numbering.addNum(abstractnumid, numid);
		}
	}

	public void addCTLvl(CTAbstractNum abstractNum, int level, int startNum, String numfmt, String lvlText) {
		int _hanging = 250;

		CTLvl cTLvl = abstractNum.addNewLvl();
		STDecimalNumber ilvl = STDecimalNumber.Factory.newInstance();
		ilvl.setBigIntegerValue(BigInteger.valueOf(level));
		cTLvl.xsetIlvl(ilvl);

		CTDecimalNumber start = cTLvl.addNewStart();
		start.setVal(BigInteger.valueOf(startNum));
		CTNumFmt numFmt = cTLvl.addNewNumFmt();
		numFmt.setVal(STNumberFormat.Enum.forString(numfmt));
		CTLevelText txt = cTLvl.addNewLvlText();
		txt.setVal(lvlText);
		CTJc jc = cTLvl.addNewLvlJc();
		jc.setVal(STJc.Enum.forString("left"));
		CTPPr ppr = cTLvl.addNewPPr();
		CTInd ind = ppr.addNewInd();
		STTwipsMeasure hanging = STTwipsMeasure.Factory.newInstance();
		hanging.setBigIntegerValue(BigInteger.valueOf(_hanging));
		ind.xsetHanging(hanging);

		STSignedTwipsMeasure left = STSignedTwipsMeasure.Factory.newInstance();
		left.setBigIntegerValue(BigInteger.valueOf(_hanging * level));
		ind.xsetLeft(left);
	}

	public void createPicture(XWPFDocument doc, XWPFRun run, String imgUrl, Integer width, Integer height,
			int picType) {
		try {

			Image image = downloadImg(imgUrl, width, height);
			if (image == null)
				return;

			int id = doc.getNextPicNameNumber(picType);
			String blipId = doc.addPictureData(image.getData(), picType);

			CTInline inline = run.getCTR().addNewDrawing().addNewInline();
			StringBuilder sb = new StringBuilder();
			sb.append("<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">");
			sb.append("   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">");
			sb.append("      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">");
			sb.append("         <pic:nvPicPr>");
			sb.append("            <pic:cNvPr id=\"" + id + "\" name=\"Generated\"/>");
			sb.append("            <pic:cNvPicPr/>");
			sb.append("         </pic:nvPicPr>");
			sb.append("         <pic:blipFill>");
			sb.append("            <a:blip r:embed=\"" + blipId + "\"");
			sb.append("             xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>");
			sb.append("            <a:stretch>");
			sb.append("               <a:fillRect/>");
			sb.append("            </a:stretch>");
			sb.append("         </pic:blipFill>");
			sb.append("         <pic:spPr>");
			sb.append("            <a:xfrm>");
			sb.append("               <a:off x=\"0\" y=\"0\"/>");
			sb.append("               <a:ext cx=\"" + image.getWidth() + "\" cy=\"" + image.getHeight() + "\"/>");
			sb.append("            </a:xfrm>");
			sb.append("            <a:prstGeom prst=\"rect\">");
			sb.append("               <a:avLst/>");
			sb.append("            </a:prstGeom>");
			sb.append("         </pic:spPr>");
			sb.append("      </pic:pic>");
			sb.append("   </a:graphicData>");
			sb.append("</a:graphic>");

			XmlToken xmlToken = null;
			try {
				xmlToken = XmlToken.Factory.parse(sb.toString());
			} catch (XmlException xe) {
				xe.printStackTrace();
			}
			inline.set(xmlToken);

			inline.setDistT(0);
			inline.setDistB(0);
			inline.setDistL(0);
			inline.setDistR(0);

			CTPositiveSize2D extent = inline.addNewExtent();
			extent.setCx(image.getWidth());
			extent.setCy(image.getHeight());

			CTNonVisualDrawingProps docPr = inline.addNewDocPr();
			docPr.setId(id);
			docPr.setName("Picture " + id);
			docPr.setDescr("Generated");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class Image {
		private byte[] data;
		private int width;
		private int height;

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}

	public static WordVO getDemo1() {
		WordVO word = new WordVO();
		word.getNumStartMap().put(1, new Integer[] { 1, 1, 1 });
		word.getNumStartMap().put(2, new Integer[] { 1, 3, 1 });
		word.getNumStartMap().put(3, new Integer[] { 1, 1, 1 });
		word.getNumStartMap().put(4, new Integer[] { 1, 3, 1 });

		WordItemVO item = new WordItemVO();
		item.setText("期末考试练习题（一）");
		item.setAlignment(WordVO.alignment_center);
		item.setFontSize(14);
		item.setBold(true);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("单位：                                           姓名：");
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("选择题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(1);
		item.setNumTemplateId(1);
		item.setNumLevel(0);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("请问天上多少颗星星？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setNumTemplateId(1);
		item.setNumLevel(1);
		item.setImg("http://192.168.6.49:7030/themes/hayden/images/login/qz_cscbg.png");
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("1个。");
		item.setFontSize(9);
		item.setNumTemplateId(1);
		item.setNumLevel(2);
		item.setImg("C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg");

		word.getItems().add(item);

		item = new WordItemVO();
		// item.setText("2个。");
		item.setFontSize(9);
		item.setNumTemplateId(1);
		item.setNumLevel(2);
		item.setImg("C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg");
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("请问天上多少颗月亮？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setNumTemplateId(1);
		item.setNumLevel(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("1个。");
		item.setFontSize(9);
		item.setNumTemplateId(1);
		item.setNumLevel(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("2个。");
		item.setFontSize(9);
		item.setNumTemplateId(1);
		item.setNumLevel(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("判断题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(1);
		item.setNumTemplateId(1);
		item.setNumLevel(0);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("请问天上多少颗星星？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setNumTemplateId(2);
		item.setNumLevel(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("1个。");
		item.setFontSize(9);
		item.setNumTemplateId(2);
		item.setNumLevel(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("2个。");
		item.setFontSize(9);
		item.setNumTemplateId(2);
		item.setNumLevel(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("请问天上多少颗月亮？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setNumTemplateId(2);
		item.setNumLevel(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("1个。");
		item.setFontSize(9);
		item.setNumTemplateId(2);
		item.setNumLevel(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("2个。");
		item.setFontSize(9);
		item.setNumTemplateId(2);
		item.setNumLevel(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("答案");
		item.setAlignment(WordVO.alignment_center);
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("选择题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(2);
		item.setNumTemplateId(3);
		item.setNumLevel(0);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("B");
		item.setFontSize(9);
		item.setNumTemplateId(3);
		item.setNumLevel(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("B");
		item.setFontSize(9);
		item.setNumTemplateId(3);
		item.setNumLevel(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("判断题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(2);
		item.setNumTemplateId(3);
		item.setNumLevel(0);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("A");
		item.setFontSize(9);
		item.setNumTemplateId(4);
		item.setNumLevel(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("A");
		item.setFontSize(9);
		item.setNumTemplateId(4);
		item.setNumLevel(1);
		word.getItems().add(item);

		return word;
	}

	public static WordVO getDemo2() {
		WordVO word = new WordVO();
		int left1 = 300;
		int left2 = 600;

		WordItemVO item = new WordItemVO();
		item.setText("期末考试练习题（一）");
		item.setAlignment(WordVO.alignment_center);
		item.setFontSize(14);
		item.setBold(true);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("单位：                                           姓名：");
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("一、选择题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("1.请问天上多少颗星星？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setLeft(left1);
		item.setImg("http://192.168.6.49:7030/themes/hayden/images/login/qz_cscbg.png");
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("A.1个。");
		item.setFontSize(9);
		item.setLeft(left2);
		item.setImg("C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg");
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("B.2个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("2.请问天上多少颗月亮？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setLeft(left1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("A.1个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("B.2个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("二、判断题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("3.请问天上多少颗星星？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setLeft(left1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("A.1个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("B.2个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("4.请问天上多少颗月亮？（）");
		item.setFontSize(10);
		item.setOutline(2);
		item.setLeft(left1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("A.1个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("B.2个。");
		item.setFontSize(9);
		item.setLeft(left2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("答案");
		item.setAlignment(WordVO.alignment_center);
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("一、选择题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("1.B");
		item.setFontSize(9);
		item.setLeft(left1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("2.B");
		item.setFontSize(9);
		item.setLeft(left1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("二、判断题（共10分）");
		item.setFontSize(14);
		item.setBold(true);
		item.setOutline(2);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("3.A");
		item.setFontSize(9);
		item.setLeft(left1);
		word.getItems().add(item);

		item = new WordItemVO();
		item.setText("4.A");
		item.setFontSize(9);
		item.setLeft(left1);
		word.getItems().add(item);

		return word;
	}

	public static void main(String[] args) {

		try {
			WordVO word = Word2007Export.getDemo1();
			Word2007Export export = new Word2007Export();
			export.setTenantid(0L);
			String path = export.buildWord(word);

			System.out.println(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
