package com.hayden.hap.common.form.entity;

import com.hayden.hap.common.common.entity.BaseVO;

/**
 * 
 * @author zhangfeng
 * @date 2017年12月4日
 */
public class FormItemCommentVO extends BaseVO{

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Long form_item_id;
	
	private String comment_type;
	
	private String content;
	
	public FormItemCommentVO() {
		super("sy_form_item_comment");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getForm_item_id() {
		return form_item_id;
	}

	public void setForm_item_id(Long form_item_id) {
		this.form_item_id = form_item_id;
	}

	public String getComment_type() {
		return comment_type;
	}

	public void setComment_type(String comment_type) {
		this.comment_type = comment_type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
