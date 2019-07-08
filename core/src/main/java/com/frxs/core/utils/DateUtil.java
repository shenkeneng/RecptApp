package com.frxs.core.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ewu on 2016/5/13.
 */
public class DateUtil {

    public final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * getCurrentDateString
     * <p>
     * Description: Get the data string based on the given pattern
     * <p>
     * @date 2014-11-10
     * @author ewu
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern)
    {
        if (null == date || TextUtils.isEmpty(pattern))
        {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static Date string2Date(String dateStr, String formatStr) {
        DateFormat sdf = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static int getGapDays(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public static String getTimeString(String endTime, String expendTime, int timeType){
        //传入字串类型 end:2016/06/28 08:30 expend: 03:25
        long longEnd = getTimeMillis(endTime);
//        String[] expendTimes = expendTime.split(":");   //截取出小时数和分钟数
//        long longExpend = Long.parseLong(expendTimes[0]) * 60 * 60 * 1000 + Long.parseLong(expendTimes[1]) * 60 * 1000;
        long longExpend = Long.parseLong(expendTime)*24 * 60 * 60 * 1000;
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd");//yyyy/MM/dd HH:mm
        if (timeType == 0) {
            return sdfTime.format(new Date(longEnd + longExpend));
        } else {
            return sdfTime.format(new Date(longEnd - longExpend));
        }
    }

    public static long getTimeMillis(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //yyyy/MM/dd HH:mm
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }

    public static Date addDateYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    public static Date addDateMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    public static Date addDateDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        cal.add(Calendar.DAY_OF_YEAR, days > 0 ? -1 : 1);
        return cal.getTime();
    }

    public static Date addDateHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }


    public static Calendar Date2Calendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Date Calendar2Date(Calendar cal) {
        Date date = cal.getTime();
        return date;
    }

    public static boolean isBetweenDate(Date objDate, Date startDate,
                                        Date endDate) {
        long obj = Long.valueOf(formatter.format(objDate));
        long obj1 = Long.valueOf(formatter.format(startDate));
        long obj2 = Long.valueOf(formatter.format(endDate));
        return obj >= obj1 && obj <= obj2;
    }

}
