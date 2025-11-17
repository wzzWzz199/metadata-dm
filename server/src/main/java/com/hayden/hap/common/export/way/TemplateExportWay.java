package com.hayden.hap.common.export.way;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.export.entity.ExportTemplateItemVO;
import com.hayden.hap.common.export.entity.ExportTemplateVO;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.utils.date.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板 导出
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public abstract class TemplateExportWay extends AbstractExportWay{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TemplateExportWay.class);

	@Override
	public ReturnResult<InputStream> getExportInputStream(
			TemplateVO templateVO, List<? extends AbstractVO> exportVoList,
			FormParamVO formParamVO, String funcnama, Long tenantid,
			String exportids, int exportcountb, int exportcounte) throws HDException {
		return export(exportVoList, templateVO.getExportTemplateVOList(), templateVO.getExportTemplateVO(), tenantid, exportids, exportcountb, exportcounte);
	}
	
	public ReturnResult<InputStream> export(List<? extends AbstractVO> voList,
			List<ExportTemplateItemVO> exportTemplateItemVOs,
			ExportTemplateVO exportTemplateVO, Long tenantid, String exportids,
			int exportcountb, int exportcounte) throws HDException {
		Workbook wb = getWorkbook();
		ReturnResult<InputStream> returnResult = new ReturnResult<InputStream>();
		List<Message> listMessage = new ArrayList<>(); 
		String mainFuncCode = exportTemplateVO.getFunc_code();
		Map<String, Boolean> has_func = new HashMap<>();
		Map<String,FormItemVO> fitem_codeMapper = new ConcurrentHashMap<>();
		int colnum = 0;//判断列数
		for(ExportTemplateItemVO itemvo:exportTemplateItemVOs){
			String func_code = itemvo.getFunc_code();
			if(null != has_func.get(func_code) && has_func.get(func_code)){
				continue;
			}
			has_func.put(func_code, true);
			FuncVO itemFuncVO =  funcService.queryByFunccode(func_code, tenantid);
			List<FormItemPCVO> formItemvolist = formItemPCService.getFormItemsByFormcode(itemFuncVO.getFunc_info(), tenantid);
			//映射表单字段为字段编码为key的map
			for(FormItemVO formItemVO:formItemvolist){
				fitem_codeMapper.put(func_code+"."+formItemVO.getFitem_code().toLowerCase(), formItemVO);
				colnum ++;
			}	
		}
		List<Message> errorList = validColnum(colnum);
		if(errorList.size()>0){
			returnResult.setMessages(errorList);
			return returnResult;
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
			String tableName = funcService.getOperaTableNameOfFunc(mainFuncCode,tenantid);
			TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//操作表信息
			String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
			pk = pkColName;
		}else{
			if(endMarker==0 || endMarker<exportcountb){
				//throw new HDException("所选区间无记录可导出");
				Message message = new Message("所选区间无记录可导出",MessageLevel.ERROR,MessageShowType.POPUP);
				listMessage.add(message);
				returnResult.setMessages(listMessage);
				return returnResult;
			}
		}
		Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap = null;
		if(endMarker>0){//主表有值才会去查对应子表孙表数据，否则不会差
			funcToListMap = setSubForMainVoListExport(exportTemplateItemVOs, mainFuncCode, voList, tenantid);	
		}
		/**
		 * 1. 查询500行去迭代
		 * 2. 
		 */
		Integer colIndex =0;
		int rownum = 1;
		for(ExportTemplateItemVO exportTempvo:exportTemplateItemVOs){
			HashMap<String,String> dictmap = new HashMap<String,String>();
			//隐藏列
			if(0==exportTempvo.getIs_display()) continue;
			if("SY_USER".equals(exportTemplateVO.getFunc_code()) && exportTempvo.getExport_item_code().equals("password")){
				continue;
			}
			String own_func_code = exportTempvo.getFunc_code();//当前功能
			String own_main_func_code = exportTempvo.getMain_func_code();//当前功能对应的主功能
			String code = exportTempvo.getExport_item_code().toLowerCase();
			String input_config_type = "";
			
			String value_type = "";
			String value_config = "";
			if(fitem_codeMapper.containsKey(own_func_code+"."+code)){
				FormItemVO formItemVO = fitem_codeMapper.get(own_func_code+"."+code);
				value_type = formItemVO.getFitem_data_type();
				value_config = formItemVO.getFitem_input_config();
				input_config_type = formItemVO.getFitem_input_type();
				if(InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())){//处理字典编码
					getDictMap(formItemVO, tenantid, dictmap);
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
				
				List<? extends AbstractVO> subVoList = new ArrayList<>();
				
				if(null != own_main_func_code){//非主功能列
					Map<String, String> key = new HashMap<String, String>();
					key.put(own_main_func_code, own_func_code);
					Map<? extends AbstractVO, List<? extends AbstractVO>> mainToSubmap = funcToListMap.get(key);
					if(mainFuncCode.equals(own_main_func_code)){//说明是子功能
						//遍历写子功能excel
						subVoList = mainToSubmap.get(voList.get(i-1));
						int size = voList.get(i-1).getInt("subVoListSize", 1);
						if(ObjectUtil.isNotEmpty(subVoList)){
							int curRowNum = rownum;
							for(AbstractVO subVo: subVoList){
								try{
									setCell(subVo, code, curRowNum, colIndex, dictmap, exportTempvo, sheet, input_config_type, value_type, value_config);									
								}catch(Exception e){
									Message message = new Message(e.getMessage(),MessageLevel.ERROR,MessageShowType.POPUP);
									listMessage.add(message);
									returnResult.setMessages(listMessage);
									return returnResult;	
								}
								int subSize = subVo.getInt("subVoListSize", 1);
								curRowNum += subSize;
							}
						}
						rownum += size;
						continue;
					}else{//孙功能
						Map<String, String> parentkey = new HashMap<String, String>();
						parentkey.put(mainFuncCode, own_main_func_code);//从主功能得到子功能数据，在得到对应孙功能数据
						Map<? extends AbstractVO, List<? extends AbstractVO>> parentMainToSubmap = funcToListMap.get(parentkey);
						subVoList = parentMainToSubmap.get(voList.get(i-1));
						int size = voList.get(i-1).getInt("subVoListSize", 1);
						//循环子功能列表找到孙功能进行写excel
						if(!ObjectUtil.isEmpty(subVoList)){
							int curRowNum = rownum;
							for(AbstractVO subVo: subVoList){
								List<? extends AbstractVO> grandVoList = new ArrayList<>();
								grandVoList = mainToSubmap.get(subVo);
								if(ObjectUtil.isNotEmpty(grandVoList)){
									for(AbstractVO grandVo: grandVoList){
										try{
											setCell(grandVo, code, curRowNum, colIndex, dictmap, exportTempvo, sheet, input_config_type, value_type, value_config);									
										}catch(Exception e){
											Message message = new Message(e.getMessage(),MessageLevel.ERROR,MessageShowType.POPUP);
											listMessage.add(message);
											returnResult.setMessages(listMessage);
											return returnResult;	
										}
										curRowNum++;
									}
								}
							}	
						}
						rownum += size;
						continue;
					}
				}else{//主功能
					try{
						setCell(voList.get(i-1), code, rownum, colIndex, dictmap, exportTempvo, sheet, input_config_type, value_type, value_config);									
					}catch(Exception e){
						Message message = new Message(e.getMessage(),MessageLevel.ERROR,MessageShowType.POPUP);
						listMessage.add(message);
						returnResult.setMessages(listMessage);
						return returnResult;	
					}
					int mainSize = voList.get(i-1).getInt("subVoListSize", 1);
					rownum += mainSize;
				}
			}
			colIndex++;
		}
		try {
			InputStream inputStream = getInputStream(wb, sheet, rownum, colIndex, exportTemplateVO.getTxt_separator());
			returnResult.setData(inputStream);
			return returnResult;
		} catch (IOException e) {
			logger.debug(e.getMessage());
			Message message = new Message(e.getMessage(),MessageLevel.ERROR,MessageShowType.POPUP);
			listMessage.add(message);
			returnResult.setMessages(listMessage);
			return returnResult;
		}
		
	}

	@SuppressWarnings("deprecation")
	private void setCell(AbstractVO vo, String code, int rownum,
			Integer colIndex, HashMap<String, String> dictmap,
			ExportTemplateItemVO exportTempvo, Sheet sheet,
			String input_config_type, String value_type, String value_config) throws HDException {
		Object value = vo.get(code);
		if(code.endsWith("__code")){
			value = vo.getString(code.replace("__code", ""));
		}
		if(null != value){
			Row row = null;
			if(null==sheet.getRow(rownum)){
				row =sheet.createRow(rownum);
			}
			else{
				row = sheet.getRow(rownum);
			}
			Cell cell = row.createCell(colIndex);
			if(InputTypeEnum.DICT_NEW.getCode().equals(input_config_type)){//处理字典编码	
				if(value.toString().contains(",")){
					String[] values = value.toString().split(",");
					StringBuilder sb = new StringBuilder();
					for(String _value:values){
						sb.append(dictmap.get(_value)).append(",");
					}
					if(sb.length()>0){
						sb.deleteCharAt(sb.length()-1);
					}
					cell.setCellValue(sb.toString());
				}else{
					cell.setCellValue(dictmap.get(value.toString()));
				}
			}
			else{
				if(DataTypeEnum.DATE.getCode().equals(value_type) && value instanceof Date){
					String strformat = exportTempvo.getDateformat();
					if(StringUtils.isEmpty(strformat)){
						if(DataTypeEnum.DATE.getCode().equals(value_type)){
							DateInputConfigVO dateInputConfigVO = null;
							if(!StringUtils.isEmpty(value_config)){
								dateInputConfigVO = JsonUtils.parse(value_config, DateInputConfigVO.class);
							}
							value = DateUtils.getDateStr((Date)value, dateInputConfigVO);
						}
					}else{
						try{
							SimpleDateFormat sf1=new SimpleDateFormat(strformat); 
							value = sf1.format(value);	
						}catch(Exception e){
							throw new HDException("模板中日期格式化"+strformat+"填写有误"+e.getMessage());
						}
					}
					cell.setCellValue(value.toString());
				}
				else{
					cell.setCellType(1);
					cell.setCellValue(value.toString());
				}
			}
			row.setHeightInPoints(15f); 	
		}
	}
	@Override
	public Workbook getExportToWorkBook(Workbook wb, TemplateVO templateVO,
			List<? extends AbstractVO> voList, String funcnama, String mainFuncCode, Long tenantid, int exportcountb,
			int exportcounte,boolean initFirstRow,HashMap<String, String> map) throws HDException {
		
		ExportTemplateVO exportTemplateVO = templateVO.getExportTemplateVO();
		List<ExportTemplateItemVO> exportTemplateItemVOs = templateVO.getExportTemplateVOList();
		Map<String, Boolean> has_func = new HashMap<>();
		Map<String,FormItemVO> fitem_codeMapper = new ConcurrentHashMap<>();
		int colnum = 0;//判断列数
		for(ExportTemplateItemVO itemvo:exportTemplateItemVOs){
			String func_code = itemvo.getFunc_code();
			if(null != has_func.get(func_code) && has_func.get(func_code)){
				continue;
			}
			has_func.put(func_code, true);
			FuncVO itemFuncVO =  funcService.queryByFunccode(func_code, tenantid);
			List<FormItemPCVO> formItemvolist = formItemPCService.getFormItemsByFormcode(itemFuncVO.getFunc_info(), tenantid);
			//映射表单字段为字段编码为key的map
			for(FormItemVO formItemVO:formItemvolist){
				fitem_codeMapper.put(func_code+"."+formItemVO.getFitem_code().toLowerCase(), formItemVO);
				colnum ++;
			}	
		}
		List<Message> errorList = validColnum(colnum);
		if(errorList.size()>0){
			logger.error("错误信息 ",errorList);
			throw new HDException("导出列数不能超过16383");
		}
		String temp_name = exportTemplateVO.getExport_temp_name();
		Sheet sheet = wb.getSheet(temp_name);
		if(sheet==null) {
			sheet = wb.createSheet(temp_name);
		}
		Integer endMarker = exportcounte;
		
		Boolean hasids = false;
		String pk ="";
		String[] splitids = null;
 
		if(endMarker==0 || endMarker<exportcountb){
			throw new HDException("所选区间无记录可导出");
		}
		Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap = null;
		if(endMarker>0){//主表有值才会去查对应子表孙表数据，否则不会差
			funcToListMap = setSubForMainVoListExport(exportTemplateItemVOs, mainFuncCode, voList, tenantid);	
		}
		Integer colIndex =0;
		int rownum = exportcountb-1;
		for(ExportTemplateItemVO exportTempvo:exportTemplateItemVOs){
			HashMap<String,String> dictmap = new HashMap<String,String>();
			//隐藏列
			if(0==exportTempvo.getIs_display()) continue;
			if("SY_USER".equals(exportTemplateVO.getFunc_code()) && exportTempvo.getExport_item_code().equals("password")){
				continue;
			}
			String own_func_code = exportTempvo.getFunc_code();//当前功能
			String own_main_func_code = exportTempvo.getMain_func_code();//当前功能对应的主功能
			String code = exportTempvo.getExport_item_code().toLowerCase();
			String input_config_type = "";
			
			String value_type = "";
			String value_config = "";
			if(fitem_codeMapper.containsKey(own_func_code+"."+code)){
				FormItemVO formItemVO = fitem_codeMapper.get(own_func_code+"."+code);
				value_type = formItemVO.getFitem_data_type();
				value_config = formItemVO.getFitem_input_config();
				input_config_type = exportTempvo.getItem_type();
				if(InputTypeEnum.DICT_NEW.getCode().equals(exportTempvo.getItem_type())){//处理字典编码
					getDictMap(formItemVO, tenantid, dictmap);
				}
				
			}
			if (initFirstRow) {
				Row row0 = null;
				if(null==sheet.getRow(0)){
					row0 =sheet.createRow(0);
				}
				else{
					row0 = sheet.getRow(0);
				}
				Cell cell0 = row0.createCell(colIndex);
				cell0.setCellValue(exportTempvo.getColumn_title());
			}
			rownum = exportcountb;
			int temp_index = 0;
			for(int i=exportcountb;i<endMarker;i++){//行循环
				if(hasids && !checkExits(splitids,voList.get(temp_index).getLong(pk).toString())) continue;
				
				List<? extends AbstractVO> subVoList = new ArrayList<>();
				
				if(null != own_main_func_code){//非主功能列
					Map<String, String> key = new HashMap<String, String>();
					key.put(own_main_func_code, own_func_code);
					Map<? extends AbstractVO, List<? extends AbstractVO>> mainToSubmap = funcToListMap.get(key);
					if(mainFuncCode.equals(own_main_func_code)){//说明是子功能
						//遍历写子功能excel
						subVoList = mainToSubmap.get(voList.get(temp_index));
						int size = voList.get(temp_index).getInt("subVoListSize", 1);
						if(ObjectUtil.isNotEmpty(subVoList)){
							int curRowNum = rownum;
							for(AbstractVO subVo: subVoList){
								try{
									setCell(subVo, code, curRowNum, colIndex, dictmap, exportTempvo, sheet, input_config_type, value_type, value_config);									
								}catch(Exception e){
									logger.error("列处理异常 ",e.getMessage());
									throw new HDException("列处理异常 "+e.getMessage());
								}
								int subSize = subVo.getInt("subVoListSize", 1);
								curRowNum += subSize;
							}
						}
						rownum += size;
						continue;
					}else{//孙功能
						Map<String, String> parentkey = new HashMap<String, String>();
						parentkey.put(mainFuncCode, own_main_func_code);//从主功能得到子功能数据，在得到对应孙功能数据
						Map<? extends AbstractVO, List<? extends AbstractVO>> parentMainToSubmap = funcToListMap.get(parentkey);
						subVoList = parentMainToSubmap.get(voList.get(temp_index));
						int size = voList.get(temp_index).getInt("subVoListSize", 1);
						//循环子功能列表找到孙功能进行写excel
						if(!ObjectUtil.isEmpty(subVoList)){
							int curRowNum = rownum;
							for(AbstractVO subVo: subVoList){
								List<? extends AbstractVO> grandVoList = new ArrayList<>();
								grandVoList = mainToSubmap.get(subVo);
								if(ObjectUtil.isNotEmpty(grandVoList)){
									for(AbstractVO grandVo: grandVoList){
										try{
											setCell(grandVo, code, curRowNum, colIndex, dictmap, exportTempvo, sheet, input_config_type, value_type, value_config);									
										}catch(Exception e){
											logger.error("列处理异常 ",e.getMessage());
											throw new HDException("列处理异常 "+e.getMessage());
										}
										curRowNum++;
									}
								}
							}	
						}
						rownum += size;
						continue;
					}
				}else{//主功能
					try{
						setCell(voList.get(temp_index), code, rownum, colIndex, dictmap, exportTempvo, sheet, input_config_type, value_type, value_config);									
					}catch(Exception e){
						logger.error("列处理异常 ",e.getMessage());
						throw new HDException("列处理异常 "+e.getMessage());
					}
					int mainSize = voList.get(temp_index).getInt("subVoListSize", 1);
					rownum += mainSize;
				}
			  temp_index++;
			}
			colIndex++;
		}
		return wb;
	}
	/**
	 * 判断列数是否超过范围
	 * @param colnum
	 * @return 
	 * @author liyan
	 * @date 2017年6月29日
	 */
	protected abstract List<Message> validColnum(int colnum);

	protected abstract Workbook getWorkbook();

	@Override
	public HttpServletResponse resetHeader(HttpServletRequest request, HttpServletResponse response, String func_code,String funcnama,
										   String suf, Long tenantid,TemplateVO...t) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return super.resetHeader(request, response, t==null?funcnama:t[0].getExportTemplateVO().getExport_temp_name(), suf);
	}
	
	
}
