package com.hayden.hap.common.spring.test;

import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.common.db.sharding.transaction.ShardingTransactionManager;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.util.Map;


/** 
 * @ClassName: BaseSpringTest 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月15日 下午3:29:03 
 * @version V1.0   
 *  
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-conf/*/spring-test*.xml"})
public class BaseSpringTest extends AbstractJUnit4SpringContextTests {
	
	@Before
	public void prepareContextData() {
		AppServiceHelper.setApplicationContext(super.applicationContext);
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
	}
	
}
