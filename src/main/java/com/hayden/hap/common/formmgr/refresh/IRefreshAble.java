package com.hayden.hap.common.formmgr.refresh;

/**
 * 可控制刷新接口
 * 
 * @author zhangfeng
 * @date 2017年10月31日
 */
public interface IRefreshAble {

	/**
	 * 获取刷新对象
	 * @return 
	 * @author zhangfeng
	 * @date 2017年7月27日
	 */
	RefreshVO getRefreshVO();
	
	/**
	 * 设置刷新对象
	 * @param refreshVO 
	 * @author zhangfeng
	 * @date 2017年7月27日
	 */
	void setRefreshVO(RefreshVO refreshVO);
}
