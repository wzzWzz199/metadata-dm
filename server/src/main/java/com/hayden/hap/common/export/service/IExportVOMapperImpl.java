package com.hayden.hap.common.export.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.hayden.hap.common.attach.itf.IAttachService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.dict.itf.IDictDataService;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.excel.utils.ExcelTemplateConstants;
import com.hayden.hap.common.export.entity.ExportTemplateItemVO;
import com.hayden.hap.common.export.entity.ExportTemplateVO;
import com.hayden.hap.common.export.itf.IExportVOMapper;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("iExportVOMapperImpl")
public class IExportVOMapperImpl implements IExportVOMapper {

	private static final Logger logger = LoggerFactory.getLogger(IExportVOMapperImpl.class);
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IFuncService funcService;
	@Autowired
	private IFormItemPCService formItemPCService;	
	@Autowired
	private ITableDefService tableDefService; 
	@Autowired
	private IAttachService attachService;
	@Autowired
	private IDictDataService dictDataService;
	
	/**
	 * 无模板导出
	 * @see com.hayden.hap.common.excel.itf.IExcelVOMapper#exportExcel(com.hayden.hap.common.common.entity.VOSet, com.hayden.hap.common.formmgr.entity.FormParamVO, java.lang.String, java.lang.Long)
	 * @author liyan
	 * @throws HDException 
	 * @date 2016年9月12日
	 */
	@SuppressWarnings("deprecation")
	@Override
	public HashMap<String,Object> exportExcel(List<? extends AbstractVO> voList,FormParamVO formParamVO, String funcnama, Long tenantid, String exportids, String suf, int exportcountb, int exportcounte) throws HDException{
		@SuppressWarnings("resource")
		Workbook wb = new XSSFWorkbook();
		HashMap<String,Object> map = new HashMap<String,Object>();
		ReturnResult<?> returnResult = new ReturnResult<>();
		List<Message> listMessage = new ArrayList<>(); 
		Sheet sheet = wb.createSheet(funcnama);
		//隐藏sheet页 标识功能名
		Sheet sheet_func_code = wb.createSheet(formParamVO.getFuncCode());
		wb.setSheetOrder(sheet_func_code.getSheetName(), wb.getSheetIndex(sheet)+1);
		wb.setSheetHidden(wb.getSheetIndex(sheet_func_code), Workbook.SHEET_STATE_VERY_HIDDEN);
		int rownum = 1;
		int colIndex = 0;
		FuncVO funcVO =  funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);
		List<FormItemPCVO> formItemvolist = formItemPCService.getFormItemsByFormcode(funcVO.getFunc_info(), tenantid);
		Integer endMarker = voList.size();
		Boolean hasids = false;
		String pk ="";
		String[] splitids = null;
		//List<? extends AbstractVO> list = voset.getVoList();
		if(StringUtils.isNotEmpty(exportids)){
			splitids = exportids.split(",");
			exportcountb=1;
			hasids =true;
			String tableName = funcService.getOperaTableNameOfFunc(formParamVO.getFuncCode(),tenantid);
			TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//操作表信息
			String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
			pk = pkColName;
		}else{			
			if(endMarker==0 || endMarker<exportcountb){
				//throw new HDException("所选区间无记录可导出");
				Message message = new Message("所选区间无记录可导出",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);
				map.put("stream", null);
				map.put("rm", returnResult);
				return map;
			}
		}
		Iterator<FormItemPCVO> it = formItemvolist.iterator();
		while (it.hasNext()){
			FormItemPCVO formItemVO = it.next();
			HashMap<String,String> dictmap = new HashMap<String,String>();
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sfdate=new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sftime=new SimpleDateFormat("HH:mm:ss");
			String value_type = formItemVO.getFitem_data_type();
			String value_config = "";
			//不在列表中显示
			if(0==formItemVO.getFitem_show_list()) continue;
			if(InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())){//处理字典编码
				String str = formItemVO.getFitem_input_config();
				@SuppressWarnings("unchecked")
				Map<String, String> strTomap = JsonUtils.parse(str, HashMap.class);
				String dict = strTomap.get("dictcode");
				value_config = formItemVO.getFitem_input_config();
				List<DictDataVO> tablecolvolist = dictDataService.getDictData(dict, tenantid);
				for(DictDataVO dictdatavo: tablecolvolist){
					dictmap.put(dictdatavo.getDict_data_code(), dictdatavo.getDict_data_name());
				}
			}
			Row row0 = null;
			if(null==sheet.getRow(0)){
				row0 =sheet.createRow(0);
			}
			else{
				row0 = sheet.getRow(0);
			}
			Cell cell0 = row0.createCell(colIndex);
			cell0.setCellValue(formItemVO.getFitem_name());
			rownum = 1;
			for(int i=exportcountb;i<=endMarker;i++){//行循环
				if(hasids && !checkExits(splitids,voList.get(i-1).getLong(pk).toString())) continue;
				String value = "";
				if(DataTypeEnum.DATE.getCode().equals(value_type)&&null!=voList.get(i-1).get(formItemVO.getFitem_code())){
					if(value_config.equalsIgnoreCase("DATE")){
						value = sfdate.format(voList.get(i-1).get(formItemVO.getFitem_code()));
					}else if(value_config.equalsIgnoreCase("TIME")){
						value = sftime.format(voList.get(i-1).get(formItemVO.getFitem_code()));
					}else{								
						value = sf.format(voList.get(i-1).get(formItemVO.getFitem_code()));	
					} 
					value = sf.format(voList.get(i-1).get(formItemVO.getFitem_code()));
				}
				else{
					 value = voList.get(i-1).getString(formItemVO.getFitem_code());
				}
				Row row = null;
				if(null==sheet.getRow(rownum)){
					row =sheet.createRow(rownum);
				}
				else{
					row = sheet.getRow(rownum);

				}
				Cell cell = row.createCell(colIndex);
				if(InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())){//处理字典编码	
					cell.setCellValue(dictmap.get(value));
				}
				else{
						cell.setCellType(1);
						cell.setCellValue(value);
				}
				row.setHeightInPoints(15f); 
				rownum++;
			}
            colIndex++;
		}
		// Write the output to ByteArrayOutputStream
		try {
			if(".xlsx".equals(suf)){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				wb.write(outputStream);
				outputStream.flush();
				outputStream.close();
				InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); 
				map.put("stream", inputStream);
				map.put("rm", returnResult);
				return map;
			}
			else if(".txt".equals(suf)){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				StringBuffer write = new StringBuffer();  
				String tab = "	";  
				String enter = "\r\n"; 
				for (int i = 0; i < rownum; i++) {  
					Row row = sheet.getRow(i);
					for(int j = 0; j < colIndex; j++){
						Cell cell = row.getCell(j); 
						if(null!=cell){
							String str = getCellValue(cell)==null?"":getCellValue(cell).toString();
							int length = 0;
							for(int len =0;len<str.length();len++){
								if(isChinese(str.charAt(len))){
									length+=2;
								}
								else{
									length++;
								}
							}
							if(length<20){
								while(length<20){
									str+=" ";
									length++;
								}
							}
							write.append(str + tab);  
						}		
					}
					write.append(enter);            
				}
				outputStream.write(write.toString().getBytes("UTF-8"));  
				outputStream.flush();  
				outputStream.close();  
				InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); 
				map.put("stream", inputStream);
				returnResult.setMessages(listMessage);
				map.put("rm", returnResult);
				return map;
		    }
			else{
				Message message = new Message("导出类型有误",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);
				map.put("stream", null);
				map.put("rm", returnResult);
				return map;
			}
		} catch (IOException e) {
			logger.debug(e.getMessage());
		}
		return map;
		
	}
	
	/**
	 * 按模板导出
	 *
	 * @see com.hayden.hap.common.export.itf.IExportVOMapper#exportExcelModel(java.util.List, java.lang.String, com.hayden.hap.common.export.entity.ExportTemplateVO, java.lang.Long, java.lang.String, java.lang.Long)
	 * @author liyan
	 * @throws HDException 
	 * @date 2016年9月18日
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	@Override
	public HashMap<String,Object> exportExcelTemplate(List<? extends AbstractVO> voList,
			List<ExportTemplateItemVO> exportTemplateItemVOs, String suf,
			ExportTemplateVO exportTemplateVO, Long tenantid, String exportids,
			int exportcountb, int exportcounte, String txt_separator) throws HDException {
		Workbook wb = null;
		HashMap<String,Object> map = new HashMap<String,Object>();
		ReturnResult<?> returnResult = new ReturnResult<>();
		List<Message> listMessage = new ArrayList<>(); 
		FuncVO funcVO =  funcService.queryByFunccode(exportTemplateVO.getFunc_code(), tenantid);
		List<FormItemPCVO> formItemvolist = formItemPCService.getFormItemsByFormcode(funcVO.getFunc_info(), tenantid);
		Map<String,FormItemVO> fitem_codeMapper = new ConcurrentHashMap<>();
		//映射表单字段为字段编码为key的map
		int colnum = 0;//判断列数
		for(FormItemVO formItemVO:formItemvolist){
			fitem_codeMapper.put(formItemVO.getFitem_code().toLowerCase(), formItemVO);
			colnum ++;
		}
		if (suf.toLowerCase().equals("."+ExcelTemplateConstants.XLS)) {
			wb = new HSSFWorkbook();
			if(colnum>255){
				Message message = new Message("导出列数不能超过255",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);		
				map.put("stream", null);
				map.put("rm", returnResult);
				return map;
			}
		} else{// if (suf.toLowerCase().equals("."+ExcelTemplateConstants.XLSX)) {
			wb = new XSSFWorkbook();
			if(colnum>16383 && suf.toLowerCase().equals("."+ExcelTemplateConstants.XLSX)){
				Message message = new Message("导出列数不能超过16383",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);	
				map.put("stream", null);
				map.put("rm", returnResult);
				return map;
			}
		}
		Sheet sheet = wb.createSheet(exportTemplateVO.getExport_temp_name());
		Integer endMarker = voList.size();
		
		Boolean hasids = false;
		String pk ="";
		String[] splitids = null;
		//List<? extends AbstractVO> list = voset.getVoList();
		if(!StringUtils.isEmpty(exportids)){
			splitids = exportids.split(",");
			exportcountb=1;
			hasids =true;
			String tableName = funcService.getOperaTableNameOfFunc(exportTemplateVO.getFunc_code(),tenantid);
			TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//操作表信息
			String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
			pk = pkColName;
		}else{
			if(endMarker==0 || endMarker<exportcountb){
				//throw new HDException("所选区间无记录可导出");
				Message message = new Message("所选区间无记录可导出",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);
				map.put("stream", null);
				map.put("rm", returnResult);
				return map;
			}
		}
		Integer colIndex =0;
		int rownum = 1;
		for(ExportTemplateItemVO exportTempvo:exportTemplateItemVOs){
			HashMap<String,String> dictmap = new HashMap<String,String>();
			//隐藏列
			if(0==exportTempvo.getIs_display()) continue;
			String input_config_type = "";
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			SimpleDateFormat sfdate=new SimpleDateFormat("yyyy-MM-dd"); 
			SimpleDateFormat sftime=new SimpleDateFormat("HH-mm-ss"); 
			String value_type = "";
			String value_config = "";
			if(fitem_codeMapper.containsKey(exportTempvo.getExport_item_code().toLowerCase())){
				FormItemVO formItemVO = fitem_codeMapper.get(exportTempvo.getExport_item_code().toLowerCase());
				//数据类型
				value_type = formItemVO.getFitem_data_type();
				//输入设定
				value_config = formItemVO.getFitem_input_config();
				//字段类型，读取表单的fitem_input_type
				input_config_type = exportTempvo.getItem_type();
				if(InputTypeEnum.DICT_NEW.getCode().equals(exportTempvo.getItem_type())){//处理字典编码
					String str = formItemVO.getFitem_input_config();
					@SuppressWarnings("unchecked")
					Map<String, String> strTomap = JsonUtils.parse(str, HashMap.class);
					String dict = strTomap.get("dictcode");
//					String dict = str;
//					if(str.contains(",")){
//						dict =str.substring(0, str.indexOf(","));		
//					}
					List<DictDataVO> tablecolvolist = dictDataService.getDictData(dict, tenantid);
					for(DictDataVO dictdatavo: tablecolvolist){
						dictmap.put(dictdatavo.getDict_data_code(), dictdatavo.getDict_data_name());
					}
				}
				
			}
			Row row0 = null;
			if(null==sheet.getRow(0)){
				row0 =sheet.createRow(0);
			}
			else{
				row0 = sheet.getRow(0);
			}
			Cell cell0 = row0.createCell(colIndex);
			cell0.setCellValue(exportTempvo.getColumn_title());
			rownum=1;
			for(int i=exportcountb;i<=endMarker;i++){//行循环
				if(hasids && !checkExits(splitids,voList.get(i-1).getLong(pk).toString())) continue;
				String code = exportTempvo.getExport_item_code();
				String value = voList.get(i-1).getString(code);
				
				if(code.endsWith("__code")){
					value = voList.get(i-1).getString(code.replace("__code", ""));
				}
				Row row = null;
				if(null==sheet.getRow(rownum)){
					row =sheet.createRow(rownum);
				}
				else{
					row = sheet.getRow(rownum);
				}
				Cell cell = row.createCell(colIndex);
//				if(InputTypeEnum.DICT_NEW.getCode().equals(input_config_type)){//处理字典无名称
//					String id_name = exportTempvo.getExport_item_code()+"__name";
//					cell.setCellValue((String)voList.get(i-1).get(id_name));
//				}
//				else 
				if(InputTypeEnum.DICT_NEW.getCode().equals(input_config_type)){//处理字典编码	
					cell.setCellValue(dictmap.get(value));
				}
				else{
					if(DataTypeEnum.DATE.getCode().equals(value_type)&&null!=voList.get(i-1).get(code)){
						String strformat = exportTempvo.getDateformat();
						if(("").equals(strformat)||null==strformat){
							if(value_config.equalsIgnoreCase("DATE")){
								value = sfdate.format(voList.get(i-1).get(code));
							}else if(value_config.equalsIgnoreCase("TIME")){
								value = sftime.format(voList.get(i-1).get(code));
							}else{
								//String va = sftime.format(new Time(10,10,10));
								value = sf.format(voList.get(i-1).get(code));	
							}
						}else{
							SimpleDateFormat sf1=new SimpleDateFormat(strformat); 
							try{
								value = sf1.format(voList.get(i-1).get(code));	
							}catch(Exception e){
								//throw new HDException("模板中日期格式化填写有误");
								Message message = new Message("模板中日期格式化填写有误",MessageLevel.ERROR,MessageShowType.POPUP);
								listMessage.add(message);
								returnResult.setMessages(listMessage);
								map.put("stream", null);
								map.put("rm", returnResult);
								return map;
							}
						}
						cell.setCellValue(value);
					}
					else{
						cell.setCellType(1);
						cell.setCellValue(value);
					}
					
				}
				row.setHeightInPoints(15f); 
				rownum++;
			}
            colIndex++;
		}
		// Write the output to ByteArrayOutputStream
		//ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			if(".xlsx".equals(suf) || ".xls".equals(suf)){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				wb.write(outputStream);
				outputStream.flush();
				outputStream.close();
				InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); 
				map.put("stream", inputStream);
				map.put("rm", returnResult);
				return map;
			}
			else if(".txt".equals(suf)){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				StringBuffer write = new StringBuffer();  
				String tab = "	";  
				if(!StringUtils.isEmpty(txt_separator)){
					tab = txt_separator; 
				}
				String enter = "\r\n"; 
				for (int i = 0; i < rownum; i++) {  
					Row row = sheet.getRow(i);
					for(int j = 0; j < colIndex; j++){
						Cell cell = row.getCell(j); 
						if(null!=cell){
							String str = getCellValue(cell)==null?"":getCellValue(cell).toString();
							write.append(str); 
						}		
						if(j==colIndex-1){
							write.append(""); 
						}else{
							write.append(tab); 
						}
					}
					write.append(enter);            
				}
				outputStream.write(write.toString().getBytes("UTF-8"));  
				outputStream.flush();  
				outputStream.close();  
				InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); 
				map.put("stream", inputStream);
				returnResult.setMessages(listMessage);
				map.put("rm", returnResult);
				return map;
		    }
			else{
				//throw new HDException("导出类型有误");
				Message message = new Message("导出类型有误",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);
				map.put("stream", null);
				map.put("rm", returnResult);
				return map;
			}
			
			
			
			
		} catch (IOException e) {
			logger.debug(e.getMessage());
			Message message = new Message(e.getMessage(),MessageLevel.ERROR,MessageShowType.POPUP);
			listMessage.add(message);
			returnResult.setMessages(listMessage);
			map.put("stream", null);
			map.put("rm", returnResult);
			return map;
		}
	
	}

	/**
	 * 获得单元格的值
	 * @param cell
	 * @param cellValue
	 * @return Object
	 * @author liyan
	 * @date 2016年9月19日
	 */
	@SuppressWarnings("deprecation")
	private Object getCellValue(Cell cell){
		Object unchecked_value = null;
		// 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
		// 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			unchecked_value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			// 这里的日期类型会被转换为数字类型，需要判别后区分处理
			if (DateUtil.isCellDateFormatted(cell)) {
				unchecked_value = cell.getDateCellValue();
			} else {
				unchecked_value = cell.getNumericCellValue();
			}
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
	 * 判断数组里是否包含对应字符串
	 * @param splitids
	 * @param string
	 * @return 
	 * @author liyan
	 * @date 2016年9月18日
	 */
	private Boolean checkExits(String[] splitids, String string) {
		for(int i = 0;i< splitids.length;i++){
			if(string.equals(splitids[i])){
				return true;
			}
		}
		return false;
	}


	
	/**
	 * 根据Unicode编码完美的判断中文汉字和符号
	 * @param c
	 * @return 
	 * @author liyan
	 * @date 2016年9月19日
	 */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

}
