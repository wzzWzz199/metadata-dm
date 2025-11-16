package com.hayden.hap.common.form.conver;

import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;

/**
 * 
 * @author zhangfeng
 * @date 2018年5月10日
 */
public interface IConverter {

	String getCode();
	String getName();
	String getDataType();
	Integer getLength();
	String getInputElement();
	String getInputType();
	String getInputConfig();
	
	String getType();
	String getCardDefault();
	String getProductFlag();
	Integer getIsenable();
	Integer getReadonly();
	Integer getNotnull();
	String getValueRegexp();
	String getValueRegexpMsg();
	String getValueScope();
	String getValueScopeMsg();
	String getUniqueGroup();
	Integer getUniqueInfo();
	String getQueryOne();
	String getQueryTwo();
	Integer getCardRow();
	Integer getCardColumn();
	Integer getColumnOrder();
	Integer getOrder();
	String getCardFormat();
	String getListFormat();
	
	String getTextAlign();	
	
	void setColumnVO(TableColumnVO columnVO);
}
