package com.hayden.hap.common.excel.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;

/**
 * Created by hayden on 2016/12/8.
 */
public interface IExcelTemplateDataAction {

    /**
     *
     * @param formParamVO 数据传输对象
     * @param excelTemplateVO 数据传输对象
     * @param returnResult 数据传输对象
     * @return ReturnResult
     * @throws HDException
     */
    ReturnResult getResult (FormParamVO formParamVO, ExcelTemplateVO excelTemplateVO, ReturnResult returnResult)
        throws HDException;
}
