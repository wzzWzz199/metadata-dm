package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.attach.entity.AttachDataVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.exception.ShouldBeCatchException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.excel.entity.ExcelTemplateVO;
import com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.MetaData;
import com.hayden.hap.common.formmgr.entity.PureVO;
import com.hayden.hap.common.formmgr.message.Message;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author zhangfeng
 * @date 2015年9月25日
 */
public interface IAction {

	/**
	 * 修改或删除前处理
	 * @param request
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年9月25日
	 */
	public List<Message> beforeCardSave(FormParamVO formParamVO,AbstractVO vo) throws HDException;
	
	/**
	 * 修改或删除后处理
	 * @param request
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @date 2015年9月25日
	 */
	public List<Message> afterCardSave(FormParamVO formParamVO,AbstractVO vo,boolean isAdd) throws HDException; 
	
	/**
	 * 批量更新前处理
	 * @param formParamVO
	 * @param batchUpdateParamVO 
	 * @author zhangfeng
	 * @date 2015年11月10日
	 */
	public List<Message> beforeListUpdate(FormParamVO formParamVO,BatchUpdateParamVO batchUpdateParamVO) throws HDException;
	
	/**
	 * 批量更新后处理
	 * @param formParamVO
	 * @param batchUpdateParamVO 
	 * @author zhangfeng
	 * @date 2015年11月10日
	 */
	public List<Message> afterListUpdate(FormParamVO formParamVO,BatchUpdateParamVO batchUpdateParamVO) throws HDException;
	
	/**
	 * 删除前处理
	 * @param formParamVO
	 * @author zhangfeng
	 * @date 2015年9月25日
	 */
	public List<Message> beforeListDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys) throws HDException;
	
	/**
	 * 删除后处理
	 * @param formParamVO
	 * @author zhangfeng
	 * @date 2015年9月25日
	 */
	public List<Message> afterListDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys) throws HDException;
	
	/**
	 * 初始化卡片VO
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年2月9日
	 */
	public AbstractVO initCardVO(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 获取卡片VO后操作
	 * @param vo
	 * @param formParamVO
	 * @param readonly
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年2月9日
	 */
	public void afterGetCardVO(AbstractVO vo, FormParamVO formParamVO, boolean readonly) throws HDException;
	
	/**
	 * 改变卡片只读状态
	 * @param vo
	 * @param formParamVO
	 * @param readonly
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年9月22日
	 */
	public boolean changeCardReadonly(AbstractVO vo, FormParamVO formParamVO, boolean readonly) throws HDException;
	
	/**
	 * 获取卡片VO前操作
	 * @param vo
	 * @param formParamVO
	 * @param readonly
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年2月9日
	 */
	public void beforeGetCardVO(AbstractVO vo, FormParamVO formParamVO, boolean readonly) throws HDException;
	
//	/**
//	 * 进入卡片前处理
//	 * @param formParamVO
//	 * @param model 视图模型
//	 * @author zhangfeng
//	 * @date 2016年3月1日
//	 */
//	@Deprecated
//	public void beforeCardView(FormParamVO formParamVO,AbstractVO editVO,List<FormItemVO> allFormItemVOs);
	
//	/**
//	 * 进入卡片前处理
//	 * @param formParamVO
//	 * @param model 视图模型
//	 * @author zhangfeng
//	 * @date 2016年3月1日
//	 */
//	@Deprecated
//	public void afterCardView(FormParamVO formParamVO,Map<String,Object> model);
	
	/**
	 * 复制前操作
	 * @param formParamVO
	 * @param copyList
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年3月7日
	 */
	public List<Message> beforeListCopy(FormParamVO formParamVO,List<? extends AbstractVO> copyList) throws HDException;
	
	/**
	 * 复制后操作
	 * @param formParamVO
	 * @param copyList
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年3月7日
	 */
	public List<Message> afterListCopy(FormParamVO formParamVO,List<? extends AbstractVO> copyList) throws HDException;
	
	/**
	 * 复制前操作
	 * @param formParamVO
	 * @param copyList
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年3月7日
	 */
	public List<Message> beforeCardCopy(FormParamVO formParamVO,AbstractVO copyVO) throws HDException;
	
	/**
	 * 卡片复制后操作
	 * @param formParamVO
	 * @param copyVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月18日
	 */
	List<Message> afterCardCopy(FormParamVO formParamVO,AbstractVO copyVO) throws HDException;
	
	/**
	 * 列表查询前
	 * @param formParamVO
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年5月25日
	 */
	public void beforeListQuery(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 改变列表查询，不再走平台的列表查询
	 * @param dynaSqlVO
	 * @param formParamVO
	 * @return
	 * @throws ShouldBeCatchException
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年9月26日
	 */
	public VOSet<? extends AbstractVO> changedListQuery(DynaSqlVO dynaSqlVO,FormParamVO formParamVO) 
			throws ShouldBeCatchException,HDException;
	
	/**
	 * 列表查询后
	 * @param formParamVO
	 * @param voset
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年5月25日
	 */
	public void afterListQuery(FormParamVO formParamVO,VOSet<? extends AbstractVO> voset) throws HDException;
	
	/**
	 * 显示打印页面前动作
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2016年8月11日
	 */
	public String beforeShowPrintView(FormParamVO formParamVO) throws HDException;
	
	
	/**
	 * 获取结构数据前操作
	 * @param formParamVO
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年3月21日
	 */
	void beforeGetMetaData(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 获取结构数据后操作
	 * @param formParamVO
	 * @param metaData
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年3月21日
	 */
	void afterGetMetaData(FormParamVO formParamVO,MetaData metaData) throws HDException;
	
	/**
	 * 是否需要部门权限
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2017年6月20日
	 */
	boolean isNeedOrgPermission(FormParamVO formParamVO);

	/**
	 * 导出附件图片前对将要导出的附件信息做一些修改，如修改文件名
	 * @param attachDataVos 附件voList
	 * @param tenantid
	 * @throws HDException 
	 * @author liyan
	 * @date 2017年7月24日
	 */
	void beforeExportAttachImage(List<AttachDataVO> attachDataVos, Long tenantid)
			throws HDException;
	
	/**
	 * 获得工作流具体实现action
	 * @return 
	 * @author liyan
	 * @throws ClassNotFoundException 
	 * @date 2018年1月22日
	 */
	Class<?> getWfAction() throws ClassNotFoundException;   
	
	/**
	 * 改变部门权限的查询条件
	 * @param orgPermissionCluase
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月8日
	 */
	String changeOrgPermissionClause(String orgPermissionClause, FormParamVO formParamVO);
	
	/**
	 * 获取业务操作接口
	 * @return 
	 * @author zhangfeng
	 * @date 2018年6月26日
	 */
	Class<?> getBusinessAction();
	/**
	 * 移动端获取结构数据后操作
	 * @param formParamVO
	 * @param metaData4M
	 * @throws HDException
	 */
	void afterGetMetaData4M(FormParamVO formParamVO,PureVO metaData4M) throws HDException;

	/**
	 * 导入 ：批量更新前处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList 主表volist
	 * @param mainAndSubMap 主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2017年7月20日
	 */
	List<Message> beforeMegerBatch(FormParamVO formParamVO, ExcelTemplateVO excelTemplateVO,
								   List<AbstractVO> mainList, LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap) throws HDException;

	/**
	 * 导入：对excel转换的vo进行插入更新赋主键等处理后，但是再插入数据库之前做的一些业务操作
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList 处理前的主表list
	 * @param mainAndSubMap 处理前的主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @param funcToVoInsertListMap  处理后的每个功能需要插入的list
	 * @param funcToVoAllListMap 处理后的每个功能需要处理的list，包括插入和更新
	 * @return
	 * @author liyan
	 * @date 2018年5月23日
	 */
	List<Message> afterDbBatch(
			FormParamVO formParamVO,
			ExcelTemplateVO excelTemplateVO,
			List<AbstractVO> mainList,
			LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap,
			Map<String, List<AbstractVO>> funcToVoInsertListMap,
			Map<String, List<AbstractVO>> funcToVoAllListMap) throws HDException;

	/**
	 * 导入：批量更新后处理主子表
	 * @param formParamVO
	 * @param excelTemplateVO
	 * @param mainList
	 * @param mainAndSubMap 主子表vo对应关系，key值为主功能编码+“.”+子功能编码，value是每个主表vo对应的子表voList
	 * @return
	 * @throws HDException
	 * @author liyan
	 * @date 2017年7月20日
	 */
	List<Message> afterMegerBatch(FormParamVO formParamVO,ExcelTemplateVO excelTemplateVO,
								  List<AbstractVO> mainList,LinkedHashMap<String, LinkedHashMap<AbstractVO, List<AbstractVO>>> mainAndSubMap) throws HDException;


}
