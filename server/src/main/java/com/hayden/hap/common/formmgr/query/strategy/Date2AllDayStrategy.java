package com.hayden.hap.common.formmgr.query.strategy;

import com.hayden.hap.common.utils.date.DateUtils;

import java.util.Calendar;
import java.util.Date;



/**
 * 
 * @author zhangfeng
 * @date 2018年8月23日
 */
public class Date2AllDayStrategy implements IQueryStrategy {

	@Override
	public String getQueryWhere(String fitem_code, String value) {
		
		Date date = DateUtils.string2Date(value, "yyyy-MM-dd");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		String start = DateUtils.date2String(calendar.getTime());
		
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String end = DateUtils.date2String(calendar.getTime());
		
		StringBuilder sb = new StringBuilder();
		sb.append(fitem_code);
		sb.append(">='");
		sb.append(start);
		sb.append("' and ");
		sb.append(fitem_code);
		sb.append("<'");
		sb.append(end);
		sb.append("'");
		return sb.toString();
	}

}
