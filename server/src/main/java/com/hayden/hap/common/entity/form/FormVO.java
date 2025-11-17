package com.hayden.hap.common.entity.form;

import com.hayden.hap.dbop.entity.BaseVO;
import com.hayden.hap.common.utils.table.annotation.Column;

/**
 * 表单公共行为接口
 * @author zhangfeng
 * @date 2018年1月23日
 */
public class FormVO extends BaseVO{

	private static final long serialVersionUID = 1L;

	@Column(type = "INTEGER", length = 50, isPK = true, allowNull = false)
	private Long formid;
	/**
	 * 表单code
	 */
	@Column(type = "varchar", length = 50, allowNull = false)
	private String form_code;
	/**
	 * 表单所属模块
	 */
	@Column(type = "varchar", length = 50)
	private String modulecode;
	/**
	 * 表单名字
	 */
	@Column(type = "varchar", length = 50, allowNull = false)
	private String form_name;
	/**
	 * 操作表
	 */
	@Column(type = "varchar", length = 100, allowNull = false)
	private String opera_table_code;
	/**
	 * 卡片列数
	 */
	@Column(type = "INTEGER", length = 1, allowNull = false, defaultIntValue = 2)
	private Integer card_num;
	/**
	 * 查询表或视图
	 */
	@Column(type = "varchar", length = 100, allowNull = false)
	private String query_table_code;
	/**
	 * 报表引擎
	 */
	@Column(type = "varchar", length = 50)
	private String report_engine_code;

	/**
	 * 查询过滤条件
	 */
	@Column(type = "varchar", length = 500)
	private String where_str;
	/**
	 * 快速列表排序
	 */
	@Column(type = "varchar", length = 500)
	private String quick_order;
	/**
	 * 扩展类
	 */
	@Column(type = "varchar", length = 100)
	private String extends_class;
	/**
	 * 是否显示帮助
	 */
	@Column(type = "INTEGER", length = 1)
	private Integer is_show_help;

	/**
	 * 列表每页显示数量
	 */
	@Column(type = "INTEGER", length = 6)
	private Integer page_num;
	/**
	 * 列表项背景颜色条件
	 */
	@Column(type = "varchar", length = 200)
	private String list_bgcolor;
	/**
	 * 描述；说明
	 */
	@Column(type = "varchar", length = 500)
	private String form_comment;

	/**
	 * 自定义模板路径
	 */
	@Column(type = "varchar", length = 500)
	private String list_jsp;

	/**
	 * 卡片页面包含jsp路径
	 */
	@Column(type = "varchar", length = 500)
	private String card_jsp;

	/**
	 * 自定义查询jsp路径
	 */
	@Column(type = "varchar", length = 500)
	private String query_jsp;

	/**
	 * 列表页面自定义js
	 */
	private String list_js;

	/**
	 * 卡片页面自定义js
	 */
	private String card_js;

	/**
	 * 客户端类型
	 */
	private String form_clienttype;

	/**
	 * 是否维护租户数据
	 */
	private Integer is_mgr_tenant_data;

	/**
	 * 是否校验一致性（并发）
	 */
	private Integer is_validate_consistency;

	/**
	 * 是否已购买
	 */
	private Integer is_buy;
	
	/**
	 * 产品项目标识
	 */
	private String product_flag;
	
	/**
	 * 普通列表排序
	 */
	private String common_order;
	/**
	 * 是否支持动态字段配置
	 */
	private Integer isDynamicConfig;

	public Integer getIsDynamicConfig() {
		return isDynamicConfig==null?0:isDynamicConfig;
	}

	public void setIsDynamicConfig(Integer isDynamicConfig) {
		this.isDynamicConfig = isDynamicConfig;
	}
	public FormVO(String tableName) {
		super(tableName);
	}

	public String getForm_code() {
		return form_code;
	}

	public void setForm_code(String form_code) {
		this.form_code = form_code;
	}

	public String getForm_name() {
		return form_name;
	}

	public void setForm_name(String form_name) {
		this.form_name = form_name;
	}

	public Integer getCard_num() {
		return card_num;
	}

	public void setCard_num(Integer card_num) {
		this.card_num = card_num;
	}

	public String getWhere_str() {
		return where_str;
	}

	public void setWhere_str(String where_str) {
		this.where_str = where_str;
	}

	public String getQuick_order() {
		return quick_order;
	}

	public void setQuick_order(String quick_order) {
		this.quick_order = quick_order;
	}

	public String getExtends_class() {
		return extends_class;
	}

	public void setExtends_class(String extends_class) {
		this.extends_class = extends_class;
	}

	public Integer getIs_show_help() {
		return is_show_help;
	}

	public void setIs_show_help(Integer is_show_help) {
		this.is_show_help = is_show_help;
	}

	public Integer getPage_num() {
		return page_num;
	}

	public void setPage_num(Integer page_num) {
		this.page_num = page_num;
	}

	public Long getFormid() {
		return formid;
	}

	public void setFormid(Long formid) {
		this.formid = formid;
	}

	public String getModulecode() {
		return modulecode;
	}

	public void setModulecode(String modulecode) {
		this.modulecode = modulecode;
	}

	public String getOpera_table_code() {
		return opera_table_code;
	}

	public void setOpera_table_code(String opera_table_code) {
		this.opera_table_code = opera_table_code;
	}

	public String getQuery_table_code() {
		return query_table_code;
	}

	public void setQuery_table_code(String query_table_code) {
		this.query_table_code = query_table_code;
	}

	public String getReport_engine_code() {
		return report_engine_code;
	}

	public void setReport_engine_code(String report_engine_code) {
		this.report_engine_code = report_engine_code;
	}

	public String getList_bgcolor() {
		return list_bgcolor;
	}

	public void setList_bgcolor(String list_bgcolor) {
		this.list_bgcolor = list_bgcolor;
	}

	public String getList_jsp() {
		return list_jsp;
	}

	public void setList_jsp(String list_jsp) {
		this.list_jsp = list_jsp;
	}

	public String getCard_jsp() {
		return card_jsp;
	}

	public void setCard_jsp(String card_jsp) {
		this.card_jsp = card_jsp;
	}

	public String getQuery_jsp() {
		return query_jsp;
	}

	public void setQuery_jsp(String query_jsp) {
		this.query_jsp = query_jsp;
	}

	public String getList_js() {
		return list_js;
	}

	public void setList_js(String list_js) {
		this.list_js = list_js;
	}

	public String getCard_js() {
		return card_js;
	}

	public void setCard_js(String card_js) {
		this.card_js = card_js;
	}

	public String getForm_comment() {
		return form_comment;
	}

	public void setForm_comment(String form_comment) {
		this.form_comment = form_comment;
	}

	public String getForm_clienttype() {
		return form_clienttype;
	}

	public void setForm_clienttype(String form_clienttype) {
		this.form_clienttype = form_clienttype;
	}

	public Integer getIs_mgr_tenant_data() {
		return is_mgr_tenant_data;
	}

	public void setIs_mgr_tenant_data(Integer is_mgr_tenant_data) {
		this.is_mgr_tenant_data = is_mgr_tenant_data;
	}

	public Integer getIs_validate_consistency() {
		return is_validate_consistency;
	}

	public void setIs_validate_consistency(Integer is_validate_consistency) {
		this.is_validate_consistency = is_validate_consistency;
	}

	public Integer getIs_buy() {
		return is_buy;
	}

	public void setIs_buy(Integer is_buy) {
		this.is_buy = is_buy;
	}

	public String getProduct_flag() {
		return product_flag;
	}

	public void setProduct_flag(String product_flag) {
		this.product_flag = product_flag;
	}

	public String getCommon_order() {
		return common_order;
	}

	public void setCommon_order(String common_order) {
		this.common_order = common_order;
	}

}
