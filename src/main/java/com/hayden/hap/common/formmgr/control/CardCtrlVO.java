package com.hayden.hap.common.formmgr.control;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hayden.hap.common.formmgr.entity.MetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年10月30日
 */
public class CardCtrlVO {
	
	public static final String ALL_FITEMS = "ALL";

	/**
	 * 只读字段集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> readonly_fitems = new ArrayList<>();
	
	/**
	 * 只读字段集合（全量）
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> fitems_readonly = new ArrayList<>();
	
	/**
	 * 可编辑字段集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> editable_fitems = new ArrayList<>();
	
	/**
	 * 可编辑字段集合（全量）
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> fitems_editable = new ArrayList<>();
	
	/**
	 * 隐藏字段集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> hide_fitems = new ArrayList<>();
	
	/**
	 * 显示字段集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> show_fitems = new ArrayList<>();
	
	/**
	 * 必填字段
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> require_fitems = new ArrayList<>();
	
	/**
	 * 可空字段
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> norequire_fitems = new ArrayList<>();
	
	/**
	 * 隐藏子功能集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> hide_child_funcs = new ArrayList<>();
	
	/**
	 * 显示子功能集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> show_child_funcs = new ArrayList<>();
	
	
	/**
	 * 只读子功能集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> readonlly_child_funcs = new ArrayList<>();
	
	
	/**
	 * 非只读子功能集合
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> editable_child_funcs = new ArrayList<>();

	/**
	 * 结构数据
	 */
	@JsonInclude(Include.NON_NULL)
	private MetaData metaData;

	public List<String> getReadonly_fitems() {
		return readonly_fitems;
	}


	public void setReadonly_fitems(List<String> readonly_fitems) {
		this.readonly_fitems = readonly_fitems;
	}


	public List<String> getEditable_fitems() {
		return editable_fitems;
	}


	public void setEditable_fitems(List<String> editable_fitems) {
		this.editable_fitems = editable_fitems;
	}


	public List<String> getFitems_readonly() {
		return fitems_readonly;
	}


	public void setFitems_readonly(List<String> fitems_readonly) {
		this.fitems_readonly = fitems_readonly;
	}


	public List<String> getFitems_editable() {
		return fitems_editable;
	}


	public void setFitems_editable(List<String> fitems_editable) {
		this.fitems_editable = fitems_editable;
	}


	public List<String> getHide_fitems() {
		return hide_fitems;
	}


	public void setHide_fitems(List<String> hide_fitems) {
		this.hide_fitems = hide_fitems;
	}


	public List<String> getHide_child_funcs() {
		return hide_child_funcs;
	}


	public void setHide_child_funcs(List<String> hide_child_funcs) {
		this.hide_child_funcs = hide_child_funcs;
	}


	public List<String> getReadonlly_child_funcs() {
		return readonlly_child_funcs;
	}


	public void setReadonlly_child_funcs(List<String> readonlly_child_funcs) {
		this.readonlly_child_funcs = readonlly_child_funcs;
	}


	public List<String> getEditable_child_funcs() {
		return editable_child_funcs;
	}


	public void setEditable_child_funcs(List<String> editable_child_funcs) {
		this.editable_child_funcs = editable_child_funcs;
	}


	public MetaData getMetaData() {
		return metaData;
	}


	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}


	public List<String> getShow_fitems() {
		return show_fitems;
	}


	public void setShow_fitems(List<String> show_fitems) {
		this.show_fitems = show_fitems;
	}


	public List<String> getRequire_fitems() {
		return require_fitems;
	}


	public void setRequire_fitems(List<String> require_fitems) {
		this.require_fitems = require_fitems;
	}


	public List<String> getNorequire_fitems() {
		return norequire_fitems;
	}


	public void setNorequire_fitems(List<String> norequire_fitems) {
		this.norequire_fitems = norequire_fitems;
	}


	public List<String> getShow_child_funcs() {
		return show_child_funcs;
	}


	public void setShow_child_funcs(List<String> show_child_funcs) {
		this.show_child_funcs = show_child_funcs;
	}
	
	
	/**
	 * 添加数据
	 * @param container
	 * @param codes 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	private void addData(List<String> container, String ...codes) {
		if(codes==null || codes.length==0)
			return;
		
		if(container==null)
			container = new ArrayList<>();
		
		for(String code : codes) {
			container.add(code);
		}
	}
	
	/**
	 * 添加隐藏字段
	 * @param fitemcode
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addHideItem(String ...fitemcodes) {
		addData(hide_fitems, fitemcodes);
		return this;
	}
	
	/**
	 * 添加显示字段
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addShowItem(String ...fitemcodes) {
		addData(show_fitems, fitemcodes);
		return this;
	}
	
	/**
	 * 添加可编辑字段
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addEditeableItem(String ...fitemcodes) {
		addData(editable_fitems, fitemcodes);
		return this;
	}
	
	/**
	 * 添加只读字段（全量）
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2018年4月25日
	 */
	public CardCtrlVO addFullEditeableItem(String ...fitemcodes) {
		addData(fitems_editable, fitemcodes);
		return this;
	}
	
	/**
	 * 添加只读字段
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addReadonlyItem(String ...fitemcodes) {
		addData(readonly_fitems, fitemcodes);
		return this;
	}
	
	/**
	 * 添加只读字段（全量）
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2018年4月25日
	 */
	public CardCtrlVO addFullReadonlyItem(String ...fitemcodes) {
		addData(fitems_readonly, fitemcodes);
		return this;
	}
	
	/**
	 * 添加非空字段
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addRequireItem(String ...fitemcodes) {
		addData(require_fitems, fitemcodes);
		return this;
	}
	
	/**
	 * 添加可空字段
	 * @param fitemcodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addNorequireItem(String ...fitemcodes) {
		addData(norequire_fitems, fitemcodes);
		return this;
	}
	
	/**
	 * 添加隐藏子功能
	 * @param funcCodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addHideChildFunc(String ...funcCodes) {
		addData(hide_child_funcs, funcCodes);
		return this;
	}
	
	/**
	 * 添加要显示的子功能
	 * @param funcCodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addShowChildFunc(String ...funcCodes) {
		addData(show_child_funcs, funcCodes);
		return this;
	}
	
	/**
	 * 添加只读子功能
	 * @param funcCodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addReadonlyChildFunc(String ...funcCodes) {
		addData(readonlly_child_funcs, funcCodes);
		return this;
	}
	
	/**
	 * 添加可编辑子功能
	 * @param funcCodes
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	public CardCtrlVO addEditableChildFunc(String ...funcCodes) {
		addData(editable_child_funcs, funcCodes);
		return this;
	}
	
	
}
