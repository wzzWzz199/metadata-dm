package com.hayden.hap.common.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 请求的参数对象
 * @author zhangfeng
 * @date 2015年10月16日
 */
public class ReqParamVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 升序排序
	 */
	public static final String SORT_ASC = "asc";
	
	/**
	 * 降序排序
	 */
	public static final String SORT_DESC = "desc";

	/**
	 * 行为字符串
	 */
	private String act;
	
	/**
	 * 分页的第几页
	 */
	private Integer page;
	
	/**
	 * 分页的每页大小
	 */
	private Integer rows;
	
	/**
	 * 父功能实体id
	 */
	private Long parentEntityId;
	
	/**
	 * 父功能编码
	 */
	private String parentFuncCode;
	
	/**
	 * 顶级功能实体id
	 */
	private Long topEntityId;
	
	/**
	 * 顶级功能编码
	 */
	private String topFuncCode;
	
	/**
	 * 是否子功能
	 */
	private Integer isChildFunc;
	
	/**
	 * isLinkedChildFunc:(是否关联子功能数据，移动端).
	 */
	private Integer isLCFunc;
	
	/**
	 * level:TODO(关联查询层级).
	 */
	private Integer level = 0;

	/**
	 * 返回的URL
	 */
	private String rtnURL;
	
	/**
	 * 编辑的主键
	 */
	private Long editId;
	
	/**
	 * 查询字符串
	 */
	private String queryParam;
	
	/**
	 * queryStr:(查询字符串，不指定查询列).
	 */
	private String queryStr;
	
	/**
	 * 高级查询串
	 */
	private String formQuerySql;
	
	/**
	 * 查询模板id
	 */
	private Long formQueryId;	

	/**
	 * 排序字段
	 */
	private String sidx; 
	
	/**
	 * 排序方式
	 */
	private String sord;
	
	/**
	 * 是否单条进卡片
	 */
	private Integer isForwardToCard;

	/**
	 * 添加页面，附件上传时临时主键
	 */
	private List<String> attach_editid;
	
	/**
	 * 添加页面，附件上传时临时业务记录创建时间
	 */
	private List<String> record_created_dt;
	
	/**
	 * 全息树选中的节点id
	 */
	private String funcTreeNodeId;
	
	/**
	 * 全息树选中的节点编码
	 */
	private String funcTreeNodeCode;
	
	/**
	 * 全息查询条件
	 */
	private String funcTreeQueryParam;
	
	/**
	 * 是否只读
	 */
	private Integer readonly;
	
	/**
	 * 租户id
	 */
	private Long tenantid;
	
	/**
	 * 租户是否改变
	 */
	private boolean tenantChanged = false;

	/**
	 * 查询选择的功能
	 */
	private String querySelectFunc;
	
	/**
	 * @return 行为字符串
	 */
	public String getAct() {
		return act;
	}

	/**
	 * @param act the act to set
	 */
	public void setAct(String act) {
		this.act = act;
	}

	/**
	 * @return 分页的第几页
	 */
	public Integer getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/**
	 * @return 分页的每页大小
	 */
	public Integer getRows() {
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(Integer rows) {
		this.rows = rows;
	}

	/**
	 * @return 父功能实体id
	 */
	public Long getParentEntityId() {
		return parentEntityId;
	}

	/**
	 * @param parentEntityId the parentEntityId to set
	 */
	public void setParentEntityId(Long parentEntityId) {
		this.parentEntityId = parentEntityId;
	}

	/**
	 * @return 父功能编码
	 */
	public String getParentFuncCode() {
		return parentFuncCode;
	}

	/**
	 * @param parentFuncCode the parentFuncCode to set
	 */
	public void setParentFuncCode(String parentFuncCode) {
		this.parentFuncCode = parentFuncCode;
	}

	/**
	 * @return 返回的URL
	 */
	public String getRtnURL() {
		return rtnURL;
	}

	/**
	 * @param rtnURL the rtnURL to set
	 */
	public void setRtnURL(String rtnURL) {
		this.rtnURL = rtnURL;
	}

	/**
	 * @return 编辑的主键
	 */
	public Long getEditId() {
		return editId;
	}

	/**
	 * @param editId the editId to set
	 */
	public void setEditId(Long editId) {
		this.editId = editId;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}

	public Long getTopEntityId() {
		return topEntityId;
	}

	public void setTopEntityId(Long topEntityId) {
		this.topEntityId = topEntityId;
	}

	public String getTopFuncCode() {
		return topFuncCode;
	}

	public void setTopFuncCode(String topFuncCode) {
		this.topFuncCode = topFuncCode;
	}

	public Integer getIsChildFunc() {
		return isChildFunc;
	}

	public void setIsChildFunc(Integer isChildFunc) {
		this.isChildFunc = isChildFunc;
	}

	public Integer getIsForwardToCard() {
		return isForwardToCard;
	}

	public void setIsForwardToCard(Integer isForwardToCard) {
		this.isForwardToCard = isForwardToCard;
	}

	public List<String> getAttach_editid() {
		return attach_editid;
	}

	public void setAttach_editid(List<String> attach_editid) {
		this.attach_editid = attach_editid;
	}
	
	public List<String> getRecord_created_dt() {
		return record_created_dt;
	}

	public void setRecord_created_dt(List<String> record_created_dt) {
		this.record_created_dt = record_created_dt;
	}

	public String getFuncTreeNodeId() {
		return funcTreeNodeId;
	}

	public void setFuncTreeNodeId(String funcTreeNodeId) {
		this.funcTreeNodeId = funcTreeNodeId;
	}

	public String getFuncTreeQueryParam() {
		return funcTreeQueryParam;
	}

	public void setFuncTreeQueryParam(String funcTreeQueryParam) {
		this.funcTreeQueryParam = funcTreeQueryParam;
	}

	public Integer getReadonly() {
		return readonly;
	}

	public void setReadonly(Integer readonly) {
		this.readonly = readonly;
	}

	public String getFormQuerySql() {
		return formQuerySql;
	}

	public void setFormQuerySql(String formQuerySql) {
		this.formQuerySql = formQuerySql;
	}

	public Long getFormQueryId() {
		return formQueryId;
	}

	public void setFormQueryId(Long formQueryId) {
		this.formQueryId = formQueryId;
	}
	
	@Deprecated
	public Integer getIsLCFunc() {
		return isLCFunc;
	}

	@Deprecated
	public void setIsLCFunc(Integer isLCFunc) {
		this.isLCFunc = isLCFunc;
	}
	
	
	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public String getFuncTreeNodeCode() {
		return funcTreeNodeCode;
	}

	public void setFuncTreeNodeCode(String funcTreeNodeCode) {
		this.funcTreeNodeCode = funcTreeNodeCode;
	}

	public Long getTenantid() {
		return tenantid;
	}

	public void setTenantid(Long tenantid) {
		this.tenantid = tenantid;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public boolean isTenantChanged() {
		return tenantChanged;
	}

	public void setTenantChanged(boolean tenantChanged) {
		this.tenantChanged = tenantChanged;
	}

	public String getQuerySelectFunc() {
		return querySelectFunc;
	}

	public void setQuerySelectFunc(String querySelectFunc) {
		this.querySelectFunc = querySelectFunc;
	}
}
