package com.hayden.hap.common.export.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordVO {
	public final static int alignment_left = 1;
	public final static int alignment_center = 2;
	public final static int alignment_right = 3;

	public String name;// 导出的文件名，不设置使用时间戳作为文件名
	public String wordExt = "docx";// 导出的文件扩展名
	public Integer fontSizeDefault = 10;// 默认字号大小
	public String fontFamilyDefault = "宋体";// 默认字体
	public Boolean boldDefault = false;// 默认是否加粗
	private Integer alignmentDefault = alignment_left;// 默认对齐方式
	private Integer imgMaxWidthDefault = 500;// 图片最大宽度，null不限制
	private Integer imgMaxHeightDefault = null;// 图片最大高度，null不限制
	private List<WordItemVO> items = new ArrayList<>();
	private Map<Integer, Integer[]> numStartMap = new HashMap<>();// key:序号的id，value：各级序号的开始

	public Integer getImgMaxWidthDefault() {
		return imgMaxWidthDefault;
	}

	public void setImgMaxWidthDefault(Integer imgMaxWidthDefault) {
		this.imgMaxWidthDefault = imgMaxWidthDefault;
	}

	public Integer getImgMaxHeightDefault() {
		return imgMaxHeightDefault;
	}

	public void setImgMaxHeightDefault(Integer imgMaxHeightDefault) {
		this.imgMaxHeightDefault = imgMaxHeightDefault;
	}

	public Integer getAlignmentDefault() {
		return alignmentDefault;
	}

	public void setAlignmentDefault(Integer alignmentDefault) {
		this.alignmentDefault = alignmentDefault;
	}

	public String getWordExt() {
		return wordExt;
	}

	public void setWordExt(String wordExt) {
		this.wordExt = wordExt;
	}

	public Map<Integer, Integer[]> getNumStartMap() {
		return numStartMap;
	}

	public void setNumStartMap(Map<Integer, Integer[]> numStartMap) {
		this.numStartMap = numStartMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFontSizeDefault() {
		return fontSizeDefault;
	}

	public void setFontSizeDefault(Integer fontSizeDefault) {
		this.fontSizeDefault = fontSizeDefault;
	}

	public String getFontFamilyDefault() {
		return fontFamilyDefault;
	}

	public void setFontFamilyDefault(String fontFamilyDefault) {
		this.fontFamilyDefault = fontFamilyDefault;
	}

	public Boolean getBoldDefault() {
		return boldDefault;
	}

	public void setBoldDefault(Boolean boldDefault) {
		this.boldDefault = boldDefault;
	}

	public List<WordItemVO> getItems() {
		return items;
	}

	public void setItems(List<WordItemVO> items) {
		this.items = items;
	}

	public void fillItemsParent() {
		for (WordItemVO wordItemVO : items) {
			wordItemVO.setParent(this);
		}
	}
}
