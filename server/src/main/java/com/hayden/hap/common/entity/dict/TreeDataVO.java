package com.hayden.hap.common.entity.dict;

import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2016年9月26日
 */
public class TreeDataVO {

	/**
	 * 简单树配置
	 */
	private TreeConfigVO config = new TreeConfigVO();
	
	/**
	 * 节点数据
	 */
	private List<? extends ITreeNodeVO> data;

	public TreeConfigVO getConfig() {
		return config;
	}

	public void setConfig(TreeConfigVO config) {
		this.config = config;
	}

	public List<? extends ITreeNodeVO> getData() {
		return data;
	}

	public void setData(List<? extends ITreeNodeVO> data) {
		this.data = data;
	}
}
