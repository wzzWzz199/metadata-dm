package com.hayden.hap.common.form.entity;

import com.hayden.hap.common.dict.entity.DictDataWarperVO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年10月21日
 */
public class GridColumnVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 列显示名称
	 */
	private String label;
	
	/**
	 * 列显示字段名
	 */
	private String name;
	
	/**
	 * 列表列宽
	 */
	private String width;
	
	/**
	 * 列是否可编辑
	 */
	private String editable = "false";
	
	/**
	 * 编辑类型
	 */
	private String edittype;
	
	/**
	 * 编辑特征
	 */
	private Map<String,String> editoptions;
	
	/**
	 * 字典数据
	 */
	private List<DictDataWarperVO> dictList;
	
	/**
	 * 输入设定
	 */
	private String fitem_input_config;
	
	/**
	 * 格式化方法(javascript)字符串
	 */
	private String formatter;
	
	/**
	 * 反格式化方法(javascript)字符串
	 */
	private String unformat;
	
	/**
	 * 单元格URL
	 */
	private String url;
	
	/**
	 * 格式化首选项
	 */
	private String formatoptions;
	
	/**
	 * 列表样式（其实只是单元格内容条件颜色）
	 */
	private String cellColor;
	
	/**
	 * 是否可排序(之所以用字符串类型，因为这个属性值只是用来拼装js对象属性，简化前端的代码，下边“是否隐藏”同理)
	 */
	private String sortable = "true";
	
	/**
	 * 是否隐藏列
	 */
	private String hidden = "false";

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public List<DictDataWarperVO> getDictList() {
		return dictList;
	}

	public void setDictList(List<DictDataWarperVO> dictList) {
		this.dictList = dictList;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public String getFitem_input_config() {
		return fitem_input_config;
	}

	public void setFitem_input_config(String fitem_input_config) {
		this.fitem_input_config = fitem_input_config;
	}

	public String getEdittype() {
		return edittype;
	}

	public void setEdittype(String edittype) {
		this.edittype = edittype;
	}

	public Map<String, String> getEditoptions() {
		return editoptions;
	}

	public void setEditoptions(Map<String, String> editoptions) {
		this.editoptions = editoptions;
	}

	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getUnformat() {
		return unformat;
	}

	public void setUnformat(String unformat) {
		this.unformat = unformat;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFormatoptions() {
		return formatoptions;
	}

	public void setFormatoptions(String formatoptions) {
		this.formatoptions = formatoptions;
	}

	public String getCellColor() {
		return cellColor;
	}

	public void setCellColor(String cellColor) {
		this.cellColor = cellColor;
	}

	public String getSortable() {
		return sortable;
	}

	public void setSortable(String sortable) {
		this.sortable = sortable;
	}

	public String getHidden() {
		return hidden;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
}
