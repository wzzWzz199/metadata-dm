package com.hayden.hap.upgrade.entity;

import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/5 16:53
 */
public class ShowVersionVO {
    private String ver;
    private String status;
    private List<String> patchs;
    private List<String> historypatchs;

    public List<String> getHistorypatchs() {
        return historypatchs;
    }

    public void setHistorypatchs(List<String> historypatchs) {
        this.historypatchs = historypatchs;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPatchs() {
        return patchs;
    }

    public void setPatchs(List<String> patchs) {
        this.patchs = patchs;
    }
}
