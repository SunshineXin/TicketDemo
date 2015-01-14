package com.luckysun.ticketdemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.format.Time;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
	
	/**
	 * 返回指定格式的日期字符串
	 */
	public static String getCurrentTime(String parten, long milliTime) {
		SimpleDateFormat df = new SimpleDateFormat(parten, Locale.CHINA);// 设置日期格式
		return df.format(new Date(milliTime));
	}
	
	/**
	 * 返回指定格式的日期字符串
	 */
	public static String getCurrentTime(String parten, String strDate) {
		Date date = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式
			date = df.parse(strDate);
		}catch(ParseException e) {
		}
		
		if(date != null)
			return getCurrentTime(parten, date.getTime());
		
		return null;
	}
	
	/**
	 * 获取当前时间
	 * 格式为2013-10-08
	 */
	public static String getCurrentTime(){
        Time t=new Time();
        t.setToNow();
        StringBuffer time = new StringBuffer();
        time.append(String.valueOf(t.year));
        time.append("-");
        time.append(String.valueOf(t.month));
        time.append("-");
        time.append(String.valueOf(t.monthDay));
        return time.toString();
    }
	
	
	/**
	 * 获取详细信息
	 * 格式为 yyyy-MM-dd
	 */
	public static String getCurrentDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);// 设置日期格式
		return df.format(new Date());
	}
	/**
	 * 获取详细信息
	 * 格式为 yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentDetailTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式
		return df.format(new Date());
	}
	/**
	 * 获取详细信息
	 * 格式为 yyyy-MM-dd HH:mm:ss
	 */
	public static Long getCurrentDetailLongTime() {
		Date data = new Date();
		return data.getTime();
	}
	
	/**
	 * 判断本地保存的时间是为今天
	 * @param timetamp
	 * @return
	 */
	public static boolean isNewDay(String timetamp){
		try {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
			SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			Date date = format1.parse(timetamp);
			String formatTime = format3.format(date);
			if(!getCurrentDateTime().equals(formatTime)){
				return true;
			}else{
				return false;
			}
		} catch (ParseException e) {
			return false;				
		}
}
	
	
	/**
	 * 判断本地TimeTamp是否小于服务器TimeTamp
	 * @param timetamp
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isOldTimeTamp(String timetamp, String newtimetamp){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date newdate = sdf.parse(newtimetamp);
				Date d = sdf.parse(timetamp);
				if(newdate.getTime() > d.getTime()){
					return true;
				}else{
					return false;
				}
			} catch (ParseException e) {
				return false;				
			}
	}
	
	/**
	 * 判断时间段（早上、下午、晚上）
	 * @param time
	 * @return
	 */
	public static String formateUpdateDatabaseDate(String time) {
		if (TextUtils.isEmpty(time)) {
			return "未知时间";
		}
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		SimpleDateFormat format3 = new SimpleDateFormat("M月d日 ", Locale.CHINA);

		String formatTime = "";
		Date date = null;
		try {
			date = format1.parse(time);
			formatTime = format3.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return formatTime;
	}
	/**
	 * 判断时间段（早上、下午、晚上）
	 * 
	 * @param time
	 * @return
	 */
	public static String formateUpdatePriceDate(String time) {
		if(TextUtils.isEmpty(time)) {
			return "未知时间";
		}
		String[][] QuantumArray = new String[][] { {"00:00", "12:00"}, {"12:00", "18:00"}, {"18:00", "00:00"}};
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
		SimpleDateFormat format3 = new SimpleDateFormat("M月d日 ", Locale.CHINA);
		
		String formatTime = "";
		Date date = null;
		try {
			date = format1.parse(time);
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			long todayZeroTime = c.getTimeInMillis();//当日凌晨时间
			c.add(Calendar.DATE, 1);
			long nextZeroTime = c.getTimeInMillis();//次日凌晨时间
			
			String quantum = "";
			if(isShift(date.getTime(), QuantumArray[0])) {
				quantum = "早上";
			}else if(isShift(date.getTime(), QuantumArray[1])) {
				quantum = "下午";
			}else if(isShift(date.getTime(), QuantumArray[2])) {
				quantum = "晚上";
			}
			
			if(date.getTime() < todayZeroTime || date.getTime() >= nextZeroTime) {//非当日
//				formatTime = format3.format(date) + quantum;
//				formatTime += format2.format(date);
				formatTime = format3.format(date);
			}else {
				formatTime = quantum + format2.format(date);
				
			}
		}catch(ParseException e) {
		}
		
		return formatTime;
	}
	
	/**
	 * 验证某一时间是否在某一时间段
	 * 
	 * @param currTime 某一时间	时间毫秒数
	 * @param timeSlot 某一时间段 	{ "07:00:00", "09:00:00" };
	 * @return true/false
	 */
	public static boolean isShift(final long currTime, String[] timeSlot) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(currTime);
		String[] tmpArray = timeSlot[0].split(":");
		long startTime, stopTime;
		tempCalendar.clear(Calendar.HOUR_OF_DAY);
		tempCalendar.clear(Calendar.MINUTE);
		tempCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tmpArray[0]));
		tempCalendar.set(Calendar.MINUTE, Integer.parseInt(tmpArray[1]));
		startTime = tempCalendar.getTimeInMillis();
		tmpArray = timeSlot[1].split(":");
		int stopHour = Integer.parseInt(tmpArray[0]), stopMinute = Integer.parseInt(tmpArray[1]);
		if(stopHour == 0) {
			tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		tempCalendar.clear(Calendar.HOUR_OF_DAY);
		tempCalendar.clear(Calendar.MINUTE);
		tempCalendar.set(Calendar.HOUR_OF_DAY, stopHour);
		tempCalendar.set(Calendar.MINUTE, stopMinute);
		stopTime = tempCalendar.getTimeInMillis();
		return ((startTime <= currTime && currTime < stopTime) ? true : false);
	}
	
	

	@SuppressLint("SimpleDateFormat")
	public static String simpleDateFormat(Date date){
		if(date != null){
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		     return format.format(date);
		}else{
			return "Unknown time";
		}
		
	}
	
	
	
	/**
	 * 将长时间格式字符串转换为时间 yyyy/MM/dd
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSimpleStringDateFromLong(Long date) {
		if(date == null){
			return"暂无时间数据";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String dateString = formatter.format(date);
		return dateString;
	}
	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSimpleStringDate2FromLong(Long date) {
		if(date == null){
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}
	/**
	 * 将长时间格式字符串转换为时间 yyyy年MM月dd日
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSimpleStringDate3FromLong(Long date) {
		if(date == null){
			return "";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * 将yyyy-MM-dd格式转换为yyyy年MM月dd日
	 */
	public static String formatStringDate(String date1){
		if(date1 == null){
			return "";
		}
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy年MM月dd日");
		String dateString = "";
		try {
			dateString = formatter2.format(formatter1.parse(date1));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return dateString;
	}
	
	
	public static String getStringDateFromLong(Long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * 将长时间格式字符串转换为时间 HH:mm
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStringMinuteTime(Long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String dateString = formatter.format(date);
		
		return dateString;
	}
	/**
	 * 将长时间格式字符串转换为时间 HH:mm:ss
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStringSecondTime(Long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateString = formatter.format(date);
		
		return dateString;
	}

	
	/**
	 * 获取两个日期的时间差
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getLongDateFomrString(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long interval = 0;
		try {
			Date startTime = dateFormat.parse(time);
			interval = (long) (startTime.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return interval;
	}
	/**
	 * 获取两个日期的时间差
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getLongDateFomrString(String time,String fromat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(fromat);
		long interval = 0;
		try {
			Date startTime = dateFormat.parse(time);
			interval = (long) (startTime.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return interval;
	}

}
