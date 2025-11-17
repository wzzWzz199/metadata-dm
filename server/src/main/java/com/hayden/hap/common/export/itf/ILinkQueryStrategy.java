package com.hayden.hap.common.export.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.formmgr.itf.IListFormService;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.spring.service.AppServiceHelper;

import java.util.List;

/**
 * 关联导入导出查询策略
 * 
 * @author liyan
 * @date 2017年6月29日
 */
public interface ILinkQueryStrategy {
	
	IListFormService listFormService = (IListFormService) AppServiceHelper.findBean("listFormService");
	IBaseService baseService = (IBaseService) AppServiceHelper.findBean("baseService");
	ITableDefService tableDefService = (ITableDefService) AppServiceHelper.findBean("tableDefService");
	IFormItemService formItemService = (IFormItemService) AppServiceHelper.findBean("formItemService");
	IFuncService funcService = (IFuncService) AppServiceHelper.findBean("funcService");
	
	/**
	 * 得到设置了查询的where子句的DynaSqlVO
	 * @param voList
	 * @param funcLinkVO
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月17日
	 */
	public List<DynaSqlVO> getWhereCauseStrategy(List<? extends AbstractVO> voList,FuncLinkVO funcLinkVO) throws HDException;

	/**
	 * 得到子表voList
	 * @param voList
	 * @param funcLinkVO
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月17日
	 */
	public List<? extends AbstractVO> getSubVoList(List<? extends AbstractVO> voList,
			FuncLinkVO funcLinkVO, Long tenantid) throws HDException;

	/**
	 * 得到将子表voList分别放入对应主表中的主表voList
	 * @param voList
	 * @param funcLinkVO
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月17日
	 */
	public List<? extends AbstractVO> getParentAndSubVoList(List<? extends AbstractVO> voList,
			FuncLinkVO funcLinkVO, Long tenantid) throws HDException;


}
