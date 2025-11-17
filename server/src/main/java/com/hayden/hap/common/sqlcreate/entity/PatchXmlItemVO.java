package com.hayden.hap.common.sqlcreate.entity;

import com.hayden.hap.common.common.entity.BaseVO;

public class PatchXmlItemVO extends BaseVO {

	private static final long serialVersionUID = 1L;

	private String itemKey;// item块的key值（目前存放的是排序值）
	private String itemName;// item块的升级内容
	private String funccode;// 功能编码
	private String tablename;// 升级表名称
	private String fixedWhere;// where子句
	private String dml_type;// 处理类型（I/U/D）
	private String fileds;// 升级的字段
	
	private String excludefields;// 过滤的字段，更新时不更新这些字段
	
	private String exeSQL;// 执行的sql
	private String isenforce;// 普通租户是否强制执行
	//只用于管理租户生成sql脚本，如2099升级包。优先级最高，
	//如果存在值，则忽略其它项的配置
	private String build_class;// 生成sql脚本扩展类
	private String upgrade_class;// 升级扩展类

	// 非xml文件中的属性字段
	private long tenantId;// 租户id
	private String xmlFileName;// xml文件名称
	private String patchName;// 补丁包名称
	
	private String patchPath;//补丁包路径
	private String versionName;//所属版本名称

	// 其中，funccode、tablename、exeSQL三个字段互斥

	public PatchXmlItemVO() {

	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getFunccode() {
		return funccode;
	}

	public void setFunccode(String funccode) {
		this.funccode = funccode;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getFixedWhere() {
		return fixedWhere;
	}

	public void setFixedWhere(String fixedWhere) {
		this.fixedWhere = fixedWhere;
	}

	public String getDml_type() {
		return dml_type;
	}

	public void setDml_type(String dml_type) {
		this.dml_type = dml_type;
	}

	public String getFileds() {
		return fileds;
	}

	public void setFileds(String fileds) {
		this.fileds = fileds;
	}

	public String getExeSQL() {
		return exeSQL;
	}

	public void setExeSQL(String exeSQL) {
		this.exeSQL = exeSQL;
	}

	public String getIsenforce() {
		return isenforce;
	}

	public void setIsenforce(String isenforce) {
		this.isenforce = isenforce;
	}

	public long getTenantId() {
		return tenantId;
	}

	public String getExcludefields() {
		return excludefields;
	}

	public void setExcludefields(String excludefields) {
		this.excludefields = excludefields;
	}

	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	public String getXmlFileName() {
		return xmlFileName;
	}

	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}

	public String getBuild_class() {
		return build_class;
	}

	public void setBuild_class(String build_class) {
		this.build_class = build_class;
	}

	public String getUpgrade_class() {
		return upgrade_class;
	}

	public void setUpgrade_class(String upgrade_class) {
		this.upgrade_class = upgrade_class;
	}

	public String getPatchName() {
		return patchName;
	}

	public void setPatchName(String patchName) {
		this.patchName = patchName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getPatchPath() {
		return patchPath;
	}

	public void setPatchPath(String patchPath) {
		this.patchPath = patchPath;
	}

}