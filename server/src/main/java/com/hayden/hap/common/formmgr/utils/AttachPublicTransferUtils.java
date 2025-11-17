package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.attach.entity.AttachDataVO;
import com.hayden.hap.common.attach.entity.AttachShareInputConfigVO;
import com.hayden.hap.common.attach.entity.AttachShareNodeVO;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.enumerate.ElementTypeEnum;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.itf.IFormItemService;
import com.hayden.hap.common.func.itf.IFuncService;
import com.hayden.hap.common.serial.JsonUtils;
import com.hayden.hap.common.tenant.entity.TenantVO;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.common.utils.VOCollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 附件共享 迁移
 * @author haocs
 * @date 2019年11月18日
 */
@Service("attachPublicTransferUtils")
public class AttachPublicTransferUtils {

	
	private static final Logger logger = LoggerFactory.getLogger(AttachPublicTransferUtils.class);
	
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IFuncService funcService;
	@Autowired
	private IFormItemService formItemService;
	@Autowired
	private ITableDefService tableDefService;
	
	
	
	/**
	 * 附件共享 迁移
	 * @return
	 * @throws HDException 
	 * @author haocs
	 * @date 2019年11月14日
	 */
	public HashMap<String, String> copyPublicAttach() throws HDException {
	
		logger.error("开始处理 附件共享 迁移 ");
		HashMap<String, String> map = new HashMap<>();
		// 所有租户
		List<TenantVO> tenants = this.getTenants();
		if(tenants==null || tenants.isEmpty()) {
			return null;
		}
		 
		for (TenantVO tenantVO : tenants) {
			
			Long tenantpk = tenantVO.getTenantpk();
			// 所有产品
			List<String> products = this.getPublicAttach(tenantpk);
			if(products==null || products.isEmpty()) continue;
			HashMap<String, List<FormItemVO>> fileAttachMaps = this.getFileAttach(products, tenantpk);
			//附件表, 当前租户 功能 主键
			if(fileAttachMaps.isEmpty())continue;
			
			for (String sourceKey : fileAttachMaps.keySet()) {
				
				String[] codes = sourceKey.split("-");
				if(codes==null) {
					continue;
				}
				String sourceFunc = codes[0];
				String sourceAttachCode = codes[1];
				String sourceCol = codes[2];
				String targetAttachCode = codes[3];
				String targetCol = codes[4];
				String sourceTid = codes[5];
				if(StringUtils.isNotBlank(sourceFunc) && StringUtils.isNotBlank(sourceCol) && StringUtils.isNotBlank(sourceTid)
						&& StringUtils.isNotBlank(sourceAttachCode) && StringUtils.isNotBlank(targetCol) && StringUtils.isNotBlank(targetAttachCode)) {
					
					DynaSqlVO dy = new DynaSqlVO();
					dy.addWhereParam("func_code", sourceFunc);
					dy.addWhereParam("att_colcode", sourceAttachCode);
					dy.addWhereParam(SyConstant.TENANT_STR, sourceTid);
					List<AttachDataVO> voList = baseService.query(new AttachDataVO(), dy).getVoList();
					
					if(voList==null || voList.isEmpty()) {
						continue;
					}
					// 源功能数据id
					List<Long> ownerids = VOCollectionUtils.getPropList(voList, "att_ownerid", Long.class);
					if(ownerids==null || ownerids.isEmpty()) {
						continue;
					}
					String tableName = funcService.getQueryTableNameOfFunc(sourceFunc, Long.parseLong(sourceTid));
					String pkColName = tableDefService.getPkColName(tableName);
					DynaSqlVO sql = new DynaSqlVO();
					sql.addWhereParam(SyConstant.TENANT_STR, tenantpk);
					String conditionSql = DBSqlUtil.getConditionSql(pkColName, ownerids, true);
					sql.addWhereClause(conditionSql);
					// 源功能数据					
					List<AbstractVO> voListSources = baseService.query(tableName, sql).getVoList();
					
					
					List<FormItemVO> list = fileAttachMaps.get(sourceKey);
					
					if(list==null || list.isEmpty()) {
						continue;
					}
					// 从功能数据
					List<String> propList = VOCollectionUtils.getPropList(voListSources, sourceCol, String.class);
					DynaSqlVO targetDy = new DynaSqlVO();
					String targetConditionSql = DBSqlUtil.getConditionSql(targetCol, propList, true);
					targetDy.addWhereClause(targetConditionSql);
					String targetFormCode = list.get(0).getForm_code();
					String targetTableName = funcService.getQueryTableNameOfFunc(targetFormCode, Long.parseLong(sourceTid));
					List<AbstractVO> targetVoList = baseService.query(targetTableName, targetDy).getVoList();
//					HashMap<String, AbstractVO> targetMap = (HashMap<String, AbstractVO>) VOCollectionUtils.groupedByProp(targetVoList, targetCol, String.class);
					
					if(targetVoList==null || targetVoList.isEmpty()) {
						continue;
					}
					// 要更新得共享附件
					List<AbstractVO> targetListFin = new ArrayList<>();
					// 有附件.
					for (AbstractVO abstractVO : voListSources) {
						String attachs = abstractVO.getString(sourceAttachCode);
						String sourceColVal = abstractVO.getString(sourceCol);
						if(StringUtils.isEmpty(sourceColVal) || StringUtils.isEmpty(attachs)) continue;
						
						for (AbstractVO targetVO : targetVoList) {
							
							String targetColVal = targetVO.getString(targetCol);
							if(StringUtils.isEmpty(targetColVal)) continue;
							
							if(targetColVal.equals(sourceColVal)) {
								targetVO.setString(targetAttachCode, attachs);
								targetListFin.add(targetVO);
							}
							
						}
					}
					if(targetListFin==null || targetListFin.isEmpty()) {
						continue;
					}
					DynaSqlVO dysqlTarget = new DynaSqlVO();
					List<String> colList = new ArrayList<>();
					colList.add(targetAttachCode);
					dysqlTarget.setSqlColumnList(colList);
					int updateBatch = baseService.updateBatch(targetListFin, dysqlTarget);
					map.put(targetTableName, updateBatch +" "+tableName);
					logger.error("从  "+tableName +" 到  " +targetTableName +"  共享迁移 "+updateBatch +"条 "+codes);
				}
				
			}
			
		}
		logger.error("结束处理 附件共享 迁移 ");
		return map;
		
	}
	/**
	 * 表单字段
	 * @param products
	 * @param tenantpk
	 * @return 
	 * @author haocs
	 * @throws HDException 
	 * @date 2019年11月14日
	 */
	private HashMap<String,List<FormItemVO>> getFileAttach(List<String> products,Long tenantpk) {
		HashMap<String,List<FormItemVO>> mapItem = new HashMap<>();
		for (String formCode : products) {
			List<? extends FormItemVO> formItemsByFormcode = formItemService.getFormItemsByFormcode(formCode, tenantpk);
			if(formItemsByFormcode==null || formItemsByFormcode.isEmpty()) {
				continue;
			}
			if(mapItem.containsKey(formCode)) continue;
				
			for (FormItemVO itemVO : formItemsByFormcode) {// 根据输入设定决定是否显示附件数量
				if (ElementTypeEnum.FILE.getCode().equals(itemVO.getFitem_input_element())
						|| ElementTypeEnum.IMG.getCode().equals(itemVO.getFitem_input_element())
						 || ElementTypeEnum.FAST_FILE.getCode().equals(itemVO.getFitem_input_element())
						   || ElementTypeEnum.FAST_IMG.getCode().equals(itemVO.getFitem_input_element())
						     || StringUtils.isNotBlank(itemVO.getFitem_input_config())) {
					String fitem_input_config = itemVO.getFitem_input_config();
					try {
						AttachShareInputConfigVO configVO = JsonUtils.parse(fitem_input_config, AttachShareInputConfigVO.class);
						mapItem = this.getKey(mapItem, configVO, itemVO, tenantpk);
					} catch (HDException e) {
						logger.error("转换共享VO失败！",e);
					}
				}
			}
		}
		return mapItem;
	}
	/**
	 * key-val 匹配
	 * @param mapItem
	 * @param configVO
	 * @param itemVO
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @date 2019年11月18日
	 */
	private HashMap<String,List<FormItemVO>> getKey(HashMap<String,List<FormItemVO>> mapItem,AttachShareInputConfigVO configVO,FormItemVO itemVO,Long tenantid) {
		if(configVO==null) {
			return mapItem;
		}
		// 是共享
//		if(!"1".equals(configVO.getIsSpread())) {
//			return mapItem;
//		}
		String key = "";
		String fitem_code = itemVO.getFitem_code();
		// 同表
		if(null != configVO.getPoolOneTable_Node()) {
			AttachShareNodeVO poolOneTable_Node = configVO.getPoolOneTable_Node();
			String shareFuncCode = poolOneTable_Node.getShareFuncCode();
			String shareItemCode = poolOneTable_Node.getShareItemCode();
			String currentItemCode = poolOneTable_Node.getCurrentItemCode();
			String shareAttachColCode = poolOneTable_Node.getShareAttachColCode();
			key = shareFuncCode+"-"+shareAttachColCode+"-"+shareItemCode+"-"+fitem_code+"-"+currentItemCode+"-"+tenantid;
		}
		// 不同表
		if(null != configVO.getPoolManyTable_Node()) {
			AttachShareNodeVO poolManyTable_Node = configVO.getPoolManyTable_Node();
			String shareAttachColCode = poolManyTable_Node.getShareAttachColCode();
			String shareFuncCode = poolManyTable_Node.getShareFuncCode();
			String shareItemCode = poolManyTable_Node.getShareItemCode();
			String currentItemCode = poolManyTable_Node.getCurrentItemCode();
			key = shareFuncCode+"-"+shareAttachColCode+"-"+shareItemCode+"-"+fitem_code+"-"+currentItemCode+"-"+tenantid;
		}
		if(StringUtils.isNotBlank(key) && mapItem.containsKey(key)) {
			List<FormItemVO> list = mapItem.get(key);
			list.add(itemVO);
		}else if(StringUtils.isNotBlank(key)){
			List<FormItemVO> list = new ArrayList<>();
			list.add(itemVO);
			mapItem.put(key, list);
		}
		// 多表
		if(null != configVO.getSingle_List()) {
			List<AttachShareNodeVO> single_List = configVO.getSingle_List();
			
			for (AttachShareNodeVO attachShareNodeVO : single_List) {
				String shareFuncCode = attachShareNodeVO.getShareFuncCode();
				String shareAttachColCode = attachShareNodeVO.getShareAttachColCode();
				String shareItemCode = attachShareNodeVO.getShareItemCode();
				String currentItemCode = attachShareNodeVO.getCurrentItemCode();
				key = shareFuncCode+"-"+shareAttachColCode+"-"+shareItemCode+"-"+fitem_code+"-"+currentItemCode+"-"+tenantid;
				if(StringUtils.isEmpty(key)) continue;
				if(mapItem.containsKey(key)) {
					List<FormItemVO> list = mapItem.get(key);
					list.add(itemVO);
				}else {
					List<FormItemVO> list = new ArrayList<>();
					mapItem.put(key, list);
				}
			}
		}
		return mapItem;
	}
   /**
	 * 模块
	 * @return 
	 * @author haocs
	 * @date 2019年8月8日
	 */
	private  List<String> getPublicAttach(long tenantid) {
		// 先查功能数据字典
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("dict_code", "attach_public_attach");
		dynaSqlVO.addWhereParam(SyConstant.TENANT_STR,tenantid);
		List<DictDataVO> voList = baseService.query(new DictDataVO(), dynaSqlVO).getVoList();
		List<String> propListNotNull = VOCollectionUtils.getPropListNotNull(voList, "dict_data_code", String.class);
		return propListNotNull;
	}
	/**
	 * 租户
	 * @return 
	 * @author haocs
	 * @date 2019年8月6日
	 */
	public  List<TenantVO> getTenants() {
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("isenable", SyConstant.SY_TRUE);
		List<TenantVO> voList = baseService.query(new TenantVO(), dynaSqlVO).getVoList();
		return voList;
	}
	 
 
	
	
}
