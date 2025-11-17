package com.hayden.hap.common.func.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.FuncTypeEnum;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.form.service.DefaultTableCallback;
import com.hayden.hap.common.formmgr.entity.BatchUpdateParamVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.itf.IAction;
import com.hayden.hap.common.func.entity.FuncLinkItemVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.func.entity.FuncVO;
import com.hayden.hap.common.func.itf.IFuncLinkService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VariableUtils;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.script.ScriptException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月30日
 */
@Service("funcLinkService")
public class FuncLinkServiceImpl implements IFuncLinkService{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FuncLinkServiceImpl.class);
	
	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private ITableDefService tableDefService;

	/**
	 * 
	 *
	 * @see com.hayden.hap.common.func.itf.IFuncService#getEnableFuncLinkByTenant()
	 * @author lianghua
	 * @date 2015年11月18日
	 */
	@Override
	public List<FuncLinkVO> getEnableFuncLinkByTenant(Long tenantid) {
		FuncLinkVO syFuncLink = new FuncLinkVO();
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
		VOSet<FuncLinkVO> voset = baseService.query(syFuncLink, dynaSqlVO);
		List<FuncLinkVO> list = voset.getVoList();
		return list;
	}
	
//	/**
//	 * 根据父功能编码查子功能
//	 * @param parentFuncCode
//	 * @return 
//	 * @author zhangfeng
//	 * @date 2015年12月3日
//	 */
//	@Override
//	@Cacheable(value="SY_FUNC_LINK",key="#mainFuncCode.concat('|').concat(#tenantid)")
//	public List<FuncLinkVO> getFuncLink(String mainFuncCode,Long tenantid) {
//		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
//		List<FuncLinkVO> result = funcLinkService.getFuncLink(mainFuncCode, "",tenantid);
//		return result;
//	}
	
	/**
	 * 根据父功能编码查子功能
	 * @param parentFuncCode
	 * @return 
	 * @author zhangfeng
	 * @date 2015年12月3日
	 */
	@Override
	@Cacheable(value="SY_FUNC_LINK",key="#mainFuncCode.concat('|').concat(#tenantid)")
	public List<FuncLinkVO> getFuncLink(String mainFuncCode,Long tenantid) {
		Long start = System.currentTimeMillis();
//		Long tenantId = ThreadLocalUtils.getLong(SyConstant.TENANT_STR);
		StringBuilder sql = new StringBuilder();
//		sql.append("select link.*,item.*,func.modulecode from sy_func_link link ");
//		sql.append(" LEFT JOIN sy_func_link_item item on link.main_func_code = item.main_func_code and link.sub_func_code = item.sub_func_code and link.tenantid = item.tenantid ");
//		sql.append(" LEFT JOIN sy_func func on link.sub_func_code = func.func_code and link.tenantid = func.tenantid ");
//		sql.append(" where link.main_func_code = ? ");
//		sql.append(" and link.tenantid = ? ");
//		sql.append(" and func.func_isbuy = 1 ");
//		sql.append(" order by link.link_order asc ");

		sql.append("SELECT link.*,item.litem_id,item.tenantid tenantid1,item.main_func_code main_func_code1,item.sub_func_code sub_func_code1,\n" +
				"item.litem_main_field,item.litem_sub_field,item.litem_iswhere,item.litem_query_sign,item.litem_isvalue,item.litem_isconstant,item.litem_comment,item.product_flag product_flag1,func.modulecode FROM (SELECT * FROM sy_func_link WHERE tenantid=?) link ");		sql.append(" LEFT JOIN (SELECT * FROM sy_func_link_item WHERE tenantid=?) item ON link.main_func_code = item.main_func_code AND link.sub_func_code = item.sub_func_code ");
		sql.append(" LEFT JOIN (SELECT * FROM sy_func WHERE tenantid=?) func ON link.sub_func_code = func.func_code ");
		sql.append(" WHERE link.main_func_code = ? ");
		sql.append(" AND func.func_isbuy = 1 ");
		sql.append(" AND link.link_is_enable = 1 ");
		sql.append(" ORDER BY link.link_order ASC ");

		Object[] preStatementParam = null;


		preStatementParam = new Object[]{tenantid,tenantid,tenantid,mainFuncCode};


		List<List<FuncLinkVO>> resultList = baseService.executeQuery(sql.toString(), null, preStatementParam, new ResultSetExtractor<List<FuncLinkVO>>(){

			@Override
			public List<FuncLinkVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<FuncLinkVO> linkList = new ArrayList<FuncLinkVO>();
				Map<Long,FuncLinkVO> map = new HashMap<Long, FuncLinkVO>();

				FuncLinkVO funcLink = null;
				List<FuncLinkItemVO> linkItems = null;
				while(rs.next()){
					if(!map.containsKey(rs.getLong("link_id"))){
						funcLink = new FuncLinkVO();
						funcLink.setLink_id(rs.getLong("link_id"));
						funcLink.setTenantid(rs.getLong("tenantid"));
						funcLink.setMain_func_code(rs.getString("main_func_code"));
						funcLink.setSub_func_code(rs.getString("sub_func_code"));
						funcLink.setLink_name(rs.getString("link_name"));
						funcLink.setLink_where(rs.getString("link_where"));
						funcLink.setLink_control(rs.getString("link_control"));
						funcLink.setLink_is_del(rs.getInt("link_is_del"));
						funcLink.setLink_is_update(rs.getInt("link_is_update"));
						funcLink.setLink_is_readonly(rs.getInt("link_is_readonly"));
						funcLink.setLink_is_showquery(rs.getInt("link_is_showquery"));
						funcLink.setLink_is_forward_card(rs.getInt("link_is_forward_card"));
						funcLink.setLink_is_show_subcard(rs.getInt("link_is_show_subcard"));
						funcLink.setLink_order(rs.getInt("link_order"));
						funcLink.setLink_is_enable(rs.getInt("link_is_enable"));
						funcLink.setSubModuleCode(rs.getString("modulecode"));
						linkItems = new ArrayList<FuncLinkItemVO>();
						funcLink.setLinkItems(linkItems);
						linkList.add(funcLink);
						map.put(rs.getLong("link_id"), funcLink);
					}else {
						funcLink = map.get(rs.getLong("link_id"));
					}
					FuncLinkItemVO linkItem = new FuncLinkItemVO();
//					linkItems.add(linkItem);
					linkItem.setLitem_id(rs.getLong("litem_id"));
					linkItem.setTenantid(rs.getLong("tenantid"));
					linkItem.setMain_func_code(rs.getString("main_func_code"));
					linkItem.setSub_func_code(rs.getString("sub_func_code"));
					linkItem.setLitem_main_field(rs.getString("litem_main_field"));
					linkItem.setLitem_sub_field(rs.getString("litem_sub_field"));
					linkItem.setLitem_iswhere(rs.getInt("litem_iswhere"));
					linkItem.setLitem_query_sign(rs.getString("litem_query_sign"));
					linkItem.setLitem_isvalue(rs.getInt("litem_isvalue"));
					linkItem.setLitem_isconstant(rs.getInt("litem_isconstant"));
					linkItem.setLitem_comment(rs.getString("litem_comment"));

					funcLink.getLinkItems().add(linkItem);
				}
				return linkList;
			}

		}, "sy_func_link");

		Long end = System.currentTimeMillis();
		logger.debug("getFuncLink查询时间花费："+(end-start));

		if(ObjectUtil.isNotEmpty(resultList))
			return resultList.get(0);

		return null;
	}
	
	/**
	 * 根据父功能编码查关联功能(新起事务)
	 * @param mainFuncCode
	 * @param tenantid
	 * @return 
	 * @author zhangfeng
	 * @date 2017年2月28日
	 */
	public List<FuncLinkVO> getFuncLink_RequiresNew(String mainFuncCode,Long tenantid) {
		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
		return funcLinkService.getFuncLink(mainFuncCode, tenantid);
	}

	@Override
//	@Cacheable(value="SY_FUNC_LINK",key="#mainFuncCode.concat('|').concat(#tenantid)")
	public List<FuncLinkVO> getFuncLink(String mainFuncCode, String subFuncCode,Long tenantid) {
		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
		List<FuncLinkVO> list = funcLinkService.getFuncLink(mainFuncCode, tenantid);
		
		if(!StringUtils.hasLength(subFuncCode))
			return list;
		
		List<FuncLinkVO> result = new ArrayList<>(); 
		for(FuncLinkVO funcLinkVO : list) {
			if(subFuncCode.equals(funcLinkVO.getSub_func_code())) {
				result.add(funcLinkVO);
			}
		}
		
		return result;
	}
	
	/**
	 * 根据子功能得到主功能编码
	 * @param subFuncCode
	 * @param tenantid
	 * @return 
	 * @author liyan
	 * @date 2017年7月12日
	 */
	@Override
	public String getMainFuncCode(String subFuncCode,Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		dynaSqlVO.addWhereParam("sub_func_code", subFuncCode);
		VOSet<FuncLinkVO> voset = baseService.query(FuncLinkVO.class, dynaSqlVO);
		if(!voset.isEmpty() && voset.getVoList().size()>0){
			return voset.getVoList().get(0).getMain_func_code();
		}
		return null;
	}
	
	
	
	@Override
	public List<FuncLinkVO> getFuncLinkInCard(List<FuncLinkVO> list) {
//		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
//		List<FuncLinkVO> list = funcLinkService.getFuncLink(mainFuncCode, tenantid);
		List<FuncLinkVO> inCardList = new ArrayList<FuncLinkVO>();
		for(FuncLinkVO funcLink:list) {
			if(ObjectUtil.isTrue(funcLink.getLink_is_show_subcard())) {
				inCardList.add(funcLink);
			}
		}
		return inCardList;
	}

	@Override
	public List<FuncLinkVO> getFuncLinkOutCard(List<FuncLinkVO> list) {
//		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
//		List<FuncLinkVO> list = funcLinkService.getFuncLink(mainFuncCode, tenantid);
		List<FuncLinkVO> outCardList = new ArrayList<FuncLinkVO>();
		for(FuncLinkVO funcLink:list) {
			if(!ObjectUtil.isTrue(funcLink.getLink_is_show_subcard())) {
				outCardList.add(funcLink);
			}
		}
		return outCardList;
	}
	
	@Override
	public void linkUpdate(AbstractVO vo, String funcCode) throws HDException {
		Long tenantid = TenantUtil.getCurrentDataTenantid(CurrentEnvUtils.getTenantId());
		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink(funcCode, tenantid);
		
		if(!ObjectUtil.isNotEmpty(funcLinkVOs)) {//没有关联功能，直接返回
			return;
		}
		
		Long pkValue = (Long)baseService.getVOPkColValue(vo);
		AbstractVO oldVO = baseService.queryByPKAndTenantid(vo, pkValue, tenantid);
		
		for(FuncLinkVO funcLinkVO:funcLinkVOs) {
			if(ObjectUtil.isTrue(funcLinkVO.getLink_is_update())) {//只设置了关联更新才去做更新操作
				FuncVO funcVO = funcService.queryByFunccode(funcCode, tenantid);
				if(!FuncTypeEnum.FORM.getId().equals(funcVO.getFunc_type())) {//如果不是表单类型的功能，不处理
					continue;
				}
				
				DynaSqlVO dynaSqlVO = new DynaSqlVO();
				List<String> updateColumns = new ArrayList<String>();
				
				Map<String,String> sub2MainMap = new HashMap<>();
				boolean changed = false;
				for(FuncLinkItemVO funcLinkItemVO:funcLinkVO.getLinkItems()) {
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
				String subTableName = funcService.getOperaTableNameOfFunc(subFunccode, tenantid);
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
				IAction subAction = formService.getActionByFuncCode(subFunccode, tenantid);
				
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
		for(AbstractVO abstractVO : list) {
			linkUpdate(abstractVO, funcCode);
		}
		
	}
	
	@Override
	public void linkDelete(Collection<Long> pks, String funcCode) throws HDException {
		Long tenantid = CurrentEnvUtils.getTenantId();
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		
		IFuncLinkService funcLinkService = AppServiceHelper.findBean(IFuncLinkService.class);
		List<FuncLinkVO> funcLinkVOs = funcLinkService.getFuncLink(funcCode, tenantid);
		
		if(!ObjectUtil.isNotEmpty(funcLinkVOs)) {//没有关联功能，直接返回
			return;
		}
		
		String tableName = funcService.getOperaTableNameOfFunc(funcCode,tenantid);
//		AbstractVO vo = baseService.queryByPK(tableName, pk);
		VOSet<BaseVO> voset = baseService.queryByPKsOfLongAndTenantid(new BaseVO(tableName), pks, tenantid);
		
		if(!ObjectUtil.isNotEmpty(voset.getVoList())) {//根据主键没有查到数据
			return;
		}
		
		List<BaseVO> voList = voset.getVoList();
		
		for(FuncLinkVO funcLinkVO:funcLinkVOs) {
			if(ObjectUtil.isTrue(funcLinkVO.getLink_is_del())) {//只有设置了关联删除才去做删除操作
				FuncVO funcVO = funcService.queryByFunccode(funcCode, tenantid);
				if(!FuncTypeEnum.FORM.getId().equals(funcVO.getFunc_type())) {//如果不是表单类型的功能，不处理
					continue;
				}
				
				DynaSqlVO dynaSqlVO = constructWhereclause4linkDelete(voList, funcLinkVO);					
				
				String subFuncCode = funcLinkVO.getSub_func_code();
				String subTableName = funcService.getOperaTableNameOfFunc(subFuncCode,tenantid);
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
					FormVO formVO = formService.getFormVOByFunccode(subFuncCode,tenantid);
					IAction action = formService.getActionByFormCode(formVO.getForm_code(), tenantid);
					
					//递归删去吧，删去吧，去吧，吧。
					linkDelete(childPks, subFuncCode);
					FormParamVO formParamaven = new FormParamVO();
					formParamaven.setFuncCode(subFuncCode);
					if(action!=null) {
						action.beforeListDeleteBatch(formParamaven, childPks);
					}
					
					baseService.deleteByPKsOfLongAndTenantid(subTableName, childPks, currentDataTenantid);
					
					if(action!=null) {
						action.afterListDeleteBatch(formParamaven, childPks);
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
	private DynaSqlVO constructWhereclause4linkDelete(List<BaseVO> voList,FuncLinkVO funcLinkVO) throws HDException {
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
			for(FuncLinkItemVO linkItemVO : funcLinkVO.getLinkItems()) {				
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
	public List<FuncLinkVO> filterByExpress(List<FuncLinkVO> all, AbstractVO vo) throws HDException {
		List<FuncLinkVO> result = new ArrayList<>();
		if(ObjectUtil.isNotEmpty(all)) {
			for(FuncLinkVO linkVO : all) {
				String express = linkVO.getLink_control();
				if(StringUtils.hasLength(express)) {
					boolean evalResult;
					try {
						evalResult = VariableUtils.replaceAllParamAndEval(express, vo);
					} catch (ScriptException e) {
						logger.error(e.getMessage(), e);
						throw new HDException("关联功能状态控制，表达式错误："+express);
					}
					if(evalResult) {
						result.add(linkVO);
					}
				}else {
					result.add(linkVO);
				}
			}
		}
		return result;
	}

	@Override
	public List<FuncLinkVO> listVOByParentFuncCode(Collection<String> parentFuncCode, Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		String conditionSql = DBSqlUtil.getConditionSql("main_func_code", parentFuncCode, true);
		dynaSqlVO.addWhereClause(conditionSql);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		return baseService.query(FuncLinkVO.class, dynaSqlVO).getVoList();
	}
}
