package com.fy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	public static long getTime(String dateStr, String dateFormat) {
		long time = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			Date date = format.parse(dateStr);
			time = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
}
