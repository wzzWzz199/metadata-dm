package com.hayden.hap.common.excel.utils;

import org.apache.poi.ss.usermodel.*;

/**
 * POI复制工具类
 * @author ZMing
 * @date 2016年9月7日
 */
public class POICopyUtils {

	/**
	 * Sheet复制 
	 * @param wb 目标workbook
	 * @param fromSheet 源sheet页
	 * @param toSheet 目标sheet页
	 */
	public static void copySheet(Workbook wb,Sheet fromSheet, Sheet toSheet){
		copySheet(wb,fromSheet,toSheet,true,true);
	}
	
	/**
	 * Sheet复制
	 * @param fromSheet 源sheet页
	 * @param toSheet 目标sheet页
	 * @param copyValueFlag 是否复制值  true则连同cell的内容一起复制
	 * @param copyCellStyleFlag 是否复制样式 true则连同样式一起复制
	 */
	public static void copySheet(Workbook wb,Sheet fromSheet, Sheet toSheet,
			boolean copyValueFlag,boolean copyCellStyleFlag) {
		//合并区域处理
//		mergerRegion(fromSheet, toSheet);
		for (Row fromRow : fromSheet){
			Row newRow = toSheet.createRow(fromRow.getRowNum());
			//行复制
			copyRow(wb,fromRow,newRow,copyValueFlag,copyCellStyleFlag);
		}
	}
	/**
	 * 行复制功能
	 * @param fromRow
	 * @param toRow
	 * @param copyValueFlag true则连同cell的内容一起复制
	 * @param copyCellStyleFlag 是否复制样式 true则连同样式一起复制
	 */
	public static void copyRow(Workbook wb,Row fromRow,Row toRow,boolean copyValueFlag,boolean copyCellStyleFlag){
		for (Cell fromCell : fromRow) {
			if(copyCellStyleFlag)
				toRow.setRowStyle(fromRow.getRowStyle());
			Cell newCell = toRow.createCell(fromCell.getColumnIndex());
			copyCell(wb,fromCell, newCell, copyValueFlag,copyCellStyleFlag);
		}
	}

	/**
	 * 复制单元格
	 * 
	 * @param srcCell
	 * @param distCell
	 * @param copyValueFlag  true则连同cell的内容一起复制
	 * @param copyCellStyleFlag 是否复制样式 true则连同样式一起复制
	 */
	public static void copyCell(Workbook wb,Cell srcCell, Cell distCell,
			boolean copyValueFlag,boolean copyCellStyleFlag) {
		if(copyCellStyleFlag){
			CellStyle newstyle=wb.createCellStyle();
			newstyle.cloneStyleFrom(srcCell.getCellStyle());
			//样式
			distCell.setCellStyle(newstyle);
			//评论
			if (srcCell.getCellComment() != null) {
				distCell.setCellComment(srcCell.getCellComment());
			}
		}
		// 解析公式结果
		Workbook srcWb = srcCell.getSheet().getWorkbook();
		FormulaEvaluator evaluator = srcWb.getCreationHelper()
				.createFormulaEvaluator();
		srcCell = evaluator.evaluateInCell(srcCell);
		// 不同数据类型处理
		if (copyValueFlag) {
			switch (srcCell.getCellTypeEnum()) {
			case BOOLEAN:
				distCell.setCellValue(srcCell.getBooleanCellValue());
				break;
			case NUMERIC:
				// 这里的日期类型会被转换为数字类型，需要判别后区分处理
				if (DateUtil.isCellDateFormatted(srcCell))
					distCell.setCellValue(srcCell.getDateCellValue());
				else
					distCell.setCellValue(srcCell.getNumericCellValue());
				break;
			case STRING:
				distCell.setCellValue(srcCell.getStringCellValue());
				break;
			case FORMULA:
				distCell.setCellFormula(srcCell.getCellFormula());
				break;
			case BLANK:
				break;
			case ERROR:
				distCell.setCellErrorValue(srcCell.getErrorCellValue());
				break;
			default:
				break;
			}
		}
//		int srcCellType = srcCell.getCellType();
//		distCell.setCellType(srcCellType);
//		if (copyValueFlag) {
//			if (srcCellType == Cell.CELL_TYPE_NUMERIC) {
//				if (DateUtil.isCellDateFormatted(srcCell)) {
//					distCell.setCellValue(srcCell.getDateCellValue());
//				} else {
//					distCell.setCellValue(srcCell.getNumericCellValue());
//				}
//			} else if (srcCellType == Cell.CELL_TYPE_STRING) {
//				distCell.setCellValue(srcCell.getStringCellValue());
//			} else if (srcCellType == Cell.CELL_TYPE_BLANK) {
//				// nothing21
//			} else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {
//				distCell.setCellValue(srcCell.getBooleanCellValue());
//			} else if (srcCellType == Cell.CELL_TYPE_ERROR) {
//				distCell.setCellErrorValue(srcCell.getErrorCellValue());
//			} else if (srcCellType == Cell.CELL_TYPE_FORMULA) {
//				distCell.setCellFormula(srcCell.getCellFormula());
//			}
//		}
	}

}
