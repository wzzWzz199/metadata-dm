package com.hayden.hap.common.db.orm.exception;

import com.hayden.hap.dbop.exception.BaseRuntimeException;

/** 
 * @ClassName: ResultSetRowMapperException 
 * @Description: 
 * @author LUYANYING
 * @date 2015年4月21日 上午11:46:45 
 * @version V1.0   
 *  
 */
public class ResultSetRowMapperException extends BaseRuntimeException{

	private static final long serialVersionUID = -5193951144111235932L;
	
	private static final String CODE_ROWMAPPER = "rowMapper";

	public ResultSetRowMapperException(String message, Exception e){
		super(message, e);
		this.setCode(CODE_ROWMAPPER);
	}
}
