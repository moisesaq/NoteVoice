package com.apaza.moises.notevoice.global;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.DaoSession;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.model.HandlerDB;
import com.apaza.moises.notevoice.MainActivity;
import com.apaza.moises.notevoice.model.Media;

import java.util.List;

public class Global {
    private static MainActivity context;
    private static Media media;

    public static void setContext(MainActivity activity){
        context = activity;
    }

    public static MainActivity getContext(){
        return context;
    }

    public static HandlerDB getHandlerDB(){
        return HandlerDB.getInstance(context);
    }

    public static Media getMedia(){
        if(media == null)
            media = new Media();
        return media;
    }


    public static void showMessage(String message){
        View view = getContext().findViewById(android.R.id.content);
        if(view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showDialogConfirmation(DialogInterface.OnClickListener positiveListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Confirmar");
        dialog.setMessage("Desea eliminar");
        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(android.R.string.yes, positiveListener);
        dialog.create().show();
    }

    public static void playAlert(Context context){
        try{
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ring = RingtoneManager.getRingtone(context, alarm);
            ring.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Note saveNewNote(){
        DaoSession daoSession = Global.getHandlerDB().getDaoSession();
        Note note = new Note();
        note.setCode(Utils.generateCodeUnique("note"));

        note.setColor(String.valueOf(Utils.colorGenerator.getRandomColor()));
        note.setCreateAt(Utils.getCurrentDate());
        note.setUpdateAt(Utils.getCurrentDate());

        long idNote = daoSession.getNoteDao().insert(note);
        if(idNote > 0)
            return note;

        return null;
    }

    public static Audio saveAudioNote(Note note, String outputFileName){
        if(note == null || outputFileName == null)
            return null;

        DaoSession daoSession = Global.getHandlerDB().getDaoSession();
        Audio audio = getNewAudio(outputFileName);
        audio.setIdNote(note.getId());
        if(daoSession.getAudioDao().insert(audio) > 0){
            showListAudio(note);
            return audio;
        } else {
            return null;
        }
    }

    public static Message saveTextNote(Note note, String text){
        if(note == null)
            return null;

        DaoSession daoSession = Global.getHandlerDB().getDaoSession();
        Message message = getNewMessage(text);
        message.setIdNote(note.getId());
        if(daoSession.getMessageDao().insert(message) > 0){
            showListText(note);
            return message;
        } else{
            return null;
        }
    }

    private static Message getNewMessage(String text){
        Message message = new Message();
        message.setCode(Utils.generateCodeUnique("message"));
        message.setTextMessage(text.isEmpty() ? "This is a note" : text);
        message.setCreateAt(Utils.getCurrentDate());
        return message;
    }

    private static Audio getNewAudio(String outputFilename){
        Audio audio = new Audio();
        audio.setCode(Utils.generateCodeUnique("audio"));
        audio.setRoute(outputFilename);
        audio.setDuration(0);
        audio.setCreateAt(Utils.getCurrentDate());
        return audio;
    }

    /*THIS METHODS IS FOR TEST*/
    public static void showListNote(){
        List<Note> list = Global.getHandlerDB().getDaoSession().getNoteDao().queryBuilder().list();
        for (Note note: list){
            Log.d("DATA BASE", "Note: " + note.getCreateAt() + " - " + note.getCode());
            showListText(note);
            showListAudio(note);
        }
    }

    public static void showListAudio(Note note){
        List<Audio> list = Global.getHandlerDB().getDaoSession().getAudioDao()._queryNote_NoteAudio(note.getId());
        for (Audio audio: list){
            Log.d("DATA BASE", "AUDIO >>> File: " + audio.getRoute() + " Created: " + audio.getCreateAt() + "\n");
        }
    }

    public static void showListText(Note note){
        List<Message> list = Global.getHandlerDB().getDaoSession().getMessageDao()._queryNote_NoteMessage(note.getId());
        for (Message message: list){
            Log.d("DATA BASE", "MESSAGE >>> Text: " +  message.getTextMessage() + " Created: " + message.getCreateAt() + "\n");
        }
    }

}
