package com.hayden.hap.common.formmgr.entity;

import com.hayden.hap.common.dict.entity.DictDataWarperVO;

import java.util.List;

/**
 * 输入设定VO
 * @author zhangfeng
 * @date 2016年8月30日
 */
public class InputConfigVO {

	public static final String SINGLE = "single";
	
	public static final String MULTI = "multi";
	
	/**
	 * 字典编码
	 */
	private String dictCode;
	
	/**
	 * 功能编码
	 */
	private String funcCode;
	
	/**
	 * 回调方法
	 */
	private String recallFunc;
	
	/**
	 * 额外条件
	 */
	private String extWhere;
	
	/**
	 * 是否加载到移动端;默认不加载
	 */
	private boolean isLoadForMobile = false;
	
	/**
	 * 当前表单元素的字段编码
	 */
	private String[] formElements;
	
	/**
	 * 取数据的字段编码
	 */
	private String[] selects;
	
	/**
	 * 单选or多选;默认单选
	 */
	private String sign = "single";
	
	/**
	 * 是否id转名称;默认不转
	 */
	private boolean isId2Name = false;
	
	/**
	 * 日期类型
	 */
	private String dateType;
	
	/**
	 * 日期格式
	 */
	private String dateFmt;
	
	/**
	 * 表编码
	 */
	private String tableCode;
	
	private List<DictDataWarperVO> dictData;
	

	public String getDictCode() {
		return dictCode;
	}
	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}
	public String getFuncCode() {
		return funcCode;
	}
	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}
	public String getRecallFunc() {
		return recallFunc;
	}
	public void setRecallFunc(String recallFunc) {
		this.recallFunc = recallFunc;
	}
	public String getExtWhere() {
		return extWhere;
	}
	public void setExtWhere(String extWhere) {
		this.extWhere = extWhere;
	}
	public boolean isLoadForMobile() {
		return isLoadForMobile;
	}
	public void setLoadForMobile(boolean isLoadForMobile) {
		this.isLoadForMobile = isLoadForMobile;
	}
	public String[] getFormElements() {
		return formElements;
	}
	public void setFormElements(String[] formElements) {
		this.formElements = formElements;
	}
	public String[] getSelects() {
		return selects;
	}
	public void setSelects(String[] selects) {
		this.selects = selects;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public boolean isId2Name() {
		return isId2Name;
	}
	public void setId2Name(boolean isId2Name) {
		this.isId2Name = isId2Name;
	}
	public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	public String getDateFmt() {
		return dateFmt;
	}
	public void setDateFmt(String dateFmt) {
		this.dateFmt = dateFmt;
	}
	public List<DictDataWarperVO> getDictData() {
		return dictData;
	}
	public void setDictData(List<DictDataWarperVO> dictData) {
		this.dictData = dictData;
	}
	public String getTableCode() {
		return tableCode;
	}
	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}
}
