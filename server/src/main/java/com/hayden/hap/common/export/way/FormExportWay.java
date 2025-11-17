package com.hayden.hap.common.export.way;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.enumerate.ExportWayType;
import com.hayden.hap.common.excel.entity.ExcelTemplateItemVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.export.entity.TemplateVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormItemPCService;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.inputconfig.DictInputConfigVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.reflect.ClassInfo;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.utils.DictUtils;
import com.hayden.hap.common.utils.ListUtil;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.template.TemplateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author suntaiming
 * @Date 2021/6/8 14:51
 **/
public abstract class FormExportWay extends ImportExportWay {
    protected IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
    protected IFormItemPCService formItemPCService = AppServiceHelper.findBean(IFormItemPCService.class);
    protected IFormItemService formItemService = AppServiceHelper.findBean(IFormItemService.class);
    protected IFormService formService = AppServiceHelper.findBean(IFormService.class);


    private static final Logger logger = LoggerFactory.getLogger(FormExportWay.class);
    protected abstract Workbook getWorkbook();
    /**
     * 导出
     *
     * @param templateVO
     * @param exportVoList
     * @param formParamVO
     * @param funcnama
     * @param tenantid
     * @param exportids
     * @param exportcountb
     * @param exportcounte
     * @return
     * @throws HDException
     * @author liyan
     * @date 2017年6月29日
     */
    @Override
    public ReturnResult<InputStream> getExportInputStream(TemplateVO templateVO, List<? extends AbstractVO> exportVoList, FormParamVO formParamVO, String funcnama, Long tenantid, String exportids, int exportcountb, int exportcounte) throws HDException {
        Workbook wb = getWorkbook();
        Sheet type_sheet = wb.createSheet(ExportWayType.FORM.getCode());

        //设置强制隐藏，不能通过excel模板显示页签
        wb.setSheetHidden(wb.getSheetIndex(type_sheet), Workbook.SHEET_STATE_VERY_HIDDEN);
        String func_code = formParamVO.getFuncCode();

        //构建template
        this.buildTemplateVO(templateVO, func_code, tenantid);

        return export(wb, exportVoList, templateVO.getExcelTemplateVO(), templateVO.getExcelTemplateVOList(), tenantid,
                exportids, exportcountb, exportcounte);
    }


    @Override
    public Workbook getExportToWorkBook(Workbook wb, TemplateVO templateVO,
                                        List<? extends AbstractVO> voList, String funcnama, String func_code, Long tenantid, int exportcountb,
                                        int exportcounte, boolean initFirstRow, HashMap<String, String> dictmap) throws HDException {
        //构建template
        this.buildTemplateVO(templateVO, func_code, tenantid);
        return getExportToWorkBook(wb, templateVO.getExcelTemplateVO(), templateVO.getExcelTemplateVOList(), voList, funcnama, func_code, tenantid, exportcountb, exportcounte, initFirstRow, dictmap);
    }

    private AbstractVO getDynamicVO(String dynamicParam,TableDefVO funcTableDefVO) throws HDException{
        // 功能对应的entityVO类，没有默认为baseVO
        Class<? extends AbstractVO> funcVoClass = TemplateUtils.getVOClass(funcTableDefVO);
        AbstractVO vo = null;
        if(StringUtils.isBlank(dynamicParam)){
            vo = ClassInfo.newInstance(funcVoClass);
        }else {
            vo = JsonUtils.parse(dynamicParam, funcVoClass);
        }
        return vo;
    }

    private TemplateVO buildTemplateVO(TemplateVO templateVO,  String func_code, Long tenantid) throws HDException{
        String dynamicParam = templateVO.getDynamicParam();
        String funcTableName = funcService.getOperaTableNameOfFunc(func_code, tenantid);
        TableDefVO funcTableDefVO = tableDefService.queryDetailedTableByTbname(funcTableName);// 操作表信息

        // 功能对应的entityVO类，没有默认为baseVO
        AbstractVO vo = getDynamicVO(dynamicParam, funcTableDefVO);
        vo.set(SyConstant.TENANT_STR, tenantid);

        FormVO formVO = formService.getFormVOByFunccode(func_code, tenantid);

        //传入vo，动态获取对应items,并且父级包含子级所有的字段，且不含隐藏字段
        List<? extends FormItemVO> formItemVOS = formItemPCService.getDynaExportFormItemsByFuncCode(func_code, vo);

        ExcelTemplateVO excelTemplateVO = TemplateUtils.transformExcelTemplateVO(formVO, func_code, templateVO.getFileType());
        List<ExcelTemplateItemVO> excelTemplateItemVOs = TemplateUtils.transformExcelTemplateItemVOs(formItemVOS, funcTableDefVO, func_code);
        templateVO.setExcelTemplateVO(excelTemplateVO);
        templateVO.setExcelTemplateVOList(excelTemplateItemVOs);
        return templateVO;
    }

    @Override
    public HttpServletResponse resetHeader(HttpServletRequest request, HttpServletResponse response, String func_code,String funcnama,
                                           String suf, Long tenantid,TemplateVO...t) throws UnsupportedEncodingException {

        StringBuilder fileName = new StringBuilder();
        if (t != null && t.length > 0){
            FormVO formVO = formService.getFormVOByFunccode(func_code, tenantid);
            List<? extends FormItemVO> formItemVOS = formItemPCService.getFormItemsByFunccode(func_code, tenantid);

            TemplateVO templateVO = t[0];

            fileName.append(formVO.getForm_name());
            String dynamicParam = templateVO.getDynamicParam();
            if(StringUtils.isNotBlank(dynamicParam)){
                try {
                    Map<String, String> map = JsonUtils.parse(dynamicParam, Map.class);
                    for (String key : map.keySet()){
                        FormItemVO formItemVO = ListUtil.first(formItemVOS, (x)->{ return x.getFitem_code().equals(key);});
                        if(formItemVO == null || StringUtils.isBlank(formItemVO.getFitem_input_config())){
                            logger.error("【构建导出文件名称】 表单字段{}未找到或者未配置字典配置", key);
                            continue;
                        }
                        DictInputConfigVO dictInputConfigVO = InputConfigUtils.getDictInputConfigVO(formItemVO.getFitem_input_config());
                        String dictCode = dictInputConfigVO.getDictcode();
                        if(StringUtils.isBlank(dictCode)){
                            logger.error("【构建导出文件名称】字段{}输入设定字典配置有误", key);
                            continue;
                        }
                        List<DictDataWarperVO> dictDataVoList = DictUtils.getDictData(dictCode);
                        List<DictDataWarperVO> allDataList = getAllDataList(dictDataVoList);
                        String value = map.get(key);
                        DictDataWarperVO dictDataWarperVO = ListUtil.first(allDataList, (x)->{ return x.getCode().equals(value);});
                        if(dictDataWarperVO != null){
                            fileName.append("_")
                                    .append(dictDataWarperVO.getName());
                        }

                    }
                } catch (HDException e) {
                    logger.error("dynamicParam json解析异常", e);
                }
            }

        }else {
            fileName.append(funcnama);
        }

        return super.resetHeader(request, response, fileName.toString(), suf);
    }

}
