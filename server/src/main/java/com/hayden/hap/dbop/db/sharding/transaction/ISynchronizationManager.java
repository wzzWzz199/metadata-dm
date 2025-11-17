package com.hayden.hap.dbop.db.sharding.transaction;

/**
 * 
 * @ClassName: ISynchronizationManager 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月2日 下午8:49:08 
 * @version V1.0   
 *
 */
public interface ISynchronizationManager {
	public void initSynchronization();

	public boolean isSynchronizationActive();

	public void clearSynchronization();
}
