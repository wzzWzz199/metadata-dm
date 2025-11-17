package com.hayden.hap.common.entity.form;

import com.hayden.hap.common.utils.table.annotation.Column;
import com.hayden.hap.common.utils.table.annotation.Table;

/**
 * 表单字段
 * @author zhangfeng
 * @date 2015年6月3日
 */
@Table(value="SY_FORM_ITEM",desc="表单字段表")
public class FormItemPCVOForExport extends FormItemVO implements Cloneable{

	private static final long serialVersionUID = 1L;

	public FormItemPCVOForExport(){
		super("SY_FORM_ITEM");
	}
	
	
	
	
	
	/**
	 * 简易连接
	 */
	@Column(type="varchar",length=200)
	private String fitem_easy_link;	
	
	/**
	 * 输入框宽度
	 */
	@Column(type="integer",length=10)
	private Integer fitem_card_width;
	
	/**
	 * 列表列宽度
	 */
	@Column(type="varchar",length=10)
	private String fitem_list_width;
	
	/**
	 * 列表自动换行
	 */
	@Column(type="integer",length=2)
	private Integer fitem_list_col_wrap;
		
	/**
	 * 单元格URL
	 */
	@Column(type="varchar",length=200)
	private String fitem_list_url;
	
	/**
	 * 卡片中禁用
	 */
	@Column(type="integer",length=2)
	private Integer fitem_card_unuse;	
	
	/**
	 * 是否快速查寻项
	 */
	@Column(type="integer",length=2)
	private Integer fitem_quick_query;
	
	/**
	 * 是否快速列表列
	 */
	@Column(type="integer",length=2)
	private Integer fitem_quick_list;	
	
	/**
	 * 是否支持批量编辑
	 */
	@Column(type="integer",length=2)
	private Integer fitem_batch;
	
	/**
	 * 是否在列表中显示
	 */
	@Column(type="integer",length=2)
	private Integer fitem_show_list;
	
	/**
	 * 字段扩展属性
	 */
	@Column(type="varchar",length=100)
	private String fitem_card_property;
	
	
	/**
	 * 列表表头顺序
	 */
	private Integer fitem_column_order;
	
	/**
	 * 是否允许排序
	 */
	private Integer fitem_sortable; 	
	
	/**
	 * 指定排序字段
	 */
	private String fitem_order_item;
	
	/**
	 * 单元格文字对齐方式
	 */
	private String fitem_text_align;
		

	public FormItemPCVO clone() {    
        try {    
            return (FormItemPCVO)super.clone();    
        } catch (CloneNotSupportedException e) {    
            return null;    
        }    
    }

	

	public String getFitem_easy_link() {
		return fitem_easy_link;
	}

	public void setFitem_easy_link(String fitem_easy_link) {
		this.fitem_easy_link = fitem_easy_link;
	}

	

	

	public Integer getFitem_card_width() {
		return fitem_card_width;
	}

	public void setFitem_card_width(Integer fitem_card_width) {
		this.fitem_card_width = fitem_card_width;
	}

	public String getFitem_list_width() {
		return fitem_list_width;
	}

	public void setFitem_list_width(String fitem_list_width) {
		this.fitem_list_width = fitem_list_width;
	}

	public Integer getFitem_list_col_wrap() {
		return fitem_list_col_wrap;
	}

	public void setFitem_list_col_wrap(Integer fitem_list_col_wrap) {
		this.fitem_list_col_wrap = fitem_list_col_wrap;
	}

	public String getFitem_list_url() {
		return fitem_list_url;
	}

	public void setFitem_list_url(String fitem_list_url) {
		this.fitem_list_url = fitem_list_url;
	}

	public Integer getFitem_card_unuse() {
		return fitem_card_unuse;
	}

	public void setFitem_card_unuse(Integer fitem_card_unuse) {
		this.fitem_card_unuse = fitem_card_unuse;
	}

	

	public Integer getFitem_quick_query() {
		return fitem_quick_query;
	}

	public void setFitem_quick_query(Integer fitem_quick_query) {
		this.fitem_quick_query = fitem_quick_query;
	}

	public Integer getFitem_quick_list() {
		return fitem_quick_list;
	}

	public void setFitem_quick_list(Integer fitem_quick_list) {
		this.fitem_quick_list = fitem_quick_list;
	}

	public Integer getFitem_batch() {
		return fitem_batch;
	}

	public void setFitem_batch(Integer fitem_batch) {
		this.fitem_batch = fitem_batch;
	}

	public Integer getFitem_show_list() {
		return fitem_show_list;
	}

	public void setFitem_show_list(Integer fitem_show_list) {
		this.fitem_show_list = fitem_show_list;
	}

	public String getFitem_card_property() {
		return fitem_card_property;
	}

	public void setFitem_card_property(String fitem_card_property) {
		this.fitem_card_property = fitem_card_property;
	}

	

	public Integer getFitem_column_order() {
		return fitem_column_order;
	}

	public void setFitem_column_order(Integer fitem_column_order) {
		this.fitem_column_order = fitem_column_order;
	}

	public Integer getFitem_sortable() {
		return fitem_sortable;
	}

	public void setFitem_sortable(Integer fitem_sortable) {
		this.fitem_sortable = fitem_sortable;
	}

	

	public String getFitem_order_item() {
		return fitem_order_item;
	}

	public void setFitem_order_item(String fitem_order_item) {
		this.fitem_order_item = fitem_order_item;
	}

	public String getFitem_text_align() {
		return fitem_text_align;
	}

	public void setFitem_text_align(String fitem_text_align) {
		this.fitem_text_align = fitem_text_align;
	}
}
