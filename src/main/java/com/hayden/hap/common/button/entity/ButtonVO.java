package com.hayden.hap.common.button.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hayden.hap.common.common.entity.BaseVO;

public class ButtonVO extends BaseVO {
  
	private static final long serialVersionUID = 1L;

    private Long btn_id;

    private Long formid;

	private String form_code;
	  
    private String btn_code;
   
    private String btn_name;
    
    private Integer btn_order;

    private String btn_show_control;

    private String btn_type;

    private String btn_group;

    private Integer btn_isenable;

    private Integer btn_history;

    private String btn_param;
    
    private String btn_comment;

    private String btn_show_group;
    
    private String btn_inputconfig;
    
    /**
     * 非持久化字段
     */
    @JsonIgnore
    private String func_code;
    
    public ButtonVO(String tableName) {
        super(tableName);
    }
    
    public ButtonVO(String btn_code, String btn_name) {
    	this.btn_code = btn_code;
    	this.btn_name = btn_name;
    }

	public Long getBtn_id() {
		return btn_id;
	}

	public void setBtn_id(Long btn_id) {
		this.btn_id = btn_id;
	}

	public Long getFormid() {
		return formid;
	}

	public void setFormid(Long formid) {
		this.formid = formid;
	}

	public String getForm_code() {
		return form_code;
	}

	public void setForm_code(String form_code) {
		this.form_code = form_code;
	}

	public String getBtn_code() {
		return btn_code;
	}

	public void setBtn_code(String btn_code) {
		this.btn_code = btn_code;
	}

	public String getBtn_name() {
		return btn_name;
	}

	public void setBtn_name(String btn_name) {
		this.btn_name = btn_name;
	}

	public Integer getBtn_order() {
		return btn_order;
	}

	public void setBtn_order(Integer btn_order) {
		this.btn_order = btn_order;
	}

	public String getBtn_show_control() {
		return btn_show_control;
	}

	public void setBtn_show_control(String btn_show_control) {
		this.btn_show_control = btn_show_control;
	}

	public String getBtn_type() {
		return btn_type;
	}

	public void setBtn_type(String btn_type) {
		this.btn_type = btn_type;
	}

	public String getBtn_group() {
		return btn_group;
	}

	public void setBtn_group(String btn_group) {
		this.btn_group = btn_group;
	}

	public Integer getBtn_isenable() {
		return btn_isenable;
	}

	public void setBtn_isenable(Integer btn_isenable) {
		this.btn_isenable = btn_isenable;
	}

	public Integer getBtn_history() {
		return btn_history;
	}

	public void setBtn_history(Integer btn_history) {
		this.btn_history = btn_history;
	}

	public String getBtn_param() {
		return btn_param;
	}

	public void setBtn_param(String btn_param) {
		this.btn_param = btn_param;
	}

	public String getBtn_comment() {
		return btn_comment;
	}

	public void setBtn_comment(String btn_comment) {
		this.btn_comment = btn_comment;
	}

	public String getBtn_show_group() {
		return btn_show_group;
	}

	public void setBtn_show_group(String btn_show_group) {
		this.btn_show_group = btn_show_group;
	}

	public String getBtn_inputconfig() {
		return btn_inputconfig;
	}

	public void setBtn_inputconfig(String btn_inputconfig) {
		this.btn_inputconfig = btn_inputconfig;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFunc_code() {
		return func_code;
	}

	public void setFunc_code(String func_code) {
		this.func_code = func_code;
	}

}