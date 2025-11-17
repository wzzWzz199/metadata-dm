package com.hayden.hap.common.utils.properties;


import org.springframework.beans.factory.annotation.Value;

/**
 * 登录配置文件
 *
 * @author wushuangyang
 * @date 2016年12月08日
 */
public class DebugPropertiesUtil {

    private static boolean noLogin = false;

    private static boolean forward = true;

    private static String testToken = null;

    private static String noLoginUser;

    private static boolean cipherMode = true;

    public void init() {

    }

    public static boolean isNoLogin() {
        return noLogin;
    }

    public static String getTestToken() {
        return testToken;
    }

    public void setTestToken(String testToken) {
        DebugPropertiesUtil.testToken = testToken;
    }

    public void setNoLogin(boolean noLogin) {
        DebugPropertiesUtil.noLogin = noLogin;
    }

    public static boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        DebugPropertiesUtil.forward = forward;
    }

    public static String getNoLoginUser() {
        return noLoginUser;
    }
    @Value("${noLoginUser:test}")
    public void setNoLoginUser(String noLoginUser) {
        DebugPropertiesUtil.noLoginUser = noLoginUser;
    }

    @Value("${debug.cipher_mode:false}")
    public static boolean isCipherMode() {
        return cipherMode;
    }

    public static void setCipherMode(boolean cipherMode) {
        DebugPropertiesUtil.cipherMode = cipherMode;
    }
}
