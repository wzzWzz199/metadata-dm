package com.hayden.hap.common.db.orm.typeHandler;

import com.hayden.hap.common.common.exception.BaseRuntimeException;

/**
 * 
 * @ClassName: TypeException 
 * @Description: java类型或jdbc类型转化处理异常
 * @author LUYANYING
 * @date 2015年4月17日 下午4:32:16 
 * @version V1.0   
 *
 */
public class TypeException extends BaseRuntimeException {

  private static final long serialVersionUID = 8614420898975117130L;

  public TypeException() {
    super();
  }

  public TypeException(String message) {
    super(message);
  }

  public TypeException(String message, Exception cause) {
    super(message, cause);
  }

  public TypeException(Exception cause) {
    super(cause);
  }

}
