package com.hayden.hap.dbop.exception;

/** 
 * 
 * @ClassName: DaoException 
 * @Description: 数据层运行异常定义
 * @author LUYANYING
 * @date 2015年5月26日 下午2:58:28 
 * @version V1.0   
 *
 */

public class DaoRuntimeException extends BaseRuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DaoRuntimeException(String code,String message,Exception e) {
		super(code,e);
		this.setCode(code);
		this.setMessage(message);
	}
	public DaoRuntimeException(String message,Exception e) {
		super(message,e);
		this.setCode("daoError");
		this.setMessage(message);
	}
	public DaoRuntimeException(String message) {
		super(message);
		this.setCode("daoError");
		this.setMessage(message);
	}
}
