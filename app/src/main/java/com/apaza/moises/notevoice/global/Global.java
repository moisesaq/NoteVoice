package com.apaza.moises.notevoice.global;

import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.apaza.moises.notevoice.model.HandlerDB;
import com.apaza.moises.notevoice.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Global {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static MainActivity context;

    public static void setContext(MainActivity activity){
        context = activity;
    }

    public static MainActivity getContext(){
        return context;
    }

    public static HandlerDB getHandlerDB(){
        return HandlerDB.getInstance(context);
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

    public static void showMessage(String message){
        Snackbar.make(getContext().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public static String getCurrentDateString(){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return formatter.format(new Date());
    }
}
