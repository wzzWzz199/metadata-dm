/**
 * Project Name:hap-sy
 * File Name:MainExtVO.java
 * Package Name:com.hayden.hap.sy.common.entity
 * Date:2016年1月16日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.common.entity;

import com.hayden.hap.common.dict.entity.DictDataWarperVO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ClassName:MainExtVO (递归对象).<br/>
 * Date:     2016年1月16日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
@SuppressWarnings("serial")
public class MainExtVO implements Serializable {
	private Map<String, List<DictDataWarperVO>> dictvos = null;
	private List<RecursionVO> mainvo = null;
	public Map<String, List<DictDataWarperVO>> getDictvos() {
		return dictvos;
	}
	public void setDictvos(Map<String, List<DictDataWarperVO>> dictvos) {
		this.dictvos = dictvos;
	}
	public List<RecursionVO> getMainvo() {
		return mainvo;
	}
	public void setMainvo(List<RecursionVO> mainvo) {
		this.mainvo = mainvo;
	}
}

