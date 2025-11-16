package com.hayden.hap.common.formmgr.entity;

import com.hayden.hap.common.dict.entity.DictDataWarperVO;

import java.util.List;

/**
 * 全息查询参数节点VO
 * @author zhangfeng
 * @date 2016年12月20日
 */
public class FuncTreeParamNodeVO {

	private String fitemCode;
	
	private List<DictDataWarperVO> nodes;

	public String getFitemCode() {
		return fitemCode;
	}

	public void setFitemCode(String fitemCode) {
		this.fitemCode = fitemCode;
	}

	public List<DictDataWarperVO> getNodes() {
		return nodes;
	}

	public void setNodes(List<DictDataWarperVO> nodes) {
		this.nodes = nodes;
	}
}
