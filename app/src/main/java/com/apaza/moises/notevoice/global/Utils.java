package com.apaza.moises.notevoice.global;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.apaza.moises.notevoice.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    //NOTIFICATIONS
    public static final String SEE_ACTION = "SEE_ACTION";
    public static final String YES_ACTION = "YES_ACTION";
    public static final String NO_ACTION = "NO_ACTION";

    public static final int NOTIFICATION_REQUEST_CODE = 12345;
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
        Calendar c = Calendar.getInstance();
        return c.getTime();
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
    /*long diff = date1.getTime() - date2.getTime();
    long seconds = diff / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long days = hours / 24;*/
    public static long getDifferenceDays(Date currentDate, Date date){
        return (currentDate.getTime() - date.getTime())/(1000*60*60*24);
    }

    public static long getDifferenceMin(Date date2){
        try{
            long diff = date2.getTime() - getCurrentDate().getTime();
            Log.d("MIN DIFF ", "> "+diff);
            return diff/(1000*60);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
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

    public static ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

    public static Drawable getTextDrawable(String text){
        int color = colorGenerator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(text.substring(0,1), color, 5);
        return drawable;
    }

    public static String generateCodeUnique(String text){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
        String date = dateFormat.format(new Date());
        return text + "-" + date;
    }
}
