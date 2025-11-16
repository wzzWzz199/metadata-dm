package com.hayden.hap.common.utils.variable.impl;

import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.db.util.ObjectUtil;
import com.hayden.hap.common.utils.date.DateUtils;
import com.hayden.hap.common.utils.variable.impl.date.*;
import com.hayden.hap.common.utils.variable.itf.IVarFunction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日期变量函数<br/>
 * 格式：f:date(type,format)<br/>
 * 实例：昨天 @f:date('yesterday','yyyy-MM-dd HH:mm:ss')@<br/>
 * type参数:必填
 * 昨天(yesterday,可以简写为小写y,下同);<br/>
 * 现在(now,可以简写为n);<br/>
 * 明天(tomorrow,可以简写为t);<br/>
 * 上个月一号(last_month,简写lm);<br/>
 * 上个月今天(last_month_now,简写lmn);<br/>
 * 这个月的一号(this_month,简写tm);<br/>
 * 下个月一号(next_month,简写nm);<br/>
 * 下个月今天(next_month_now,简写nmn);<br/>
 * 今年一月一号(this_year,简写ty);<br/>
 * 去年一月一号(last_year,简写ly);<br/>
 * 去年的今天(last_year_now,简写lyn);<br/>
 * 明年一月一号(next_year,ny);<br/>
 * 明年的今天(next_year_now,nyn);<br/>
 * 
 * formart：日期格式， 非必填， 默认yyyy-MM-dd HH:mm:ss
 * 
 * @author zhangfeng
 * @date 2018年7月9日
 */
public class DateVarFunction implements IVarFunction {
	
	private static final Map<String, Class<? extends IDateProvider>> map = new HashMap<>();
	
	static {
		map.put("yesterday", YesterdayProvider.class);
		map.put("y", YesterdayProvider.class);
		
		map.put("now", NowProvider.class);
		map.put("n", NowProvider.class);
		
		map.put("tomorrow", TomorrowProvider.class);
		map.put("t", TomorrowProvider.class);
		
		map.put("last_month", LastMonthProvider.class);
		map.put("lm", LastMonthProvider.class);
		
		map.put("last_month_now", LastMonthNowProvider.class);
		map.put("lmn", LastMonthProvider.class);
		
		map.put("this_month", ThisMonthPorvider.class);
		map.put("tm", ThisMonthPorvider.class);
		
		map.put("next_month", NextMonthProvider.class);
		map.put("nm", NextMonthProvider.class);
		
		map.put("next_month_now", NextMonthNowProvider.class);
		map.put("nmn", NextMonthNowProvider.class);
		
		map.put("this_year", ThisYearProvider.class);
		map.put("ty", ThisYearProvider.class);
		
		map.put("last_year", LastYearProvider.class);
		map.put("ly", LastYearProvider.class);
		
		map.put("last_year_now", LastYearNowProvider.class);
		map.put("lyn", LastYearNowProvider.class);
		
		map.put("next_year", NextYearProvider.class);
		map.put("ny", NextYearProvider.class);
		
		
		map.put("next_year_now", NextYearNowProvider.class);
		map.put("nyn", NextYearNowProvider.class);
		

		map.put("this_year_lastday", ThisYearLastDayProvider.class);
		map.put("tyld", ThisYearLastDayProvider.class);
		
	}
	
	@Override
	public String supportFunction() {
		return "date";
	}

	@Override
	public Date getValue(List<String> params) throws HDException {
		if(ObjectUtil.isEmpty(params))
			throw new HDException("日期的系统变量函数拼写有误");
		
		Class<? extends IDateProvider> clazz = map.get(params.get(0));
		if(clazz==null)
			throw new HDException("不支持的日期系统变量函数参数类型："+params.get(0));
		
		IDateProvider provider;
		try {
			provider = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new HDException(e);
		}
		Date date = provider.getDate();		
		
		return date;
	}

	@Override
	public String getStringValue(List<String> params) throws HDException {
		Date date = getValue(params);
		
		String pattern = "yyyy-MM-dd HH:mm:ss";
		if(params.size()>=2)
			pattern = params.get(1);
		
		return DateUtils.getStringFromDate(date, pattern);
	}

}
