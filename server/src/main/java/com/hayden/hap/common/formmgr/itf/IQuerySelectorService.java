package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.CommonVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月23日
 */
@IService("querySelectorService")
public interface IQuerySelectorService {
	
	/**
	 * pc端列查询选择的列表查询
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月19日
	 */
	public VOSet<? extends AbstractVO> querySelectorListQuery(FormParamVO formParamVO) throws HDException ;
	
	/**
	 * pc端查询选择统一接口，能干三件事，获取结构数据、列表查询、全息查询树数据
	 * @param formParamVO
	 * @param operaType 操作类型：获取结构数据、列表查询、全息查询树数据
	 * @param tenantid 租户id
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年6月7日
	 */
	public ReturnResult<?> querySelector(FormParamVO formParamVO, Map<String,String> item2FuncMap, Long userid, Long tenantid) throws HDException ;
	
	/**
	 * 移动端查询选择的列表查询
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月19日
	 */
	public List<CommonVO> querySelectorMListQuery(FormParamVO formParamVO) throws HDException ;

	/**
	 * 解析查询选择的功能编码<br/>
	 * 由于存在查询选择嵌套，这里的字段编码是由逗号隔开的各个字段编码，最上层的查询选择字段在最后
	 * @param formParamVO
	 * @param fitemCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月12日
	 */
	String parseQueryselectFuncCode(FormParamVO formParamVO, Long tenantid, Map<String,String> item2FuncMap) throws HDException;
}
