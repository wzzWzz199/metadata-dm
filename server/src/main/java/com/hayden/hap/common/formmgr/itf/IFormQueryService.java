package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.form.entity.FormQueryItemVO;
import com.hayden.hap.common.form.entity.FormQueryVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
//import com.hayden.hap.sy.formmgr.entity.ReturnResult;

/**
 * 
 * @author zhangfeng
 * @date 2016年3月18日
 */
@IService("formQueryService")
public interface IFormQueryService {

	public  VOSet<? extends AbstractVO> listQuery(FormParamVO formParamVO,String fromfuncCode) throws HDException;
	
//	/**
//	 * 显示高级查询
//	 * @param formParamVO
//	 * @return
//	 * @throws HDException 
//	 * @author zhangfeng
//	 * @date 2016年3月15日
//	 */
//	public Map<String,Object> getFormQueryViewModel(FormParamVO formParamVO) throws HDException;
//	
//	
//	public Map<String,Object> getCardViewModel(FormParamVO formParamVO,boolean readOnly) throws HDException;
	
	/**
	 * 保存查询策略
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年3月29日
	 */
	public ReturnResult<?> save(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 将高级查询项转换成标准sql
	 * @param funcCode
	 * @param items
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年3月31日
	 */
	public String handleQuerySQL(String funcCode,List<FormQueryItemVO> items,Long tenantid);
	
	public List<FormQueryVO> getFormQueryByFunc(String funcCode,Long userid,Long tenantid);
	
	public List<FormQueryVO> getFormQueryByFunc(String funcCode,Long tenantid);
	
	/**
	 * 获取查询模板标准sql
	 * @param funcCode
	 * @param tenantid
	 * @param formQueryId
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月1日
	 */
	public String getFormQuerySql(String funcCode,Long tenantid,Long formQueryId);
	
//	public List<ButtonVO> getListViewBtns(Long tenantid);
//	
//	public List<ButtonVO> getCardViewBtns(Long tenantid);
}
