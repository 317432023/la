package com.jeetx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

public class DateTimeTool {	
	
	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * 
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

	/**
	 * String类型按格式转化成Date类型
	 * @version 2011-8-31 上午10:54:47
	 * @param pattern
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public static Date dateFormat(String pattern, String date) throws ParseException {
		if(StringUtils.isBlank(date)){
			return null;
		}
		
		if (pattern == null || pattern.equals("")) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
		} else {
			return new SimpleDateFormat(pattern).parse(date);
		}
	}

	/**
	 * Date类型按格式转化成String类型
	 * @version 2011-8-31 上午10:54:47
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String dateFormat(String pattern, Date date) {
		if(date == null)
			return "";
		if (pattern == null || pattern.equals("")) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		} else {
			return new SimpleDateFormat(pattern).format(date);
		}
	}
	
	/**
	 * Date类型按格式转化成String类型
	 * @version 2011-8-31 上午10:54:47
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String timeFormatDate(String pattern, Date date) {
		return DateTimeTool.dateFormat("yyyy-MM-dd", date);
	}
	/**
	 * 获取本月的第一天
	 * @version 2011-9-22 下午05:43:48
	 * @return
	 */
	public static String getDateByMonthFirst() { 
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar calendar = Calendar.getInstance();      
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));  
	    return format.format(calendar.getTime());     
	} 
	
	/**
	 * 获取本月的最后一天
	 * @version 2011-9-22 下午05:43:48
	 * @return
	 */
	public static String getDateByMonth () { 
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar calendar = Calendar.getInstance();      
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  
	    return format.format(calendar.getTime());     
	} 
	
	/**
	 * 获取当前日期
	 * @version 2011-9-22 下午05:43:48
	 * @return
	 */
	public static String getDateByToday() { 
		return DateTimeTool.dateFormat("yyyy-MM-dd", new Date());  
	} 
	
	
	public static String queryStartDate(String date) { 
		StringBuffer sb = new StringBuffer();
		return sb.append(date).append(" ").append("00:00:00").toString();
	}
	
	public static String queryStartDate() { 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		StringBuffer sb = new StringBuffer();
		if(hour<=2){
			calendar.set(calendar.DAY_OF_MONTH, day-1);
			//sb.append(DateTimeTool.dateFormat("yyyy-MM-dd", calendar.getTime())).append(" ").append("10:00:00").toString();
		}
		return sb.append(DateTimeTool.dateFormat("yyyy-MM-dd", calendar.getTime())).append(" ").append("10:00:00").toString();
	}
	
	public static String queryEndDate() { 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		StringBuffer sb = new StringBuffer();
		if(hour>=10){
			calendar.set(calendar.DAY_OF_MONTH, day+1);
		}
		return sb.append(DateTimeTool.dateFormat("yyyy-MM-dd", calendar.getTime())).append(" ").append("02:00:00").toString();
	} 
	
	public static String queryMonth(String date) { 
		StringBuffer sb = new StringBuffer();
		return sb.append(date).append("-01 00:00:00").toString();
	} 
	
	public static String queryEndDate(String date) { 
		StringBuffer sb = new StringBuffer();
		return sb.append(date).append(" ").append("23:59:59").toString();
	} 
	
	public static String DateParserT(String pattern,long l) {
	    Date date = new Date(l);
	    if (pattern == null || pattern.equals("")) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		} else {
			return new SimpleDateFormat(pattern).format(date);
		}
	}
    
	/**
	 * 获取时间的前后月份时间
	 * @version 2012-3-16 上午11:47:00
	 * @param num
	 * @return
	 */
    public static Date getDateByDate2Months(Integer months,Date date) {
    	Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
    	Date previousdate=cal.getTime();
    	return previousdate;
	}

    /**
     * 是否在时间段内
     * @param status
     * @param end
     * @return
     */
    public static boolean timecheck(Integer begin,Integer end) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(begin<=end){
			if(hour>=begin && hour<end){
				return true;
			}
		}else{
			if(hour>=end && hour<begin){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
    
    public static boolean secondCheck(Integer status,Integer end){
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int second = calendar.get(Calendar.SECOND);
		if(second>=status && second<end){
			return true;
		}
		return false;
    }
    
    public static Date getNextMinute(){
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int minute = calendar.get(Calendar.MINUTE)+1;
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
    }
	
	/**
	 * 获取时间的前后几天时间
	 * @version 2012-3-16 上午11:47:13
	 * @param n
	 * @param date
	 * @return
	 */
    public static Date getDaysByDate2Days(Integer days, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - days);
		return cal.getTime();
	}
    
    public static Date getDaysByDate2Minute(Integer minute, Date date){
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+minute);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
    }
    
    /**
     * 判断是否在几点之前
     * @return
     * @throws ParseException 
     */
    public static Boolean checkHourBefore(Integer hour) throws ParseException{
    	Boolean flag = false;
    	
    	Date now = DateTimeTool.dateFormat("HH",DateTimeTool.dateFormat("HH", new Date()));
    	Date fixd = DateTimeTool.dateFormat("HH", String.valueOf(hour));
    	if(now.before(fixd)){
    		flag = true;
    	}
    	return flag;
    }
    
    public static  long fromDateStringToLong(String inVal){   
        Date date = null;   
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd hh:ss");   
        try{   
            date = inputFormat.parse(inVal);   
        }catch(Exception e){   
            e.printStackTrace();   
        }
        return date.getTime();   
    }   
    
	/**
	 * 获取时间的前后月份的最后一天
	 * @version 2011-9-22 下午05:43:48
	 * @return
	 */
	public static Date getMonthMaxDay(Integer months,Date date) { 
	    Calendar calendar = Calendar.getInstance();   
	    calendar.setTime(date);
	    calendar.add(Calendar.MONTH, months);
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  
	    return calendar.getTime();     
	} 
	
	/**
	 * 获取时间的前后月份的第一天
	 * @version 2011-9-22 下午05:43:48
	 * @return
	 */
	public static Date getMonthMinDay(Integer months,Date date) { 
	    Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(date);
	    calendar.add(Calendar.MONTH, months);
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));  
	    return calendar.getTime();     
	} 
	
	/**
	 * 时间相加
	 * @param date 当前时间
	 * @param second 增加的秒数
	 * @param bool 结果是否清零秒数
	 * @return
	 */
	public static Date addTime(Date date,Integer second) { 
	    Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(date);
	    calendar.add(Calendar.SECOND, second);
	    return calendar.getTime();     
	} 
	
	public static boolean range(Date beginDate, Date endDate) {
        Date now = new Date();
        if(beginDate.getTime() < now.getTime() && now.getTime() < endDate.getTime()){
        	return true;
        }
        return false;
     }
	
	public static String dateFormat(String date) {
		return date.substring(0, 4).concat("-").concat(date.substring(4, 6)).concat("-").concat(date.substring(6, 8));
	}
	
	/**
	 * 判断某一时间是否在一个区间内
	 * 
	 * @param sourceTime
	 *            时间区间,半闭合,如[10:00-20:00)
	 * @param curTime
	 *            需要判断的时间 如10:00
	 * @return 
	 * @throws IllegalArgumentException
	 */
	public static boolean isInTime(String sourceTime, String curTime) {
	    if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }
	    if (curTime == null || !curTime.contains(":")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
	    }
	    String[] args = sourceTime.split("-");
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	    try {
	        long now = sdf.parse(curTime).getTime();
	        long start = sdf.parse(args[0]).getTime();
	        long end = sdf.parse(args[1]).getTime();
	        if (args[1].equals("00:00")) {
	            args[1] = "24:00";
	        }
	        if (end < start) {
	            if (now >= end && now < start) {
	                return false;
	            } else {
	                return true;
	            }
	        } 
	        else {
	            if (now >= start && now < end) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	    } catch (ParseException e) {
	        e.printStackTrace();
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }

	}
	
	/**
	 * 判断某一时间是否在一个区间内
	 * 
	 * @param sourceTime
	 *            时间区间,半闭合,如[10:00-20:00)
	 * @param curTime
	 *            需要判断的时间 如10:00
	 * @return 
	 * @throws IllegalArgumentException
	 */
	public static boolean isInTime(String sourceTime, String curTime,String dateFormat) {
	    if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }
	    if (curTime == null || !curTime.contains(":")) {
	        throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
	    }
	    String[] args = sourceTime.split("-");
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    try {
	        long now = sdf.parse(curTime).getTime();
	        long start = sdf.parse(args[0]).getTime();
	        long end = sdf.parse(args[1]).getTime();
	        if (args[1].equals("00:00")) {
	            args[1] = "24:00";
	        }
	        if (args[1].equals("00:00:00")) {
	            args[1] = "24:00:00";
	        }
	        if (end < start) {
	            //if (now >= end && now < start) {
	            if (now > end && now <= start) {
	                return false;
	            } else {
	                return true;
	            }
	        } 
	        else {
	            //if (now >= start && now < end) {
	            if (now > start && now <= end) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	    } catch (ParseException e) {
	        e.printStackTrace();
	        throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
	    }

	}
	
	/**
	 * 获得所在周的星期一
	 * @return
	 */
	public static Date getMonday() { 
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(new Date());  
        
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
        if(1 == dayWeek) {  
          cal.add(Calendar.DAY_OF_MONTH, -1);  
        }  
        
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
        //String imptimeBegin = sdf.format(cal.getTime());  
        //System.out.println("所在周星期一的日期："+imptimeBegin);  
		return cal.getTime();
	}
	
    public static void main(String[] args) throws ParseException {
		//System.out.println("获取时间的前后几天时间："+DateTimeTool.dateFormat(null, getDaysByDate2Days(1,new Date())));
//		System.out.println("获取时间的前后几分钟时间："+DateTimeTool.dateFormat(null, getDaysByDate2Minute(10,new Date())));
		
		//System.out.println("获取时间的前后月份时间："+DateTimeTool.dateFormat(null, getDateByDate2Months(-1,new Date())));
		//System.out.println(DateTimeTool.dateFormat(null, queryMonth("2012-02")));
		//System.out.println(DateTimeTool.dateFormat(null, getDaysByDate2Days(-1,new Date())));
		//System.out.println(DateTimeTool.checkHourBefore(5));
//    	 long   startT=DateTimeTool.fromDateStringToLong("2005-03-03   16:00:00");   
//         long   endT=DateTimeTool.fromDateStringToLong("2005-02-03   16:00:00");   
//         long   mint=(startT-endT)/(1000);   
//         int   hor=(int)mint/3600;   
//         int   secd=(int)mint%60;   
//         int   day=(int)hor/24;   
//         
//         int   secd1=(int)mint%60; 
//         System.out.println(mint);
//         System.out.println("共"+day+"天   准确时间是：小时="+hor+"   分钟"+secd);   
//         System.out.println("相差"+secd1+"分钟");
//         System.out.println(DateTimeTool.dateFormat(null, new Date()).substring(0, 10));
    	//System.out.println(DateTimeTool.timecheck(0,6));
    	
    	
    	//System.out.println(DateTimeTool.dateFormat("yyyy-MM-dd",DateTimeTool.getDaysByDate2Days(1,new Date())));
    	//System.out.println(DateTimeTool.dateFormat("yyyy-MM-dd",DateTimeTool.getDaysByDate2Days(1,new Date())));
    	//System.out.println(DateTimeTool.dateFormat(null, "2005-03-03 16:00:02").getTime()-DateTimeTool.dateFormat(null, "2005-03-03 16:00:00").getTime());
//    	System.out.println(DateTimeTool.addTime(new Date(), -300));
    	
//    	Date beginDate = DateTimeTool.dateFormat(null, "2005-03-03 16:00:02");
//    	Date endDate = DateTimeTool.dateFormat(null, "2015-12-03 16:00:02");
    	
    	//System.out.println(DateTimeTool.getNextMinute());
//    	System.out.println(DateTimeTool.getMonday());
    	
    	// 定义输出日期格式
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEE");
//		
//		Date currentDate = new Date();
//		
//		// 比如今天是2012-12-25
//		List<Date> days = dateToWeek(currentDate);
//		System.out.println("今天的日期: " + sdf.format(currentDate));
//		for (Date date : days) {
//			System.out.println(sdf.format(date));
//		}
    	
    	//DateTimeTool.convertWeekByDate(new Date());
//    	Date date = new Date();
//    	System.out.println(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", date));
//    	System.out.println(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", DateTimeTool.addTime(date, -5)));
//    	System.out.println(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", DateTimeTool.addTime(date, 5)));
//    	if(DateTimeTool.timecheck(1,9)||DateTimeTool.timecheck(10,23)){
//    		System.out.println("true");
//    	}else{
//    		System.out.println("false");
//    	}
//    	Date date = new Date(1478750256*1000l);
//    	System.out.println(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", date));
    	//System.out.println(DateTimeTool.dateFormat("20131230"));
		
//		String dateFormat = "HH:mm:ss";
//		String curTime = DateTimeTool.dateFormat(dateFormat, new Date());
//		if(DateTimeTool.isInTime("21:00:00-21:50:00", curTime,dateFormat)){
//			System.out.println("1");
//		}else{
//			System.out.println("0");
//		}
		
		Date nowTime = new Date();
		Date startTime = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", "2019-10-01 00:00:00");
		Date endTime = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", "2019-10-07 23:59:59");
		if(DateTimeTool.isEffectiveDate(nowTime, startTime, endTime)) {
			System.out.println("1");
		}else {
			System.out.println("0");
		}
    }
}
