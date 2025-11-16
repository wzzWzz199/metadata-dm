package com.hayden.hap.common.formmgr.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.db.orm.sql.Page;
import com.hayden.hap.common.formmgr.control.ListDataCtrlVO;
import com.hayden.hap.common.formmgr.refresh.IRefreshAble;
import com.hayden.hap.common.formmgr.refresh.ListRefreshVO;
import com.hayden.hap.common.formmgr.refresh.RefreshVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2017年3月10日
 */
@JsonInclude(Include.NON_NULL)
public class ListDataVO implements IRefreshAble {
	
	/**
	 * 列表数据结合
	 */
	List<? extends AbstractVO> voList = null;

	/**
	 * 分页信息
	 */
	private Page page = null;
	
	/**
	 * 刷新对象
	 */
	private RefreshVO refreshVO = null;

	/**
	 * 列表数据控制对象集合
	 */
	private List<ListDataCtrlVO> ctrlList;
	
	/**
	 * 创建刷新列表的列表数据对象
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月17日
	 */
	public static ListDataVO createListRefreshDataVO() {
		ListDataVO dataVO = new ListDataVO();
		dataVO.setRefreshVO(new ListRefreshVO());
		return dataVO;
	}

	public ListDataVO(VOSet<? extends AbstractVO> voset) {
		this.voList = voset.getVoList();
		this.page = voset.getPage();
	}
	
	public ListDataVO() {
		this.voList = new ArrayList<>();
		this.page = new Page(1, 50, 0);
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	public List<ListDataCtrlVO> getCtrlList() {
		return ctrlList;
	}

	public void setCtrlList(List<ListDataCtrlVO> ctrlList) {
		this.ctrlList = ctrlList;
	}
	
	public List<? extends AbstractVO> getVoList() {
		return voList;
	}

	public void setVoList(List<? extends AbstractVO> voList) {
		this.voList = voList;
	}	

	public RefreshVO getRefreshVO() {
		return refreshVO;
	}

	public void setRefreshVO(RefreshVO refreshVO) {
		this.refreshVO = refreshVO;
	}
}
