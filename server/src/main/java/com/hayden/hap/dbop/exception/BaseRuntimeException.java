package com.hayden.hap.dbop.exception;


/** 
 * @ClassName: BaseRuntimeException 
 * @Description: 
 * @author dongheng
 * @date 2013年10月10日 下午3:49:34 
 * @version V1.0   
 *  
 */

public class BaseRuntimeException extends RuntimeException implements HaydenBizException{
	
	private static final long serialVersionUID = 1L;

	private String code;

	private String message;
	
	public BaseRuntimeException(){
		 super();
	}
	
	public BaseRuntimeException(String message,Exception e){
		super(message,e);
	}
	
	public BaseRuntimeException(String message){
		super(message);
	}
	
	public BaseRuntimeException(Exception e){
		super(e);
	}
	
	public BaseRuntimeException(String code,String message){
		super(code);
		this.setCode(code);
		this.setMessage(message);
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

}
