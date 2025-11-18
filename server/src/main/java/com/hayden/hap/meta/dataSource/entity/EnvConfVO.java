package com.hayden.hap.meta.dataSource.entity;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 10:47
 */
public class EnvConfVO {
    private String code;
    private String name;
    private String url;
    private String username;
    private String password;
    private String dbType;
    private String driverClassName;
    private RedisConfVO redisConfVO;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RedisConfVO getRedisConfVO() {
        return redisConfVO;
    }

    public void setRedisConfVO(RedisConfVO redisConfVO) {
        this.redisConfVO = redisConfVO;
    }

    public String getDbType() { return dbType;}

    public void setDbType(String dbType) { this.dbType = dbType;}

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
