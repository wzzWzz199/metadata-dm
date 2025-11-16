package com.hayden.hap.common.utils;

import org.slf4j.Logger;

/**
 * 打印时间花费日志工具类
 * 
 * @author zhangfeng
 * @date 2017年4月24日
 */
public class TimeSpendUtils {

	/**
	 * debug打印时间花费
	 * @param logger
	 * @param previousTime
	 * @param msg
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月24日
	 */
	public static long debugTimeSpend(Logger logger, long previousTime, String msg) {
		if(logger.isDebugEnabled()) {
			long currentTime = System.currentTimeMillis();
			logger.debug(msg, currentTime-previousTime);
			return currentTime;
		}
		return previousTime;
	}
	
	/**
	 * info打印时间花费
	 * @param logger
	 * @param previousTime
	 * @param msg
	 * @return 
	 * @author zhangfeng
	 * @date 2017年4月24日
	 */
	public static long infoTimeSpend(Logger logger, long previousTime, String msg) {
		if(logger.isInfoEnabled()) {
			long currentTime = System.currentTimeMillis();
			logger.info(msg, currentTime-previousTime);
			return currentTime;
		}
		return previousTime;
	}
	
	/**
	 * error打印时间花费
	 * @param logger
	 * @param previousTime
	 * @param msg
	 * @return 
	 * @author zhangfeng
	 * @date 2018年2月1日
	 */
	public static long errorTimeSpend(Logger logger, long previousTime, String msg) {
		if(logger.isErrorEnabled()) {
			long currentTime = System.currentTimeMillis();
			logger.error(msg, currentTime-previousTime);
			return currentTime;
		}
		return previousTime;
	}
}
