package com.hayden.hap.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redis.metadata")
public class MetadataRedisProperties {
    private String host = "localhost";
    private int port = 6379;
    private String pass = "";
    private int database = 0;
    private int maxIdle = 8;
    private int maxTotal = 8;
    private long maxWaitMillis = -1L;
    private boolean testOnBorrow = true;

}
