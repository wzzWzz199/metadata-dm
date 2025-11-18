package com.hayden.hap.service.upgrade.handle;

import com.hayden.hap.dbop.entity.AbstractVO;
import com.hayden.hap.dbop.entity.VOSet;
import com.hayden.hap.dbop.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.entity.dict.DictDataVO;
import com.hayden.hap.common.entity.dict.DictVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.meta.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.vo.upgrade.UpgradeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dictdataDataHandleService")
public class DictdataDataHandleService extends ComDataHandleServiceImpl {
    @Autowired
    ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;

    /**
     * 数据预处理
     *
     * @param
     */
    @Override
    public void preDataHandle(UpgradeContext upgradeContext, AbstractVO oldvo, String dataSourceId, ParamVO paramVO) throws Exception {
        super.preDataHandle(upgradeContext, oldvo, dataSourceId, paramVO);
        DictDataVO vo = (DictDataVO) upgradeContext.getVo();

        DynaSqlVO dynaSqlVO = new DynaSqlVO();
        dynaSqlVO.addWhereParam("dictid", vo.getDictid());
        dynaSqlVO.addWhereParam("tenantid", upgradeContext.getTenantid());
        VOSet<DictVO> abstractVOS = simpleJdbcTemplateSupportDao.query(new DictVO(), dynaSqlVO, dataSourceId);
        if (abstractVOS != null && abstractVOS.getVoList() != null && abstractVOS.getVoList().size() > 0) {
            DictVO parentVO = abstractVOS.getVoList().get(0);
            DictDataVO frontVO = this.getTreeParent(parentVO, vo, dataSourceId, upgradeContext.getTenantid());
            if (frontVO != null) {
                vo.setDict_data_parent(frontVO.getDictdataid());
            }
        }
    }


    private DictDataVO getTreeParent(DictVO dictVO, DictDataVO dictDataVO, String dataSourceId, Long tenantid) {
        if (dictVO != null && dictDataVO != null) {
            String dictCode = dictVO.getDict_code();
            String treeType = dictVO.getDict_t_type();
            String type = dictVO.getDict_type();
            String table = dictVO.getDict_t_table() != null ? dictVO.getDict_t_table() : "";

            //树状字典并且来源为字典
            if (type.equals("2") && table.equals("sy_dict_data") && dictDataVO.getDict_data_layer() != null) {
                Integer layer = dictDataVO.getDict_data_layer() - 1;
                char[] treeTypes = treeType.toCharArray();
                Integer frontNum = Integer.valueOf(String.valueOf(treeTypes[layer]));
                String frontItemCode = dictDataVO.getDict_data_code().substring(0, dictDataVO.getDict_data_code().length() - frontNum);

                DynaSqlVO dynaSqlVO = new DynaSqlVO();
                dynaSqlVO.addWhereParam("dict_code", dictCode);
                dynaSqlVO.addWhereParam("dict_data_code", frontItemCode);
                dynaSqlVO.addWhereParam("tenantid", tenantid);
                VOSet<DictDataVO> abstractVOS = simpleJdbcTemplateSupportDao.query(new DictDataVO(), dynaSqlVO, dataSourceId);
                if (abstractVOS != null && abstractVOS.getVoList() != null && abstractVOS.getVoList().size() > 0) {
                    return abstractVOS.getVoList().get(0);
                }
            }
        }
        return null;
    }
}
