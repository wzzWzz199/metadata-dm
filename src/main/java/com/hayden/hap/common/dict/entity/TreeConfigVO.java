package com.hayden.hap.common.dict.entity;

/**
 * 
 * @author zhangfeng
 * @date 2016年9月26日
 */
public class TreeConfigVO {

	private Boolean enable = true;
	
	private String idKey = "dictdataid";
	
	private String pIdKey = "parentDictDataId";
	
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public String getIdKey() {
		return idKey;
	}
	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}
	public String getpIdKey() {
		return pIdKey;
	}
	public void setpIdKey(String pIdKey) {
		this.pIdKey = pIdKey;
	}
}
