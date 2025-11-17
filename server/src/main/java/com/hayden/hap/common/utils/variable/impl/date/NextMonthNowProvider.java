package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;

/**
 * 下个月的今天
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class NextMonthNowProvider extends AbstractDateProvider {

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.impl.date.AbstractDateProvider#handleCalendar(java.util.Calendar)
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	protected void handleCalendar(Calendar calendar) {
		calendar.add(Calendar.MONTH, 1);
	}

}
