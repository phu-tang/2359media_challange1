package com.s2359media.journeytracker.ulti;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUlti {

	public static long getDateWithoutTime(long time){
		long millisInDay = 60 * 60 * 24 * 1000;
		
		long dateOnly = (time / millisInDay) * millisInDay;
		return  dateOnly;
	}
	
	public static String getFormatDate(long time) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat(CommonConstant.FORMAT_DATE);

		Date today = new Date(time);

		return formatter.format(today);
		
	}
	
	public static String getFormatTime(long time) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat(CommonConstant.FORMAT_TIME);

		Date today = new Date(time);

		return formatter.format(today);
		
	}
}
