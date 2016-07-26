package com.apaza.moises.notevoice.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.apaza.moises.notevoice.R;

public class OptionDialog extends DialogFragment{
    public static final String TAG = "OPTION_DIALOG";
    public static String DATA = "data";

    private String[] options = {"Image capture", "Select image"};
    private String data;

    private OnOptionDialogListener onOptionDialogListener;

    public OptionDialog(){

    }

    public static OptionDialog newInstance(String data, OnOptionDialogListener onOptionDialogListener){
        OptionDialog optionDialog = new OptionDialog();
        optionDialog.setOnOptionDialogListener(onOptionDialogListener);
        Bundle args = new Bundle();
        args.putString(DATA, data);
        optionDialog.setArguments(args);
        return optionDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            data = getArguments().getString(DATA);

        if(data != null && !data.isEmpty())
            options[2] = "Removing image";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(onOptionDialogListener == null)
                    return;

                switch (i){
                    case 0:
                        onOptionDialogListener.onImageCapture();
                        break;
                    case 1:
                        onOptionDialogListener.onSelectImage();
                        break;
                    case 2:
                        onOptionDialogListener.onRemovingImage();
                        break;
                    default:
                        dismiss();
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return dialog.create();
    }

    public void setOnOptionDialogListener(OnOptionDialogListener listener){
        this.onOptionDialogListener = listener;
    }

    public interface OnOptionDialogListener{
        void onImageCapture();
        void onSelectImage();
        void onRemovingImage();
    }
}
