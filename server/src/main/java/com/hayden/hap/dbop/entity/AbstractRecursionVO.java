/**
 * Project Name:hap-sy
 * File Name:RecursionTVO.java
 * Package Name:com.hayden.hap.sy.common.entity
 * Date:2016年4月22日
 * Copyright (c) 2016, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.dbop.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:RecursionTVO ().<br/>
 * Date:     2016年4月22日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class AbstractRecursionVO<T> {
	public List<String> getPkValues() {
		return pkValues;
	}
	public void setPkValues(List<String> pkValues) {
		this.pkValues = pkValues;
	}
	public List<AbstractRecursionVO<T>> getChildrenVO() {
		return childrenVO;
	}
	public void setChildrenVO(List<AbstractRecursionVO<T>> childrenVO) {
		this.childrenVO = childrenVO;
	}
	T parentVO;
	public T getParentVO() {
		return parentVO;
	}
	public void setParentVO(T parentVO) {
		this.parentVO = parentVO;
	}
	List<String> pkValues = new ArrayList<String>();
	List<AbstractRecursionVO<T>> childrenVO;

}

