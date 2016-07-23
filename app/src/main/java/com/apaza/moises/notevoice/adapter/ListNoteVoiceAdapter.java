package com.apaza.moises.notevoice.adapter;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.AudioPlayView;
import com.apaza.moises.notevoice.view.TextNoteView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ListNoteVoiceAdapter extends RecyclerView.Adapter<ListNoteVoiceAdapter.ViewHolder>{

    private List<Note> listNote;
    private OnNoteVoiceListAdapterListener onNoteVoiceListAdapterListener;

    private AudioPlayView lastAudioPlayView;

    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    private Comparator<? super Note> comparator;

    public interface OnNoteVoiceListAdapterListener{
        void onDeleteClick(Note note);
        void onEditClick(Note note);
    }

    public ListNoteVoiceAdapter(List<Note> listNote){
        this.listNote = listNote;
        comparator = new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                return rhs.getCreateAt().compareTo(lhs.getCreateAt());
            }
        };
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
            holder.audioPlay.setVisibility(View.VISIBLE);
            prepareAudio(holder.audioPlay, note.getNoteAudio().get(0));
        }else{
            holder.audioPlay.setVisibility(View.GONE);
        }

        if(note.getNoteMessage().size() > 0){
            holder.layoutTextNote.setVisibility(View.VISIBLE);
            List<Message> listMessage = Utils.listMessageSorded(note.getNoteMessage(), Utils.ORDER_DESC);
            holder.textNote.setTextNote(listMessage.get(0).getTextMessage() + " Total: " + listMessage.size());
        } else{
            holder.layoutTextNote.setVisibility(View.GONE);
        }

        holder.date.setText(Utils.getTimeCustom(note.getCreateAt()));
    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    public void addItem(Note note){
        listNote.add(note);
        sortDesc();
        notifyDataSetChanged();
    }

    public void swap(List<Note> listData){
        listNote.clear();
        listNote.addAll(listData);
        sortDesc();
        notifyDataSetChanged();
    }

    public void sortDesc(){
        Collections.sort(listNote, comparator);
    }

    public void removeItem(Note note){
        int position = listNote.indexOf(note);
        listNote.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public AudioPlayView audioPlay;
        public LinearLayout layoutTextNote;
        public TextNoteView textNote;
        public TextView date;
        public ImageButton delete, edit;

        public ViewHolder(View view){
            super(view);
            audioPlay = (AudioPlayView)view.findViewById(R.id.audioPlay);
            layoutTextNote = (LinearLayout)view.findViewById(R.id.layoutTextNote);
            textNote = (TextNoteView) view.findViewById(R.id.textNote);
            date = (TextView)view.findViewById(R.id.date);
            delete = (ImageButton)view.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            edit = (ImageButton)view.findViewById(R.id.edit);
            edit.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Note note = listNote.get(getAdapterPosition());
            switch (v.getId()){

                case R.id.delete:
                    if(onNoteVoiceListAdapterListener != null && note != null)
                        onNoteVoiceListAdapterListener.onDeleteClick(note);
                    break;
                case R.id.edit:
                    if(onNoteVoiceListAdapterListener != null && note != null)
                        onNoteVoiceListAdapterListener.onEditClick(note);
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
