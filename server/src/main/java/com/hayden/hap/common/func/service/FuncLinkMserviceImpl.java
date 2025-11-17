package com.hayden.hap.common.func.service;

import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormMService;
import com.hayden.hap.common.form.service.DefaultTableCallback;
import com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.func.entity.FuncLinkItemMVO;
import com.hayden.hap.common.func.entity.FuncLinkMVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkMService;
import com.hayden.hap.common.func.itf.IFuncMService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author zhangfeng
 * @date 2018年5月10日
 */
@Service("funcLinkMService")
public class FuncLinkMserviceImpl implements IFuncLinkMService {

	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFuncMService funcMService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	@Autowired
	private IFormMService formMService;

	/**
	 * 获取所有启用的关联功能新型
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2018年5月11日
	 */
	@Override
	public List<FuncLinkMVO> getEnableFuncLinkByTenant(Long tenantid) {
		List<String> columList = new ArrayList<String>();
		columList.add("main_func_code");
		columList.add("sub_func_code");
		String orderByClause = "main_func_code,link_order";
		StringBuffer whereStringBuffer = new StringBuffer("tenantid=");
		whereStringBuffer.append(tenantid);
		whereStringBuffer.append(" and link_is_enable=1");
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.setSqlColumnList(columList);
		dynaSqlVO.setWhereClause(whereStringBuffer.toString());
		dynaSqlVO.setOrderByClause(orderByClause);
		VOSet<FuncLinkMVO> voset = baseService.query(FuncLinkMVO.class, dynaSqlVO);
		List<FuncLinkMVO> list = voset.getVoList();
		return list;
	}
	
	/** 
	 *
	 * @see com.hayden.hap.common.func.itf.IFuncLinkMService#getFuncLink(java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年5月10日
	 */
	@Override
	@Cacheable(value=CacheConstant.CACHE_FUNC_LINK_MOBILE,key="#mainFuncCode.concat('|').concat(#tenantid)")
	public List<FuncLinkMVO> getFuncLink(String mainFuncCode, Long tenantid) {
		DynaSqlVO linkDynaSqlVO = new DynaSqlVO();
		linkDynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		linkDynaSqlVO.addWhereParam("main_func_code", mainFuncCode);
		linkDynaSqlVO.addWhereParam("link_is_enable", SyConstant.SY_TRUE);		
		linkDynaSqlVO.setOrderByClause(" link_order ");
		List<FuncLinkMVO> linkList = baseService.query(FuncLinkMVO.class, linkDynaSqlVO).getVoList();
		if(ObjectUtil.isEmpty(linkList))
			return linkList;

		Map<String,FuncLinkMVO> linkMap = new HashMap<>();
		for(FuncLinkMVO funcLinkMVO : linkList) {
			linkMap.put(funcLinkMVO.getSub_func_code(), funcLinkMVO);
		}

		DynaSqlVO itemDynaSqlVO = new DynaSqlVO();
		itemDynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		itemDynaSqlVO.addWhereParam("main_func_code", mainFuncCode);
		itemDynaSqlVO.addWhereParam("sub_func_code", linkMap.keySet());		
		List<FuncLinkItemMVO> itemList = baseService.query(FuncLinkItemMVO.class, itemDynaSqlVO).getVoList();

		Map<String,List<FuncLinkItemMVO>> itemMap = new HashMap<>();
		for(FuncLinkItemMVO itemMVO : itemList) {
			List<FuncLinkItemMVO> list = itemMap.get(itemMVO.getSub_func_code());
			if(list==null) {
				list = new ArrayList<>();
				itemMap.put(itemMVO.getSub_func_code(), list);
			}
			list.add(itemMVO);
			
		}
		
		for(Entry<String, FuncLinkMVO> entry : linkMap.entrySet()) {
			FuncLinkMVO linkVO = entry.getValue();
			String subFunccode = entry.getKey();
			List<FuncLinkItemMVO> list = itemMap.get(subFunccode);
			linkVO.setLinkItems(list);
		}
		
		return linkList;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.func.itf.IFuncLinkMService#getFuncLink(java.lang.String, java.lang.String, java.lang.Long)
	 * @author zhangfeng
	 * @date 2018年5月10日
	 */
	@Override
	public List<FuncLinkMVO> getFuncLink(String mainFuncCode, String subFuncCode, Long tenantid) {
		IFuncLinkMService funcLinkService = AppServiceHelper.findBean(IFuncLinkMService.class);
		List<FuncLinkMVO> list = funcLinkService.getFuncLink(mainFuncCode, tenantid);
		
		if(!StringUtils.hasLength(subFuncCode))
			return list;
		
		List<FuncLinkMVO> result = new ArrayList<>(); 
		for(FuncLinkMVO funcLinkVO : list) {
			if(subFuncCode.equals(funcLinkVO.getSub_func_code())) {
				result.add(funcLinkVO);
			}
		}
		
		return result;
	}
	
	@Override
	public void linkDelete(Collection<Long> pks, String funcCode) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		
		IFuncLinkMService funcLinkMService = AppServiceHelper.findBean(IFuncLinkMService.class);
		List<FuncLinkMVO> funcLinkVOs = funcLinkMService.getFuncLink(funcCode, tenantid);
		
		if(!ObjectUtil.isNotEmpty(funcLinkVOs)) {//没有关联功能，直接返回
			return;
		}
		
		String tableName = funcMService.getOperaTableNameOfFunc(funcCode,tenantid);
//		AbstractVO vo = baseService.queryByPK(tableName, pk);
		VOSet<BaseVO> voset = baseService.queryByPKsOfLongAndTenantid(new BaseVO(tableName), pks, tenantid);
		
		if(!ObjectUtil.isNotEmpty(voset.getVoList())) {//根据主键没有查到数据
			return;
		}
		
		List<BaseVO> voList = voset.getVoList();
		
		for(FuncLinkMVO funcLinkVO : funcLinkVOs) {
			if(ObjectUtil.isTrue(funcLinkVO.getLink_is_del())) {//只有设置了关联删除才去做删除操作
				FuncVO funcVO = funcMService.queryByFunccode(funcCode, tenantid);
				if(!FuncTypeEnum.FORM.getId().equals(funcVO.getFunc_type())) {//如果不是表单类型的功能，不处理
					continue;
				}
				
				DynaSqlVO dynaSqlVO = constructWhereclause4linkDelete(voList, funcLinkVO);					
				
				String subFuncCode = funcLinkVO.getSub_func_code();
				String subTableName = funcMService.getOperaTableNameOfFunc(subFuncCode,tenantid);
				if(!SyConstant.NONTENANTID_FUNC.contains(subFuncCode)) {
					dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, currentDataTenantid);
				}
				
				String pkColumn = tableDefService.getPkColName(new DefaultTableCallback(subFuncCode, null, tenantid));
				VOSet<AbstractVO> voSet = baseService.query(subTableName, dynaSqlVO);
				List<Long> childPks = new ArrayList<Long>();
				for(AbstractVO childVO:voSet.getVoList()) {
					childPks.add(childVO.getLong(pkColumn));
				}
				
				if(ObjectUtil.isNotEmpty(childPks)) {
					FormVO formVO = formMService.getFormVOByFunccode(subFuncCode,tenantid);
					IAction action = formMService.getActionByFormCode(formVO.getForm_code(), tenantid);
					
					//递归删去吧，删去吧，去吧，吧。
					linkDelete(childPks, subFuncCode);
					
					if(action!=null) {
						action.beforeListDeleteBatch(new FormParamVO(), childPks);
					}
					
					baseService.deleteByPKsOfLongAndTenantid(subTableName, childPks, currentDataTenantid);
					
					if(action!=null) {
						action.afterListDeleteBatch(new FormParamVO(), childPks);
					}
				}
			}
		}
	}
	
	/**
	 * 为关联删除构造查询条件
	 * @param voList
	 * @param funcLinkVO
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年4月5日
	 */
	private DynaSqlVO constructWhereclause4linkDelete(List<BaseVO> voList,FuncLinkMVO funcLinkVO) throws HDException {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		
		boolean isWhere = false;
		
		Map<String,Boolean> isStringMap = new HashMap<>();
		StringBuilder whereSB = new StringBuilder();
		boolean isOutFirst = true;
		for(BaseVO vo : voList) {
			if(isOutFirst) {
				whereSB.append(" ( ");
			}else {
				whereSB.append(" or ");
			}
			
			isOutFirst = false;
			
			boolean isInnerFirst = true;
			for(FuncLinkItemMVO linkItemVO : funcLinkVO.getLinkItems()) {				
				if(ObjectUtil.isTrue(linkItemVO.getLitem_iswhere())) {
					if(isInnerFirst) {
						whereSB.append(" ( ");						
					}else {
						whereSB.append(" and ");
					}
					isInnerFirst = false;
					
					whereSB.append(linkItemVO.getLitem_sub_field());
					whereSB.append(" = ");
					
					Boolean isString = isStringMap.get(linkItemVO.getLitem_main_field());
					Object value = vo.get(linkItemVO.getLitem_main_field());
					if(isString==null) {
						if(value instanceof String) {
							isStringMap.put(linkItemVO.getLitem_main_field(), Boolean.TRUE);
							appendSingleQuotes(whereSB, value);
						}else {
							isStringMap.put(linkItemVO.getLitem_main_field(), Boolean.FALSE);
							whereSB.append(value);
						}
					}else if(isString){
						appendSingleQuotes(whereSB, value);
					}else {
						whereSB.append(value);
					}
					
					isWhere = true;
					
				}
			}
			if(!isInnerFirst) {
				whereSB.append(" ) ");
			}
		}
		if(!isOutFirst) {
			whereSB.append(" ) ");
		}		
		
		if(!isWhere) {//如果没有关联字段作为条件，则不处理...因为一删就删子功能的所有数据了，这应该是不正确的，要不抛个异常？
			throw new HDException("关联功能："+funcLinkVO.getSub_func_code()+",设置为关联删除，但关联字段没有设置关联条件");
		}
		
		dynaSqlVO.addWhereClause(whereSB.toString());
		
		if(StringUtils.hasLength(funcLinkVO.getLink_where())) {
			dynaSqlVO.addWhereClause(funcLinkVO.getLink_where());
		}
		
		return dynaSqlVO;
	}

	private void appendSingleQuotes(StringBuilder sb,Object value) {
		sb.append(" '");
		sb.append(value);
		sb.append("' ");
	}
	
	@Override
	public void linkUpdate(AbstractVO vo, String funcCode) throws HDException {
		Long tenantid = TenantUtil.getCurrentDataTenantid(CurrentEnvUtils.getTenantId());
		IFuncLinkMService funcLinkService = AppServiceHelper.findBean(IFuncLinkMService.class);
		List<FuncLinkMVO> funcLinkVOs = funcLinkService.getFuncLink(funcCode, tenantid);
		
		if(!ObjectUtil.isNotEmpty(funcLinkVOs)) {//没有关联功能，直接返回
			return;
		}
		
		Long pkValue = (Long)baseService.getVOPkColValue(vo);
		AbstractVO oldVO = baseService.queryByPKAndTenantid(vo, pkValue, tenantid);
		
		for(FuncLinkMVO funcLinkVO:funcLinkVOs) {
			if(ObjectUtil.isTrue(funcLinkVO.getLink_is_update())) {//只设置了关联更新才去做更新操作
				FuncVO funcVO = funcMService.queryByFunccode(funcCode, tenantid);
				if(!FuncTypeEnum.FORM.getId().equals(funcVO.getFunc_type())) {//如果不是表单类型的功能，不处理
					continue;
				}
				
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				List<String> updateColumns = new ArrayList<String>();
				
				Map<String,String> sub2MainMap = new HashMap<>();
				boolean changed = false;
				for(FuncLinkItemMVO funcLinkItemVO:funcLinkVO.getLinkItems()) {
					if(ObjectUtil.isTrue(funcLinkItemVO.getLitem_isvalue()) 
							&& isModify(oldVO, vo, funcLinkItemVO.getLitem_main_field())) {
						updateColumns.add(funcLinkItemVO.getLitem_sub_field());
						sub2MainMap.put(funcLinkItemVO.getLitem_sub_field(), funcLinkItemVO.getLitem_main_field());
						changed = true;
					}
					if(ObjectUtil.isTrue(funcLinkItemVO.getLitem_iswhere())) {
						dynaSqlVO.addWhereParam(funcLinkItemVO.getLitem_sub_field(), oldVO.get(funcLinkItemVO.getLitem_main_field()));
					}
				}
				//如果关联字段没有被修改，则不用往下处理
				if(!changed) {
					continue;
				}
				
				if(StringUtils.hasLength(funcLinkVO.getLink_where())) {
					dynaSqlVO.setWhereClause(funcLinkVO.getLink_where());
				}
				
				String subFunccode = funcLinkVO.getSub_func_code();
				String subTableName = funcMService.getOperaTableNameOfFunc(subFunccode, tenantid);
				VOSet<AbstractVO> voSet = baseService.query(subTableName, dynaSqlVO);
				if(!ObjectUtil.isNotEmpty(voSet.getVoList())) {//没有查到子功能对应数据，不往下处理
					continue;
				}
				
				for(AbstractVO subVO : voSet.getVoList()) {
					for(Entry<String, String> entry : sub2MainMap.entrySet()) {
						subVO.set(entry.getKey(), vo.get(entry.getValue()));
					}					
				}
				
				DynaSqlVO updateDynaSqlVO = new DynaSqlVO();
				updateDynaSqlVO.setSqlColumnList(updateColumns);
				//递归更新去吧...
				linkUpdate(voSet.getVoList(), subFunccode);
				IAction subAction = formMService.getActionByFuncCode(subFunccode, tenantid);
				
				BatchUpdateParamVO batchUpdateParamVO = new BatchUpdateParamVO();
				batchUpdateParamVO.setDynaSqlVO(updateDynaSqlVO);
				batchUpdateParamVO.setVoList(voSet.getVoList());
				
				if(subAction!=null) {
					ReqParamVO paramVO = new ReqParamVO();
					paramVO.setParentEntityId(pkValue);
					paramVO.setParentFuncCode(funcCode);
					FormParamVO formParamVO = new FormParamVO();
					formParamVO.setReqParamVO(paramVO);
					subAction.beforeListUpdate(formParamVO, batchUpdateParamVO);
				}
				baseService.updateBatch(voSet.getVoList(), updateDynaSqlVO);
				
				if(subAction!=null) {
					ReqParamVO paramVO = new ReqParamVO();
					paramVO.setParentEntityId(pkValue);
					paramVO.setParentFuncCode(funcCode);
					FormParamVO formParamVO = new FormParamVO();
					formParamVO.setReqParamVO(paramVO);
					subAction.afterListUpdate(formParamVO, batchUpdateParamVO);
				}
			}
		}
	}
	
	/**
	 * 判断vo是否被修改
	 * @param oldVO
	 * @param newVO
	 * @param fieldStr
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月16日
	 */
	private boolean isModify(AbstractVO oldVO,AbstractVO newVO,String fieldStr) {
		String[] fields = fieldStr.split(",");
		for(String field:fields) {
			if(oldVO.get(field)==null) {
				if(newVO.get(field)==null)
					continue;
				if(newVO.get(field)!=null)
					return true;
			}else if(!oldVO.get(field).equals(newVO.get(field))) {
				return true;
			}
		}		
		return false;
	}
	
	@Override
	public void linkUpdate(List<? extends AbstractVO> list, String funcCode) throws HDException {
		Long tenantid = TenantUtil.getCurrentDataTenantid(CurrentEnvUtils.getTenantId());
		IFuncLinkMService funcLinkService = AppServiceHelper.findBean(IFuncLinkMService.class);
		List<FuncLinkMVO> funcLinkVOs = funcLinkService.getFuncLink(funcCode, tenantid);
		
		if(!ObjectUtil.isNotEmpty(funcLinkVOs)) {//没有关联功能，直接返回
			return;
		}
		for(AbstractVO abstractVO : list) {
			linkUpdate(abstractVO, funcCode);
		}
		
	}

	@Override
	public List<FuncLinkMVO> listVOByParentFuncCode(Collection<String> parentFuncCode, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		String conditionSql = DBSqlUtil.getConditionSql("main_func_code", parentFuncCode, true);
		dynaSqlVO.addWhereClause(conditionSql);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		return baseService.query(FuncLinkMVO.class, dynaSqlVO).getVoList();
	}
}
