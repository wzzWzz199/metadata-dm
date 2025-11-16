package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.enumerate.MessageLevel;
import com.hayden.hap.common.formmgr.message.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 可预知、同时需要反馈给前端展示的异常消息处理
 * 
 * @author zhangfeng
 * @date 2015年11月13日
 */
public class ExceptionHandlerUtils {

	public static final String HAS_ERROR_MESSAGE = "错误信息:";
	
	/**
	 * 验证错误消息集合是否有值，若有则将错误消息存入request，同时抛出异常
	 * @param list
	 * @param request
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2015年11月13日
	 */
	public static void handle(List<Message> list,HttpServletRequest request) throws HDException {
		if(list!=null && list.size()>0) {
			boolean hasError = false;
			for(Message resultMessage:list) {
				if(MessageLevel.ERROR==resultMessage.getMessageLevel()) {
					hasError = true;
					break;
				}
			}
			
			if(hasError) {
				HDException exception = new HDException(HAS_ERROR_MESSAGE);
				exception.setList(list);
				throw exception;
			}
		}			
	}
	
	/**
	 * 验证错误消息集合是否有值，若有则将错误消息存入request，同时抛出异常
	 * @param list
	 * @param request
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2015年11月13日
	 */
	public static void handle(List<Message> list) throws HDException {
		if(list!=null && list.size()>0) {
			boolean hasError = false;
			for(Message resultMessage:list) {
				if(MessageLevel.ERROR==resultMessage.getMessageLevel()) {
					hasError = true;
					break;
				}
			}
			
			if(hasError) {
				HDException exception = new HDException(HAS_ERROR_MESSAGE);
				exception.setList(list);
				throw exception;
			}
		}			
	}
}
