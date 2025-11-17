package com.hayden.hap.common.formmgr.control;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月10日
 */
public class ListDataCtrlVO {

	private Long id;
	
	private boolean readonly = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}
