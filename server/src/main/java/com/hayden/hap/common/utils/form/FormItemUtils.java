package com.hayden.hap.common.utils.form;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.utils.VariableUtils;

import java.util.List;

public class FormItemUtils {

	/**
	 * 所有字段
	 */
	public static String ALL_ITEMS = "__all_items";
	
	/**
	 * 哪些字段可能存在系统变量，需要替换的
	 */
	private static final String[] codeArr= new String[] {"fitem_readonly_express","fitem_hide_express",
		"fitem_query_default","fitem_card_default","fitem_cell_color","fitem_display_formula","fitem_edit_formula"};
	
	/**
	 * 改变表单字段属性值<br/>
	 * @param items 表单字段集合<br/>
	 * @param itemCode 表单字段编码<br/>
	 * @param propName 要修改的属性的属性名<br/>
	 * @param value 要改变的值<br/>
	 * @author zhangfeng
	 * @date 2016年8月23日
	 */
	public static void changeValue(List<? extends FormItemVO> items, String itemCode, String propName, Object value) {
		if(ALL_ITEMS.equals(itemCode)) {
			for(FormItemVO itemVO : items) {				
				((AbstractVO)itemVO).set(propName, value);				
			}
			return;
		}
		
		for(FormItemVO itemVO : items) {
			if(itemVO.getFitem_code().equals(itemCode)) {
				((AbstractVO)itemVO).set(propName, value);
			}
		}
	}
	
	/**
	 * 改变表单字段属性值<br/>
	 * @param items
	 * @param itemCodes
	 * @param propName
	 * @param value 
	 * @author zhangfeng
	 * @date 2016年8月23日
	 */
	public static void changeValue(List<FormItemPCVO> items, String[] itemCodes, String propName, Object value) {
		
		out:for(FormItemPCVO itemVO : items) {
			for(String itemCode : itemCodes) {
				if(itemVO.getFitem_code().equals(itemCode)) {
					itemVO.set(propName, value);
					continue out;
				}
			}
		}
	}
	
	/**
	 * 改变表单多个字段属性值<br/>
	 * @param items 表单字段集合<br/>
	 * @param itemCode 表单字段编码<br/>
	 * @param propNames 要修改的属性的属性名集合<br/>
	 * @param values 要改变的值集合，顺序与属性保持一一对应<br/>
	 * @author zhangfeng
	 * @date 2016年8月23日
	 */
	public static void changeValue(List<FormItemPCVO> items, String itemCode, List<String> propNames, List<Object> values) {
		if(ALL_ITEMS.equals(itemCode)) {
			for(FormItemPCVO itemVO : items) {				
				for(int i=0; i<propNames.size(); i++) {
					itemVO.set(propNames.get(i), values.get(i));
				}	
				break;
			}
			return;
		}
		for(FormItemPCVO itemVO : items) {
			if(itemVO.getFitem_code().equals(itemCode)) {
				for(int i=0; i<propNames.size(); i++) {
					itemVO.set(propNames.get(i), values.get(i));
				}
				break;
			}
		}
	}
	
	/**
	 * 设置字段隐藏
	 * @param items
	 * @param itemCodes 
	 * @author zhangfeng
	 * @date 2016年8月24日
	 */
	public static void hideItems(List<FormItemPCVO> items, String[] itemCodes) {
		changeValue(items, itemCodes, "fitem_input_element", "0");
	}
	
	/**
	 * 设置字段只读状态
	 * @param items
	 * @param itemCodes
	 * @param readonly 
	 * @author zhangfeng
	 * @date 2016年8月24日
	 */
	public static void setReadonly(List<FormItemPCVO> items, String[] itemCodes, boolean readonly) {
		changeValue(items, itemCodes, "fitem_readonly", readonly?1:0);
	}
	
	/**
	 * 替换表单字段中的系统变量
	 * @param list 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2017年2月20日
	 */
	public static void replaceSystemParam(List<? extends FormItemVO> list) throws HDException {
		for(FormItemVO itemVO : list) {
			for(String code : codeArr) {
				((AbstractVO)itemVO).set(code,VariableUtils.replaceSystemParam(((AbstractVO)itemVO).getString(code), itemVO));
			}
		}
	}
}
