package com.hayden.hap.common.entity.button;

import com.hayden.hap.dbop.entity.CommonVO;

public class ButtonCacheVO extends CommonVO implements
        Comparable<ButtonCacheVO> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private Long btn_id;
    private String btn_code;
    private String btn_name;
    private Long formid;
    private String btn_type;
    private String top_func_code;
    private String func_code;
    private String func_name;
    private Integer btn_order;
    private String btn_group;
    private String btn_property;
    private String btn_show_control;
    private String btn_param;
    private String func_clienttype;
    private String btn_show_group;
    private String btn_inputconfig;
    /**
     * 挂接的工作流类型
     */
    private String func_wf_link_proctype;

    /**
     * 审核结束后允许的操作
     */
    private String func_wf_finish_allow;

    public ButtonCacheVO() {
    }
    
    public ButtonCacheVO(ButtonVO vo) {
		this.setBtn_id(vo.getLong("btn_id"));
		this.setBtn_code(vo.getString("btn_code"));
		this.setFormid(vo.getLong("formid"));
		this.setBtn_name(vo.getString("btn_name"));
		this.setBtn_type(vo.getString("btn_type"));
		this.setTop_func_code(vo.getString("top_func_code"));
		this.setFunc_code(vo.getString("func_code"));
		this.setFunc_name(vo.getString("func_name"));
		this.setBtn_order(vo.getInt("btn_order"));
		this.setBtn_group(vo.getString("btn_group"));
		this.setBtn_show_control(vo.getString("btn_show_control"));
		this.setBtn_param(vo.getString("btn_param"));
		this.setFunc_clienttype(vo.getString("func_clienttype"));
		this.setFunc_wf_finish_allow(vo.getString("func_wf_finish_allow"));
		this.setFunc_wf_link_proctype(vo.getString("func_wf_link_proctype"));
		this.setBtn_show_group(vo.getString("btn_show_group"));
		this.setBtn_inputconfig(vo.getString("btn_inputconfig"));
    }

    public ButtonCacheVO(String btnCode, String btnName) {
        this.btn_code = btnCode;
        this.btn_name = btnName;
    }
    
    public ButtonCacheVO(String btnCode) {
        this.btn_code = btnCode;
    }

    public Long getBtn_id() {
        return btn_id;
    }

    public void setBtn_id(Long btn_id) {
        this.btn_id = btn_id;
    }

    public String getBtn_code() {
        return btn_code;
    }

    public void setBtn_code(String btn_code) {
        this.btn_code = btn_code;
    }

    public String getBtn_name() {
        return btn_name;
    }

    public void setBtn_name(String btn_name) {
        this.btn_name = btn_name;
    }

    public Long getFormid() {
        return formid;
    }

    public void setFormid(Long formid) {
        this.formid = formid;
    }

    public String getBtn_type() {
        return btn_type;
    }

    public void setBtn_type(String btn_type) {
        this.btn_type = btn_type;
    }

    public String getTop_func_code() {
        return top_func_code;
    }

    public void setTop_func_code(String top_func_code) {
        this.top_func_code = top_func_code;
    }

    public String getFunc_code() {
        return func_code;
    }

    public void setFunc_code(String func_code) {
        this.func_code = func_code;
    }

    public String getFunc_name() {
        return func_name;
    }

    public void setFunc_name(String func_name) {
        this.func_name = func_name;
    }

    public Integer getBtn_order() {
        return btn_order;
    }

    public void setBtn_order(Integer btn_order) {
        this.btn_order = btn_order;
    }

    public String getBtn_group() {
        return btn_group;
    }

    public void setBtn_group(String btn_group) {
        this.btn_group = btn_group;
    }

    public String getBtn_property() {
        return btn_property;
    }

    public void setBtn_property(String btn_property) {
        this.btn_property = btn_property;
    }

    public String getBtn_show_control() {
        return btn_show_control;
    }

    public void setBtn_show_control(String btn_show_control) {
        this.btn_show_control = btn_show_control;
    }

    public String getBtn_param() {
        return btn_param;
    }

    public void setBtn_param(String btn_param) {
        this.btn_param = btn_param;
    }

    public String getFunc_clienttype() {
        return func_clienttype;
    }

    public void setFunc_clienttype(String func_clienttype) {
        this.func_clienttype = func_clienttype;
    }

    public String getFunc_wf_link_proctype() {
        return func_wf_link_proctype;
    }

    public void setFunc_wf_link_proctype(String func_wf_link_proctype) {
        this.func_wf_link_proctype = func_wf_link_proctype;
    }

    public String getFunc_wf_finish_allow() {
        return func_wf_finish_allow;
    }

    public void setFunc_wf_finish_allow(String func_wf_finish_allow) {
        this.func_wf_finish_allow = func_wf_finish_allow;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int compareTo(ButtonCacheVO o) {
        if (this.btn_order == null)
            return -1;
        if (o.btn_order == null)
            return 1;
        if (this.btn_order.intValue() == o.btn_order.intValue()) {
            if (this.btn_id < o.btn_id)
                return -1;
            if (this.btn_id > o.btn_id)
                return 1;
            if (this.btn_id.equals(o.btn_id))
                return 0;
        }
        return this.btn_order > o.btn_order ? 1 : -1;
    }

    public String getBtn_show_group() {
        return btn_show_group;
    }

    public void setBtn_show_group(String btn_show_group) {
        this.btn_show_group = btn_show_group;
    }

	public String getBtn_inputconfig() {
		return btn_inputconfig;
	}

	public void setBtn_inputconfig(String btn_inputconfig) {
		this.btn_inputconfig = btn_inputconfig;
	}

}
