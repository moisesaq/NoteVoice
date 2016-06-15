package com.apaza.moises.notevoice.model;
import android.content.Context;
import android.media.MediaPlayer;

public class Media {
    private static Media media;
    private static Context context;
    private MediaPlayer mediaPlayer;

    private int recourseId;
    private MediaPlayer.OnCompletionListener listener;

    private Media(Context context){
        Media.context = context;
    }

    public static Media getInstance(Context context){
        if(media == null)
            media = new Media(context);
        return media;
    }

    public void setupAudio(int recourseId){
        mediaPlayer = MediaPlayer.create(context, recourseId);
    }

    public int getMediaDuration(){
        return mediaPlayer.getDuration();
    }

    public int getMediaCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void startAudio(MediaPlayer.OnCompletionListener listener){
        try{
            this.listener = listener;
            if(mediaPlayer != null){
                if(mediaPlayer.isPlaying())
                    stopAudio();
                else{
                    mediaPlayer.setOnCompletionListener(this.listener);
                    mediaPlayer.start();
                }
            }
            /*mediaPlayer = MediaPlayer.create(context, recourseId);

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            seekBar.setMax(finalTime);
            mediaPlayer.setOnCompletionListener(this.listener);*/



            /*seekBar.setProgress(startTime);
            handler.postDelayed(UpdateSeekBar, 100);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopAudio(){
        try{
            if(mediaPlayer != null){
                //seekBar.setProgress(0);
                this.listener.onCompletion(mediaPlayer);
                this.listener = null;
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                //new MediaPlayer.OnCompletionListener().onCompletion(mediaPlayer);
            }
            //handler.removeCallbacks(UpdateSeekBar);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
