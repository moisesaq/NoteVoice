package com.apaza.moises.notevoice.model;


import com.apaza.moises.notevoice.R;

import java.util.ArrayList;
import java.util.List;

public class NoteTest {
    private String idNote, message;
    private int audio;

    public NoteTest(String idNote, String message, int audio){
        this.idNote = idNote;
        this.message = message;
        this.audio = audio;
    }

    public String getIdNote() {
        return idNote;
    }

    public NoteTest setIdNote(String idNote) {
        this.idNote = idNote;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public NoteTest setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getAudio() {
        return audio;
    }

    public NoteTest setAudio(int audio) {
        this.audio = audio;
        return this;
    }

    public static List<NoteTest> getListTest(){
        List<NoteTest> list = new ArrayList<>();
        list.add(new NoteTest("001", "Message test 111", R.raw.dejala_hablar));
        list.add(new NoteTest("001", "Message test 222", R.raw.detective));
        list.add(new NoteTest("003", "Message test 333", R.raw.los_simpsons_a_cargo_de_la_seguridad));
        return list;
    }
}
