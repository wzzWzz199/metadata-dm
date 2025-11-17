package com.hayden.hap.common.entity;

import java.io.Serializable;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/16 14:34
 */
public class UserVO implements Serializable {
    private String usercode;
    private String password;

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
