package com.hayden.hap.common.func.service;

import com.hayden.hap.common.cache.constant.CacheConstant;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.enumerate.DeleteCtrlEnum;
import com.hayden.hap.common.func.entity.FuncLinkDataVO;
import com.hayden.hap.common.func.itf.IFuncLinkDataService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.common.tenant.utils.TenantUtil;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.session.CurrentEnvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author zhangfeng
 * @date 2015年12月16日
 */
@Service(value="funcLinkDataService")
public class FuncLinkDataServiceImpl implements IFuncLinkDataService{

	@Autowired
	private IBaseService baseService;
	
	@Autowired
	private IFuncService funcService;
	
	@Autowired
	private ITableDefService tableDefService;
	
	@Override
	public void linkUpdate(AbstractVO vo, String funcCode,Long tenantid) {
		IFuncLinkDataService funcLinkDataService = (IFuncLinkDataService)AppServiceHelper.findBean("funcLinkDataService");
		List<FuncLinkDataVO> funcLinkDatas = funcLinkDataService.getFuncLinkDatasByFunccode_Cache(funcCode,tenantid);
		linkUpdate(vo, funcLinkDatas, tenantid);
	}

	private void linkUpdate(AbstractVO vo, List<FuncLinkDataVO> list, Long tenantid) {
		if(ObjectUtil.isNotEmpty(list)) {
//			Long tenantid = CurrentEnvUtils.getTenantId();
			Long pkValue = (Long)baseService.getVOPkColValue(vo);
			Long currentDataTenantid = TenantUtil.getCurrentDataTenantid(tenantid);
			AbstractVO oldVO = baseService.queryByPKAndTenantid(vo, pkValue, currentDataTenantid);
			
			for(FuncLinkDataVO linkData:list) {
				if(SyConstant.SY_TRUE == linkData.getLdata_update()) {
					
					//如果关联字段没有被修改，则不用往下处理
					if(!isModify(oldVO, vo, linkData.getParent_fields())) {
						continue;
					}
					
					String tableName = linkData.getTable_code();
					String[] table_fields = linkData.getTable_fields().split(",");				
					String[] parent_fields = linkData.getParent_fields().split(",");
					
					if(table_fields.length!=parent_fields.length) {
						throw new HDRuntimeException("本表字段编码和被动表字段编码长度不同...");
					}
										
					StringBuilder sql = new StringBuilder();
					sql.append(" update ");
					sql.append(tableName);
					sql.append(" set ");
					
					for(int i=0;i<table_fields.length;i++) {						
						sql.append(table_fields[i]);
						sql.append(" = '");
						sql.append(vo.get(parent_fields[i]));
						sql.append("'");
						sql.append(",");
					}
					
					sql.deleteCharAt(sql.length()-1);
					sql.append(" where 1=1 ");
					
					for(int i=0;i<table_fields.length;i++) {	
						sql.append(" and ");
						sql.append(table_fields[i]);
						sql.append(" = '");
						sql.append(oldVO.get(parent_fields[i]));
						sql.append("' ");
					}
					
					if(StringUtils.hasLength(linkData.getLdata_where())) {
						sql.append(" ");
						sql.append(linkData.getLdata_where());
					}
					
					if(!SyConstant.NONTENANTID_TABLE.containsKey(tableName)) {
						sql.append(" and tenantid=");
						sql.append(tenantid);
					}
					
					baseService.executeUpate(sql.toString(), tableName);
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
			if(oldVO.get(field)==null && newVO.get(field)!=null) {
				return true;
			}
			if(!oldVO.get(field).equals(newVO.get(field))) {
				return true;
			}
		}		
		return false;
	}

	@Override
	public void linkUpdate(List<? extends AbstractVO> list, String funcCode,Long tenantid) {
		IFuncLinkDataService funcLinkDataService = (IFuncLinkDataService)AppServiceHelper.findBean("funcLinkDataService");
		List<FuncLinkDataVO> funcLinkDatas = funcLinkDataService.getFuncLinkDatasByFunccode_Cache(funcCode,tenantid);
		for(AbstractVO vo:list) {
			linkUpdate(vo, funcLinkDatas, tenantid);
		}
		
	}

	@Override
	public void linkDelete(Long pk, String funcCode,Long tenantid) throws HDException {
		IFuncLinkDataService funcLinkDataService = (IFuncLinkDataService)AppServiceHelper.findBean("funcLinkDataService");
		List<FuncLinkDataVO> funcLinkDatas = funcLinkDataService.getFuncLinkDatasByFunccode_Cache(funcCode,tenantid);
		for(FuncLinkDataVO funcLinkData:funcLinkDatas) {
			if(DeleteCtrlEnum.NO_CTRL.getCode().equals(funcLinkData.getLdata_del())) {//不控制就是不用管咯
				continue;
			}
			if(DeleteCtrlEnum.NO_DELETE.getCode().equals(funcLinkData.getLdata_del())) {
				if(hasRecord(pk, funcLinkData, tenantid)) {
					throw new HDException("有禁止删除的关联数据");
				}
			}
			if(DeleteCtrlEnum.DELETE.getCode().equals(funcLinkData.getLdata_del())) {
				deleteLinkData(pk, funcLinkData, tenantid);
			}
		}
	}
	
	private boolean hasRecord(Long pk, FuncLinkDataVO funcLinkData,Long tenantid) {
//		Long tenantid = CurrentEnvUtils.getTenantId();
		AbstractVO vo = baseService.queryByPKAndTenantid(funcLinkData.getParent_table(), pk, tenantid);
		if(vo==null)
			return false;
		
		String[] table_fields = funcLinkData.getTable_fields().split(",");				
		String[] parent_fields = funcLinkData.getParent_fields().split(",");
		if(table_fields.length!=parent_fields.length) {
			throw new HDRuntimeException("本表字段编码和被动表字段编码长度不同...");
		}
		
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		
		for(int i=0;i<table_fields.length;i++) {	
			dynaSqlVO.addWhereParam(table_fields[i], vo.get(parent_fields[i]));
		}
		
		if(StringUtils.hasLength(funcLinkData.getLdata_where())) {
			dynaSqlVO.setWhereClause(funcLinkData.getLdata_where());
		}
		
		if(!(ObjectUtil.isNotEmpty(dynaSqlVO.getWhereParamMap())
				&&dynaSqlVO.getWhereParamMap().containsKey(SyConstant.TENANT_STR))){
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		}
		VOSet<AbstractVO> voSet = baseService.query(funcLinkData.getTable_code(), dynaSqlVO);
		return ObjectUtil.isNotEmpty(voSet.getVoList());
	}
	
	private boolean hasRecord(Collection<Long> pks, FuncLinkDataVO funcLinkData, Long tenantid) {
		for(Long s:pks) {//由于涉及到关联表和主表不在同一个库的可能，所以只能循环去查找了
			if(hasRecord(s, funcLinkData, tenantid)) {
				return true;
			}
		}
		return false;
	}

	private void deleteLinkData(Long pk, FuncLinkDataVO funcLinkData, Long tenantid) {
//		Long tenantid = CurrentEnvUtils.getTenantId();
		AbstractVO vo = baseService.queryByPKAndTenantid(funcLinkData.getParent_table(), pk, tenantid);
		if(vo==null)
			return;
		
		String[] table_fields = funcLinkData.getTable_fields().split(",");				
		String[] parent_fields = funcLinkData.getParent_fields().split(",");
		if(table_fields.length!=parent_fields.length) {
			throw new HDRuntimeException("本表字段编码和被动表字段编码长度不同...");
		}
		if(table_fields.length==0) {//都没有条件，让我怎么敢进行删除？
			return;
		} 
		
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		
		for(int i=0;i<table_fields.length;i++) {	
			dynaSqlVO.addWhereParam(table_fields[i], vo.get(parent_fields[i]));
		}
		
		if(StringUtils.hasLength(funcLinkData.getLdata_where())) {
			dynaSqlVO.setWhereClause(funcLinkData.getLdata_where());
		}	
		
		if(!SyConstant.NONTENANTID_TABLE.containsKey(funcLinkData.getTable_code())) {
			dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		}
		
		baseService.delete(new BaseVO(funcLinkData.getTable_code()), dynaSqlVO);
	}
	
	private void deleteLinkData(Collection<Long> pks, FuncLinkDataVO funcLinkData, Long tenantid) {
		for(Long s:pks) {//由于涉及到关联表和主表不在同一个库的可能，所以只能循环去删除了
			deleteLinkData(s, funcLinkData, tenantid);
		}
	}
	
	@Override
	public void linkDelete(Collection<Long> pks, String funcCode, Long tenantid) throws HDException {
		
		IFuncLinkDataService funcLinkDataService = (IFuncLinkDataService)AppServiceHelper.findBean("funcLinkDataService");
		List<FuncLinkDataVO> funcLinkDatas = funcLinkDataService.getFuncLinkDatasByFunccode_Cache(funcCode,tenantid);
		for(FuncLinkDataVO funcLinkData:funcLinkDatas) {
			if(DeleteCtrlEnum.NO_CTRL.getCode().equals(funcLinkData.getLdata_del())) {//不控制就是不用管咯
				continue;
			}
			if(DeleteCtrlEnum.NO_DELETE.getCode().equals(funcLinkData.getLdata_del())) {
				if(hasRecord(pks, funcLinkData, tenantid)) {
					throw new HDException("有禁止删除的关联数据");
				}
			}
			if(DeleteCtrlEnum.DELETE.getCode().equals(funcLinkData.getLdata_del())) {
				deleteLinkData(pks,funcLinkData,tenantid);
			}
		}
		
	}

	@Deprecated
	@Override
	public List<FuncLinkDataVO> getFuncLinkDatasByFunccode(String funcCode,Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam("ldata_isenable", SyConstant.SY_TRUE);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, CurrentEnvUtils.getTenantId());
		VOSet<FuncLinkDataVO> voset = baseService.query(new FuncLinkDataVO(), dynaSqlVO);
		return voset.getVoList();
	}

	@Override
	@Cacheable(value=CacheConstant.CACHE_FUNC_LINK_DATA,key="#funcCode.concat('|').concat(#tenantid)")
	public List<FuncLinkDataVO> getFuncLinkDatasByFunccode_Cache(String funcCode,Long tenantid) {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("func_code", funcCode);
		dynaSqlVO.addWhereParam("ldata_isenable", SyConstant.SY_TRUE);
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR, tenantid);
		VOSet<FuncLinkDataVO> voset = baseService.query(new FuncLinkDataVO(), dynaSqlVO);
		return voset.getVoList();
	}
	
	

}
