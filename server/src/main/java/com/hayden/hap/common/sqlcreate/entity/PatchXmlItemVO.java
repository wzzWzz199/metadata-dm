package com.hayden.hap.common.sqlcreate.entity;

import com.hayden.hap.dbop.entity.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
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

}