package com.hayden.hap.vo;

import java.util.List;

public class MetaTypeVO {
	private String type;
	private String name;
	private String queryItemName;
	private String queryItem;
	private String listItemName;
	private String listItem;
	private List<MetaLinkTypeVO> linkedType;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQueryItemName() {
		return queryItemName;
	}
	public void setQueryItemName(String queryItemName) {
		this.queryItemName = queryItemName;
	}
	public String getQueryItem() {
		return queryItem;
	}
	public void setQueryItem(String queryItem) {
		this.queryItem = queryItem;
	}
	public List<MetaLinkTypeVO> getLinkedType() {
		return linkedType;
	}
	public void setLinkedType(List<MetaLinkTypeVO> linkedType) {
		this.linkedType = linkedType;
	}
	public String getListItemName() {
		return listItemName;
	}
	public void setListItemName(String listItemName) {
		this.listItemName = listItemName;
	}
	public String getListItem() {
		return listItem;
	}
	public void setListItem(String listItem) {
		this.listItem = listItem;
	}
	
	

}
