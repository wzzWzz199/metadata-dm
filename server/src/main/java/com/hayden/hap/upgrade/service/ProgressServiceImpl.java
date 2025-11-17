package com.hayden.hap.upgrade.service;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.orm.sql.DynaSqlVO;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.db.dataSource.DataSourceCreator;
import com.hayden.hap.db.dataSource.itf.ISimpleJdbcTemplateSupportDao;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;
import com.hayden.hap.upgrade.entity.ShowModuleVO;
import com.hayden.hap.upgrade.entity.UpgradeModuleVO;
import com.hayden.hap.upgrade.enums.UpgradeEnum;
import com.hayden.hap.upgrade.itf.IProgressService;
import com.hayden.hap.upgrade.itf.IUpgradeService;
import com.hayden.hap.utils.CurrentEnvUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/9 15:56
 */
@Service("progressService")
public class ProgressServiceImpl implements IProgressService {

    private final String PROGRESSKEY="upgradeprogress";

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IUpgradeService upgradeService;
    @Autowired
    private DataSourceCreator dataSourceCreator;
    @Autowired
    private ISimpleJdbcTemplateSupportDao simpleJdbcTemplateSupportDao;

    private ProgressModuleVO changeStatus(ParamVO paramVO,ProgressModuleVO progressModuleVO,String status) throws HDException {
        progressModuleVO.setStatus(status);
        //修改数据库模块信息
        Map<String, UpgradeModuleVO> upgradeModuleVOMap = upgradeService.getModuleMapfromDataBase(paramVO.getProject(), paramVO.getEnv());
        UpgradeModuleVO upgradeModuleVO=upgradeModuleVOMap.get(paramVO.getModule());
        upgradeModuleVO.setStatus(progressModuleVO.getStatus());
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        upgradeModuleVO.setProgress(decimalFormat.format(progressModuleVO.getProgress()));
        upgradeModuleVO.setCurrentver(progressModuleVO.getCurrentver());
        upgradeModuleVO.setLastfilets(progressModuleVO.getLastfilets());
        upgradeModuleVO.setPerson(progressModuleVO.getPerson());
        upgradeModuleVO.setDatetime(progressModuleVO.getDatetime());
        upgradeModuleVO.setProductver(progressModuleVO.getProductver());
        upgradeModuleVO.setProlastfilets(progressModuleVO.getProlastfilets());

        String dataSourceId = dataSourceCreator.getDataSourceId(paramVO.getProject(), paramVO.getEnv());
        dataSourceCreator.getDataSource(dataSourceId);
        simpleJdbcTemplateSupportDao.update(upgradeModuleVO,new DynaSqlVO(),dataSourceId);

        //修改显示缓存
        for(ShowModuleVO showModuleVO:upgradeService.getShowVOsCache().get(paramVO.getProject()+paramVO.getEnv())){
            if(showModuleVO.getCode().equals(paramVO.getModule())){
                showModuleVO.setStatus(progressModuleVO.getStatus());
                showModuleVO.setCurrentver(progressModuleVO.getCurrentver());
                showModuleVO.setDatetime(progressModuleVO.getDatetime());
            }
        }
        return progressModuleVO;
    }

    @Override
    public void changeStatus2Upgrading(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws HDException {
        //首先修改缓存
        progressModuleVO.setStatus(UpgradeEnum.MODULE_STATUS_UPGRADING.getCode());
        progressModuleVO.setDatetime(System.currentTimeMillis());
        progressModuleVO.setPerson(CurrentEnvUtils.getUserCode());
        setProgressModuleVO(paramVO,progressModuleVO);
        changeStatus(paramVO,progressModuleVO,UpgradeEnum.MODULE_STATUS_UPGRADING.getCode());
    }

    @Override
    public void changeStatus2UpgradeError(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws HDException {
        changeStatus(paramVO,progressModuleVO,UpgradeEnum.MODULE_STATUS_UPGRADEERROR.getCode());
        //清除缓存
        redisTemplate.opsForHash().delete(PROGRESSKEY,paramVO.getKey());
    }

    @Override
    public void upgradeFinish(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws HDException {
        changeStatus(paramVO,progressModuleVO,UpgradeEnum.MODULE_STATUS_UPGRADEABLE.getCode());
        //清除缓存
        redisTemplate.opsForHash().delete(PROGRESSKEY,paramVO.getKey());
    }

    @Override
    public ProgressModuleVO getProgressModuleVO(ParamVO paramVO) {
        return (ProgressModuleVO) redisTemplate.opsForHash().get(PROGRESSKEY, paramVO.getKey());
    }

    @Override
    public void setProgressModuleVO(ParamVO paramVO, ProgressModuleVO progressModuleVO) {
        redisTemplate.opsForHash().put(PROGRESSKEY, paramVO.getKey(),progressModuleVO);
    }

    @Override
    public boolean hasProgressModuleVO(ParamVO paramVO) {
        return redisTemplate.opsForHash().hasKey(PROGRESSKEY, paramVO.getKey());
    }
}
