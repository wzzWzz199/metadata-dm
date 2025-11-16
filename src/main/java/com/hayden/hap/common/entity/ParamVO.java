package com.hayden.hap.common.entity;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 14:46
 */
public class ParamVO {
    private String project;
    private String env;
    private String module;
    private String version;
    private Long tenantid;
    private String dataSourceId;
    private Long datetime;
    private String proVer;
    private Boolean hasProduct=false;

    public String getProVer() {
        return proVer;
    }

    public void setProVer(String proVer) {
        if(proVer!=null&&!proVer.equals(""))
            hasProduct=true;
        this.proVer = proVer;
    }

    public Boolean getHasProduct() {
        return hasProduct;
    }

    public void setHasProduct(Boolean hasProduct) {
        this.hasProduct = hasProduct;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Long getTenantid() {
        return tenantid;
    }

    public void setTenantid(Long tenantid) {
        this.tenantid = tenantid;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        return (project + env + module).intern();
    }
}
