package com.hayden.hap.common.resp;

/**
 * 状态码常量类
 * 注：类型定义成int型，不要Integer
 * @author zhangfeng
 * @date 2016年12月22日
 */
public class Status {

	/**
	 * 成功
	 */
	public static final int SUCCESS = 3200;
	
	/**
	 * 失败
	 */
	public static final int FAIL = 3500;
	
	/**
	 * 未认证
	 */
	public static final int UNAUTHORIZED = 4100; 
	
	/**
	 * 租户id错误
	 */
	public static final int TID_ERROR = 4101;
	
	/**
	 * 无权限
	 */
	public static final int NO_PERMISSION = 4030;
	
	/**
	 * 升级中
	 */
	public static final int UPGRADING = 1503;
}
