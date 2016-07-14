package com.apaza.moises.notevoice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.view.TextNoteView;

import java.util.List;

public class ListTextAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> textList;

    public ListTextAdapter(Context context, List<Message> textList){
        super(context, R.layout.list_text_item, textList);
        this.context = context;
        this.textList = textList;
    }

    public static class ViewHolder{
        TextNoteView textNote;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Message getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_text_item, null);
            holder = new ViewHolder();
            holder.textNote = (TextNoteView)view.findViewById(R.id.textNote);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        Message message = getItem(position);
        holder.textNote.setTextNote(message.getTextMessage());
        return view;
    }
}
