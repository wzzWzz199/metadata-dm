package com.hayden.hap.meta.dataSource.entity;

import java.util.List;

public class MetaRelationVO {
	private String nodevo;
	private String nodetype;
	private String exportColumn;
	private String importColumn;
	private String parentColumn;
	private List<MetaRelationVO> childNodes;
	public String getNodevo() {
		return nodevo;
	}
	public void setNodevo(String nodevo) {
		this.nodevo = nodevo;
	}
	public String getNodetype() {
		return nodetype;
	}
	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}
	public String getExportColumn() {
		return exportColumn;
	}
	public void setExportColumn(String exportColumn) {
		this.exportColumn = exportColumn;
	}
	public List<MetaRelationVO> getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(List<MetaRelationVO> childNodes) {
		this.childNodes = childNodes;
	}
	public String getImportColumn() {
		return importColumn;
	}
	public void setImportColumn(String importColumn) {
		this.importColumn = importColumn;
	}
	public String getParentColumn() {
		return parentColumn;
	}
	public void setParentColumn(String parentColumn) {
		this.parentColumn = parentColumn;
	}
	
	
}
