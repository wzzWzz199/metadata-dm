package com.hayden.hap.common.formmgr.refresh;

import com.hayden.hap.common.formmgr.constant.FormmgrConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2017年7月27日
 */
public class RefreshVO {
	
	/**
	 * 是否刷新卡片
	 */
	private boolean card=false; 
	
	/**
	 * 是否刷新列表
	 */
	private boolean list=false; 
	
	/**
	 * 是否刷新父卡片
	 */
	private boolean parentcard=false; 
	
	/**
	 * 是否刷新父列表
	 */
	private boolean parentlist=false;
	
	/**
	 * 是否刷新顶层卡片
	 */
	private boolean topcard = false;
	
	/**
	 * 是否刷新顶层列表
	 */
	private boolean toplist = false;
	
	private Map<String,String> children = new HashMap<>();
	
	/**
	 * 添加子功能卡片刷新
	 * @param funccode 子功能功能编码
	 * @author zhangfeng
	 * @date 2017年7月27日
	 */
	public void addRefreshChildCard(String funccode) {
		children.put(funccode, FormmgrConstant.CARD_VIEW);
	}
	
	/**
	 * 添加子功能列表刷新
	 * @param funccode 子功能功能编码
	 * @author zhangfeng
	 * @date 2017年7月27日
	 */
	public void addRefreshChildList(String funccode) {
		children.put(funccode, FormmgrConstant.LIST_VIEW);
	}
	
	/**
	 * 移除子功能卡片刷新
	 * @param funccode 子功能功能编码
	 * @author zhangfeng
	 * @date 2017年7月27日
	 */
	public void removeRefreshChildCard(String funccode) {
		children.remove(funccode);
	}
	
	/**
	 * 移除子功能列表刷新
	 * @param funccode 子功能功能编码
	 * @author zhangfeng
	 * @date 2017年7月27日
	 */
	public void removeRefreshChildList(String funccode) {
		children.remove(funccode);
	}
	
	
	public boolean isCard() {
		return card;
	}
	public void setCard(boolean card) {
		this.card = card;
	}
	public boolean isList() {
		return list;
	}
	public void setList(boolean list) {
		this.list = list;
	}
	public boolean isParentcard() {
		return parentcard;
	}
	public void setParentcard(boolean parentcard) {
		this.parentcard = parentcard;
	}
	public boolean isParentlist() {
		return parentlist;
	}
	public void setParentlist(boolean parentlist) {
		this.parentlist = parentlist;
	}

	public Map<String, String> getChildren() {
		return children;
	}

	public boolean isTopcard() {
		return topcard;
	}

	public void setTopcard(boolean topcard) {
		this.topcard = topcard;
	}

	public boolean isToplist() {
		return toplist;
	}

	public void setToplist(boolean toplist) {
		this.toplist = toplist;
	}
	
}
