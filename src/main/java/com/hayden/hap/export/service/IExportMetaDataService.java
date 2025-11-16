package com.hayden.hap.export.service;

import com.hayden.hap.common.common.entity.ReqParamVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.spring.service.IService;
import com.hayden.hap.export.entity.MetaDataVO;
import com.hayden.hap.export.entity.MetaTypeVO;
import com.hayden.hap.upgrade.entity.ShowModuleVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@IService("exportMetaDataService")
public interface IExportMetaDataService {
	List<MetaTypeVO> getMetaDataType() throws HDException;
	
	Map<String,Object> queryMetaData(String project,String env,String metaType,String metaDataCode, ReqParamVO reqParam, Long tenantid)throws HDException;

	void exportMetaData(HttpServletResponse response, String project, String env, String metaType, List<MetaDataVO> metaDatas, Long tenantid) throws HDException;

	void exportFieldMetaData(HttpServletResponse response, String project, String env, String table, String fields,
			String where) throws HDException;

	List<ShowModuleVO> getModuListWithSync(String project, String env, Long tenantid) throws HDException;

	Map<String,Object> getMetaAndTenants(String project, String env) throws HDException;
}
