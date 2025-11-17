package com.hayden.hap.common.form.entity;


public class FormItemMVO extends FormItemVO{

	private static final long serialVersionUID = 1L;

	public FormItemMVO() {
		super("SY_FORM_ITEM_MOBILE");
	}

	public FormItemMVO clone() {    
		try {    
			return (FormItemMVO)super.clone();    
		} catch (CloneNotSupportedException e) {    
			return null;    
		}    
	}

	
	private String fitem_list_show_type;
	
	
	private String fitem_relation_func;


	private String fitem_list_show_type_quick;


	private Integer fitem_list_show_title;


	private Integer fitem_list_show_title_quick;


	private String fitem_placeholder;


	private String fitem_text_align;

	public String getFitem_list_show_type() {
		return fitem_list_show_type;
	}

	public void setFitem_list_show_type(String fitem_list_show_type) {
		this.fitem_list_show_type = fitem_list_show_type;
	}

	public String getFitem_list_show_type_quick() {
		return fitem_list_show_type_quick;
	}

	public void setFitem_list_show_type_quick(String fitem_list_show_type_quick) {
		this.fitem_list_show_type_quick = fitem_list_show_type_quick;
	}

	public Integer getFitem_list_show_title() {
		return fitem_list_show_title;
	}

	public void setFitem_list_show_title(Integer fitem_list_show_title) {
		this.fitem_list_show_title = fitem_list_show_title;
	}

	public Integer getFitem_list_show_title_quick() {
		return fitem_list_show_title_quick;
	}

	public void setFitem_list_show_title_quick(Integer fitem_list_show_title_quick) {
		this.fitem_list_show_title_quick = fitem_list_show_title_quick;
	}

	public String getFitem_placeholder() {
		return fitem_placeholder;
	}

	public void setFitem_placeholder(String fitem_placeholder) {
		this.fitem_placeholder = fitem_placeholder;
	}

	public String getFitem_text_align() {
		return fitem_text_align;
	}

	public void setFitem_text_align(String fitem_text_align) {
		this.fitem_text_align = fitem_text_align;
	}

	public String getFitem_relation_func() {
		return fitem_relation_func;
	}

	public void setFitem_relation_func(String fitem_relation_func) {
		this.fitem_relation_func = fitem_relation_func;
	}

 


}
