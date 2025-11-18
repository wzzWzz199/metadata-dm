package com.hayden.hap.vo.upgrade;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/4 18:07
 */
public class ShowModuleVO {
    private String code;
    private String name;
    private String currentver;
    private String status;
    private String person;
    private Long datetime;

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

}
