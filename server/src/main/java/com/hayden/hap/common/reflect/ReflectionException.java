package com.hayden.hap.common.reflect;

import com.hayden.hap.common.common.exception.BaseRuntimeException;

/** 
 * @ClassName: ReflectionException 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月21日 下午3:31:44 
 * @version V1.0   
 *  
 */
public class ReflectionException extends BaseRuntimeException{
	private static final long serialVersionUID = 7642570221267566591L;

	  public ReflectionException() {
	    super();
	  }

	  public ReflectionException(String message) {
	    super(message);
	  }

	  public ReflectionException(String message, Exception cause) {
	    super(message, cause);
	  }

	  public ReflectionException(Exception cause) {
	    super(cause);
	  }
}
