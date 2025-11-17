package com.hayden.hap.common.formmgr.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.formmgr.control.CardCtrlVO;
import com.hayden.hap.common.formmgr.control.ICardControlAble;
import com.hayden.hap.common.formmgr.itf.ICardData;
import com.hayden.hap.common.formmgr.refresh.IRefreshAble;
import com.hayden.hap.common.formmgr.refresh.RefreshVO;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年11月29日
 */
public class CardEditVO implements ICardData, IRefreshAble, ICardControlAble{

	/**
	 * 业务VO
	 */
	private AbstractVO data;
	
	/**
	 * 工作流按钮
	 */
	private List<? extends ButtonVO> btnList;
	
	/**
	 * 只读状态
	 */
	private boolean readonly = false;
		
	/**
	 * 控制vo
	 */
	@JsonInclude(Include.NON_NULL)
	private CardCtrlVO cardCtrlVO = null;
	
	/**
	 * 刷新对象，默认只刷新卡片
	 */
	@JsonInclude(Include.NON_NULL)
	private RefreshVO refreshVO = null;

	public AbstractVO getData() {
		return data;
	}

	public void setData(AbstractVO data) {
		this.data = data;
	}

	public List<? extends ButtonVO> getBtnList() {
		return btnList;
	}

	public void setBtnList(List<? extends ButtonVO> btnList) {
		this.btnList = btnList;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public RefreshVO getRefreshVO() {
		return refreshVO;
	}

	public void setRefreshVO(RefreshVO refreshVO) {
		this.refreshVO = refreshVO;
	}

	public CardCtrlVO getCardCtrlVO() {
		return cardCtrlVO;
	}

	public void setCardCtrlVO(CardCtrlVO cardCtrlVO) {
		this.cardCtrlVO = cardCtrlVO;
	}
}
