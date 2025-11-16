package com.hayden.hap.upgrade.entity;

import java.io.Serializable;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/12 9:33
 */
public class LogsVO implements Serializable {

    private Long datetime;
    private String progress;
    private String name;
    private String date;
    private String msg;
    private String status;

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
