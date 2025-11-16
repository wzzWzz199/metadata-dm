package com.hayden.hap.upgrade.enums;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/8 10:51
 */
public enum UpgradeEnum {

    MODULE_STATUS_UPGRADEABLE("0","可升级"),
    MODULE_STATUS_UPGRADING("1","升级中"),
    MODULE_STATUS_UPGRADEERROR("2","升级异常"),

    VERSION_STATUS_UNUPDATE("0","未升级"),
    VERSION_STATUS_UPDATED("1","已升级"),
    VERSION_STATUS_CHANGED("2","已更新"),

    ENV_TYPE_DEV("dev","开发环境"),
    ENV_TYPE_TEST("test","测试环境"),
    ENV_TYPE_PRE("pre","预发布"),
    ENV_TYPE_PRO("pro","现场"),

    PRODUCT_CODE("hd","产品编码");

    private String code;
    private String value;

    private UpgradeEnum(String code,String value){
        this.code=code;
        this.value=value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
