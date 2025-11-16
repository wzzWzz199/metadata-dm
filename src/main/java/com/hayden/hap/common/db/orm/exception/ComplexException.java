package com.hayden.hap.common.db.orm.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: ComplexException 
 * @Description: 复合异常：对多个异常信息的包装
 * @author LUYANYING
 * @date 2015年4月2日 下午8:45:29 
 * @version V1.0   
 *
 */
public class ComplexException extends Throwable {
	private static final long serialVersionUID = -3415211380220963254L;
	private List<Throwable> causes = new ArrayList<Throwable>();

	public ComplexException() {

	}

	public ComplexException(List<Throwable> causes) {
		if (!(causes == null || causes.isEmpty()))
			this.causes.addAll(causes);
	}

	public void add(Throwable cause) {
		this.causes.add(cause);
	}

	public List<Throwable> getCauses() {
		return new ArrayList<Throwable>(this.causes);
	}

}