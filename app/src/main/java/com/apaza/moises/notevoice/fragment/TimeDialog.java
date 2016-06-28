package com.apaza.moises.notevoice.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimeDialog extends DialogFragment{

    public static final String TAG = "TIME_DIALOG";
    private TimePickerDialog.OnTimeSetListener listener;

    public TimeDialog(){}

    @SuppressLint("ValidFragment")
    public TimeDialog(TimePickerDialog.OnTimeSetListener listener){
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), listener,
                hour, min, DateFormat.is24HourFormat(getActivity()));
    }
}
