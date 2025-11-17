package com.hayden.hap.upgrade.service.handle;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.entity.VOSet;
import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.keyGen.entity.SerialGeneratorVO;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.db.tableDef.entity.TableColumnVO;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.db.dataSource.itf.IDataSourceGeneratorService;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.serial.JsonUtils;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.entity.UpgradeContext;
import com.hayden.hap.upgrade.itf.IDbHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/10 17:52
 */
@Service("tablecolumnDataHandleService")
public class TableColHandleServiceImpl extends TableHandleServiceImpl {
    @Value("${META.GENCONF}")
    private String GENCONF;

    @Autowired
    private IDbHandleService dbHandleService;
    @Autowired
    private IDataSourceGeneratorService dataSourceGeneratorService;
    @Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;

    //元数据导入通用处理方法
    @Override
    public void commonDataUpgradeHandle(File patchFile, ParamVO paramVO, ProgressModuleVO progressModuleVO) throws Exception {
        paramVO.setTenantid(1l);
        super.commonDataUpgradeHandle(patchFile, paramVO, progressModuleVO);
    }

    @Override
    public void addWhereParam(DynaSqlVO dynaSqlVO, UpgradeContext upgradeContext) {
    }

    @Override
    public void spicelMetaDataHandle(UpgradeContext upgradeContext, ParamVO paramVO) throws Exception {
        dbHandleService.recordUpgradeTable(paramVO, upgradeContext.getVo().getString("table_code"));
        createGeneratorData(upgradeContext, paramVO);
    }

    @Override
    public void preDataHandle(UpgradeContext upgradeContext, AbstractVO oldvo, String dataSourceId, ParamVO paramVO) throws Exception {
        super.preDataHandle(upgradeContext,oldvo,dataSourceId,paramVO);
        TableColumnVO tableColumnVO = (TableColumnVO) upgradeContext.getVo();
        String gencode=tableColumnVO.getGencode()!=null?tableColumnVO.getGencode():tableColumnVO.getTable_code();
        tableColumnVO.setGencode(gencode);
    }

    private void createGeneratorData(UpgradeContext upgradeContext, ParamVO paramVO) throws HDException {
        TableColumnVO tableColumnVO = (TableColumnVO) upgradeContext.getVo();
        if ((tableColumnVO.getIspk()!=null&&tableColumnVO.getIspk() == 1) && tableColumnVO.getGencode() != null) {

            SerialGeneratorVO serialGeneratorVO = new SerialGeneratorVO();
            DynaSqlVO dynaSqlVO = new DynaSqlVO();
            dynaSqlVO.addWhereParam("gencode", tableColumnVO.getGencode());
            VOSet<AbstractVO> voSet = simpleJdbcTemplateSupportDao.query(serialGeneratorVO, dynaSqlVO, paramVO.getDataSourceId());

            if (voSet == null || voSet.getVoList() == null || voSet.getVoList().size() == 0) {
                HashMap<String, String> genConfMap = JsonUtils.parse(GENCONF, HashMap.class);
                String gennext = genConfMap.get(paramVO.getEnv());

                TableDefVO tableDefVO = simpleJdbcTemplateSupportDao.queryDetailedTableByTbname(serialGeneratorVO.getTableName(), paramVO.getDataSourceId());
                Long pkValue = Long.parseLong(dataSourceGeneratorService.generate(dataSourceGeneratorService.getPkColGencode(tableDefVO), paramVO.getDataSourceId()));

                serialGeneratorVO.setSerialgenid(pkValue);
                serialGeneratorVO.setGencode(tableColumnVO.getGencode());
                serialGeneratorVO.setGennext(gennext);
                serialGeneratorVO.setGencache(20l);
                serialGeneratorVO.setCreated_by(1000l);
                serialGeneratorVO.setCreated_dt(new Date());
                serialGeneratorVO.setUpdated_by(1000l);
                serialGeneratorVO.setUpdated_dt(new Date());
                serialGeneratorVO.setVer(1);
                serialGeneratorVO.setDf(0);
                simpleJdbcTemplateSupportDao.insert(serialGeneratorVO, new DynaSqlVO(), paramVO.getDataSourceId());
            }
        }
    }
}
