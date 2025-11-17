package com.hayden.hap.common.entity.func;

/**
 * app端功能VO
 * @author zhangfeng
 * @date 2018年1月29日
 */
public class FuncMVO extends FuncVO {

	private static final long serialVersionUID = 1L;

	public FuncMVO() {
		super("sy_func_mobile");
	}
	
	/**
	 * 结构数据版本号
	 */
	private Long meta_ver;

	public Long getMeta_ver() {
		return meta_ver;
	}

	public void setMeta_ver(Long meta_ver) {
		this.meta_ver = meta_ver;
	}
}
