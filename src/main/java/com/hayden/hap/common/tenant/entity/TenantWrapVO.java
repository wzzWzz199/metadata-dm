package com.hayden.hap.common.tenant.entity;

import java.io.Serializable;

/**
 * 租户包装VO
 * @author zhangfeng
 * @date 2018年3月26日
 */
public class TenantWrapVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long tenantid;
	
	private String tenantcode;
	
	private String tenantname;
	
	private Integer isExperience;

	private String tenantlogoimg;
	
	public TenantWrapVO() {
		
	}

	public TenantWrapVO(TenantVO tenantVO) {
		this.tenantid = tenantVO.getTenantpk();
		this.tenantcode = tenantVO.getTenantcode();
		this.tenantname = tenantVO.getTenantname();
		this.isExperience = tenantVO.getIs_experience();
		this.tenantlogoimg = tenantVO.getTenant_logo_img();
	}

	public Long getTenantid() {
		return tenantid;
	}

	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}

	public String getTenantcode() {
		return tenantcode;
	}

	public void setTenantcode(String tenantcode) {
		this.tenantcode = tenantcode;
	}

	public String getTenantname() {
		return tenantname;
	}

	public void setTenantname(String tenantname) {
		this.tenantname = tenantname;
	}

	public Integer getIsExperience() {
		return isExperience;
	}

	public void setIsExperience(Integer isExperience) {
		this.isExperience = isExperience;
	}

	public String getTenantlogoimg() {
		return tenantlogoimg;
	}

	public void setTenantlogoimg(String tenantlogoimg) {
		this.tenantlogoimg = tenantlogoimg;
	}
}
