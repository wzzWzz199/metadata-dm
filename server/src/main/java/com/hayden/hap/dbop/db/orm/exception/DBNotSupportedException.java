package com.hayden.hap.dbop.db.orm.exception;

import com.hayden.hap.dbop.exception.BaseRuntimeException;

/** 
 * @ClassName: DBNotSupportedException 
 * @Description: 不支持数据库异常
 * @author LUYANYING
 * @date 2015年4月16日 上午11:35:33 
 * @version V1.0   
 *  
 */
public class DBNotSupportedException extends BaseRuntimeException {

	private static final long serialVersionUID = -7507914662920839501L;
	private static final String CODE_DBNOTSUPPORTED = "dbNotSupported";
	
	public DBNotSupportedException(String message){
		super(message);
		this.setCode(CODE_DBNOTSUPPORTED);
	}
}
