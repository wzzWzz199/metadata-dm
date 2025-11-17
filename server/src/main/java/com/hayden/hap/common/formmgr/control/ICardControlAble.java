package com.hayden.hap.common.formmgr.control;


/**
 * 卡片控制接口
 * @author zhangfeng
 * @date 2017年10月30日
 */
public interface ICardControlAble {

	/**
	 * 获取卡片控制对象
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月30日
	 */
	CardCtrlVO getCardCtrlVO();
	
	/**
	 * 设置卡片控制对象
	 * @param cardCtrlVO 
	 * @author zhangfeng
	 * @date 2017年10月30日
	 */
	void setCardCtrlVO(CardCtrlVO cardCtrlVO);
}
