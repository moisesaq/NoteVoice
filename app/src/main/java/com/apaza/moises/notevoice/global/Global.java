package com.apaza.moises.notevoice.global;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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


    public static void playAlert(Context context){
        try{
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ring = RingtoneManager.getRingtone(context, alarm);
            ring.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
