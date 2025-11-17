package com.hayden.hap.common.export.way;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.enumerate.ExportTypeEnum;
import com.hayden.hap.common.enumerate.ExportWayType;
import com.hayden.hap.common.export.itf.IExportWay;
import org.apache.commons.lang.StringUtils;

/**
 * 导出方式工厂
 * @author liyan
 * @date 2017年6月28日
 */
public class ExportWayFactory {

	private String exportWay = "";
	private String fileType = "";
	
	public ExportWayFactory(String exportWay, String fileType) throws HDException {
		if(StringUtils.isBlank(exportWay)) {
			throw new HDException("导出方式 不能为空");
		}
		this.exportWay = exportWay;
		this.fileType = fileType;
	}
	
	/**
	 * 导出 方式子类
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年6月29日   
	 */
	public IExportWay getExportWay() throws HDException {
		if(ExportWayType.TXT.getCode().equals(exportWay)) {
			return new TxtExportWay();
		}else if(ExportWayType.EXCEL.getCode().equals(exportWay)) {
			return new ExcelExportWay();
		}else if(ExportWayType.IMPORT.getCode().equals(exportWay)) {
			if(fileType.equalsIgnoreCase(ExportTypeEnum.EXCEL2003.getName())){
				return new ImportExcel2003ExportWay();
			}else if(fileType.equalsIgnoreCase(ExportTypeEnum.EXCEL2007.getName())){
				return new ImportExcel2007ExportWay();
			}else{
				throw new HDException("导出文件类型有误"+exportWay);
			}
		}else if(ExportWayType.TEMPLATE.getCode().equals(exportWay)) {
			if(fileType.equalsIgnoreCase(ExportTypeEnum.EXCEL2003.getName())){
				return new TemplateExcel2003ExportWay();
			}else if(fileType.equalsIgnoreCase(ExportTypeEnum.EXCEL2007.getName())){
				return new TemplateExcel2007ExportWay();
			}else if(fileType.equalsIgnoreCase(ExportTypeEnum.TXT.getName())){
				return new TemplateTxtExportWay();
			}else{
				throw new HDException("导出文件类型有误"+exportWay);
			}
		}
		//新增支持form表单导出方式
		else if (ExportWayType.FORM.getCode().equals(exportWay)){
			if(fileType.equalsIgnoreCase(ExportTypeEnum.EXCEL2003.getName())){
				return new FormExcel2003ExportWay();
			}else if(fileType.equalsIgnoreCase(ExportTypeEnum.EXCEL2007.getName())){
				return new FormExcel2007ExportWay();
			}else{
				throw new HDException("导出文件类型有误"+exportWay);
			}
		}
		else{
			throw new HDException("导出方式有误"+exportWay);
		}
	}
	
}
