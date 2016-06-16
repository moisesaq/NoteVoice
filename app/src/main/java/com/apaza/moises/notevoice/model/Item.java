package com.apaza.moises.notevoice.model;

import com.apaza.moises.notevoice.database.Note;

public class Item {
    public static final int SECTION = 0;
    public static final int NOTE = 1;

    public int sectionPosition;
    public int listPosition;

    public final int type;
    public String title;
    public Note note;

    public Item(int type, String titleSection){
        this.type = type;
        this.title = titleSection;
    }

    public Item(int type, Note note){
        this.type = type;
        this.note = note;
    }
}
