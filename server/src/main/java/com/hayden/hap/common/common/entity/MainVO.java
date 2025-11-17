/**
 * Project Name:hap-sy
 * File Name:MainVO.java
 * Package Name:com.hayden.hap.sy.common.entity
 * Date:2015年12月23日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
 */

package com.hayden.hap.common.common.entity;

import com.hayden.hap.common.dict.entity.DictDataWarperVO;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:MainVO ().<br/>
 * Date: 2015年12月23日 <br/>
 * 
 * @author ZhangJie
 * @version
 * @see
 */
public class MainVO implements Serializable {

	private Map<String,CommonVO> headvo;
	private LinkedHashMap<String,List<CommonVO>> bodyvos;
	private Map<String, List<DictDataWarperVO>> dictvos;
	
	public Map<String,CommonVO> getHeadvo() {
		return headvo;
	}
	public void setHeadvo(Map<String,CommonVO> headvo) {
		this.headvo = headvo;
	}
	public LinkedHashMap<String, List<CommonVO>> getBodyvos() {
		return bodyvos;
	}
	public void setBodyvos(LinkedHashMap<String, List<CommonVO>> bodyvos) {
		this.bodyvos = bodyvos;
	}
	public Map<String, List<DictDataWarperVO>> getDictvos() {
		return dictvos;
	}
	public void setDictvos(Map<String, List<DictDataWarperVO>> dictvos) {
		this.dictvos = dictvos;
	}

}
