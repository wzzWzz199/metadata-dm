package com.hayden.hap.common.db.sharding.transaction;

import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.utils.SyConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.*;

import javax.sql.DataSource;
import java.util.*;

/**
 * 
 * @ClassName: ShardingTransactionManager 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月2日 下午8:49:28 
 * @version V1.0   
 *
 */
@Component
public class ShardingTransactionManager implements PlatformTransactionManager, InitializingBean {
	protected transient Logger logger = LoggerFactory.getLogger(ShardingTransactionManager.class);

    private  List<PlatformTransactionManager> transactionManagers = new ArrayList<PlatformTransactionManager>();

    private String defaultDataSourceId = "dataSource";
    @Autowired
    @Qualifier("dataSource")
    private DataSource defaultDataSource;

    public void init(Map<String, DataSource> dataSources){
    		Iterator<String> iterator = dataSources.keySet().iterator();
    			while(iterator.hasNext()){
        			String key = iterator.next();
        			if(key.equals(defaultDataSourceId))
        				continue ;
        			PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSources.get(key));
        			transactionManagers.add(platformTransactionManager);
        		}
    	}
    @Override
	public void afterPropertiesSet() throws Exception {
		PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(defaultDataSource);
        transactionManagers.add(platformTransactionManager);
	}

    @Override
    public MultiTransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {

        MultiTransactionStatus mts = new MultiTransactionStatus(transactionManagers.get(0)/*First TM is main TM*/);

        for (PlatformTransactionManager transactionManager : transactionManagers) {        	
            mts.registerTransactionManager(definition, transactionManager);
        }

        return mts;
    }

    public ISynchronizationManager getSynchronizationManager() {
		return synchronizationManager;
	}

	public void setSynchronizationManager(ISynchronizationManager synchronizationManager) {
		this.synchronizationManager = synchronizationManager;
	}

	public void setTransactionManagers(List<PlatformTransactionManager> transactionManagers) {
		this.transactionManagers = transactionManagers;
	}

	@Override
    public void commit(TransactionStatus status) throws TransactionException {

        MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus) status;

        boolean commit = true;
        Exception commitException = null;
        PlatformTransactionManager commitExceptionTransactionManager = null;

        for (PlatformTransactionManager transactionManager : reverse(transactionManagers)) {
            if (commit) {
                try {
                    multiTransactionStatus.commit(transactionManager);
                } catch (Exception ex) {
                    commit = false;
                    commitException = ex;
                    commitExceptionTransactionManager = transactionManager;
                }
            } else {
                //after unsucessfull commit we must try to rollback remaining transaction managers
                try {
                    multiTransactionStatus.rollback(transactionManager);
                } catch (Exception ex) {
                    logger.warn("Rollback exception (after commit) (" + transactionManager + ") " + ex.getMessage(), ex);
                }
            }
        }

        if (multiTransactionStatus.isNewSynchonization()){
            synchronizationManager.clearSynchronization();
        }

        if (commitException != null) {
            boolean firstTransactionManagerFailed = commitExceptionTransactionManager == getLastTransactionManager();
            int transactionState = firstTransactionManagerFailed ? HeuristicCompletionException.STATE_ROLLED_BACK : HeuristicCompletionException.STATE_MIXED;
            throw new HeuristicCompletionException(transactionState, commitException);
        }

    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {

        Exception rollbackException = null;
        PlatformTransactionManager rollbackExceptionTransactionManager = null;


        MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus) status;

        for (PlatformTransactionManager transactionManager : reverse(transactionManagers)) {
            try {
                multiTransactionStatus.rollback(transactionManager);
            } catch (Exception ex) {
                if (rollbackException == null) {
                    rollbackException = ex;
                    rollbackExceptionTransactionManager = transactionManager;
                } else {
                    logger.warn("Rollback exception (" + transactionManager + ") " + ex.getMessage(), ex);
                }
            }
        }

        if (multiTransactionStatus.isNewSynchonization()){
            synchronizationManager.clearSynchronization();
        }

        if (rollbackException != null) {
            throw new UnexpectedRollbackException("Rollback exception, originated at ("+rollbackExceptionTransactionManager+") "+
              rollbackException.getMessage(), rollbackException);
        }
    }

    private <T> Iterable<T> reverse(Collection<T> collection) {
        List<T> list = new ArrayList<T>(collection);
        Collections.reverse(list);
        return list;
    }


	public List<PlatformTransactionManager> getTransactionManagers() {
		return transactionManagers;
	}

	private PlatformTransactionManager getLastTransactionManager() {
        return transactionManagers.get(lastTransactionManagerIndex());
    }

    private int lastTransactionManagerIndex() {
        return transactionManagers.size() - 1;
    }

	public String getDefaultDataSourceId() {
		return defaultDataSourceId;
	}

	public void setDefaultDataSourceId(String defaultDataSourceId) {
		this.defaultDataSourceId = defaultDataSourceId;
	}
	public DataSource getDefaultDataSource() {
		return defaultDataSource;
	}
	public void setDefaultDataSource(DataSource defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}

}
