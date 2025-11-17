package com.hayden.hap.common.export.entity;

import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;

import java.util.List;

/**
 * 导出时，存放导入导出模板
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public class TemplateVO {
	
	private ExportTemplateVO exportTemplateVO;
	
	private List<ExportTemplateItemVO> exportTemplateVOList;
	
	private ExcelTemplateVO excelTemplateVO;
	
	private List<ExcelTemplateItemVO> excelTemplateVOList;
	
	private String fileType;

	private String dynamicParam; //动态参数-fitleType为form时 会用到该字段

	public ExportTemplateVO getExportTemplateVO() {
		return exportTemplateVO;
	}

	public void setExportTemplateVO(ExportTemplateVO exportTemplateVO) {
		this.exportTemplateVO = exportTemplateVO;
	}

	public List<ExportTemplateItemVO> getExportTemplateVOList() {
		return exportTemplateVOList;
	}

	public void setExportTemplateVOList(
			List<ExportTemplateItemVO> exportTemplateVOList) {
		this.exportTemplateVOList = exportTemplateVOList;
	}

	public ExcelTemplateVO getExcelTemplateVO() {
		return excelTemplateVO;
	}

	public void setExcelTemplateVO(ExcelTemplateVO excelTemplateVO) {
		this.excelTemplateVO = excelTemplateVO;
	}

	public List<ExcelTemplateItemVO> getExcelTemplateVOList() {
		return excelTemplateVOList;
	}

	public void setExcelTemplateVOList(List<ExcelTemplateItemVO> excelTemplateVOList) {
		this.excelTemplateVOList = excelTemplateVOList;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getDynamicParam() {
		return dynamicParam;
	}

	public void setDynamicParam(String dynamicParam) {
		this.dynamicParam = dynamicParam;
	}
}
