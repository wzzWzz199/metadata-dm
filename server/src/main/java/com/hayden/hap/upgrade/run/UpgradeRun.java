package com.hayden.hap.upgrade.run;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.entity.ParamVO;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import com.hayden.hap.upgrade.itf.IMetaDataHandleService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 10:23
 */
public class UpgradeRun implements Runnable {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private ParamVO paramVO;
    private boolean isStop = false;

    public UpgradeRun(ThreadPoolTaskExecutor threadPoolTsakExecutor, ParamVO paramVO) {
        this.threadPoolTaskExecutor = threadPoolTsakExecutor;
        this.paramVO = paramVO;
    }


    @Override
    public void run() {

        IMetaDataHandleService metaDataService = null;

        if (paramVO.getHasProduct()) {
            metaDataService = (IMetaDataHandleService) AppServiceHelper.findBean("prodMetaDataHandleService");
        } else {
            metaDataService = (IMetaDataHandleService) AppServiceHelper.findBean("projMetaDataHandleService");
        }

        try {
            metaDataService.upgradeMetaData(this.paramVO);
        } catch (HDException e) {
            e.printStackTrace();
        }
    }

    public boolean isInterrupt() {
        return isStop;
    }

    public void interrupt() {
        isStop = true;
    }
}
