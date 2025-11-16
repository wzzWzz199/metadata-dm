package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.common.entity.AbstractVO;

import java.util.List;

/**
 * 卡片数据
 * @author zhangfeng
 * @date 2017年10月31日
 */
public interface ICardData {

	/**
	 * 数据VO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	AbstractVO getData();

	/**
	 * 卡片按钮
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	List<? extends ButtonVO> getBtnList();

	/**
	 * 只读状态
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月31日
	 */
	boolean isReadonly();
}
