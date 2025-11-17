package com.hayden.hap.common.formmgr.entity;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;

import java.io.Serializable;
import java.util.List;

/**
 * 批量更新的参数VO类
 * @author zhangfeng
 * @date 2015年11月10日
 */
public class BatchUpdateParamVO implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 批量更新的vo集合
	 */
	private List<? extends AbstractVO> voList;
	
	/**
	 * 主键名
	 */
	private String pkName;
	
	/**
	 * 批量更新的DynaSqlVO
	 */
	private DynaSqlVO dynaSqlVO;

	public List<? extends AbstractVO> getVoList() {
		return voList;
	}

	public void setVoList(List<? extends AbstractVO> voList) {
		this.voList = voList;
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	public DynaSqlVO getDynaSqlVO() {
		return dynaSqlVO;
	}

	public void setDynaSqlVO(DynaSqlVO dynaSqlVO) {
		this.dynaSqlVO = dynaSqlVO;
	}
}
