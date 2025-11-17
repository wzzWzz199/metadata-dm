package com.hayden.hap.dbop.db.orm.entity;

import com.alibaba.druid.sql.ast.SQLExpr;

import java.io.Serializable;
import java.util.Map;

/**
 * sql的解析结果，包括表名、列值的映射
 * @author wangyi
 * @date 2018年1月2日
 */
public class SqlParseVO implements Serializable{
	private String tableName;
	private Map<String, SQLExpr> colValMap;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, SQLExpr> getColValMap() {
		return colValMap;
	}
	public void setColValMap(Map<String, SQLExpr> colValMap) {
		this.colValMap = colValMap;
	}
}