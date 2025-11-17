package com.hayden.hap.common.entity.dict;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * 含字典版本的包装器
 * @author zhangfeng
 * @date 2018年1月30日
 */
public class DictVersionWarper {

	/**
	 * 有更新
	 */
	public static final String STATUS_UPDATED = "updated";
	
	/**
	 * 缓存，无更新
	 */
	public static final String STATUS_CACHED = "cached";
	
	/**
	 * 出现了错误
	 */
	public static final String STATUS_ERROR = "error";
	
	
	/**
	 * 字典编码
	 */
	private String code;
	
	/**
	 * 字典版本
	 */
	private String ts;
	
	/**
	 * 字典状态
	 */
	private String status;
	
	/**
	 * 租户id
	 */
	private Long tenantid;
	
	/**
	 * 错误信息
	 */
	@JsonInclude(Include.NON_NULL)
	private String errMsg;
	
	/**
	 * 字典数据
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<DictDataWarperVO> dictData;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<DictDataWarperVO> getDictData() {
		return dictData;
	}

	public void setDictData(List<DictDataWarperVO> dictData) {
		this.dictData = dictData;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public Long getTenantid() {
		return tenantid;
	}

	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}
}
