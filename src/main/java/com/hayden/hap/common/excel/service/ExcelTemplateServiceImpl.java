package com.hayden.hap.common.excel.service;

import com.hayden.hap.common.billcode.itf.IBillCodeService;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.keyGen.itf.IKeyGeneratorService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.ExportWayType;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.enumerate.MessageShowType;
import com.hayden.hap.common.excel.action.ExcelTemplateBaseAction;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.excel.itf.IExcelTemplateAction;
import com.hayden.hap.common.excel.itf.IExcelTemplateDataAction;
import com.hayden.hap.common.excel.itf.IExcelTemplateService;
import com.hayden.hap.common.excel.itf.IExcelVOMapper;
import com.hayden.hap.common.excel.utils.ExcelTemplateConstants;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.action.BaseAction;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.Message;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.common.formmgr.utils.ValidateUtils;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.ExceptionHandlerUtils;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import com.hayden.hap.common.utils.template.TemplateUtils;
import com.hayden.hap.common.utils.tuple.TupleUtils;
import com.hayden.hap.common.utils.tuple.TwoTuple;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author ZMing
 * @date 2016年4月12日
 */
@Service("excelTemplateService")
public class ExcelTemplateServiceImpl implements IExcelTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelTemplateServiceImpl.class);

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
    private IBillCodeService billCodeService;
    @Autowired
    private IExcelVOMapper POIExcelVOMapperService;
    @Autowired
    private IKeyGeneratorService serialGeneratorService;
    @Autowired
    private IFuncLinkService funcLinkService;

    @Override
    public TemplateVO getImportTempByCode(String funcCode, Long tenantid) throws HDException {
        TemplateVO templateVO = new TemplateVO();
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("func_code", funcCode);
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
        dynaSqlVO.setOrderByClause("created_dt desc");
        VOSet<ExcelTemplateVO> voSet = baseService.query(ExcelTemplateVO.class, dynaSqlVO);
        ExcelTemplateVO resultvo = new ExcelTemplateVO();
        if (voSet.getVoList().size() > 0) {
            resultvo = voSet.getVoList().get(0);
            dynaSqlVO = new DynaSqlVO();
            dynaSqlVO.addWhereParam("templateid", resultvo.getTemplateid());
            dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
            dynaSqlVO.setOrderByClause("column_no asc");
            VOSet<ExcelTemplateItemVO> itemVoSet = baseService.query(ExcelTemplateItemVO.class, dynaSqlVO);
            templateVO.setExcelTemplateVO(resultvo);
            templateVO.setExcelTemplateVOList(itemVoSet.getVoList());
            templateVO.setFileType(resultvo.getFile_type());
        }
        return templateVO;
    }

    @Override
    @Deprecated
    public List<Message> exportTemplate(String func_code, HttpServletRequest request, HttpServletResponse response) {
        Long tenantid = CurrentEnvUtils.getTenantId();

        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("FUNC_CODE", func_code);
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
        dynaSqlVO.setOrderByClause("created_dt desc");
        ExcelTemplateVO excelTemplateVO = baseService.query(ExcelTemplateVO.class, dynaSqlVO).getVO(0);
        //ReturnResult returnResult = new ReturnResult();
        List<Message> listMessage = new ArrayList<>();
        //List<ResultMessage<AbstractVO>> errorList = new ArrayList<>();
        if (ObjectUtil.isNotNull(excelTemplateVO)) {
            try {
                dynaSqlVO.addWhereParam("templateid", excelTemplateVO.getTemplateid());
                dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
                List<ExcelTemplateItemVO> excelTemplateItemVOs = baseService.query(ExcelTemplateItemVO.class, dynaSqlVO).getVoList();
                InputStream in = POIExcelVOMapperService.exportExcel(excelTemplateVO, excelTemplateItemVOs);
                response.reset();
                // 设置头信息,内容处理的方式,attachment以附件的形式打开,就是进行下载,并设置下载文件的命名
                String suf = ".xls";
                if (excelTemplateVO.getFile_type().equalsIgnoreCase("XLS")) {
                    suf = ".xls";
                    response.setContentType("application/vnd.ms-excel; charset=utf-8");
                } else {
                    suf = ".xlsx";
                    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=utf-8");
                }
                String agent = request.getHeader("user-agent");
                String fileNameTemp = excelTemplateVO.getTemp_name() + suf;
                if (agent.contains("Firefox"))
                    fileNameTemp = new String(fileNameTemp.getBytes("UTF-8"), "ISO8859-1");
                else {
                    //fileNameTemp = EncoderUtils.encodeUrl(fileNameTemp);
                    fileNameTemp = URLEncoder.encode(fileNameTemp, "UTF-8").replaceAll("\\+", "%20")
                            .replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";")
                            .replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&");
                }
                response.setHeader("Content-Disposition", "attachment; filename=" + fileNameTemp);
                // 响应输出流
                OutputStream out = response.getOutputStream();
                // 创建缓冲区
                byte[] buffer = new byte[10240];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.flush();
                out.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                Message message = new Message(e.getMessage(), MessageLevel.ERROR, MessageShowType.POPUP);
                listMessage.add(message);
            }
        } else {
            Message message = new Message("请先配置功能相对应EXCEL模板！", MessageLevel.ERROR, MessageShowType.POPUP);
            listMessage.add(message);
        }
        return listMessage;
    }

    /**
     * 	 查询导入模板定义
     * @param temp_code
     * @param tenantid
     * @return
     * @throws HDException 
     */
    private ExcelTemplateVO getExcelTemplataVO(String temp_code,Long tenantid) throws HDException {
	
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(ExcelTemplateConstants.TEMP_CODE, temp_code);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		List<ExcelTemplateVO> excelTemplateVOs = baseService.query(ExcelTemplateVO.class, dynaSqlVO).getVoList();
		if (ObjectUtil.isEmpty(excelTemplateVOs)) {
			throw new HDException("模板编码为" + temp_code + "的导入模板不存在！");
		}
		if (excelTemplateVOs.size() > 1) {
			throw new HDException("模板编码为" + temp_code + "的导入模板不唯一！");
		}
		ExcelTemplateVO excelTemplateVO = excelTemplateVOs.get(0);
		return excelTemplateVO;
    }
    /**
     * 	 查询导入模板字段
     * @param temp_code
     * @param tenantid
     * @return
     * @throws HDException 
     */
    private List<ExcelTemplateItemVO> getExcelTemplataItemVOs(String temp_code,Long templateid,Long tenantid) throws HDException {
    	
    	DynaSqlVO dynaSqlVO = new DynaSqlVO();
    	dynaSqlVO.addWhereParam(ExcelTemplateConstants.TEMPLATEID,templateid);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		List<ExcelTemplateItemVO> excelTemplateItemVOs = baseService.query(ExcelTemplateItemVO.class, dynaSqlVO)
				.getVoList();
		if (ObjectUtil.isEmpty(excelTemplateItemVOs))
			throw new HDException("模板编码为" + temp_code + "的导入模板无列信息！");
    	return excelTemplateItemVOs;
    }


    @Override
    public ReturnResult<?> importTemplate(FormParamVO formParamVO, String temp_code, InputStream is,
                                   String extensionName, Long tenantid) throws HDException{
        ReturnResult<?> result = null;
        Workbook workbook = getWorkbook(is);
        ExportWayType exportWayType = getExportWayType(workbook);
        if(exportWayType == ExportWayType.IMPORT){
            result = this.importTemplateOfImport(formParamVO, workbook, temp_code, tenantid);
        }else if (exportWayType == ExportWayType.FORM){
            result = this.importTemplateOfForm(formParamVO, workbook, tenantid);
        }else{
            throw new HDException("未知的导入模板");
        }

        return result;
    }


    /**
     * 导入excle  -  表单模板方式
     * @param formParamVO
     * @param workbook
     * @param tenantid
     * @return: com.hayden.hap.common.formmgr.message.ReturnResult<?>
     * @Author: suntaiming
     * @Date: 2021/6/10 15:07
     */
    public ReturnResult<?> importTemplateOfForm(FormParamVO formParamVO, Workbook workbook, Long tenantid) throws HDException{
        // 解析excel获取数据
        // 包括主子孙等功能数据
        ReturnResult<?> returnResult = POIExcelVOMapperService.exportListFromExcelOfForm(formParamVO, workbook, tenantid);
        TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>> twoTuple = (TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>) returnResult
                .getData();
        //主数据
        List<AbstractVO> mainList = twoTuple._1();
        //子孙数据
        LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap = twoTuple._2();
        if ((ObjectUtil.isEmpty(mainList) && !ObjectUtil.isNotNull(mainAndSubMap))) {
            return new ReturnResult<>("excel中没有数据可导入！", MessageLevel.ERROR);
        }

        String func_code = formParamVO.getFuncCode();

        String funcTableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
        TableDefVO funcTableDefVO = tableDefService.queryDetailedTableByTbname(funcTableName);
        FormVO formVO = formService.getFormVOByFunccode(func_code, tenantid);
        List<? extends FormItemVO> formItemVOS = formItemService.getFormItemsByFunccode(func_code, tenantid);

        // 查询导入模板定义
        ExcelTemplateVO excelTemplateVO = TemplateUtils.transformExcelTemplateVO(formVO, func_code, null);
        // 模板字段
        List<ExcelTemplateItemVO> excelTemplateItemVOs = TemplateUtils.transformExcelTemplateItemVOs(formItemVOS, funcTableDefVO, func_code);


        // 获取导入模板定义的处理类
        Object action = getActionByClassName(formVO.getExtends_class());
        if (action != null) {
            // 找到对应action，此分支为自定义导入，执行此分支后将不再执行下面的内容
            // 此action分支，实现各产品统一按照导入模板逻辑导入数据，导入前业务动作
            if (BaseAction.class.isAssignableFrom(action.getClass())) {
                BaseAction baseAction = (BaseAction) action;
                List<Message> beforeMegerBatchErrorList = baseAction.beforeMegerBatch(formParamVO,
                        excelTemplateVO, mainList, mainAndSubMap);
                if (ObjectUtil.isNotEmpty(beforeMegerBatchErrorList)) {// 如果验证未通过，返回验证信息
                    //beforeMegerBatchErrorList.get(0).setMessageLevel(MessageLevel.ERROR);
                    ExceptionHandlerUtils.handle(beforeMegerBatchErrorList, null);
                    returnResult.setMessages(beforeMegerBatchErrorList);

                }
            }

        }
        logger.error(" 开始拆分！ ");
        try {
            // 拆分list两个集合，一个更新，一个插入
            // 解析数据，得到新增和更新的数据
            returnResult = insert_or_update(mainList, mainAndSubMap, excelTemplateVO, excelTemplateItemVOs, formParamVO,
                    tenantid);
        } catch (Exception e) {
            throw new HDException("校验导入数据出错" + e.getMessage());
        }
        logger.error(" 拆分完成！ ");
        TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>> insertOrUpdateTuple = (TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>>) returnResult
                .getData();
        Map<String, List<AbstractVO>> funcToVoInsertListMap = insertOrUpdateTuple._1();// 每个功能要插入的list
        Map<String, List<AbstractVO>> funcToVoAllListMap = insertOrUpdateTuple._2();// 每个功能的list
        //如果验证未通过，返回验证信息
        ExceptionHandlerUtils.handle(returnResult.getMessages(), null);


        // 从数据库区分需要插入还是更新后再给业务进行操作，减少业务查询次数
        if (action != null) {
            BaseAction excelTemplateBaseAction = (BaseAction) action;
            List<Message> afterDbBatchErrorList = excelTemplateBaseAction.afterDbBatch(formParamVO, excelTemplateVO,
                    mainList, mainAndSubMap, funcToVoInsertListMap, funcToVoAllListMap);

            if (ObjectUtil.isNotEmpty(afterDbBatchErrorList)) {// 如果验证未通过，返回验证信息
                //afterDbBatchErrorList.get(0).setMessageLevel(MessageLevel.ERROR);
                ExceptionHandlerUtils.handle(afterDbBatchErrorList, null);
                returnResult.setMessages(afterDbBatchErrorList);
            }
        }
        //插入或者更新导入数据
        ReturnResult<?> rm = mergeTemplate(funcToVoInsertListMap, funcToVoAllListMap, excelTemplateVO,
                excelTemplateItemVOs);
        if (rm != null && rm.getMessages() != null) {
            if (rm.getStatus() == Status.FAIL) {
                List<Message> errorList = new ArrayList<Message>(rm.getMessages());
                //errorList.get(0).setMessageLevel(MessageLevel.ERROR);
                ExceptionHandlerUtils.handle(errorList, null);
                returnResult.setMessages(errorList);
            } else {
                returnResult.getMessages().addAll(rm.getMessages());
            }
        }

        if (action != null) {
            BaseAction excelTemplateBaseAction = (BaseAction) action;
            List<Message> afterMegerBatchErrorList = excelTemplateBaseAction.afterMegerBatch(formParamVO,
                    excelTemplateVO, mainList, mainAndSubMap);
            if (ObjectUtil.isNotEmpty(afterMegerBatchErrorList)) {// 如果验证未通过，返回验证信息
                //afterMegerBatchErrorList.get(0).setMessageLevel(MessageLevel.ERROR);
                ExceptionHandlerUtils.handle(afterMegerBatchErrorList, null);
                returnResult.setMessages(afterMegerBatchErrorList);

            }
        }
        returnResult.setData(null);
        return returnResult;
    }



    private Workbook getWorkbook(InputStream is) throws HDException{
        try {
            return WorkbookFactory.create(is);
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


    /**
     * 导入excle  -  导入模板方式
     * @param formParamVO
     * @param workbook
     * @param temp_code
     * @param tenantid
     * @return: com.hayden.hap.common.formmgr.message.ReturnResult<?>
     * @Author: suntaiming
     * @Date: 2021/6/10 15:06
     */
    @SuppressWarnings("unchecked")
	@Transactional
	public ReturnResult<?> importTemplateOfImport(FormParamVO formParamVO, Workbook workbook,String temp_code, Long tenantid) throws HDException {
		// 查询导入模板定义
		ExcelTemplateVO excelTemplateVO = this.getExcelTemplateVOOfSheet(formParamVO, workbook, temp_code, tenantid);
		// 模板字段
		List<ExcelTemplateItemVO> excelTemplateItemVOs = this.getExcelTemplataItemVOs(excelTemplateVO.getTemp_code(), excelTemplateVO.getTemplateid(), tenantid);

		// 解析excel获取数据
		// 包括主子孙等功能数据
		ReturnResult<?> returnResult = POIExcelVOMapperService.exportListFromExcel(workbook, excelTemplateVO,
				excelTemplateItemVOs);
		logger.error(" exportListFromExcel 数据装配完成！ ");
		if (returnResult.getStatus() == Status.FAIL) {
			return returnResult;
		}
		TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>> twoTuple = (TwoTuple<List<AbstractVO>, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>>>) returnResult
				.getData();
		if (!ObjectUtil.isNotNull(twoTuple)) {
			return new ReturnResult<>("excel中没有数据可导入！", MessageLevel.ERROR);
		}
		//主数据
		List<AbstractVO> mainList = twoTuple._1();
		//子孙数据
		LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap = twoTuple._2();
		if ((ObjectUtil.isEmpty(mainList) && !ObjectUtil.isNotNull(mainAndSubMap))) { 
			return new ReturnResult<>("excel中没有数据可导入！", MessageLevel.ERROR);
		}
		// 获取导入模板定义的处理类
		Object action = getActionByClassName(excelTemplateVO.getPre_class());
		if (action != null) {
			// 找到对应action，此分支为自定义导入，执行此分支后将不再执行下面的内容
			// 此action分支，实现各产品按照各自的业务逻辑excel数据导入到数据库
			if (IExcelTemplateDataAction.class.isAssignableFrom(action.getClass())) {
				IExcelTemplateDataAction excelTemplateDataAction = (IExcelTemplateDataAction) action;
				// 对应action传入Excel内容
				returnResult = excelTemplateDataAction.getResult(formParamVO, excelTemplateVO, returnResult);
				if (returnResult != null) {
					returnResult.setData(null);
				}
				return returnResult;
			}
			// 此action分支，实现各产品统一按照导入模板逻辑导入数据，导入前业务动作
			if (ExcelTemplateBaseAction.class.isAssignableFrom(action.getClass())) {
				ExcelTemplateBaseAction excelTemplateBaseAction = (ExcelTemplateBaseAction) action;
				List<Message> beforeMegerBatchErrorList = excelTemplateBaseAction.beforeMegerBatch(formParamVO,
						excelTemplateVO, mainList, mainAndSubMap);
				if (ObjectUtil.isNotEmpty(beforeMegerBatchErrorList)) {// 如果验证未通过，返回验证信息
					//beforeMegerBatchErrorList.get(0).setMessageLevel(MessageLevel.ERROR);
					ExceptionHandlerUtils.handle(beforeMegerBatchErrorList, null);
                    returnResult.setMessages(beforeMegerBatchErrorList);
				}
			}

		}
		logger.error(" 开始拆分！ ");
		try {
			// 拆分list两个集合，一个更新，一个插入
			// 解析数据，得到新增和更新的数据
			returnResult = insert_or_update(mainList, mainAndSubMap, excelTemplateVO, excelTemplateItemVOs, formParamVO,
					tenantid);
		} catch (Exception e) {
			throw new HDException("校验导入数据出错" + e.getMessage());
		}
		logger.error(" 拆分完成！ ");
		TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>> insertOrUpdateTuple = (TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>>) returnResult
				.getData();
		Map<String, List<AbstractVO>> funcToVoInsertListMap = insertOrUpdateTuple._1();// 每个功能要插入的list
		Map<String, List<AbstractVO>> funcToVoAllListMap = insertOrUpdateTuple._2();// 每个功能的list
		//如果验证未通过，返回验证信息
		ExceptionHandlerUtils.handle(returnResult.getMessages(), null);

		// 对vo进行验证
		List<Message> validateErrorList = this.validateErrorList(funcToVoAllListMap, new ArrayList<>(), tenantid);
		if (ObjectUtil.isNotEmpty(validateErrorList)) {// 如果验证未通过，返回验证信息
			returnResult.setMessages(validateErrorList);
			return returnResult;
		}
		// vo验证结束
		// 从数据库区分需要插入还是更新后再给业务进行操作，减少业务查询次数
		if (action != null) {
			ExcelTemplateBaseAction excelTemplateBaseAction = (ExcelTemplateBaseAction) action;
			List<Message> afterDbBatchErrorList = excelTemplateBaseAction.afterDbBatch(formParamVO, excelTemplateVO,
					mainList, mainAndSubMap, funcToVoInsertListMap, funcToVoAllListMap);

			if (ObjectUtil.isNotEmpty(afterDbBatchErrorList)) {// 如果验证未通过，返回验证信息
				//afterDbBatchErrorList.get(0).setMessageLevel(MessageLevel.ERROR);
				ExceptionHandlerUtils.handle(afterDbBatchErrorList, null);
                returnResult.setMessages(afterDbBatchErrorList);
			}
		}
		//插入或者更新导入数据 
		ReturnResult<?> rm = mergeTemplate(funcToVoInsertListMap, funcToVoAllListMap, excelTemplateVO,
				excelTemplateItemVOs);
		if (rm != null && rm.getMessages() != null) {
			if (rm.getStatus() == Status.FAIL) {
				List<Message> errorList = new ArrayList<Message>(rm.getMessages());
				//errorList.get(0).setMessageLevel(MessageLevel.ERROR);
				ExceptionHandlerUtils.handle(errorList, null);
                returnResult.setMessages(errorList);
			} else {
				returnResult.getMessages().addAll(rm.getMessages());
			}
		}

		if (action != null) {
			ExcelTemplateBaseAction excelTemplateBaseAction = (ExcelTemplateBaseAction) action;
			List<Message> afterMegerBatchErrorList = excelTemplateBaseAction.afterMegerBatch(formParamVO,
					excelTemplateVO, mainList, mainAndSubMap);
			if (ObjectUtil.isNotEmpty(afterMegerBatchErrorList)) {// 如果验证未通过，返回验证信息
				//afterMegerBatchErrorList.get(0).setMessageLevel(MessageLevel.ERROR);
				ExceptionHandlerUtils.handle(afterMegerBatchErrorList, null);
                returnResult.setMessages(afterMegerBatchErrorList);
			}
		}
		returnResult.setData(null);
		return returnResult;
	}


	private ExcelTemplateVO getExcelTemplateVOOfSheet(FormParamVO formParamVO, Workbook workbook,String temp_code, Long tenantid) throws HDException{
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("func_code", formParamVO.getFuncCode());
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
        List<ExcelTemplateVO> excelTemplateVOS = baseService.query(ExcelTemplateVO.class, dynaSqlVO).getVoList();
        if(excelTemplateVOS.isEmpty()){
            dynaSqlVO = new DynaSqlVO();
            dynaSqlVO.addWhereParam("temp_code", temp_code);
            dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
            excelTemplateVOS = baseService.query(ExcelTemplateVO.class, dynaSqlVO).getVoList();
        }

        if(excelTemplateVOS.isEmpty()){
             throw new HDException("该功能未配置到导入模板,请先配置");
        }
        ExcelTemplateVO target =  null;

        for (ExcelTemplateVO excelTemplateVO : excelTemplateVOS){
            
            // 因poi会把sheetName截取到31个字符，所以需要特殊处理下funcCode过长的
            // 判断下tempCode是否在过长的导入funcCode里面，如果在的话截取一下
            String tempCode = excelTemplateVO.getTemp_code();
            if (SyConstant.TOO_LONG_EXCEL_FUNC_CODE.contains(tempCode)) {
                tempCode = tempCode.substring(0,31);
            }
            Sheet sheet = workbook.getSheet(tempCode);
            if(sheet != null){
                target =  excelTemplateVO;
                break;
            }
        }

        if(target == null){
            throw new HDException("该excel不是一个导入模板");
        }

        return target;
    }
	/**
	 * 对vo数据进行验证
	 * 
	 * @param funcToVoAllListMap
	 * @param validateErrorList
	 * @param tenantid
	 * @return
	 * @throws HDException
	 */
	private List<Message> validateErrorList(Map<String, List<AbstractVO>> funcToVoAllListMap,List<Message> validateErrorList,Long tenantid) throws HDException{
		for (Entry<String, List<AbstractVO>> entry : funcToVoAllListMap.entrySet()) {
			List<AbstractVO> validateList = entry.getValue();// 当前功能要检验的list
			List<? extends FormItemVO> formItemVOs = formItemService.getFormItemsByFunccode(entry.getKey(), tenantid);
			List<Message> uniqueMessage = ValidateUtils.validateLocalUnique(validateList, formItemVOs,
					x -> x.getString("rowNum"));
			if (ObjectUtil.isNotEmpty(uniqueMessage)) {
				return uniqueMessage;
			}
			for (int i = 0; i < validateList.size(); i++) {
				AbstractVO avo = validateList.get(i);
				// 表单校验
				List<Message> validateResultList = ValidateUtils.validate(avo, formItemVOs, tenantid);
				if (ObjectUtil.isNotEmpty(validateResultList)) {// 如果验证未通过，返回验证信息
					String msg = "";
					for (Message validateResult : validateResultList) {
						msg = validateResult.getMessage() + ";";
						Message message = new Message("第" + avo.getString("rowNum") + "行 " + msg, MessageLevel.ERROR);
						validateErrorList.add(message);
					}
				}
			}
		}
		return validateErrorList;
	}
    @SuppressWarnings("unchecked")
    @Override
    @Deprecated
    public ReturnResult<?> readTemplateData(FormParamVO formParamVO, InputStream is, String extensionName) throws HDException {
        Long tenantid = CurrentEnvUtils.getTenantId();
        ReturnResult<?> returnResult = null;
        //根据功能编码和租户id查找对应excel模板
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("FUNC_CODE", formParamVO.getFuncCode());
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
        dynaSqlVO.setOrderByClause("created_dt desc");
        ExcelTemplateVO excelTemplateVO = baseService.query(ExcelTemplateVO.class, dynaSqlVO).getVO(0);
        if (ObjectUtil.isNotNull(excelTemplateVO)) { //有对应模板
            dynaSqlVO.addWhereParam("templateid", excelTemplateVO.getTemplateid());
            //根据模板id查找子字段项
            List<ExcelTemplateItemVO> excelTemplateItemVOs = baseService.query(ExcelTemplateItemVO.class, dynaSqlVO).getVoList();
            //读取excel文件内容
            returnResult = POIExcelVOMapperService.exportListFromExcel(is, extensionName, excelTemplateVO, excelTemplateItemVOs);
            if (returnResult.getStatus() == Status.FAIL) {
                return returnResult;
            }

            List<AbstractVO> list = (List<AbstractVO>) returnResult.getData();
            if (!ObjectUtil.isNotEmpty(list)) {
                returnResult = new ReturnResult<List<AbstractVO>>("excel中没有数据可导入", MessageLevel.ERROR);
                return returnResult;
            }
            //找到对应action
            Object obj = getActionByClassName(excelTemplateVO.getPre_class());
            if (ObjectUtil.isNotNull(obj) && IExcelTemplateDataAction.class.isAssignableFrom(obj.getClass())) {
                IExcelTemplateDataAction action = (IExcelTemplateDataAction) obj;
                if (ObjectUtil.isNotNull(action)) {
                    //对应action传入Excel内容
                    returnResult = action.getResult(formParamVO, excelTemplateVO, returnResult);
                }
            }
        }
        return returnResult;
    }

    /**
     * 根据配置导入主键，找到是否更新还是新增，，更新vo设置pk，返回需要更新vo列表，
     *
     * @param voList
     * @param mainAndSubMap        //有序map先处理主功能在处理子功能,然后处理孙功能
     * @param excelTemplateVO
     * @param excelTemplateItemVOs
     * @param formParamVO
     * @param tenantid
     * @return updateList 需要更新vo列表
     * @throws HDException
     */
    @Transactional
    public ReturnResult<TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>>> insert_or_update(List<AbstractVO> voList, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap, ExcelTemplateVO excelTemplateVO,
                                                                                                                 List<ExcelTemplateItemVO> excelTemplateItemVOs, FormParamVO formParamVO, Long tenantid) throws HDException {
        ReturnResult<TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>>> returnResult = new ReturnResult<>();
        String mainFuncCode = excelTemplateVO.getFunc_code();
        List<Message> msgList = new ArrayList<>();
        Map<String, List<AbstractVO>> funcToVoInsertListMap = new HashMap<>();//每个功能要插入的list
        Map<String, List<AbstractVO>> funcToVoAllListMap = new HashMap<>();//每个功能的list
        //解析主功能数据
        ReturnResult<?> mainResult = handleVoList(mainFuncCode, voList, excelTemplateVO, excelTemplateItemVOs, funcToVoInsertListMap, formParamVO, tenantid);//处理主功能
        if (ObjectUtil.isNotEmpty(mainResult.getMessages())) {
            msgList.addAll(mainResult.getMessages());
        }
        funcToVoAllListMap.put(mainFuncCode, voList);
        for (Entry<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> entry : mainAndSubMap.entrySet()) {
            String key = entry.getKey();
            LinkedHashMap<AbstractVO, List<AbstractVO>> mainAndSubVoMap = entry.getValue();
            int index = key.indexOf(".");
            String main_func_code = key.substring(0, index);
            String func_code = key.substring(index + 1);
            if (mainAndSubVoMap.isEmpty()) continue;
            ReturnResult<List<AbstractVO>> subResult = handleSubVoList(main_func_code, func_code, mainAndSubVoMap, excelTemplateVO, excelTemplateItemVOs, funcToVoInsertListMap);//处理子功能
            if (ObjectUtil.isNotEmpty(subResult.getMessages())) {
                msgList.addAll(subResult.getMessages());
            }
            List<AbstractVO> subList = subResult.getData();
            if (null != funcToVoAllListMap.get(func_code)) {
                subList.addAll(funcToVoAllListMap.get(func_code));
                funcToVoAllListMap.put(func_code, subList);
            } else {
                funcToVoAllListMap.put(func_code, subList);
            }
        }
        //返回功能的新增数据和所有数据
        TwoTuple<Map<String, List<AbstractVO>>, Map<String, List<AbstractVO>>> insertOrUpdateTuple = TupleUtils.tuple(funcToVoInsertListMap, funcToVoAllListMap);
        returnResult.setMessages(msgList);
        returnResult.setData(insertOrUpdateTuple);
        return returnResult;
    }
    /**
     * 主子表关联字段拼装map
     * 主VO集合
     * @param pkmap
     * @param mainAndSubVoMap
     * @param voList
     * @param errorList
     * @param main_func_code
     * @param func_code
     * @param tenantid
     * @return
     */
    private Map<String,String> getPkMap(Map<String,String> pkmap,Map<AbstractVO, List<AbstractVO>> mainAndSubVoMap,List<AbstractVO> voList,
    		List<Message> errorList,String main_func_code,String func_code,Long tenantid){
    	 //主子表关联字段匹配
    	
        FuncLinkVO funcLinkVO = funcLinkService.getFuncLink(main_func_code, func_code, tenantid).get(0);
        if (null != funcLinkVO) {
            List<FuncLinkItemVO> itemVOList = funcLinkVO.getLinkItems();
            if (ObjectUtil.isNotEmpty(itemVOList)) {
                int length = itemVOList.size();
                int errorSubNum = 0;//记录主表数据为空，子表有数据的错误数据条数
                for (FuncLinkItemVO itemVO : itemVOList) {
                    length = length - 1;
                    if (ObjectUtil.isTrue(itemVO.getLitem_isvalue())) {
                        String litem_sub_field = itemVO.getLitem_sub_field();
                        String litem_main_field = itemVO.getLitem_main_field();
                        if (!pkmap.isEmpty()) {//主子表联合主键，子表主键+主子表关联字段，jobcode+jobparamcode
                            pkmap.put(litem_sub_field, litem_main_field);
                        }
                        for (Entry<AbstractVO, List<AbstractVO>> entry : mainAndSubVoMap.entrySet()) {
                            AbstractVO curMainVo = entry.getKey();
                            if (null == curMainVo) {
                                errorSubNum++;
                                continue;
                            }
                            List<AbstractVO> curSubList = entry.getValue();
                            if (ObjectUtil.isTrue(itemVO.getLitem_isconstant())) {
                                for (AbstractVO vo : curSubList) {//如果是常量，直接赋值
                                    vo.set(litem_sub_field, litem_main_field);
                                    if (0 == length) {
                                        voList.add(vo);
                                    }
                                }
                            } else {
                                for (AbstractVO vo : curSubList) {
                                    vo.set(litem_sub_field, curMainVo.get(litem_main_field));
                                    if (0 == length) {
                                        voList.add(vo);
                                    }
                                }
                            }
                        }
                    }
                }
                if (errorSubNum > 0) {
                    Message msg = new Message("由于主功能没有数据，子功能部分数据已被忽略", MessageLevel.WARN);
                    errorList.add(msg);
                }
            }
        }
        return pkmap;
    }
    private ReturnResult<List<AbstractVO>> handleSubVoList(String main_func_code,
                                                           String func_code, LinkedHashMap<AbstractVO, List<AbstractVO>> mainAndSubVoMap,
                                                           ExcelTemplateVO excelTemplateVO,
                                                           List<ExcelTemplateItemVO> excelTemplateItemVOs, Map<String, List<AbstractVO>> funcToVoInsertListMap) {
        Long tenantid = CurrentEnvUtils.getTenantId();
        ReturnResult<List<AbstractVO>> returnResult = new ReturnResult<>();
        List<Message> errorList = new ArrayList<>();
        List<AbstractVO> insertList = new ArrayList<>();
        List<AbstractVO> voList = new ArrayList<>();
        //更新 根据标识的主键字段 判断
        Map<String, String> pkmap = new HashMap<>();
        for (ExcelTemplateItemVO excelTemplateItemVO : excelTemplateItemVOs) {
            if (func_code.equals(excelTemplateItemVO.getFunc_code()) && ObjectUtil.isTrue(excelTemplateItemVO.getIs_pk())) {
                pkmap.put(excelTemplateItemVO.getItem_code(),
                        excelTemplateItemVO.getColumn_title());
            }
        }
        //主子表关联字段拼装map 	主VO集合
        pkmap = this.getPkMap(pkmap, mainAndSubVoMap, voList, errorList, main_func_code, func_code, tenantid);
        //查询表信息
        String tableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
        TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
        String pkName = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO().getColcode() : null;
        if (!StringUtils.hasLength(pkName)) {
            Message message = new Message("table:" + tableName + "的主键列没有找到", MessageLevel.ERROR);
            errorList.add(message);
            returnResult.setMessages(errorList);
            return returnResult;
        }
        if (ObjectUtil.isNotEmpty(pkmap)) {
            //遍历list  找到主键列重复数据
        	// 校验excel 数据重复
        	errorList = this.checkExcelDuplication(pkmap, voList, errorList);
            if (ObjectUtil.isNotEmpty(errorList)) {
                returnResult.setMessages(errorList);
                if (returnResult.getStatus() == Status.FAIL) {
                    return returnResult;
                }
            }
            //数据筛选
            List<AbstractVO> dblist = this.getListData(pkmap, voList, tenantid, tableName);
            logger.error( func_code +" 查询库数据！");
            //数据库比对
            if (ObjectUtil.isNotEmpty(dblist)) {
            	/**
            	 * 新增添加到insetList
            	 * 更新 赋值PK 添加至 voList
            	 */
				this.checkDbDuplication(pkmap, dblist, voList, insertList, errorList, pkName);
            } else
                insertList.addAll(voList);
        } else {
            insertList.addAll(voList);
        }
        //赋主键
        String[] pks = serialGeneratorService.generate(tableName, insertList.size());
        for (int i = 0; i < insertList.size(); i++) {
            insertList.get(i).setLong(pkName, Long.parseLong(pks[i]));
        }
        funcToVoInsertListMap.put(func_code, insertList);
        returnResult.setData(voList);
        return returnResult;
    }
    /**
     * 数据筛选
     * @param pkmap
     * @param dblist
     * @param voList
     * @param tenantid
     * @param tableName
     * @return
     */
    private List<AbstractVO> getListData(Map<String, String> pkmap,List<AbstractVO> voList,Long tenantid,String tableName){
    	 //数据筛选
        StringBuilder sub = new StringBuilder();
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        for (AbstractVO abstractVO : voList) {
        	StringBuilder subtmp = new StringBuilder();
        	for (String pk : pkmap.keySet()) {
        		Object value = abstractVO.get(pk);
        		if (value==null) {
        			subtmp.append(pk).append(" is null");
				}else {
					if(value instanceof String){
						subtmp.append(pk).append(" = ").append("'").append(((String) value).replace("'", "\\'")).append("'");
					}else if(value instanceof Date){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String dateString = sdf.format(value);
                        subtmp.append(pk).append(" = ").append("'").append(dateString).append("'");
                    }else{
						subtmp.append(pk).append(" = ").append(ConvertUtils.convert(value, value.getClass()));
					}
				}
        		subtmp.append(" and ");
    		}
        	subtmp.delete(subtmp.lastIndexOf("and"),subtmp.length());
        	subtmp.insert(0, "(").append(") or ");
        	sub.append(subtmp.toString());
		}
        logger.error(sub.toString());
        sub.delete(sub.lastIndexOf("or"),sub.length());
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
        if(!StringUtils.isEmpty(sub.toString()) && sub.length()>0) {
        	dynaSqlVO.addWhereClause(sub.toString());
        }
        return baseService.query(tableName, dynaSqlVO).getVoList();
    }
    /**
     * 
     * excel 数据和db 数据比对
     * i--> insertList
     * @param pkmap
     * @param dblist
     * @param voList
     * @param insertList
     * @param errorList
     * @param pkName
     */
    private  void checkDbDuplication( Map<String, String> pkmap, List<AbstractVO> dblist,List<AbstractVO> voList,List<AbstractVO> insertList,List<Message> errorList,String pkName){
    	
    	HashMap<String, AbstractVO> mapByDb = new HashMap<>();
		for (int voIndex = 0; voIndex < dblist.size(); voIndex++) {
			AbstractVO excelvo = dblist.get(voIndex);
			String key = "";
			for (String pk : pkmap.keySet()) {
				Object object = excelvo.get(pk);
				if (object != null) {
					key += object;
				}
			}
			mapByDb.put(key, excelvo);
		}
		
		for (int voIndex = 0; voIndex < voList.size(); voIndex++) {

			AbstractVO abstractVO = voList.get(voIndex);
			String key = "";
			boolean hasDb = false;
			for (String pk : pkmap.keySet()) {
				Object object = abstractVO.get(pk);
				if (object != null) {
					key += object;
				}
			}
			// 更新赋值主键 新增不处理
			if (!StringUtils.isEmpty(key) && mapByDb.containsKey(key)) {

				int update_index = voList.indexOf(abstractVO);
				voList.remove(update_index);
				AbstractVO dbvo = mapByDb.get(key);
				abstractVO.set(pkName, dbvo.get(pkName));
				voList.add(update_index, abstractVO);
				hasDb = true;
			}
			if (!hasDb)
				insertList.add(abstractVO);
		}
    }
    /**
     * 校验excel 数据重复
     * 
     * @param pkmap
     * @param voList
     * @param errorList
     * @return
     */
    private  List<Message> checkExcelDuplication( Map<String, String> pkmap, List<AbstractVO> voList,List<Message> errorList){
    	
    	HashMap<String, AbstractVO> map = new HashMap<>();
		for (int voIndex = 0; voIndex < voList.size(); voIndex++) {
			AbstractVO excelvo = voList.get(voIndex);
			String key = "";
			for (String pk : pkmap.keySet()) {
				Object object = excelvo.get(pk);
				if (object != null) {
					key += object;
				}
			}
			map.put(key, excelvo);
		}
		for (int voIndex = 0; voIndex < voList.size(); voIndex++) {

			AbstractVO excelvo = voList.get(voIndex);
			String key = "";
			for (String pk : pkmap.keySet()) {
				Object object = excelvo.get(pk);
				if (object != null) {
					key += object;
				}
			}
			if (!StringUtils.isEmpty(key) && map.containsKey(key)) {

				AbstractVO abstractVO2 = map.get(key);
				int isRepeat = abstractVO2.getInt("isRepeat@_temp", 0);
				if (isRepeat >= 1) {
					Message message = new Message("上传文档第" + excelvo.getInt("rowNum") + "行与第"
							+ abstractVO2.getInt("rowNum") + "行" + pkmap.values().toString() + "重复！",
							MessageLevel.ERROR);
					errorList.add(message);
				} else {
					abstractVO2.setInt("isRepeat@_temp", isRepeat + 1);
				}
			}
		}
		return errorList;
    }

    private ReturnResult<List<AbstractVO>> handleVoList(String funcCode, List<AbstractVO> voList,
                                                        ExcelTemplateVO excelTemplateVO,
                                                        List<ExcelTemplateItemVO> excelTemplateItemVOs, Map<String, List<AbstractVO>> funcToVoInsertListMap, FormParamVO formParamVO, Long tenantid) {

        ReturnResult<List<AbstractVO>> returnResult = new ReturnResult<>();
        List<Message> errorList = new ArrayList<>();
        List<AbstractVO> insertList = new ArrayList<>();
        //更新 根据标识的主键字段 判断
        Map<String, String> pkmap = new HashMap<>();
        Map<String,List<Object>> pkValues = new HashMap<>();
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        //拿到主键字段的的定义
        for (ExcelTemplateItemVO excelTemplateItemVO : excelTemplateItemVOs) {
            if (funcCode.equals(excelTemplateItemVO.getFunc_code()) && ObjectUtil.isTrue(excelTemplateItemVO.getIs_pk())) {
                pkmap.put(excelTemplateItemVO.getItem_code(),
                        excelTemplateItemVO.getColumn_title());
                pkValues.put(excelTemplateItemVO.getItem_code(),new ArrayList<>());
            }
        }

        //对于子表直接导入的获取当前数据主表主键并赋值
        //处理主功能数据时不走这里
        Long parentEntityId = formParamVO.getReqParamVO().getParentEntityId();
        if (null != parentEntityId) {
            String parentFuncCode = formParamVO.getReqParamVO().getParentFuncCode();
            FuncLinkVO funcLinkVO = funcLinkService.getFuncLink(parentFuncCode, funcCode, tenantid).get(0);
            if (null != funcLinkVO) {
                List<FuncLinkItemVO> itemVOList = funcLinkVO.getLinkItems();
                if (ObjectUtil.isNotEmpty(itemVOList)) {
                    int length = itemVOList.size();
                    String tableName = funcService.getOperaTableNameOfFunc(parentFuncCode, tenantid);
                    AbstractVO bizParentvo = baseService.queryByPKAndTenantid(tableName, parentEntityId, tenantid);
                    for (FuncLinkItemVO itemVO : itemVOList) {
                        length = length - 1;
                        if (ObjectUtil.isTrue(itemVO.getLitem_isvalue())) {
                            String litem_sub_field = itemVO.getLitem_sub_field();
                            String litem_main_field = itemVO.getLitem_main_field();
                            if (ObjectUtil.isTrue(itemVO.getLitem_isconstant())) {
                                for (AbstractVO vo : voList) {//如果是常量，直接赋值
                                    vo.set(litem_sub_field, litem_main_field);
                                }
                            } else {
                                Object value = bizParentvo.get(litem_main_field);
                                if (pkmap.size()>0) {
                                	pkmap.put(litem_sub_field, litem_main_field);
								}
                                for (AbstractVO vo : voList) {
                                    vo.set(litem_sub_field, value);
                                }
                            }
                        }
                    }
                }
            }
        }
        String tableName = funcService.getOperaTableNameOfFunc(funcCode, tenantid);
        TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);//查询表信息
        String pkName = tableDefVO.getPkColumnVO() != null ? tableDefVO.getPkColumnVO().getColcode() : null;
        if (!StringUtils.hasLength(pkName)) {
            Message message = new Message("table:" + tableName + "的主键列没有找到", MessageLevel.ERROR);
            errorList.add(message);
            returnResult.setMessages(errorList);
            return returnResult;
        }
        //如果不含主键字段，全部插入
        if (ObjectUtil.isNotEmpty(pkmap)) {
            //遍历list  找到主键列重复数据
            Set<Integer> cpSet = new HashSet<>();
            //excel中录入的数据，是否存在重复。
            for (int voIndex = 0; voIndex < voList.size(); voIndex++) {
                AbstractVO excelvo = voList.get(voIndex);
                if (cpSet.contains(voIndex)) continue;
                int rowIndex = excelvo.getInt("rowNum");
                for (int i = 0; i < voList.size(); i++) {
                    AbstractVO temp = voList.get(i);
                    int rowIndexTemp = temp.getInt("rowNum");
                    if (voIndex != i && compareTo(temp, excelvo, pkmap.keySet())) {//跟之前数据主键相同
                        Message message = new Message("上传文档第" + rowIndex + "行与第" +
                                rowIndexTemp + "行" + pkmap.values().toString() + "重复！", MessageLevel.ERROR);
                        errorList.add(message);
                        cpSet.add(i);
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(errorList)) {
                returnResult.setMessages(errorList);
                return returnResult;
            }
            List<String> colCodes = tableDefVO.getColumnList().stream().map(TableColumnVO::getColcode).collect(Collectors.toList());
            //根据模板配置的主键及组装vo对应主键的值 拼装sql去数据查询数据,减少查询数据量
            for (Entry<String, List<Object>> entry : pkValues.entrySet()) {
                if (colCodes.contains(entry.getKey().toLowerCase())) {
                    List<Object> values = voList.stream().map(x -> x.get(entry.getKey())).collect(Collectors.toList());
                    dynaSqlVO.addWhereParam(entry.getKey(), values);
                }
            }
            dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, CurrentEnvUtils.getTenantId());
            List<AbstractVO> dblist = baseService.query(tableName, dynaSqlVO).getVoList();
            //遍历模板数据
            if (ObjectUtil.isNotEmpty(dblist)) {
            	//获取自增编号字段
                List<? extends FormItemVO> autoSerialNumberItems = formItemService.getAutoSerialNumberItem(funcCode, tenantid);
                List<String> autoSerialNumberFields = new ArrayList<String>();
                for(FormItemVO autoSerialNumberItem:autoSerialNumberItems){
                	autoSerialNumberFields.add(autoSerialNumberItem.getFitem_code());
                }
                for (int voIndex = 0; voIndex < voList.size(); voIndex++) {
                    AbstractVO excelvo = voList.get(voIndex);
                    boolean hasDb = false;
                    //遍历数据库数据
                    for (int i = 0; i < dblist.size(); i++) {
                        AbstractVO dbvo = dblist.get(i);
                        if (compareTo(dbvo, excelvo, pkmap.keySet())) {//数据库已存在
                            int update_index = voList.indexOf(excelvo);
                            voList.remove(update_index);
                            //设置主键列 方便update
                            excelvo.set(pkName, dbvo.get(pkName));
                            voList.add(update_index, excelvo);
                            //updateList.add(excelvo);
                            hasDb = true;
                            //如果该业务包含有编号字段，读取编号值
                            for(String autoSerialNumberField:autoSerialNumberFields){
                            	excelvo.set(autoSerialNumberField, dbvo.get(autoSerialNumberField));
                            }
                            break;
                        }
                    }
                    if (!hasDb) insertList.add(excelvo);
                }
            } else
                insertList.addAll(voList);
        } else {
            insertList.addAll(voList);
        }
        //赋主键
        String[] pks = serialGeneratorService.generate(tableName, insertList.size());
        for (int i = 0; i < insertList.size(); i++) {
            insertList.get(i).setLong(pkName, Long.parseLong(pks[i]));
        }
        funcToVoInsertListMap.put(funcCode, insertList);
        return returnResult;
    }

    private boolean compareTo(AbstractVO a, AbstractVO b, Collection<String> pks) {
        boolean boo = true;
        for (String pk : pks) {
            boo &= a.get(pk, false).equals(b.get(pk, false));
        }
        return boo;
    }

    /**
     * 插入或者更新导入数据
     *
     * @param funcToVoInsertListMap 导入数据列表
     * @param funcToVoAllListMap    需要更新数据列表
     * @param excelTemplateVO
     * @param excelTemplateItemVOs
     * @return 更新或插入一共条数
     * @throws HDException
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public ReturnResult<Integer> mergeTemplate(Map<String, List<AbstractVO>> funcToVoInsertListMap, Map<String, List<AbstractVO>> funcToVoAllListMap, ExcelTemplateVO excelTemplateVO,
                                               List<ExcelTemplateItemVO> excelTemplateItemVOs) throws HDException {
        ReturnResult<Integer> rr = new ReturnResult<Integer>();
        List<Message> msgList = new ArrayList<>();
        //插入和更新条数
        Integer rm = 0;
        for (Entry<String, List<AbstractVO>> entry : funcToVoAllListMap.entrySet()) {
            String funcCode = entry.getKey();
            List<AbstractVO> allList = entry.getValue();//当前功能要检验的list
            List<AbstractVO> insertList = funcToVoInsertListMap.get(funcCode);
            //插入集合
            List<AbstractVO> updateList = new ArrayList<>();
            updateList.addAll(allList);
            updateList.removeAll(insertList);

            try {
                if (ObjectUtil.isTrue(ObjectUtil.asInteger(excelTemplateVO.getIsupdate()))) {
                    if (ObjectUtil.isNotEmpty(updateList)) {
                        List<String> sqlColumnList = new ArrayList<>();
                        DynaSqlVO dynaSqlVO = new DynaSqlVO();
                        dynaSqlVO.setSqlColumnList(sqlColumnList);
                        if (null != excelTemplateVO.get("updateList")) {
                            sqlColumnList.addAll((List<String>) excelTemplateVO.get("updateList"));
                        }
                        for (ExcelTemplateItemVO item : excelTemplateItemVOs) {
                            if (funcCode.equals(item.getFunc_code()) && !sqlColumnList.contains(item.getItem_code().toLowerCase())) {
                                sqlColumnList.add(item.getItem_code().toLowerCase());
                            }
                        }
                        //rm += baseService.updateBatch(updateList, dynaSqlVO);
                        batch(updateList,rm,dynaSqlVO);
                    }
                }
                if (ObjectUtil.isNotEmpty(insertList)) {
                    for (AbstractVO vo : insertList) {
                        handleSerialNumberAuto(vo, funcCode);
                    }
                    rm += baseService.insertBatchHavePks(insertList).getVoList().size();
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                Message message = new Message("数据库操作异常" + e.getMessage(), MessageLevel.ERROR);
                msgList.add(message);
                rr.setMessages(msgList);
                rr.setData(rm);
                return rr;
            }
        }
        Message message = new Message(rm.toString(), MessageLevel.INFO, MessageShowType.FLOW);
        msgList.add(message);
        rr.setMessages(msgList);
        return rr;
    }
    /**
     * 分批更新
     * @param updateList
     * @param num 
     * @author haocs
     * @date 2020年2月4日
     */
    private  void batch(List<AbstractVO> updateList,Integer num,DynaSqlVO dynaSqlVO){
    	
    	int nums = updateList.size();
    	int updateNum = 0;
    	if(nums>2000) {
    		updateNum = nums/2000;
    		
    			for(int i = 0 ; i<updateNum ; i++) {
    				List<AbstractVO> sub = updateList.subList(0, i*2000);
    				int updateBatch = baseService.updateBatch(sub, dynaSqlVO);
    				num += updateBatch;
    			}
    			if(nums%2000>0) {
    				List<AbstractVO> sub = updateList.subList(updateNum*2000, nums);
        			int updateBatch = baseService.updateBatch(sub, dynaSqlVO);
    				num += updateBatch;
    			}
    			
    	}else {
    		int updateBatch = baseService.updateBatch(updateList, dynaSqlVO);
			num += updateBatch;
    	}
    	
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

    /**
     * 处理自动编码
     *
     * @param t
     * @param funcCode
     * @return
     * @throws HDException
     * @author zhangfeng
     * @date 2015年12月18日
     */
    private <T extends AbstractVO> T handleSerialNumberAuto(T t, String funcCode) throws HDException {
        Long tenantid = CurrentEnvUtils.getTenantId();
        List<? extends FormItemVO> list = formItemService.getAutoSerialNumberItem(funcCode, tenantid);
        for (FormItemVO vo : list) {
            Object val = t.get(vo.getFitem_code());
            if (val == null || "".equals(val) || "<系统自动生成>".equals(val)) {
                String value = billCodeService.generalDocNum(t, funcCode, vo.getFitem_code());
                t.set(vo.getFitem_code(), value);
            }
        }
        return t;
    }

    @Override
    public List<ExcelTemplateItemVO> getAllItems(ReqParamVO paramVO, Long tenantid, String funcCode) throws HDException {
        Long templateid = paramVO.getParentEntityId();
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("templateid", templateid);
        dynaSqlVO.addWhereClause("column_no is not NULL");
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);

        dynaSqlVO.setOrderByClause(" cast(column_no as signed) asc");
        VOSet<ExcelTemplateItemVO> voset = baseService.query(ExcelTemplateItemVO.class, dynaSqlVO);
        return voset.getVoList();
    }

    @Override
    @Transactional
    public void updateOrder(List<Long> ids) throws HDException {
        Long tenantid = CurrentEnvUtils.getTenantId();
        if (!ObjectUtil.isNotEmpty(ids)) return;

        List<ExcelTemplateItemVO> voList = new ArrayList<ExcelTemplateItemVO>();
        for (long i = 1; i <= ids.size(); i++) {
            ExcelTemplateItemVO vo = new ExcelTemplateItemVO();
            Long id = ids.get((int) i - 1);
            vo.setItemid(id);
            vo.setColumn_no(i + "");
            voList.add(vo);
        }
        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        List<String> columnList = new ArrayList<String>();
        columnList.add("column_no");
        dynaSqlVO.setSqlColumnList(columnList);
        dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);

        baseService.updateBatch(voList, dynaSqlVO);
    }

}
