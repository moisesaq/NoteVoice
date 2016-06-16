package com.apaza.moises.notevoice.global;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.apaza.moises.notevoice.model.HandlerDB;
import com.apaza.moises.notevoice.MainActivity;
import com.apaza.moises.notevoice.model.Media;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Global {
    private static MainActivity context;
    private static Media media;

    public static void setContext(MainActivity activity){
        context = activity;
    }

    public static MainActivity getContext(){
        return context;
    }

    public static HandlerDB getHandlerDB(){
        return HandlerDB.getInstance(context);
    }

    public static Media getMedia(){
        if(media == null)
            media = new Media();
        return media;
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
        View view = getContext().findViewById(android.R.id.content);
        if(view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showDialogConfirmation(DialogInterface.OnClickListener positiveListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Confirmar");
        dialog.setMessage("Desea eliminar");
        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(android.R.string.yes, positiveListener);
        dialog.create().show();
    }


}
