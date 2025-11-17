package com.hayden.hap.upgrade.itf;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.spring.service.IService;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.entity.UpgradeContext;

import java.io.File;

@IService("dataHandleService")
public interface IDataHandleService {
    void metaDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception;
    void commonDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception;
    void spicelMetaDataHandle(UpgradeContext upgradeContext,ParamVO paramVO)  throws Exception;

    boolean doCommonUpdate(UpgradeContext upgradeContext, ParamVO paramVO, SqlResultVO[] sqlResultVOS) throws Exception;

    void addWhereParam(DynaSqlVO dynaSqlVO, UpgradeContext upgradeContext);

    void preDataHandle(UpgradeContext upgradeContext, AbstractVO oldvo, String dataSourceId, ParamVO paramVO)  throws Exception;

    Long getParentId(UpgradeContext upgradeContext, String dataSourceId)  throws Exception;

    void getParentData(UpgradeContext upgradeContext,String dataSourceId)  throws Exception;
}
