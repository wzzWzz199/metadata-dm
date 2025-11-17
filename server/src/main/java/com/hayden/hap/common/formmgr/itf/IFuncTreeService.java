package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 全息树相关服务接口
 * @author zhangfeng
 * @date 2016年12月20日
 */
@IService("funcTreeService")
public interface IFuncTreeService {

	/**
	 * 解析全息查询条件
	 * @param queryString
	 * @param funcCode
	 * @param formItemVOs
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月22日
	 */
	public String parseFuncTreeQueryStr(String queryString,String funcCode,
			List<? extends FormItemVO> formItemVOs,Long tenantid) throws HDException;
	
	/**
	 * 当前功能配置的全息查询字段，那个字段对应的字典是当前功能维护的，哪些不是<br/>
	 * 键为表单字段的字段编码，值为是or否
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月20日
	 */
	public Map<String,Boolean> getTreeMaintenanceMap(String funcCode, Long tenantid) throws HDException;
	
	/**
	 * 根据全息查询条件，赋值一些属性初始值
	 * @param vo
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年12月20日
	 */
	public AbstractVO assignFunctreeParam(AbstractVO vo, FormParamVO formParamVO, 
			Long tenantid) throws HDException;
	
	/**
	 * 找到当前功能的parentid字段（已知当前功能是树的维护的前提下）
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月20日
	 */
	public String getParentidColumnName(String funcCode,Long tenantid) throws HDException;
	
	/**
	 * 找到当前功能的编码字段（已知当前功能是树的维护的前提下）
	 * @param funcCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年12月20日
	 */
	public String getCodeColumnName(String funcCode,Long tenantid) throws HDException;
}
