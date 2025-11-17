package com.hayden.hap.common.formmgr.inputconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 按钮输入设定VO
 * @author zhangfeng
 * @date 2017年7月27日
 */
@JsonInclude(Include.NON_EMPTY)
public class ButtonInputConfigVO {
	
	/**
	 * 请求方法 get/post
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String method = "get";
	
	/**
	 * 要提交的列表数据
	 * 整行or主键or不提交列表数据or功能选择，默认提交主键（"pk"/"row"/"no"/"selector/upload"）
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String listdata = "pk";
	
	/**
	 * 是否允许不选中记录
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean isallownoselect = false;
	
	/**
	 * 是否多选
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private boolean ismulti = false;
	
	/**
	 * 要提交的卡片数据
	 * 主键or卡片数据or功能选择（"pk"/"vo"/"selector"）
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String carddata = "vo";
	
	/**
	 * 像 查询选择，选完数据后要提交的按钮编码
	 */
	private String okbtncode;
	
	/**
	 * 选完数据后，提交请求的方法
	 */
	@JsonInclude(Include.NON_DEFAULT)
	private String okmethod = "post";
	
	/**
	 * 功能选择的输入设定
	 */
	private QueryselectorInputConfigVO inputconfig;
	
	/**
	 * 确认信息
	 */
	@JsonInclude(Include.NON_EMPTY)
	private String confirm;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getListdata() {
		return listdata;
	}

	public void setListdata(String listdata) {
		this.listdata = listdata;
	}

	public boolean isIsallownoselect() {
		return isallownoselect;
	}

	public void setIsallownoselect(boolean isallownoselect) {
		this.isallownoselect = isallownoselect;
	}

	public boolean isIsmulti() {
		return ismulti;
	}

	public void setIsmulti(boolean ismulti) {
		this.ismulti = ismulti;
	}

	public String getCarddata() {
		return carddata;
	}

	public void setCarddata(String carddata) {
		this.carddata = carddata;
	}

	public String getOkbtncode() {
		return okbtncode;
	}

	public void setOkbtncode(String okbtncode) {
		this.okbtncode = okbtncode;
	}

	public String getOkmethod() {
		return okmethod;
	}

	public void setOkmethod(String okmethod) {
		this.okmethod = okmethod;
	}

	public QueryselectorInputConfigVO getInputconfig() {
		return inputconfig;
	}

	public void setInputconfig(QueryselectorInputConfigVO inputconfig) {
		this.inputconfig = inputconfig;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
}
