package com.hayden.hap.common.export.way;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.utils.date.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * txt导出
 * 
 * @author liyan
 * @date 2017年6月28日
 */
public class TxtExportWay extends AbstractExportWay{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TxtExportWay.class);

	TxtExport txtExport = new TxtExport();
//	protected TxtExportWay(String exportWay, String fileType) {
//		super(exportWay, fileType);
//	}


	@Override
	protected HttpServletResponse changeRes(HttpServletResponse response) {
		return txtExport.changeRes(response);
	}

	@Override
	public String getSuf() {
		return txtExport.getSuf();
	}

	@Override
	protected InputStream getInputStream(Workbook wb, Sheet sheet, int rownum,
			Integer colIndex, String txt_separator) throws IOException {
		return txtExport.getInputStream(wb, sheet, rownum, colIndex, txt_separator);
	}
	
	@Override
	public ReturnResult<InputStream> getExportInputStream(
			TemplateVO templateVO, List<? extends AbstractVO> exportVoList,
			FormParamVO formParamVO, String funcnama, Long tenantid,
			String exportids, int exportcountb, int exportcounte) throws HDException {
		return export(exportVoList, formParamVO, funcnama, tenantid, exportids, exportcountb, exportcounte);
	}
	
	@SuppressWarnings("deprecation")
	private ReturnResult<InputStream> export(List<? extends AbstractVO> voList,FormParamVO formParamVO, String funcnama, Long tenantid, String exportids, int exportcountb, int exportcounte) throws HDException{
		Workbook wb = new XSSFWorkbook();
		ReturnResult<InputStream> returnResult = new ReturnResult<InputStream>();
		List<Message> listMessage = new ArrayList<>(); 
		Sheet sheet = wb.createSheet(funcnama);
		String func_code = formParamVO.getFuncCode();
		//隐藏sheet页 标识功能名
		Sheet sheet_func_code = wb.createSheet(func_code);
		wb.setSheetOrder(sheet_func_code.getSheetName(), wb.getSheetIndex(sheet)+1);
		wb.setSheetHidden(wb.getSheetIndex(sheet_func_code), Workbook.SHEET_STATE_VERY_HIDDEN);
		int rownum = 1;
		int colIndex = 0;
//		FuncVO funcVO =  funcService.queryByFunccode(formParamVO.getFuncCode(), tenantid);
//		List<FormItemVO> formItemvolist = formItemService.getFormItemsByFormcode(funcVO.getFunc_info(), tenantid);
		List<FormItemPCVO> formItemvolist = formItemPCService.getGridItems(func_code, tenantid);
		Integer endMarker = voList.size();
		Boolean hasids = false;
		String pk ="";
		String[] splitids = null;
		//List<? extends AbstractVO> list = voset.getVoList();
		if(!StringUtils.isEmpty(exportids)){
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
				
				try {
					wb.close();
				} catch (IOException e) {
					Message messageIo = new Message("IO异常"+e.getMessage(),MessageLevel.ERROR,MessageShowType.POPUP);
					listMessage.add(messageIo);
				}
				returnResult.setMessages(listMessage);
				return returnResult;
			}
		}
		Iterator<FormItemPCVO> it = formItemvolist.iterator();
		while (it.hasNext()){
			FormItemPCVO formItemVO = it.next();
			HashMap<String,String> dictmap = new HashMap<String,String>();
			String value_type = formItemVO.getFitem_data_type();
			String value_config = formItemVO.getFitem_input_config();
			//不在列表中显示
			if(0==formItemVO.getFitem_show_list()) continue;
			if("SY_USER".equals(formParamVO.getFuncCode()) && formItemVO.getFitem_code().equals("password")){
				continue;
			}
			if(InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())){//处理字典编码
				getDictMap(formItemVO, tenantid, dictmap);
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
				Object value = voList.get(i-1).get(formItemVO.getFitem_code());
				if(null != value){
					if(DataTypeEnum.DATE.getCode().equals(value_type)){
						DateInputConfigVO dateInputConfigVO = null;
						if(!StringUtils.isEmpty(value_config)){
							dateInputConfigVO = JsonUtils.parse(value_config, DateInputConfigVO.class);
						}
						value = DateUtils.getDateStr((Date)value, dateInputConfigVO);
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
							cell.setCellType(1);
							cell.setCellValue(value.toString());
					}
					row.setHeightInPoints(15f); 
				}
				rownum++;
			}
            colIndex++;
		}
		// Write the output to ByteArrayOutputStream
		try {
			String txt_separator = "	"; 
			InputStream inputStream = getInputStream(wb, sheet, rownum, colIndex, txt_separator);
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
}
