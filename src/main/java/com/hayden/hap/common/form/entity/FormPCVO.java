package com.hayden.hap.common.form.entity;

import com.hayden.hap.common.utils.table.annotation.Table;

/**
 * 表单实体
 * 
 * @author zhangfeng
 * @date 2015年6月3日
 */
@Table(value = "SY_FORM", desc = "表单")
public class FormPCVO extends FormVO {

	private static final long serialVersionUID = 1L;

	public FormPCVO() {
		super("SY_FORM");
	}
}
