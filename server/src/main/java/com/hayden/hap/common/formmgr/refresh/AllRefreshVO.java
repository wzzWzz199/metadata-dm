package com.hayden.hap.common.formmgr.refresh;

/**
 * 
 * @author zhangfeng
 * @date 2017年7月27日
 */
public class AllRefreshVO extends RefreshVO {
	
	public AllRefreshVO() {
		setCard(true);
		setList(true);
		setParentcard(true);
		setParentlist(true);
	}
}
