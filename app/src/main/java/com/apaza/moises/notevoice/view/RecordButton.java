package com.apaza.moises.notevoice.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.global.CustomAnimationListener;
import com.apaza.moises.notevoice.global.Global;

import java.util.concurrent.TimeUnit;

public class RecordButton extends FrameLayout implements View.OnTouchListener{

    public static final String TAG = "RADIO_BUTTON";

    private TextView time;
    private ImageButton record;

    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;

    private long count = 0;
    private boolean isCanceled = false;

    private OnRecordButtonListener onRecordButtonListener;

    public interface OnRecordButtonListener{
        void onStartRecord();
        void onEndRecord(long second);
        void onCancelRecord();
    }
    public RecordButton(Context context) {
        super(context);
        setupView();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    private void setupView(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_record_button, this, true);
        time = (TextView)findViewById(R.id.time);
        record = (ImageButton)findViewById(R.id.record);
        record.setOnTouchListener(this);
    }

    public void setOnRecordButtonListener(OnRecordButtonListener listener){
        this.onRecordButtonListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(onRecordButtonListener != null)
                    startAnimationRecord(record);
                break;

            case MotionEvent.ACTION_UP:
                if(onRecordButtonListener != null)
                    collapseText(time);

                break;

            case MotionEvent.ACTION_CANCEL:
                //collapseText(time);
                break;
        }
        return false;
    }

    public void startAnimationRecord(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.7f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.7f);
        //ObjectAnimator animColor = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), Color.parseColor("#FF0000"), Color.parseColor("#8B0000"));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim1, anim2);
        animatorSet.addListener(new CustomAnimationListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, " >>> END START ANIMATED RECORD BUTTON");
                time.setVisibility(View.VISIBLE);
                expanseText(time);
            }
        });
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    public void expanseText(View view1){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view1, "translationX", 0, -200);
        anim.setDuration(200);
        anim.setInterpolator(new BounceInterpolator());
        anim.addListener(new CustomAnimationListener(){

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, " >>> END EXPANSE TEXT VIEW TIME");
                /*startTime();
                onRecordButtonListener.onStartRecord();*/
                Global.getMedia().startAudioPlaying(R.raw.pip_pip, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(!isCanceled){
                            startTime();
                            onRecordButtonListener.onStartRecord();
                        }
                    }
                });
            }
        });
        anim.start();
    }

    public void collapseText(View view1){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view1, "X", record.getLeft());
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new CustomAnimationListener(){

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, " >>> END COLLAPSE TEXT VIEW TIME");
                endAnimationRecord(record);
            }
        });
        anim.start();
    }



    public void endAnimationRecord(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.7f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.7f, 1.0f);
        //ObjectAnimator animColor = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(),Color.parseColor("#8B0000"), Color.parseColor("#FF0000"));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim2, anim1);
        animatorSet.setDuration(300);
        animatorSet.addListener(new CustomAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, " >>> END END ANIMATED RECORD BUTTON");
                if(count > 1)
                    onRecordButtonListener.onEndRecord(count);
                else{
                    onRecordButtonListener.onCancelRecord();
                    isCanceled = true;
                    Global.getMedia().stopAudio();
                }

                time.setVisibility(View.INVISIBLE);
                stopTime();
            }
        });
        animatorSet.start();
    }

    protected void startTime(){
        stopTime();
        timeHandler.postDelayed(timeRunnable = new Runnable() {
            @Override
            public void run() {
                count++;
                time.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(count*1000),
                        TimeUnit.MILLISECONDS.toSeconds(count*1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(count*1000))
                ));
                timeHandler.postDelayed(timeRunnable, 1000);
            }
        }, 0);
    }

    private void stopTime(){
        count = 0;
        isCanceled = false;
        timeHandler.removeCallbacks(timeRunnable);
        time.setText("00:00");
    }

    //recordingTime.setText(String.valueOf(count));
                /*recordingTime.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(count*1000),
                        TimeUnit.MILLISECONDS.toSeconds(count*1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(count*1000))
                ));*/

    /*Handler recordMissingEndingHandler = new Handler();
                recordMissingEndingHandler.postDelayed(new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                }, Media.RECORD_MISSING_ENDING_FIX_INTERVAL);*/
}
