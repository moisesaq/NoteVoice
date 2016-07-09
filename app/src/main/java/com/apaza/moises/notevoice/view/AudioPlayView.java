package com.apaza.moises.notevoice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;

public class AudioPlayView extends LinearLayout{

    private ImageButton play, stop;
    private SeekBar progress;
    private TextView duration;

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
        stop = (ImageButton)findViewById(R.id.stop);
        progress = (SeekBar)findViewById(R.id.progress);
        duration = (TextView)findViewById(R.id.duration);
    }
}
