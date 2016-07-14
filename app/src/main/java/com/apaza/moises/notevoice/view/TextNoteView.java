package com.apaza.moises.notevoice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;

public class TextNoteView extends LinearLayout{

    private ImageView icon;
    private TextView text;

    public TextNoteView(Context context) {
        super(context);
        setupView();
    }

    public TextNoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    private void  setupView(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_text_note, this, true);
        icon = (ImageView)findViewById(R.id.icon);
        text = (TextView)findViewById(R.id.text);
    }

    public void setTextNote(String text){
        this.text.setText(text);
    }
}
