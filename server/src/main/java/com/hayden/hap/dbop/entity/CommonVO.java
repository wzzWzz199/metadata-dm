/**
 * Project Name:hap-sy
 * File Name:CommonVO.java
 * Package Name:com.hayden.hap.sy.common.entity
 * Date:2015年11月23日
 * Copyright (c) 2015, zhangjie@ushayden.com All Rights Reserved.
 *
*/

package com.hayden.hap.dbop.entity;

/**
 * ClassName:CommonVO ().<br/>
 * Date:     2015年11月23日  <br/>
 * @author   ZhangJie
 * @version  
 * @see 	 
 */
public class CommonVO extends AbstractVO {

	/**
	 * serialVersionUID:TODO().
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * DATA_STATS_DEFAULT:(标记数据的默认值，没有更多意义[select]).
	 */
	public static final Integer DATA_STATS_DEFAULT = 0;
	/**
	 * DATA_STATS_ADDED:(标记数据是新增未保存的[insert]).
	 */
	public static final Integer DATA_STATS_ADDED = 1;
	/**
	 * DATA_STATS_UPDATED:(标记数据是修改后未保存的[update]).
	 */
	public static final Integer DATA_STATS_UPDATED = 3;
	/**
	 * DATA_STATS_DELETED:(标记数据是要删除未保存的[delete]).
	 */
	public static final Integer DATA_STATS_DELETED = 5;

	public CommonVO(String tableName) {
		super(tableName.toLowerCase());
	}

	public CommonVO() {

	}
	
	private Integer dataStatus = 0; //SyConstant.DATA_STATS_DEFAULT

	public Integer getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}  

}

