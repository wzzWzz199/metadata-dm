package com.hayden.hap.upgrade.service.handle;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.upgrade.entity.UpgradeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/11 9:22
 */
@Service("pcmenuDataHandleService")
public class PcMenuHandleServiceImpl extends ComDataHandleServiceImpl {
    @Autowired
    ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;

    @Override
    public void getParentData(UpgradeContext upgradeContext,String dataSourceId)  throws Exception {
        //menu父对象就是自身
        upgradeContext.setParentMetaRelationVO(upgradeContext.getMetaRelationVO());
        upgradeContext.setParentTableDefVO(upgradeContext.getTableDefVO());
        upgradeContext.setParentVO(upgradeContext.getVo());
    }

    @Override
    public Long getParentId(UpgradeContext upgradeContext, String dataSourceId)  throws Exception {
        //menucode以4位为一层
        Long id = null;

        String uniqueColumn = upgradeContext.getParentMetaRelationVO().getExportColumn();
        String pkcol = upgradeContext.getParentTableDefVO().getPkColumnVO().getColcode();
        String menucode = String.valueOf(upgradeContext.getVo().get(uniqueColumn));

        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        if (menucode.length() > 4) {
            dynaSqlVO.addWhereParam(uniqueColumn, menucode.substring(0, menucode.length() - 4));
            dynaSqlVO.addWhereParam("tenantid", upgradeContext.getTenantid());
            VOSet<AbstractVO> abstractVOS = simpleJdbcTemplateSupportDao.query(upgradeContext.getParentVO(), dynaSqlVO, dataSourceId);
            if(abstractVOS!=null&&abstractVOS.getVoList()!=null&&abstractVOS.getVoList().size()>0)
                id = abstractVOS.getVoList().get(0).getLong(pkcol);
        }
        return id;
    }
}
