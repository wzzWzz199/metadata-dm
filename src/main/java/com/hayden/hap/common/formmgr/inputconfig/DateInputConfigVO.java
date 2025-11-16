/**
 * 
 */
package com.hayden.hap.common.formmgr.inputconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author zhangfeng
 *
 */
public class DateInputConfigVO extends QueryAbleConfigVO {

	/**
	 * 日期类型
	 */
	private String datetype;
	
	/**
	 * 回调函数
	 */
	@JsonInclude(Include.NON_EMPTY)
	private String callback;
	
	/**
	 * 格式
	 */
	@JsonInclude(Include.NON_EMPTY)
	private String format;

	public String getDatetype() {
		return datetype;
	}

	public void setDatetype(String datetype) {
		this.datetype = datetype;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
