package com.hayden.hap.common.form.conver.pc;

import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormPCVO;

import java.util.List;

/**
 * 表字段转换表单字段的转换器接口
 * @author zhangfeng
 * @date 2017年3月2日
 */
public interface IPCConverter {
	
	String getCode();
	String getName();
	String getDataType();
	Integer getLength();
	String getInputElement();
	String getInputType();
	String getInputConfig();
	
	String getInputElementQuery();
	String getInputTypeQuery();
	String getInputConfigQuery();
	
	String getType();
	String getCardDefault();
	String getProductFlag();
	Integer getIsenable();
	Integer getReadonly();
	Integer getNotnull();
	Integer getShowList();
	Integer getQuickList();
	Integer getBatch();
	Integer getComQuery();
	Integer getQuickQuery();
	Integer getSortable();
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
	Integer getQueryOrder();
	Integer getOrder();
	String getQueryDefault();
	String getListWidth();
	String getCardFormat();
	String getListFormat();
	Integer getQueryNotnull();
	Integer getListColWrap();
	
	String getTextAlign();	
	
	void setColumnVO(TableColumnVO columnVO);
	void setFormVO(FormPCVO formVO);
	List<FormItemPCVO> getItems();
}
