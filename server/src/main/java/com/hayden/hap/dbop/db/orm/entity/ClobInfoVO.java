package com.hayden.hap.dbop.db.orm.entity;

import java.io.Serializable;
import java.util.List;

/**
 * clob对象，包括是否处理clob、sql的解析结果、clob列等。
 * @author wangyi
 * @date 2018年1月2日
 */
public class ClobInfoVO implements Serializable{
	private boolean dealClob;
	private SqlParseVO sqlParseVO;
	//clob字段集
	List<String> clobCols;
	public boolean isDealClob() {
		return dealClob;
	}
	public void setDealClob(boolean dealClob) {
		this.dealClob = dealClob;
	}
	public SqlParseVO getSqlParseVO() {
		return sqlParseVO;
	}
	public void setSqlParseVO(SqlParseVO sqlParseVO) {
		this.sqlParseVO = sqlParseVO;
	}
	public List<String> getClobCols() {
		return clobCols;
	}
	public void setClobCols(List<String> clobCols) {
		this.clobCols = clobCols;
	}
}
