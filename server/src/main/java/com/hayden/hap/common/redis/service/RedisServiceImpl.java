package com.hayden.hap.common.redis.service;

import com.hayden.hap.common.redis.itf.IRedisService;
import com.hayden.hap.common.utils.properties.LoginPropertiesUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author zhangfeng
 * @date 2018年3月27日
 */
@Service("redisService")
public class RedisServiceImpl implements IRedisService {

	@Resource(name = "redisTemplate_common")
	private RedisTemplate<String, Object> redisTemplate_common;
	
	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#isNeedValidateLoginCaptcha(java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public boolean isNeedValidateLoginCaptcha(String usercode) {
		String key = getValidateLoginCaptchaKey(usercode);
		if(redisTemplate_common.hasKey(key)) {
			return true;
		}
		return false;
	}
	
	private String getValidateLoginCaptchaKey(String usercode) {
		return "needLoginCaptcha_"+usercode;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#setNeedValidateLoginCaptcha(java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public void setNeedValidateLoginCaptcha(String usercode, boolean needCaptcha) {
		String key = getValidateLoginCaptchaKey(usercode);
		
		if(!needCaptcha) {
			redisTemplate_common.delete(key);
			return;
		}
		
		if(redisTemplate_common.hasKey(key)) {
			redisTemplate_common.expire(key, LoginPropertiesUtil.getCaptchaShowFailDuring(), TimeUnit.SECONDS);
		}else {
			redisTemplate_common.opsForValue().set(key, Boolean.TRUE, LoginPropertiesUtil.getCaptchaShowFailDuring(), TimeUnit.SECONDS);
		}
	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#getLoginCaptcha(java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public String getLoginCaptcha(String captchaStoken) {
		Object obj = redisTemplate_common.opsForValue().get(captchaStoken);
//		Object obj = redisTemplate_common.boundValueOps(captchaStoken).get();
		return obj!=null?obj.toString():null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#PersistLoginCaptcha(java.lang.String, java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public String PersistLoginCaptcha(String captchaStoken, String captcha) {
		redisTemplate_common.opsForValue().set(captchaStoken, captcha,15, TimeUnit.MINUTES);
//		redisTemplate_common.boundValueOps(captchaStoken).set(captcha);
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#isNeedValidateSmsCaptcha(java.lang.String, java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public boolean isNeedValidateSmsCaptcha(String ip, String appCode) {
		// TODO Auto-generated method stub
		return false;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#setNeedValidateSmsCaptcha(java.lang.String, java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public void setNeedValidateSmsCaptcha(String ip, String appCode) {
		// TODO Auto-generated method stub

	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#getSmsCaptcha(java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public String getSmsCaptcha(String captchaStoken) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 *
	 * @see com.hayden.hap.common.redis.itf.IRedisService#PersistSmsCaptcha(java.lang.String, java.lang.String)
	 * @author zhangfeng
	 * @date 2018年3月27日
	 */
	@Override
	public String PersistSmsCaptcha(String captchaStoken, String captcha) {
		// TODO Auto-generated method stub
		return null;
	}

}
