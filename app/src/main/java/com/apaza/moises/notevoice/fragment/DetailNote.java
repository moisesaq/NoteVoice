package com.apaza.moises.notevoice.fragment;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.AlarmReceiver;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.model.Media;

public class DetailNote extends DialogFragment implements View.OnClickListener{
    private View view;

    private SeekBar seekBar;
    private TextView textNote, durationAudio, dateNote;

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

        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 100, alarmIntent, 0);//PendingIntent.FLAG_CANCEL_CURRENT);
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
        durationAudio.setText(Media.formatDuration(Media.getDurationAudioFile(note.getPathAudio())));
        textNote = (TextView)view.findViewById(R.id.textNote);
        textNote.setText(note.getText());
        dateNote = (TextView)view.findViewById(R.id.date);
        dateNote.setText(note.getDateCreated());

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
                stopAudio();
                break;
            case R.id.testAlarm:
                startAlarm();
                break;
        }
    }

    private void startAlarm(){
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        int interval = 5000;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30 * 1000, pendingIntent);
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
            Global.getMedia().setupAudio(note.getPathAudio());
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


}
