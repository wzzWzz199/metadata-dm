package com.hayden.hap.upgrade.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.spring.service.IService;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@IService("metaDataHandleService")
public interface IMetaDataHandleService {
    List<File> upgradeMetaData(ParamVO paramVO) throws HDException;

    void doUpgrade(ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception;

    void clearAllCache();

    void beforeUpgrade(ParamVO paramVO, ProgressModuleVO progressModuleVO) throws IOException, GitAPIException;
}
