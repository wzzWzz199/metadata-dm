package com.hayden.hap.common.formmgr.entity;

import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.utils.VariableUtils;


/**
 * 做名称转换时的数据传输对象
 * @author zhangfeng
 * @date 2016年9月8日
 */
public class Id2NameVO {
	private String tableName;
	private String pkColName;
	private QueryselectorInputConfigVO inputConfigVO;
	private Long tenantid;
	private String uniqueColName;

	private String where;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPkColName() {
		return pkColName;
	}
	public void setPkColName(String pkColName) {
		this.pkColName = pkColName;
	}
	public Long getTenantid() {
		return tenantid;
	}
	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}

	public QueryselectorInputConfigVO getInputConfigVO() {
		return inputConfigVO;
	}
	public void setInputConfigVO(QueryselectorInputConfigVO inputConfigVO) {
		this.inputConfigVO = inputConfigVO;
	}
	public String getUniqueColName() {
		return uniqueColName;
	}
	public void setUniqueColName(String uniqueColName) {
		this.uniqueColName = uniqueColName;
	}
	
	public String getWhere() {
		return where;
	}
	
	public void setWhere(String where) {
		if(VariableUtils.hasFitemParam(where)) {
			throw new HDRuntimeException("查询选择返名称条件中含有不支持的字段变量");
		}
		
		this.where = where!=null?where.trim():where;
	}
}
