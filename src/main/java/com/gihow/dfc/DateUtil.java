// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DateUtil.java

package com.gihow.dfc;

import com.documentum.fc.common.DfLogger;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

// Referenced classes of package com.documentum.web.util:
//			StringUtil

public final class DateUtil
{

	private static SimpleDateFormat sdf;
	private static Boolean s_bLatestOnTop = null;
	private static final String TIME_SORT_ORDER = "application.sorting_order.latest_on_top";
	private static Map s_shortWeekdaysCache = new Hashtable(7);
	private static Map s_shortMonthsCache = new Hashtable(7);
	private static Map s_monthsCache = new Hashtable(7);

	public DateUtil()
	{
	}

	public static Calendar getCalendar()
	{
		return Calendar.getInstance(LocaleService.getTimeZone(), LocaleService.getLocale());
	}

	private static SimpleDateFormat getSimpleDateFormatInstance()
	{
		if (sdf == null)
			sdf = new SimpleDateFormat();
		return sdf;
	}

	public static DateFormat getDateFormat(int dateStyle)
	{
		Locale locale = LocaleService.getLocale();
		String pattern = getDateFormatPattern4DigitYear(dateStyle, locale);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.setTimeZone(LocaleService.getTimeZone());
		return sdf;
	}

	public static DateFormat getDateTimeFormat(int dateStyle, int timeStyle)
	{
		Locale locale = LocaleService.getLocale();
		String pattern = getDateTimeFormatPattern4DigitYear(dateStyle, timeStyle, locale);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.setTimeZone(LocaleService.getTimeZone());
		return sdf;
	}

	public static DateFormat getDateTimeFormatNoTimeZone(int dateStyle, int timeStyle)
	{
		Locale locale = LocaleService.getLocale();
		String pattern = getDateTimeFormatPattern4DigitYear(dateStyle, timeStyle, locale);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		return sdf;
	}

	public static DateFormat getTimeFormat(int timeStyle)
	{
		Locale locale = LocaleService.getLocale();
		String pattern = getTimeFormatPattern(timeStyle, locale);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.setTimeZone(LocaleService.getTimeZone());
		return sdf;
	}

	public static String getDateFormatPattern4DigitYear(int dateFormat, Locale locale)
	{
		Context context = Context.getApplicationContext();
		String pattern = null;
		IConfigLookup lookup = null;
		try
		{
			lookup = ConfigService.getConfigLookup();
		}
		catch (Exception e)
		{
			Trace.println("Can not create lookup. there's no application context.");
		}
		if (lookup != null)
		{
			IConfigElement dateFormatElement = lookup.lookupElement("application.format.date", context);
			if (dateFormatElement != null)
			{
				pattern = dateFormatElement.getValue();
				pattern = validatePattern(pattern);
				if (pattern == null)
					DfLogger.warn(dateFormatElement, "The config item:'application.format.date' is invalid. Date will display in default format.", null, null);
			}
		}
		if (pattern == null)
		{
			pattern = getDateFormatPattern(dateFormat, locale);
			pattern = ensure4DigitYearPattern(pattern);
		}
		return pattern;
	}

	public static String getDateTimeFormatPattern4DigitYear(int dateFormat, int timeFormat, Locale locale)
	{
		Context context = Context.getApplicationContext();
		String pattern = null;
		IConfigLookup lookup = null;
		try
		{
			lookup = ConfigService.getConfigLookup();
		}
		catch (Exception e)
		{
			Trace.println("Can not create lookup. there's no application context.");
		}
		if (lookup != null)
		{
			IConfigElement dateFormatElement = lookup.lookupElement("application.format.datetime", context);
			if (dateFormatElement != null)
			{
				pattern = dateFormatElement.getValue();
				pattern = validatePattern(pattern);
				if (pattern == null)
					DfLogger.warn(dateFormatElement, "The config item:'application.format.datetime' is invalid. Datetime will display in default format.", null, null);
			}
		}
		if (pattern == null)
		{
			pattern = getDateTimeFormatPattern(dateFormat, timeFormat, locale);
			pattern = ensure4DigitYearPattern(pattern);
		}
		return pattern;
	}

	public static String getTimeFormatPattern(int timeStyle, Locale locale)
	{
		Context context = Context.getApplicationContext();
		String pattern = null;
		IConfigLookup lookup = null;
		try
		{
			lookup = ConfigService.getConfigLookup();
		}
		catch (Exception e)
		{
			Trace.println("Can not create lookup. there's no application context.");
		}
		if (lookup != null)
		{
			IConfigElement dateFormatElement = lookup.lookupElement("application.format.time", context);
			if (dateFormatElement != null)
			{
				pattern = dateFormatElement.getValue();
				pattern = validatePattern(pattern);
				if (pattern == null)
					DfLogger.warn(dateFormatElement, "The config item:'application.format.time' is invalid. The time will display in default format.", null, null);
			}
		}
		if (pattern == null)
		{
			Calendar calendar = Calendar.getInstance(locale);
			calendar.set(2003, 3, 5, 18, 7, 9);
			Date date = calendar.getTime();
			DateFormat df = DateFormat.getTimeInstance(timeStyle, locale);
			if (df instanceof SimpleDateFormat)
			{
				SimpleDateFormat sdf = (SimpleDateFormat)df;
				pattern = sdf.toPattern();
			} else
			{
				pattern = df.format(date);
				pattern = convertToTimePattern(pattern, locale, "18", "6", "07", "09");
			}
		}
		return pattern;
	}

	private static String validatePattern(String pattern)
	{
		try
		{
			getSimpleDateFormatInstance().applyPattern(pattern);
		}
		catch (IllegalArgumentException e)
		{
			pattern = null;
		}
		return pattern;
	}

	private static String ensure4DigitYearPattern(String pattern)
	{
		int index = pattern.indexOf('y');
		if (index != -1)
		{
			int len = pattern.length();
			int end;
			for (end = index; end < len && pattern.charAt(end) == 'y'; end++);
			if (end - index != 4)
			{
				StringBuffer sbuf = new StringBuffer((len - (end - index)) + 4);
				sbuf.append(pattern.substring(0, index));
				sbuf.append("yyyy");
				sbuf.append(pattern.substring(end, len));
				pattern = sbuf.toString();
			}
		}
		return pattern;
	}

	public static String getDateFormatPattern(int dateFormat, Locale locale)
	{
		Calendar calendar = Calendar.getInstance(locale);
		calendar.set(2003, 1, 1);
		Date date = calendar.getTime();
		DateFormat df = DateFormat.getDateInstance(dateFormat, locale);
		if (df instanceof SimpleDateFormat)
		{
			SimpleDateFormat sdf = (SimpleDateFormat)df;
			String pattern = sdf.toPattern();
			return pattern;
		} else
		{
			String strDateFormat = df.format(date);
			strDateFormat = convertToDatePattern(strDateFormat, locale, "2003", "02", "01");
			return strDateFormat;
		}
	}

	public static String getDateTimeFormatPattern(int dateFormat, int timeFormat, Locale locale)
	{
		Calendar calendar = Calendar.getInstance(locale);
		calendar.set(2003, 3, 5, 18, 7, 9);
		Date date = calendar.getTime();
		DateFormat df = DateFormat.getDateTimeInstance(dateFormat, timeFormat, locale);
		if (df instanceof SimpleDateFormat)
		{
			SimpleDateFormat sdf = (SimpleDateFormat)df;
			String pattern = sdf.toPattern();
			return pattern;
		} else
		{
			String strDateFormat = df.format(date);
			strDateFormat = convertToDatePattern(strDateFormat, locale, "2003", "04", "05");
			strDateFormat = convertToTimePattern(strDateFormat, locale, "18", "6", "07", "09");
			return strDateFormat;
		}
	}

	private static String convertToDatePattern(String strDate, Locale locale, String strYear, String strMonth, String strDay)
	{
		strDate = StringUtil.replace(strDate, strYear, "yyyy");
		strDate = StringUtil.replace(strDate, strYear.substring(2), "yy");
		strDate = StringUtil.replace(strDate, strMonth, "MM");
		strDate = StringUtil.replace(strDate, strMonth.substring(1), "M");
		strDate = StringUtil.replace(strDate, strDay, "dd");
		strDate = StringUtil.replace(strDate, strDay.substring(1), "d");
		String days[] = getShortWeekdays(locale);
		for (int ii = 0; ii < days.length; ii++)
			strDate = StringUtil.replace(strDate, days[ii], "ddd");

		String months[] = getMonths(locale);
		for (int ii = 0; ii < months.length; ii++)
			strDate = StringUtil.replace(strDate, months[ii], "MMMM");

		months = getShortMonths(locale);
		for (int ii = 0; ii < months.length; ii++)
			strDate = StringUtil.replace(strDate, months[ii], "MMM");

		return strDate;
	}

	private static String convertToTimePattern(String strTime, Locale locale, String strHour24h, String strHour12h, String strMinute, String strSecond)
	{
		strTime = StringUtil.replace(strTime, strHour24h, "HH");
		strTime = StringUtil.replace(strTime, strHour12h, "h");
		strTime = StringUtil.replace(strTime, strMinute, "mm");
		strTime = StringUtil.replace(strTime, strMinute.substring(1), "m");
		strTime = StringUtil.replace(strTime, strSecond, "ss");
		strTime = StringUtil.replace(strTime, strSecond.substring(1), "s");
		String ampm[] = getAmPmStrings(locale);
		for (int i = 0; i < ampm.length; i++)
			strTime = StringUtil.replace(strTime, ampm[i], "a");

		return strTime;
	}

	public static String[] getShortWeekdays(Locale locale)
	{
		String days[] = (String[])(String[])s_shortWeekdaysCache.get(locale);
		if (days == null)
		{
			days = new String[7];
			String tempDays[] = (new DateFormatSymbols(locale)).getShortWeekdays();
			days[0] = tempDays[1];
			days[1] = tempDays[2];
			days[2] = tempDays[3];
			days[3] = tempDays[4];
			days[4] = tempDays[5];
			days[5] = tempDays[6];
			days[6] = tempDays[7];
			s_shortWeekdaysCache.put(locale, days);
		}
		return days;
	}

	public static String[] getShortMonths(Locale locale)
	{
		String months[] = (String[])(String[])s_shortMonthsCache.get(locale);
		if (months == null)
		{
			months = new String[12];
			String tempMonths[] = (new DateFormatSymbols(locale)).getShortMonths();
			for (int ii = 0; ii < 12; ii++)
				months[ii] = tempMonths[ii];

			s_shortMonthsCache.put(locale, months);
		}
		return months;
	}

	public static String[] getMonths(Locale locale)
	{
		String months[] = (String[])(String[])s_monthsCache.get(locale);
		if (months == null)
		{
			months = new String[12];
			String tempMonths[] = (new DateFormatSymbols(locale)).getMonths();
			for (int ii = 0; ii < 12; ii++)
				months[ii] = tempMonths[ii];

			s_monthsCache.put(locale, months);
		}
		return months;
	}

	public static String[] getAmPmStrings(Locale locale)
	{
		String ampm[] = (new DateFormatSymbols(locale)).getAmPmStrings();
		return ampm;
	}

	public static boolean isTimeSortOrderEnabled()
	{
		if (s_bLatestOnTop != null)
			return s_bLatestOnTop.booleanValue();
		IConfigLookup lookup = ConfigService.getConfigLookup();
		s_bLatestOnTop = lookup.lookupBoolean("application.sorting_order.latest_on_top", Context.getApplicationContext());
		if (s_bLatestOnTop == null)
			s_bLatestOnTop = Boolean.FALSE;
		return s_bLatestOnTop.booleanValue();
	}

}
