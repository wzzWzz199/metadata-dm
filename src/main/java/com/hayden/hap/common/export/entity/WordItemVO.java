package com.hayden.hap.common.export.entity;

public class WordItemVO {
	private String text;// 输出文本內容
	private String img;// 输出图片,在文本下一行显示
	private Integer left;// 缩进距离，设置序号可以不设置此值
	private Integer numLevel;// 需要级别值为 0,1,2。一级：一、二、三;二级：1,2,3;三级：A,B,C
	private Integer numTemplateId;// 序号模板id
	private Integer outline;// 大纲级别从1开始,不设置不在大纲显示
	private Integer alignment;// 对齐方式， 默认WordVO.alignment_left
	private Integer fontSize;// 字号大小
	private String fontFamily;// 字体
	private Boolean bold;// 加粗
	private WordVO parent;// 父级引用
	private Integer imgMaxWidth;// 图片最大宽度，null不限制
	private Integer imgMaxHeight;// 图片最大高度，null不限制
	
	public Integer getImgMaxWidth() {
		if (imgMaxWidth == null && parent != null)
			imgMaxWidth = parent.getImgMaxWidthDefault();
		return imgMaxWidth;
	}

	public void setImgMaxWidth(Integer imgMaxWidth) {
		this.imgMaxWidth = imgMaxWidth;
	}

	public Integer getImgMaxHeight() {
		if (imgMaxHeight == null && parent != null)
			imgMaxHeight = parent.getImgMaxHeightDefault();
		return imgMaxHeight;
	}

	public void setImgMaxHeight(Integer imgMaxHeight) {
		this.imgMaxHeight = imgMaxHeight;
	}

	public WordVO getParent() {
		return parent;
	}

	public void setParent(WordVO parent) {
		this.parent = parent;
	}

	public Integer getNumTemplateId() {
		return numTemplateId;
	}

	public void setNumTemplateId(Integer numTemplateId) {
		this.numTemplateId = numTemplateId;
	}

	public Integer getFontSize() {
		if (fontSize == null && parent != null)
			fontSize = parent.getFontSizeDefault();
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontFamily() {
		if (fontFamily == null && parent != null)
			fontFamily = parent.getFontFamilyDefault();
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Boolean getBold() {
		if (bold == null && parent != null)
			bold = parent.getBoldDefault();
		return bold;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;
	}
 
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Integer getOutline() {
		return outline;
	}

	public void setOutline(Integer outline) {
		this.outline = outline;
	}

	public Integer getLeft() {
		return left;
	}

	public void setLeft(Integer left) {
		this.left = left;
	}

	public Integer getNumLevel() {
		return numLevel;
	}

	public void setNumLevel(Integer numLevel) {
		this.numLevel = numLevel;
	}

	public Integer getAlignment() {
		if (alignment == null && parent != null)
			alignment = parent.getAlignmentDefault();
		return alignment;
	}

	public void setAlignment(Integer alignment) {
		this.alignment = alignment;
	}

}
