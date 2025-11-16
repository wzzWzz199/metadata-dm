package com.hayden.hap.upgrade.itf;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.upgrade.entity.ProgressModuleVO;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/9 15:53
 */
public interface IProgressService {
    void changeStatus2Upgrading(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws HDException;

    void changeStatus2UpgradeError(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws HDException;

    void upgradeFinish(ParamVO paramVO,ProgressModuleVO progressModuleVO) throws HDException;

    ProgressModuleVO getProgressModuleVO(ParamVO paramVO);

    void setProgressModuleVO(ParamVO paramVO,ProgressModuleVO progressModuleVO);

    boolean hasProgressModuleVO(ParamVO paramVO);
}
