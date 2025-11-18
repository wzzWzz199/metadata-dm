package com.hayden.hap.cache.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.vo.upgrade.UpgradeContext;

/**
 * @Description
 * @Author suntaiming
 * @Date 2022/4/22 15:25
 **/
public interface ICacheService {
    /**
     * 过期缓存
     * @param upgradeContext
     * @param paramVO
     * @return: void
     * @Author: suntaiming
     * @Date: 2022/4/22 15:28
     */
    void evict(UpgradeContext upgradeContext, ParamVO paramVO) throws HDException;
}
