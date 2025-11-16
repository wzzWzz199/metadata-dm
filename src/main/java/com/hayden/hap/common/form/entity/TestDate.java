package com.hayden.hap.common.form.entity;

import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.utils.table.annotation.Column;
import com.hayden.hap.common.utils.table.annotation.Table;

import java.util.Date;
//import com.hayden.hap.common.workflow.entity.WorkflowVO;

/**
 * 
 * @author zhangfeng
 * @date 2015年10月28日
 */
@Table(value="TEST_DATE",desc="测试日期")
public class TestDate extends BaseVO {

	private static final long serialVersionUID = 1L;

	@Column(type="integer",length=19,isPK=true,allowNull=false)
	private Long id;
	
	@Column(type="datetime",length=0)
	private Date start_time;
	
	@Column(type="varchar",length=50)
	private String modulecode;
	
	@Column(type="varchar",length=200)
	private String photo;
	
	
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * 非持久化测试字段
	 */
	private String extString = "testString";
	
	public TestDate() {
		super("TEST_DATE");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public String getModulecode() {
		return modulecode;
	}

	public void setModulecode(String modulecode) {
		this.modulecode = modulecode;
	}

	public String getExtString() {
		return extString;
	}

	public void setExtString(String extString) {
		this.extString = extString;
	}
}
