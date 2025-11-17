package com.hayden.hap.common.button.entity;


/**
 * 
 * @author zhangfeng
 * @date 2018年1月23日
 */
public class ButtonMVO extends ButtonVO {

	private static final long serialVersionUID = 1L;

    public ButtonMVO() {
        super("SY_FORM_BUTTON_MOBILE");
    }
    
    public ButtonMVO(String btnCode, String btnName) {
    	super("SY_FORM_BUTTON_MOBILE");
    	setBtn_code(btnCode);
    	setBtn_name(btnName);      
    }
    
    /**
     * 按钮图标
     */
    private String btn_img;

	public String getBtn_img() {
		return btn_img;
	}

	public void setBtn_img(String btn_img) {
		this.btn_img = btn_img;
	}
}
