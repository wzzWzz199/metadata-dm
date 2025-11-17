package com.hayden.hap.common.entity.button;

/**
 * 
 * @author zhangfeng
 * @date 2018年1月25日
 */
public class ButtonPCVO extends ButtonVO {

	private static final long serialVersionUID = 1L;

	public ButtonPCVO() {
		super("SY_FORM_BUTTON");
	}
	
	public ButtonPCVO(String btn_code, String btn_name) {
		super("SY_FORM_BUTTON");
		
		super.setBtn_code(btn_code);
		super.setBtn_name(btn_name);		
	}

    private String btn_hotkey;

    private String btn_event;

    private Integer btn_event_order;

    private String btn_css;

    private Integer btn_history;

    private String btn_property;

    private String btn_show_group;
    
    private Integer btn_is_default;

    private Integer product_flag;
    
    
	public Integer getProduct_flag() {
		return product_flag;
	}

	public void setProduct_flag(Integer product_flag) {
		this.product_flag = product_flag;
	}

	public String getBtn_hotkey() {
		return btn_hotkey;
	}

	public void setBtn_hotkey(String btn_hotkey) {
		this.btn_hotkey = btn_hotkey;
	}

	public String getBtn_event() {
		return btn_event;
	}

	public void setBtn_event(String btn_event) {
		this.btn_event = btn_event;
	}

	public Integer getBtn_event_order() {
		return btn_event_order;
	}

	public void setBtn_event_order(Integer btn_event_order) {
		this.btn_event_order = btn_event_order;
	}

	public String getBtn_css() {
		return btn_css;
	}

	public void setBtn_css(String btn_css) {
		this.btn_css = btn_css;
	}

	public Integer getBtn_history() {
		return btn_history;
	}

	public void setBtn_history(Integer btn_history) {
		this.btn_history = btn_history;
	}

	public String getBtn_property() {
		return btn_property;
	}

	public void setBtn_property(String btn_property) {
		this.btn_property = btn_property;
	}

	public String getBtn_show_group() {
		return btn_show_group;
	}

	public void setBtn_show_group(String btn_show_group) {
		this.btn_show_group = btn_show_group;
	}

	public Integer getBtn_is_default() {
		return btn_is_default;
	}

	public void setBtn_is_default(Integer btn_is_default) {
		this.btn_is_default = btn_is_default;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
