package com.apaza.moises.notevoice.adapter;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.AudioPlayView;

import java.util.List;


public class NoteVoiceListAdapter extends RecyclerView.Adapter<NoteVoiceListAdapter.ViewHolder>{

    private List<Note> listNote;
    private OnNoteVoiceListAdapterListener onNoteVoiceListAdapterListener;

    private AudioPlayView lastAudioPlayView;

    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    public interface OnNoteVoiceListAdapterListener{
        void onDeleteClick(Note note);
        void onEditClick(Note note);
    }

    public NoteVoiceListAdapter(List<Note> listNote){
        this.listNote = listNote;
    }


    public void setOnNoteVoiceListAdapterListener(OnNoteVoiceListAdapterListener listener){
        this.onNoteVoiceListAdapterListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_voice_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = listNote.get(position);
        if(note.getNoteAudio().size() > 0){
            prepareAudio(holder.audioPlay, note.getNoteAudio().get(0));
        }else{
            holder.audioPlay.setVisibility(View.GONE);
        }

        if(note.getNoteMessage().size() > 0)
            holder.textNote.setText(note.getNoteMessage().get(0).getTextMessage());
        else
            holder.textNote.setVisibility(View.GONE);
        holder.date.setText(Utils.getTimeCustom(note.getCreateAt()));
    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public AudioPlayView audioPlay;
        public TextView textNote, date;
        public ImageButton delete, edit;

        public ViewHolder(View view){
            super(view);
            audioPlay = (AudioPlayView)view.findViewById(R.id.audioPlay);
            textNote = (TextView)view.findViewById(R.id.textNote);
            date = (TextView)view.findViewById(R.id.date);
            delete = (ImageButton)view.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            edit = (ImageButton)view.findViewById(R.id.edit);
            edit.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.delete:
                    onNoteVoiceListAdapterListener.onDeleteClick(listNote.get(getAdapterPosition()));
                    break;
                case R.id.edit:
                    onNoteVoiceListAdapterListener.onEditClick(listNote.get(getAdapterPosition()));
                    break;
            }
        }
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
