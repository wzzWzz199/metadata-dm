package com.hayden.hap.common.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 非字段查询选择参数
 * @author zhangfeng
 * @date 2016年1月23日
 */
public class FuncSelectorParamVO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String btnCode;
	
	private String extWhere;
	
	private String multi;
	
	private Integer isShowView;
	
	private String btnURL;
	
	private List<String> selects;

	public String getBtnCode() {
		return btnCode;
	}

	public void setBtnCode(String btnCode) {
		this.btnCode = btnCode;
	}

	public String getExtWhere() {
		return extWhere;
	}

	public void setExtWhere(String extWhere) {
		this.extWhere = extWhere;
	}

	public String getMulti() {
		return multi;
	}

	public void setMulti(String multi) {
		this.multi = multi;
	}

	public Integer getIsShowView() {
		return isShowView;
	}

	public void setIsShowView(Integer isShowView) {
		this.isShowView = isShowView;
	}

	public String getBtnURL() {
		return btnURL;
	}

	public void setBtnURL(String btnURL) {
		this.btnURL = btnURL;
	}

	public List<String> getSelects() {
		return selects;
	}

	public void setSelects(List<String> selects) {
		this.selects = selects;
	}
}
