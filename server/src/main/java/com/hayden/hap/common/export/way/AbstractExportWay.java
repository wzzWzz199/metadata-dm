package com.hayden.hap.common.export.way;

import com.hayden.hap.common.attach.entity.AttachDataVO;
import com.hayden.hap.common.attach.itf.IAttachConstants;
import com.hayden.hap.common.attach.utils.FastDfsUtils;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.DataTypeEnum;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.export.entity.ExportTemplateItemVO;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.export.itf.IExportWay;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IQueryChildrenService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.DictUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.date.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 抽象的导出方式
 * 
 * @author liyan
 * @date 2017年6月28日
 */

public abstract class AbstractExportWay implements IExportWay{

	
//	protected String exportWay;
//	protected String fileType;
//	
//	protected AbstractExportWay(String exportWay, String fileType) {
//		this.exportWay = exportWay;
//		this.fileType = fileType;
//	}
	protected IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
	protected IQueryChildrenService queryChildrenService = AppServiceHelper.findBean(IQueryChildrenService.class);
	 

	public void getDictMap(FormItemVO formItemVO, Long tenantid, HashMap<String, String> dictmap) throws HDException {
		String str = formItemVO.getFitem_input_config();
		DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(str);
		String dictCode = dictInputConfigVO.getDictcode();
		List<DictDataWarperVO> dictDataVoList = DictUtils.getDictData(dictCode);
		List<DictDataWarperVO> allDataList = getAllDataList(dictDataVoList);
		Map<String, String> map = dictInputConfigVO.getMap();
		String key = "code";
		if(null != map){
			for(Entry<String, String> entry : map.entrySet()){	
				String value = entry.getValue();
				if(value.equals(formItemVO.getFitem_code())){
					if("id".equals(entry.getKey())){
						key = "dictdataid";
					}	
				}
			}	
		}
		for(DictDataWarperVO dictdatavo: allDataList){
			dictmap.put(dictdatavo.get(key).toString(), dictdatavo.getName());
		}
	}

	private List<DictDataWarperVO> getAllDataList(List<DictDataWarperVO> dictDataVoList) {
		List<DictDataWarperVO> allDataList = new ArrayList<>();
		for(int i=0;i<dictDataVoList.size();i++ ){
			DictDataWarperVO dictDataVo = dictDataVoList.get(i);
			List<DictDataWarperVO> childrenList = dictDataVo.getChildren();
			allDataList.add(dictDataVo);
			if(ObjectUtil.isNotEmpty(childrenList)){
				allDataList.addAll(getAllDataList(childrenList));
			}
		}
		return allDataList;
	}
	
	public HttpServletResponse resetHeader(HttpServletRequest request,
			HttpServletResponse response, String funcnama, String suf,TemplateVO... t) throws UnsupportedEncodingException {

		changeRes(response);

		String fileNameTemp = funcnama+suf;
		String agent = request.getHeader("user-agent");
		if(agent.contains("Firefox")) 
			fileNameTemp = new String(fileNameTemp.getBytes("UTF-8"), "ISO8859-1");
		else{
			//fileNameTemp = EncoderUtils.encodeUrl(fileNameTemp);
		    fileNameTemp = URLEncoder.encode(fileNameTemp, "UTF-8").replaceAll("\\+", "%20")
				.replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";")
				.replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&");
		}
		response.setHeader("Content-Disposition", "attachment; filename=" +fileNameTemp);
		return response;
	}

	public HttpServletResponse resetHeader(HttpServletRequest request,
										   HttpServletResponse response, String func_code,  String funcnama, String suf, Long tenantid,TemplateVO... t) throws UnsupportedEncodingException {
		return resetHeader(request, response, funcnama, suf, t);
	}


	protected abstract HttpServletResponse changeRes(HttpServletResponse response);
	
	/**
	 * 判断数组里是否包含对应字符串
	 * @param splitids
	 * @param string
	 * @return 
	 * @author liyan
	 * @date 2016年9月18日
	 */
	protected Boolean checkExits(String[] splitids, String string) {
		for(int i = 0;i< splitids.length;i++){
			if(string.equals(splitids[i])){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将wb转换为InputStream
	 * @param wb
	 * @param sheet
	 * @param rownum
	 * @param colIndex
	 * @param txt_separator
	 * @return
	 * @throws IOException 
	 * @author liyan
	 * @date 2017年7月3日
	 */
	protected abstract InputStream getInputStream(Workbook wb, Sheet sheet, int rownum, Integer colIndex, String txt_separator)  throws IOException;
	
	/**
	 * 得到子表volist
	 * @param voList 主表volist
	 * @param funcLinkVO 主子关联关系
	 * @param tenantid  租户id
	 * @return 
	 * @author liyan
	 * @throws HDException 
	 * @date 2017年7月14日
	 */
//	protected List<? extends AbstractVO> getSubListVos(List<? extends AbstractVO> voList,
//			FuncLinkVO funcLinkVO, Long tenantid) throws HDException {
//		List<? extends AbstractVO> subVoList = new ArrayList<>();
//		// 只有启用才去找，否则相当于没有关联表
//		if (ObjectUtil.isNotEmpty(voList) && null != funcLinkVO && ObjectUtil.isTrue(funcLinkVO.getLink_is_enable())) {
//			Boolean isAllEquals = isAllEquals(funcLinkVO);
//			LinkQuery linkQuery = new LinkQuery(null);
//			/**使用策略模式 得到关联查询where子句，如果都是=查询方式，则一次性查询数据库，
//			/**然后组装主子映射，否则一个一个查询数据库，不用组装主子映射
//			*/
//			if(isAllEquals){
//				linkQuery.setLinkQueryStrategy(new AllEqualsStrategy());
//			}else{
//				linkQuery.setLinkQueryStrategy(new NotAllEqualsStrategy());
//			}
//			subVoList = linkQuery.getSubVoList(voList, funcLinkVO, tenantid);
//		}
//		return subVoList;
//	}
	
	/**
	 * 根据主表volist得到对应子表volist,直接修改主表volist
	 * @param voList 主表volist
	 * @param funcLinkVO 主子关联关系
	 * @param tenantid  租户id
	 * @return 
	 * @author liyan
	 * @throws HDException 
	 * @date 2017年7月14日
	 */
//	protected void getSubForMainVos(List<? extends AbstractVO> voList,
//			FuncLinkVO funcLinkVO, Long tenantid) throws HDException {
//
//		// 只有启用才去找，否则相当于没有关联表
//		if (ObjectUtil.isNotEmpty(voList) && null != funcLinkVO && ObjectUtil.isTrue(funcLinkVO.getLink_is_enable())) {
//			Boolean isAllEquals = isAllEquals(funcLinkVO);
//			LinkQuery linkQuery = new LinkQuery(null);
//			/**使用策略模式 得到关联查询where子句，如果都是=查询方式，则一次性查询数据库，
//			/**然后组装主子映射，否则一个一个查询数据库，不用组装主子映射
//			*/
//			if(isAllEquals){
//				linkQuery.setLinkQueryStrategy(new AllEqualsStrategy());
//			}else{
//				linkQuery.setLinkQueryStrategy(new NotAllEqualsStrategy());
//			}
//			linkQuery.getParentAndSubVoList(voList, funcLinkVO, tenantid);
//		}
//	}

	protected Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> setSubForMainVoList(List<ExcelTemplateItemVO> excelTemplateItemVOs, String mainFuncCode, List<? extends AbstractVO> voList, Long tenantid) throws HDException {
		List<String> funcList = new ArrayList<>();
		//key为主功能编码，子功能编码map，value为主功能vo，子功能volist的map
		Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap = new HashMap<>();
		for(ExcelTemplateItemVO itemvo: excelTemplateItemVOs){
			String funcCode = itemvo.getFunc_code();
			String main_func_code = itemvo.getMain_func_code();
			//if(mainFuncCode.equals(funcCode)) continue;//当前主功能不加入
			funcList.add(funcCode);//重复也没关系
			if(!StringUtils.isEmpty(main_func_code)) {
				funcList.add(main_func_code);
			}
		}
		getfuncToListMap(mainFuncCode, tenantid, funcList, voList, funcToListMap);
		return funcToListMap;	
	}

	protected Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> setSubForMainVoListExport(List<ExportTemplateItemVO> exportTemplateItemVOs, String mainFuncCode, List<? extends AbstractVO> voList, Long tenantid) throws HDException {
		List<String> funcList = new ArrayList<>();
		//key为主功能编码，子功能编码map，value为主功能vo，子功能volist的map
		Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap = new HashMap<>();
		for(ExportTemplateItemVO itemvo: exportTemplateItemVOs){
			String funcCode = itemvo.getFunc_code();
			String main_func_code = itemvo.getMain_func_code();
			//if(mainFuncCode.equals(funcCode)) continue;//当前主功能不加入
			funcList.add(funcCode);//重复也没关系
			if(!StringUtils.isEmpty(main_func_code)) {
				funcList.add(main_func_code);
			}
		}
		getfuncToListMap(mainFuncCode, tenantid, funcList, voList, funcToListMap);
		return funcToListMap;	
	}

	/**
	 * 得到主子功能vo对应关系列表
	 * @param mainFuncCode
	 * @param tenantid
	 * @param funcList
	 * @param voList
	 * @param funcToListMap
	 * @throws HDException 
	 * @author liyan
	 * @date 2018年5月10日
	 */
	private void getfuncToListMap(String mainFuncCode, Long tenantid, List<String> funcList, List<? extends AbstractVO> voList, Map<Map<String, String>, Map<? extends AbstractVO, List<? extends AbstractVO>>> funcToListMap) throws HDException {
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
						getfuncToListMap(sub_func_code, tenantid, funcList, allChildren, funcToListMap);
					}
					Map<? extends AbstractVO, List<? extends AbstractVO>> parent2ChildrenMap = queryChildrenService.matchChildren(voList, allChildren, funcLinkVO);
					for(AbstractVO vo: voList){//在主表数据中记录对应子表数据数量
						List<? extends AbstractVO> curSubVoList = parent2ChildrenMap.get(vo);
						int size = vo.getInt("subVoListSize", 0);
						if(null != curSubVoList){
							if(size < curSubVoList.size()){
								vo.setInt("subVoListSize", curSubVoList.size());
							}
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
	 * 原有逻辑采用循环列行的方式进行处理，异步导出分批处理时，会重复读写第0行数据，更换使用SXSSFWorkbook方式，导致第0行被写入磁盘不可读
	 * 在不修改原有逻辑的情况下增加新参数isSplit，isSplit==fasle时为同步导出逻辑，isSplit==true时同时判断
	 * @param wb
	 * @param templateVO
	 * @param voList
	 * @param funcnama
	 * @param func_code
	 * @param tenantid
	 * @param exportcountb
	 * @param exportcounte
	 * @param isBatch
	 * @return
	 * @throws HDException
	 */
	@Override
	public Workbook getExportToWorkBook(Workbook wb, TemplateVO templateVO,
			List<? extends AbstractVO> voList, String funcnama, String func_code, Long tenantid, int exportcountb,
			int exportcounte,boolean initFirstRow,HashMap<String, String> map) throws HDException {
		Sheet sheet = wb.getSheet(funcnama);
		if(sheet==null) {
			sheet = wb.createSheet(funcnama);
		}
		//隐藏sheet页 标识功能名
		Sheet sheet_func_code = wb.getSheet(func_code);
		if(sheet_func_code==null) {
			sheet_func_code = wb.createSheet(func_code);
			wb.setSheetOrder(sheet_func_code.getSheetName(), wb.getSheetIndex(sheet)+1);
			wb.setSheetHidden(wb.getSheetIndex(sheet_func_code), Workbook.SHEET_STATE_VERY_HIDDEN);
		}
		int rownum = exportcountb;
		int colIndex = 0;
		List<FormItemPCVO> formItemvolist = formItemPCService.getGridItems(func_code, tenantid);
		Integer endMarker = voList.size();
		Boolean hasids = false;
		String pk ="";
		String[] splitids = null;
 		
		if(endMarker==0){
			try {
				wb.close();
			} catch (IOException e) {
				throw new HDException("关闭工作簿时异常");
			}
			throw new HDException("所选区间无记录可导出!");
		}
		Iterator<FormItemPCVO> it = formItemvolist.iterator();
		while (it.hasNext()){
			FormItemPCVO formItemVO = it.next();
			HashMap<String,String> dictmap = new HashMap<String,String>();
			String value_type = formItemVO.getFitem_data_type();
			String value_config = ((AbstractVO)formItemVO).getString("fitem_input_config", "");
			//不在列表中显示
			if(0==formItemVO.getFitem_show_list()) continue;
			if("SY_USER".equals(func_code) && formItemVO.getFitem_code().equals("password")){
				continue;
			}
			if(InputTypeEnum.DICT_NEW.getCode().equals(formItemVO.getFitem_input_type())){//处理字典编码
				getDictMap(formItemVO, tenantid, dictmap);
			}
			Row row0 = null;
			if(exportcountb==1) {
				if (null == sheet.getRow(0)) {
					row0 = sheet.createRow(0);
				} else {
					row0 = sheet.getRow(0);
				}
				Cell cell0 = row0.createCell(colIndex);
				cell0.setCellValue(formItemVO.getFitem_name());
			}
			// 下一列 初始行
			rownum = exportcountb;
			//集合数据行
			int temp_index = 0;
			for(int i=(exportcountb-1);i<exportcounte;i++){//行循环
				Object value = voList.get(temp_index).get(formItemVO.getFitem_code());
				if(null != value){
					if(DataTypeEnum.DATE.getCode().equals(value_type)){
						DateInputConfigVO dateInputConfigVO = null;
						if(!StringUtils.isEmpty(value_config)){
							dateInputConfigVO = JsonUtils.parse(value_config, DateInputConfigVO.class);
						}
						value = DateUtils.getDateStr((Date)value, dateInputConfigVO);
					}
					else{
						value = value.toString();
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
						cell.setCellValue(dictmap.get(value.toString()));
					}
					else{
						cell.setCellType(1);
						cell.setCellValue(value.toString());
					}		
					row.setHeightInPoints(15f); 
				}
				rownum++;
				temp_index++;
			}
            colIndex++;
		}
		return wb;
	}
//	@SuppressWarnings("unchecked")
//	private void mapSubAndGrand(String subFuncCode, String grandFuncCode, List<? extends AbstractVO> voList,
//			List<? extends AbstractVO> subMainList) {
//		String subTableName = funcService.getOperaTableNameOfFunc(subFuncCode);
//		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(subTableName);//操作表信息
//		String pkColName = tableDefVO.getPkColumnVO()!=null?tableDefVO.getPkColumnVO().getColcode():null;
//		
//		for(AbstractVO parentVo: voList){//遍历每一条主表数据
//			if(null != parentVo.get(subFuncCode+"subVoList")){
//				List<? extends AbstractVO> oneSubVoList = (List<? extends AbstractVO>) parentVo.get(subFuncCode+"subVoList");
//				int maxSize = 0;
//				for(AbstractVO subVo: oneSubVoList){//遍历单条主表数据对应的子表数据
//					for(AbstractVO subAndGrandVo: subMainList){//遍历每条子孙表对应list，根据id进行匹配
//						if(subVo.get(pkColName).equals(subAndGrandVo.get(pkColName))){
//							subVo.set(grandFuncCode+"subVoList", subAndGrandVo.get(grandFuncCode+"subVoList"));
//							subVo.set("subVoListSize", subAndGrandVo.get("subVoListSize"));
//							maxSize += Integer.parseInt(subAndGrandVo.get("subVoListSize").toString());
//							subMainList.remove(subAndGrandVo);
//							break;
//						}		
//					}
//				}
//				if(Integer.parseInt(parentVo.get("subVoListSize").toString())<maxSize){
//					parentVo.set("subVoListSize", maxSize);
//				}
//			}	
//		}
//	}

	/**
	 * 关联字段的条件是否都是 等于
	 * @param funcLinkVO
	 * @return 
	 * @author liyan
	 * @date 2017年7月14日
	 */
//	private Boolean isAllEquals(FuncLinkVO funcLinkVO) {
//		Boolean isAllEquals = true;
//		for(FuncLinkItemVO linkItemVO : funcLinkVO.getLinkItems()) {				
//			if(ObjectUtil.isTrue(linkItemVO.getLitem_iswhere())) {
//				if(!"eq".equals(linkItemVO.getLitem_query_sign())) {
//					isAllEquals = false;
//					break;
//				}
//			}	
//		}
//		return isAllEquals;
//	}

//	protected List<? extends AbstractVO> createMainSubVoListMap(String mainFuncCode,
//			List<? extends AbstractVO> voList,
//			Map<String, List<? extends AbstractVO>> funcCodeMap, Long tenantid) {
//		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink(mainFuncCode, tenantid);
//		for(FuncLinkVO funcLinkvo: funcLinkVOs){//先遍历主功能对应子功能，然后递归处理子功能
//			String subFunCode = funcLinkvo.getSub_func_code();
//			List<? extends AbstractVO> subList = funcCodeMap.get(subFunCode);
//			if(ObjectUtil.isNotEmpty(subList)){//如果子表有值，先去找子表对应的孙表值
//				subList = createMainSubVoListMap(subFunCode, subList, funcCodeMap, tenantid);		
//				for(AbstractVO vo: voList){//循环主表，找到子表对应值
//					
//				}
//			}
//		}
//		return voList;
//	}
	//if(funcCodeMap.size()>0){//有关联功能的数据，则查询
//	for(String currentFunc: funcList){//遍历所有子孙功能，获取对应数据
//		if(null != funcCodeMap.get(currentFunc)) continue;//说明此功能已经查询过
//		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink(mainFuncCode, currentFunc, tenantid);
//		if(funcLinkVOs.size()>0){
//			getSubForMainVos(voList, funcLinkVOs.get(0), tenantid);
//			funcCodeMap.put(currentFunc, true);
//		}else{//说明此功能为孙功能
//			String subMainFuncCode = funcLinkService.getMainFuncCode(currentFunc, tenantid);
//			if(null != subMainFuncCode){//如果为null则说明此功能与当前功能无关，忽略他，不为null则必为当前功能的子功能
//				List<FuncLinkVO> subMainFuncLinkVOs = funcLinkService.getFuncLink(mainFuncCode, subMainFuncCode, tenantid);
//				if(subMainFuncLinkVOs.size()>0){
//					List<? extends AbstractVO> subMainList = getSubListVos(voList, subMainFuncLinkVOs.get(0), tenantid);
//					getSubForMainVos(voList, funcLinkVOs.get(0), tenantid);
//					funcCodeMap.put(subMainFuncCode, true);
//					List<FuncLinkVO> subFuncLinkVOs = funcLinkService.getFuncLink(subMainFuncCode, currentFunc, tenantid);
//					if(subFuncLinkVOs.size()>0){
//						getSubForMainVos(subMainList, subFuncLinkVOs.get(0), tenantid);
//						mapSubAndGrand(subMainFuncCode, currentFunc, voList, subMainList);//主表与子表已经有对应关系，子表与孙表也有对应关系，根据这俩个得到主子孙三表对应
//						funcCodeMap.put(currentFunc, true);	
//					}
//				}
//			}
//		}
//	}	
	public void handleImage(FormItemPCVO formItemVO, AbstractVO abstractVO, Workbook wb, XSSFDrawing patriarch,
			int colIndex, int rownum, String func_code, String pk) {
		 try {
			 	long pkvalue = abstractVO.getLong(pk);
			 	String table = abstractVO.getString("tableName");
			 	String colcode = formItemVO.getFitem_code();
			 	if (ElementTypeEnum.FAST_IMG.getCode().equals(formItemVO.getFitem_input_element())||ElementTypeEnum.FAST_FILE.getCode().equals(formItemVO.getFitem_input_element())) {
			 		AbstractVO sourceVOfromDB = baseService.queryByPK(table, pkvalue);
			 		String[] fastdfsKeys = sourceVOfromDB.getString(colcode).split(IAttachConstants.COMMA);
			    	int x = 1024000/fastdfsKeys.length;
			 		for (int i = 0; i < fastdfsKeys.length; i++) {
			 			InputStream inStream = FastDfsUtils.download_file(fastdfsKeys[i]);  
			 				 //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
			            byte[] data = readInputStream(inStream);  
			            XSSFClientAnchor anchor = new XSSFClientAnchor(x*i, 0, x*(i+1), 1024000,colIndex, rownum,colIndex, rownum);
			            //Sets the anchor type （图片在单元格的位置）
			            //0 = Move and size with Cells, 2 = Move but don't size with cells, 3 = Don't move or size with cells.
			            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
			            patriarch.createPicture(anchor, wb.addPicture(data, XSSFWorkbook.PICTURE_TYPE_JPEG));
					}
				}else {
					SimpleDateFormat sdf = new SimpleDateFormat(SyConstant.DATE_TIME_PATTERN);
					List<AttachDataVO> attachs = (List<AttachDataVO>) abstractVO.get(colcode + "_attachShowList");
					int x = 1024000/attachs.size();
					for (int i = 0; i < attachs.size(); i++) {
						AttachDataVO attachDataVO = attachs.get(i);
						InputStream inStream = attachMethodService.getAttachInputStream(attachDataVO.getAttachdataid(), attachDataVO.getTenantid(), sdf.format(attachDataVO.getRecord_created_dt()));
						//得到图片的二进制数据，以二进制封装得到数据，具有通用性  
			            byte[] data = readInputStream(inStream);  
			            XSSFClientAnchor anchor = new XSSFClientAnchor(x*i, 0, x*(i+1), 1024000,colIndex, rownum,colIndex, rownum);
			            //Sets the anchor type （图片在单元格的位置）
			            //0 = Move and size with Cells, 2 = Move but don't size with cells, 3 = Don't move or size with cells.
			            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
			            patriarch.createPicture(anchor, wb.addPicture(data, XSSFWorkbook.PICTURE_TYPE_JPEG));
					}
				}
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		
	}
	private static byte[] readInputStream(InputStream inStream) throws Exception{  
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	    //创建一个Buffer字符串  
	    byte[] buffer = new byte[1024];  
	    //每次读取的字符串长度，如果为-1，代表全部读取完毕  
	    int len = 0;  
	    //使用一个输入流从buffer里把数据读取出来  
	    while( (len=inStream.read(buffer)) != -1 ){  
	        //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
	        outStream.write(buffer, 0, len);  
	    }  
	    //关闭输入流  
	    inStream.close();  
	    //把outStream里的数据写入内存  
	    return outStream.toByteArray();  
	}
	
}
