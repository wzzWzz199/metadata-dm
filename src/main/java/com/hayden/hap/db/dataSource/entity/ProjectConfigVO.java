package com.hayden.hap.db.dataSource.entity;

import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/2 11:44
 */
public class ProjectConfigVO {
    private String code;
    private String name;
    private List<EnvConfVO> envs;

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

    public List<EnvConfVO> getEnvs() {
        return envs;
    }

    public void setEnvs(List<EnvConfVO> envs) {
        this.envs = envs;
    }
}
