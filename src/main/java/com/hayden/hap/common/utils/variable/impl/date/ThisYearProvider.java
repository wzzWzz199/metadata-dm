package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;

/**
 * 今年一月一号
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class ThisYearProvider extends AbstractDateProvider{

	@Override
	protected void handleCalendar(Calendar calendar) {
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
	}

}
