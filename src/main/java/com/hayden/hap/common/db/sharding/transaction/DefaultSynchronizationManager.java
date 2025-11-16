package com.hayden.hap.common.db.sharding.transaction;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 
 * @ClassName: DefaultSynchronizationManager 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月2日 下午8:49:12 
 * @version V1.0   
 *
 */
public class DefaultSynchronizationManager implements ISynchronizationManager {
    @Override
    public void initSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @Override
    public boolean isSynchronizationActive() {
        return TransactionSynchronizationManager.isSynchronizationActive();
    }

    @Override
    public void clearSynchronization() {
        TransactionSynchronizationManager.clear();
    }
}
