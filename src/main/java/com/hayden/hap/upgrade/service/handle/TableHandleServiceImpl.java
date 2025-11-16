package com.hayden.hap.upgrade.service.handle;

import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.entity.UpgradeContext;
import com.hayden.hap.upgrade.itf.IDbHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 17:52
 */
@Service("tableDataHandleService")
public class TableHandleServiceImpl extends ComDataHandleServiceImpl {
    @Autowired
    private IDbHandleService dbHandleService;

    //元数据导入通用处理方法
    @Override
    public void commonDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception {
        paramVO.setTenantid(1l);
        super.commonDataUpgradeHandle(patchFile,paramVO,progressModuleVO);
    }

    @Override
    public void addWhereParam(DynaSqlVO dynaSqlVO, UpgradeContext upgradeContext){
    }

    @Override
    public void spicelMetaDataHandle(UpgradeContext upgradeContext, ParamVO paramVO) throws Exception {
        dbHandleService.recordUpgradeTable(paramVO, upgradeContext.getVo().getString("table_code"));
    }

    @Override
    public void metaDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception {
        Long tenantid = paramVO.getTenantid();
        paramVO.setTenantid(1L);
        super.commonDataUpgradeHandle(patchFile, paramVO, progressModuleVO);
        paramVO.setTenantid(tenantid);
    }
}
