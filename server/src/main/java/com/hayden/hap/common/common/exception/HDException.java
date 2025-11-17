package com.hayden.hap.common.common.exception;

import com.hayden.hap.common.formmgr.message.Message;

import java.util.List;

/**
 * 海顿业务 Exception , 由底层或业务逻辑处理产生了程序无法处理的意外
 * 
 * 默认使用 HDException
 * 
 * @author lengzy
 * @date 2015年10月22日
 */
public class HDException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2654733692609009860L;

	/**
	 * @param message
	 * @param e
	 */
	public HDException(Exception e) {
		super(e);
	}
	
	public HDException(String message){
		super(message);
	}
	
	public HDException(String message,Exception e){
		super(message,e);
	}

	public HDException(String message,List<Message> list) {
		super(message,list);
	}
	
	public HDException(String message,Exception e,List<Message> list) {
		super(message,e,list);
	}
	
	public HDException(String code,String message){
		super(code, message);
	} 
}
