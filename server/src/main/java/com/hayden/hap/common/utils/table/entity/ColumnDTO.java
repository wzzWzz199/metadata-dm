package com.hayden.hap.common.utils.table.entity;

public class ColumnDTO {
	private String name;
	private String type;
	private int length;
	private boolean allowNull;
	private boolean isPK;
	private Integer defaultIntValue;
	private String defaultStringValue;
	private String tableDefId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isAllowNull() {
		return allowNull;
	}
	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}
	public boolean isPK() {
		return isPK;
	}
	public void setPK(boolean isPK) {
		this.isPK = isPK;
	}
	
	public Integer getDefaultIntValue() {
		return defaultIntValue;
	}
	public void setDefaultIntValue(Integer defaultIntValue) {
		this.defaultIntValue = defaultIntValue;
	}
	public String getDefaultStringValue() {
		return defaultStringValue;
	}
	public void setDefaultStringValue(String defaultStringValue) {
		this.defaultStringValue = defaultStringValue;
	}
	public String getTableDefId() {
		return tableDefId;
	}
	public void setTableDefId(String tableDefId) {
		this.tableDefId = tableDefId;
	}
}
