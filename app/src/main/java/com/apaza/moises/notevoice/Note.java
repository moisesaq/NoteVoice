package com.apaza.moises.notevoice;


import java.util.ArrayList;
import java.util.List;

public class Note {

    private String idNote, message;
    private int audio;

    public Note(String idNote, String message, int audio){
        this.idNote = idNote;
        this.message = message;
        this.audio = audio;
    }

    public String getIdNote() {
        return idNote;
    }

    public Note setIdNote(String idNote) {
        this.idNote = idNote;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Note setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getAudio() {
        return audio;
    }

    public Note setAudio(int audio) {
        this.audio = audio;
        return this;
    }

    public static List<Note> getListTest(){
        List<Note> list = new ArrayList<>();
        list.add(new Note("001", "Message test 111", R.raw.dejala_hablar));
        list.add(new Note("001", "Message test 222", R.raw.detective));
        list.add(new Note("003", "Message test 333", R.raw.los_simpsons_a_cargo_de_la_seguridad));
        return list;
    }
}
