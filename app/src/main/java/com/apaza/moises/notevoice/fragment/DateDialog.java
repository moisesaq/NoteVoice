package com.apaza.moises.notevoice.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

public class DateDialog extends DialogFragment {

    public static final String TAG = "DATE_DIALOG";
    private DatePickerDialog.OnDateSetListener listener;

    public DateDialog(){}

    @SuppressLint("ValidFragment")
    public DateDialog(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}
