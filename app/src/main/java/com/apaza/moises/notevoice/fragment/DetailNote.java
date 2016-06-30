package com.apaza.moises.notevoice.fragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.AlarmReceiver;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Media;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailNote extends DialogFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private View view;

    private SeekBar seekBar;
    private TextView textNote, durationAudio, dateNote;

    private TextView timeAlarm, dateAlarm, alarmIn;
    private Switch statusAlarm;

    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();
    public static int oneTimeOnly = 0;

    private static final String ID_NOTE = "idNote";
    private long idNote;
    private Note note;

    private PendingIntent pendingIntent;

    public DetailNote() {
        // Required empty public constructor
    }
    public static DetailNote newInstance(long param1) {
        DetailNote fragment = new DetailNote();
        Bundle args = new Bundle();
        args.putLong(ID_NOTE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            idNote = getArguments().getLong(ID_NOTE);
            note = Global.getHandlerDB().getDaoSession().getNoteDao().load(idNote);
        }

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_save:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_note, container, false);
        setupView();
        return view;
    }

    private void setupView(){
        if(note == null)
            return;
        Button play = (Button) view.findViewById(R.id.play);
        play.setOnClickListener(this);
        Button stop = (Button)view.findViewById(R.id.stop);
        stop.setOnClickListener(this);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        durationAudio = (TextView)view.findViewById(R.id.durationAudio);
        durationAudio.setText(Media.formatDuration(Media.getDurationAudioFile(note.getNoteAudio().get(0).getRoute())));
        textNote = (TextView)view.findViewById(R.id.textNote);
        textNote.setText(note.getNoteMessage().get(0).getTextMessage());
        dateNote = (TextView)view.findViewById(R.id.date);
        dateNote.setText(note.getCreateAt().toString());

        timeAlarm = (TextView)view.findViewById(R.id.timeAlarm);
        timeAlarm.setOnClickListener(this);
        dateAlarm = (TextView)view.findViewById(R.id.dateAlarm);
        dateAlarm.setOnClickListener(this);
        alarmIn = (TextView)view.findViewById(R.id.alarmIn);
        statusAlarm = (Switch)view.findViewById(R.id.statusAlarm);
        statusAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Global.showMessage("ON");
                    long when =(Long)timeAlarm.getTag();
                    if(when > 0)
                        //Global.showMessage("Alarm in " + ((when-Utils.getCurrentDate().getTime())/(1000*60)) + "min");
                        startAlarm(when);
                }else{
                    //Global.showMessage("OFF");
                    cancelAlarm(ALARM_REQUEST_CODE);
                }
            }
        });
        Button testAlarm = (Button)view.findViewById(R.id.testAlarm);
        testAlarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                startAudio(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopAudio();
                    }
                });
                break;
            case R.id.stop:
                //stopAudio();
                cancelAlarm(ALARM_REQUEST_CODE);
                break;
            case R.id.testAlarm:
                startAlarm();
                break;

            case R.id.timeAlarm:
                new TimeDialog(this).show(getFragmentManager(), TimeDialog.TAG);
                break;

            case R.id.dateAlarm:
                new DateDialog(this).show(getFragmentManager(), DateDialog.TAG);
                break;
        }
    }

    private void selectTime(){
        //new TimeDialo
    }

    private void selectDate(){

    }

    public static int ALARM_REQUEST_CODE = 100;

    private void startAlarm(){
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra("code", ALARM_REQUEST_CODE);

        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), ALARM_REQUEST_CODE, alarmIntent, 0);//PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30 * 1000, pendingIntent);
        Global.showMessage("Alarm set for 30 second");
    }

    private void cancelAlarm(int requestCode){
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), ALARM_REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Global.showMessage("Alarm canceled!");
    }


    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = Global.getMedia().getAudioCurrentPosition();
            seekBar.setProgress(startTime);
            handler.postDelayed(this, 100);
        }
    };

    public void startAudio(MediaPlayer.OnCompletionListener listener){
        try{
            if(Global.getMedia().isAudioPlaying()){
                stopAudio();
            }
            Global.getMedia().setupAudio(note.getNoteAudio().get(0).getRoute());
            finalTime = Global.getMedia().getAudioMaxDuration();
            startTime = Global.getMedia().getAudioCurrentPosition();
            this.seekBar.setMax(finalTime);
            this.seekBar.setProgress(startTime);

            Global.getMedia().startAudio(listener);
            handler.postDelayed(UpdateSeekBar, 100);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopAudio(){
        try{
            Global.getMedia().stopAudio();
            if(seekBar != null)
                seekBar.setProgress(0);
            handler.removeCallbacks(UpdateSeekBar);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("E MMM d yyyy");
        dateAlarm.setText(format.format(c.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(SystemClock.currentThreadTimeMillis());

        c.setTimeInMillis(Utils.getCurrentDate().getTime());

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm a");
        if(DateFormat.is24HourFormat(getActivity()))
            format = new SimpleDateFormat("HH:mm");
        timeAlarm.setText(format.format(c.getTime()));
        timeAlarm.setTag(c.getTimeInMillis());

        //startAlarm(c.getTimeInMillis());
        Log.d("CURRENT TIME ", "> "+Utils.getCurrentDate().toString());
        Log.d("TIME ALARM ", "> "+c.getTime().toString());
        try{
            alarmIn.setText("Test " + Utils.getDifferenceMin(c.getTime()));
            //Log.d("LOG TIME ", "> " + c.getTime().getTime()+"-"+Utils.getCurrentDate().getTime());//
            Log.d("STRING TIME ", "> "+Utils.getDifferenceMin(c.getTime()));//
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void startAlarm(long when){
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra("code", ALARM_REQUEST_CODE);

        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), ALARM_REQUEST_CODE, alarmIntent, 0);//PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, 1000, pendingIntent);
    }
}
