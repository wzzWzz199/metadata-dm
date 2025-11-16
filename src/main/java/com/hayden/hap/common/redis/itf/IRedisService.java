package com.hayden.hap.common.redis.itf;

import com.hayden.hap.common.spring.service.IService;

/**
 * 
 * @author zhangfeng
 * @date 2018年3月27日
 */
@IService("redisService")
public interface IRedisService {

	/**
	 * 登陆是否需要图形验证码
	 * @param usercode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	boolean isNeedValidateLoginCaptcha(String usercode);
	
	/**
	 * 设置需要图形验证码
	 * @param usercode 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	void setNeedValidateLoginCaptcha(String usercode, boolean needCaptch);
	
	/**
	 * 获取登陆验证码
	 * @param captchaStoken
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	String getLoginCaptcha(String captchaStoken);
	
	/**
	 * 存储登陆验证码
	 * @param captchaStoken
	 * @param captcha
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	String PersistLoginCaptcha(String captchaStoken, String captcha);
	
	/**
	 * 发短信是否需要图形验证码
	 * @param usercode
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	boolean isNeedValidateSmsCaptcha(String ip, String appCode);
	
	/**
	 * 设置发短信需要图形验证码
	 * @param usercode 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	void setNeedValidateSmsCaptcha(String ip, String appCode);
	
	/**
	 * 获取发短信的图形验证码
	 * @param captchaStoken
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	String getSmsCaptcha(String captchaStoken);
	
	/**
	 * 存储发短信的图形验证码
	 * @param captchaStoken
	 * @param captcha
	 * @return 
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	String PersistSmsCaptcha(String captchaStoken, String captcha);
}
