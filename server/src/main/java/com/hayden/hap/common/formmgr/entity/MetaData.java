/**
 * 
 */
package com.hayden.hap.common.formmgr.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.export.entity.ExportTemplateVO;
import com.hayden.hap.common.form.entity.*;
import com.hayden.hap.common.formmgr.itf.ICardData;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncVO;

import java.util.List;
import java.util.Map;

/**
 * @author zhangfeng
 *
 */
public class MetaData extends PureVO{
	
	private static final long serialVersionUID = 1L;
	
	private FuncVO funcVO;
	private FormPCVO formVO;
	private List<FormItemPCVO> formItemVOs;
	private List<FormItemPCVO> queryItemVOs;
	private List<? extends ButtonVO> buttons;
	private Map<String,List<DictDataWarperVO>> dictMap;	
	private String pkColName;
	private List<FuncLinkVO> funcLinkVOs;
	private List<FormQueryVO> formQueryVOs;
	private List<MetaData> children;
	private List<ExportTemplateVO> exportQueryList;
	private List<ExcelTemplateVO> importQueryList;	
	private Boolean isTreeMaintenance = false;
	private AbstractVO queryDefaultVO;
	private List<FormConditionVO> conditionVOs;
	private List<FormConditionDetailVO> conditionDetail;

	public List<FormConditionVO> getConditionVOs() {
		return conditionVOs;
	}

	public void setConditionVOs(List<FormConditionVO> conditionVOs) {
		this.conditionVOs = conditionVOs;
	}

	public List<FormConditionDetailVO> getConditionDetail() {
		return conditionDetail;
	}

	public void setConditionDetail(List<FormConditionDetailVO> conditionDetail) {
		this.conditionDetail = conditionDetail;
	}

	// 扩展数据。传递一些扩展数据，供前端使用。 add by lengzy  2017-08-31
	private Map<String, Object> extData;   
	/**
	 * 批量编辑字段 add by haocs  2019-03-22
	 */
	private List<String> batchEditFormItems;
	/**
	 * 卡片VO
	 */
	private ICardData cardEditVO;
	/**
	 * 视图类型：1列表，2卡片
	 */
	private Integer viewType = 1;
	
	/**
	 * 通知前端是否操作树
	 */
	private Boolean isOperationTree = false;
	
	/**
	 * @author yinbinchen
	 * 树结构字段与数据字段关系
	 */
	private Map<String,TreeToDataField> treeToDataFiled;
	
	/**
	 * @author yinbinchen
	 * 全息查询字段默认值（全息树默认选中节点的dictdataid）
	 */
	private Map<String,String> defaultfilter;
	
	@JsonInclude(Include.NON_NULL)
	private ListDataVO voset;
	
	@JsonInclude(Include.NON_NULL)
	private FuncVO sourceFuncVO;
	
	public FuncVO getFuncVO() {
		return funcVO;
	}
	public void setFuncVO(FuncVO funcVO) {
		this.funcVO = funcVO;
	}
	public FormPCVO getFormVO() {
		return formVO;
	}
	public void setFormVO(FormPCVO formVO) {
		this.formVO = formVO;
	}
	public Map<String, List<DictDataWarperVO>> getDictMap() {
		return dictMap;
	}
	public void setDictMap(Map<String, List<DictDataWarperVO>> dictMap) {
		this.dictMap = dictMap;
	}
	public List<MetaData> getChildren() {
		return children;
	}
	public void setChildren(List<MetaData> children) {
		this.children = children;
	}
	public List<FormItemPCVO> getFormItemVOs() {
		return formItemVOs;
	}
	public void setFormItemVOs(List<FormItemPCVO> formItemVOs) {
		this.formItemVOs = formItemVOs;
	}
	public List<? extends ButtonVO> getButtons() {
		return buttons;
	}
	public void setButtons(List<? extends ButtonVO> buttons) {
		this.buttons = buttons;
	}
	public String getPkColName() {
		return pkColName;
	}
	public void setPkColName(String pkColName) {
		this.pkColName = pkColName;
	}
	public List<FuncLinkVO> getFuncLinkVOs() {
		return funcLinkVOs;
	}
	public void setFuncLinkVOs(List<FuncLinkVO> funcLinkVOs) {
		this.funcLinkVOs = funcLinkVOs;
	}
	public FuncVO getSourceFuncVO() {
		return sourceFuncVO;
	}
	public void setSourceFuncVO(FuncVO sourceFuncVO) {
		this.sourceFuncVO = sourceFuncVO;
	}
	public List<FormQueryVO> getFormQueryVOs() {
		return formQueryVOs;
	}
	public void setFormQueryVOs(List<FormQueryVO> formQueryVOs) {
		this.formQueryVOs = formQueryVOs;
	}
	
	public List<FormItemPCVO> getQueryItemVOs() {
		return queryItemVOs;
	}
	public void setQueryItemVOs(List<FormItemPCVO> queryItemVOs) {
		this.queryItemVOs = queryItemVOs;
	}
	public List<ExportTemplateVO> getExportQueryList() {
		return exportQueryList;
	}
	public void setExportQueryList(List<ExportTemplateVO> exportQueryList) {
		this.exportQueryList = exportQueryList;
	}
	public List<String> getBatchEditFormItems() {
		return batchEditFormItems;
	}
	public void setBatchEditFormItems(List<String> batchEditFormItems) {
		this.batchEditFormItems = batchEditFormItems;
	}
	public List<ExcelTemplateVO> getImportQueryList() {
		return importQueryList;
	}
	public void setImportQueryList(List<ExcelTemplateVO> importQueryList) {
		this.importQueryList = importQueryList;
	}
	public ListDataVO getVoset() {
		return voset;
	}
	public void setVoset(ListDataVO voset) {
		this.voset = voset;
	}
	public Boolean getIsTreeMaintenance() {
		return isTreeMaintenance;
	}
	public void setIsTreeMaintenance(Boolean isTreeMaintenance) {
		this.isTreeMaintenance = isTreeMaintenance;
	}
	public AbstractVO getQueryDefaultVO() {
		return queryDefaultVO;
	}
	public void setQueryDefaultVO(AbstractVO queryDefaultVO) {
		this.queryDefaultVO = queryDefaultVO;
	}
	public ICardData getCardEditVO() {
		return cardEditVO;
	}
	public void setCardEditVO(ICardData cardEditVO) {
		this.cardEditVO = cardEditVO;
	}
	public Integer getViewType() {
		return viewType;
	}
	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}
	public Map<String, Object> getExtData() {
		return extData;
	}
	public void setExtData(Map<String, Object> extData) {
		this.extData = extData;
	}
	public Boolean getIsOperationTree() {
		return isOperationTree;
	}
	public void setIsOperationTree(Boolean isOperationTree) {
		this.isOperationTree = isOperationTree;
	}
	public Map<String, TreeToDataField> getTreeToDataFiled() {
		return treeToDataFiled;
	}
	public void setTreeToDataFiled(Map<String, TreeToDataField> treeToDataFiled) {
		this.treeToDataFiled = treeToDataFiled;
	}
	public Map<String, String> getDefaultfilter() {
		return defaultfilter;
	}
	public void setDefaultfilter(Map<String, String> defaultfilter) {
		this.defaultfilter = defaultfilter;
	}
	
	
}
