package com.apaza.moises.notevoice.fragment.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;

public class NewTextNoteDialog extends DialogFragment implements View.OnClickListener{

    public static final String TAG = "NEW_TEXT_NOTE_DIALOG";

    private OnNewTextNoteListener onNewTextNoteListener;
    private EditText etNote;

    public NewTextNoteDialog(){

    }

    public static NewTextNoteDialog newInstance(OnNewTextNoteListener listener){
        NewTextNoteDialog newTextNoteDialog = new NewTextNoteDialog();
        newTextNoteDialog.setOnNewTextNoteListener(listener);

        return newTextNoteDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_new_text_note, null);
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(getString(R.string.new_text_note).toUpperCase());
        etNote = (EditText)view.findViewById(R.id.note);
        Button cancel = (Button)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        Button save = (Button)view.findViewById(R.id.save);
        save.setOnClickListener(this);
        dialog.setView(view);

        return dialog.create();
    }

    public void setOnNewTextNoteListener(OnNewTextNoteListener listener){
        this.onNewTextNoteListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.save:
                if(etNote.getText().length() > 0 && onNewTextNoteListener != null){
                    onNewTextNoteListener.onAccept(etNote.getText().toString());
                    this.dismiss();
                }
                break;
        }

    }

    public interface OnNewTextNoteListener {
        void onAccept(String text);
    }
}
