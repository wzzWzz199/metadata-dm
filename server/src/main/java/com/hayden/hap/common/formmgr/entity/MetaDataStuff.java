package com.hayden.hap.common.formmgr.entity;

/**
 * 构造结构数据的材料
 * @author zhangfeng
 * @date 2017年6月13日
 */
public class MetaDataStuff {

	/**
	 * 列定义，如：{{"btn_code","按钮编码"},{"btn_name","按钮名称"}}
	 */
	private String[][] columns;
	
	/**
	 * 字典编码
	 */
	private String[] dictcodes;

	public String[][] getColumns() {
		return columns;
	}

	public void setColumns(String[][] columns) {
		this.columns = columns;
	}

	public String[] getDictcodes() {
		return dictcodes;
	}

	public void setDictcodes(String[] dictcodes) {
		this.dictcodes = dictcodes;
	}
}
