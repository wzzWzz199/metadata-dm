package com.hayden.hap.common.entity.func;

import com.hayden.hap.dbop.entity.CommonVO;

public class NoValidateFuncVO extends CommonVO {
	/**
	 * 用于缓存，存放不需要较验功能权限或不需要较验按钮权限func
	 */
	private static final long serialVersionUID = 1L;
	private String func_code;
	private Integer func_acl_flag;
	private Integer func_action_falg;
	
	
	public String getFunc_code() {
		return func_code;
	}
	public void setFunc_code(String func_code) {
		this.func_code = func_code;
	}
	public Integer getFunc_acl_flag() {
		return func_acl_flag;
	}
	public void setFunc_acl_flag(Integer func_acl_flag) {
		this.func_acl_flag = func_acl_flag;
	}
	public Integer getFunc_action_falg() {
		return func_action_falg;
	}
	public void setFunc_action_falg(Integer func_action_falg) {
		this.func_action_falg = func_action_falg;
	}
	public NoValidateFuncVO(){
		super("SY_FUNC");
	}
	
	public NoValidateFuncVO(FuncVO funcVO) {
		this.func_code = funcVO.getFunc_code();
		this.func_acl_flag = funcVO.getFunc_acl_flag();
		this.func_action_falg = funcVO.getFunc_action_flag();
	}
}
