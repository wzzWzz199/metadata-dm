package com.hayden.hap.common.export.way;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.ExportWayType;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.excel.action.ExcelTemplateBaseAction;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.excel.itf.IExcelTemplateAction;
import com.hayden.hap.common.excel.itf.IExcelTemplateDataAction;
import com.hayden.hap.common.excel.utils.ExcelTemplateConstants;
import com.hayden.hap.common.excel.utils.POICopyUtils;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.DictUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.date.DateUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 导入模板 导出方式
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public abstract class ImportExportWay extends AbstractExportWay {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportExportWay.class);


	@Override
	public ReturnResult<InputStream> getExportInputStream(TemplateVO templateVO,
														  List<? extends AbstractVO> exportVoList, FormParamVO formParamVO, String funcnama, Long tenantid,
														  String exportids, int exportcountb, int exportcounte) throws HDException {
		Workbook wb = getWorkbook();
		Sheet type_sheet = wb.createSheet(ExportWayType.IMPORT.getCode());  //增加类型标识，导入时解析
		//设置强制隐藏，不能通过excel模板显示页签
		wb.setSheetHidden(wb.getSheetIndex(type_sheet), Workbook.SHEET_STATE_VERY_HIDDEN);

		return export(wb, exportVoList, templateVO.getExcelTemplateVO(), templateVO.getExcelTemplateVOList(), tenantid,
				exportids, exportcountb, exportcounte);
	}


	private void getfuncToListMap(String mainFuncCode, Long tenantid, /*List<String>*/Set<String>  funcList, List<? extends AbstractVO> voList,
								  Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap, Map<Object,
			List<? extends AbstractVO>> vo2subVos) throws HDException {
		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink(mainFuncCode, tenantid);
		if(ObjectUtil.isNotEmpty(funcLinkVOs)) {
			for(FuncLinkVO funcLinkVO : funcLinkVOs) {
				String sub_func_code = funcLinkVO.getSub_func_code();
				if(!funcList.contains(sub_func_code)) continue;
				List<FuncLinkItemVO> valueItems = new ArrayList<>();

				for(FuncLinkItemVO itemVO : funcLinkVO.getLinkItems()) {
					if(ObjectUtil.isTrue(itemVO.getLitem_isvalue())) {
						valueItems.add(itemVO);
					}
				}
				if(valueItems.size()==0) {//是否传值，如果不传值的子功能，则不进行复制
					continue;
				}
				try{
					List<? extends AbstractVO> allChildren = queryChildrenService.getChildren4Export(voList, funcLinkVO, tenantid);
					if(ObjectUtil.isNotEmpty(allChildren)){//需要递归先操作子表数据，再与主表数据进行匹配
						getfuncToListMap(sub_func_code, tenantid, funcList, allChildren, funcToListMap,vo2subVos);
					}
					Map<? extends AbstractVO, List<? extends AbstractVO>> parent2ChildrenMap = queryChildrenService.matchChildren(voList, allChildren, funcLinkVO);
					if(parent2ChildrenMap == null) return;
					vo2subVos.putAll(parent2ChildrenMap);
					for(AbstractVO vo: voList){//在主表数据中记录对应子表数据数量
						List<? extends AbstractVO> curSubVoList = parent2ChildrenMap.get(vo);
						int size = vo.getInt("subVoListSize", 0);
						if(null != curSubVoList){
							if(size < curSubVoList.size()){
								vo.setInt("subVoListSize", curSubVoList.size());
							}
//							if(!curSubVoList.isEmpty()) {
//								
//							}
						}
					}

					Map<String, String> funcMap = new HashMap<>();
					funcMap.put(mainFuncCode, sub_func_code);
					funcToListMap.put(funcMap, parent2ChildrenMap);
				}catch(Exception e){
					throw new HDException(sub_func_code+"功能"+"导出时 主子表匹配 有误"+e.getMessage());
				}
			}
		}
	}

	/**
	 * 导出 导入模板
	 *
	 * @param excelTemplateVO
	 * @param excelTemplateItemVOs
	 * @return InputStream
	 * @throws HDException
	 */
	public ReturnResult<InputStream> export(Workbook wb, List<? extends AbstractVO> voList, ExcelTemplateVO excelTemplateVO,
											List<ExcelTemplateItemVO> excelTemplateItemVOs, Long tenantid, String exportids, int exportcountb,
											int exportcounte) throws HDException {

		//创建模板名称页签
		Sheet sheet = wb.createSheet(excelTemplateVO.getTemp_name());
		String mainFuncCode = excelTemplateVO.getFunc_code();
		String temp_code = excelTemplateVO.getTemp_code();
		//创建主功能页签，业务上没有什么用。导入模板时用到了 -  修改为创建模板code页签
		Sheet func_code_sheet = wb.createSheet(temp_code);
		// Sheet func_code_sheet = wb.createSheet("RMT_RISK_MEAS_REPO_LINE_TECH_RISK");
		wb.setSheetOrder(func_code_sheet.getSheetName(), wb.getSheetIndex(sheet) + 1);
		//设置强制隐藏，不能通过excel模板显示页签
		wb.setSheetHidden(wb.getSheetIndex(func_code_sheet), Workbook.SHEET_STATE_VERY_HIDDEN);
		Integer exportEndMarker = voList.size();// 导出的数据行数

		Boolean hasids = false;
		String pk = "";
		String[] splitids = null;
		// List<? extends AbstractVO> list = voset.getVoList();
		String tableName = funcService.getOperaTableNameOfFunc(excelTemplateVO.getFunc_code(), tenantid);
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);// 操作表信息
		if (!StringUtils.isEmpty(exportids)) {// 为null是导出全部，为“”是只导出标题，导出全部时如果没有合适的数据导出，则只导出标题
			splitids = exportids.split(",");
			exportcountb = 1;
			hasids = true;
			String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO().getColcode() : null;
			pk = pkColName;
		}

		//key为主功能编码，子功能编码map，value为主功能vo，子功能volist的map
		Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap = new HashMap<>();
		Map<Object, List<? extends AbstractVO>> vo2subVos = new HashMap<>();
		if (exportEndMarker > 0) {// 主表有值才会去查对应子表孙表数据，否则不会差

			Set<String> funcList = new HashSet<>();
			for(ExcelTemplateItemVO itemvo: excelTemplateItemVOs){
				String funcCode = itemvo.getFunc_code();
				String main_func_code = itemvo.getMain_func_code();
				funcList.add(funcCode);
				if(!StringUtils.isEmpty(main_func_code)) {
					funcList.add(main_func_code);
				}
			}

			getfuncToListMap(mainFuncCode, tenantid, funcList, voList, funcToListMap,vo2subVos);
		}
		int rownum = 1;
		int sheetNum = wb.getSheetIndex(sheet) + 2;// 除了主sheet，每个查询选择和字典一个独立的sheet
		Integer endMarker = ObjectUtil.asInteger(excelTemplateVO.getEmarker());// 导出 加 可添加字典，查询选择导入的行数
		if (null == endMarker)
			endMarker = exportEndMarker + ExcelTemplateConstants.DICTENDROW;
		else
			endMarker += exportEndMarker;

		Row row0 = sheet.createRow(0);
		Integer Ix, colIndex = 0;
		Iterator<ExcelTemplateItemVO> it = excelTemplateItemVOs.iterator();

		//获取文本单元格格式，设置默认列格式不起作用，需要对每个单元格设置格式
		DataFormat fmt = wb.createDataFormat();
		CellStyle textStyle = wb.createCellStyle();
		textStyle.setDataFormat(fmt.getFormat("@"));

		//key为当前导出列所属的功能编码（excelTemplateItemVO.getFunc_code()）+要导出的数据条目的编号（编号从1开始），value为在excel中的行位置
		//要导出的每一条数据，都需要重新计算各个列的行位置 add by 王振军 2019.7.19
		Map<String,Integer> funcStartRow = new HashMap<>();


		//遍历导入模板字段，然后再结合起始行和结束行标记，完成数据的导出。
		while (it.hasNext()) {
			ExcelTemplateItemVO excelTemplateItemVO = it.next();
			// Ix one-based
			Ix = ObjectUtil.asInteger(excelTemplateItemVO.getColumn_no());
			String item_column_title = excelTemplateItemVO.getColumn_title();
			String item_column_code = excelTemplateItemVO.getItem_code();
			String item_item_type = excelTemplateItemVO.getItem_type();
			// 未填写序号下载模板不处理此列
			if (null == Ix)
				continue;
			// Ix one-based
			Cell cell0 = row0.createCell(Ix - 1);
			colIndex = Ix - 1;
			cell0.setCellValue(item_column_title);

			//添加批注（字段编码） --- 新增
			addCellComment(cell0, item_column_code);

//			if (ObjectUtil.isTrue(excelTemplateItemVO.getIs_notnull())) {
//				CellStyle cellStyle = wb.createCellStyle();
//				cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  //填充单元格
//				cellStyle.setFillForegroundColor(HSSFColor.RED.index);    //填红色
//				cell0.setCellStyle(cellStyle);
//			}
			row0.setHeightInPoints(15f);
			// datatime类型 格式限制
			//日期类型字段列添加数据有效性校验
			if (JdbcType.DATETIME.getTypeName().equalsIgnoreCase(item_item_type)) {
				DataValidationHelper helper = sheet.getDataValidationHelper();
				// 序列类约束 公式 例如：SY_ORG!$A$1:$A$1000
				DataValidationConstraint constraint = helper.createDateConstraint(DVConstraint.OperatorType.BETWEEN,
						"=DATE(1949,10,1)", "=DATE(2099,12,31)", null);
				// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
				Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
				CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker, cell0.getColumnIndex(),
						cell0.getColumnIndex());
				// 数据有效性对象
				DataValidation data_validation_list = helper.createValidation(constraint, regions);
				// 处理Excel兼容性问题
				if (data_validation_list instanceof XSSFDataValidation) {
					data_validation_list.setSuppressDropDownArrow(true);
					data_validation_list.setShowErrorBox(true);
				} else {
					data_validation_list.setSuppressDropDownArrow(false);
				}
				// 设置输入信息提示信息
				data_validation_list.createPromptBox("日期格式提示", "请输入日期");
				data_validation_list.setShowPromptBox(true);
				// 设置输入错误提示信息
				data_validation_list.createErrorBox("日期格式错误提示", "你输入的数据不符合日期格式规范，请重新输入！");
				sheet.addValidationData(data_validation_list);
			}
			//设置单元格的格式为文本框，默认是常规。
			//常规会引起编辑后，单元格的数据发生改变。如数字、日期，导出时都为字符串，修改后值就变了。
			else if (JdbcType.VARCHAR.getTypeName().equalsIgnoreCase(item_item_type) ||
					JdbcType.CHAR.getTypeName().equalsIgnoreCase(item_item_type)) {
				sheet.setDefaultColumnStyle(cell0.getColumnIndex(), textStyle);
			}

			//add by xdc 2024-3-19 14:51:30  以前没有decimal 新加上
			else if (JdbcType.DECIMAL.getTypeName().equalsIgnoreCase(item_item_type)){
				CellStyle decimalStyle = wb.createCellStyle();

				DataFormat decimalFormat = wb.getCreationHelper().createDataFormat();
				short format = decimalFormat.getFormat("#");

				String itemPrecision = excelTemplateItemVO.getItem_precision();
				if (NumberUtils.isDigits(itemPrecision)){
					int itemPrecisionValue = NumberUtils.createInteger(itemPrecision);
					if (itemPrecisionValue > 0){
						format = decimalFormat.getFormat("#0." + org.apache.commons.lang3.StringUtils.repeat("0", itemPrecisionValue));
					}
				}
				decimalStyle.setDataFormat(format);

				DataValidationHelper helper = sheet.getDataValidationHelper();
				DataValidationConstraint constraint = helper.createDecimalConstraint(DataValidationConstraint.OperatorType.NOT_EQUAL, "1E+307", null);
				// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
				Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
				CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker, cell0.getColumnIndex(), cell0.getColumnIndex());
				// 数据有效性对象
				DataValidation validation = helper.createValidation(constraint, regions);
				// 处理Excel兼容性问题
				if (validation instanceof XSSFDataValidation) {
					validation.setSuppressDropDownArrow(true);
					validation.setShowErrorBox(true);
				} else {
					validation.setSuppressDropDownArrow(false);
				}
				// 设置输入信息提示信息
				validation.createPromptBox("数值格式提示", "请输入数值");
				validation.setShowPromptBox(true);
				// 设置输入错误提示信息
				validation.createErrorBox("数值格式错误提示", "你输入的数据不符合数值格式规范，请重新输入！");
				sheet.addValidationData(validation);
				sheet.setDefaultColumnStyle(cell0.getColumnIndex(), decimalStyle);
			}

			// 带出字典项
			String dict_str = excelTemplateItemVO.getDict();
			String query_selector_str = excelTemplateItemVO.getQuery_sel();

			//判断是真数值还是假的
			if (StringUtils.isEmpty(dict_str) && StringUtils.isEmpty(query_selector_str) && (JdbcType.TINYINT.getTypeName().equalsIgnoreCase(item_item_type)
					|| JdbcType.SMALLINT.getTypeName().equalsIgnoreCase(item_item_type)
					|| JdbcType.INTEGER.getTypeName().equalsIgnoreCase(item_item_type)
					|| JdbcType.BIGINT.getTypeName().equalsIgnoreCase(item_item_type))) {
				CellStyle intStyle = wb.createCellStyle();
				intStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("#"));

				DataValidationHelper helper = sheet.getDataValidationHelper();
				DataValidationConstraint constraint = helper.createIntegerConstraint(DataValidationConstraint.OperatorType.LESS_OR_EQUAL, String.valueOf(Integer.MAX_VALUE), null);
				Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
				CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker, cell0.getColumnIndex(), cell0.getColumnIndex());
				DataValidation validation = helper.createValidation(constraint, regions);
				if (validation instanceof XSSFDataValidation) {
					validation.setSuppressDropDownArrow(true);
					validation.setShowErrorBox(true);
				} else {
					validation.setSuppressDropDownArrow(false);
				}
				validation.createPromptBox("数值格式提示", "请输入整数值");
				validation.setShowPromptBox(true);
				validation.createErrorBox("数值格式错误提示", "你输入的数据不符合整数格式规范，请重新输入！");
				sheet.addValidationData(validation);
				sheet.setDefaultColumnStyle(cell0.getColumnIndex(), intStyle);
			}

			HashMap<String, String> dictmap = new HashMap<String, String>();

			if (StringUtils.hasLength(dict_str) || StringUtils.hasLength(query_selector_str)) {// 处理字典
				if ("dropdown".equals(excelTemplateItemVO.getExcel_cell_type())
						|| 1 == excelTemplateItemVO.getIs_newlable_show()) {
					// 只要配了下拉或是新页签显示就得查询字典或查询选择
					// 字典项有值
					int limit_len = 0;
					String item_func = excelTemplateItemVO.getFunc_code();
					int item_func_len = item_func.length() > 10 ? 10 : item_func.length();
					int item_title_len = item_column_title.length() > 10 ? 10 : item_column_title.length();
					String sheetName = item_func.substring(0, item_func_len) + "_"
							+ item_column_title.substring(0, item_title_len) + (sheetNum - 1) + "_详情";
					Sheet sheet_qsOrDict = wb.createSheet(sheetName);
					wb.setSheetOrder(sheet_qsOrDict.getSheetName(), sheetNum);
					if (StringUtils.hasLength(dict_str)) {
						int dict_len = queryDict(sheet_qsOrDict, 0, excelTemplateItemVO, dictmap);
						limit_len = dict_len;
						if (dict_len <= 0)
							continue;
					}
					if (StringUtils.hasLength(query_selector_str)) {
						int qs_len = querySelector(sheet_qsOrDict, 0, excelTemplateItemVO, voList, funcToListMap,
								excelTemplateVO.getPre_class());
						limit_len = qs_len;
						if (qs_len <= 0)
							continue;
					}
					if (1 != excelTemplateItemVO.getIs_newlable_show()) {// 不独立页签显示则隐藏
						wb.setSheetHidden(wb.getSheetIndex(sheet_qsOrDict), Workbook.SHEET_STATE_VERY_HIDDEN);
					}
					if ("dropdown".equals(excelTemplateItemVO.getExcel_cell_type())) {
						try {
							// create数据校验
							DataValidationHelper helper = sheet.getDataValidationHelper();
							// 加载下拉列表内容
							StringBuffer stringBuffer = new StringBuffer();
							stringBuffer.append(sheet_qsOrDict.getSheetName()).append("!$")
									.append(CellReference.convertNumToColString(0)).append("$2").append(":$")
									.append(CellReference.convertNumToColString(0)).append("$").append(limit_len + 1);
							// 序列类约束 公式 例如：SY_ORG!$A$1:$A$1000
							DataValidationConstraint constraint = helper
									.createFormulaListConstraint("name_" + sheetName);
							// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
							Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
							CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker,
									cell0.getColumnIndex(), cell0.getColumnIndex());
							// 数据有效性对象
							DataValidation data_validation_list = helper.createValidation(constraint, regions);
							// 处理Excel兼容性问题
							if (data_validation_list instanceof XSSFDataValidation) {
								data_validation_list.setSuppressDropDownArrow(true);
								data_validation_list.setShowErrorBox(true);
							} else {
								data_validation_list.setSuppressDropDownArrow(false);
							}
							sheet.addValidationData(data_validation_list);
							// create数据校验 end
						} catch (Exception e) {
							throw new HDException("导出时" + item_column_title + "字段下拉单元各生成出错" + e.getMessage());
						}
					}
					sheetNum++;
				}
			}
			if ("SY_USER".equals(excelTemplateVO.getFunc_code())
					&& excelTemplateItemVO.getItem_code().equalsIgnoreCase("password")) {
				colIndex++;
				continue;
			}


			//每一列开始写入时，行位置为1
			rownum = 1;
			for (int i = exportcountb; i <= exportEndMarker; i++) {// 行循环
				if (hasids && !checkExits(splitids, voList.get(i - 1).getLong(pk).toString()))
					continue;
				String own_func_code = excelTemplateItemVO.getFunc_code();// 当前功能
				String own_main_func_code = excelTemplateItemVO.getMain_func_code();// 当前功能对应的主功能
				String code = excelTemplateItemVO.getItem_code();
				List<? extends AbstractVO> subVoList = new ArrayList<>();
				try {
					if (null != own_main_func_code) {// 非主功能列
						Map<String, String> key = new HashMap<String, String>();
						key.put(own_main_func_code, own_func_code);
						Map<? extends AbstractVO, List<? extends AbstractVO>> mainToSubmap = funcToListMap.get(key);
						if (mainFuncCode.equals(own_main_func_code)) {// 说明是子功能
							// 遍历写子功能excel
							subVoList = mainToSubmap.get(voList.get(i - 1));
							if (!ObjectUtil.isEmpty(subVoList)) {
								int curRownum = funcStartRow.get(own_main_func_code+i);
								for (AbstractVO subVo : subVoList) {
									setCell(subVo, code, curRownum, colIndex, dictmap, excelTemplateItemVO, sheet,
											dict_str, textStyle);
									int subSize = subVo.getInt("subVoListSize", 1);
									curRownum += subSize;
								}
							}
							continue;
						} else {// 孙功能
							Map<String, String> parentkey = new HashMap<String, String>();
							parentkey.put(mainFuncCode, own_main_func_code);// 从主功能得到子功能数据，在得到对应孙功能数据
							Map<? extends AbstractVO, List<? extends AbstractVO>> parentMainToSubmap = funcToListMap
									.get(parentkey);
							subVoList = parentMainToSubmap.get(voList.get(i - 1));
							if (!ObjectUtil.isEmpty(subVoList)) {
								// 循环子功能列表找到孙功能进行写excel
								int curRownum = funcStartRow.get(mainFuncCode+i);
								for (AbstractVO subVo : subVoList) {
									List<? extends AbstractVO> grandVoList = new ArrayList<>();
									grandVoList = mainToSubmap.get(subVo);
									if (!ObjectUtil.isEmpty(grandVoList)) {
										for (AbstractVO grandVo : grandVoList) {
											setCell(grandVo, code, curRownum, colIndex, dictmap, excelTemplateItemVO,
													sheet, dict_str, textStyle);
											curRownum++;
										}
									}
								}
							}
							continue;
						}
					} else {// 主功能

						//没有考虑孙功能的数据占用行，所以这段代码注释掉，最新的写法在下边 modified by 王振军 2019.7.19
//						setCell(voList.get(i - 1), code, rownum, colIndex, dictmap, excelTemplateItemVO, sheet,
//								dict_str, textStyle);
//						int mainSize = voList.get(i - 1).getInt("subVoListSize", 1);
//						rownum += mainSize;


						//旧的写法在计算主功能的写入行位置时，没有考虑孙功能的数据占用行，这是修改的算法 modified by 王振军 2019.7.19
						switch(i) {
							case 1:
								rownum = 1;
								funcStartRow.put(own_func_code+i, 1);
								break;
							default:
								rownum = getRowNum(rownum,own_func_code,i,funcStartRow,vo2subVos,voList.get(i - 2));
								break;
						}

						setCell(voList.get(i - 1), code, rownum, colIndex, dictmap, excelTemplateItemVO, sheet,
								dict_str, textStyle);

					}
				} catch (Exception e) {
					throw new HDException("导出时" + code + "字段赋值出错" + e.getMessage());
				}
			}
			colIndex++;
		}
		// 读取栗子
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(SyConstant.DATE_TIME_PATTERN);
			InputStream is = attachMethodService.getAttachInputStream(ExcelTemplateConstants.MODULE_CODE,
					ExcelTemplateConstants.FUNC_CODE, ExcelTemplateConstants.ATTR_COL_CODE,
					excelTemplateVO.getTemplateid(), excelTemplateVO.getTenantid(),
					sdf.format(excelTemplateVO.getCreated_dt()));
			if (null != is) {
				Workbook wb_example = null;
				try {
					wb_example = WorkbookFactory.create(is);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					return new ReturnResult<>("功能模板中的附件必须是excel格式", MessageLevel.ERROR);
					// throw new HDException("功能模板中的附件必须是excel格式");
				}
				Sheet src_sheet_example = wb_example.getSheetAt(ExcelTemplateConstants.EP_INDEX);
				Sheet dest_sheet = wb.createSheet(ExcelTemplateConstants.EP_NAME);
				if (wb_example.getClass().equals(wb.getClass()))
					POICopyUtils.copySheet(wb, src_sheet_example, dest_sheet, true, true);
				else
					POICopyUtils.copySheet(wb, src_sheet_example, dest_sheet, true, false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// throw new HDException(e);
			try {
				InputStream inputStream = getInputStream(wb, sheet, 0, 0, "");
				return new ReturnResult<InputStream>(inputStream);
			} catch (IOException e1) {
				// throw new HDException(e1.getMessage(), e1);
				logger.error(e1.getMessage(), e1);
				return new ReturnResult<>(e1.getMessage(), MessageLevel.ERROR);
			}
		}

		// Write the output to ByteArrayOutputStream
		try {
			InputStream inputStream = getInputStream(wb, sheet, 0, 0, "");
			return new ReturnResult<InputStream>(inputStream);
		} catch (IOException e1) {
			// throw new HDException(e1.getMessage(), e1);
			logger.error(e1.getMessage(), e1);
			return new ReturnResult<>(e1.getMessage(), MessageLevel.ERROR);
		}
	}


	/**
	 *
	 * @Function: ImportExportWay.java
	 * @Description: 获取在Excel中的行位置
	 *
	 * @param:@param rownum
	 * @param:@param own_func_code
	 * @param:@param i
	 * @param:@param funcStartRow
	 * @param:@param vo2subVos
	 * @param:@param cvo
	 * @param:@return
	 * @return：int
	 * @throws：异常描述
	 *
	 * @version: v3
	 * @author: 王振军
	 * @date: 2019年7月19日 下午6:43:12
	 *
	 */
	private int getRowNum(int rownum,String own_func_code,int i,Map<String,Integer> funcStartRow,Map<Object, List<? extends AbstractVO>> vo2subVos,AbstractVO cvo) {
		//当前导出列行位置已有时，直接写入
		if(funcStartRow.containsKey(own_func_code+i)) {
			rownum = funcStartRow.get(own_func_code+i);
		}
		//行位置没有时，则计算
		else {

			if(cvo.getInt("subVoListSize", 0)<1) {
				rownum ++;
			}else {
				List<? extends AbstractVO> subVos = vo2subVos.get(cvo);
				for(AbstractVO vo : subVos) {
					if(vo.getInt("subVoListSize", 0)==0) {
						rownum++;
					}else {
						List<? extends AbstractVO> grandsubVos = vo2subVos.get(vo);
						for(AbstractVO gsvo : grandsubVos) {
							rownum += gsvo.getInt("subVoListSize", 1);
						}

					}
				}
			}

			funcStartRow.put(own_func_code+i, rownum);

		}

		return rownum;
	}


	@SuppressWarnings("deprecation")
	private void setCell(AbstractVO subVo, String code, int rownum, Integer colIndex, HashMap<String, String> dictmap,
						 ExcelTemplateItemVO excelTemplateItemVO, Sheet sheet, String dict_str,
						 CellStyle textStyle) throws HDException {
		Object value = subVo.get(code);
		if (null != value) {
			Row row = null;
			if (null == sheet.getRow(rownum)) {
				row = sheet.createRow(rownum);
			} else {
				row = sheet.getRow(rownum);
			}
			Cell cell = row.createCell(colIndex);
			String item_ype = excelTemplateItemVO.getItem_type();
			//单元格字段类型配置为varchar时，设置样式为文本
			if (JdbcType.VARCHAR.getTypeName().equalsIgnoreCase(item_ype) ||
					JdbcType.CHAR.getTypeName().equalsIgnoreCase(item_ype)) {
				cell.setCellStyle(textStyle);
			}
			if (("DATETIME".equals(item_ype) || "DATE".equals(item_ype))) {
				DateInputConfigVO dateInputConfigVO = null;
				if (!StringUtils.isEmpty(dict_str)) {
					dateInputConfigVO = JsonUtils.parse(dict_str, DateInputConfigVO.class);
				}
				if(value instanceof Date){
					value = DateUtils.getDateStr((Date) value, dateInputConfigVO);
				}
				// String str = sf.format((Date)value);
				cell.setCellValue(value.toString());
			} else if (!StringUtils.isEmpty(dict_str)) {// 处理字典编码
				String exportSplit = ",";
				if (!StringUtils.isEmpty(dict_str)) {
					DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(dict_str);
					if(!StringUtils.isEmpty(dictInputConfigVO.getSplit())){
						exportSplit = dictInputConfigVO.getSplit();
					}
				}
				//字典多个值时以逗号分隔
				if(value.toString().contains(",")){
					String[] values = value.toString().split(",");
					StringBuilder sb = new StringBuilder();
					for(String _value:values){
						sb.append(dictmap.get(_value)).append(exportSplit); //根据配置的分隔符 分隔
					}
					if(sb.length()>0){
						sb.deleteCharAt(sb.length()-1);
					}
					cell.setCellValue(sb.toString());
				}else{
					cell.setCellValue(dictmap.get(value.toString()));
				}
			} else if ("DECIMAL".equals(item_ype) || "FLOAT".equals(item_ype) || "DOUBLE".equals(item_ype)) {
				cell.setCellValue(Double.parseDouble(value.toString()));
			} else {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(value.toString());
			}
			row.setHeightInPoints(15f);
		}
	}

	protected abstract Workbook getWorkbook();

	protected List<DictDataWarperVO> getAllDataList(List<DictDataWarperVO> dictDataVoList) {
		List<DictDataWarperVO> allDataList = new ArrayList<>();
		for (int i = 0; i < dictDataVoList.size(); i++) {
			DictDataWarperVO dictDataVo = dictDataVoList.get(i);
			List<DictDataWarperVO> childrenList = dictDataVo.getChildren();
			allDataList.add(dictDataVo);
			if (ObjectUtil.isNotEmpty(childrenList)) {
				allDataList.addAll(getAllDataList(childrenList));
			}
		}
		return allDataList;
	}

	/**
	 * 查询选择
	 *
	 * @param sheet
	 *            隐藏sheet页
	 * @param colIndex
	 *            查询选择所在列数
	 * @param excelTemplateItemVO
	 * @param voList
	 * @param funcToListMap
	 * @param tableDefVO2
	 * @return 查询选择项个数
	 * @throws HDException
	 */
	private int querySelector(Sheet sheet, int colIndex, ExcelTemplateItemVO excelTemplateItemVO,
							  List<? extends AbstractVO> exportVoList,
							  Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap,
							  String pre_class)
			throws HDException {
		// String dict_str = excelTemplateItemVO.getValue_item();
		Long tenantid = CurrentEnvUtils.getTenantId();
		String query_selector_str = excelTemplateItemVO.getQuery_sel();
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		String func_code = excelTemplateItemVO.getFunc_code();

		String item_name = sheet.getSheetName();
		int resArr = 0;
		if (!StringUtils.isEmpty(query_selector_str)) {// 处理查询选择
			try {
				QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
						.getQueryselectorInputConfigVO(query_selector_str);
				//结合处理类，添加查询选择
				if(StringUtils.hasLength(pre_class)){
					Object action = getActionByClassName(pre_class);
					if (action != null) {
						// 此action分支，实现各产品统一按照导入模板逻辑导入数据，导入前业务动作
						if (ExcelTemplateBaseAction.class.isAssignableFrom(action.getClass())) {
							ExcelTemplateBaseAction excelTemplateBaseAction = (ExcelTemplateBaseAction) action;
							queryselectorInputConfigVO = excelTemplateBaseAction.beforeQry4ImportItemQry(
									excelTemplateItemVO, queryselectorInputConfigVO, exportVoList);
						}
					}
				}

				String funcCode = queryselectorInputConfigVO.getFunccode();
				String tableName = funcService.getOperaTableNameOfFunc(funcCode, tenantid);
				TableDefVO tableDefVOQ = tableDefService.queryDetailedTableByTbname(tableName);// 查询表信息
				// String where = queryselectorInputConfigVO.getWhere();
				Map<String, String> map = queryselectorInputConfigVO.getMap();
				Map<Object, Object> valueMap = new HashMap<Object, Object>();
				String excelTableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
				TableDefVO excelTableDefVO = tableDefService.queryDetailedTableByTbname(excelTableName);// 操作表信息
				List<TableColumnVO> columnList = excelTableDefVO.getColumnList();
				String voItemKey = "";
				String voItemValue = "";
				List<String> keyList = new ArrayList<>();// 查询选择中 配置功能 对应的字段code
				List<String> valueList = new ArrayList<>();// 查询选择中 业务表单 对应的字段code，与keyList一一对应，第一个为当前字段code
				List<String> nameList = new ArrayList<>();// excel新标签上显示各个查询选择字段对应的name
				for (Entry<String, String> entry : map.entrySet()) {// 为了将当前excel中的字段放到第一个
					if (entry.getValue().equals(item_code)) {
						keyList.add(entry.getKey());
						valueList.add(entry.getValue());
						break;
					}
				}
				for (Entry<String, String> entry : map.entrySet()) {
					if (!entry.getValue().equals(item_code)) {
						keyList.add(entry.getKey());
						valueList.add(entry.getValue());
					}
				}
				// 查询选择各列名称放到map中，在标签页中显示列名称
				List<TableColumnVO> queryTableColumnList = tableDefVOQ.getColumnList();
				for (String value : keyList) {
					for (TableColumnVO colVo : queryTableColumnList) {
						if (value.equals(colVo.getColcode())) {
							nameList.add(colVo.getColname());
							break;
						}
					}
				}
				int valueIndex = 0;
				for (String value : valueList) {// 找到vo里已经有值的字段
					Boolean hasFind = false;
					for (TableColumnVO colVo : columnList) {
						if (value.equals(colVo.getColcode())) {
							voItemKey = keyList.get(valueIndex);
							voItemValue = value;
							hasFind = true;
							break;
						}
					}
					valueIndex++;
					if (hasFind) {
						break;
					}
				}
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				// 租户
				dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
				// where条件
				String where = queryselectorInputConfigVO.getWhere();
				if(!StringUtils.isEmpty(where)){
					dynaSqlVO.setWhereClause(where);
				}
				VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);
				if (!voset.isEmpty()) {

					String pkName = tableDefVOQ.getPkColumnVO() != null ? tableDefVOQ.getPkColumnVO().getColcode()
							: null;
					ObjectUtil.validNotNull(pkName, "table:" + tableName + "的主键列没有找到");
					Row row0;
					if (ObjectUtil.isNotNull(sheet.getRow(0))) {
						row0 = sheet.getRow(0);
					} else
						row0 = sheet.createRow(0);
					int colNum = colIndex;
					// Map<String, Name> nameMap = new HashMap<>();
					for (String name : nameList) {
						Cell cell_value = row0.createCell(colNum++);
						cell_value.setCellValue(name);
						// nameMap.put(value, sheet.getWorkbook().createName());
					}
					List<AbstractVO> tableVoList = voset.getVoList();
					int tableVolistLength = tableVoList.size();
					for (int i = 0; i < tableVolistLength; i++) {
						colNum = colIndex;
						AbstractVO tmpvo = tableVoList.get(i);
						Row row;
						if (ObjectUtil.isNotNull(sheet.getRow(i + 1))) {
							row = sheet.getRow(i + 1);
						} else
							row = sheet.createRow(i + 1);
						for (String key : keyList) {
							Cell cell_value = row.createCell(colNum++);
							cell_value.setCellValue(tmpvo.getString(key));
						}
						valueMap.put(tmpvo.get(voItemKey), tmpvo.get(keyList.get(0)));
						resArr++;
					}
					String main_func_code = excelTemplateItemVO.getMain_func_code();// 当前功能对应的主功能
					if (null == main_func_code) {// 主功能
						if(!queryselectorInputConfigVO.isIsmulti()) {
							for (AbstractVO bizVo : exportVoList) {
								if (null != bizVo.get(voItemValue)) {
									bizVo.set(item_code, valueMap.get(bizVo.get(voItemValue)));
								}
							}
						}
					} else {// 非主功能
						Map<String, String> key = new HashMap<String, String>();
						key.put(main_func_code, func_code);
						if (funcToListMap.size() > 0) {
							Map<? extends AbstractVO, List<? extends AbstractVO>> mainToSubmap = funcToListMap.get(key);
							if (mainToSubmap != null)
								for (Entry<? extends AbstractVO, List<? extends AbstractVO>> entry : mainToSubmap
										.entrySet()) {
									List<? extends AbstractVO> list = entry.getValue();
									if (ObjectUtil.isNotEmpty(list)) {
										for (AbstractVO vo : list) {
											if (null != vo.get(voItemValue)) {
												vo.set(item_code, valueMap.get(vo.get(voItemValue)));
											}
										}
									}
								}
						}
					}
					colNum = colIndex;
					Name sheet_name = sheet.getWorkbook().getName("name_" + item_name);
					if(sheet_name==null) {
						Name name = sheet.getWorkbook().createName();
						name.setNameName("name_" + item_name);
						StringBuilder stringBuffer = new StringBuilder();
						stringBuffer.append(sheet.getSheetName()).append("!$")
								.append(CellReference.convertNumToColString(colNum)).append("$2:$")
								.append(CellReference.convertNumToColString(colNum++)).append("$").append(resArr + 1);
						name.setRefersToFormula(stringBuffer.toString());

					}
					return resArr;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new HDException(item_code + "字段导出查询选择转换有误" + e.getMessage());
			}
		}
		return resArr;
	}

	/**
	 * 查询字典项
	 *
	 * @param sheet
	 *            隐藏sheet页
	 * @param colIndex
	 *            字典所在列数
	 * @param excelTemplateItemVO
	 * @param dictmap
	 * @return 字典项个数
	 * @throws HDException
	 */
	private int queryDict(Sheet sheet, int colIndex, ExcelTemplateItemVO excelTemplateItemVO,
						  HashMap<String, String> dictmap) throws HDException {
		String dict_str = excelTemplateItemVO.getDict();
		// String func_code = excelTemplateItemVO.getFunc_code();
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		String item_name = sheet.getSheetName();

		int resArr = 0;
		if (!StringUtils.isEmpty(dict_str)) {// 处理字典
			String key = "code";
			try {
				DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(dict_str);
				String dictCode = dictInputConfigVO.getDictcode();
				List<DictDataWarperVO> dictDataVoList = DictUtils.getDictData(dictCode);
				if (dictInputConfigVO.getDictdata() != null) {
					dictDataVoList = dictInputConfigVO.getDictdata();
				}
				List<DictDataWarperVO> allDataList = getAllDataList(dictDataVoList);
				Map<String, String> map = dictInputConfigVO.getMap();
				if (null != map) {
					for (Entry<String, String> entry : map.entrySet()) {
						String value = entry.getValue();
						if (value.equals(item_code)) {
							if ("id".equals(entry.getKey())) {
								key = "dictdataid";
							}
						}
					}
				}
				if (ObjectUtil.isNotEmpty(allDataList)) {
					Row row0;
					if (ObjectUtil.isNotNull(sheet.getRow(0))) {
						row0 = sheet.getRow(0);
					} else
						row0 = sheet.createRow(0);
					// 字段项列(name)
					Cell cell_value0 = row0.createCell(colIndex);
					cell_value0.setCellValue(item_name + "_name");
					Cell cell_value1 = row0.createCell(colIndex + 1);
					cell_value1.setCellValue(item_name + "_code");
					// Cell cell_value2 = row0.createCell(colIndex+2);
					// cell_value2.setCellValue(item_name+"_id");
					for (int i = 0; i < allDataList.size(); i++) {
						DictDataWarperVO dictDataVo = allDataList.get(i);
						Row row;
						if (ObjectUtil.isNotNull(sheet.getRow(i + 1))) {
							row = sheet.getRow(i + 1);
						} else
							row = sheet.createRow(i + 1);
						// 字段项列(name)
						Cell cell_value = row.createCell(colIndex);
						cell_value.setCellValue(dictDataVo.getName());
						// code列
						Cell cell_code = row.createCell(colIndex + 1);
						cell_code.setCellValue(String.valueOf(dictDataVo.getCode()));
						// id
						// Cell cell_id = row.createCell(colIndex+2);
						// cell_id.setCellValue(String.valueOf(dictDataVo.getDictdataid()));
						dictmap.put(dictDataVo.get(key).toString(), dictDataVo.getName());
					}
					resArr = allDataList.size();
					// 定义名称 储存下拉备选列 name excelTemplateItemVO.getColumn_title()
					StringBuilder stringBuffer = new StringBuilder();
/*					Name name = sheet.getWorkbook().createName();
					name.setNameName("name_" + item_name);
					stringBuffer.append(sheet.getSheetName()).append("!$")
							.append(CellReference.convertNumToColString(colIndex)).append("$2:$")
							.append(CellReference.convertNumToColString(colIndex)).append("$").append(resArr + 1);
					name.setRefersToFormula(stringBuffer.toString());*/

					Name sheet_name = sheet.getWorkbook().getName("name_" + item_name);
					if(sheet_name==null) {
						Name name = sheet.getWorkbook().createName();
						name.setNameName("name_" + item_name);
						stringBuffer.append(sheet.getSheetName()).append("!$")
								.append(CellReference.convertNumToColString(colIndex)).append("$2:$")
								.append(CellReference.convertNumToColString(colIndex)).append("$").append(resArr + 1);
						name.setRefersToFormula(stringBuffer.toString());
					}

					// 定义名称 储存下拉备选列对应主键 name_id excelTemplateItemVO.getColumn_title()+"_id"
/*					Name name_code = sheet.getWorkbook().createName();
					name_code.setNameName("code_" + item_name);
					stringBuffer.delete(0, stringBuffer.length());
					stringBuffer.append(sheet.getSheetName()).append("!$")
							.append(CellReference.convertNumToColString(colIndex + 1)).append("$2:$")
							.append(CellReference.convertNumToColString(colIndex + 1)).append("$").append(resArr + 1);
					name_code.setRefersToFormula(stringBuffer.toString());*/
					// Name name_id = sheet.getWorkbook().createName();
					// name_id.setNameName(item_name+"_id");
					// stringBuffer.delete(0, stringBuffer.length());
					// stringBuffer.append(sheet.getSheetName()).append("!$").append(CellReference.convertNumToColString(colIndex+2))
					// .append("$2:$").append(CellReference.convertNumToColString(colIndex+2)).append("$").append(resArr+1);
					// name_id.setRefersToFormula(stringBuffer.toString());

					Name sheet_code = sheet.getWorkbook().getName("code_" + item_name);
					if(sheet_code==null) {
						Name name_code = sheet.getWorkbook().createName();
						name_code.setNameName("code_" + item_name);
						stringBuffer.delete(0, stringBuffer.length());
						stringBuffer.append(sheet.getSheetName()).append("!$")
								.append(CellReference.convertNumToColString(colIndex + 1)).append("$2:$")
								.append(CellReference.convertNumToColString(colIndex + 1)).append("$").append(resArr + 1);
						name_code.setRefersToFormula(stringBuffer.toString());
					}
					return resArr;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new HDException(item_code + "字段导出字典转换有误" + e.getMessage());
			}
		}
		return resArr;
	}


	@Override
	public Workbook getExportToWorkBook(Workbook wb, TemplateVO templateVO,
										List<? extends AbstractVO> voList, String funcnama, String func_code, Long tenantid, int exportcountb,
										int exportcounte,boolean initFirstRow,HashMap<String, String> dictmap) throws HDException {
		Sheet type_sheet = wb.createSheet(ExportWayType.IMPORT.getCode());  //增加类型标识，导入时解析
		//设置强制隐藏，不能通过excel模板显示页签
		wb.setSheetHidden(wb.getSheetIndex(type_sheet), Workbook.SHEET_STATE_VERY_HIDDEN);
		ExcelTemplateVO excelTemplateVO = templateVO.getExcelTemplateVO();
		List<ExcelTemplateItemVO> excelTemplateItemVOs = templateVO.getExcelTemplateVOList();
		return getExportToWorkBook(wb, excelTemplateVO, excelTemplateItemVOs, voList, funcnama, func_code, tenantid, exportcountb, exportcounte, initFirstRow, dictmap);
	}

	public Workbook getExportToWorkBook(Workbook wb, ExcelTemplateVO excelTemplateVO,List<ExcelTemplateItemVO> excelTemplateItemVOs,
										List<? extends AbstractVO> voList, String funcnama, String func_code, Long tenantid, int exportcountb,
										int exportcounte,boolean initFirstRow,HashMap<String, String> dictmap) throws HDException {
		//创建模板名称页签
		String temp_name = excelTemplateVO.getTemp_name();
		Sheet sheet = wb.getSheet(temp_name);
		if(sheet==null) {
			sheet = wb.createSheet(excelTemplateVO.getTemp_name());
		}
		String mainFuncCode = excelTemplateVO.getFunc_code();
		String temp_code = excelTemplateVO.getTemp_code();
		//创建主功能页签，业务上没有什么用。导入模板时用到了 - 修改为创建模板编码页签
		Sheet func_code_sheet = wb.getSheet(temp_code);
		if(func_code_sheet==null) {
			func_code_sheet = wb.createSheet(temp_code);
		}
		wb.setSheetOrder(func_code_sheet.getSheetName(), wb.getSheetIndex(sheet) + 1);
		//设置强制隐藏，不能通过excel模板显示页签
		wb.setSheetHidden(wb.getSheetIndex(func_code_sheet), Workbook.SHEET_STATE_VERY_HIDDEN);

		Integer exportEndMarker = exportcounte;// 导出的数据行数
		//key为主功能编码，子功能编码map，value为主功能vo，子功能volist的map
		Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap = new HashMap<>();
		Map<Object, List<? extends AbstractVO>> vo2subVos = new HashMap<>();
		if (exportEndMarker > 0) {// 主表有值才会去查对应子表孙表数据，否则不会差

			Set<String> funcList = new HashSet<>();
			for(ExcelTemplateItemVO itemvo: excelTemplateItemVOs){
				String funcCode = itemvo.getFunc_code();
				String main_func_code = itemvo.getMain_func_code();
				funcList.add(funcCode);
				if(!StringUtils.isEmpty(main_func_code)) {
					funcList.add(main_func_code);
				}
			}

			getfuncToListMap(mainFuncCode, tenantid, funcList, voList, funcToListMap,vo2subVos);
		}
		int sheetNum = wb.getSheetIndex(sheet) + 2;// 除了主sheet，每个查询选择和字典一个独立的sheet
		Integer endMarker = ObjectUtil.asInteger(excelTemplateVO.getEmarker());// 导出 加 可添加字典，查询选择导入的行数
		if (null == endMarker)
			endMarker = exportEndMarker + ExcelTemplateConstants.DICTENDROW;
		else
			endMarker += exportEndMarker;
		//获取文本单元格格式，设置默认列格式不起作用，需要对每个单元格设置格式
		DataFormat fmt = wb.createDataFormat();
		CellStyle textStyle = wb.createCellStyle();
		textStyle.setDataFormat(fmt.getFormat("@"));
		if (!initFirstRow) {
			handleQuerySelectForExport(excelTemplateItemVOs,excelTemplateVO.getPre_class(),voList, funcToListMap);
		}
		//根据模版写入每行的数据
		Iterator<ExcelTemplateItemVO> it = excelTemplateItemVOs.iterator();

		for (AbstractVO abstractVO : voList) {
			int  rownum = sheet.getPhysicalNumberOfRows();
			if (rownum==0) {
				rownum = 1;
			}
			for (ExcelTemplateItemVO excelTemplateItemVO : excelTemplateItemVOs) {
				Integer column_no = ObjectUtil.asInteger(excelTemplateItemVO.getColumn_no());
				// 未填写序号下载模板不处理此列
				if (null == column_no)
					continue;
				// 带出字典项
				String item_column_title = excelTemplateItemVO.getColumn_title();
				String item_item_type = excelTemplateItemVO.getItem_type();
				String dict_str = excelTemplateItemVO.getDict();
				String query_selector_str = excelTemplateItemVO.getQuery_sel();
				Row row0 = null;
				if (initFirstRow&&voList.indexOf(abstractVO)==0) {
					row0 =sheet.getRow(0);
					if (row0==null) {
						row0 =sheet.createRow(0);
					}
					Cell cell_row0 = row0.createCell(column_no - 1);
					cell_row0.setCellValue(item_column_title);
					row0.setHeightInPoints(15f);
					if (JdbcType.DATETIME.getTypeName().equalsIgnoreCase(item_item_type)) {
						DataValidationHelper helper = sheet.getDataValidationHelper();
						// 序列类约束 公式 例如：SY_ORG!$A$1:$A$1000
						DataValidationConstraint constraint = helper.createDateConstraint(DVConstraint.OperatorType.BETWEEN,
								"=DATE(1949,10,1)", "=DATE(2099,12,31)", null);
						// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
						Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
						CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker, cell_row0.getColumnIndex(),
								cell_row0.getColumnIndex());
						// 数据有效性对象
						DataValidation data_validation_list = helper.createValidation(constraint, regions);
						// 处理Excel兼容性问题
						if (data_validation_list instanceof XSSFDataValidation) {
							data_validation_list.setSuppressDropDownArrow(true);
							data_validation_list.setShowErrorBox(true);
						} else {
							data_validation_list.setSuppressDropDownArrow(false);
						}
						// 设置输入信息提示信息
						data_validation_list.createPromptBox("日期格式提示", "请输入日期");
						data_validation_list.setShowPromptBox(true);
						// 设置输入错误提示信息
						data_validation_list.createErrorBox("日期格式错误提示", "你输入的数据不符合日期格式规范，请重新输入！");
						sheet.addValidationData(data_validation_list);
					}
					//设置单元格的格式为文本框，默认是常规。
					//常规会引起编辑后，单元格的数据发生改变。如数字、日期，导出时都为字符串，修改后值就变了。
					if (JdbcType.VARCHAR.getTypeName().equalsIgnoreCase(item_item_type) ||
							JdbcType.CHAR.getTypeName().equalsIgnoreCase(item_item_type)) {
						sheet.setDefaultColumnStyle(cell_row0.getColumnIndex(), textStyle);
					}
					if (StringUtils.hasLength(dict_str) || StringUtils.hasLength(query_selector_str)) {// 处理字典
						if ("dropdown".equals(excelTemplateItemVO.getExcel_cell_type())
								|| 1 == excelTemplateItemVO.getIs_newlable_show()) {
							// 只要配了下拉或是新页签显示就得查询字典或查询选择
							// 字典项有值
							int limit_len = 0;
							String item_func = excelTemplateItemVO.getFunc_code();
							int item_func_len = item_func.length() > 10 ? 10 : item_func.length();
							int item_title_len = item_column_title.length() > 10 ? 10 : item_column_title.length();
							String sheetName = item_func.substring(0, item_func_len) + "_"
									+ item_column_title.substring(0, item_title_len) + (sheetNum - 1) + "_详情";
							Sheet sheet_qsOrDict = wb.getSheet(sheetName);
							if(sheet_qsOrDict==null) {
								sheet_qsOrDict = wb.createSheet(sheetName);
								wb.setSheetOrder(sheet_qsOrDict.getSheetName(), sheetNum);
							}
							if (StringUtils.hasLength(dict_str)) {
								int dict_len = queryDict(sheet_qsOrDict, 0, excelTemplateItemVO, dictmap);
								limit_len = dict_len;
								if (dict_len <= 0)
									continue;
							}
							if (StringUtils.hasLength(query_selector_str)) {
								int qs_len = querySelector(sheet_qsOrDict, 0, excelTemplateItemVO, voList, funcToListMap,
										excelTemplateVO.getPre_class());
								limit_len = qs_len;
								if (qs_len <= 0)
									continue;
							}
							if (1 != excelTemplateItemVO.getIs_newlable_show()) {// 不独立页签显示则隐藏
								wb.setSheetHidden(wb.getSheetIndex(sheet_qsOrDict), Workbook.SHEET_STATE_VERY_HIDDEN);
							}
							if ("dropdown".equals(excelTemplateItemVO.getExcel_cell_type())) {
								try {
									// create数据校验
									DataValidationHelper helper = sheet.getDataValidationHelper();
									// 加载下拉列表内容
									StringBuffer stringBuffer = new StringBuffer();
									stringBuffer.append(sheet_qsOrDict.getSheetName()).append("!$")
											.append(CellReference.convertNumToColString(0)).append("$2").append(":$")
											.append(CellReference.convertNumToColString(0)).append("$").append(limit_len + 1);
									// 序列类约束 公式 例如：SY_ORG!$A$1:$A$1000
									DataValidationConstraint constraint = helper
											.createFormulaListConstraint("name_" + sheetName);

									// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
									Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
									CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker,
											cell_row0.getColumnIndex(), cell_row0.getColumnIndex());
									// 数据有效性对象
									DataValidation data_validation_list = helper.createValidation(constraint, regions);
									// 处理Excel兼容性问题
									if (data_validation_list instanceof XSSFDataValidation) {
										data_validation_list.setSuppressDropDownArrow(true);
										data_validation_list.setShowErrorBox(true);
									} else {
										data_validation_list.setSuppressDropDownArrow(false);
									}
									sheet.addValidationData(data_validation_list);
									// create数据校验 end
								} catch (Exception e) {
									throw new HDException("导出时" + item_column_title + "字段下拉单元各生成出错" + e.getMessage());
								}
							}
							sheetNum++;
						}
					}
				}
				if ("SY_USER".equals(excelTemplateVO.getFunc_code())
						&& excelTemplateItemVO.getItem_code().equalsIgnoreCase("password")) {
					continue;
				}
				// 行循环
				String own_func_code = excelTemplateItemVO.getFunc_code();// 当前功能
				String own_main_func_code = excelTemplateItemVO.getMain_func_code();// 当前功能对应的主功能
				String code = excelTemplateItemVO.getItem_code();
				List<? extends AbstractVO> subVoList = new ArrayList<>();
				try {
					if (null != own_main_func_code) {// 非主功能列
						Map<String, String> key = new HashMap<String, String>();
						key.put(own_main_func_code, own_func_code);
						Map<? extends AbstractVO, List<? extends AbstractVO>> mainToSubmap = funcToListMap.get(key);
						if (mainFuncCode.equals(own_main_func_code)) {// 说明是子功能
							// 遍历写子功能excel
							subVoList = mainToSubmap.get(abstractVO);
							if (!ObjectUtil.isEmpty(subVoList)) {
								int curRownum = rownum;
								for (AbstractVO subVo : subVoList) {
									setCell(subVo, code, curRownum, column_no-1, dictmap, excelTemplateItemVO, sheet,
											dict_str, textStyle);
									int subSize = subVo.getInt("subVoListSize", 1);
									curRownum += subSize;
								}
							}
							continue;
						} else {// 孙功能
							Map<String, String> parentkey = new HashMap<String, String>();
							parentkey.put(mainFuncCode, own_main_func_code);// 从主功能得到子功能数据，在得到对应孙功能数据
							Map<? extends AbstractVO, List<? extends AbstractVO>> parentMainToSubmap = funcToListMap
									.get(parentkey);
							subVoList = parentMainToSubmap.get(abstractVO);
							if (!ObjectUtil.isEmpty(subVoList)) {
								// 循环子功能列表找到孙功能进行写excel
								int curRownum = rownum;
								for (AbstractVO subVo : subVoList) {
									List<? extends AbstractVO> grandVoList = new ArrayList<>();
									grandVoList = mainToSubmap.get(subVo);
									if (!ObjectUtil.isEmpty(grandVoList)) {
										for (AbstractVO grandVo : grandVoList) {
											setCell(grandVo, code, curRownum, column_no-1, dictmap, excelTemplateItemVO,
													sheet, dict_str, textStyle);
											curRownum++;
										}
									}
								}
							}
							continue;
						}
					} else {
						setCell(abstractVO, code, rownum, column_no-1, dictmap, excelTemplateItemVO, sheet,
								dict_str, textStyle);
					}
				} catch (Exception e) {
					throw new HDException("导出时" + code + "字段赋值出错" + e.getMessage());
				}
			}
		}
		return wb;
	}

	private Object getActionByClassName(String className) {
        if (className == null || "".equals(className))
            return null;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
            Component component = clazz.getAnnotation(Component.class);
            if (component != null) {//如果没有spring的
                String value = component.value();
                if (ExcelTemplateBaseAction.class.isAssignableFrom(clazz)) {
                    clazz.asSubclass(IExcelTemplateAction.class);
                    return AppServiceHelper.findBean(clazz, value);
                } else if (IExcelTemplateDataAction.class.isAssignableFrom(clazz)) {
                    clazz.asSubclass(IExcelTemplateDataAction.class);
                    return AppServiceHelper.findBean(clazz, value);
                }

            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

	private void handleQuerySelectForExport(List<ExcelTemplateItemVO> excelTemplateItemVOs, String pre_class, List<? extends AbstractVO> exportVoList, Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap) throws HDException {
		Iterator<ExcelTemplateItemVO> it = excelTemplateItemVOs.iterator();
		while (it.hasNext()) {
			ExcelTemplateItemVO excelTemplateItemVO = it.next();
			Long tenantid = CurrentEnvUtils.getTenantId();
			String query_selector_str = excelTemplateItemVO.getQuery_sel();
			String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
			String func_code = excelTemplateItemVO.getFunc_code();

			if (!StringUtils.isEmpty(query_selector_str)) {// 处理查询选择
				try {
					QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
							.getQueryselectorInputConfigVO(query_selector_str);
					//结合处理类，添加查询选择
					if(StringUtils.hasLength(pre_class)){
						Object action = getActionByClassName(pre_class);
						if (action != null) {
							// 此action分支，实现各产品统一按照导入模板逻辑导入数据，导入前业务动作
							if (ExcelTemplateBaseAction.class.isAssignableFrom(action.getClass())) {
								ExcelTemplateBaseAction excelTemplateBaseAction = (ExcelTemplateBaseAction) action;
								queryselectorInputConfigVO = excelTemplateBaseAction.beforeQry4ImportItemQry(
										excelTemplateItemVO, queryselectorInputConfigVO, exportVoList);
							}
						}
					}

					String funcCode = queryselectorInputConfigVO.getFunccode();
					String tableName = funcService.getOperaTableNameOfFunc(funcCode, tenantid);
					TableDefVO tableDefVOQ = tableDefService.queryDetailedTableByTbname(tableName);// 查询表信息
					// String where = queryselectorInputConfigVO.getWhere();
					Map<String, String> map = queryselectorInputConfigVO.getMap();
					Map<Object, Object> valueMap = new HashMap<Object, Object>();
					String excelTableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
					TableDefVO excelTableDefVO = tableDefService.queryDetailedTableByTbname(excelTableName);// 操作表信息
					List<TableColumnVO> columnList = excelTableDefVO.getColumnList();
					String voItemKey = "";
					String voItemValue = "";
					List<String> keyList = new ArrayList<>();// 查询选择中 配置功能 对应的字段code
					List<String> valueList = new ArrayList<>();// 查询选择中 业务表单 对应的字段code，与keyList一一对应，第一个为当前字段code
					List<String> nameList = new ArrayList<>();// excel新标签上显示各个查询选择字段对应的name
					for (Entry<String, String> entry : map.entrySet()) {// 为了将当前excel中的字段放到第一个
						if (entry.getValue().equals(item_code)) {
							keyList.add(entry.getKey());
							valueList.add(entry.getValue());
							break;
						}
					}
					for (Entry<String, String> entry : map.entrySet()) {
						if (!entry.getValue().equals(item_code)) {
							keyList.add(entry.getKey());
							valueList.add(entry.getValue());
						}
					}
					// 查询选择各列名称放到map中，在标签页中显示列名称
					List<TableColumnVO> queryTableColumnList = tableDefVOQ.getColumnList();
					for (String value : keyList) {
						for (TableColumnVO colVo : queryTableColumnList) {
							if (value.equals(colVo.getColcode())) {
								nameList.add(colVo.getColname());
								break;
							}
						}
					}
					int valueIndex = 0;
					for (String value : valueList) {// 找到vo里已经有值的字段
						Boolean hasFind = false;
						for (TableColumnVO colVo : columnList) {
							if (value.equals(colVo.getColcode())) {
								voItemKey = keyList.get(valueIndex);
								voItemValue = value;
								hasFind = true;
								break;
							}
						}
						valueIndex++;
						if (hasFind) {
							break;
						}
					}
					DynaSqlVO dynaSqlVO = new DynaSqlVO();
					// 租户
					dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
					// where条件
					String where = queryselectorInputConfigVO.getWhere();
					if(!StringUtils.isEmpty(where)){
						dynaSqlVO.setWhereClause(where);
					}
					VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);
					if (!voset.isEmpty()) {

						String pkName = tableDefVOQ.getPkColumnVO() != null ? tableDefVOQ.getPkColumnVO().getColcode()
								: null;
						ObjectUtil.validNotNull(pkName, "table:" + tableName + "的主键列没有找到");

						List<AbstractVO> tableVoList = voset.getVoList();
						int tableVolistLength = tableVoList.size();
						for (int i = 0; i < tableVolistLength; i++) {
							AbstractVO tmpvo = tableVoList.get(i);
							valueMap.put(tmpvo.get(voItemKey), tmpvo.get(keyList.get(0)));
						}
						String main_func_code = excelTemplateItemVO.getMain_func_code();// 当前功能对应的主功能
						if (null == main_func_code) {// 主功能
							if(!queryselectorInputConfigVO.isIsmulti()) {
								for (AbstractVO bizVo : exportVoList) {
									if (null != bizVo.get(voItemValue)) {
										bizVo.set(item_code, valueMap.get(bizVo.get(voItemValue)));
									}
								}
							}
						} else {// 非主功能
							Map<String, String> key = new HashMap<String, String>();
							key.put(main_func_code, func_code);
							if (funcToListMap.size() > 0) {
								Map<? extends AbstractVO, List<? extends AbstractVO>> mainToSubmap = funcToListMap.get(key);
								if (mainToSubmap != null)
									for (Entry<? extends AbstractVO, List<? extends AbstractVO>> entry : mainToSubmap
											.entrySet()) {
										List<? extends AbstractVO> list = entry.getValue();
										if (ObjectUtil.isNotEmpty(list)) {
											for (AbstractVO vo : list) {
												if (null != vo.get(voItemValue)) {
													vo.set(item_code, valueMap.get(vo.get(voItemValue)));
												}
											}
										}
									}
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					throw new HDException(item_code + "字段导出查询选择转换有误" + e.getMessage());
				}
			}
		}
	}


	@Override
	public HttpServletResponse resetHeader(HttpServletRequest request, HttpServletResponse response, String func_code,String funcnama,
										   String suf, Long tenantid,TemplateVO...t) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.resetHeader(request, response, t==null?funcnama:t[0].getExcelTemplateVO().getTemp_name(), suf);
	}


    /**
     * 单元格添加批注
     *
     * @param cell
     * @param value
     * @return: void
     * @Author: suntaiming
     * @Date: 2021/6/10 9:53
     */
    public void addCellComment(Cell cell, String value){
        Sheet sheet = cell.getSheet();
        cell.removeCellComment();

        ClientAnchor anchor = getClientAnchor();
        // 关键修改
        anchor.setDx1(0);
        anchor.setDx2(0);
        anchor.setDy1(0);
        anchor.setDy2(0);
        anchor.setCol1(cell.getColumnIndex());
        anchor.setRow1(cell.getRowIndex());
        anchor.setCol2(cell.getColumnIndex()+1);
        anchor.setRow2(cell.getRowIndex()+2);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
        // 结束
        Drawing drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        // 输入批注信息
        comment.setString(getRichTextString(value));
        comment.setVisible(false);
        // 将批注添加到单元格对象中
        cell.setCellComment(comment);

    }

    protected abstract ClientAnchor getClientAnchor();

    protected abstract RichTextString getRichTextString(String value);

}