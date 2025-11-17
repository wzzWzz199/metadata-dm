package com.hayden.hap.common.formmgr.entity;

import com.hayden.hap.common.common.entity.FuncSelectorParamVO;
import com.hayden.hap.common.common.entity.QuerySelectorParamVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.refresh.RefreshVO;
import com.hayden.hap.common.func.entity.FuncVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;

/**
 * 数据传输对象，主要用来承载从(ListFormCtrl、CardFromCtrl)到CardFormService或层的参数
 * @author zhangfeng
 * @date 2015年11月3日
 */
public class FormParamVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 模块编码
	 */
	private String moduleCode;
	
	/**
	 * 功能编码
	 */
	private String funcCode;
	
	/**
	 * 请求参数
	 */
	private ReqParamVO reqParamVO;
	
	/**
	 * 数据体字符串
	 */
	private String dataBody;
	
	/**
	 * request
	 */
	private HttpServletRequest request;
	
	/**
	 * (字段)查询选择页面的相关参数vo
	 */
	private QuerySelectorParamVO querySelectorParamVO;
	
	/**
	 * (页面)查询选择页面的相关参数VO
	 */
	private FuncSelectorParamVO funcSelectorParamVO;
	
	/**
	 * response
	 */
	private HttpServletResponse response;
	
	/**
	 * 功能vo
	 */
	private FuncVO funcVO;
	
	/**
	 * 额外条件
	 */
	private String extWhere;
	
	/**
	 * 
	 */
	private String isExport;

	/**
	 * 排序
	 */
	private String extOrder;
	
	/**
	 * 字段编码（查询选择时使用）
	 */
	private String fitemCode;
	
	/**
	 * isShowListSelect:(是否按列表展示字段查询0：否；1：是).
	 */
	private Integer isShowListSelect =0;
	/**
	 * 列表查询是否查询附件信息0：否，1:是
	 */
	private Integer isAttach = 1;
	
	/**
	 * 用于指定自定义的报表文件
	 */
	private String rpx;
	/**
	 * 打开报表页时是否自动加载报表，默认为否
	 */
	private boolean autoLoadReport=false;
	
	/**
	 * 返回结果对象，一般用来承载错误信息
	 */
	private ReturnResult<Object> returnMessage;
	
	/**
	 * 主键集合
	 */
	private List<Long> pkValues;
	
	/**
	 * 刷新对象，告诉前端要刷新那些位置
	 */
	private RefreshVO refreshVO;
	
	/**
	 * 是否查询选择
	 */
	private boolean isQuerySelector = false;
	
	/**
	 * 来源功能编码（查询选择情况）
	 */
	private String funcCodeSource;

	public Integer getIsShowListSelect() {
		return isShowListSelect;
	}

	public void setIsShowListSelect(Integer isShowListSelect) {
		this.isShowListSelect = isShowListSelect;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getFuncCode() {
		return funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}

	public String getDataBody() {
		return dataBody;
	}

	public void setDataBody(String dataBody) {
		this.dataBody = dataBody;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public ReqParamVO getReqParamVO() {
		return reqParamVO;
	}

	public void setReqParamVO(ReqParamVO reqParamVO) {
		this.reqParamVO = reqParamVO;
	}

	public QuerySelectorParamVO getQuerySelectorParamVO() {
		return querySelectorParamVO;
	}

	public void setQuerySelectorParamVO(QuerySelectorParamVO querySelectorParamVO) {
		this.querySelectorParamVO = querySelectorParamVO;
	}

	public FuncVO getFuncVO() {
		return funcVO;
	}

	public void setFuncVO(FuncVO funcVO) {
		this.funcVO = funcVO;
	}

	public String getExtWhere() {
		return extWhere;
	}

	public void setExtWhere(String extWhere) {
		this.extWhere = extWhere;
	}

	public String getFitemCode() {
		return fitemCode;
	}

	public void setFitemCode(String fitemCode) {
		this.fitemCode = fitemCode;
	}

	public FuncSelectorParamVO getFuncSelectorParamVO() {
		return funcSelectorParamVO;
	}

	public void setFuncSelectorParamVO(FuncSelectorParamVO funcSelectorParamVO) {
		this.funcSelectorParamVO = funcSelectorParamVO;
	}

	public List<Long> getPkValues() {
		return pkValues;
	}

	public void setPkValues(List<Long> pkValues) {
		this.pkValues = pkValues;
	}


	public String getRpx() {
		return rpx;
	}

	public void setRpx(String rpx) {
		this.rpx = rpx;
	}

	public Integer getIsAttach() {
		return isAttach;
	}

	public void setIsAttach(Integer isAttach) {
		this.isAttach = isAttach;
	}

	public boolean isAutoLoadReport() {
		return autoLoadReport;
	}

	public void setAutoLoadReport(boolean autoLoadReport) {
		this.autoLoadReport = autoLoadReport;
	}

	public ReturnResult<Object> getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(ReturnResult<Object> returnMessage) {
		this.returnMessage = returnMessage;
	}

	public RefreshVO getRefreshVO() {
		return refreshVO;
	}

	public void setRefreshVO(RefreshVO refreshVO) {
		this.refreshVO = refreshVO;
	}

	public boolean isQuerySelector() {
		return isQuerySelector;
	}

	public void setQuerySelector(boolean isQuerySelector) {
		this.isQuerySelector = isQuerySelector;
	}

	public String getFuncCodeSource() {
		return funcCodeSource;
	}

	public void setFuncCodeSource(String funcCodeSource) {
		this.funcCodeSource = funcCodeSource;
	}

	public String getExtOrder() {
		return extOrder;
	}

	public void setExtOrder(String extOrder) {
		this.extOrder = extOrder;
	}
	
	public String getIsExport() {
		return isExport;
	}

	public void setIsExport(String isExport) {
		this.isExport = isExport;
	}
}
