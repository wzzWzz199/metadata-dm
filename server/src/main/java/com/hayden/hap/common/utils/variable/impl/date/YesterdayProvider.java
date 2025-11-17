package com.hayden.hap.common.utils.variable.impl.date;

import java.util.Calendar;

/**
 * 昨天
 * @author zhangfeng
 * @date 2018年7月10日
 */
public class YesterdayProvider extends AbstractDateProvider {

	@Override
	protected void handleCalendar(Calendar calendar) {
		calendar.add(Calendar.DATE, -1);		
	}
}
