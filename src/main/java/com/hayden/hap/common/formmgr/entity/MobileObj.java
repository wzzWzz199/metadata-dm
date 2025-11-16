/**
 * Project Name:hap-sy
 * File Name:MobileObj.java
 * Package Name:com.hayden.hap.m.sy.formmgr.entity
 * Date:2016年1月14日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.common.formmgr.entity;

import com.hayden.hap.common.common.entity.CommonVO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:MobileObj ().<br/>
 * Date:     2016年1月14日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class MobileObj implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String,Object> paraMap = null;
	
	public Map<String, Object> getParaMap() {
		return paraMap;
	}

	public void setParaMap(Map<String, Object> paraMap) {
		this.paraMap = paraMap;
	}

	private Map<String,CommonVO> headvo = null;
	
	private Map<String,List<CommonVO>> bodyvos = null;
	
	public Map<String, List<CommonVO>> getBodyvos() {
		return bodyvos==null?new HashMap<String, List<CommonVO>>():bodyvos;
	}

	public void setBodyvos(Map<String, List<CommonVO>> bodyvos) {
		this.bodyvos = bodyvos;
	}

	public Map<String,CommonVO> getHeadvo() {
		return headvo;
	}

	public void setHeadvo(Map<String,CommonVO> headvo) {
		this.headvo = headvo;
	}

}

