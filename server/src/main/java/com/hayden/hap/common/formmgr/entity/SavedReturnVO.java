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

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年7月27日
 */
public class SavedReturnVO implements ICardData, IRefreshAble, ICardControlAble, Serializable{

	private static final long serialVersionUID = 1L;

	private AbstractVO data;
	
	private List<? extends ButtonVO> btnList;
	
	@JsonInclude(Include.NON_NULL)
	private RefreshVO refreshVO;
	
	@JsonInclude(Include.NON_NULL)
	private CardCtrlVO cardCtrlVO;

	public List<? extends ButtonVO> getBtnList() {
		return btnList;
	}

	public void setBtnList(List<? extends ButtonVO> btnList) {
		this.btnList = btnList;
	}

	public AbstractVO getData() {
		return data;
	}

	public void setData(AbstractVO data) {
		this.data = data;
	}

	public RefreshVO getRefreshVO() {
		return refreshVO;
	}

	public void setRefreshVO(RefreshVO refreshVO) {
		this.refreshVO = refreshVO;
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

	@Override
	public CardCtrlVO getCardCtrlVO() {
		return cardCtrlVO;
	}

	@Override
	public void setCardCtrlVO(CardCtrlVO cardCtrlVO) {
		this.cardCtrlVO = cardCtrlVO;
		
	}
	
}
