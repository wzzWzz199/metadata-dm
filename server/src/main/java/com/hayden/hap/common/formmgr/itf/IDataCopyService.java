package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

/**
 * 数据复制服务
 * @author zhangfeng
 * @date 2017年4月13日
 */
@IService("dataCopyService")
public interface IDataCopyService {

	/**
	 * 原始主键
	 */
//	public static final String OLD_PK = "old__id";
	
	/**
	 * 批量复制
	 * @param formParamVO
	 * @param originalList
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月13日
	 */
	List<AbstractVO> batchCopy(String funcCode, List<AbstractVO> originalList, Long tenantid) throws HDException;
	
	/**
	 * 复制
	 * @param formParamVO
	 * @param originalVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月13日
	 */
	AbstractVO copy(FormParamVO formParamVO, AbstractVO originalVO, Long tenantid) throws HDException;
	
	/**
	 * 关联复制
	 * @param funcCode
	 * @param tenantid
	 * @param voList 
	 * @author zhangfeng
	 * @date 2017年4月18日
	 */
	void linkCopy(String funcCode,Long tenantid,List<AbstractVO> voList) throws HDException;
	
	/**
	 * 主键置空
	 * @param vo
	 * @param pkColName
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月18日
	 */
	AbstractVO setNullPrimaryKey(AbstractVO vo, String pkColName);
}
