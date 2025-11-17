package com.hayden.hap.db.dataSource;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateManager;
import com.hayden.hap.common.db.orm.jdbc.JdbcTemplateSupportDao;
import com.hayden.hap.db.dataSource.entity.EnvConfVO;
import com.hayden.hap.db.dataSource.entity.ProjectConfigVO;
import com.hayden.hap.serial.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 10:16
 */
@Component("dataSourceCreator")
public class DataSourceCreator implements ApplicationContextAware {

    @Autowired
    private JdbcTemplateSupportDao jdbcTemplateSupportDao;

    @Value("${META.ENVCONF}")
    private String ENVCONF;
    private String envCodeCache;
    private List<ProjectConfigVO> projectList;
    private Map<String, EnvConfVO> envMap = new HashMap<>();
    private Map<String, DataSource> dataSourceMap = new HashMap<>();
    private Map<String, RedisTemplate> redisTemplateMap = new HashMap<>();
    private ConfigurableApplicationContext applicationContext = null;
    private DefaultListableBeanFactory beanFactory = null;

    /**
     * 拼装数据源id,可通过此id获得数据源
     *
     * @param project
     * @param env
     * @return
     */
    public String getDataSourceId(String project, String env) {
        return ("dataSource" + project + env).intern();
    }

    /**
     * 根据数据源id获得数据源
     *
     * @param dataSourceId
     * @return
     * @throws HDException
     */
    public DataSource getDataSource(String dataSourceId) throws HDException {
        try {
            if (!dataSourceMap.containsKey(dataSourceId) || jdbcTemplateSupportDao.getJdbcTemplateManager().getJdbcTemplate(dataSourceId) == null) {
                //每次调用重新组织项目环境，以防止apollo修改
                organizeProjectAndEnv();
                //不存在环境则提示，一般不存在这种情况
                if (!envMap.containsKey(dataSourceId)) {
                    throw new HDException("没有配置对应的项目环境");
                }
                //注入数据源
                createDataSource(dataSourceId, envMap.get(dataSourceId));
                DataSource dataSource = (DataSource) applicationContext.getBean(dataSourceId);
                dataSourceMap.put(dataSourceId, dataSource);

                jdbcTemplateSupportDao.setJdbcTemplateManager(new JdbcTemplateManager(jdbcTemplateSupportDao.getDataSourceManager()));
                jdbcTemplateSupportDao.init(dataSourceMap);
            }
            return dataSourceMap.get(dataSourceId);
        } catch (BeanDefinitionStoreException e) {
            throw new HDException(e);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        this.beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        dataSourceMap.put("dataSource", dataSource);

        try {
            //项目启动时先组织缓存数据项目环境
            organizeProjectAndEnv();
        } catch (HDException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注入新数据源
     *
     * @param dataSourceId
     * @param envConfVO
     */
    public void createDataSource(String dataSourceId, EnvConfVO envConfVO) throws BeanDefinitionStoreException {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(com.alibaba.druid.pool.xa.DruidXADataSource.class);
        builder.setInitMethodName("init");
        builder.setDestroyMethodName("close");
        builder.addPropertyValue("url", envConfVO.getUrl());
        builder.addPropertyValue("username", envConfVO.getUsername());
        builder.addPropertyValue("password", envConfVO.getPassword());
        if (StringUtils.isNotBlank(envConfVO.getDriverClassName())){
            builder.addPropertyValue("driverClassName",envConfVO.getDriverClassName());
        }

        builder.addPropertyValue("initialSize", 10);
        builder.addPropertyValue("maxActive", 100);
        builder.addPropertyValue("minIdle", 10);
        builder.addPropertyValue("filters", "stat,slf4j");
        builder.addPropertyValue("maxWait", 60000);
        builder.addPropertyValue("timeBetweenEvictionRunsMillis", 60000);
        builder.addPropertyValue("minEvictableIdleTimeMillis", 300000);
        builder.addPropertyValue("validationQuery", "select 1");

        builder.addPropertyValue("testWhileIdle", true);
        builder.addPropertyValue("testOnBorrow", false);
        builder.addPropertyValue("testOnReturn", false);
        builder.addPropertyValue("removeAbandoned", true);
        builder.addPropertyValue("removeAbandonedTimeout", 1800);
        builder.addPropertyValue("logAbandoned", true);
        builder.addPropertyValue("dbType", envConfVO.getDbType()==null?"mysql":envConfVO.getDbType());

        this.beanFactory.registerBeanDefinition(dataSourceId, builder.getBeanDefinition());
    }

    /**
     * 组织项目环境结构数据
     *
     * @throws HDException
     */
    public void organizeProjectAndEnv() throws HDException {
        if (envCodeCache != null) {
            if (ENVCONF == null || ENVCONF.equals(envCodeCache)) {
                return;
            }
        }
        //判断执行时环境配置是否发生了变化，如果发生了变化则重新加载
        projectList = JsonUtils.parseArrayInit(ENVCONF, ProjectConfigVO.class);

        for (ProjectConfigVO projectConfigVO : projectList) {
            if (projectConfigVO.getEnvs() != null) {
                for (EnvConfVO envConfVO : projectConfigVO.getEnvs()) {
                    String dataSourceName = "dataSource" + projectConfigVO.getCode() + envConfVO.getCode();
                    envMap.put(dataSourceName, envConfVO);
                }
            }
        }
        //记录上一次的值
        envCodeCache = ENVCONF;
    }

    public List<ProjectConfigVO> getProjectList() throws HDException {
        this.organizeProjectAndEnv();
        return projectList;
    }

    /**
     * 根据数据源id获得RedisTemplate
     *
     * @param dataSourceId
     * @return
     * @throws HDException
     */
    public RedisTemplate getRedisTemplate(String dataSourceId) throws HDException {
        try {
            if (!redisTemplateMap.containsKey(dataSourceId)) {
                //每次调用重新组织项目环境，以防止apollo修改
                organizeProjectAndEnv();
                //不存在环境则提示，一般不存在这种情况
                if (!envMap.containsKey(dataSourceId)) {
                    throw new HDException("没有配置对应的项目环境");
                }
                if (envMap.get(dataSourceId).getRedisConfVO() == null) {
                    redisTemplateMap.put(dataSourceId, null);
                }else{
                    //注入redistemplate
                    createRedisTemplate(dataSourceId,envMap.get(dataSourceId));
                    RedisTemplate redisTemplate = (RedisTemplate) applicationContext.getBean(dataSourceId+"_redisTemplate");
                    redisTemplateMap.put(dataSourceId, redisTemplate);
                }
            }
            return redisTemplateMap.get(dataSourceId);
        } catch (BeanDefinitionStoreException e) {
            throw new HDException(e);
        }
    }

    private void createRedisTemplate(String dataSourceId, EnvConfVO envConfVO) throws BeanDefinitionStoreException  {
        BeanDefinitionBuilder jedisPoolConfig = BeanDefinitionBuilder.genericBeanDefinition(redis.clients.jedis.JedisPoolConfig.class);
        jedisPoolConfig.addPropertyValue("maxIdle", 8);
        jedisPoolConfig.addPropertyValue("maxTotal", 16);
        jedisPoolConfig.addPropertyValue("maxWaitMillis", 10000);
        jedisPoolConfig.addPropertyValue("testOnBorrow", true);
        this.beanFactory.registerBeanDefinition(dataSourceId+"_jedisPoolConfig", jedisPoolConfig.getBeanDefinition());

        BeanDefinitionBuilder jedisConnectionFactory = BeanDefinitionBuilder.genericBeanDefinition(org.springframework.data.redis.connection.jedis.JedisConnectionFactory.class);
        jedisConnectionFactory.addPropertyReference("poolConfig", dataSourceId+"_jedisPoolConfig");
        jedisConnectionFactory.addPropertyValue("hostName", envConfVO.getRedisConfVO().getHost());
        jedisConnectionFactory.addPropertyValue("port", envConfVO.getRedisConfVO().getPort());
        jedisConnectionFactory.addPropertyValue("password", envConfVO.getRedisConfVO().getPass());
        jedisConnectionFactory.addPropertyValue("database", envConfVO.getRedisConfVO().getDatabase());
        this.beanFactory.registerBeanDefinition(dataSourceId+"_jedisConnectionFactory", jedisConnectionFactory.getBeanDefinition());
        BeanDefinitionBuilder redisTemplate = BeanDefinitionBuilder.genericBeanDefinition(org.springframework.data.redis.core.RedisTemplate.class);
        redisTemplate.addPropertyReference("connectionFactory",dataSourceId+"_jedisConnectionFactory");
        redisTemplate.addPropertyValue("keySerializer", new StringRedisSerializer());
        this.beanFactory.registerBeanDefinition(dataSourceId+"_redisTemplate", redisTemplate.getBeanDefinition());
    }
}
