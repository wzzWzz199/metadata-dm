package com.hayden.hap.common.formmgr.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.InputTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.form.itf.IFormService;
import com.hayden.hap.common.formmgr.entity.Id2NameVO;
import com.hayden.hap.common.formmgr.factory.Id2NameStrategyFactory;
import com.hayden.hap.common.formmgr.inputconfig.QueryselectorInputConfigVO;
import com.hayden.hap.common.formmgr.itf.IId2NameStrategy;
import com.hayden.hap.common.formmgr.utils.Id2NameContext;
import com.hayden.hap.common.formmgr.utils.InputConfigUtils;
import com.hayden.hap.common.spring.service.IService;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 查询选择处理器
 * @author zhangfeng
 * @date 2016年4月20日
 */
@IService("querySelectHandler")
@Component("querySelectHandler")
public class QuerySelectHandler {
	@Autowired
	private ITableDefService tableDefService;
	
	@Autowired
	private IFormService formService;
	
	@Autowired
	private IBaseService baseService;
	
	public void id2name(AbstractVO abstractVO,FormItemVO itemVO,Long tenantid) throws HDException {
		if(!InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) 
			return;
		
		if(abstractVO==null)
			return;
		
		String inputConfig = itemVO.getFitem_input_config();
		QueryselectorInputConfigVO inputConfigVO = InputConfigUtils.getQueryselectorInputConfigVO(inputConfig);
		
		id2Name(abstractVO, inputConfigVO, tenantid);
	}
	
	/**
	 * 
	 * @param abstractVO
	 * @param inputConfigVO
	 * @param tenantid 
	 * @author zhangfeng
	 * @date 2017年4月27日
	 */
	public void id2Name(AbstractVO abstractVO,QueryselectorInputConfigVO inputConfigVO,Long tenantid) {	
		
		if(inputConfigVO==null) {
			return;
		}
		
		//对人员特殊处理
		if("SY_USER_QUERY_M".equals(inputConfigVO.getFunccode())
				|| "SY_USER_CON_QUERY_M".equals(inputConfigVO.getFunccode())
				|| "SY_USER_ALL_QUERY_M".equals(inputConfigVO.getFunccode())) {
			Map<String,String> map = inputConfigVO.getMap();
			if(map!=null) {
				String usernameFiled = map.get("username");
				if(StringUtils.isNotBlank(usernameFiled)) {
					if(!inputConfigVO.isIsid2name()) {
						inputConfigVO.setIsid2name(true);
						
						List<String> exclusions = inputConfigVO.getExclusions();
						if(exclusions==null) {
							exclusions = new ArrayList<>();
							inputConfigVO.setExclusions(exclusions);
						}
						for(Entry<String, String> entry : map.entrySet()) {
							exclusions.add(entry.getValue());
						}
					}
						
//					inputConfigVO = new UserQueryselectorInputConfigVO(inputConfigVO);
//					inputConfigVO.setIsid2name(true);
				}
			}
			
		}
		
		if(!inputConfigVO.isIsid2name()) {
			return;
		}
		
		String tableName = inputConfigVO.getTableName();
		if(StringUtils.isEmpty(tableName)) {
			//没有租户id字段的功能，那么通过海顿租户来找对应的表名
			if(SyConstant.NONTENANTID_FUNC.contains(inputConfigVO.getFunccode())) {
				FormVO formVO = formService.getFormVOByFunccode(inputConfigVO.getFunccode(), SyConstant.TENANT_HD);
				tableName = formVO.getQuery_table_code();
			}else {
				FormVO formVO = formService.getFormVOByFunccode(inputConfigVO.getFunccode(), tenantid);
				tableName = formVO.getQuery_table_code();
			}
		}
		
		String pkColName = tableDefService.getPkColName(tableName);	
		if(!isNeedId2Name(inputConfigVO, pkColName))
			return;
		
		Id2NameVO id2NameVO = new Id2NameVO();
		id2NameVO.setInputConfigVO(inputConfigVO);
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		id2NameVO.setTenantid(currentDataTenantid);
		id2NameVO.setPkColName(pkColName);
		id2NameVO.setTableName(tableName);
		id2NameVO.setWhere(inputConfigVO.getWhere4name());
		if(StringUtils.isNotEmpty(inputConfigVO.getUnique())) {
			id2NameVO.setUniqueColName(inputConfigVO.getUnique());
		}else {
			id2NameVO.setUniqueColName(pkColName);
		}
		
		Id2NameStrategyFactory factory = new Id2NameStrategyFactory();
		IId2NameStrategy strategy = factory.createId2NameStrategy(tableName);
		Id2NameContext context = new Id2NameContext(strategy);
		context.assignName(abstractVO, id2NameVO);
	}
		
	public void id2name(List<? extends AbstractVO> abstractVOs,FormItemVO itemVO,Long tenantid) throws HDException {
		if(!InputTypeEnum.QUERY_SELECT.getCode().equals(itemVO.getFitem_input_type())) 
			return;
		
		if(!ObjectUtil.isNotEmpty(abstractVOs)) {
			return;
		}
		
		String inputConfig = itemVO.getFitem_input_config();
		QueryselectorInputConfigVO inputConfigVO = InputConfigUtils.getQueryselectorInputConfigVO(inputConfig);
				
		id2name(abstractVOs, inputConfigVO, tenantid);
	}
	
	public void id2name(List<? extends AbstractVO> abstractVOs,
			QueryselectorInputConfigVO inputConfigVO,Long tenantid) throws HDException {
		
		if(inputConfigVO==null) {
			return;
		}
		
		if(!inputConfigVO.isIsid2name()) {
			return;
		}
		
		String tableName = inputConfigVO.getTableName();
		if(StringUtils.isEmpty(tableName)) {
			//没有租户id字段的功能，那么通过海顿租户来找对应的表名
			if(SyConstant.NONTENANTID_FUNC.contains(inputConfigVO.getFunccode())) {
				FormVO formVO = formService.getFormVOByFunccode(inputConfigVO.getFunccode(), SyConstant.TENANT_HD);
				tableName = formVO.getQuery_table_code();
			}else {
				FormVO formVO = formService.getFormVOByFunccode(inputConfigVO.getFunccode(), tenantid);
				tableName = formVO.getQuery_table_code();
			}
		}
		
		String pkColName = tableDefService.getPkColName(tableName);	
		
		if(!isNeedId2Name(inputConfigVO, pkColName)) {
			return;
		}
						
		Id2NameVO id2NameVO = new Id2NameVO();
		id2NameVO.setInputConfigVO(inputConfigVO);
		Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
		id2NameVO.setTenantid(currentDataTenantid);
		id2NameVO.setPkColName(pkColName);
		id2NameVO.setTableName(tableName);
		id2NameVO.setWhere(inputConfigVO.getWhere4name());
		if(StringUtils.isNotEmpty(inputConfigVO.getUnique())) {
			id2NameVO.setUniqueColName(inputConfigVO.getUnique());
		}else {
			id2NameVO.setUniqueColName(pkColName);
		}
		
		Id2NameStrategyFactory factory = new Id2NameStrategyFactory();
		IId2NameStrategy strategy = factory.createId2NameStrategy(tableName);
		Id2NameContext context = new Id2NameContext(strategy);
		context.assignName(abstractVOs, id2NameVO);
	}
	
	/**
	 * 判定是否需要返名称
	 * @param inputConfigVO
	 * @param pkColName
	 * @return
	 */
	private boolean isNeedId2Name(QueryselectorInputConfigVO inputConfigVO, String pkColName) {
		if(inputConfigVO.getMap().size()==1) {//就一个字段映射，就用不着返名称
			return false;
		}
		
		//配置的不需要返名称
		if(!inputConfigVO.isIsid2name()) {
			return false;
		}
		
		//映射中没有主键字段，同时没有指定唯一字段，则不返名称
		if(!inputConfigVO.getMap().containsKey(pkColName) && StringUtils.isEmpty(inputConfigVO.getUnique())) {
			return false;
		}
		
		return true;
	}
}
