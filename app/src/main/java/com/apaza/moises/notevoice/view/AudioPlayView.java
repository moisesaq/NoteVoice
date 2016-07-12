package com.apaza.moises.notevoice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;

public class AudioPlayView extends LinearLayout implements View.OnClickListener{

    private ImageButton play, stop, error;
    private SeekBar progress;
    private TextView duration;

    private OnAudioPlayViewListener onAudioPlayViewListener;

    @Override
    public void onClick(View v) {
        if(onAudioPlayViewListener == null)
            return;

        switch (v.getId()){
            case R.id.play:
                onAudioPlayViewListener.onPlayClick();
                showStop();
                break;
            case R.id.stop:
                onAudioPlayViewListener.onStopClick();
                showPlay();
                break;
        }
    }

    public interface OnAudioPlayViewListener{
        void onPlayClick();
        void onStopClick();
    }
    public AudioPlayView(Context context) {
        super(context);
        setupView();
    }

    public AudioPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    private void setupView(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_audio_play, this, true);
        play = (ImageButton)findViewById(R.id.play);
        play.setOnClickListener(this);
        stop = (ImageButton)findViewById(R.id.stop);
        stop.setOnClickListener(this);
        error = (ImageButton)findViewById(R.id.error);
        progress = (SeekBar)findViewById(R.id.progress);
        duration = (TextView)findViewById(R.id.duration);
    }

    public void setOnAudioPlayViewListener(OnAudioPlayViewListener onAudioPlayViewListener){
        this.onAudioPlayViewListener = onAudioPlayViewListener;
    }

    public void showPlay(){
        play.setVisibility(View.VISIBLE);
        stop.setVisibility(View.GONE);
    }

    public void showStop(){
        play.setVisibility(View.GONE);
        stop.setVisibility(View.VISIBLE);
    }

    public SeekBar getProgress(){
        return this.progress;
    }

    public TextView getDuration(){
        return duration;
    }

    public void showError(){
        play.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        duration.setText("Sorry, error");
    }
}
