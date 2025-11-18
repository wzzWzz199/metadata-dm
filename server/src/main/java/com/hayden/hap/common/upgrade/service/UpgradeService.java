package com.hayden.hap.common.upgrade.service;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.itf.IBaseService;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.upgrade.entity.UpgradeFrontVO;
import com.hayden.hap.common.upgrade.itf.IUpgradeService;
import com.hayden.hap.dbop.utils.FileUtil;
import com.hayden.hap.dbop.utils.VOCollectionUtils;
import com.hayden.hap.dbop.utils.ZipUtils;
import com.hayden.hap.dbop.utils.properties.ModulePropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author haocs
 *
 */
@Service("commonUpgradeService")
public class UpgradeService implements IUpgradeService {

	private static final Logger logger = LoggerFactory.getLogger(UpgradeService.class);
	@Autowired
	private IBaseService baseService;

	@Override
	public void listUpgrade() throws HDException {

		String moduleCode = ModulePropertiesUtil.getModuleCode();
		if (StringUtils.isEmpty(moduleCode)) {
			logger.error("moduleCode is null 前端未升级");
			return;
		}
		DynaSqlVO sql = new DynaSqlVO();
		sql.addWhereParam("module_code", moduleCode);
		List<UpgradeFrontVO> voList = baseService.query(new UpgradeFrontVO(), sql).getVoList();

		if (voList == null || voList.isEmpty()) {
			logger.error(" moduleCode 查询list is null  前端未升级");
			return;
		}

		Map<String, UpgradeFrontVO> groupedByProp = VOCollectionUtils.groupedByProp(voList, "module_code",
				String.class);
		UpgradeFrontVO upgradeFrontVO = groupedByProp.get(moduleCode);
		String static_path = upgradeFrontVO.getStatic_path();
		String current_version = upgradeFrontVO.getCurrent_version();
		String app_path = upgradeFrontVO.getApp_path();
		String module_code = upgradeFrontVO.getModule_code();

		String url = static_path + "/" + module_code + "/" + current_version + ".zip";

		String tmpPath = app_path + File.separator + "tmp";
		String downloadFilePath = tmpPath + File.separator + current_version + ".zip";
		String sourcePath = tmpPath + File.separator + "static";
		String targetPath = app_path + File.separator + "static";

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse backResponse = httpclient.execute(httpGet);
			InputStream inputStream = backResponse.getEntity().getContent();
			ZipUtils.unzipStream2TempPath(inputStream, downloadFilePath, tmpPath);
			FileUtil.delFolder(targetPath);
			FileUtil.moveFolder(sourcePath, app_path);
			FileUtil.delFolder(sourcePath);
			
			logger.error("sourcePath  " +sourcePath);
			logger.error("app_path  " +app_path);
			logger.error("targetPath  " +targetPath);
		} catch (IOException e) {
			logger.error("升级文件处理失败 ", e);
		}
	}

}
