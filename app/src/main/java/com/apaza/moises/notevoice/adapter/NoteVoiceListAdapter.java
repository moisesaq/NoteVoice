package com.apaza.moises.notevoice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.fragment.NoteVoiceListFragment;
import com.apaza.moises.notevoice.view.AudioPlayView;

import java.util.List;


public class NoteVoiceListAdapter extends RecyclerView.Adapter<NoteVoiceListAdapter.ViewHolder>{

    private List<Note> listNote;
    private OnNoteVoiceListAdapterListener onNoteVoiceListAdapterListener;

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
        holder.textNote.setText(listNote.get(position).getNoteMessage().get(0).getTextMessage());
        holder.date.setText(listNote.get(position).getCreateAt().toString());
    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        AudioPlayView audioPlay;
        TextView textNote, date;
        ImageButton delete, edit;

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
}
