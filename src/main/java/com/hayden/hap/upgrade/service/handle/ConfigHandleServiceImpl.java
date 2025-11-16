package com.hayden.hap.upgrade.service.handle;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.db.dataSource.entity.SqlResultVO;
import com.hayden.hap.upgrade.entity.UpgradeContext;
import com.hayden.hap.upgrade.itf.IDbHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 17:52
 */
@Service("configDataHandleService")
public class ConfigHandleServiceImpl extends ComDataHandleServiceImpl {
    @Autowired
    private IDbHandleService dbHandleService;

    public boolean doCommonUpdate(UpgradeContext upgradeContext, ParamVO paramVO, SqlResultVO[] sqlResultVOS) throws Exception {
        AbstractVO vo = upgradeContext.getVo();
        Long tenantid=upgradeContext.getTenantid();
        String type=vo.getString("tenant_category");
        if(type.equals("manager")&&tenantid!=1l){
            return false;
        }
        return super.doCommonUpdate(upgradeContext,paramVO,sqlResultVOS);
    }
}
