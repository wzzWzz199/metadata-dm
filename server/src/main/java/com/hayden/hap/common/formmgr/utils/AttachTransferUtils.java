package com.hayden.hap.common.formmgr.utils;

import com.hayden.hap.common.attach.IFastDfsService;
import com.hayden.hap.common.attach.entity.AttachDataVO;
import com.hayden.hap.common.attach.entity.AttachDfsVO;
import com.hayden.hap.common.attach.itf.IAttachConstants;
import com.hayden.hap.common.attach.itf.IAttachMethodService;
import com.hayden.hap.common.attach.server.AbstractAttachServer;
import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.common.itf.IBaseService;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.itf.ITableDefService;
import com.hayden.hap.common.db.util.DBSqlUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author haocs
 * @date 2019年8月5日
 */
@Service("attachTransferUtils")
public class AttachTransferUtils {

	
	private static final Logger logger = LoggerFactory.getLogger(AttachTransferUtils.class);
	
	@Autowired
	private IBaseService baseService;
	@Autowired
	private IFastDfsService fastDfsService;
	@Autowired
	private IFuncService funcService;
	@Autowired
	private ITableDefService tableDefService;
	@Autowired
	private IAttachMethodService attachMethodService;
	
	
    /**
     * 1. 查租户下模块下 所有附件
     * 2. 获取附件位置并上传,并新增一条数据库记录
     * 3. 根据附件VO 中得业务funcode 和 id  更新业务附件字段 
     * @throws HDException 
     * @throws IOException 
     * 
     */
	public HashMap<String, String> attachTransfer() throws HDException {

		HashMap<String, String> map = new HashMap<>();
		// 所有租户
		List<TenantVO> tenants = getTenants();
		if(tenants==null || tenants.isEmpty()) {
			return null;
		}
		long currentTimeMillis = System.currentTimeMillis();
		for (TenantVO tenantVO : tenants) {
			Long tenantpk = tenantVO.getTenantpk();
			// 所有产品
			List<String> products = this.getProducts(tenantpk);
			
			
			for (String moduleOrFuncCode : products) {
				
				if(moduleOrFuncCode.isEmpty()) {
					continue;
				}
				logger.error("耗时：" +String.valueOf(currentTimeMillis-System.currentTimeMillis())+" 开始处理 租户 "+tenantpk + " 功能 "+moduleOrFuncCode);
				// 先功能再模块
				List<AttachDataVO> data = this.getData(moduleOrFuncCode, tenantpk);
				if (data == null || data.isEmpty()) {
					continue;
				}
				map = this.getData(data, tenantpk);
				logger.error("耗时：" +String.valueOf(currentTimeMillis-System.currentTimeMillis())+" 处理结束  租户 "+tenantpk + " 功能 "+moduleOrFuncCode);
				
				
				
				
				logger.error("迁移成功！", JsonUtils.writeValueAsString(map));
			}
		}
		return map;

	}
	/**
	 * 模块
	 * @return 
	 * @author haocs
	 * @date 2019年8月8日
	 */
	private  List<String> getProducts(long tenantid) {
		// 先查功能数据字典
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("dict_code", "transfer_attach_funcOrModule");
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
	/**
	 * 租户下所有附件
	 * @param module
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @date 2019年8月6日
	 */
	public List<AttachDataVO> getData(String module,Long tenantid) {
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam("func_code",module);
		sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
		List<AttachDataVO> voList = baseService.query(new AttachDataVO(), sql).getVoList();
		
		if(voList==null || voList.isEmpty()) {
			  sql.removeWhereParam("func_code");
			  sql.addWhereParam("modulecode", module);
			  voList = baseService.query(new AttachDataVO(), sql).getVoList();
		}
		return voList;
	}
	/**
	 * 
	 * @param attachs
	 * @return
	 * @throws HDException 
	 * @author haocs
	 * @throws IOException 
	 * @date 2019年8月6日
	 */
	public  HashMap<String, List<AttachDataVO>> getAttachMapByFuncAndCol(List<AttachDataVO> attachs,Long tenantid) throws HDException {
		HashMap<String, List<AttachDataVO>> tableBypks = new HashMap<>();
//		ArrayList<AttachDfsVO> insertAttachList = new ArrayList<>();
		for (AttachDataVO attachDataVO : attachs) {
			String att_colcode = attachDataVO.getAtt_colcode();
			Long att_ownerid = attachDataVO.getAtt_ownerid();
			String func_code = attachDataVO.getFunc_code();
			if (att_ownerid == null || StringUtils.isBlank(func_code) || StringUtils.isBlank(att_colcode)) {
				continue;
			}

			String att_d_url = attachDataVO.getAtt_d_url();
			String att_d_uuid = attachDataVO.getAtt_d_uuid();
			String att_d_type = attachDataVO.getAtt_d_type();
			String att_d_name = attachDataVO.getAtt_d_name();

			
			AttachDfsVO attachUploadFastDfs = null;
			String absolutePath = null;
			try {
				if(att_d_url.contains("administrator")||att_d_url.startsWith("/myfiles-dev/") || att_d_url.startsWith("/myfiles-test/")) {
						// 链接服务器下载
					  ByteArrayOutputStream serverData = getServerData(attachDataVO, tenantid);
					  if(serverData!=null) {
						  attachUploadFastDfs= fastDfsService.attachUploadFastDfs(serverData.toByteArray(),att_d_name,att_d_type,tenantid);
					  }
					
				}else {
					absolutePath = att_d_url + att_d_uuid + att_d_type;
					attachUploadFastDfs = fastDfsService.attachUploadFastDfs(absolutePath, att_d_name, att_d_type,tenantid);
				}
			}catch(HDException e) {
				logger.error(e.getMessage());
			}
			if (attachUploadFastDfs != null) {
				String key = attachUploadFastDfs.getDfs_file_key();
				attachDataVO.setString("key", key);
			}
//			insertAttachList.add(attachUploadFastDfs);
			String mapKey = func_code + "-" + att_colcode;
			if (tableBypks.containsKey(mapKey)) {
				List<AttachDataVO> list = tableBypks.get(mapKey);
				if(attachDataVO!=null) {
					list.add(attachDataVO);
				}
				tableBypks.put(func_code, list);
			} else {
				List<AttachDataVO> list = new ArrayList<>();
				if(attachDataVO!=null) {
					list.add(attachDataVO);
				}
				tableBypks.put(mapKey, list);
			}
		}
//		if(!insertAttachList.isEmpty()) {
//			baseService.insertBatch(insertAttachList);
//		}
		return tableBypks;
	}
	/**
	 * 从文件服务器下载到本地
	 * @param attachDataVO
	 * @param tenantid
	 * @return
	 * @throws HDException
	 * @throws IOException 
	 * @author haocs
	 * @date 2019年8月6日
	 */
	public ByteArrayOutputStream getServerData(AttachDataVO attachDataVO,Long tenantid) throws HDException{
		// 获得server
		AbstractAttachServer server = attachMethodService.getAttachServer(attachDataVO.getFunc_code(),
				attachDataVO.getAtt_colcode(), tenantid);
		ByteArrayOutputStream byteArrayOutputStream = null;
		FileOutputStream output = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			server.downloadFile(attachDataVO.getAtt_d_url() + attachDataVO.getAtt_d_uuid()+ attachDataVO.getAtt_d_type(),byteArrayOutputStream);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}finally {
			if(output!=null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.error("流关闭失败!",e.getMessage());
				}
			}
		}
		return byteArrayOutputStream;
	}
	/**
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception 
	 * @author haocs
	 * @date 2019年8月6日
	 */
	public static byte[] readStream(InputStream inStream) throws Exception{	
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];	  
	    int len = -1;	
	    while((len = inStream.read(buffer)) != -1){	  
	      outStream.write(buffer, 0, len);	  
	    }	  
	    outStream.close();	  
	    inStream.close();
	    return outStream.toByteArray();	  
	}
	/**
	 * 通过附件找业务数据
	 *  
	 * @author haocs
	 * @throws HDException 
	 * @throws IOException 
	 * @date 2019年8月6日
	 */
	@Transactional
	public HashMap<String, String> getData(List<AttachDataVO> attachs, Long tenantid) throws HDException {
		long curr = System.currentTimeMillis();
		HashMap<String, String> updateDetailInfo = new HashMap<>();
		if (attachs == null || attachs.isEmpty()) {
			return updateDetailInfo;
		}
		// 将已经处理过得 去掉
		List<AttachDataVO> delProcessed = this.delProcessed(attachs,tenantid);
		// 上传
		HashMap<String, List<AttachDataVO>> tableBypks = this.getAttachMapByFuncAndCol(delProcessed,tenantid);
		
		
		if (tableBypks.isEmpty()) {
			return updateDetailInfo;
		}
		logger.error("耗时：" +String.valueOf(curr-System.currentTimeMillis())+" 开始处理 租户 "+tenantid + " 附件数量 "+attachs.size() +" 需要处理得字段 "+JsonUtils.writeValueAsString(tableBypks.keySet())+"  []  "+tableBypks.size());
		// 更新历史字段为""
		for (String mapKey : tableBypks.keySet()) {
			if(StringUtils.isEmpty(mapKey)) {
				continue;
			}
			String[] split = mapKey.split("-");
			if(split==null || split.length<=1) {
				continue;
			}
			String funcCode = split[0];
			if(StringUtils.isEmpty(funcCode)) {
				continue;
			}
			String colCode = split[1];
			if(StringUtils.isEmpty(colCode)) {
				continue;
			}

			String tableName = funcService.getQueryTableNameOfFunc(funcCode, tenantid);

			DynaSqlVO dynaSqlVO = new DynaSqlVO();
			dynaSqlVO.addWhereClause("  CHAR_LENGTH("+colCode+") <6 ");
			logger.error( "开始查询 租户 "+tenantid + " table "+tableName +" col "+colCode + " dynasqlVO " +JsonUtils.writeValueAsString(dynaSqlVO) );
			List<AbstractVO> voList = baseService.query(tableName,dynaSqlVO ).getVoList();
			
			if(voList==null || voList.isEmpty()) {
				continue;
			}
			logger.error( "开始处理 租户 "+tenantid + " table "+tableName +" col "+colCode + "数据量" +voList.size() );
			
			for (AbstractVO abstractVO : voList) {
				abstractVO.setString(colCode, "");
			}
			DynaSqlVO updateSql = new DynaSqlVO();
			List<String> cols = new ArrayList<>();
			cols.add(colCode);
			updateSql.setSqlColumnList(cols);
			int updateBatch = baseService.updateBatch(voList,updateSql);
			logger.error("耗时：" +String.valueOf(curr-System.currentTimeMillis())+" 开始处理 租户 "+tenantid + " table "+tableName +" col "+colCode + "更新为空数量" +updateBatch );
		}
		for (String mapKey : tableBypks.keySet()) {

			List<AttachDataVO> attachlist = tableBypks.get(mapKey);
			if (attachlist.isEmpty()) {
				continue;
			}
			String[] split = mapKey.split("-");
			if(split==null || split.length<=1) {
				continue;
			}
			String funcCode = split[0];
			if(StringUtils.isEmpty(funcCode)) {
				continue;
			}
			String colCode = split[1];
			if(StringUtils.isEmpty(colCode)) {
				continue;
			}

			String tableName = funcService.getQueryTableNameOfFunc(funcCode, tenantid);
			String pkCol = tableDefService.getPkColName(tableName);
			List<Long> pks = VOCollectionUtils.getPropList(attachlist, "att_ownerid", Long.class);

			String conditionSql = DBSqlUtil.getConditionSql(pkCol, pks, true);
			DynaSqlVO sql = new DynaSqlVO();
			sql.addWhereClause(conditionSql);
			sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
			List<AbstractVO> abstractVOList = baseService.query(tableName, sql).getVoList();
			if (abstractVOList == null || abstractVOList.isEmpty()) {
				continue;
			}
			Map<Long, AbstractVO> maps = VOCollectionUtils.groupedByProp(abstractVOList, pkCol, Long.class);

//			List<AbstractVO> updateVOs = new ArrayList<>();
			HashMap<Long, AbstractVO> ownerMapById = new HashMap<>();
			
			for (AttachDataVO attach : attachlist) {

				String att_colcode = attach.getAtt_colcode();
				Long att_ownerid = attach.getAtt_ownerid();
				String key = attach.getString("key");
				if (StringUtils.isBlank(key)) {
					continue;
				}
				if (maps.containsKey(att_ownerid)) {
					// 多附件情况
					if(ownerMapById.containsKey(att_ownerid)) {
						// 业务VO 数据
						AbstractVO abstractVO1 = ownerMapById.get(att_ownerid);
						String attach_key1 = abstractVO1.getString(att_colcode);
						// 防止 多附件时 被替换;
						String tempKey = key+IAttachConstants.COMMA+attach_key1;
						abstractVO1.setString(att_colcode, tempKey);
						
					}else {
						AbstractVO abstractVO = maps.get(att_ownerid);
						abstractVO.setTableName(tableName);
						abstractVO.setString(att_colcode, key);
						ownerMapById.put(att_ownerid, abstractVO);
					}
					
				}
			}
			logger.error(" 主键-VO "+JsonUtils.writeValueAsString(ownerMapById));
			ArrayList<AbstractVO> updateVOs = new ArrayList<AbstractVO>(ownerMapById.values());
			DynaSqlVO updateSql = new DynaSqlVO();
			List<String> cols = new ArrayList<>();
			cols.add(colCode);
			updateSql.setSqlColumnList(cols);
			logger.error("VOs "+updateVOs.size()+" 耗时：" +String.valueOf(curr-System.currentTimeMillis())+" 开始处理 租户 "+tenantid + " table "+tableName +" col "+colCode + "更新 sql " +JsonUtils.writeValueAsString(updateSql) );
			int updateBatch = baseService.updateBatch(updateVOs, updateSql);
			updateDetailInfo.put(mapKey, "更新 【" + updateBatch + "】 条" + pks);
			logger.error("耗时：" +String.valueOf(curr-System.currentTimeMillis())+" 开始处理 租户 "+tenantid + " table "+tableName +" col "+colCode + "更新AttahcKey数量" +updateBatch );
		}
		return updateDetailInfo;
	}

	/**
	 * 删除已经恢复过得附件
	 * @param attachs
	 * @param tenantid
	 * @return 
	 * @author haocs
	 * @date 2019年8月7日
	 */
	private List<AttachDataVO> delProcessed(List<AttachDataVO> attachs,Long tenantid) {
			HashMap<String,List<Long>> pks = new HashMap<>(); 
		// 附件 Map funcode-colcode : attachDataVO
		for (AttachDataVO attachDataVO : attachs) {
			
			String func_code = attachDataVO.getFunc_code();
			String att_colcode = attachDataVO.getAtt_colcode();
			Long att_ownerid = attachDataVO.getAtt_ownerid();
			String key = func_code+"-"+att_colcode;
			if (pks.containsKey(key)) {
				List<Long> list = pks.get(key);
				list.add(att_ownerid);
				pks.put(key, list);
			} else {
				List<Long> list = new ArrayList<>();
				list.add(att_ownerid);
				pks.put(key, list);
			}
		}
		List<String> procced = new ArrayList<>();
		// 业务VO将已处理得funccode-colcode 剔除 
		for (String key : pks.keySet()) {
			
			List<Long> list = pks.get(key);
			String[] split = key.split("-");
			String funcCode = split[0];
			String colCode = split[1];
			if(list.isEmpty()) {
				continue;
			}
			String table = funcService.getQueryTableNameOfFunc(funcCode, tenantid);
			String pkColName = tableDefService.getPkColName(table);
			DynaSqlVO sql = new DynaSqlVO();
		    String conditionSql = DBSqlUtil.getConditionSql(pkColName, list, true);
			sql.addWhereClause(conditionSql);
			sql.addWhereParam(SyConstant.TENANT_STR, tenantid);
			List<AbstractVO> voList = baseService.query(table, sql).getVoList();
			
			if(voList==null || voList.isEmpty()) {
				continue;
			}
			int size = 0;
			for (AbstractVO abstractVO : voList) {
				String attachVal = abstractVO.getString(colCode);
				if(StringUtils.isBlank(attachVal) || !attachVal.startsWith("M00")) {
					break;
				}
				size++;
			}
			// 已处理
			if(size==voList.size()) {
				procced.add(key);
			}
		}
		if(procced.isEmpty()) {
			return attachs;
		}
		//业务VO将已处理得funccode-colcode 剔除 
//		Map<String, List<AttachDataVO>> listMap = VOCollectionUtils.groupedListByProp(attachs, "func_code", String.class);
		ArrayList<AttachDataVO> attachFast = new ArrayList<>();
		
		for (AttachDataVO attachDataVO : attachs) {
			
			String func_code = attachDataVO.getFunc_code();
			String att_colcode = attachDataVO.getAtt_colcode();
			String source_key = func_code+"-"+att_colcode;
				for (String key : procced) {
					if(!source_key.equals(key)) {
						attachFast.add(attachDataVO);
					}
				}
		}
		
//		for (String key : procced) {
//			String[] split = key.split("-");
//			String funcCode = split[0];
//			String colCode = split[1];
//			if(listMap.containsKey(funcCode)) {
//				List<AttachDataVO> list = listMap.get(funcCode);
//				for (AttachDataVO attachDataVO : list) {
//					String att_colcode = attachDataVO.getAtt_colcode();
//					 if(!colCode.equals(att_colcode)) {
//						 attachFast.add(attachDataVO);
//					 }
//				}
//			}
//		}
		return attachFast;
	}
	
	
}
