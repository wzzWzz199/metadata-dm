package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.dict.entity.DictDataWarperVO;
import com.hayden.hap.common.dict.entity.TreeNodeVO;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.formmgr.entity.CardEditVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.entity.ListDataVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.spring.service.IService;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.List;
//import com.hayden.hap.sy.formmgr.entity.ReturnResult;

/**
 * 
 * @author zhangfeng
 * @date 2015年11月3日
 */
@IService("listFormService")
public interface IListFormService {
	
	/**
	 * 列表页批量更新
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月4日
	 */
	public ReturnResult<ListDataVO> listUpdate(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 列表页面查询
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月4日
	 */
	public VOSet<? extends AbstractVO> listQuery(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 列表页面查询（移动端定制）
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月7日
	 */
	VOSet<? extends AbstractVO> listQuery4Mobile(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 查询选择的列表页面查询
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月4日
	 */
	public VOSet<? extends AbstractVO> listQuery4querySelect(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 全查
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2015年12月9日
	 */
	public VOSet<? extends AbstractVO> queryAll(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 批量删除
	 * @param formParamVO 
	 * @author zhangfeng
	 * @date 2015年11月9日
	 */
	public ReturnResult<ListDataVO> listDeleteBatch(FormParamVO formParamVO,Collection<Long> primaryKeys)throws HDException;
	
	/**
	 * 构建列表页查询条件
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月29日
	 */
	public DynaSqlVO buildWhereClause(FormParamVO formParamVO) throws HDException;
	
	public List<String> sqlColumnList(List<? extends FormItemVO> formItemVOs) throws HDException;
	
	/**
	 * 全息查询页面左边树数据
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年2月17日
	 */
	public List<DictDataWarperVO> getDictTree(FormParamVO formParamVO);
	
	/**
	 * 全息查询页面左边树数据
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年6月21日
	 */
	public List<TreeNodeVO> getTreeData(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 判断功能是否为树的维护
	 * @param funcCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月22日
	 */
	public boolean isTreeMaintenance(String funcCode,Long tenantid) throws HDException;
	
	/**
	 * 获取查询默认值VO
	 * @param itemVOs
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月12日
	 */
	AbstractVO getQueryDefaultVO(List<? extends FormItemVO> itemVOs, Long currentDataTenantid) throws HDException;
	
	/**
	 * 列表复制
	 * @param formParamVO
	 * @param primaryKey
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月14日
	 */
	ReturnResult<CardEditVO> listCopy(FormParamVO formParamVO, Long primaryKey, Long tenantid) throws HDException;
	
	/**
	 * 显示报表页面
	 * @param formParamVO
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年6月16日
	 */
	ModelAndView shorReport(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 批量编辑
	 * 根据表单字段是否为批量编辑配置;
	 * 
	 * @param formParamVO
	 * @param primaryKeys
	 * @param vo
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author haocs
	 * @date 2019年3月22日
	 */
	ReturnResult<List<? extends AbstractVO>> batchEditByFItemConfig(FormParamVO formParamVO, Collection<Long> primaryKeys,AbstractVO vo, Long tenantid) throws HDException;
	/**
	 * 
	 * 单字段批量编辑
	 * 
	 * @param formParamVO
	 * @param primaryKeys pks
	 * @param code 字段
	 * @param value 值
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @throws HDException 
	 * @date 2019年3月22日
	 */
	public ReturnResult<List<? extends AbstractVO>> batchEdit(FormParamVO formParamVO, Collection<Long> primaryKeys,
			String code, Object value, Long tenantid) throws HDException;
	
	/**
	 * 
	 * @description 
	 * @author yinbinchen
	 * @date 2019年6月18日 下午5:34:04
	 * @param formParamVO
	 * @param ispage
	 * @param isCodetoName
	 * @return
	 * @throws HDException
	 */
	public VOSet<? extends AbstractVO> listQuery(FormParamVO formParamVO,boolean ispage,boolean isCodetoName) throws HDException;
	/**
	 * 添加查询策略
	 * @param formParamVO
	 * @return 
	 * @author haocs
	 * @throws HDException 
	 * @date 2019年9月29日
	 */
	public ReturnResult<AbstractVO> addQueryStrategy(FormParamVO formParamVO) throws HDException;
	/**
	 * 删除查询策略
	 * @param formParamVO
	 * @return 
	 * @author haocs
	 * @param formParamVO 
	 * @throws HDException 
	 * @date 2019年9月29日
	 */
	public void delQueryStrategy(Collection<Long> primaryKeys, FormParamVO formParamVO) throws HDException;

}
