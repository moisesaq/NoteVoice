package com.apaza.moises.notevoice.global;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.apaza.moises.notevoice.MainActivity;
import com.apaza.moises.notevoice.R;


public class AlarmReceiver extends BroadcastReceiver{

    int code = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        //AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Bundle extra = intent.getExtras();
        if(extra != null)
            code = extra.getInt("code");
        sendNotification(context, "Alarm ring! code-" + code);
        //Toast.makeText(context, "Alarm ring! code-" + code, Toast.LENGTH_SHORT).show();
        //Global.playAlert(context);
    }

    public void sendNotification(Context context, String text){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intentSee = new Intent();
        intentSee.setAction(Utils.SEE_ACTION);
        intentSee.putExtra("alarm_code", code);
        PendingIntent pendingIntentSee = PendingIntent.getBroadcast(context, Utils.NOTIFICATION_REQUEST_CODE,intentSee, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(context, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //This clear and restart the app
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Utils.NOTIFICATION_REQUEST_CODE,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setTicker(context.getResources().getString(R.string.app_name))
                .addAction(R.mipmap.ic_check_white_24dp, "SEE", pendingIntentSee)
                .build();
        notificationManager.notify(Utils.NOTIFICATION_REQUEST_CODE, notification);
    }
}
