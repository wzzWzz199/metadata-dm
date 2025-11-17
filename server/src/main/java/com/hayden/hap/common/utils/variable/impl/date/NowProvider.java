package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Date;

/**
 * 现在
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class NowProvider implements IDateProvider {

	@Override
	public Date getDate() {
		return new Date();
	}


}
