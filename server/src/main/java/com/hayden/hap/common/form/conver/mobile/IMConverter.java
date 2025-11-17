package com.hayden.hap.common.form.conver.mobile;

import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.form.conver.IConverter;
import com.hayden.hap.common.form.entity.FormItemMVO;
import com.hayden.hap.common.form.entity.FormMVO;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2018年5月10日
 */
public interface IMConverter extends IConverter{
	
	String getFitem_list_show_type();
	String getFitem_list_show_type_quick();
	Integer getFitem_list_show_title();
	Integer getFitem_list_show_title_quick();
	String getFitem_placeholder();

	void setColumnVO(TableColumnVO columnVO);
	void setFormVO(FormMVO formVO);
	List<FormItemMVO> getItems();
}
