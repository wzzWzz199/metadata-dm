package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;

/**
 * 今年十二月三十一号
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class ThisYearLastDayProvider extends AbstractDateProvider{

	@Override
	protected void handleCalendar(Calendar calendar) {
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 59);
	}

}
