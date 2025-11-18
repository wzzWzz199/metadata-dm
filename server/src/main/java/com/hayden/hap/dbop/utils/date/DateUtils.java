package com.hayden.hap.dbop.utils.date;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.dbop.exception.HDRuntimeException;
import com.hayden.hap.common.formmgr.inputconfig.DateInputConfigVO;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间utils类
 * @author zhangfeng
 * @date 2016年8月29日
 */
public class DateUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

	/**
	 * 将时间段转换成xx天xx小时xx分xx秒格式
	 * @param begin
	 * @param end
	 * @param isShowMillisecond 是否需要展示毫秒
	 * @return 
	 * @author zhangfeng
	 * @date 2016年8月29日
	 */
	public static String timePeriod2Str(Date begin,Date end,boolean isShowMillisecond) {
		
		long between=(end.getTime()-begin.getTime())/1000;
		//除以1000是为了转换成秒   
		long day=between/(24*3600);   
		long hour=between%(24*3600)/3600;   
		long minute=between%3600/60;   
		long second=between%60;   
		
		StringBuilder sb = new StringBuilder();
		if(day>0) {
			sb.append(day);
			sb.append("天");
		}
		
		if(hour>0 || sb.length()>0) {
			sb.append(hour);
			sb.append("小时");
		}
		
		if(minute>0 || sb.length()>0) {
			sb.append(minute);
			sb.append("分");
		}
		
		if(second>0 || sb.length()>0 || !isShowMillisecond) {
			sb.append(second);
			sb.append("秒");
		}
		
		if(isShowMillisecond) {
			long millisecond = (end.getTime()-begin.getTime())%1000;
			sb.append(millisecond);
			sb.append("毫秒");
		}
		
		return sb.toString();
	}
	
	public static String longTime2Str(Long timeBetween) {
		Long between = timeBetween/1000;
		//除以1000是为了转换成秒   
		long day=between/(24*3600);   
		long hour=between%(24*3600)/3600;   
		long minute=between%3600/60;   
		long second=between%60;   
		
		StringBuilder sb = new StringBuilder();
		if(day>0) {
			sb.append(day);
			sb.append("天");
		}
		
		if(hour>0 || sb.length()>0) {
			sb.append(hour);
			sb.append("小时");
		}
		
		if(minute>0 || sb.length()>0) {
			sb.append(minute);
			sb.append("分");
		}
		
		if(second>0 || sb.length()>0) {
			sb.append(second);
			sb.append("秒");
		}
		
		long millisecond = timeBetween%1000;
		if(millisecond>0 || sb.length()>0) {
			sb.append(millisecond);
			sb.append("毫秒");
		}
		
		return sb.toString();
	}
	   /**
     * 将日期格式化为指定的字符串 add by gzh
     * @param d 日期
     * @param format    输出字符串格式
     * @return 日期字符串
     */
    public static String getStringFromDate(Date d, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }
    
    /**
     * 日期转字符串（yyyy-MM-dd HH:mm:ss）
     * @param date
     * @return 
     * @author zhangfeng
     * @date 2017年9月26日
     */
    public static String date2String(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    /**
     * 字符串转日期(yyyy-MM-dd HH:mm:ss)
     * @param dateStr
     * @return 
     * @author zhangfeng
     * @date 2017年9月26日
     */
    public static Date string2Date(String dateStr) {
    	try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
    }
    
    /**
     * 字符串转日期
     * @param dateStr
     * @return 
     * @author zhangfeng
     * @throws HDException 
     * @date 2018年8月23日
     */
    public static Date string2Date(String dateStr, String pattern) {
    	try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, pattern);
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
			throw new HDRuntimeException(e);
		}
    }
    
    public static String getDateStr(Date date, DateInputConfigVO inputConfigVO) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		if(inputConfigVO==null) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}else if(StringUtils.isEmpty(inputConfigVO.getDatetype())) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}else {
			pattern = getDateTypePattern(inputConfigVO.getDatetype());			
		}		
		
		return DateFormatUtils.format(date, pattern);
	}
	
	public static String getDateByStr(String date, DateInputConfigVO inputConfigVO) throws ParseException {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		if(inputConfigVO==null) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}else if(StringUtils.isEmpty(inputConfigVO.getDatetype())) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}else {
			pattern = getDateTypePattern(inputConfigVO.getDatetype());			
		}	
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date dateValue = simpleDateFormat.parse(date);
		return DateFormatUtils.format(dateValue, pattern);
	}
	
	private static String getDateTypePattern(String dateType) {
		dateType = dateType.toUpperCase();
		char firstChar = changeChar(dateType.charAt(0));
		char endChar = changeChar(dateType.charAt(1));
		
		String pattern = "yyyy-MM-dd HH:mm:ss";
		int firstPosition = pattern.indexOf(firstChar);
		int endPosition = pattern.lastIndexOf(endChar);
		
		return pattern.substring(firstPosition, endPosition+1);
	}
	
	private static char changeChar(char s) {
		if('Y'==s || 'D'==s || 'S'==s)
			return Character.toLowerCase(s);
		if('N'==s)
			return 'm';
		return s;
	}
	
	/**
	 * @Description: 获取该日期的间隔日期，days等于-1时返回昨天日期
	 * @author: wangyi
	 * @date: 2018年8月20日
	 */
	public static String getCertainDate(Date date, int days){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
	    calendar.add(Calendar.DAY_OF_YEAR, days);
	    return getStringFromDate(calendar.getTime(), "yyyy-MM-dd");
	} 
	
	/**
	 * @Description: mysql中 Date日期类型取出时进行格式转换，清除00:00:00形式的时分秒
	 * @author: liuyd
	 * @date: 2019年4月30日
	 */
	public static Date getOnlyYD(Date dateTime) throws HDException{
		if(dateTime==null) {
			throw new HDException("时间不能为空");
		}
		LocalDate localDate = dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Date newDate = java.sql.Date.valueOf(localDate);
	    return newDate;
	} 
	
}
