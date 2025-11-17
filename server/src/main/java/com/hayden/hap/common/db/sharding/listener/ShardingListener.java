package com.hayden.hap.common.db.sharding.listener;

import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.common.db.sharding.transaction.ShardingTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/** 
 * @ClassName: ShardingListener 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月2日 下午7:02:23 
 * @version V1.0   
 *  
 */
@Component
public class ShardingListener implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = LoggerFactory.getLogger(ShardingListener.class);  
	
        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
                ApplicationContext applicationContext = event.getApplicationContext();
            if(applicationContext == null)
                return ;
            Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
            JdbcTemplateSupportDao jdbcTemplateSupportDao = applicationContext.getBean(JdbcTemplateSupportDao.class);
            if(jdbcTemplateSupportDao != null){
            	jdbcTemplateSupportDao.init(dataSources);
            }
            ShardingTransactionManager shardingTransactionManager = applicationContext.getBean(ShardingTransactionManager.class);
            if(shardingTransactionManager != null){
            	shardingTransactionManager.init(dataSources);
            }
        } catch (Exception e) {
                logger.error(e.getMessage());
        }
        }
}
