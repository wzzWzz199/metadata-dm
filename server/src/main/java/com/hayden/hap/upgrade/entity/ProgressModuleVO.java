package com.hayden.hap.upgrade.entity;

import java.io.Serializable;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 10:48
 */
public class ProgressModuleVO implements Serializable {
    private String code;
    private String name;
    private String currentver;
    private String productver;
    private Long prolastfilets;
    private String status;
    private Float progress;
    private String person;
    private Long datetime;
    private Long lastfilets;
    private Integer patchNum;
    private Float pct;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getProlastfilets() {
        return prolastfilets;
    }

    public void setProlastfilets(Long prolastfilets) {
        this.prolastfilets = prolastfilets;
    }

    public Integer getPatchNum() {
        return patchNum;
    }

    public void setPatchNum(Integer patchNum) {
        this.patchNum = patchNum;
    }

    public Float getPct() {
        return pct;
    }

    public void setPct(Float pct) {
        this.pct = pct;
    }

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

    public String getCurrentver() {
        return currentver;
    }

    public void setCurrentver(String currentver) {
        this.currentver = currentver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public Long getLastfilets() {
        return lastfilets;
    }

    public void setLastfilets(Long lastfilets) {
        this.lastfilets = lastfilets;
    }

    public String getProductver() {
        return productver;
    }

    public void setProductver(String productver) {
        this.productver = productver;
    }
}
