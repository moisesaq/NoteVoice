package com.apaza.moises.notevoice.global;

import com.apaza.moises.notevoice.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by moises on 16/06/16.
 */
public class Utils {
    //DATE FORMAT
    //public static final String DATE_FORMAT_INPUT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_INPUT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_INPUT_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAY = "EEEE";
    public static final String DATE_FORMAT_CLASSIC = "dd/MM/yyyy";
    public static final String DATE_FORMAT_TIME = "HH:mm:ss";
    public static final String DATE_FORMAT_TIME_12 = "hh:mm a";

    public static String getTimeCustom(Date date){
        String result;
        SimpleDateFormat formatter;

        long difference = getDifferenceDays(getCurrentDate(), date);

        if (difference == 0){
            if(getDifferenceDaysDate(date) == 0){
                formatter = new SimpleDateFormat(DATE_FORMAT_TIME_12, Locale.getDefault());
                return formatter.format(date);
            }else{
                return Global.getContext().getString(R.string.yesterday);
            }
        }else if(difference == 1){
            return Global.getContext().getString(R.string.yesterday);
        }else if(difference <= 7 && difference > 1){
            formatter = new SimpleDateFormat(DATE_FORMAT_DAY, Locale.getDefault());
            result = formatter.format(date);
            char first = result.charAt(0);
            return Character.toUpperCase(first)+result.substring(1, result.length());
        } else {
            formatter = new SimpleDateFormat(DATE_FORMAT_CLASSIC, Locale.getDefault());
            return formatter.format(date);
        }
    }

    public static Date getCurrentDate(){
        //Calendar c = Calendar.getInstance();
        return new Date();//c.getTime();
    }

    public static int getDifferenceDaysDate(Date date){
        Calendar ca1 = Calendar.getInstance();
        ca1.setTime(date);
        int day1 = ca1.get(Calendar.DAY_OF_MONTH);

        Calendar ca2 = Calendar.getInstance();
        ca2.setTime(getCurrentDate());
        int day2 = ca2.get(Calendar.DAY_OF_MONTH);

        return day2 - day1;
    }

    public static long getDifferenceDays(Date currentDate, Date date){
        return (currentDate.getTime() - date.getTime())/(1000*60*60*24);
    }

    public static String getCurrentDateString(){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_INPUT_FULL, Locale.getDefault());
        return formatter.format(new Date());
    }

    public static Date parseToDate(String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_INPUT_FULL, Locale.getDefault());
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
