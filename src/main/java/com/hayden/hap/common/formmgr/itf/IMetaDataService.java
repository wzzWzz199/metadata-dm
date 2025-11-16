/**
 * 
 */
package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.MetaData;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author zhangfeng
 *
 */
@IService("metaDataService")
public interface IMetaDataService {

	/**
	 * 获取功能页面结构数据
	 * @param formParamVO
	 * @param needListData 是否需要加载列表数据
	 * @return
	 * @throws HDException
	 * @author zhangfeng
	 * @date 2016年10月12日
	 */
	public MetaData getMetaData(FormParamVO formParamVO, boolean needListData) throws HDException;
	
	/**
	 * 获取查询选择页面的结构数据
	 * @param funcCode
	 * @param colCode
	 * @return 
	 * @author zhangfeng
	 * @date 2016年10月12日
	 */
	public MetaData getQuerySelectorMetaData(FormParamVO formParamVO,Long tenantid, Long userid) throws HDException;
	
	/**
	 * 获取功能选择结构数据
	 * @param formParamVO
	 * @param tenantid
	 * @param userid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年1月19日
	 */
	public MetaData getFuncSelectorMetaData(FormParamVO formParamVO, Long tenantid, Long userid) throws HDException;
	
	/**
	 * 获取主功能的列表数据
	 * @param funcCode
	 * @return
	 * @throws HDException
	 */
	public VOSet<? extends AbstractVO> getListData(FormParamVO formParamVO,Long tenantid) throws HDException;
	
	/**
	 * 根据功能编码获取字典数据
	 * @param funcCode
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月14日
	 */
	public Map<String,List<DictDataWarperVO>> getDictMap(String funcCode,Long tenantid) throws HDException;
	
	/**
	 * 跳转页面 ，获取结构数据
	 * @param formParamVO
	 * @param needListData
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月25日
	 */
	public MetaData getMetaDataForPopPage(FormParamVO formParamVO) throws HDException;
}
