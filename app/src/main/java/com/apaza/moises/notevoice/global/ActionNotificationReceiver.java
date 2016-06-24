package com.apaza.moises.notevoice.global;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionNotificationReceiver extends BroadcastReceiver{

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(action.equals(Utils.YES_ACTION)){
            Toast.makeText(context, "You trip continues, it come a car for you", Toast.LENGTH_LONG).show();
            notificationManager.cancel(Utils.NOTIFICATION_REQUEST_CODE);
        }else if(action.equals(Utils.NO_ACTION)){
            Toast.makeText(context, "You trip was canceled", Toast.LENGTH_LONG).show();
            notificationManager.cancel(Utils.NOTIFICATION_REQUEST_CODE);
        }else if(action.equals(Utils.SEE_ACTION)){
            Toast.makeText(context, "action see", Toast.LENGTH_LONG).show();
            notificationManager.cancel(Utils.NOTIFICATION_REQUEST_CODE);
        }
    }
}
