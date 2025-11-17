package com.hayden.hap.common.dict.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.dict.entity.DictVO;
import com.hayden.hap.common.dict.entity.DictVersionWarper;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;
import java.util.Map;
//import com.hayden.hap.sy.formmgr.entity.ReturnResult;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月7日
 */
@IService("dictDataService")
public interface IDictDataService {

	public List<DictDataVO> getDictData(String dictCode,Long tenantid);
	
	public List<DictDataVO> getDictData(String funcCode,String dictCode,Long tenantid);
	
	/**
	 * 获取带有顶点节点的字典数据（顶点节点为虚拟节点，name为字典名称，id为0，code为'@-1'）
	 * @param dictCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月26日
	 */
	public List<DictDataVO> getDictDataWithTopnode(String dictCode,Long tenantid);
	/**
	 * 获取带有顶点节点的字典数据（顶点节点为虚拟节点，name为字典名称，id为0，code为'@-1'）
	 * 带数据权限
	 * @param dictCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月26日
	 */
	public List<DictDataVO> getDictDataWithTopnode(String funcCode,String dictCode,Long tenantid);
	
	public List<DictDataVO> getDictData(String dictCode,Long tenantid,String extWhere);
	
	
	
//	/**
//	 * 返回列表
//	 * @param formParamVO
//	 * @return
//	 * @throws HDException 
//	 * @author zhangfeng
//	 * @date 2016年1月18日
//	 */
//	public ModelAndView back2List(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 根据字典查树形字典数据
	 * @param dictVO 字典对象
	 * @param extWhere 额外条件
	 * @param isEnableFlag 是否只查启用的数据-
	 * @author zhangfeng
	 * @date 2016年1月11日
	 */
	public List<DictDataVO> getTreeData(DictVO dictVO, String extWhere, boolean isEnableFlag);
	
	/**
	 * 给一组字典数据添加一个顶点节点
	 * @param list 字典数据集合
	 * @param itemCode 字段编码
	 * @param itemName 字段名称
	 * @return 
	 * @author zhangfeng
	 * @date 2016年9月1日
	 */
	public List<DictDataWarperVO> addTopnode(List<DictDataWarperVO> list,String itemCode,String itemName);
	
	/**
	 * 将业务对象转换成字典数据对象
	 * @param dictVO
	 * @param dictTableName
	 * @param businessVOs
	 * @return 
	 * @author zhangfeng
	 * @date 2017年3月8日
	 */
	List<DictDataVO> businessVO2DictDataVO(DictVO dictVO,String dictTableName,List<AbstractVO> businessVOs);
	
	/**
	 * 根据字典版本获取字典数据<br/>
	 * 当参数中字典版本与服务器字典不匹配的时候，提供相关字典数据，同时状态为updated，否则不提供,状态为缓存状态cached<br/>
	 * <font color="red">注意：在2.0大版本中，任何时候都是updated，并提供数据</font>
	 * @since 2.0.8
	 * @param list
	 * @return 
	 * @author zhangfeng
	 * @date 2018年1月30日
	 */
	List<DictVersionWarper> getDictVersionWarpers(List<DictVersionWarper> list, Long tenantid);

	/**
	 * 获取表单下的字典
	 * @param itemVOs
	 * @param funcTree
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2018年1月31日
	 */
	public Map<String, List<DictDataWarperVO>> getDictMap(
			List<? extends FormItemVO> itemVOs, String funcTree, Long tenantid)
			throws HDException;

	/**
	 * 获取表单下的字典 不考虑funcTree
	 * @param itemVOs
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author liyan
	 * @date 2018年1月31日
	 */
	public Map<String, List<DictDataWarperVO>> getDictMap(
			List<? extends FormItemVO> itemVOs, Long tenantid)
			throws HDException; 
}
