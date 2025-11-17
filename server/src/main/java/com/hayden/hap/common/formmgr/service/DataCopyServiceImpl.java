package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.itf.IDataCopyService;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IQueryChildrenService;
import com.hayden.hap.common.utils.OriginalInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 数据复制服务
 * @author zhangfeng
 * @date 2017年4月13日
 */
@Service("dataCopyService")
public class DataCopyServiceImpl implements IDataCopyService {
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	@Autowired
	private IFuncLinkService funcLinkService;
	
	@Autowired
	private IQueryChildrenService queryChildrenService;
	
	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IDataCopyService#batchCopy(com.hayden.hap.common.formmgr.entity.FormParamVO, java.util.List)
	 * @author zhangfeng
	 * @date 2017年4月13日
	 */
	@Override
	@Transactional
	public List<AbstractVO> batchCopy(String funcCode, List<AbstractVO> originalList, Long tenantid) throws HDException {
		FormVO formVO = formService.getFormVOByFunccode(funcCode, tenantid);
		ObjectUtil.validNotNull(formVO, "根据功能编码："+funcCode+",没有找到表单对象");
		
		String tableName = formVO.getOpera_table_code();
		TableDefVO tableDefVO = tableDefService.queryDetailedTableByTbname(tableName);
		String pkColName = tableDefVO.getPkColumnVO().getColcode();
		
		for(AbstractVO vo:originalList) {
			OriginalInfoUtils.saveOriginalInfo(vo, pkColName);
			vo.set(pkColName, null);
		}
		VOSet<AbstractVO> afterVoSet = baseService.insertBatch(originalList);
		linkCopy(funcCode, tenantid, afterVoSet.getVoList());
		
		
		return afterVoSet.getVoList();
	}

	/** 
	 *
	 * @see com.hayden.hap.common.formmgr.itf.IDataCopyService#copy(com.hayden.hap.common.formmgr.entity.FormParamVO, com.hayden.hap.common.common.entity.AbstractVO)
	 * @author zhangfeng
	 * @date 2017年4月13日
	 */
	@Override
	@Transactional
	public AbstractVO copy(FormParamVO formParamVO, AbstractVO originalVO, Long tenantid) throws HDException {
		List<AbstractVO> list = new ArrayList<>();
		list.add(originalVO);
		
		List<AbstractVO> resultList = batchCopy(formParamVO.getFuncCode(), list, tenantid);
		
		return resultList.get(0);
	}
	
	/**
	 * 复制关联功能的数据
	 * @param funcCode
	 * @param tenantid 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年3月14日
	 */
	@Override
	@Transactional
	public void linkCopy(String funcCode,Long tenantid,List<AbstractVO> voList) throws HDException {
		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink(funcCode, tenantid);
		if(ObjectUtil.isNotEmpty(funcLinkVOs)) {
			for(FuncLinkVO funcLinkVO : funcLinkVOs) {
				
				List<FuncLinkItemVO> valueItems = new ArrayList<>();
				
				for(FuncLinkItemVO itemVO : funcLinkVO.getLinkItems()) {
					if(ObjectUtil.isTrue(itemVO.getLitem_isvalue())) {
						valueItems.add(itemVO);
					}
				}
				
				if(valueItems.size()==0) {//是否传值，如果不传值的子功能，则不进行复制
					continue;
				}
				List<AbstractVO> allChildren = queryChildrenService.getChildren(voList, funcLinkVO, tenantid);
				if(ObjectUtil.isEmpty(allChildren)) {
					continue;
				}
				
				Map<AbstractVO, List<? extends AbstractVO>> parent2ChildrenMap = queryChildrenService.matchChildren(voList, allChildren, funcLinkVO);
				
				for(Entry<AbstractVO, List<? extends AbstractVO>> entry : parent2ChildrenMap.entrySet()) {
					AbstractVO parentVO = entry.getKey();
					List<? extends AbstractVO> children = entry.getValue();
					if(ObjectUtil.isEmpty(children)) {
						continue;
					}
					for(AbstractVO child : children) {						
						for(FuncLinkItemVO itemVO:valueItems) {
							OriginalInfoUtils.saveOriginalInfo(child, itemVO.getLitem_sub_field());
							child.set(itemVO.getLitem_sub_field(), parentVO.get(itemVO.getLitem_main_field()));							
						}
					}
				}
				
				batchCopy(funcLinkVO.getSub_func_code(), allChildren, tenantid);
			}
		}
	}

	/**
	 * 主键置空
	 * @param vo
	 * @param pkColName
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月18日
	 */
	public AbstractVO setNullPrimaryKey(AbstractVO vo, String pkColName) {
		OriginalInfoUtils.saveOriginalInfo(vo, pkColName);
		vo.set(pkColName, null);
		return vo;
	}
}
