package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author zhangfeng
 * @date 2018年7月10日
 */
public abstract class AbstractDateProvider implements IDateProvider {

	/** 
	 *
	 * @see com.hayden.hap.common.utils.variable.impl.date.IDateProvider#getDate()
	 * @author zhangfeng
	 * @date 2018年7月10日
	 */
	@Override
	public Date getDate() {
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(new Date());
		handleCalendar(calendar);
		return calendar.getTime();
	}

	protected abstract void handleCalendar(Calendar calendar);
}
