package com.hayden.hap.common.export.service;

import cn.jpush.api.utils.StringUtils;
import com.hayden.hap.common.attach.IFastDfsService;
import com.hayden.hap.common.attach.entity.AttachDfsVO;
import com.hayden.hap.common.authz.func.itf.IFuncAuthzPCService;
import com.hayden.hap.common.button.entity.ButtonPCVO;
import com.hayden.hap.common.button.entity.ButtonVO;
import com.hayden.hap.common.button.itf.IButtonPCService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.enumerate.*;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.excel.itf.IExcelTemplateService;
import com.hayden.hap.common.export.entity.*;
import com.hayden.hap.common.export.itf.IExportService;
import com.hayden.hap.common.export.itf.IExportWay;
import com.hayden.hap.common.export.way.Excel2007Export;
import com.hayden.hap.common.export.way.ExportWayFactory;
import com.hayden.hap.common.export.way.TxtExport;
import com.hayden.hap.common.export.way.Word2007Export;
import com.hayden.hap.common.form.entity.FormItemPCVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.BaseSettingUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import com.hayden.hap.wtask.center.entity.TaskParamVO;
import com.hayden.hap.wtask.center.itf.ITaskCenterService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Service("exportServiceImpl")
public class ExportServiceImpl implements IExportService {
	private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IFuncService funcService;
	@Autowired
	private IFormItemPCService formItemPCService;
	@Autowired
	private ITableDefService tableDefService;
	@Autowired
	private IListFormService listFormService;

	@Autowired
	private IExcelTemplateService excelTemplateService;
	@Autowired
	private IFuncLinkService funcLinkService;

	@Autowired
	private IFuncAuthzPCService funcAuthzPCService;

	@Autowired
	private IButtonPCService buttonPCService;
	@Autowired
	private IFastDfsService fastDfsService;
	@Autowired
	private ITaskCenterService taskCenterService;

	private int colNum = 1;

	@Resource(name="redisTemplate")
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	@Cacheable(value = "SY_EXPORT_TEMPLATE", key = "#funcCode.concat('|').concat(#tenantid)")
	public List<ExportTemplateVO> getExportTempByCode(String funcCode, Long tenantid) throws HDException {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("is_enable", Useable.USEABLE.getCode());
		VOSet<ExportTemplateVO> voSet = baseService.query(ExportTemplateVO.class, dynaSqlVO);
		List<ExportTemplateVO> result = new ArrayList<>();
		if (voSet.getVoList().size() > 0) {
			result = voSet.getVoList();
		}
		return result;
	}

	@Override
//	@Cacheable(value = "SY_EXCEL_TEMPLATE", key = "#funcCode.concat('|').concat(#tenantid)")
	public List<ExcelTemplateVO> getImportTempByCode(String funcCode, Long tenantid) throws HDException {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		VOSet<ExcelTemplateVO> voSet = baseService.query(ExcelTemplateVO.class, dynaSqlVO);
		List<ExcelTemplateVO> result = new ArrayList<>();
		if (voSet.getVoList().size() > 0) {
			result = voSet.getVoList();
		}
		return result;
	}
	/**
	 *
	 * @param handle
	 * @return
	 * @throws HDException
	 */
	public void exportToFastDfs(ExportHandleRecordVO handle) throws HDException {
		try {
			String type = handle.getAttach_type();
			Long templateid = handle.getExport_template_id();
			Long importtemplateid = handle.getImport_template_id();
			String funcode = handle.getFunc_code();
			FormParamVO formParamVO = new FormParamVO();
			String handle_query_param = handle.getHandle_query_param();

			if(org.apache.commons.lang3.StringUtils.isNotBlank(handle.getHandle_query_param())) {
				ReqParamVO param = JsonUtils.parse(handle_query_param, ReqParamVO.class);
				formParamVO.setReqParamVO(param);
			}
			Long tenantid = CurrentEnvUtils.getTenantId();

			TemplateVO templateVO = getTemp(type, templateid, importtemplateid, funcode, tenantid, handle.getDynamic_param());
			ExportWayFactory exportWayFactory = new ExportWayFactory(type, templateVO.getFileType());
			IExportWay exportWay = exportWayFactory.getExportWay();
			String suf = exportWay.getSuf();
			FuncVO funcVO = funcService.queryByFunccode(funcode, tenantid);
			String funcname = funcVO.getFunc_name();

			/**
			 * 1.批量迭代查 500
			 * 2.工厂加一个函数 返回work  sheet 
			 * 3.手动传入sheet
			 * 4.转换为流
			 * 5.上传到fastdfs
			 * 6.发送rabbitMQ通知 
			 *
			 */
			byte[] bytes = this.getBytes(handle, templateVO, exportWay, funcname, tenantid);
			if(bytes==null) {
				logger.error("工作簿转流异常");
				throw new HDException("工作簿转流异常!");
			}
			// 上传fastDfs
			AttachDfsVO attachUploadFastDfs = fastDfsService.attachUploadFastDfs(bytes,  funcname+suf,suf, tenantid);
			if(attachUploadFastDfs==null) {
				throw new HDException("附件上传失败!");
			}
			// 更新数据到业务VO
			this.updateExportVO(handle,ExportStateEnum.COMPLE.getCode(),attachUploadFastDfs.getDfs_file_key(), tenantid);

			//发送代办任务
			try {
				this.sendWtask(funcVO, handle, suf);
			} catch (Exception e) {
				logger.error("导出成功，生成待办异常",e);
			}
			logger.error("【 "+Thread.currentThread().getName()+" 】 "+funcname +" 导出完成！" );
		}catch (Exception e) {
			logger.error("导出异常",e);
			throw new HDException(e.getMessage(),e);
		}
	}
	/**
	 * 发送代办任务
	 * @param funcVO
	 * @param handle
	 * @param suf
	 * @throws HDException
	 */
	private void sendWtask(FuncVO funcVO,ExportHandleRecordVO handle,String suf) throws HDException {

		String func_name = funcVO.getFunc_name();
		String modulecode = funcVO.getModulecode();

		TaskParamVO taskParamVO = new TaskParamVO();
		taskParamVO.setSrcFuncCode("PUB_EXPORT_HANDLE_RECORD");
		taskParamVO.setFuncCode("PUB_EXPORT_HANDLE_RECORD");
		taskParamVO.setDataId(handle.getHandle_id());
		taskParamVO.setModuleCode(modulecode);
		taskParamVO.setTaskTitle("导出数据成功!");
		taskParamVO.setTaskCard(" 功能 [ "+func_name+" ] 导出数据 "+func_name+suf+" 成功!");
		taskParamVO.setTaskCatg("workflow");
		List<String> list = new ArrayList<String>();
		list.add(String.valueOf(handle.getExport_user()));
		taskParamVO.setRecerIds(list);
		taskParamVO.setRecerId(String.valueOf(handle.getExport_user()));
		//发送代办任务
		taskCenterService.saveTaskAndSendPush(taskParamVO);
	}
	/**
	 *
	 * @return
	 * @throws HDException
	 */
	private byte[] getBytes(ExportHandleRecordVO handle, TemplateVO templateVO, IExportWay exportWay, String funcname,
							Long tenantid) throws HDException {
		// 流
		byte[] bytes = null;
		try {
			String type = handle.getAttach_type();
			int exportcountb = handle.getStart_rows();
			int exportcounte = handle.getEnd_rows();
			String funcode = handle.getFunc_code();
			FormParamVO formParamVO = new FormParamVO();
			String handle_query_param = handle.getHandle_query_param();

			if (org.apache.commons.lang3.StringUtils.isNotBlank(handle.getHandle_query_param())) {
				ReqParamVO param = JsonUtils.parse(handle_query_param, ReqParamVO.class);
				formParamVO.setReqParamVO(param);
			}
			//拼装条件
			formParamVO.getReqParamVO().setRows(SyConstant.MAX_NUMS);
			formParamVO.setFuncCode(funcode);
			formParamVO.getReqParamVO().setPage(1);
			// 查询第一页数据，为了得到实际数据总数
			VOSet voSet=listFormService.listQuery(formParamVO);
			int totalRows=voSet.getPage().getTotalRows();
			//如果前端选择导出数据小于实际数据总数则以前端选择为准，否则以实际总数为准
			exportcounte=exportcounte<totalRows?exportcounte:totalRows;

			// 开始
			exportcountb = exportcountb == 0 ? exportcountb : exportcountb - 1;
			// 总数
			int countNums = exportcounte - exportcountb;
			int nums = countNums / SyConstant.MAX_NUMS;
			int remainder = countNums % SyConstant.MAX_NUMS;

			if (formParamVO != null)
				formParamVO.setIsExport("true");


			/**
			 * TODO
			 * 1.需要写到系统文件中
			 * 2.文件名命名添加唯一标识
			 * 3.定量写
			 */
			Workbook workBook = new SXSSFWorkbook(SyConstant.MAX_NUMS+1);
			int temp_end = SyConstant.MAX_NUMS;
			int temp_start = 1;
			//导入模版导出时存储第一次导出时模版初始化的字典项
			HashMap<String, String> dictmap = new HashMap<String, String>();
			for (int i = 1; i <= nums; i++) {

				formParamVO.getReqParamVO().setPage(i);
				// 查询
				voSet=listFormService.listQuery(formParamVO);

				List<? extends AbstractVO> exportVoList = voSet.getVoList();
				if (exportVoList != null && !exportVoList.isEmpty()) {
					// 拼装excel list 越界问题
					int temp_ListSize =  temp_end - (i-1)*SyConstant.MAX_NUMS;
					int size = exportVoList.size();
					if(temp_ListSize>size) {
						temp_end =  (i-1)*SyConstant.MAX_NUMS + size;
					}
					boolean intFirstRow = false;
					if (temp_start==1) {
						intFirstRow = true;
					}
					// 拼装
					exportWay.getExportToWorkBook(workBook, templateVO, exportVoList, funcname, funcode, tenantid,
							temp_start, temp_end,intFirstRow,dictmap);
					// 更新进度
					this.updateProgress(handle, countNums, temp_end);
				}
				temp_start = temp_end;
				temp_start++;
				temp_end += SyConstant.MAX_NUMS;

			}
			// 余数
			if(remainder>0) {

				formParamVO.getReqParamVO().setRows(SyConstant.MAX_NUMS);
				formParamVO.getReqParamVO().setPage(nums+1);
				List<? extends AbstractVO> exportVoList = listFormService.listQuery(formParamVO).getVoList();
				if (exportVoList != null && !exportVoList.isEmpty()) {
					// 拼装excel list 越界问题
					int size = exportVoList.size();
					temp_end = nums * SyConstant.MAX_NUMS + remainder ;
					if(remainder>size) {
						temp_end =  nums*SyConstant.MAX_NUMS + size;
					}
					boolean intFirstRow = false;
					if (nums<=0) {
						intFirstRow = true;
					}
					// 拼装
					exportWay.getExportToWorkBook(workBook, templateVO, exportVoList, funcname, funcode, tenantid, nums * SyConstant.MAX_NUMS+1,
							temp_end,intFirstRow,dictmap);

					// 更新进度
					this.updateProgress(handle, countNums, temp_end);
				}
			}

			logger.info(" 进度完成 线程 --->  "+ Thread.currentThread().getName()+ "  更新进度 " +handle.getProgress() + "  temp_end： " +temp_end+" countNums: "+countNums);
			// txt 用
			Sheet sheet = workBook.getSheet(funcname);

			bytes = getInputStreamByWorkBook(workBook, sheet, type);
		} catch (IOException e) {
			logger.error("工作簿转流异常", e.getMessage());
			throw new HDException( e.getMessage(), e.getMessage());
		}
		return bytes;
	}
	/**
	 *
	 * 更新导出进度
	 */
	private void updateProgress(ExportHandleRecordVO handle,int countNums,int temp_end) {
		BigDecimal percent= new BigDecimal(100);
		BigDecimal count= new BigDecimal(countNums);
		BigDecimal temp= new BigDecimal(temp_end);

		BigDecimal divide = temp.divide(count,4, BigDecimal.ROUND_HALF_UP).multiply(percent);

		Double df = divide.doubleValue();
		handle.setProgress(df);
//		DynaSqlVO sql = new DynaSqlVO();
//		List<String> cols = new ArrayList<>();
//		cols.add("progress");
//		sql.setSqlColumnList(cols);
		redisTemplate.opsForHash().put("updateProgress",handle.getHandle_id(),handle);
		//baseService.update(handle,sql);
	}
	/**
	 * 更新开始/完成
	 * @param handle
	 * @param dfsKey
	 * @param tenantid
	 */
	private void updateExportVO(ExportHandleRecordVO handle,String state,String dfsKey,Long tenantid) {

		ArrayList<String> procStateColumnList = new ArrayList<>();
		DynaSqlVO procState = new DynaSqlVO();
		if(ExportStateEnum.PROCE.getCode().equals(state)) {
			// 执行中
			procStateColumnList.add("state");
			procStateColumnList.add("start_time");
			handle.setStart_time(new Date());
			handle.setState(ExportStateEnum.PROCE.getCode());

		}else if(ExportStateEnum.COMPLE.getCode().equals(state)) {
			ExportHandleRecordVO temphandle= (ExportHandleRecordVO) redisTemplate.opsForHash().get("updateProgress", handle.getHandle_id());
			double progress=temphandle.getProgress();
			// 完成
			handle.setState(ExportStateEnum.COMPLE.getCode());
			handle.setAttach(dfsKey);
			handle.setResult("导出成功!");
			handle.setEnd_time(new Date());
			handle.setProgress(progress);
			procStateColumnList.add("state");
			procStateColumnList.add("result");
			procStateColumnList.add("attach");
			procStateColumnList.add("end_time");
			procStateColumnList.add("progress");
			redisTemplate.opsForHash().delete("updateProgress",handle.getHandle_id());

		}
		procState.addWhereParam(SyConstant.TENANT_STR, tenantid);
		procState.setSqlColumnList(procStateColumnList);
		baseService.update(handle,procState);
	}
	/**
	 * 获取工作簿
	 * @return
	 */
	private Workbook getWorkBook (String type) {
		if(org.apache.commons.lang3.StringUtils.isEmpty(type) || !ExportWayType.TXT.getCode().equals(type)) {
			return new Excel2007Export().getWorkbook();
		}
		return new XSSFWorkbook();

	}
	/**
	 * 根据工作簿获取工作流
	 * @return
	 * @throws IOException
	 */
	private byte[] getInputStreamByWorkBook(Workbook workBook,Sheet sheet,String type) throws IOException {
		if(org.apache.commons.lang3.StringUtils.isEmpty(type) || !ExportWayType.TXT.getCode().equals(type)) {
			ByteArrayOutputStream stream = new Excel2007Export().getStream(workBook);
			return stream.toByteArray();
		}
		int cols=sheet.getRow(0).getPhysicalNumberOfCells();
		int rows=sheet.getPhysicalNumberOfRows();//获得总行数
		String txt_separator = "	";
		ByteArrayOutputStream stream = new TxtExport().getStream(workBook, sheet, rows, cols, txt_separator);
		return stream.toByteArray();
	}
	@Override
	public ReturnResult<?> export(HttpServletRequest request, HttpServletResponse response, FormParamVO formParamVO,
								  String type, Long templateid, Long importtemplateid, String exportids, int exportcountb, int exportcounte) {
		Long tenantid = CurrentEnvUtils.getTenantId();
		String funcode = formParamVO.getFuncCode();
		ReturnResult<?> returnResult = new ReturnResult<>();
		List<Message> listMessage = new ArrayList<>();
		try {
			TemplateVO templateVO = getTemp(type, templateid, importtemplateid, funcode, tenantid, formParamVO.getDataBody());
			ExportWayFactory exportWayFactory = new ExportWayFactory(type, templateVO.getFileType());
			IExportWay exportWay = exportWayFactory.getExportWay();
			String suf = exportWay.getSuf();
			FuncVO funcVO = funcService.queryByFunccode(funcode, tenantid);
			String funcnama = funcVO.getFunc_name();
			exportWay.resetHeader(request, response, funcode,funcnama, suf, tenantid, templateVO);
			List<? extends AbstractVO> exportVoList = new ArrayList<>();
			if (StringUtils.isEmpty(formParamVO.getExtWhere())) {
				if(formParamVO!=null)
					formParamVO.setIsExport("true");
				exportVoList = listFormService.listQuery(formParamVO).getVoList();
			}
			ReturnResult<InputStream> rr = exportWay.getExportInputStream(templateVO, exportVoList, formParamVO,
					funcnama, tenantid, exportids, exportcountb, exportcounte);
			if (null != rr && rr.getStatus() == Status.FAIL) {
				return rr;
			}
			if (null == rr) {
				throw new HDException("导出对象为空");
			}
			InputStream in = rr.getData();
			// 响应输出流
			OutputStream out = response.getOutputStream();
			// 创建缓冲区
			byte[] buffer = new byte[10240];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.flush();
			out.close();
		} catch (Exception e1) {
			logger.error(e1.getMessage(),e1);
			Message message = new Message("导出出错" + e1.getMessage(), MessageLevel.ERROR, MessageShowType.POPUP);
			listMessage.add(message);
			returnResult.setMessages(listMessage);
			return returnResult;
		}
		return returnResult;
	}
	/**
	 * 如果为导入模板 导出 或 其他模板 导出，获取对应的模板及模板明细
	 *
	 * @param type
	 * @param templateid
	 * @param tenantid
	 * @return
	 * @author liyan
	 * @param funcode
	 * @throws HDException
	 * @date 2017年6月28日
	 */
	private TemplateVO getTemp(String type, Long templateid, Long importtemplateid, String funcode, Long tenantid, String dynamicParam) throws HDException {
		TemplateVO templateVO = new TemplateVO();
		if (ExportWayType.TEMPLATE.getCode().equals(type) && null != templateid) {// 其他模板 导出
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam("exportid", templateid);
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
			ExportTemplateVO exportTemplateVO = baseService.query(ExportTemplateVO.class, dynaSqlVO).getVO(0);
			if (ObjectUtil.isNotNull(exportTemplateVO)) {
				dynaSqlVO = new DynaSqlVO();
				dynaSqlVO.addWhereParam("exportid", exportTemplateVO.getExportid());
				dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
				dynaSqlVO.setOrderByClause("column_no asc");
				List<ExportTemplateItemVO> exportTemplateItemVOs = baseService
						.query(ExportTemplateItemVO.class, dynaSqlVO).getVoList();
				templateVO.setExportTemplateVO(exportTemplateVO);
				templateVO.setExportTemplateVOList(exportTemplateItemVOs);
				templateVO.setFileType(exportTemplateVO.getFile_type());
			}
		} else if (ExportWayType.IMPORT.getCode().equals(type) && null != importtemplateid) {// 导入模板导出
			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam("templateid", importtemplateid);
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
			ExcelTemplateVO excelTemplateVO = baseService.query(ExcelTemplateVO.class, dynaSqlVO).getVO(0);
			dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereParam("templateid", excelTemplateVO.getTemplateid());
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
			dynaSqlVO.setOrderByClause("column_no asc");
			VOSet<ExcelTemplateItemVO> itemVoSet = baseService.query(ExcelTemplateItemVO.class, dynaSqlVO);
			templateVO.setExcelTemplateVO(excelTemplateVO);
			templateVO.setExcelTemplateVOList(itemVoSet.getVoList());
			templateVO.setFileType(excelTemplateVO.getFile_type());

		} else if (ExportWayType.FORM.getCode().equals(type)){
			templateVO.setFileType(ExportTypeEnum.EXCEL2007.getName());
			templateVO.setDynamicParam(dynamicParam);
		}
		else if (ExportWayType.IMPORT.getCode().equals(type)) { // 兼容旧版本，即不传导入模板的ID时
			templateVO = excelTemplateService.getImportTempByCode(funcode, tenantid);
		}
		return templateVO;
	}

	/**
	 * 重置模板列
	 *
	 * @see com.hayden.hap.common.export.itf.IExportService#reset(java.lang.Long)
	 * @author liyan
	 * @throws HDException
	 * @date 2016年10月8日
	 */
	@Override
	public void reset(Long exportid) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("exportid", exportid);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		List<ExportTemplateItemVO> itemlist = baseService.query(ExportTemplateItemVO.class, dynaSqlVO).getVoList();
		ExportTemplateVO et = baseService.query(ExportTemplateVO.class, dynaSqlVO).getVoList().get(0);
		if (itemlist.size() > 0) {
			baseService.deleteBatch(itemlist);
		}
		String func_code = et.getFunc_code();
		Integer product_flag = et.getProduct_flag();
		List<ExportTemplateItemVO> etivos = creatItemVo(tenantid, null, func_code, exportid, product_flag, "");
		if (ObjectUtil.isTrue(et.getIs_export_linklist())) {// 导入关联列表列
			List<ExportTemplateItemVO> subEtivos = getSubList(func_code, tenantid, exportid, product_flag);
			etivos.addAll(subEtivos);
		}
		baseService.insertBatch(etivos);
	}

	/**
	 * 得到用户拥有哪些功能的编码集合
	 *
	 * @param userid
	 * @param tenantid
	 * @param is_sy
	 *            是否只取系统模块下的功能
	 * @return
	 * @author liyan
	 * @date 2016年11月7日
	 */
	@Override
	public List<String> getFuncodeByUserid(Long userid, Long tenantid, Boolean is_sy) {
		List<String> result = new ArrayList<String>();
		List<? extends FuncVO> funclist = funcAuthzPCService.getFuncListByUserId(userid, tenantid);
		if (funclist.size() > 0) {
			for (FuncVO vo : funclist) {
				result.add(vo.getFunc_code());
			}
		}
		return result;
	}

	/**
	 * 找出有导出按钮的功能
	 *
	 * @see com.hayden.hap.common.export.itf.IExportService#getHasButtonFunc(java.util.List,
	 *      java.lang.Long)
	 * @author liyan
	 * @date 2016年11月7日
	 */
	@Override
	public List<String> getHasButtonFunc(List<String> userHasList, Long tenantid) {
		// Long tenantid = CurrentEnvUtils.getTenantId();
		List<String> result = new ArrayList<String>();
		if (userHasList.size() > 0) {
			for (String func_code : userHasList) {
				FuncVO funcvo = funcService.queryByFunccode(func_code, tenantid);
				String form_code = funcvo.getFunc_info();
				if(StringUtils.isEmpty(form_code))
					continue;
				List<? extends ButtonVO> buttonlist = buttonPCService.getBtnsByFormcode(form_code, tenantid);
				if (null != buttonlist && buttonlist.size() > 0) {
					for (ButtonVO vo : buttonlist) {
						if ("exportToFile".equals(vo.getBtn_code())) {
							result.add(func_code);
							break;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 判断导出合法性
	 *
	 * @see com.hayden.hap.common.export.itf.IExportService#checkNum(java.lang.String,
	 *      int, int, java.lang.Long, java.lang.String, java.lang.Long)
	 * @author liyan
	 * @date 2016年11月7日
	 */
	@Override
	public ReturnResult<Integer> checkNum(String func_code, int exportcounte, int exportcountb, Long templateid,
										  String type, Long tenantid) throws HDException {
		ReturnResult<Integer> returnResult = new ReturnResult<>();
		List<Message> msgList = new ArrayList<>();
		int limit = 0;
		Boolean is_legal = false;

		if (type.equals(ExportWayType.TXT.getCode())) {
			limit = -1;
			returnResult.setData(limit);
		} else if (type.equals(ExportWayType.EXCEL.getCode())) {
			limit = 1048576;
			returnResult.setData(limit);
		} else if (null != templateid && type.equals(ExportWayType.TEMPLATE.getCode())) {// 按模板导出,根据模板版本号判断
			// 判断模板合法性
			IExportService service = AppServiceHelper.findBean(IExportService.class);
			List<ExportTemplateVO> list = service.getExportTempByCode(func_code, tenantid);
			for (ExportTemplateVO vo : list) {
				if (templateid.equals(vo.getExportid())) {
					is_legal = true;
					if (vo.getFile_type().equals(ExportTypeEnum.EXCEL2003.getName())) {
						limit = 65534;
						returnResult.setData(limit);
					}
					if (vo.getFile_type().equals(ExportTypeEnum.EXCEL2007.getName())) {
						limit = 100000;// excel限制1048574
						returnResult.setData(limit);
					}
					if (vo.getFile_type().equals(ExportTypeEnum.TXT.getName())) {
						limit = 100000;// 无限制
						returnResult.setData(limit);
					}
					break;
				}
			}
			if (!is_legal) {
				Message msg = new Message("导出模板有误，请重新选择", MessageLevel.ERROR);
				msgList.add(msg);
				returnResult.setMessages(msgList);
				return returnResult;
				// throw new HDException("导出模板有误，请重新选择");
			}
		} else if (null == templateid && type.equals("template")) {
			Message msg = new Message("请选择模板", MessageLevel.ERROR);
			msgList.add(msg);
			returnResult.setMessages(msgList);
			return returnResult;
			// throw new HDException("请选择模板");
		} else if (type.equals(ExportWayType.IMPORT.getCode())) {// 导入模板
			// 判断模板合法性
			IExcelTemplateService excelTemplateService = AppServiceHelper.findBean(IExcelTemplateService.class);
			TemplateVO templateVO = excelTemplateService.getImportTempByCode(func_code, tenantid);
			ExcelTemplateVO excelTemplateVO = templateVO.getExcelTemplateVO();
			if (null != excelTemplateVO) {
				if (excelTemplateVO.getFile_type().toLowerCase().equals(ExportTypeEnum.EXCEL2003.getName())) {
					limit = 65534;
					returnResult.setData(limit);
				}
				if (excelTemplateVO.getFile_type().toLowerCase().equals(ExportTypeEnum.EXCEL2007.getName())) {
					limit = 100000;// excel限制1048574
					returnResult.setData(limit);
				}
			} else {
				Message msg = new Message("请先配置导入模板", MessageLevel.ERROR);
				msgList.add(msg);
				returnResult.setMessages(msgList);
				return returnResult;
			}
		} else if (type.equals(ExportWayType.FORM.getCode())){
			//后续可以支持配置Excel版本 2003 、 2007，目前默认2007
			limit = 1048576;
			returnResult.setData(limit);
		} else {
			Message msg = new Message("导出类型有误", MessageLevel.ERROR);
			msgList.add(msg);
			returnResult.setMessages(msgList);
			return returnResult;
			// throw new HDException("导出类型有误");
		}
		if (limit == -1) {
			return returnResult;
			// return true;
		}
		int countnum = exportcounte - exportcountb;
		if (exportcountb > limit || exportcounte > limit || countnum > limit) {
			Message msg = new Message("导出行数不能超过" + limit, MessageLevel.ERROR);
			msgList.add(msg);
			returnResult.setMessages(msgList);
			return returnResult;
			// throw new HDException("导出行数不能超过"+limit);
		}
		if (exportcountb < 1 || exportcounte < 1) {
			Message msg = new Message("行数不能少于1", MessageLevel.ERROR);
			msgList.add(msg);
			returnResult.setMessages(msgList);
			return returnResult;
			// throw new HDException("行数不能少于1");
		}
		if (exportcountb > exportcounte) {
			Message msg = new Message("结束行不能大于开始行", MessageLevel.ERROR);
			msgList.add(msg);
			returnResult.setMessages(msgList);
			return returnResult;
			// throw new HDException("结束行不能大于开始行");
		}
		return returnResult;
		// return true;
	}

	/**
	 * 得到子列表列
	 *
	 * @param func_code
	 * @param tenantid
	 * @param templateid
	 * @param product_flag
	 * @return
	 * @author liyan
	 * @throws HDException
	 * @date 2017年7月12日
	 */
	@Override
	public List<ExportTemplateItemVO> getSubList(String func_code, Long tenantid, Long templateid, int product_flag)
			throws HDException {
		List<FuncLinkVO> sub_func_list = funcLinkService.getFuncLink(func_code, tenantid);
		List<ExportTemplateItemVO> itemList = new ArrayList<>();
		if (null != sub_func_list && sub_func_list.size() > 0) {
			for (FuncLinkVO funcLinkVO : sub_func_list) {
				StringBuilder exceptItemsb = new StringBuilder();
				String sub_func = funcLinkVO.getSub_func_code();
				List<FuncLinkItemVO> linkItems = funcLinkVO.getLinkItems();
				for (FuncLinkItemVO linkItem : linkItems) {
					if (ObjectUtil.isTrue(linkItem.getLitem_isvalue())) {
						exceptItemsb.append(linkItem.getLitem_sub_field());
					}
				}
				List<ExportTemplateItemVO> sub_etivos = creatItemVo(tenantid, func_code, sub_func, templateid,
						product_flag, exceptItemsb.toString());
				List<ExportTemplateItemVO> sub_sub_etivos = getSubList(sub_func, tenantid, templateid, product_flag);// 孙表
				if (null != sub_etivos) {
					itemList.addAll(sub_etivos);
				}
				if (null != sub_sub_etivos) {
					itemList.addAll(sub_sub_etivos);
				}
			}
		}
		return itemList;
	}

	@Override
	public List<ExportTemplateItemVO> creatItemVo(Long tenantid, String main_func_code, String func_code, Long exportid,
												  Integer product_flag, String exceptItem) throws HDException {
		// 新建后 自动生成模板
		List<FormItemPCVO> formItemVOs = formItemPCService.getFormItemsByFunccode(func_code, tenantid);
		Map<String, FormItemVO> fitem_codeMapper = new ConcurrentHashMap<>();
		// 映射表单字段为字段编码为key的map
		for (FormItemVO formItemVO : formItemVOs) {
			fitem_codeMapper.put(formItemVO.getFitem_code().toLowerCase(), formItemVO);
		}
		String tableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);// 操作表信息
		String pkColName = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO().getColcode() : null;
		ObjectUtil.validNotNull(pkColName, "Can't find pkColName of the table[" + tableName + "].");
		List<TableColumnVO> tcs = tableDefVO.getColumnList();
		List<ExportTemplateItemVO> etivos = new ArrayList<ExportTemplateItemVO>();
		// 自动提取表列 所有字段
		for (TableColumnVO tableColumnVO : tcs) {
			// 主键不提取
			if (ObjectUtil.isTrue(tableColumnVO.getIspk()))
				continue;
			String _item_code = tableColumnVO.getColcode().toLowerCase();
			String item_type = tableColumnVO.getColtype().toLowerCase();
			// 跳过一些系统字段
			if (_item_code.equalsIgnoreCase("TENANTID") || _item_code.equalsIgnoreCase("CREATED_BY")
					|| _item_code.equalsIgnoreCase("CREATED_BY_DESC") || _item_code.equalsIgnoreCase("CREATED_DT")
					|| _item_code.equalsIgnoreCase("UPDATED_BY") || _item_code.equalsIgnoreCase("UPDATED_BY_DESC")
					|| _item_code.equalsIgnoreCase("UPDATED_DT") || _item_code.equalsIgnoreCase("DF")
					|| _item_code.equalsIgnoreCase("TS") || _item_code.equalsIgnoreCase("VER")
					|| _item_code.equalsIgnoreCase("CREATED_BY_NAME") || _item_code.equalsIgnoreCase("UPDATED_BY_NAME")
					|| _item_code.equalsIgnoreCase("TENANT_CATEGORY"))
				continue;
			if (func_code.equals("SY_USER") && _item_code.equalsIgnoreCase("password"))
				continue;
			if (exceptItem.contains(_item_code))
				continue;// 主子表传值字段，子表中不需要再有了
			ExportTemplateItemVO exportTemplateItem = new ExportTemplateItemVO();
			exportTemplateItem.setExportid(exportid);
			exportTemplateItem.setInt("product_flag", product_flag);
			exportTemplateItem.setExport_item_code(_item_code);
			exportTemplateItem.setMain_func_code(main_func_code);
			exportTemplateItem.setFunc_code(func_code);
			exportTemplateItem.setColumn_title(tableColumnVO.getColname());
			exportTemplateItem.setColumn_no(10 * (colNum++));
			exportTemplateItem.setIs_display(1);
			if (("datetime").equals(item_type)) {
				exportTemplateItem.setDateformat("yyyy-MM-dd HH:mm:ss");
			}
			if (("date").equals(item_type)) {
				exportTemplateItem.setDateformat("yyyy-MM-dd");
			}
			if (("time").equals(item_type)) {
				exportTemplateItem.setDateformat("HH:mm:ss");
			}
			// exportTemplateItem.setIs_pk(SyConstant.SY_FALSE);
			BaseSettingUtils.setCU_TPD(exportTemplateItem);

			FormItemVO _formItemVO = fitem_codeMapper.get(_item_code);
			if (ObjectUtil.isNotNull(_formItemVO)) {
				exportTemplateItem.setExport_item_name(_formItemVO.getFitem_name());
				exportTemplateItem.setItem_type(_formItemVO.getFitem_input_type());
				if (InputTypeEnum.DICT_NEW.getCode().equals(_formItemVO.getFitem_input_type())) {
					ExportTemplateItemVO exportTemplateItemb = new ExportTemplateItemVO();
					// i++;
					exportTemplateItemb.setExport_item_name(_formItemVO.getFitem_name() + "__编码");
					exportTemplateItemb.setExportid(exportid);
					exportTemplateItemb.setInt("product_flag", product_flag);
					exportTemplateItemb.setExport_item_code(_formItemVO.getFitem_code() + "__code");
					exportTemplateItemb.setMain_func_code(main_func_code);
					exportTemplateItemb.setFunc_code(func_code);
					exportTemplateItemb.setColumn_title(_formItemVO.getFitem_name() + "__编码");
					exportTemplateItemb.setColumn_no(10 * (colNum++));
					exportTemplateItemb.setIs_display(1);
					exportTemplateItem.setDateformat(null);
					BaseSettingUtils.setCU_TPD(exportTemplateItemb);
					etivos.add(exportTemplateItemb);
				}
				fitem_codeMapper.remove(_item_code);
			}
			etivos.add(exportTemplateItem);
			// i++;
		}
		for (FormItemPCVO formItemVO : formItemVOs) {// 添加一些自定义字段，并处理查询选择
			if (fitem_codeMapper.containsKey(formItemVO.getFitem_code().toLowerCase())
					&& formItemVO.getFitem_show_list() == 1) {
				if (ObjectUtil.isNotNull(formItemVO)) {
					ExportTemplateItemVO exportTemplateItem = new ExportTemplateItemVO();
					exportTemplateItem.setExport_item_name(formItemVO.getFitem_name());
					exportTemplateItem.setExportid(exportid);
					exportTemplateItem.setInt("product_flag", product_flag);
					exportTemplateItem.setExport_item_code(formItemVO.getFitem_code());
					exportTemplateItem.setMain_func_code(main_func_code);
					exportTemplateItem.setFunc_code(func_code);
					exportTemplateItem.setColumn_title(formItemVO.getFitem_name());
					exportTemplateItem.setColumn_no(10 * (colNum++));
					exportTemplateItem.setIs_display(1);
					exportTemplateItem.setDateformat(null);
					// exportTemplateItem.setIs_pk(SyConstant.SY_FALSE);
					exportTemplateItem.setItem_type(formItemVO.getFitem_input_type());
					BaseSettingUtils.setCU_TPD(exportTemplateItem);
					etivos.add(exportTemplateItem);
					fitem_codeMapper.remove(formItemVO.getFitem_code().toLowerCase());
					// i++;

					// 查询选择
					if (InputTypeEnum.QUERY_SELECT.getCode().equals(formItemVO.getFitem_input_type())) {
						String query_selector_str = formItemVO.getFitem_input_config();
						QueryselectorInputConfigVO queryselectorInputConfigVO = InputConfigUtils
								.getQueryselectorInputConfigVO(query_selector_str);
						Map<String, String> map = queryselectorInputConfigVO.getMap();
						List<String> item_config_list = new ArrayList<>();
						for (Entry<String, String> entry : map.entrySet()) {
							item_config_list.add(entry.getValue());
						}
						// 默认为code~name 需改动
						for (String item_config_code : item_config_list) {
							if (fitem_codeMapper.containsKey(item_config_code.toLowerCase())) {
								ExportTemplateItemVO exportTemplateItema = new ExportTemplateItemVO();
								String itemname = fitem_codeMapper.get(item_config_code).getFitem_name();
								exportTemplateItema.setExport_item_name(itemname);
								exportTemplateItema.setExportid(exportid);
								exportTemplateItema.setInt("product_flag", product_flag);
								exportTemplateItema.setExport_item_code(item_config_code);
								exportTemplateItema.setMain_func_code(main_func_code);
								exportTemplateItema.setFunc_code(func_code);
								exportTemplateItema.setColumn_title(itemname);
								exportTemplateItema.setColumn_no(10 * (colNum++));
								exportTemplateItema.setIs_display(1);
								exportTemplateItem.setDateformat(null);
								BaseSettingUtils.setCU_TPD(exportTemplateItema);
								etivos.add(exportTemplateItema);
								fitem_codeMapper.remove(item_config_code.toLowerCase());
								// i++;
							}
						}
					}
				}
			}
		}
		return etivos;
	}
	/**
	 * 导出处理
	 */
	public void handleExportDataToFile(ExportHandleRecordVO handle, Long tenantid) throws HDException{

		//更新状态执行中 
		handle.setState(ExportStateEnum.PROCE.getCode());
		handle.setStart_time(new Date());
		DynaSqlVO sql = new DynaSqlVO();
		ArrayList<String> sqlColumnList = new ArrayList<>();
		sqlColumnList.add("state");
		sqlColumnList.add("start_time");
		sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
		sql.setSqlColumnList(sqlColumnList);
		baseService.update(handle,sql);

		int start_rows = handle.getStart_rows();
		int end_rows = handle.getEnd_rows();

		//文件路径名
		ArrayList<String> fileList = new ArrayList<>();
		//总数 
		int countNums = end_rows - start_rows;
		int nums = countNums/8000;
		int remainder = countNums%8000;

		int maxNums = remainder>0?nums++:nums;

		// 开始数
		int temp_nums = start_rows ==0?start_rows :start_rows-1;
		for (int i = 0; i < maxNums; i++) {

			//i * 8000 + 余数 
			if(i==nums) {
				InputStream data = this.getData(handle, temp_nums, temp_nums+remainder);
				// 生成文件名  模块/功能/导出处理主键
				String streamToFile = this.streamToFile(data);
				fileList.add(streamToFile);
			}
			// end
			int iterNums = i*8000;
			// 文件操作
			InputStream data = this.getData(handle, temp_nums, iterNums);
			// 生成文件名  模块/功能/导出处理主键
			String streamToFile = this.streamToFile(data);
			fileList.add(streamToFile);
			// start
			temp_nums = iterNums;
		}
	}
	/**
	 *
	 * @param handle
	 * @param begin
	 * @param end
	 * @throws HDException
	 */
	private InputStream getData(ExportHandleRecordVO handle,int begin,int end) throws HDException {

		String type = handle.getAttach_type();
		Long templateid = handle.getExport_template_id();
		Long importtemplateid = handle.getExport_template_id();
		String funcode = handle.getFunc_code();
		Long tenantid = handle.getTenantid();
		String handle_query_param = handle.getHandle_query_param();
		ReqParamVO param = new ReqParamVO();
		if(org.apache.commons.lang3.StringUtils.isNotBlank(handle_query_param)) {
			param = JsonUtils.parse(handle_query_param, ReqParamVO.class);
		}
		TemplateVO templateVO = getTemp(type, templateid, importtemplateid, funcode, tenantid, handle.getDynamic_param());
		ExportWayFactory exportWayFactory = new ExportWayFactory(type, templateVO.getFileType());
		IExportWay exportWay = exportWayFactory.getExportWay();
		FuncVO funcVO = funcService.queryByFunccode(funcode, tenantid);

		String funcnama = funcVO.getFunc_name();
		List<? extends AbstractVO> exportVoList = new ArrayList<>();
		FormParamVO formParamVO = new FormParamVO();
		formParamVO.setReqParamVO(param);
		if (StringUtils.isEmpty(formParamVO.getExtWhere())) {
			if(formParamVO!=null)
				formParamVO.setIsExport("true");
			exportVoList = listFormService.listQuery(formParamVO).getVoList();
		}
		ReturnResult<InputStream> rr = exportWay.getExportInputStream(templateVO, exportVoList, formParamVO,
				funcnama, tenantid, null, begin, end);
		if(rr==null || rr.getData()==null) {
			throw new HDException("获取文件流失败!");
		}
		return rr.getData();
	}
	/**
	 * 输入流 写文件
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private String streamToFile(InputStream input) {
		String fileName = String.valueOf(System.currentTimeMillis());
		String filePath = System.getProperties().getProperty("user.home");
		filePath = filePath+File.separator+"temp"+File.separator+fileName;
		FileWriter fw = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(filePath);
			bw = new BufferedWriter(fw);
			bufferedReader = new BufferedReader(new InputStreamReader(input));
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				bw.write(line);
			}

			bw.flush();

		} catch (IOException e) {
			logger.error("io 读写文件异常！", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error("io 关闭流异常！", e);
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("io 关闭流异常！", e);
				}
			}

		}
		return filePath;
	}
	@Override
	public String buildWordTestPaper(WordVO word, Long tenantid) throws HDException {

		Word2007Export export = new Word2007Export();
		export.setTenantid(tenantid);
		String path = export.buildWord(word);
		return path;
	}
	/**
	 *  是否已存在
	 */
	public ExportHandleRecordVO queryExportData(String funcCode,String moduleCode,Long tenantId,Long userId)throws HDException {
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam("func_code", funcCode);
		sql.addWhereParam("module_code", moduleCode);
		sql.addWhereParam(SyConstant.TENANT_STR, tenantId);
		sql.addWhereParam("export_user", userId);
		sql.addWhereClause(" state in ('init','proce') ");
		sql.setOrderByClause(" created_dt ,updated_dt desc ");
		return  baseService.query(new ExportHandleRecordVO(), sql).getVO(0);
	}
	/**
	 *  insert
	 */
	public ExportHandleRecordVO insterExportData(ExportHandleRecordVO record) throws HDException {
		return baseService.insert(record);
	}
	/**
	 * 查询功能按钮参数配置
	 * @param funcCode
	 * @param btnCode
	 * @author haocs
	 * @return
	 */
	public int getExportMaxNums(String funcCode,String btnCode,Long tenantid) throws HDException{

		DynaSqlVO sql =new DynaSqlVO();
		sql.addWhereParam("btn_code", btnCode);
		sql.addWhereParam("form_code", funcCode);
		sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
		ButtonPCVO vo = baseService.query(new ButtonPCVO(), sql).getVO(0);
		if(vo ==null  || StringUtils.isEmpty(vo.getBtn_param())) {
			return 0;
		}
		/**
		 * &maxExportNums=4220
		 * &maxExportNums=4220&xxxxx=xxx
		 */
		String btn_param = vo.getBtn_param();
		if(btn_param.indexOf("&")>-1) {
			String maxExportNums= "maxExportNums";
			String[] split = btn_param.split("&");
			for (String index : split) {

				int indexOf = index.indexOf(maxExportNums);
				if(indexOf>=0){
					String substring = index.substring(indexOf+maxExportNums.length());
					String substring2 = substring.substring(1);
					return Integer.parseInt(substring2);
				}

			}
		}
		return 0;
	}

	/**
	 *  查询数据字典
	 */
	public int getDefaultExportMaxNums(Long tenantid) throws HDException {
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam("dict_code", "sy_export_max_nums");
		sql.addWhereParam(SyConstant.TENANT_STR, SyConstant.TENANT_HD);
		DictDataVO vo = baseService.query(new DictDataVO(), sql).getVO(0);
		if(vo==null || StringUtils.isEmpty(vo.getDict_data_code())) {
			throw new HDException("异步导出字典,默认最大数未配置!");
		}
		String max_num = vo.getDict_data_code();
		return Integer.parseInt(max_num);
	}
}
