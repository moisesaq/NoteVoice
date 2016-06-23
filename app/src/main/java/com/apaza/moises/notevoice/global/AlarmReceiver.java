package com.apaza.moises.notevoice.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm ring! ring! :)", Toast.LENGTH_SHORT).show();
        Global.playAlert(context);
    }
}
