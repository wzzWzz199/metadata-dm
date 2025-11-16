package com.hayden.hap.common.excel.service;

import com.hayden.hap.common.attach.itf.IAttachMethodService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.CommonVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.orm.sql.JdbcType;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.db.util.ResourceUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.ExportWayType;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.excel.itf.IExcelVOMapper;
import com.hayden.hap.common.excel.utils.ExcelTemplateConstants;
import com.hayden.hap.common.excel.utils.ExcelValidations;
import com.hayden.hap.common.excel.utils.POICopyUtils;
import com.hayden.hap.common.form.entity.FormConditionVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormDynamicItemService;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.formmgr.service.QuerySelectHandler;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.reflect.ClassInfo;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.utils.*;
import com.hayden.hap.common.utils.date.DateUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import com.hayden.hap.common.utils.template.TemplateUtils;
import com.hayden.hap.common.utils.tuple.TupleUtils;
import com.hayden.hap.common.utils.tuple.TwoTuple;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Service("POIExcelVOMapperService")
public class POIExcelVOMapperImpl implements IExcelVOMapper {

	private static final Logger logger = LoggerFactory.getLogger(POIExcelVOMapperImpl.class);
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IFuncService funcService;
	@Autowired
	private IFormService formService;
	@Autowired
	private IFormItemService formItemService;
	@Autowired
	private ITableDefService tableDefService;
	@Autowired
	private IAttachMethodService attachMethodService;
	@Autowired
	private QuerySelectHandler querySelectHandler;

	@Autowired
	private IFormDynamicItemService formDynamicItemService;


	//行号字段编码
	public static final String ROW_NUM_FILED = "rowNum";
	@Override
	/**
	 * 由Excel流的Sheet导出至List
	 *
	 * @param is
	 * @param extensionName
	 * @param sheetNum
	 * @return
	 * @throws IOException
	 */
	public ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> exportListFromExcel(
			InputStream is, String extensionName, ExcelTemplateVO excelTemplateVO,
			List<ExcelTemplateItemVO> excelTemplateItemVOs) throws HDException {

		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(is);
		} catch (EncryptedDocumentException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e.getMessage(), e);
		} catch (InvalidFormatException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new HDException(e.getMessage(), e);
		}

		return exportListFromExcel(workbook, excelTemplateVO, excelTemplateItemVOs);
	}

	private ExportWayType getExportWayType(Workbook workbook){
		for (ExportWayType type : ExportWayType.values()){
			Sheet formSheet = workbook.getSheet(type.getCode());
			if(formSheet != null){
				return type;
			}
		}
		//兼容之前版本，默认为导入模板
		return ExportWayType.IMPORT;

	}

	public ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> exportListFromExcelOfForm(
			FormParamVO formParamVO, Workbook workbook, Long tenantid) throws HDException {

		ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> returnResult = new ReturnResult<>();
		List<Message> listMessage = new ArrayList<>();
		String func_code = formParamVO.getFuncCode();
		FormVO formVO = formService.getFormVOByFunccode(func_code, tenantid);
		String funcTableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
		TableDefVO funcTableDefVO = tableDefService.queryDetailedTableByTbname(funcTableName);
		Class<? extends AbstractVO> funcVoClass = TemplateUtils.getVOClass(funcTableDefVO);
		AbstractVO initialVo = ClassInfo.newInstance(funcVoClass);
		initialVo.setTableName(funcTableName);  //初始VO

		//取表单名字相同的sheet页
		Sheet sheet = workbook.getSheet(formVO.getForm_name());
		// 没有取第一个sheet页
		if (!ObjectUtil.isNotNull(sheet)) {
			Message message = new Message("上传excel文件sheet页签名称与表单名称称不一致！", MessageLevel.ERROR, MessageShowType.POPUP);
			listMessage.add(message);
			returnResult.setMessages(listMessage);
			return returnResult;
		}

		// 解析公式结果
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		//excel解析的业务数据
		List<AbstractVO> excelDataList = resolveToDataList(sheet, evaluator, funcTableDefVO, initialVo, listMessage, tenantid );



		//处理 从exel解析的数据，得到业务数据
		ReturnResult<List<AbstractVO>> bizVOResult = handleBizVOFromExcelDataVO(formVO, funcTableDefVO, initialVo, func_code, excelDataList, tenantid);

		if (bizVOResult.getStatus() == Status.SUCCESS ) {
			TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>> twoTuple = TupleUtils
					.tuple(bizVOResult.getData(), new LinkedHashMap());
			returnResult.setData(twoTuple);

		}else if (returnResult.getMessages()!= null){
			listMessage.addAll(returnResult.getMessages());
		}

		returnResult.setMessages(listMessage);
		return returnResult;
	}


	private ReturnResult<List<AbstractVO>> handleBizVOFromExcelDataVO(FormVO formVO, TableDefVO funcTableDefVO,
																	  AbstractVO initialVo, String func_code, List<AbstractVO> excelDataList, Long tenantid) throws HDException {
		ReturnResult<List<AbstractVO>> result = new ReturnResult<>();
		List<Message> messageList = new ArrayList<>();
		//原始表单项
		List<? extends FormItemVO> formItemVOS = formItemService.getFormItemsByFunccode(func_code, tenantid);

		// 每个字段对应的查询选择查出来的数据 ： querySelectMap  key  item_code  value VO
		Map<String, List<AbstractVO>> qyselectorMap = new HashMap<>();

		//是否动态表单
		Integer isDynamicConfig = formVO.getIsDynamicConfig();

		//解析及校验完成的业务数据
		List<AbstractVO> bizVOList = new ArrayList<>();


		//动态表单
		if (isDynamicConfig != null && isDynamicConfig.intValue() == SyConstant.SY_TRUE) {

			//1.预处理excleDataVo 2.分类获取对应的item集合并缓存再map中 3.封装最终的业务数据
			List<FormItemVO> conditionFormItems = getConditionFormItems(formItemVOS, formVO.getForm_code(), tenantid);
			List<AbstractVO> preDataList = new ArrayList<>();

			//预处理
			ReturnResult<List<AbstractVO>> preHandleResult = handleExcelDataListVO(excelDataList, formVO, conditionFormItems, qyselectorMap, initialVo, funcTableDefVO, func_code);
			if (preHandleResult.getStatus() == Status.SUCCESS && !preHandleResult.getData().isEmpty()) {
				preDataList.addAll(preHandleResult.getData());
			}else if(!preHandleResult.getMessages().isEmpty()){
				messageList.addAll(preHandleResult.getMessages());
				result.setMessages(messageList);
				return result;
			}

			//key：行号 和  value：条件字段值拼接的key
			Map<Integer, String> rowNumAndKeyMap = new HashMap<>();
			//key:key 和 value:对应表单字段
			Map<String, List<? extends FormItemVO>> keyAndFormItemListMap = new HashMap<>();

			//key:key  value:excel解析的业务数据
			Map<String, List<AbstractVO>> keyAndExcelDataList = new HashMap<>();
 			for (AbstractVO preVO : preDataList){
				//拼接key
				String key = getKey(preVO, conditionFormItems);

				rowNumAndKeyMap.put(preVO.getInt(ROW_NUM_FILED), key);
				if(keyAndFormItemListMap.get(key) == null){
					List<? extends FormItemVO> dynamicFormItems = formItemService.getFormItemsByFunccode(func_code, preVO);
					keyAndFormItemListMap.put(key, dynamicFormItems);
				}

				List<AbstractVO> currExcelDataList = keyAndExcelDataList.get(key);
				if(currExcelDataList == null){
					currExcelDataList = new ArrayList<>();
					keyAndExcelDataList.put(key, currExcelDataList);
				}
				AbstractVO excelDataVO = ListUtil.first(excelDataList, (x)->x.getInt(ROW_NUM_FILED).intValue() == preVO.getInt(ROW_NUM_FILED).intValue());
				currExcelDataList.add(excelDataVO);
			}


 			//相同key的数据一起解析处理
 			for (String key : keyAndExcelDataList.keySet()){
 				List<AbstractVO> currExcelDataList =  keyAndExcelDataList.get(key);
				List<? extends FormItemVO>  currFormItemList = keyAndFormItemListMap.get(key);

				//具体解析
				ReturnResult<List<AbstractVO>> currDynamicFormResult = handleExcelDataListVO(currExcelDataList, formVO, currFormItemList, qyselectorMap, initialVo, funcTableDefVO, func_code);
				handleReturnResult(bizVOList, messageList, currDynamicFormResult);
			}

		}
		//普通表单
		else {
			//具体解析
			ReturnResult<List<AbstractVO>> commonFormResult = handleExcelDataListVO(excelDataList, formVO, formItemVOS, qyselectorMap, initialVo, funcTableDefVO, func_code);
			handleReturnResult(bizVOList, messageList, commonFormResult);

		}

		result.setData(bizVOList);
		result.setMessages(messageList);
		return result;
	}

	private void handleReturnResult(List<AbstractVO> bizVOList, List<Message> messageList, ReturnResult<List<AbstractVO>> returnResult){
		if (returnResult.getStatus() == Status.SUCCESS && !returnResult.getData().isEmpty()) {
			bizVOList.addAll(returnResult.getData());
		}else if(!returnResult.getMessages().isEmpty()){
			messageList.addAll(returnResult.getMessages());
		}
	}


	private String getKey(AbstractVO vo, List<FormItemVO> conditionFormItems){
		StringBuilder key = new StringBuilder();
		for (FormItemVO formItemVO : conditionFormItems){
			String item_code = formItemVO.getFitem_code();
			Object value = vo.get(item_code);
			key.append(String.valueOf(value));
		}
		return key.toString();
	}

	private List<FormItemVO> getConditionFormItems(List<? extends FormItemVO> formItemVOS, String form_code, Long tenantid) throws HDException{
		List<FormConditionVO> formConditionVOS = formDynamicItemService.getFormDynamicConfig(form_code, tenantid);
		List<FormItemVO> conditionFormItems = new ArrayList<>();
		for (FormConditionVO formConditionVO : formConditionVOS){
			String condition_code = formConditionVO.getCondition_code();
			FormItemVO formItemVO = ListUtil.first(formItemVOS, (x)->{return x.getFitem_code().equals(condition_code);});
			if(formItemVO != null){
				conditionFormItems.add(formItemVO);
			}
		}

		return conditionFormItems;
	}





	/**
	 * 解析并封装最终的业务数据
	 * @param excelDataList
	 * @param formVO
	 * @param formItemVOS
	 * @param qyselectorMap
	 * @param initialVo
	 * @param funcTableDefVO
	 * @param func_code
	 * @return: com.hayden.hap.common.formmgr.message.ReturnResult<java.util.List<com.hayden.hap.common.common.entity.AbstractVO>>
	 * @Author: suntaiming
	 * @Date: 2021/6/15 11:16
	 */
	private ReturnResult<List<AbstractVO>> handleExcelDataListVO(List<AbstractVO> excelDataList, FormVO formVO, List<? extends FormItemVO> formItemVOS,  Map<String, List<AbstractVO>> qyselectorMap,
										 AbstractVO initialVo, TableDefVO funcTableDefVO, String func_code) throws HDException{

		ExcelTemplateVO excelTemplateVO = TemplateUtils.transformExcelTemplateVO(formVO, func_code, null);
		List<AbstractVO> bizVOList = new ArrayList<>();
		ReturnResult<List<AbstractVO>> result = new ReturnResult<>();
		List<Message> messageList = new ArrayList<>();
		for (AbstractVO excelDataVO : excelDataList) {
			AbstractVO bizVO = CloneUtils.cloneObj(initialVo);
			bizVO.set(ROW_NUM_FILED, excelDataVO.getInt(ROW_NUM_FILED));
			bizVO.set(SyConstant.TENANT_STR, formVO.getTenantid());
			List<ExcelTemplateItemVO> excelTemplateItemVOS = TemplateUtils.transformExcelTemplateItemVOs(formItemVOS, funcTableDefVO, func_code);
			for (ExcelTemplateItemVO excelTemplateItemVO : excelTemplateItemVOS){
				String item_code = excelTemplateItemVO.getItem_code();
				String query_sel = excelTemplateItemVO.getQuery_sel();
				if(!StringUtils.isEmpty(query_sel)) {
					List<AbstractVO> qyselectorData = qyselectorMap.get(item_code);

					//如果缓存中不存在查询选择的数据，则查询并缓存
					if(qyselectorData == null){
						qyselectorData = getQuerySelectorData(excelTemplateItemVO);
						qyselectorMap.put(item_code, qyselectorData);
					}
				}

				//业务VO解析并校验
				List<Message> messages = setAndHandleFormItemDataVO(excelDataVO.get(excelTemplateItemVO.getItem_code()), bizVO, excelTemplateVO, excelTemplateItemVO, qyselectorMap);
				if(!messageList.isEmpty()){
					messageList.addAll(messages);
				}
			}

			bizVOList.add(bizVO);
		}
		result.setData(bizVOList);
		result.setMessages(messageList);
		return result;
	}

	/**
	 * 业务数据处理：查询选择、字典、字段值类型校验、默认值等处理
	 * @param unchecked_value
	 * @param vo
	 * @param excelTemplateVO
	 * @param excelTemplateItemVO
	 * @param qyselectorMap
	 * @return: java.util.List<com.hayden.hap.common.formmgr.message.Message>
	 * @Author: suntaiming
	 * @Date: 2021/6/11 15:41
	 */
	private List<Message> setAndHandleFormItemDataVO(Object unchecked_value, AbstractVO vo, ExcelTemplateVO excelTemplateVO,ExcelTemplateItemVO excelTemplateItemVO, Map<String, List<AbstractVO>> qyselectorMap) throws HDException{
		List<Message> listMessage = new ArrayList<>();
		String item_code = excelTemplateItemVO.getItem_code();
		String item_type = excelTemplateItemVO.getItem_type();
		Integer rowNum = vo.getInt(ROW_NUM_FILED);
		if(StringUtils.isEmpty(unchecked_value)){
			unchecked_value = excelTemplateItemVO.get("default_value", null);
		}
		vo.set(item_code, unchecked_value);

		if (!StringUtils.isEmpty(unchecked_value)) {
			if (StringUtils.hasLength(excelTemplateItemVO.getQuery_sel())) {
				List<AbstractVO> list = qyselectorMap.get(item_code);
				handleQuery(unchecked_value, list, excelTemplateVO, excelTemplateItemVO, vo);

			} else if ("DATE".equals(item_type) || "DATETIME".equals(item_type)) {// 日期类型
				// 有输入设定的日期,一般数据库和vo里应该是字符串类型，模板可设为日期类型
				if (StringUtils.hasLength(excelTemplateItemVO.getDict())) {
					try {
						DateInputConfigVO inputConfigVO = JsonUtils.parse(excelTemplateItemVO.getDict(),
								DateInputConfigVO.class);
						String dateValue = null;
						if (unchecked_value instanceof Date) {
							dateValue = DateUtils.getDateStr((Date) unchecked_value, inputConfigVO);
						} else {
							dateValue = DateUtils.getDateByStr(unchecked_value.toString(), inputConfigVO);
						}
						vo.set(item_code, dateValue);
					} catch (Exception e) {
						Message message = new Message("第" + rowNum + "行" + e.getMessage(), MessageLevel.ERROR,
								MessageShowType.POPUP);
						listMessage.add(message);
					}
				} else {// 正常日期
					if (unchecked_value instanceof Date) {
						vo.set(item_code, unchecked_value);
					} else if (unchecked_value instanceof String) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date date = sf.parse(unchecked_value.toString());
							vo.set(item_code, date);
						} catch (ParseException e) {
							Message message = new Message("第" + rowNum + "行,日期转化失败，请检查！", MessageLevel.ERROR,
									MessageShowType.POPUP);
							listMessage.add(message);
						}
					}
				}
			} else if (StringUtils.hasLength(excelTemplateItemVO.getDict())) {// 字典
				try {
					handleDict(unchecked_value, excelTemplateItemVO, vo);
				} catch (Exception e) {
					Message message = new Message("第" + rowNum + "行" + e.getMessage(), MessageLevel.ERROR,
							MessageShowType.POPUP);
					listMessage.add(message);
				}
			}
		}

		// 校验excel录入字段并赋值
		Object uncheckedValue = vo.get(item_code);
		ReturnResult<?> rr1 = ExcelValidations.validate(excelTemplateItemVO, vo, uncheckedValue);
		if (null != rr1 && null != rr1.getMessages() && rr1.getStatus() == Status.FAIL) {
			for (Message msg : rr1.getMessages()) {
				Message message = new Message("第" + rowNum + "行" + msg.getMessage(), MessageLevel.ERROR,
						MessageShowType.POPUP);
				listMessage.add(message);
			}
		}
		return listMessage;
	}




	private List<AbstractVO> resolveToDataList(Sheet sheet, FormulaEvaluator evaluator, TableDefVO funcTableDefVO, AbstractVO initialVo, List<Message> listMessage, Long tenantid) throws HDException{
		List<TableColumnVO> columnList = funcTableDefVO.getColumnList();

		//列数和字段item_code的映射
		Map<Integer, String> cellIxAndItemCodeMapper = resolveCellIxAndItemCodeMapper(sheet);

		//起始行
		Integer stratMarker = 1;
		// 获取sheet页有内容的首末行数
		int minRowIx = sheet.getFirstRowNum();
		int maxRowIx = sheet.getLastRowNum();


		// 去除excel最后的无效空行
		maxRowIx = updateMaxRowIx(maxRowIx, sheet);
		// 按行遍历excel文件
		List<AbstractVO> dataListVO = new ArrayList<>();
		//解析excel行数据
		logger.error("导入中 poi解析开始 "+new Date()+"  毫秒 " +new Date().getTime());
		for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx ++) {
			if (stratMarker > rowIx) {
				continue;// 起始行开始
			}
			Row row = sheet.getRow(rowIx);
			if (!ObjectUtil.isNotNull(row)) {
				Message message = new Message("第" + (rowIx + 1) + "行没有内容！ ", MessageLevel.ERROR, MessageShowType.POPUP);
				listMessage.add(message);
				continue;
			}

			//获取本行内容的首末列数
			int firstCellNum = row.getFirstCellNum();
			int lastCellNum = row.getLastCellNum();
			AbstractVO vo = new CommonVO();
			vo.setInt(ROW_NUM_FILED, rowIx + 1); //设置行号
			for (int cellIx = firstCellNum; cellIx <= lastCellNum; cellIx++) {
				Cell cell = row.getCell(cellIx);
				if (cell == null){
					continue;
				}

				cell = evaluator.evaluateInCell(cell);
				Object unchecked_value = getCellValue(cell);

				String item_code = cellIxAndItemCodeMapper.get(cellIx);
				if (!StringUtils.isEmpty(item_code)) {

					//根据数据存储类型，格式化单元格内容
					TableColumnVO targetColumnVO = ListUtil.first(columnList, (x)->{return x.getColcode().equals(item_code);});
					if (targetColumnVO !=null && (JdbcType.forName(targetColumnVO.getColtype()) == JdbcType.VARCHAR || JdbcType.forName(targetColumnVO.getColtype()) == JdbcType.CHAR)
							&& cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						unchecked_value = formatNumericCell(unchecked_value, cell);
					}
					vo.set(item_code, unchecked_value);
				}
			}

			//添加租户id
			vo.setLong(SyConstant.TENANT_STR, tenantid);
			dataListVO.add(vo);
		}

		return dataListVO;
	}

	/**
	 * 解析页签的第一行数（标题行）中每列的备注（字段的item_code），并且把列数和item_code映射
	 * @param sheet
	 * @return: java.util.Map<java.lang.Integer,java.lang.String>
	 * @Author: suntaiming
	 * @Date: 2021/6/10 17:04
	 */
	private Map<Integer, String> resolveCellIxAndItemCodeMapper(Sheet sheet) throws HDException{
		Map<Integer, String> cellIxAndItemCodeMapper = new HashMap<>();
		Row row = sheet.getRow(0); // 解析第一行 标题行
		//获取本行内容的首末列数
		int firstCellNum = row.getFirstCellNum();
		int lastCellNum = row.getLastCellNum();
		for (int cellIx = firstCellNum; cellIx <= lastCellNum; cellIx++){
			Cell cell = row.getCell(cellIx);
			if(cell == null){
				continue;
			}
			Comment comment = cell.getCellComment();
			if(comment == null){
				continue;
			}
			RichTextString richTextString = comment.getString();
			if (richTextString == null){
				continue;
			}
			String item_code = richTextString.getString();
			if(!StringUtils.isEmpty(item_code)){
				cellIxAndItemCodeMapper.put(cellIx, item_code);
			}
		}

		return cellIxAndItemCodeMapper;
	}



	/**
	 * 由指定的Sheet导出至List
	 * 
	 * @param workbook
	 * @param sheetNum
	 * @return
	 * @throws HDException
	 * @throws IOException
	 */
	public ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> exportListFromExcel(
			Workbook workbook, ExcelTemplateVO excelTemplateVO, List<ExcelTemplateItemVO> excelTemplateItemVOs)
			throws HDException {
		ReturnResult<TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>> returnResult = new ReturnResult<>();
		List<Message> listMessage = new ArrayList<>();
		List<AbstractVO> list = new ArrayList<>();
		// 判断是否模板含有模板下载时的隐藏sheet页
		//默认模板所属功能为主功能
		String mainFuncCode = excelTemplateVO.getFunc_code();
		//这里确定下，取得是编码页签？感觉excel中没有
		Sheet sheet_func_code = workbook.getSheet(mainFuncCode);
		Long tenantid = excelTemplateVO.getTenantid();
		// 校验功能编码和隐藏sheet页编码
//		if (!ObjectUtil.isNotNull(sheet_func_code)
//				|| !workbook.isSheetVeryHidden(workbook.getSheetIndex(sheet_func_code))) {
//			Message message = new Message("上传excel文件不是标准下载模板！", MessageLevel.ERROR, MessageShowType.POPUP);
//			listMessage.add(message);
//			returnResult.setMessages(listMessage);
//			return returnResult;
//		}
		//存储功能的数据映射
		//主功能key:top.main_func_code，value：主vo，子vo映射。主子功能，子功能数据很多。
		LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> funcToListMap = new LinkedHashMap<>();
		Map<String, AbstractVO> voMap = new LinkedHashMap<>();
		ListSortUtil.sortByFuncCode(excelTemplateItemVOs, "main_func_code", "func_code");// 主功能在前，子功能在后，保证voMap按主子孙顺序处理
		//querySelectMap  key  code  value VO
		HashMap<String, List<AbstractVO>> querySelectByItemCodeMap = new HashMap<String, List<AbstractVO>>();
		//解析所有导入模板字段
		//先生成各功能的初始化vo
		//读取表单字段配置的默认值
		logger.error("导入中 poi "+new Date()+"  毫秒 " +new Date().getTime());
		for (ExcelTemplateItemVO itemvo : excelTemplateItemVOs) {
			String func_code = itemvo.getFunc_code();
			String main_func_code = itemvo.getMain_func_code();
			if (null == main_func_code)
				main_func_code = "TOP";// 为主功能虚拟一个主功能
			String key = main_func_code + "." + func_code;
			
			String query_sel = itemvo.getQuery_sel();
			if(!StringUtils.isEmpty(query_sel)) {
				List<AbstractVO> querySelectorData = getQuerySelectorData(itemvo);
				if(querySelectorData!=null&& !querySelectorData.isEmpty()) {
					String item_code = itemvo.getItem_code().toLowerCase();
					querySelectByItemCodeMap.put(key+item_code, querySelectorData);
				}
			}
			if (null != voMap.get(key))
				continue;
			String funcTableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
			List<? extends FormItemVO> formItems = formItemService.getFormItemsByFunccode(func_code, tenantid);
			TableDefVO funcTableDefVO = tableDefService.queryDetailedTableByTbname(funcTableName);// 操作表信息
			// 功能对应的entityVO类，没有默认为baseVO
			Class<? extends AbstractVO> funcVoClass = this.getVOClass(funcTableDefVO);
			if (!ObjectUtil.isNotNull(funcVoClass)) {
				Message message = new Message(func_code + "功能没有实体类，请使用正确的模板上传！", MessageLevel.ERROR,
						MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);
				return returnResult;
			}
			AbstractVO vo = ClassInfo.newInstance(funcVoClass);
			// 未找到class，补充默认baseVo的表名 ，add by zhenjianting 2018-7-10
			if (vo.getTableName() == null)
				vo.setTableName(funcTableDefVO.getTable_code());
			for (FormItemVO itemVo : formItems) {
				String defaultValue = itemVo.getFitem_card_default();
				if (!StringUtils.isEmpty(defaultValue)) {
					if (VariableUtils.hasFitemParam(defaultValue))
						continue;
					String replaceDefaultValue = VariableUtils.replaceSystemParam(defaultValue);
					vo.set(itemVo.getFitem_code(), replaceDefaultValue);
				}
			}
			voMap.put(key, vo);// 每个功能对应的实体类
		}
		//取默认模板名字相同的sheet页
		Sheet sheet = workbook.getSheet(excelTemplateVO.getTemp_name());
		// 没有取第一个sheet页
		if (!ObjectUtil.isNotNull(sheet)) {
			sheet = workbook.getSheetAt(0);
			Message message = new Message("上传excel文件名称与系统模板名称不一致！", MessageLevel.ERROR, MessageShowType.POPUP);
			listMessage.add(message);
			returnResult.setMessages(listMessage);
			return returnResult;
		}

		// 解析公式结果
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		// 获取sheet页有内容的首末行数
		int minRowIx = sheet.getFirstRowNum();
		int maxRowIx = sheet.getLastRowNum();
		// 去除excel最后的无效空行
		maxRowIx = updateMaxRowIx(maxRowIx, sheet);
		// 获取模板配置的首末行
		Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
		Integer endMarker = ObjectUtil.asInteger(excelTemplateVO.getEmarker());
		if (null != endMarker && endMarker < maxRowIx)
			maxRowIx = endMarker;
		// 按行遍历excel文件
		Map<String, AbstractVO> lastVoMap = new HashMap<>();// 存放当前最近一次处理的
		//解析excel行数据
		logger.error("导入中 poi解析开始 "+new Date()+"  毫秒 " +new Date().getTime());
		for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
			if (stratMarker > rowIx)
				continue;// 起始行开始
			Row row = sheet.getRow(rowIx);
			if (!ObjectUtil.isNotNull(row)) {
				Message message = new Message("第" + (rowIx + 1) + "行没有内容！ ", MessageLevel.ERROR, MessageShowType.POPUP);
				listMessage.add(message);
				continue;
			}
			Integer Ix = 0;
			// 按列遍历每行的cell格
			Map<String, AbstractVO> rowVoMap = CloneUtils.cloneObj(voMap);// 存放一行的vo
			Map<String, List<Message>> rowErrorMap = new HashMap<>();// 存放一行的错误信息
			Map<String, Boolean> rowNotEmptyMap = new LinkedHashMap<>();// 存放一行中各个vo是否为空
			//功能和数据异常信息的映射
			Map<String, List<Message>> funcMessageMap = new HashMap<String, List<Message>>();
			//遍历导出单元格，这里只解析excel中录入的字段
			for (ExcelTemplateItemVO excelTemplateItemVO : excelTemplateItemVOs) {
				String itemCode = excelTemplateItemVO.getItem_code().toLowerCase();
				String func_code = excelTemplateItemVO.getFunc_code();
				// rowNotEmptyMap.equals(func_code);
				String main_func_code = excelTemplateItemVO.getString("main_func_code", "TOP");
				// TwoTuple<String, String> tuple = TupleUtils.tuple(main_func_code, func_code);
				// key.put(main_func_code, func_code);
				String key = main_func_code + "." + func_code;// 一个子功能可能有不同的主功能
				AbstractVO vo = rowVoMap.get(key);
				// 赋值vo的某个字段

//				List<Message> msgList = getVoValue(excelTemplateVO, excelTemplateItemVO, Ix, vo, evaluator, row, rowIx);
				List<Message> msgList = getVoValue(querySelectByItemCodeMap,excelTemplateVO, excelTemplateItemVO, Ix, vo, evaluator, row, rowIx);

				// 获取excel原始cell的值，用户判断是否为空，add by zhenjianting 2018-7-11
				Object cellVal = getCellValue(evaluator, row, ObjectUtil.asInteger(excelTemplateItemVO.getColumn_no()));
				//获取某个功能的错误信息
				List<Message> errorList = rowErrorMap.get(key);
				// 说明此行此vo不为空，需要检验，如果为空，则不校验
				// 这里是用于当包含主子孙等功能时，例如子功能有多个，这时非首行时主功能的字段都为空。
				// 存放当前行非空的功能
				// 如果某个功能没有数据，rowNotEmptyMap.get(key)值为空
				if (null == rowNotEmptyMap.get(key) && null == cellVal) {
					if (null == errorList) {
						rowErrorMap.put(key, msgList);
					} else {
						errorList.addAll(msgList);
						rowErrorMap.put(key, errorList);
					}
					//添加到listMessage中					
					//listMessage.addAll(msgList);
					addFuncMessage(funcMessageMap, key, msgList);
				} else {
					rowNotEmptyMap.put(key, true);
					if (null != errorList) {
						errorList.addAll(msgList);
					}
					//listMessage.addAll(msgList);
					addFuncMessage(funcMessageMap, key, msgList);
				}

			}
			//添加错误记录
			//依据行，当功能包含值时，才加入错误信息。
			//例如主子功能，子功能数据有多条，对于非首行的主功能funcMessageMap都记了，但是这个错误不需要提示。
			//只有rowNotEmptyMap为true的功能，即最少录入了一个字段值。
			//这里就要求主子孙功能时，子孙功能多条数据时，相应的主功能字段非首行一定不要录入值
			for(Map.Entry<String, Boolean> entry:rowNotEmptyMap.entrySet()){
				if(entry.getValue()){
					listMessage.addAll(funcMessageMap.get(entry.getKey()));
				}
			}
			//下周来了再做
			for (Entry<String, AbstractVO> entry : rowVoMap.entrySet()) {// rowVoMap是按主子孙顺序放的
				String key = entry.getKey();
				AbstractVO tmpVo = entry.getValue();
				int index = key.indexOf(".");
				//相应的主子功能
				String main_func_code = key.substring(0, index);
				String func_code = key.substring(index + 1);
				if (null != rowNotEmptyMap.get(key) && rowNotEmptyMap.get(key)) {// 说明此vo不为空
					if (mainFuncCode.equals(func_code)) {// 主功能vo先放到list里
						BaseSettingUtils.setCU_TPD(tmpVo);
						tmpVo.setInt(ROW_NUM_FILED, rowIx + 1);
						list.add(tmpVo);
					} else {// 处理子孙功能
						BaseSettingUtils.setCU_TPD(tmpVo);
						tmpVo.setInt(ROW_NUM_FILED, rowIx + 1);
						//获取key映射是否存在
						LinkedHashMap<AbstractVO, List<AbstractVO>> linkMap = funcToListMap.get(key);
						if (null == linkMap) {
							linkMap = new LinkedHashMap<>();
							funcToListMap.put(key, linkMap);
						}
						//最近一次处理的父功能的vo
						List<AbstractVO> curList = linkMap.get(lastVoMap.get(main_func_code));
						if (ObjectUtil.isEmpty(curList)) {
							curList = new ArrayList<>();
							linkMap.put(lastVoMap.get(main_func_code), curList);
						}
						curList.add(tmpVo);
					}
					//功能和vo映射
					lastVoMap.put(func_code, tmpVo);// 最新处理的功能vo
				}
			}
		}
		logger.error("导入中 poi解析完成 "+new Date()+"  毫秒 " +new Date().getTime());
		returnResult.setMessages(listMessage);

		if (returnResult.getStatus() == Status.SUCCESS && ObjectUtil.isNotEmpty(list)) {
			TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>> twoTuple = TupleUtils
					.tuple(list, funcToListMap);
			returnResult.setData(twoTuple);

		}
		return returnResult;
	}

	/**
	 * @Description: 添加功能和错误信息映射
	 * @author: wangyi
	 * @date: 2018年9月3日
	 */
	private void addFuncMessage(Map<String, List<Message>> funcMessageMap, String funcCode,
			List<Message> msgList){
		if(funcMessageMap.containsKey(funcCode)){
			funcMessageMap.get(funcCode).addAll(msgList);
		}else{
			List<Message> tmpList = new ArrayList<Message>();
			tmpList.addAll(msgList);
			funcMessageMap.put(funcCode, tmpList);
		}
	}
	/**
	 * 去除excel最后的无效空行
	 * 
	 * @param maxRowIx
	 * @param sheet
	 * @return
	 * @author liyan
	 * @date 2017年9月8日
	 */
	private int updateMaxRowIx(int maxRowIx, Sheet sheet) {
		boolean rowflag = false;
		for (int i = maxRowIx; i > 0; i--) {
			Row r = sheet.getRow(i);
			if (r == null) {
				// 如果是空行（即没有任何数据、格式），直接把它以下的数据往上移动
				maxRowIx--;
				continue;
			}
			rowflag = false;
			for (Cell c : r) {
				if (c.getCellType() != Cell.CELL_TYPE_BLANK) {
					rowflag = true;
					break;
				}
			}
			if (rowflag) {
				break;
			} else {
				maxRowIx--;
				continue;
			}
		}
		return maxRowIx;
	}

	private Object getCellValue(FormulaEvaluator evaluator, Row row, Integer Ix) {
		if (!ObjectUtil.isNotNull(Ix) || row == null || evaluator == null)
			return null;
		Cell cell = row.getCell(Ix - 1);
		if (cell == null)
			return null;
		cell = evaluator.evaluateInCell(cell);
		return getCellValue(cell);
	}


	@SuppressWarnings("deprecation")
	private List<Message> getVoValue(HashMap<String, List<AbstractVO>> qyselectorMap,ExcelTemplateVO excelTemplateVO, ExcelTemplateItemVO excelTemplateItemVO,
			Integer Ix, AbstractVO vo, FormulaEvaluator evaluator, Row row, int rowIx) throws HDException {
		Map<String, Object> columnValues = new HashMap<>();
		List<Message> listMessage = new ArrayList<>();
		String itemCode = excelTemplateItemVO.getItem_code().toLowerCase();
		//获取导入模板定义的默认值
		Object unchecked_value = excelTemplateItemVO.get("default_value", "");
		////获取列序号
		Ix = ObjectUtil.asInteger(excelTemplateItemVO.getColumn_no());
		// 获取列类型
		String item_type = excelTemplateItemVO.getItem_type();
		// 有序号 按序号读取对应单元格内容
		if (ObjectUtil.isNotNull(Ix)) {
			Cell cell = row.getCell(Ix - 1); // excel 从0开始
			if (null != cell) {// 空单元格不做处理
				// 所有公式取计算结果
				cell = evaluator.evaluateInCell(cell);
				// 获取cell的值
				unchecked_value = getCellValue(cell);
				// 如果cell是数字 而目标是char或者varchar 去显示格式
				if ((JdbcType.forName(item_type) == JdbcType.VARCHAR || JdbcType.forName(item_type) == JdbcType.CHAR)
						&& cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					unchecked_value = formatNumericCell(unchecked_value, cell);
				}
				if (!ObjectUtil.isNotNull(unchecked_value))
					unchecked_value = excelTemplateItemVO.get("default_value", null);
			}
		} else {// 没有序号，读取模板默认值
				// 非excel读取，赋excelTemplateItemVO配置的默认值
			if (null == vo.get(itemCode)) {// 防止查询选择赋值后被覆盖
				vo.set(itemCode, vo.get(itemCode, unchecked_value));
			}else if(!StringUtils.isEmpty(unchecked_value)){//添加读取excelTemplateItemVO配置的默认值，当unchecked_value值不为空时读取
				vo.set(itemCode, unchecked_value);
			}
			unchecked_value = vo.get(itemCode);
		}

		// 保存excel未处理数据
		// 上面序号不为空时，这个值并没有放到vo中
		columnValues.put(itemCode, unchecked_value);
		// vo.set(itemCode,unchecked_value);
		// 处理查询选择，字典，日期
		if (!StringUtils.isEmpty(unchecked_value)) {
			if (StringUtils.hasLength(excelTemplateItemVO.getQuery_sel())) {
				// 解析查询选择配置，将map中包含的字段及值赋到vo中
				String func_code = excelTemplateItemVO.getFunc_code();
				String main_func_code = excelTemplateItemVO.getMain_func_code();
				if (null == main_func_code)
					main_func_code = "TOP";// 为主功能虚拟一个主功能
				String key = main_func_code + "." + func_code;
				List<AbstractVO> list = qyselectorMap.get(key+itemCode);
				handleQuery(unchecked_value, list, excelTemplateVO, excelTemplateItemVO, vo);
				
				columnValues.put(itemCode, vo.get(itemCode));
			} else if ("DATE".equals(item_type) || "DATETIME".equals(item_type)) {// 日期类型
				// 有输入设定的日期,一般数据库和vo里应该是字符串类型，模板可设为日期类型
				if (StringUtils.hasLength(excelTemplateItemVO.getDict())) {
					try {
						DateInputConfigVO inputConfigVO = JsonUtils.parse(excelTemplateItemVO.getDict(),
								DateInputConfigVO.class);
						String dateValue = null;
						if (unchecked_value instanceof Date) {
							dateValue = DateUtils.getDateStr((Date) unchecked_value, inputConfigVO);
						} else {
							dateValue = DateUtils.getDateByStr(unchecked_value.toString(), inputConfigVO);
						}
						vo.set(itemCode, dateValue);
						columnValues.put(itemCode, dateValue);
					} catch (Exception e) {
						Message message = new Message("第" + (rowIx + 1) + "行" + e.getMessage(), MessageLevel.ERROR,
								MessageShowType.POPUP);
						listMessage.add(message);
					}
				} else {// 正常日期
					if (unchecked_value instanceof Date) {
						vo.set(itemCode, unchecked_value);
						columnValues.put(itemCode, unchecked_value);
					} else if (unchecked_value instanceof String) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date date = sf.parse(unchecked_value.toString());
							vo.set(itemCode, date);
							columnValues.put(itemCode, date);
						} catch (ParseException e) {
							Message message = new Message("第" + (rowIx + 1) + "行,日期转化失败，请检查！", MessageLevel.ERROR,
									MessageShowType.POPUP);
							listMessage.add(message);
						}
					}
				}
			} else if (StringUtils.hasLength(excelTemplateItemVO.getDict())) {// 字典
				try {
					handleDict(columnValues, excelTemplateItemVO, vo);
					columnValues.put(itemCode, vo.get(itemCode));
				} catch (Exception e) {
					Message message = new Message("第" + (rowIx + 1) + "行" + e.getMessage(), MessageLevel.ERROR,
							MessageShowType.POPUP);
					listMessage.add(message);
				}
			}
		}
		// 校验excel录入字段并赋值
		Object uncheckedValue = columnValues.get(itemCode);
		ReturnResult<?> rr1 = ExcelValidations.validate(excelTemplateItemVO, vo, uncheckedValue);
		if (null != rr1 && null != rr1.getMessages() && rr1.getStatus() == Status.FAIL) {
			for (Message msg : rr1.getMessages()) {
				Message message = new Message("第" + (rowIx + 1) + "行" + msg.getMessage(), MessageLevel.ERROR,
						MessageShowType.POPUP);
				listMessage.add(message);
			}
		}
		return listMessage;
	}
	
	@SuppressWarnings("deprecation")
	private List<Message> getVoValue(ExcelTemplateVO excelTemplateVO, ExcelTemplateItemVO excelTemplateItemVO,
			Integer Ix, AbstractVO vo, FormulaEvaluator evaluator, Row row, int rowIx) throws HDException {
		Map<String, Object> columnValues = new HashMap<>();
		List<Message> listMessage = new ArrayList<>();
		String itemCode = excelTemplateItemVO.getItem_code().toLowerCase();
		//获取导入模板定义的默认值
		Object unchecked_value = excelTemplateItemVO.get("default_value", "");
		//获取列序号
		Ix = ObjectUtil.asInteger(excelTemplateItemVO.getColumn_no());
		//获取列类型
		String item_type = excelTemplateItemVO.getItem_type();
		// 有序号 按序号读取对应单元格内容
		if (ObjectUtil.isNotNull(Ix)) {
			Cell cell = row.getCell(Ix - 1); // excel 从0开始
			if (null != cell) {// 空单元格不做处理
				// 所有公式取计算结果
				cell = evaluator.evaluateInCell(cell);
				// 获取cell的值
				unchecked_value = getCellValue(cell);
				// 如果cell是数字 而目标是char或者varchar 去显示格式
				if ((JdbcType.forName(item_type) == JdbcType.VARCHAR || JdbcType.forName(item_type) == JdbcType.CHAR)
						&& cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					unchecked_value = formatNumericCell(unchecked_value, cell);
				}
				if (!ObjectUtil.isNotNull(unchecked_value))
					unchecked_value = excelTemplateItemVO.get("default_value", null);
			}
		} else {// 没有序号，读取模板默认值
				// 非excel读取，赋excelTemplateItemVO配置的默认值
			if (null == vo.get(itemCode)) {// 防止查询选择赋值后被覆盖
				vo.set(itemCode, vo.get(itemCode, unchecked_value));
			}else if(!StringUtils.isEmpty(unchecked_value)){//添加读取excelTemplateItemVO配置的默认值，当unchecked_value值不为空时读取
				vo.set(itemCode, unchecked_value);
			}
			unchecked_value = vo.get(itemCode);
		}
		// 保存excel未处理数据
		// 上面序号不为空时，这个值并没有放到vo中
		columnValues.put(itemCode, unchecked_value);
		// vo.set(itemCode,unchecked_value);
		// 处理查询选择，字典，日期
		if (!StringUtils.isEmpty(unchecked_value)) {
			if (StringUtils.hasLength(excelTemplateItemVO.getQuery_sel())) {
				//解析查询选择配置，将map中包含的字段及值赋到vo中
				handleQuery(columnValues, excelTemplateVO, excelTemplateItemVO, vo);
				columnValues.put(itemCode, vo.get(itemCode));
			} else if ("DATE".equals(item_type) || "DATETIME".equals(item_type)) {// 日期类型
				// 有输入设定的日期,一般数据库和vo里应该是字符串类型，模板可设为日期类型
				if (StringUtils.hasLength(excelTemplateItemVO.getDict())) {
					try {
						DateInputConfigVO inputConfigVO = JsonUtils.parse(excelTemplateItemVO.getDict(),
								DateInputConfigVO.class);
						String dateValue = null;
						if (unchecked_value instanceof Date) {
							dateValue = DateUtils.getDateStr((Date) unchecked_value, inputConfigVO);
						} else {
							dateValue = DateUtils.getDateByStr(unchecked_value.toString(), inputConfigVO);
						}
						vo.set(itemCode, dateValue);
						columnValues.put(itemCode, dateValue);
					} catch (Exception e) {
						Message message = new Message("第" + (rowIx + 1) + "行" + e.getMessage(), MessageLevel.ERROR,
								MessageShowType.POPUP);
						listMessage.add(message);
					}
				} else {// 正常日期
					if (unchecked_value instanceof Date) {
						vo.set(itemCode, unchecked_value);
						columnValues.put(itemCode, unchecked_value);
					} else if (unchecked_value instanceof String) {
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date date = sf.parse(unchecked_value.toString());
							vo.set(itemCode, date);
							columnValues.put(itemCode, date);
						} catch (ParseException e) {
							Message message = new Message("第" + (rowIx + 1) + "行,日期转化失败，请检查！" , MessageLevel.ERROR,
									MessageShowType.POPUP);
							listMessage.add(message);
						}
					}
				}
			} else if (StringUtils.hasLength(excelTemplateItemVO.getDict())) {// 字典
				try {
					handleDict(columnValues, excelTemplateItemVO, vo);
					columnValues.put(itemCode, vo.get(itemCode));
				} catch (Exception e) {
					Message message = new Message("第" + (rowIx + 1) + "行" + e.getMessage(), MessageLevel.ERROR,
							MessageShowType.POPUP);
					listMessage.add(message);
				}
			}
		}
		// 校验excel录入字段并赋值
		Object uncheckedValue = columnValues.get(itemCode);
		ReturnResult<?> rr1 = ExcelValidations.validate(excelTemplateItemVO, vo, uncheckedValue);
		if (null != rr1 && null != rr1.getMessages() && rr1.getStatus() == Status.FAIL) {
			for (Message msg : rr1.getMessages()) {
				Message message = new Message("第" + (rowIx + 1) + "行" + msg.getMessage(), MessageLevel.ERROR,
						MessageShowType.POPUP);
				listMessage.add(message);
			}
		}
		return listMessage;
	}

	@Override
	/**
	 * 下载模板
	 * 
	 * @param excelTemplateVO
	 * @param excelTemplateItemVOs
	 * @return InputStream
	 * @throws HDException
	 */
	@Deprecated
	public InputStream exportExcel(ExcelTemplateVO excelTemplateVO, List<ExcelTemplateItemVO> excelTemplateItemVOs)
			throws HDException {
		String file_type = excelTemplateVO.getFile_type();
		Workbook wb;

		if (file_type.equalsIgnoreCase(ExcelTemplateConstants.XLS))
			wb = new HSSFWorkbook();
		else
			wb = new XSSFWorkbook();

		Sheet sheet = wb.createSheet(excelTemplateVO.getTemp_name());
		// 隐藏sheet页 标识功能名
		Sheet sheet_func_code = wb.createSheet(excelTemplateVO.getFunc_code());
		wb.setSheetOrder(sheet_func_code.getSheetName(), wb.getSheetIndex(sheet) + 1);
		wb.setSheetHidden(wb.getSheetIndex(sheet_func_code), Workbook.SHEET_STATE_VERY_HIDDEN);
		Integer endMarker = ObjectUtil.asInteger(excelTemplateVO.getEmarker());
		if (null == endMarker)
			endMarker = ExcelTemplateConstants.DICTENDROW;
		Row row = sheet.createRow(0);
		Integer Ix, colIndex = 0;
		Iterator<ExcelTemplateItemVO> it = excelTemplateItemVOs.iterator();
		while (it.hasNext()) {
			ExcelTemplateItemVO excelTemplateItemVO = it.next();
			// Ix one-based
			Ix = ObjectUtil.asInteger(excelTemplateItemVO.getColumn_no());
			// 未填写序号下载模板不处理此列
			if (null == Ix)
				continue;
			// Ix one-based
			Cell cell = row.createCell(Ix - 1);
			cell.setCellValue(excelTemplateItemVO.getColumn_title());
			row.setHeightInPoints(15f);

			// datatime类型 格式限制
			if (JdbcType.DATETIME.getTypeName().equalsIgnoreCase(excelTemplateItemVO.getItem_type())) {
				DataValidationHelper helper = sheet.getDataValidationHelper();
				// 序列类约束 公式 例如：SY_ORG!$A$1:$A$1000
				DataValidationConstraint constraint = helper.createDateConstraint(DVConstraint.OperatorType.BETWEEN,
						"=DATE(1949,10,1)", "=DATE(2099,12,31)", null);
				// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
				Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
				CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker, cell.getColumnIndex(),
						cell.getColumnIndex());
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
			// 带出字典项
			String dict_str = excelTemplateItemVO.getDict();
			String query_selector_str = excelTemplateItemVO.getQuery_sel();
			if (StringUtils.hasLength(dict_str) || StringUtils.hasLength(query_selector_str)) {// 处理字典
				// 字典项有值
				int dict_len = 0;
				int qs_len = 0;
				if (StringUtils.hasLength(dict_str)) {
					dict_len = queryDict(sheet_func_code, colIndex, excelTemplateItemVO);
					if (dict_len <= 0)
						continue;
				}
				if (StringUtils.hasLength(query_selector_str)) {
					qs_len = querySelector(sheet_func_code, colIndex, excelTemplateItemVO);
					if (qs_len <= 0)
						continue;
				}
				// create数据校验
				DataValidationHelper helper = sheet.getDataValidationHelper();
				// 加载下拉列表内容
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(sheet_func_code.getSheetName()).append("!$")
						.append(CellReference.convertNumToColString(colIndex)).append("$1").append(":$")
						.append(CellReference.convertNumToColString(colIndex)).append("$").append(dict_len);
				// 序列类约束 公式 例如：SY_ORG!$A$1:$A$1000
				DataValidationConstraint constraint = helper
						.createFormulaListConstraint(excelTemplateItemVO.getColumn_title());
				// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
				Integer stratMarker = ObjectUtil.asInteger(excelTemplateVO.getString("smarker", "1"));
				CellRangeAddressList regions = new CellRangeAddressList(stratMarker, endMarker, cell.getColumnIndex(),
						cell.getColumnIndex());
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
				colIndex += 2;
			}
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
					throw new HDException("功能模板中的附件必须是excel格式");
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
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				wb.write(outputStream);
				outputStream.flush();
				outputStream.close();
				return new ByteArrayInputStream(outputStream.toByteArray());
			} catch (IOException e1) {
				throw new HDException(e1.getMessage(), e1);
			}
		}

		// Write the output to ByteArrayOutputStream
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			wb.write(outputStream);
			outputStream.flush();
			outputStream.close();
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException e) {
			throw new HDException(e.getMessage(), e);
		}

	}

	/**
	 * 处理字典匹配项
	 * @param temp_item_value
	 * @param excelTemplateItemVO
	 * @param vo
	 * @return: void
	 * @Author: suntaiming
	 * @Date: 2021/6/11 13:50
	 */
	private void handleDict(Object temp_item_value, ExcelTemplateItemVO excelTemplateItemVO,
							AbstractVO vo) throws HDException {
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		if(StringUtils.isEmpty(temp_item_value) || excelTemplateItemVO.getIs_enable() == SyConstant.SY_FALSE){
			return;
		}
		String dict_str = excelTemplateItemVO.getDict();
		DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(dict_str);
		//定义的字典编码
		String dictCode = dictInputConfigVO.getDictcode();
		Map<String, String> dictMap = dictInputConfigVO.getMap();
		//查询具体的字典数据
		List<DictDataWarperVO> dictDataVoList = DictUtils.getDictData(dictCode);
		if (dictInputConfigVO.getDictdata() != null) {
			dictDataVoList = dictInputConfigVO.getDictdata();
		}
		List<DictDataWarperVO> allDataList = TemplateUtils.getAllDataList(dictDataVoList);
		//缓存数据字典vo，key为名称
		Map<String, DictDataWarperVO> allDataMap = new HashMap<String, DictDataWarperVO>();
		for(DictDataWarperVO allData:allDataList){
			allDataMap.put(allData.getName(), allData);
		}
		//解析temp_item_value
		//支持批量的导入
		String splitRegex = ",|，";
		String split = dictInputConfigVO.getSplit();
		if(!StringUtils.isEmpty(split)){
			splitRegex = split;
		}
	    String[] values = temp_item_value.toString().split(splitRegex);
	    //未匹配的名称
	    //用于检查输入的字典名称是否系统中存在
		List<String> unMatchValueList = new ArrayList<String>();
		for(String value:values){
			if(!allDataMap.containsKey(value)){
				unMatchValueList.add(value);
			}
		}
		//包含未匹配的值
		if(unMatchValueList.size()!=0){
			String errMsg = MessageFormat.format("{0}列，值{1}，未找到对应字典名称！",
					excelTemplateItemVO.getItem_name(),
					unMatchValueList.toString());
			throw new HDException(errMsg);
		}else{
			//记录值
			Map<String, StringBuilder> valueMap = new HashMap<String, StringBuilder>();
			for(String value:values){
				DictDataWarperVO dictDataVo = allDataMap.get(value);
				if (null != dictMap) {
					for (Entry<String, String> entry : dictMap.entrySet()) {
						String key = entry.getKey();
						if ("id".equals(entry.getKey())) {
							key = "dictdataid";
						}
						setMapValue(valueMap, entry.getValue(), dictDataVo.get(key));
					}
				} else {
					setMapValue(valueMap, item_code, dictDataVo.getCode());
				}
			}
			for(Map.Entry<String, StringBuilder> entry:valueMap.entrySet()){
				vo.set(entry.getKey(), entry.getValue().toString());
			}
		}
	}

	/**
	 * 处理字典匹配项
	 *
	 * @param vo
	 * @param excelTemplateItemVO
	 * @throws HDException
	 */
	private void handleDict(Map<String, Object> columnValues, ExcelTemplateItemVO excelTemplateItemVO,
			AbstractVO vo) throws HDException {
		// excel里值 对应 字典表哪个字段
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		//获取excel中定义的值
		Object temp_item_value = columnValues.get(item_code);

		handleDict(temp_item_value, excelTemplateItemVO, vo);
	}
	/**
	 * @Description: 设置map值，map的value为stringbuilder对象
	 * @author: wangyi
	 * @date: 2018年8月29日
	 */
	private void setMapValue(Map<String, StringBuilder> valueMap, String key, Object value){
		if(valueMap.containsKey(key)){
			valueMap.get(key).append(",").append(value);
		}else{
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			valueMap.put(key, sb);
		}
	}

	/**
	 * 处理查询选择匹配项
	 * 
	 * @param excelTemplateVO
	 * @param vo
	 * @param excelTemplateItemVO
	 * @throws HDException
	 */
	private void handleQuery(Object temp_item_value,List<AbstractVO> listData, ExcelTemplateVO excelTemplateVO,
			ExcelTemplateItemVO excelTemplateItemVO, AbstractVO vo) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		// excel里值 对应 字典表哪个字段
		List<String> updateList = new ArrayList<>();
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		String item_name = excelTemplateItemVO.getItem_name();
		String query_selector_str = excelTemplateItemVO.getQuery_sel();
		QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
				.getQueryselectorInputConfigVO(query_selector_str);
		String split = queryselectorInputConfigVO.getSplit();
		Map<String, String> map = queryselectorInputConfigVO.getMap();

		//赋值VO
		vo.set(item_code, temp_item_value);
		
		String unique = queryselectorInputConfigVO.getUnique();
		String where = queryselectorInputConfigVO.getWhere();
		
		String cur_item_code = item_code;
		//查询选择key为待查功能的字段，value为当前vo的字段
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals(item_code)) {
				//这里特殊处理，用到后面的unique字段。
				cur_item_code = entry.getKey();
			}
			updateList.add(entry.getValue());
		}
		// listData is null ||where is not null? exec query
		// TODO where 完善
		if((listData==null || listData.isEmpty()) || !StringUtils.isEmpty(where)  ) {
			
			excelTemplateVO.set("updateList", updateList);// 后续需要更新的字段
			// 设置唯一字段编码
			queryselectorInputConfigVO.setUnique(cur_item_code);
			try {
				querySelectHandler.id2Name(vo, queryselectorInputConfigVO, tenantid);
			} catch (Exception e) {
				throw new HDException(item_name + "excel中此列查询选择转换出错，请检查数据是否符合规范!");
			}
		}
		
		// unique is null || 有值 为空     
		if(StringUtils.isEmpty(unique) || (!StringUtils.isEmpty(unique) || null==(vo.get(unique)))) {
			unique = cur_item_code;
		}
		//唯一字段 数据分组
		Map<Object, List<AbstractVO>> groupedListByProp = VOCollectionUtils.groupedListByProp(listData, unique, Object.class);
		
		if(groupedListByProp!=null &&  !groupedListByProp.isEmpty() &&
				vo.get(item_code)!=null) {
			if (queryselectorInputConfigVO.isIsmulti()) {
				Object pk = vo.get(item_code);
				String[] pks = pk.toString().split(split);
				List<AbstractVO> list = new ArrayList<AbstractVO>();
				Object uniqueType = null;
				for(Entry<Object, List<AbstractVO>> entry : groupedListByProp.entrySet()) {
					uniqueType = entry.getKey();
					break;
				}
				for (String string : pks) {
					if(uniqueType instanceof String){
						if (groupedListByProp.containsKey(string)) {
							list.addAll(groupedListByProp.get(string));
						}
					}
					else {
						if (groupedListByProp.containsKey(ConvertUtils.convert(string, uniqueType.getClass()))) {
							list.addAll(groupedListByProp.get(ConvertUtils.convert(string, uniqueType.getClass())));
						}
					}
				}
				for(Entry<String, String> entry : queryselectorInputConfigVO.getMap().entrySet()) {
					StringBuilder valueBuilder = new StringBuilder();
					for(AbstractVO foreignvo : list) {
						if(foreignvo!=null) {
							valueBuilder.append(foreignvo.get(entry.getKey()));
						}
						valueBuilder.append(split);
					}
					if(valueBuilder.length()>0)
						valueBuilder.deleteCharAt(valueBuilder.length() - split.length());
					vo.set(entry.getValue(), valueBuilder.toString());
				}
			}else {
				if(groupedListByProp.containsKey(vo.get(item_code))) {
					Object pk = vo.get(item_code);
					List<AbstractVO> list = groupedListByProp.get(pk);
					
					for(Entry<String, String> entry : queryselectorInputConfigVO.getMap().entrySet()) {
						vo.set(entry.getValue(), list.get(0).get(entry.getKey()));
					}
				}
			}
		}
	}
	/**
	 * 处理查询选择匹配项
	 * 
	 * @param excelTemplateVO
	 * @param vo
	 * @param excelTemplateItemVO
	 * @throws HDException
	 */
	private void handleQuery(Map<String, Object> columnValues, ExcelTemplateVO excelTemplateVO,
			ExcelTemplateItemVO excelTemplateItemVO, AbstractVO vo) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		// excel里值 对应 字典表哪个字段
		List<String> updateList = new ArrayList<>();
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		String item_name = excelTemplateItemVO.getItem_name();
		//上面excel读到的值
		Object temp_item_value = columnValues.get(item_code);
		//放到了vo中
		vo.set(item_code, temp_item_value);
		String query_selector_str = excelTemplateItemVO.getQuery_sel();
		QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
				.getQueryselectorInputConfigVO(query_selector_str);
		// String tableName =
		// funcService.getOperaTableNameOfFunc(queryselectorInputConfigVO.getFunccode(),tenantid);
		Map<String, String> map = queryselectorInputConfigVO.getMap();
		String cur_item_code = item_code;
		//查询选择key为待查功能的字段，value为当前vo的字段
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals(item_code)) {
				//这里特殊处理，用到后面的unique字段。
				cur_item_code = entry.getKey();
			}
			updateList.add(entry.getValue());
		}
		excelTemplateVO.set("updateList", updateList);// 后续需要更新的字段
		//这里设置unique值，张丰很清楚
		queryselectorInputConfigVO.setUnique(cur_item_code);
		// QuerySelectHandler querySelectHandler =
		// AppServiceHelper.findBean(QuerySelectHandler.class);
		try {
			querySelectHandler.id2Name(vo, queryselectorInputConfigVO, tenantid);
		} catch (Exception e) {
			throw new HDException(item_name + "excel中此列查询选择转换出错，请检查数据是否符合规范");
		}
		// IId2NameStrategy st = new DefaultId2NameStrategy();
		// Id2NameVO id2namevo = new Id2NameVO();
		// id2namevo.setInputConfigVO(queryselectorInputConfigVO);
		// id2namevo.setTableName(tableName);
		// id2namevo.setTenantid(tenantid);
		// id2namevo.setUniqueColName(cur_item_code);
		// st.assignName4multiple(vo, id2namevo);
	}

	/**
	 * 查询字典项
	 * 
	 * @param sheet
	 *            隐藏sheet页
	 * @param colIndex
	 *            字典所在列数
	 * @param excelTemplateItemVO
	 * @return 字典项个数
	 */
	private int queryDict(Sheet sheet, int colIndex, ExcelTemplateItemVO excelTemplateItemVO) {
		String dict_str = excelTemplateItemVO.getDict();
		// String query_selector_str = excelTemplateItemVO.getWhere_item();
		int resArr = 0;
		if (!StringUtils.isEmpty(dict_str)) {// 处理字典
			try {
				DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(dict_str);
				String dictCode = dictInputConfigVO.getDictcode();
				List<DictDataWarperVO> dictDataVoList = DictUtils.getDictData(dictCode);
				List<DictDataWarperVO> allDataList = TemplateUtils.getAllDataList(dictDataVoList);
				if (ObjectUtil.isNotEmpty(allDataList)) {
					for (int i = 0; i < allDataList.size(); i++) {
						DictDataWarperVO dictDataVo = allDataList.get(i);
						Row row;
						if (ObjectUtil.isNotNull(sheet.getRow(i))) {
							row = sheet.getRow(i);
						} else
							row = sheet.createRow(i);
						// 字段项列(name)
						Cell cell_value = row.createCell(colIndex);
						cell_value.setCellValue(dictDataVo.getName());
						// 主键列
						Cell cell_pk = row.createCell(colIndex + 1);
						cell_pk.setCellValue(String.valueOf(dictDataVo.getDictdataid()));
					}
					resArr = allDataList.size();
					// 定义名称 储存下拉备选列 name excelTemplateItemVO.getColumn_title()
					Name name = sheet.getWorkbook().createName();
					name.setNameName(excelTemplateItemVO.getColumn_title());
					StringBuilder stringBuffer = new StringBuilder();
					stringBuffer.append(sheet.getSheetName()).append("!$")
							.append(CellReference.convertNumToColString(colIndex)).append("$1:$")
							.append(CellReference.convertNumToColString(colIndex)).append("$").append(resArr);
					name.setRefersToFormula(stringBuffer.toString());
					// 定义名称 储存下拉备选列对应主键 name_id excelTemplateItemVO.getColumn_title()+"_id"
					Name name_id = sheet.getWorkbook().createName();
					name_id.setNameName(excelTemplateItemVO.getColumn_title() + "_主键");
					stringBuffer.delete(0, stringBuffer.length());
					stringBuffer.append(sheet.getSheetName()).append("!$")
							.append(CellReference.convertNumToColString(colIndex + 1)).append("$1:$")
							.append(CellReference.convertNumToColString(colIndex + 1)).append("$").append(resArr);
					name_id.setRefersToFormula(stringBuffer.toString());
					return resArr;
				}
			} catch (HDException e) {
				logger.error(e.getMessage());
				return 0;
			}
		}
		return resArr;
	}

	// private List<DictDataWarperVO> getAllDataList(List<DictDataWarperVO>
	// dictDataVoList) {
	// List<DictDataWarperVO> allDataList = new ArrayList<>();
	// for(int i=0;i<dictDataVoList.size();i++ ){
	// DictDataWarperVO dictDataVo = dictDataVoList.get(i);
	// List<DictDataWarperVO> childrenList = dictDataVo.getChildren();
	// allDataList.add(dictDataVo);
	// if(ObjectUtil.isNotEmpty(childrenList)){
	// allDataList.addAll(getAllDataList(childrenList));
	// }
	// }
	// return allDataList;
	// }

	/**
	 * 查询选择
	 * 
	 * @param sheet
	 *            隐藏sheet页
	 * @param colIndex
	 *            查询选择所在列数
	 * @param excelTemplateItemVO
	 * @return 查询选择项个数
	 */
	private int querySelector(Sheet sheet, int colIndex, ExcelTemplateItemVO excelTemplateItemVO) {
		// String dict_str = excelTemplateItemVO.getValue_item();
		Long tenantid = CurrentEnvUtils.getTenantId();
		String query_selector_str = excelTemplateItemVO.getQuery_sel();
		String item_code = excelTemplateItemVO.getItem_code().toLowerCase();
		int resArr = 0;
		if (!StringUtils.isEmpty(query_selector_str)) {// 处理查询选择
			try {
				QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
						.getQueryselectorInputConfigVO(query_selector_str);
				String funcCode = queryselectorInputConfigVO.getFunccode();
				String tableName = funcService.getOperaTableNameOfFunc(funcCode, tenantid);
				String where = queryselectorInputConfigVO.getWhere();
				Map<String, String> map = queryselectorInputConfigVO.getMap();
				String cur_item_code = item_code;
				for (Entry<String, String> entry : map.entrySet()) {
					if (entry.getValue().equals(item_code)) {
						cur_item_code = entry.getKey();
					}
				}
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				// 租户
				dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
				// where条件
				dynaSqlVO.setWhereClause(where);
				VOSet<AbstractVO> voset = baseService.query(tableName, dynaSqlVO);
				if (!voset.isEmpty()) {
					TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);// 查询表信息
					String pkName = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO().getColcode() : null;
					ObjectUtil.validNotNull(pkName, "table:" + tableName + "的主键列没有找到");
					for (int i = 0; i < voset.getVoList().size(); i++) {
						AbstractVO tmpvo = voset.getVO(i);
						Row row;
						if (ObjectUtil.isNotNull(sheet.getRow(i))) {
							row = sheet.getRow(i);
						} else
							row = sheet.createRow(i);
						// 字段项列(name)
						Cell cell_value = row.createCell(colIndex);
						cell_value.setCellValue(tmpvo.getString(cur_item_code));
						// 主键列
						Cell cell_pk = row.createCell(colIndex + 1);
						cell_pk.setCellValue(String.valueOf(tmpvo.getLong(pkName)));
						resArr++;
					}
					// 定义名称 储存下拉备选列 name excelTemplateItemVO.getColumn_title()
					Name name = sheet.getWorkbook().createName();
					name.setNameName(excelTemplateItemVO.getColumn_title());
					StringBuilder stringBuffer = new StringBuilder();
					stringBuffer.append(sheet.getSheetName()).append("!$")
							.append(CellReference.convertNumToColString(colIndex)).append("$1:$")
							.append(CellReference.convertNumToColString(colIndex)).append("$").append(resArr);
					name.setRefersToFormula(stringBuffer.toString());
					// 定义名称 储存下拉备选列对应主键 name_id excelTemplateItemVO.getColumn_title()+"_id"
					Name name_id = sheet.getWorkbook().createName();
					name_id.setNameName(excelTemplateItemVO.getColumn_title() + "_主键");
					stringBuffer.delete(0, stringBuffer.length());
					stringBuffer.append(sheet.getSheetName()).append("!$")
							.append(CellReference.convertNumToColString(colIndex + 1)).append("$1:$")
							.append(CellReference.convertNumToColString(colIndex + 1)).append("$").append(resArr);
					name_id.setRefersToFormula(stringBuffer.toString());
					return resArr;
				}
			} catch (HDException e) {
				logger.error(e.getMessage());
				return 0;
			}
		}
		return resArr;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends AbstractVO> getVOClass(TableDefVO tableDefVO) {
		ObjectUtil.validNotNull(tableDefVO, "tableDefVO is required.");
		if (ObjectUtil.isNotNull(tableDefVO.getClassname())) {
			try {
				return (Class<? extends AbstractVO>) ResourceUtil.classForName(tableDefVO.getClassname());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
				return BaseVO.class;
			}
		}
		return BaseVO.class;
	}

	/**
	 * 获得单元格的值
	 * 
	 * @param cell
	 * @return Object
	 */
	@SuppressWarnings("deprecation")
	private Object getCellValue(Cell cell) {
		Object unchecked_value = null;
		// 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
		// 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			unchecked_value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			// 这里的日期类型会被转换为数字类型，需要判别后区分处理
			if (DateUtil.isCellDateFormatted(cell))
				unchecked_value = cell.getDateCellValue();
			else
				unchecked_value = cell.getNumericCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			unchecked_value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_ERROR:
			break;
		default:
			break;
		}
		return unchecked_value;
	}

	/**
	 * 查询选择数据
	 * @param excelTemplateVO
	 * @param excelTemplateItemVO
	 * @param vo
	 * @return 
	 * @throws HDException
	 */
	private List<AbstractVO> getQuerySelectorData(ExcelTemplateItemVO excelTemplateItemVO) throws HDException {
		
		Long tenantid = CurrentEnvUtils.getTenantId();
		String query_selector_str = excelTemplateItemVO.getQuery_sel();
		QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
				.getQueryselectorInputConfigVO(query_selector_str);
		
		String table_name = funcService.getQueryTableNameOfFunc( queryselectorInputConfigVO.getFunccode(), tenantid);
		DynaSqlVO dysql = new DynaSqlVO();
		dysql.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dysql.setWhereClause(queryselectorInputConfigVO.getWhere());
		return baseService.query(table_name, dysql).getVoList();
		
	}	
	/**
	 * 原样返回数值单元格的内容
	 */
	@SuppressWarnings("deprecation")
	private String formatNumericCell(Object value, Cell cell) {
		if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC && cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
			return null;
		}
		// isCellDateFormatted判断该单元格是"时间格式"或者该"单元格的公式算出来的是时间格式"
		if (DateUtil.isCellDateFormatted(cell)) {
			// cell.getDateCellValue()碰到单元格是公式,会自动计算出Date结果
			// Date date = DateUtil.getJavaDate(value);
			Date date = cell.getDateCellValue();
			DataFormatter dataFormatter = new DataFormatter();
			Format format = dataFormatter.createFormat(cell);
			return format.format(date);
		} else {// 如12%，在excel读出时为0.12，要显示12%字符串，需要进行转换
			DataFormatter dataFormatter = new DataFormatter();
			Format format = dataFormatter.createFormat(cell);
			return format.format(value);
		}
	}

}
