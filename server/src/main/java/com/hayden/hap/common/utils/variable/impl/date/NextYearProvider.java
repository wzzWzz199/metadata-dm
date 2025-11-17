package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;

/**
 * 明年一月一号
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class NextYearProvider extends AbstractDateProvider {

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.impl.date.AbstractDateProvider#handleCalendar(java.util.Calendar)
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	protected void handleCalendar(Calendar calendar) {
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

	}

}
