package com.hayden.hap.common.common.exception;


/**
 * 应该被捕获的异常，用于特殊的业务逻辑
 * @author zhangfeng
 * @date 2016年1月20日
 */
public class ShouldBeCatchException extends Exception implements HaydenBizException{

	private static final long serialVersionUID = 1L;

	private String code;

	private String message;
	
	public ShouldBeCatchException(){
		 super();
	}
	
	public ShouldBeCatchException(String message,Exception e){
		super(message,e);
	}
	
	public ShouldBeCatchException(String message){
		super(message);
	}
	
	public ShouldBeCatchException(Exception e){
		super(e);
	}
	
	public ShouldBeCatchException(String code,String message){
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
	

	@Override
	public String getCode() {
		return code;
	}


	@Override
	public String getErrorMessage() {
		return message;
	}
	


}
