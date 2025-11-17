package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;

/**
 * 下个月一号
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class NextMonthProvider extends AbstractDateProvider {

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.impl.date.AbstractDateProvider#handleCalendar(java.util.Calendar)
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	protected void handleCalendar(Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);

	}

}
