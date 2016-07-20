package com.apaza.moises.notevoice.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.AudioPlayView;

import java.util.Comparator;
import java.util.List;

public class ListAudioAdapter extends ArrayAdapter<Audio> {

    private Context context;
    private AudioPlayView lastAudioPlayView;

    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    private Comparator<? super Audio> comparator;

    public ListAudioAdapter(Context context, List<Audio> audioList){
        super(context, R.layout.list_audio_item, audioList);
        this.context = context;
        comparator = new Comparator<Audio>() {
            @Override
            public int compare(Audio lhs, Audio rhs) {
                return lhs.getCreateAt().compareTo(rhs.getCreateAt());
            }
        };
    }

    public static class ViewHolder{
        AudioPlayView audioPlayView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Audio getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_audio_item, null);
            holder.audioPlayView = (AudioPlayView)view.findViewById(R.id.audioPlay);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        Audio audio = getItem(position);
        prepareAudio(holder.audioPlayView, audio);
        return view;
    }

    @Override
    public void add(Audio object) {
        super.add(object);
        this.sort(comparator);
        notifyDataSetChanged();
    }

    public void prepareAudio(final AudioPlayView audioPlayView, final Audio audio){
        if(audio == null){
            audioPlayView.showError();
            return;
        }

        audioPlayView.getDuration().setText(Media.formatDuration(Media.getDurationAudioFile(audio.getRoute())));
        audioPlayView.setOnAudioPlayViewListener(new AudioPlayView.OnAudioPlayViewListener() {
            @Override
            public void onPlayClick() {
                startAudio(audio.getRoute(), audioPlayView, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Global.showMessage("Play");
                        stopAudio();
                    }
                });
            }

            @Override
            public void onStopClick() {
                Global.showMessage("Stop");
                Global.getMedia().stopAudio();
            }
        });
    }

    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = Global.getMedia().getAudioCurrentPosition();//mediaPlayer.getCurrentPosition();
            lastAudioPlayView.getProgress().setProgress(startTime);
            handler.postDelayed(this, 100);
        }
    };

    public void startAudio(String pathAudio, AudioPlayView audioPlayView, MediaPlayer.OnCompletionListener listener){
        try{
            if(Global.getMedia().isAudioPlaying()){
                stopAudio();
            }
            Global.getMedia().setupAudio(pathAudio);
            this.lastAudioPlayView = audioPlayView;

            finalTime = Global.getMedia().getAudioMaxDuration();
            startTime = Global.getMedia().getAudioCurrentPosition();
            this.lastAudioPlayView.getProgress().setMax(finalTime);
            this.lastAudioPlayView.getProgress().setProgress(startTime);

            Global.getMedia().startAudio(listener);
            handler.postDelayed(UpdateSeekBar, 100);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopAudio(){
        try{
            Global.getMedia().stopAudio();
            //showPlayButton(holder);
            if(lastAudioPlayView != null){
                lastAudioPlayView.showPlay();
                lastAudioPlayView.getProgress().setProgress(0);
            }
            handler.removeCallbacks(UpdateSeekBar);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
