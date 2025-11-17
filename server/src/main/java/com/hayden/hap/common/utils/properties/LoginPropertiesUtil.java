package com.hayden.hap.common.utils.properties;



/**
 * 登录配置文件
 * 
 * @author wushuangyang
 * @date 2016年05月31日
 */
public class LoginPropertiesUtil {
	/** 是否启用验证码 */
	private static Boolean captchaEnabled = false;
	private static Long captchaShowFailDuring;
	/** 未启动验证码的情况下，当登录失败多少次后自动开启验证码 */
	private static Integer captchaShowFailCount = 0;
	/** 同一帐号登录失败次数时间范围（单位：秒） */
	private static Long accountFailDuring;
	/** 登录失败多少次后锁定账户 */
	private static Integer lockedFailCount = 3;
	/** 自动锁定的帐号多长时间后自动解锁（单位：秒） */
	private static Long accountUnlockTime;
	/** 同一ip登录失败次数时间范围（单位：秒） */
	private static Long ipFailDuring;
	/** 同一ip在指定时间内登录失败多少次后锁定用户 */
	private static Integer ipLockedFailCount;
	/** 自动锁定的ip多长时间后自动解锁（单位：秒） */
	private static Long ipUnlockTime;
	/** 是否允许管理租户登录 */
	private static Boolean manageTenantLoginLimited = true;
	private static Boolean getPubKeyEnabled = true;
	/** 密码强度正则表达式（javascript正则表达式） */
	private static String passwordRegexp;
	/** 密码强度不满足正则表达式的提示 */
	private static String passwordErrorTip;
	/**
	 * 密码是否需要散列
	 */
	private static String encryptType; 

	public void init() {
	}

	public static Boolean getCaptchaEnabled() {
		return captchaEnabled;
	}

	public static Integer getCaptchaShowFailCount() {
		return captchaShowFailCount;
	}

	public static Integer getLockedFailCount() {
		return lockedFailCount;
	}

	public static Boolean getManageTenantLoginLimited() {
		return manageTenantLoginLimited;
	}

	public static Boolean getGetPubKeyEnabled() {
		return getPubKeyEnabled;
	}

	public void setGetPubKeyEnabled(Boolean getPubKeyEnabled) {
		this.getPubKeyEnabled = getPubKeyEnabled;
	}

	public void setCaptchaEnabled(Boolean captchaEnabled) {
		this.captchaEnabled = captchaEnabled;
	}

	public void setCaptchaShowFailCount(Integer captchaShowFailCount) {
		this.captchaShowFailCount = captchaShowFailCount;
	}

	public void setLockedFailCount(Integer lockedFailCount) {
		this.lockedFailCount = lockedFailCount;
	}

	public void setManageTenantLoginLimited(Boolean manageTenantLoginLimited) {
		this.manageTenantLoginLimited = manageTenantLoginLimited;
	}

	public static String getPasswordRegexp() {
		return passwordRegexp;
	}

	public void setPasswordRegexp(String passwordRegexp) {
		this.passwordRegexp = passwordRegexp;
	}

	public static String getPasswordErrorTip() {
		return passwordErrorTip;
	}

	public void setPasswordErrorTip(String passwordErrorTip) {
		this.passwordErrorTip = passwordErrorTip;
	}

	public static Long getAccountFailDuring() {
		return accountFailDuring;
	}

	public void setAccountFailDuring(Long accountFailDuring) {
		this.accountFailDuring = accountFailDuring;
	}

	public static Long getAccountUnlockTime() {
		return accountUnlockTime;
	}

	public void setAccountUnlockTime(Long accountUnlockTime) {
		this.accountUnlockTime = accountUnlockTime;
	}

	public static Long getIpFailDuring() {
		return ipFailDuring;
	}

	public void setIpFailDuring(Long ipFailDuring) {
		this.ipFailDuring = ipFailDuring;
	}

	public static Integer getIpLockedFailCount() {
		return ipLockedFailCount;
	}

	public void setIpLockedFailCount(Integer ipLockedFailCount) {
		this.ipLockedFailCount = ipLockedFailCount;
	}

	public static Long getIpUnlockTime() {
		return ipUnlockTime;
	}

	public void setIpUnlockTime(Long ipUnlockTime) {
		this.ipUnlockTime = ipUnlockTime;
	}

	public static Long getCaptchaShowFailDuring() {
		return captchaShowFailDuring;
	}

	public void setCaptchaShowFailDuring(Long captchaShowFailDuring) {
		this.captchaShowFailDuring = captchaShowFailDuring;
	}

	public static String getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}

}
