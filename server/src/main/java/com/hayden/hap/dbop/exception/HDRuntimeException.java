package com.hayden.hap.dbop.exception;

/**
 * 海顿 RuntimeException， 由底层或业务逻辑处理产生了程序无法处理的意外
 * 
 * @author lengzy
 * @date 2015年10月22日
 */
public class HDRuntimeException extends BaseRuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -8802663101864088037L;

	/**
	 * @param message
	 * @param e
	 */
	public HDRuntimeException(Exception e) {
		super(e);
	}

	public HDRuntimeException(String message){
		super(message);
	}

	public HDRuntimeException(String message,Exception e){
		super(message,e);
	}

	public HDRuntimeException(String code,String message){
		super(code, message);
	}

	@Override
	public String toString() {
		StringBuilder tureMsg  = new StringBuilder();
		String msg = getLocalizedMessage();
		tureMsg.append("msg: ")
				.append(msg)
				.append(System.lineSeparator());
		StackTraceElement[] stackTrace = getStackTrace();
		if (stackTrace != null){
			for (StackTraceElement stackTraceElement : stackTrace) {
				tureMsg.append(stackTraceElement.toString());
			}
		}
		return tureMsg.toString();
	}
}
