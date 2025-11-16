package com.hayden.hap.export.service;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.BaseVO;
import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.dict.entity.DictDataVO;
import com.hayden.hap.common.func.entity.FuncLinkVO;
import com.hayden.hap.common.menu.entity.MenuVO;
import com.hayden.hap.common.tenant.entity.TenantVO;
import com.hayden.hap.common.utils.SyConstant;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.entity.MetaRelationVO;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.export.entity.MetaDataVO;
import com.hayden.hap.export.entity.MetaTypeRelationVO;
import com.hayden.hap.export.entity.MetaTypeVO;
import com.hayden.hap.serial.JsonUtils;
import com.hayden.hap.upgrade.entity.ShowModuleVO;
import com.hayden.hap.utils.CurrentEnvUtils;
import com.hayden.hap.utils.MetaDataRelation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service("exportMetaDataService")
public class ExportMetaDataServiceImpl implements IExportMetaDataService {

	@Autowired
	MetaDataRelation metaDataRelation;
	@Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;
    @Autowired
    private DataSourceCreator dataSourceCreator;
    @Value("${META.TYPE}")
    private String METATYPE;
    @Value("${METATYPE_RELATION}")
    private String METATYPE_RELATION;
    @Value("${SENIOR_EXPORT_ALLFIELDS:false}")
    private boolean SENIOR_EXPORT_ALLFIELDS;
	@Override
	public List<MetaTypeVO> getMetaDataType() throws HDException {
		List<MetaTypeVO> metaType = (List<MetaTypeVO>) JsonUtils.parseArrayInit(METATYPE, MetaTypeVO.class);
		return metaType;
	}
	@Override
	public Map<String,Object> queryMetaData(String project, String env, String metaType, String metaDataCode,ReqParamVO reqParam, Long tenantid)
			throws HDException {
		List<MetaTypeVO> metaTypeVO = getMetaDataType();
    	MetaTypeVO metatype = null;
    	for (MetaTypeVO metaTypeVO2 : metaTypeVO) {
			if (metaTypeVO2.getType().equals(metaType)) {
				metatype = metaTypeVO2;
				break;
			}
		}
    	if (metatype==null) {
    		throw new HDException("根据请求参数metaType未找到对应元数据类型，请检查apollo配置");
		}
		MetaRelationVO metaDataRelationVO = metaDataRelation.getMetaDataRelation(metaType);
		String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
        dataSourceCreator.getDataSource(dataSourceId);
        Class queryVO;
		try {
			queryVO = Class.forName(metaDataRelationVO.getNodevo());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new HDException("根据元数据类型找到对应元数据VO 加载异常，请检查apollo配置",e);
		}
        DynaSqlVO sql = new DynaSqlVO();
//        sql.addWhereParam(queryColumn, metaDataCode);
        String[] queryColumns = metatype.getListItem().split(",");
        StringBuffer querySql = new StringBuffer();
        querySql.append("(");
        for (int i = 0; i < queryColumns.length; i++) {
        	if (i!=0) {
        		querySql.append(" or ");
			}
        	querySql.append(queryColumns[i]+" like '"+metaDataCode+"%'");
        }
        querySql.append(")");
        sql.addWhereClause(querySql.toString());
        sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
        if(reqParam.getPage()!=null) {
			int page = reqParam.getPage();
			if(page < 1)
				page = 1;
			int pageSize = reqParam.getRows()!=null?reqParam.getRows():20;
			sql.createPage(page, pageSize);
		}
        VOSet<BaseVO> vOSet = simpleJdbcTemplateSupportDao.query(queryVO, sql, dataSourceId);
        if (ObjectUtil.isNotNull(vOSet) && ObjectUtil.isNotNull(vOSet.getVoList())) {
        	Map<String,Object> result = new HashMap<String,Object>();
        	List<MetaDataVO> metaDatas = new ArrayList<MetaDataVO>();
        	for(BaseVO base :vOSet.getVoList()) {
        		MetaDataVO metaData = new MetaDataVO();
        		metaData.setMetaDataType(metaType);
        		try {
					metaData.setMetaDataCode(base.getString(metatype.getListItem().split(",")[0]));
					metaData.setMetaDataName(base.getString(metatype.getListItem().split(",")[1]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new HDException("META.TYPE元数据类型配置listitem有误，请检查apollo配置",e);
				}
        		metaDatas.add(metaData);
        	}
        	result.put("page", vOSet.getPage());
        	result.put("voList", metaDatas);
        	return result;
        }
		return null;
	}
	@Override
	public void exportMetaData(HttpServletResponse response, String project, String env, String metaType,
			List<MetaDataVO> metaDatas, Long tenantid) throws HDException {
		// TODO Auto-generated method stub
		//设置文件下载及压缩文件名通用METADATA.zip 将要导出的元数据都压缩到一起
		response.setContentType("application/octet-stream");
	    response.setHeader("Content-Disposition", "attachment;filename=METADATA.zip");
	    try {
			ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
			String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
			dataSourceCreator.getDataSource(dataSourceId);
			metaDatas = handleALLAppType(metaDatas,dataSourceId,tenantid);
			metaDatas = handleExportAllFromMenu(metaDatas, dataSourceId,tenantid);
			//查询元数据及关联导出元数据
			for (MetaDataVO metaDataVO : metaDatas) {
				//获取对应环境数据源及初始元数据
				MetaRelationVO metaDataRelationVO = metaDataRelation.getMetaDataRelation(metaDataVO.getMetaDataType());
				String queryColumn = metaDataRelationVO.getExportColumn();
		        Class queryVO;
				try {
					queryVO = Class.forName(metaDataRelationVO.getNodevo());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					throw new HDException("根据元数据类型找到对应元数据VO 加载异常，请检查apollo配置",e);
				}
		        DynaSqlVO sql = new DynaSqlVO();
		        sql.addWhereParam(queryColumn, metaDataVO.getMetaDataCode());
		        sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
		        VOSet<BaseVO> vOSet = simpleJdbcTemplateSupportDao.query(queryVO, sql, dataSourceId);
		        if (vOSet.getVoList().size()==0){
		        	throw new HDException("导出的元数据在指定租户不存在，请确定导出数据的正确性");
				}
		        String metaData="";
				try {
					metaData = JsonUtils.writeValueAsString(vOSet.getVoList());
				} catch (HDException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        InputStream in = new ByteArrayInputStream(metaData.getBytes("UTF-8"));
		        String fileName=metaDataVO.getMetaDataType()+"-"+metaDataVO.getMetaDataCode()+"-"+new Date().getTime()+"-"+project+"-"+env+"-"+CurrentEnvUtils.getUserCode();
		        if (tenantid.longValue()!=1){
		        	fileName = fileName+"-"+tenantid;
				}
//		        if (StringUtils.isNotEmpty(metaDataVO.getMetaDataLinkType())) {
//		        	fileName+="-ALL";
//				}
		        ZipEntry entry = new ZipEntry(fileName+".hdml");
		        zipOut.putNextEntry(entry);
		        int nNumber;
		        byte[] buffer = new byte[1024];
		        while ((nNumber = in.read(buffer)) != -1) {
		        	zipOut.write(buffer, 0, nNumber);
		        }
		        in.close();
		        if (metaDataRelationVO.getChildNodes()!=null) {
		        	exportChildMetaData(zipOut, metaDataRelationVO, dataSourceId, metaDataVO,vOSet.getVoList(),project, env,tenantid);
				}
		        if (StringUtils.isNotEmpty(metaDataVO.getMetaDataLinkType())) {
		        	MetaTypeRelationVO typeRelation = getMetaTypeRelationVO(metaDataVO.getMetaDataType());
		        	typeRelation = handleMetaTypeRelationData(typeRelation, vOSet.getVoList(), dataSourceId,tenantid);
		        	String[] linkType = metaDataVO.getMetaDataLinkType().split(",");
		        	for (int i = 0; i < linkType.length; i++) {
		        		MetaRelationVO metaDataLinkRelationVO = metaDataRelation.getMetaDataRelation(linkType[i]);
		        		List<BaseVO> linkMetaData = getLinkTypeMetaData(typeRelation,linkType[i]);
		        		if (linkMetaData!=null) {
		        			for (BaseVO baseVO : linkMetaData) {
			        			List<BaseVO> tempBaseList = new ArrayList<BaseVO>();
			        			tempBaseList.add(baseVO);
			        			String linkData="";
								try {
									linkData = JsonUtils.writeValueAsString(tempBaseList);
								} catch (HDException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						        InputStream inLink = new ByteArrayInputStream(linkData.getBytes("UTF-8"));
						        fileName=linkType[i]+"-"+baseVO.getString(metaDataLinkRelationVO.getExportColumn())+"-"+new Date().getTime()+"-"+project+"-"+env+"-"+CurrentEnvUtils.getUserCode();
						        if (tenantid.longValue()!=1){
						        	fileName = fileName+"-"+tenantid;
								}
						        ZipEntry entryLink = new ZipEntry(fileName+".hdml");
						        zipOut.putNextEntry(entryLink);
						        int nNumberLink;
						        byte[] bufferLink = new byte[1024];
						        while ((nNumberLink = inLink.read(bufferLink)) != -1) {
						        	zipOut.write(bufferLink, 0, nNumberLink);
						        }
						        inLink.close();
						        MetaDataVO linkmetaDataVO = new MetaDataVO();
						        linkmetaDataVO.setMetaDataCode(baseVO.getString(metaDataLinkRelationVO.getExportColumn()));
						        linkmetaDataVO.setMetaDataLinkType(linkType[i]);
						        exportChildMetaData(zipOut, metaDataLinkRelationVO, dataSourceId, linkmetaDataVO,tempBaseList,project, env,tenantid);
//				        		exportLinkMetaData(zipOut,metaDataLinkRelationVO,dataSourceId,vOSet.getVoList());
							}
						}
					}
				}
			}
	        
	        zipOut.flush();
	        zipOut.close();
		} catch (Exception e) {
			throw new HDException("导出异常:"+e.getMessage(),e);
		}
	}
	private List<MetaDataVO> handleALLAppType(List<MetaDataVO> metaDatas, String dataSourceId, Long tenantid) throws HDException {
		List<MetaDataVO> tmp = new ArrayList<>();
		for (MetaDataVO metaDataVO : metaDatas) {
        	if (metaDataVO.getMetaDataType().equals("ALL")) {
				Map<String,String> metaTypeToModule = new HashMap<>();
				metaTypeToModule.put("pcmenu", "func_code like '"+metaDataVO.getMetaDataCode().toUpperCase()+"%'");
				metaTypeToModule.put("mobilemenu", "func_code like '"+metaDataVO.getMetaDataCode().toUpperCase()+"%'");
				metaTypeToModule.put("permpackage", "func_code_default like '"+metaDataVO.getMetaDataCode().toUpperCase()+"%'");
				metaTypeToModule.put("pcfunc", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("mobilefunc", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("pcform", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("mobileform", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("table", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("dict", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("config", "modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"' or modulecode = '"+metaDataVO.getMetaDataCode().toLowerCase()+"_m'");
				metaTypeToModule.put("import", "func_code like '"+metaDataVO.getMetaDataCode().toUpperCase()+"%'");
				metaTypeToModule.put("export", "func_code like '"+metaDataVO.getMetaDataCode().toUpperCase()+"%'");
		        getMetaDataByModuleCode(tmp, dataSourceId, metaDataVO, metaTypeToModule,tenantid);
		    }else {
		    	tmp.add(metaDataVO);
		    }
		}
		return tmp;
	}
	private void getMetaDataByModuleCode(List<MetaDataVO> metaDatas, String dataSourceId, MetaDataVO metaDataVO,Map<String,String> metaTypeToModule, Long tenantid) throws HDException {
		for (String key : metaTypeToModule.keySet()) {
			MetaRelationVO metaDataRelationVO= metaDataRelation.getMetaDataRelation(key);
			Class queryVO;
			try {
				queryVO = Class.forName(metaDataRelationVO.getNodevo());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				throw new HDException("根据元数据类型找到对应元数据VO 加载异常，请检查apollo配置",e);
			}
			DynaSqlVO sql = new DynaSqlVO();
			sql.addWhereClause(metaTypeToModule.get(key));
			sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
			VOSet<BaseVO> vOSet = simpleJdbcTemplateSupportDao.query(queryVO, sql, dataSourceId);
			Map<String,List<Long>> parentids = new HashMap<String,List<Long>>();
			for (BaseVO basevo : vOSet.getVoList()) {
				if ("pcmenu".equals(key)||"mobilemenu".equals(key)) {
					if (parentids.get(key)==null) {
						parentids.put(key, new ArrayList<Long>());
					}
					if (basevo.getLong("parentid")!=null&&!parentids.get(key).contains(basevo.getLong("parentid"))) {
						parentids.get(key).add(basevo.getLong("parentid"));
						getParentMenu(metaDatas,basevo.getLong("parentid"),queryVO,dataSourceId,metaDataRelationVO,key,parentids,tenantid);
					}
				}
				MetaDataVO m = new MetaDataVO();
				m.setMetaDataCode(basevo.getString(metaDataRelationVO.getExportColumn()));
				m.setMetaDataType(key);
				metaDatas.add(m);
			}
		}
	}
	private void getParentMenu(List<MetaDataVO> metaDatas, Long parentid, Class queryVO, String dataSourceId, MetaRelationVO metaDataRelationVO, String key, Map<String,List<Long>> parentids, Long tenantid) throws HDException {
		
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam("menuid", parentid);;
		sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
		VOSet<BaseVO> vOSet = simpleJdbcTemplateSupportDao.query(queryVO, sql, dataSourceId);
		for (BaseVO basevo : vOSet.getVoList()) {
			if (basevo.getLong("parentid")!=null&&!parentids.get(key).contains(basevo.getLong("parentid"))) {
				parentids.get(key).add(basevo.getLong("parentid"));
				getParentMenu(metaDatas,basevo.getLong("parentid"),queryVO,dataSourceId,metaDataRelationVO,key,parentids,tenantid);
			}
			MetaDataVO m = new MetaDataVO();
			m.setMetaDataCode(basevo.getString(metaDataRelationVO.getExportColumn()));
			m.setMetaDataType(key);
			metaDatas.add(m);
		}
	}
	private List<BaseVO> getLinkTypeMetaData(MetaTypeRelationVO typeRelation, String type) {
		List<MetaTypeRelationVO> relations = typeRelation.getLinkType();
		for (MetaTypeRelationVO metaTypeRelationVO : relations) {
			if (metaTypeRelationVO.getMetaType().equals(type)) {
				return metaTypeRelationVO.getMetaData();
			}
			List<BaseVO> basevo = getLinkTypeMetaData(metaTypeRelationVO, type);
			if (basevo!=null) {
				return basevo;
			}
		}
		return null;
	}
	//导出元数据关系子节点数据
	private void exportChildMetaData(ZipOutputStream zipOut, MetaRelationVO metaDataRelationVO, String dataSourceId,
			MetaDataVO metaDataVO, List<BaseVO> list, String project, String env, Long tenantid) throws HDException, IOException {
		for (MetaRelationVO metaRelationChild : metaDataRelationVO.getChildNodes()) {
			Class child;
			try {
				child = Class.forName(metaRelationChild.getNodevo());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				throw new HDException("根据元数据类型找到对应元数据VO 加载异常，请检查apollo配置",e);
			}
		    DynaSqlVO childsql = new DynaSqlVO();
		    String[] childQueryColumns = metaRelationChild.getExportColumn().split(",");
		    
		    String[] parentQueryColumns = metaDataRelationVO.getExportColumn().split(",");
		    if (StringUtils.isNotEmpty(metaRelationChild.getParentColumn())) {
		    	String[] childParentColumns = metaRelationChild.getParentColumn().split(",");
		    	for (int i = 0; i < childParentColumns.length; i++) {
			    	List<Object> values = new ArrayList<>();
			    	for (Iterator iterator = list.iterator(); iterator.hasNext();) {
						BaseVO baseVO = (BaseVO) iterator.next();
						values.add(baseVO.get(childParentColumns[i]));
					}
			    	childsql.addWhereParam(childParentColumns[i], values);
				}
			}
		    else {
		    	for (int i = 0; i < childQueryColumns.length; i++) {
			    	List<Object> values = new ArrayList<>();
			    	for (Iterator iterator = list.iterator(); iterator.hasNext();) {
						BaseVO baseVO = (BaseVO) iterator.next();
						values.add(baseVO.get(parentQueryColumns[i]));
					}
			    	childsql.addWhereParam(childQueryColumns[i], values);
				}
		    }
//		    childsql.addWhereParam(metaRelationChild.getExportColumn(), metaDataVO.getMetaDataCode());
		    childsql.addWhereParam(SyConstant.TENANT_STR,tenantid);
		    if ("dictdata".equals(metaRelationChild.getNodetype())) {
		    	childsql.setOrderByClause("dict_data_layer");
			}
		    VOSet<BaseVO> childvOSet = simpleJdbcTemplateSupportDao.query(child, childsql, dataSourceId);
		    if (childvOSet.getVoList()!=null&&childvOSet.getVoList().size()>0) {
		    	String childMetaData="";
				try {
					childMetaData = JsonUtils.writeValueAsString(childvOSet.getVoList());
				} catch (HDException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    InputStream childin = new ByteArrayInputStream(childMetaData.getBytes("UTF-8"));
			    String childfileName=metaRelationChild.getNodetype()+"-"+metaDataVO.getMetaDataCode()+"-"+new Date().getTime()+"-"+project+"-"+env+"-"+CurrentEnvUtils.getUserCode();
			    if (tenantid.longValue()!=1){
			    	childfileName = childfileName+"-"+tenantid;
				}
//			    if (StringUtils.isNotEmpty(metaDataVO.getMetaDataLinkType())) {
//			    	childfileName+="-ALL";
//				}
			    ZipEntry childEntry = new ZipEntry(childfileName+".hdml");
			    zipOut.putNextEntry(childEntry);
			    int nNumberChild;
			    byte[] bufferChild = new byte[1024];
			    while ((nNumberChild = childin.read(bufferChild)) != -1) {
			    	zipOut.write(bufferChild, 0, nNumberChild);
			    }
			    childin.close();
			    exportChildMetaData(zipOut, metaRelationChild, dataSourceId, metaDataVO, childvOSet.getVoList(), project, env,tenantid);
			}
		}
	}

	private MetaTypeRelationVO getMetaTypeRelationVO(String metaType) throws HDException {
		List<MetaTypeRelationVO> metaTypeRelations = JsonUtils.parseArrayInit(METATYPE_RELATION,MetaTypeRelationVO.class);
		if (metaTypeRelations==null) {
			throw new HDException("加载元数据类型关系异常，请检查apollo METATYPE_RELATION配置");
		}
		for (Iterator iterator = metaTypeRelations.iterator(); iterator.hasNext();) {
			MetaTypeRelationVO metaTypeRelationVO = (MetaTypeRelationVO) iterator.next();
			if (metaTypeRelationVO.getMetaType().equals(metaType)) {
				return metaTypeRelationVO;
			}
			if (metaTypeRelationVO.getLinkType()!=null) {
				MetaTypeRelationVO m =  getMetaTypeRelationRecursion(metaTypeRelationVO,metaType);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}
	private MetaTypeRelationVO getMetaTypeRelationRecursion(MetaTypeRelationVO metaTypeRelationVO, String metaType) {
		for (MetaTypeRelationVO metaTypeRelationVO2 : metaTypeRelationVO.getLinkType()) {
			if (metaTypeRelationVO2.getMetaType().equals(metaType)) {
				return metaTypeRelationVO2;
			}
			if (metaTypeRelationVO2.getLinkType()!=null) {
				MetaTypeRelationVO m =  getMetaTypeRelationRecursion(metaTypeRelationVO2,metaType);
				if (m!=null) {
					return m;
				}
			}
		}
		return null;
	}
	
	private MetaTypeRelationVO handleMetaTypeRelationData(MetaTypeRelationVO m,List<BaseVO> metaDataList,String dataSourceId, Long tenantid) throws HDException {
		List<MetaTypeRelationVO> relations = m.getLinkType();
		for (MetaTypeRelationVO metaTypeRelationVO : relations) {
			//处理级联数据
			MetaRelationVO metaDataRelationVO = metaDataRelation.getMetaDataRelation(metaTypeRelationVO.getMetaType());
			dataSourceCreator.getDataSource(dataSourceId);
			Class linkClass;
			try {
				linkClass = Class.forName(metaDataRelationVO.getNodevo());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				throw new HDException("根据元数据类型找到对应元数据VO 加载异常，请检查apollo配置",e);
			}
		    DynaSqlVO sql = new DynaSqlVO();
		    String[] queryColumns = metaDataRelationVO.getExportColumn().split(",");
		    for (int i = 0; i < queryColumns.length; i++) {
		    	List<Object> values = new ArrayList<>();
		    	for (Iterator iterator = metaDataList.iterator(); iterator.hasNext();) {
					BaseVO baseVO = (BaseVO) iterator.next();
					String[] valueColumns = metaTypeRelationVO.getParentColumn().split(",");
					for (int j = 0; j < valueColumns.length; j++) {
						values.add(baseVO.get(valueColumns[j]));
					}
				}
		    	sql.addWhereParam(queryColumns[i], values);
			}
		    sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
		    VOSet<BaseVO> vOSet = simpleJdbcTemplateSupportDao.query(linkClass, sql, dataSourceId);
		    metaTypeRelationVO.setMetaData(vOSet.getVoList());
			if (metaTypeRelationVO.getLinkType()!=null) {
				handleMetaTypeRelationData(metaTypeRelationVO, vOSet.getVoList(),dataSourceId,tenantid);
			}
		}
		return m;
	}
	@Override
	public void exportFieldMetaData(HttpServletResponse response, String project, String env, String table,
			String fields, String where) throws HDException {
		// TODO Auto-generated method stub
				if (!where.toLowerCase().contains("tenantid")&&!table.toLowerCase().contains("sy_table")){
					throw new HDException("查询条件中必须指定租户");
				}
				//设置文件下载及压缩文件名通用METADATA.zip 将要导出的元数据都压缩到一起
				response.setContentType("application/octet-stream");
			    response.setHeader("Content-Disposition", "attachment;filename=METADATA.zip");
			    try {
					ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
					String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
					dataSourceCreator.getDataSource(dataSourceId);
					//查询元数据及关联导出元数据
					DynaSqlVO sql = new DynaSqlVO();
			        sql.addWhereClause(where);
			        VOSet<BaseVO> vOSet = simpleJdbcTemplateSupportDao.query(table, sql, dataSourceId);
			        Long tenantid=1L;
			        if (vOSet.getVoList()!=null&&vOSet.getVoList().size()>0) {
						tenantid  = vOSet.getVoList().get(0).getLong("tenantid");
						if (tenantid==null){
							tenantid = 1L;
						}
			        	Map<String,List<Map<String,Object>>> exportData = new HashMap<String,List<Map<String,Object>>>();
			        	for (BaseVO basevo : vOSet.getVoList()) {
			        		MetaRelationVO metaRelationVO=metaDataRelation.getMetaDataRelationByClass(basevo.getClass().getName());
			        		String metaDataCode = basevo.getString(metaRelationVO.getExportColumn().split(",")[0]);
			        		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
			        		if (exportData.containsKey(metaRelationVO.getNodetype()+"-"+metaDataCode)) {
			        			result = exportData.get(metaRelationVO.getNodetype()+"-"+metaDataCode);
							}else {
								exportData.put(metaRelationVO.getNodetype()+"-"+metaDataCode, result);
							}
			        		if ("*".equals(fields)&&SENIOR_EXPORT_ALLFIELDS) {
			        			Map<String,Object> all = JsonUtils.parse(JsonUtils.writeValueAsString(basevo),Map.class);
			        			result.add(all);
							}else{
								String[] field = fields.split(",");
								Map<String,Object> map = new HashMap<>();
								for (int i = 0; i < field.length; i++) {
									if ("*".equals(field[i])) {
										continue;
									}
									map.put(field[i], basevo.get(field[i]));
								}
								String[] uniqueColumn = metaRelationVO.getImportColumn().split(",");
								for (int i = 0; i < uniqueColumn.length; i++) {
									map.put(uniqueColumn[i], basevo.get(uniqueColumn[i]));
								}
								if (StringUtils.isNotEmpty(metaRelationVO.getParentColumn())) {
									String[] parentColumn = metaRelationVO.getParentColumn().split(",");
									for (int i = 0; i < parentColumn.length; i++) {
										map.put(parentColumn[i], basevo.get(parentColumn[i]));
									}
								}
								result.add(map);
							}
						}
			        	for(Entry<String,List<Map<String,Object>>> export:exportData.entrySet()) {
			        		String metaData="";
							try {
								metaData = JsonUtils.writeValueAsString(export.getValue());
							} catch (HDException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					        InputStream in = new ByteArrayInputStream(metaData.getBytes("UTF-8"));
					        String fileName=export.getKey()+"-"+new Date().getTime()+"-"+project+"-"+env+"-"+CurrentEnvUtils.getUserCode();
					        if (tenantid.longValue()!=1){
					        	fileName = fileName+"-"+tenantid;
							}
//					        if (StringUtils.isNotEmpty(metaDataVO.getMetaDataLinkType())) {
//					        	fileName+="-ALL";
//							}
					        ZipEntry entry = new ZipEntry(fileName+".hdml");
					        zipOut.putNextEntry(entry);
					        int nNumber;
					        byte[] buffer = new byte[1024];
					        while ((nNumber = in.read(buffer)) != -1) {
					        	zipOut.write(buffer, 0, nNumber);
					        }
					        in.close();
			        	}
			        }
			        zipOut.flush();
			        zipOut.close();
				} catch (Exception e) {
					throw new HDException("导出异常:"+e.getMessage(),e);
				}
		
	}
	@Override
	public List<ShowModuleVO> getModuListWithSync(String project, String env, Long tenantid) throws HDException {
		List<ShowModuleVO> showVOs = new ArrayList<>();
		String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
        dataSourceCreator.getDataSource(dataSourceId);
        DynaSqlVO sql = new DynaSqlVO();
        sql.addWhereParam("dict_code", "module");
        sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
        VOSet<DictDataVO> vOSet = simpleJdbcTemplateSupportDao.query("sy_dict_data", sql, dataSourceId);
        if (vOSet.getVoList()!=null&&vOSet.getVoList().size()>0) {
        	for (DictDataVO basevo : vOSet.getVoList()) {
        		if (!(basevo.getDict_data_code().contains("_m")||"common".equals(basevo.getDict_data_code()))) {
        			ShowModuleVO showModuleVO = new ShowModuleVO();
        	        showModuleVO.setCode(basevo.getDict_data_code());
        	        showModuleVO.setName(basevo.getDict_data_name());
        	        showVOs.add(showModuleVO);
				}
			}
        }
       
		return showVOs;
	}

	@Override
	public Map<String, Object> getMetaAndTenants(String project, String env) throws HDException {
		String dataSourceId = dataSourceCreator.getDataSourceId(project, env);
		dataSourceCreator.getDataSource(dataSourceId);
		DynaSqlVO dynaSqlVO = new DynaSqlVO();
		dynaSqlVO.addWhereParam("isenable", 1);
		List<AbstractVO> tenantvos= simpleJdbcTemplateSupportDao.query(new TenantVO(), dynaSqlVO, dataSourceId).getVoList();
		tenantvos.sort(Comparator.comparing(x -> x.getLong("tenantpk")));
		List<Map<String,Object>> tenantinfos = new ArrayList<>();
		for (AbstractVO t : tenantvos) {
		    Map<String,Object> tenantinfo = new HashMap<>();
		    tenantinfo.put("tenantid",t.getLong("tenantpk"));
			tenantinfo.put("tenantname",t.getString("tenantname"));
		    tenantinfos.add(tenantinfo);
		}
		Map<String,Object> data = new HashMap<>();
		data.put("tenants",tenantinfos);
		data.put("meta",JsonUtils.parseArray("[  {\n" +
				"    \"type\"  :  \"table\",\n" +
				"    \"name\"  :  \"表定义\",\n" +
				"    \"queryItemName\"  :  \"表编码\",\n" +
				"    \"queryItem\"  :  \"table_code\",\n" +
				"    \"listItemName\"  :  \"表编码,表名称\",\n" +
				"    \"listItem\"  :  \"table_code,table_desc\",\n" +
				"    \"linkedType\"  :  [  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"pcform\",\n" +
				"    \"name\"  :  \"PC表单\",\n" +
				"    \"queryItemName\"  :  \"表单编码\",\n" +
				"    \"queryItem\"  :  \"form_code\",\n" +
				"    \"listItemName\"  :  \"表单编码,表单名称\",\n" +
				"    \"listItem\"  :  \"form_code,form_name\",\n" +
				"    \"linkedType\"  :  [  {\n" +
				"      \"linkedtype\"  :  \"table\",\n" +
				"      \"name\"  :  \"导出表定义\"\n" +
				"    }  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"mobileform\",\n" +
				"    \"name\"  :  \"MOBILE表单\",\n" +
				"    \"queryItemName\"  :  \"表单编码\",\n" +
				"    \"queryItem\"  :  \"form_code\",\n" +
				"    \"listItemName\"  :  \"表单编码,表单名称\",\n" +
				"    \"listItem\"  :  \"form_code,form_name\",\n" +
				"    \"linkedType\"  :  [  {\n" +
				"      \"linkedtype\"  :  \"table\",\n" +
				"      \"name\"  :  \"导出表定义\"\n" +
				"    }  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"pcfunc\",\n" +
				"    \"name\"  :  \"PC功能\",\n" +
				"    \"queryItemName\"  :  \"功能编码\",\n" +
				"    \"queryItem\"  :  \"func_code\",\n" +
				"    \"listItemName\"  :  \"功能编码,功能名称\",\n" +
				"    \"listItem\"  :  \"func_code,func_name\",\n" +
				"    \"linkedType\"  :  [  {\n" +
				"      \"linkedtype\"  :  \"table\",\n" +
				"      \"name\"  :  \"导出表定义\"\n" +
				"    },  {\n" +
				"      \"linkedtype\"  :  \"pcform\",\n" +
				"      \"name\"  :  \"导出PC表单\"\n" +
				"    }  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"mobilefunc\",\n" +
				"    \"name\"  :  \"MOBILE功能\",\n" +
				"    \"queryItemName\"  :  \"功能编码\",\n" +
				"    \"queryItem\"  :  \"func_code\",\n" +
				"    \"listItemName\"  :  \"功能编码,功能名称\",\n" +
				"    \"listItem\"  :  \"func_code,func_name\",\n" +
				"    \"linkedType\"  :  [  {\n" +
				"      \"linkedtype\"  :  \"table\",\n" +
				"      \"name\"  :  \"导出表定义\"\n" +
				"    },  {\n" +
				"      \"linkedtype\"  :  \"mobileform\",\n" +
				"      \"name\"  :  \"导出MOBILE表单\"\n" +
				"    }  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"pcmenu\",\n" +
				"    \"name\"  :  \"PC菜单\",\n" +
				"    \"queryItemName\"  :  \"菜单编码\",\n" +
				"    \"queryItem\"  :  \"menucode\",\n" +
				"    \"listItemName\"  :  \"菜单编码,菜单名称\",\n" +
				"    \"listItem\"  :  \"menucode,menuname\",\n" +
				"    \"linkedType\"  :  [  {\n" +
				"      \"linkedtype\"  :  \"table\",\n" +
				"      \"name\"  :  \"导出表定义\"\n" +
				"    },  {\n" +
				"      \"linkedtype\"  :  \"pcform\",\n" +
				"      \"name\"  :  \"导出PC表单\"\n" +
				"    },  {\n" +
				"      \"linkedtype\"  :  \"pcfunc\",\n" +
				"      \"name\"  :  \"导出PC功能\"\n" +
				"    }  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"mobilemenu\",\n" +
				"    \"name\"  :  \"MOBILE菜单\",\n" +
				"    \"queryItemName\"  :  \"菜单编码\",\n" +
				"    \"queryItem\"  :  \"menucode\",\n" +
				"    \"listItemName\"  :  \"菜单编码,菜单名称\",\n" +
				"    \"listItem\"  :  \"menucode,menuname\",\n" +
				"    \"linkedType\"  :  [  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"dict\",\n" +
				"    \"name\"  :  \"字典\",\n" +
				"    \"queryItemName\"  :  \"字典编码\",\n" +
				"    \"queryItem\"  :  \"dict_code\",\n" +
				"    \"listItemName\"  :  \"字典编码,字典名称\",\n" +
				"    \"listItem\"  :  \"dict_code,dict_name\",\n" +
				"    \"linkedType\"  :  [  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"export\",\n" +
				"    \"name\"  :  \"导出模版\",\n" +
				"    \"queryItemName\"  :  \"导出模版名称\",\n" +
				"    \"queryItem\"  :  \"export_temp_name\",\n" +
				"    \"listItemName\"  :  \"导出模版名称,功能编码\",\n" +
				"    \"listItem\"  :  \"export_temp_name,func_code\",\n" +
				"    \"linkedType\"  :  [  ]\n" +
				"  },  {\n" +
				"    \"type\"  :  \"import\",\n" +
				"    \"name\"  :  \"导入模版\",\n" +
				"    \"queryItemName\"  :  \"导入模版编码\",\n" +
				"    \"queryItem\"  :  \"temp_code\",\n" +
				"    \"listItemName\"  :  \"导入模版编码,导入模版名称\",\n" +
				"    \"listItem\"  :  \"temp_code,temp_name\",\n" +
				"    \"linkedType\"  :  [  ]\n" +
				"  },  {\n" +
				"    \"type\" : \"config\",\n" +
				"    \"name\" : \"系统参数\",\n" +
				"    \"queryItemName\" : \"参数编码\",\n" +
				"    \"queryItem\" : \"conf_code\",\n" +
				"    \"listItemName\" : \"参数编码,参数名称\",\n" +
				"    \"listItem\" : \"conf_code,conf_name\",\n" +
				"    \"linkedType\" : [ ]\n" +
				"  }, {\n" +
				"    \"type\" : \"permpackage\",\n" +
				"    \"name\" : \"APP权限组\",\n" +
				"    \"queryItemName\" : \"权限组编码\",\n" +
				"    \"queryItem\" : \"package_code\",\n" +
				"    \"listItemName\" : \"权限组编码,权限组名称\",\n" +
				"    \"listItem\" : \"package_code,package_name\",\n" +
				"    \"linkedType\" : [ ]\n" +
				"  } ]", Map.class));
		return data;
	}

	/**
	 * 从菜单一键导出新开发的所有功能、子功能、表单、表定义
	 * @param metaDatas
	 * @param dataSourceId
	 * @return
	 * @throws HDException
	 */
	private List<MetaDataVO> handleExportAllFromMenu(List<MetaDataVO> metaDatas, String dataSourceId, Long tenantid) throws HDException {
		List<MetaDataVO> tmp = new ArrayList<>();
		for (MetaDataVO metaDataVO : metaDatas) {
			if (metaDataVO.getMetaDataType().equals("pcmenu")&&StringUtils.isNotEmpty(metaDataVO.getMetaDataLinkType())) {
				String[] linkTypes = metaDataVO.getMetaDataLinkType().split(",");
        		if (metaDataVO.getMetaDataLinkType().indexOf("pcfunc")>-1&&metaDataVO.getMetaDataLinkType().indexOf("pcform")>-1&&metaDataVO.getMetaDataLinkType().indexOf("table")>-1) {//table,pcform,
    		        DynaSqlVO sql = new DynaSqlVO();
    		        sql.addWhereParam("menucode", metaDataVO.getMetaDataCode());
    		        sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
    		        VOSet<MenuVO> vOSet = simpleJdbcTemplateSupportDao.query("sy_menu", sql, dataSourceId);
    		        if (vOSet.getVoList()!=null&&vOSet.getVoList().size()>0) {
    		        	List<String> mainfuncodes = new ArrayList<String>();
    		        	for (MenuVO basevo : vOSet.getVoList()) {
    		        		if (StringUtils.isNotEmpty(basevo.getFunc_code())) {
    		        			mainfuncodes.add(basevo.getFunc_code());
    						}
    					}
    		        	if (mainfuncodes.size()>0) {
    		        		sql = new DynaSqlVO();
    	    		        sql.addWhereParam("main_func_code", mainfuncodes);
    	    		        sql.addWhereParam(SyConstant.TENANT_STR,tenantid);
    	    		        VOSet<FuncLinkVO> linkfuncs = simpleJdbcTemplateSupportDao.query("sy_func_link", sql, dataSourceId);
    	    		        if (linkfuncs.getVoList()!=null&&linkfuncs.getVoList().size()>0) {
    	    		        	for (FuncLinkVO basevo : linkfuncs.getVoList()) {
    	    		        		MetaDataVO m = new MetaDataVO();
    	    						m.setMetaDataCode(basevo.getSub_func_code());
    	    						m.setMetaDataType("pcfunc");
    	    						m.setMetaDataLinkType("table,pcform");
    	    						tmp.add(m);
    	    		        	}
    	    				}
						}
    		        }
				}
		    }
		}
		metaDatas.addAll(tmp);
		return metaDatas;
	}
}
