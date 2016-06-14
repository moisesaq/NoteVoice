package com.apaza.moises.notevoice;

public class ContractNoteVoice {
    public static final String NAME_DB = "dataBaseNoteVoice";
    public static final String NOTE = "Note";

    interface ColumnsNote{
        String ID = "id";
        String TEXT = "text";
        String PATH_AUDIO = "pathAudio";
        String COLOR = "color";
        String DATE = "dateCreated";
    }
}
