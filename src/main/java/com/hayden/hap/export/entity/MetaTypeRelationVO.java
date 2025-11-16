package com.hayden.hap.export.entity;

import com.hayden.hap.common.common.entity.BaseVO;

import java.util.List;

public class MetaTypeRelationVO {
	private String metaType;
	private String parentColumn;
	private List<BaseVO> metaData;
	private List<MetaTypeRelationVO> linkType;
	public String getMetaType() {
		return metaType;
	}
	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}
	public String getParentColumn() {
		return parentColumn;
	}
	public void setParentColumn(String parentColumn) {
		this.parentColumn = parentColumn;
	}
	public List<BaseVO> getMetaData() {
		return metaData;
	}
	public void setMetaData(List<BaseVO> metaData) {
		this.metaData = metaData;
	}
	public List<MetaTypeRelationVO> getLinkType() {
		return linkType;
	}
	public void setLinkType(List<MetaTypeRelationVO> linkType) {
		this.linkType = linkType;
	}
	
}
