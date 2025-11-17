package com.hayden.hap.dbop.exception;

import com.hayden.hap.common.formmgr.message.Message;

import java.util.List;

/**
 * 可捕获异常。
 * 应用场景：主方法声明事务管理，其中调用子方法。当子方法的执行不能影响到主方法时，声明抛出该异常。
 * 主方法中对异常进行捕获 ，将继续执行后续逻辑。 
 *  
 * @author wangyi
 * @date 2018年3月19日
 */
@SuppressWarnings("serial")
public class HDCapturedException extends BaseException {
	/**
	 * @param message
	 * @param e
	 */
	public HDCapturedException(Exception e) {
		super(e);
	}
	
	public HDCapturedException(String message){
		super(message);
	}
	
	public HDCapturedException(String message,Exception e){
		super(message,e);
	}

	public HDCapturedException(String message,List<Message> list) {
		super(message,list);
	}
	
	public HDCapturedException(String message,Exception e,List<Message> list) {
		super(message,e,list);
	}
	
	public HDCapturedException(String code,String message){
		super(code, message);
	} 
}
