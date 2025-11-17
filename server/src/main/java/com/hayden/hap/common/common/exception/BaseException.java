package com.hayden.hap.common.common.exception;

import com.hayden.hap.common.formmgr.message.Message;

import java.util.List;


/** 
 * @ClassName: BaseRuntimeException 
 * @Description: 
 * @author dongheng
 * @date 2013年10月10日 下午3:49:34 
 * @version V1.0   
 *  
 */

public class BaseException extends Exception implements HaydenBizException{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6543394142103604249L;

	private String code;

	private String message;
	
	private List<Message> list;
	
	public BaseException(){
		 super();
	}
	
	public BaseException(String message,Exception e){
		super(message,e);
	}
	
	public BaseException(String message){
		super(message);
	}
	
	public BaseException(Exception e){
		super(e);
	}
	
	public BaseException(String code,String message){
		super(code);
		this.setCode(code);
		this.setMessage(message);
	}
	
	public BaseException(String message,List<Message> list) {
		super(message);
		this.setMessage(message);
		this.setList(list);
	}
	
	public BaseException(String message,Exception e,List<Message> list) {
		super(message,e);
		this.setMessage(message);
		this.setList(list);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	/* 
	 * @see com.hayden.common.exception.HaydenBizException#getCode() 
	 */ 
	@Override
	public String getCode() {
		return code;
	}

	/*
	 * (non-Javadoc)
	 * @see com.hayden.common.exception.HaydenBizException#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return message;
	}
	
	public List<Message> getList() {
		return list;
	}

	public void setList(List<Message> list) {
		this.list = list;
	}

}
