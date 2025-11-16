package com.hayden.hap.common.export.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.export.entity.ExportTemplateItemVO;
import com.hayden.hap.common.export.entity.ExportTemplateVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author liyan
 *
 */
public interface IExportVOMapper {

	/**
	 * 无模板导出
	 * @param list
	 * @return 
	 * @author liyan
	 * @param tenantid 
	 * @param funcnama 
	 * @param suf 
	 * @param exportids 
	 * @param exportcounte 
	 * @param exportcountb 
	 * @throws HDException 
	 * @date 2016年9月9日
	 */
	public HashMap<String, Object> exportExcel(List<? extends AbstractVO> list,FormParamVO formParamVO, String funcnama, Long tenantid, String exportids, String suf, int exportcountb, int exportcounte) throws HDException;

	/**
	 * 按模板导出
	 * @param exportTemplateItemVOs
	 * @param suf
	 * @param exportTemplateVO
	 * @param tenantid
	 * @param exportids
	 * @param exportcount
	 * @return 
	 * @author liyan
	 * @param voset 
	 * @param exportcounte 
	 * @param txt_separator 
	 * @throws HDException 
	 * @date 2016年9月18日
	 */	
	public HashMap<String, Object> exportExcelTemplate(
			List<? extends AbstractVO> voList, List<ExportTemplateItemVO> exportTemplateItemVOs, String suf,
			ExportTemplateVO exportTemplateVO, Long tenantid, String exportids,
			int exportcountb, int exportcounte, String txt_separator) throws HDException;

}
