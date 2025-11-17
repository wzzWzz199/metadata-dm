package com.hayden.hap.common.entity.form;

import com.hayden.hap.dbop.entity.BaseVO;
import com.hayden.hap.common.entity.dict.DictDataWarperVO;
import com.hayden.hap.common.utils.table.annotation.Column;

import java.util.List;

/**
 * 表单字段公共行为接口
 * @author zhangfeng
 * @date 2018年1月23日
 */
public class FormItemVO extends BaseVO {

	protected FormItemVO(String tableName) {
		super(tableName);
	}

	private static final long serialVersionUID = 1L;

	@Column(type="integer",isPK=true,length=50,allowNull=false)
	private Long fitem_id;

	/**
	 * 表单id
	 */
	@Column(type="integer",length=50,allowNull=false)
	private Long formid;

	/**
	 * 表单编码
	 */
	@Column(type="varchar",length=50,allowNull=false)
	private String form_code;

	/**
	 * 字段编码
	 */
	@Column(type="varchar",length=100,allowNull=false)
	private String fitem_code;

	/**
	 * 数据类型
	 */
	@Column(type="varchar",length=50,allowNull=false)
	private String fitem_data_type;

	/**
	 * 数据长度
	 */
	@Column(type="integer",length=10,allowNull=false)
	private Integer fitem_length;

	/**
	 * 字段名称
	 */
	@Column(type="varchar",length=100,allowNull=false)
	private String fitem_name;

	/**
	 * 输入元素
	 */
	@Column(type="varchar",length=50,allowNull=false)
	private String fitem_input_element;



	/**
	 * 输入类型
	 */
	@Column(type="varchar",length=50)
	private String fitem_input_type;



	/**
	 * 输入设定
	 */
	@Column(type="varchar",length=500)
	private String fitem_input_config;



	/**
	 * 字段类型
	 */
	@Column(type="varchar",length=50)
	private String fitem_type;
	
	/**
	 * 查询输入元素
	 */
	private String fitem_input_element_query;
	
	/**
	 * 查询输入类型
	 */
	private String fitem_input_type_query;
	
	/**
	 * 查询输入设定
	 */
	private String fitem_input_config_query;
	
	/**
	 * 是否普通查询项
	 */
	@Column(type="integer",length=2)
	private Integer fitem_com_query;	

	/**
	 * 查询缺省值
	 */
	@Column(type="varchar",length=200)
	private String fitem_query_default;
	
	/**
	 * 查询表单顺序
	 */
	private Integer fitem_query_order;

	/**
	 * 查询非空
	 */
	@Column(type="integer",length=2)
	private Integer fitem_query_notnull;
	
	/**
	 * 卡片缺省值
	 */
	@Column(type="varchar",length=200)
	private String fitem_card_default;
	
	/**
	 * 正则表达式
	 */
	@Column(type="varchar",length=100)
	private String fitem_value_regexp;

	/**
	 * 正则表达式错误提示
	 */
	@Column(type="varchar",length=100)
	private String fitem_value_regexp_msg;

	/**
	 * 取值范围
	 */
	@Column(type="varchar",length=100)
	private String fitem_value_scope;

	/**
	 * 取值范围错误提示
	 */
	@Column(type="varchar",length=100)
	private String fitem_value_scope_msg;

	/**
	 * 非空
	 */
	@Column(type="integer",length=2)
	private Integer fitem_notnull;





	/**
	 * 所占行数
	 */
	@Column(type="integer",length=10)
	private Integer fitem_card_row;

	/**
	 * 所占列数
	 */
	@Column(type="integer",length=10)
	private Integer fitem_card_column;


	/**
	 * 是否启用
	 */
	@Column(type="integer",length=2)
	private Integer fitem_isenable;

	/**
	 * 唯一组
	 */
	@Column(type="varchar",length=50)
	private String fitem_unique_group;

	/**
	 * 在唯一错误列表中？
	 */
	@Column(type="integer",length=2)
	private Integer fitem_unique_info;


	/**
	 * 是否只读
	 */
	@Column(type="integer",length=2)
	private Integer fitem_readonly;

	/**
	 * 卡片数据显示格式
	 */
	@Column(type="varchar",length=200)
	private String fitem_card_format;

	/**
	 * 列表数据显示格式
	 */
	@Column(type="varchar",length=200)
	private String fitem_list_format;
	/**
	 * 是否批量编辑
	 */
	@Column(type="integer",length=2)
	private Integer fitem_batch_edit;

	/**
	 * 查询方式1
	 */
	@Column(type="varchar",length=50)
	private String fitem_query_one;

	/**
	 * 查询方式2
	 */
	@Column(type="varchar",length=50)
	private String fitem_query_two;

	/**
	 * 字段扩展属性
	 */
	@Column(type="varchar",length=100)
	private String fitem_card_property;

	/**
	 * 说明
	 */
	@Column(type="varchar",length=500)
	private String fitem_comment;

	/**
	 * 排序
	 */
	@Column(type="integer",length=4)
	private Integer fitem_order;

	/**
	 * 只读表达式
	 */
	private String fitem_readonly_express;

	/**
	 * 隐藏表达式
	 */
	private String fitem_hide_express;

	/**
	 * 列表表头顺序
	 */
	private Integer fitem_column_order;

	/**
	 * 产品项目标识
	 */
	private String product_flag;

	/**
	 * 显示公式
	 */
	private String fitem_display_formula;

	/**
	 * 编辑公式
	 */
	private String fitem_edit_formula;	

	/**
	 * 是否在卡片中显示
	 */
	private Integer fitem_show_card;

	private String ext_item_str1;

	private String ext_item_str2;

	private String ext_item_str3;

	/**
	 * 表单字段所关联的字典数据
	 */
	private List<DictDataWarperVO> dictList;

	public Long getFitem_id() {
		return fitem_id;
	}

	public void setFitem_id(Long fitem_id) {
		this.fitem_id = fitem_id;
	}

	public Long getFormid() {
		return formid;
	}

	public void setFormid(Long formid) {
		this.formid = formid;
	}

	public String getForm_code() {
		return form_code;
	}

	public void setForm_code(String form_code) {
		this.form_code = form_code;
	}

	public String getFitem_code() {
		return fitem_code;
	}

	public void setFitem_code(String fitem_code) {
		this.fitem_code = fitem_code;
	}

	public String getFitem_data_type() {
		return fitem_data_type;
	}

	public void setFitem_data_type(String fitem_data_type) {
		this.fitem_data_type = fitem_data_type;
	}

	public Integer getFitem_length() {
		return fitem_length;
	}

	public void setFitem_length(Integer fitem_length) {
		this.fitem_length = fitem_length;
	}

	public String getFitem_name() {
		return fitem_name;
	}

	public void setFitem_name(String fitem_name) {
		this.fitem_name = fitem_name;
	}

	public String getFitem_input_element() {
		return fitem_input_element;
	}

	public void setFitem_input_element(String fitem_input_element) {
		this.fitem_input_element = fitem_input_element;
	}

	public String getFitem_input_type() {
		return fitem_input_type;
	}

	public void setFitem_input_type(String fitem_input_type) {
		this.fitem_input_type = fitem_input_type;
	}

	public String getFitem_input_config() {
		return fitem_input_config;
	}

	public void setFitem_input_config(String fitem_input_config) {
		this.fitem_input_config = fitem_input_config;
	}

	public String getFitem_type() {
		return fitem_type;
	}

	public void setFitem_type(String fitem_type) {
		this.fitem_type = fitem_type;
	}

	public String getFitem_card_default() {
		return fitem_card_default;
	}

	public void setFitem_card_default(String fitem_card_default) {
		this.fitem_card_default = fitem_card_default;
	}

	public String getFitem_value_regexp() {
		return fitem_value_regexp;
	}

	public void setFitem_value_regexp(String fitem_value_regexp) {
		this.fitem_value_regexp = fitem_value_regexp;
	}

	public String getFitem_value_regexp_msg() {
		return fitem_value_regexp_msg;
	}

	public void setFitem_value_regexp_msg(String fitem_value_regexp_msg) {
		this.fitem_value_regexp_msg = fitem_value_regexp_msg;
	}

	public String getFitem_value_scope() {
		return fitem_value_scope;
	}

	public void setFitem_value_scope(String fitem_value_scope) {
		this.fitem_value_scope = fitem_value_scope;
	}

	public String getFitem_value_scope_msg() {
		return fitem_value_scope_msg;
	}

	public void setFitem_value_scope_msg(String fitem_value_scope_msg) {
		this.fitem_value_scope_msg = fitem_value_scope_msg;
	}

	public Integer getFitem_notnull() {
		return fitem_notnull;
	}

	public void setFitem_notnull(Integer fitem_notnull) {
		this.fitem_notnull = fitem_notnull;
	}

	public Integer getFitem_card_row() {
		return fitem_card_row;
	}

	public void setFitem_card_row(Integer fitem_card_row) {
		this.fitem_card_row = fitem_card_row;
	}

	public Integer getFitem_card_column() {
		return fitem_card_column;
	}

	public void setFitem_card_column(Integer fitem_card_column) {
		this.fitem_card_column = fitem_card_column;
	}

	public Integer getFitem_isenable() {
		return fitem_isenable;
	}

	public void setFitem_isenable(Integer fitem_isenable) {
		this.fitem_isenable = fitem_isenable;
	}

	public String getFitem_unique_group() {
		return fitem_unique_group;
	}

	public void setFitem_unique_group(String fitem_unique_group) {
		this.fitem_unique_group = fitem_unique_group;
	}

	public Integer getFitem_unique_info() {
		return fitem_unique_info;
	}

	public void setFitem_unique_info(Integer fitem_unique_info) {
		this.fitem_unique_info = fitem_unique_info;
	}

	public Integer getFitem_readonly() {
		return fitem_readonly;
	}

	public void setFitem_readonly(Integer fitem_readonly) {
		this.fitem_readonly = fitem_readonly;
	}

	public String getFitem_card_format() {
		return fitem_card_format;
	}

	public void setFitem_card_format(String fitem_card_format) {
		this.fitem_card_format = fitem_card_format;
	}

	public String getFitem_list_format() {
		return fitem_list_format;
	}

	public void setFitem_list_format(String fitem_list_format) {
		this.fitem_list_format = fitem_list_format;
	}

	public String getFitem_query_one() {
		return fitem_query_one;
	}

	public void setFitem_query_one(String fitem_query_one) {
		this.fitem_query_one = fitem_query_one;
	}

	public String getFitem_query_two() {
		return fitem_query_two;
	}

	public void setFitem_query_two(String fitem_query_two) {
		this.fitem_query_two = fitem_query_two;
	}

	public String getFitem_card_property() {
		return fitem_card_property;
	}

	public void setFitem_card_property(String fitem_card_property) {
		this.fitem_card_property = fitem_card_property;
	}

	public String getFitem_comment() {
		return fitem_comment;
	}

	public void setFitem_comment(String fitem_comment) {
		this.fitem_comment = fitem_comment;
	}

	public Integer getFitem_order() {
		return fitem_order;
	}

	public void setFitem_order(Integer fitem_order) {
		this.fitem_order = fitem_order;
	}

	public String getFitem_readonly_express() {
		return fitem_readonly_express;
	}

	public void setFitem_readonly_express(String fitem_readonly_express) {
		this.fitem_readonly_express = fitem_readonly_express;
	}

	public String getFitem_hide_express() {
		return fitem_hide_express;
	}

	public void setFitem_hide_express(String fitem_hide_express) {
		this.fitem_hide_express = fitem_hide_express;
	}

	public Integer getFitem_column_order() {
		return fitem_column_order;
	}

	public void setFitem_column_order(Integer fitem_column_order) {
		this.fitem_column_order = fitem_column_order;
	}

	public String getProduct_flag() {
		return product_flag;
	}

	public void setProduct_flag(String product_flag) {
		this.product_flag = product_flag;
	}

	public String getFitem_display_formula() {
		return fitem_display_formula;
	}

	public void setFitem_display_formula(String fitem_display_formula) {
		this.fitem_display_formula = fitem_display_formula;
	}

	public String getFitem_edit_formula() {
		return fitem_edit_formula;
	}

	public void setFitem_edit_formula(String fitem_edit_formula) {
		this.fitem_edit_formula = fitem_edit_formula;
	}

	public Integer getFitem_show_card() {
		return fitem_show_card;
	}

	public void setFitem_show_card(Integer fitem_show_card) {
		this.fitem_show_card = fitem_show_card;
	}

	public String getExt_item_str3() {
		return ext_item_str3;
	}

	public void setExt_item_str3(String ext_item_str3) {
		this.ext_item_str3 = ext_item_str3;
	}

	public String getExt_item_str1() {
		return ext_item_str1;
	}

	public void setExt_item_str1(String ext_item_str1) {
		this.ext_item_str1 = ext_item_str1;
	}

	public String getExt_item_str2() {
		return ext_item_str2;
	}

	public void setExt_item_str2(String ext_item_str2) {
		this.ext_item_str2 = ext_item_str2;
	}

	public List<DictDataWarperVO> getDictList() {
		return dictList;
	}

	public void setDictList(List<DictDataWarperVO> dictList) {
		this.dictList = dictList;
	}	

	public String getFitem_input_element_query() {
		return fitem_input_element_query;
	}

	public void setFitem_input_element_query(String fitem_input_element_query) {
		this.fitem_input_element_query = fitem_input_element_query;
	}

	public String getFitem_input_type_query() {
		return fitem_input_type_query;
	}

	public void setFitem_input_type_query(String fitem_input_type_query) {
		this.fitem_input_type_query = fitem_input_type_query;
	}
	
	public Integer getFitem_com_query() {
		return fitem_com_query;
	}

	public void setFitem_com_query(Integer fitem_com_query) {
		this.fitem_com_query = fitem_com_query;
	}
	
	public String getFitem_input_config_query() {
		return fitem_input_config_query;
	}

	public void setFitem_input_config_query(String fitem_input_config_query) {
		this.fitem_input_config_query = fitem_input_config_query;
	}
	
	public String getFitem_query_default() {
		return fitem_query_default;
	}
	
	public void setFitem_query_default(String fitem_query_default) {
		this.fitem_query_default = fitem_query_default;
	}
	
	public Integer getFitem_query_order() {
		return fitem_query_order;
	}

	public void setFitem_query_order(Integer fitem_query_order) {
		this.fitem_query_order = fitem_query_order;
	}
	
	public Integer getFitem_query_notnull() {
		return fitem_query_notnull;
	}

	public void setFitem_query_notnull(Integer fitem_query_notnull) {
		this.fitem_query_notnull = fitem_query_notnull;
	}

	public Integer getFitem_batch_edit() {
		return fitem_batch_edit;
	}

	public void setFitem_batch_edit(Integer fitem_batch_edit) {
		this.fitem_batch_edit = fitem_batch_edit;
	}
}