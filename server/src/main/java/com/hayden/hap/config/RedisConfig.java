package com.hayden.hap.config;

import com.hayden.hap.config.properties.MetadataRedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(MetadataRedisProperties.class)
public class RedisConfig {

    @Bean
    public JedisConnectionFactory metadataJedisConnectionFactory(MetadataRedisProperties properties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(properties.getHost());
        configuration.setPort(properties.getPort());
        configuration.setDatabase(properties.getDatabase());
        if (StringUtils.hasText(properties.getPass())) {
            configuration.setPassword(properties.getPass());
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(properties.getMaxIdle());
        poolConfig.setMaxTotal(properties.getMaxTotal());
        poolConfig.setMaxWait(Duration.ofMillis(properties.getMaxWaitMillis()));
        poolConfig.setTestOnBorrow(properties.isTestOnBorrow());

        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig)
                .build();

        return new JedisConnectionFactory(configuration, clientConfiguration);
    }

    @Bean(name = "metadata_redisTemplate_cache")
    public RedisTemplate<String, Object> metadataRedisTemplateCache(RedisConnectionFactory metadataJedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(metadataJedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = {"redisTemplate", "redisTemplate_common", "redisTemplate_rq"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory metadataJedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(metadataJedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
