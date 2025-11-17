package com.hayden.hap.common.formmgr.entity;

import java.io.Serializable;

/**
 * 
 * @author yinbinchen
 *
 */
public class TreeToDataField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 指定树结构主键字段
	 */
	private String keyFiled;
	
	/**
	 * 树结构父级字段
	 */
	private String parentFiled;

	public String getKeyFiled() {
		return keyFiled;
	}

	public void setKeyFiled(String keyFiled) {
		this.keyFiled = keyFiled;
	}

	public String getParentFiled() {
		return parentFiled;
	}

	public void setParentFiled(String parentFiled) {
		this.parentFiled = parentFiled;
	}

	public TreeToDataField(String keyFiled, String parentFiled) {
		this.keyFiled = keyFiled;
		this.parentFiled = parentFiled;
	}

	public TreeToDataField() {
		// TODO Auto-generated constructor stub
	}
}
