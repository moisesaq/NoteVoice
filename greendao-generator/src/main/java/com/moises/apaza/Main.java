package com.moises.apaza;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Main {

    interface ColumnsNote{
        String CODE = "code";
        String TEXT = "text";
        String PATH_AUDIO = "pathAudio";
        String COLOR = "color";
        String DATE = "dateCreated";
    }

    public static void main(String[] args){
        Schema schema = new Schema(1, "com.apaza.moises.notevoice.database");
        Entity note = schema.addEntity("Note");
        note.addIdProperty();
        note.addStringProperty(ColumnsNote.CODE);
        note.addStringProperty(ColumnsNote.TEXT);
        note.addStringProperty(ColumnsNote.PATH_AUDIO);
        note.addStringProperty(ColumnsNote.COLOR);
        note.addStringProperty(ColumnsNote.DATE);

        try{
            DaoGenerator daoGenerator = new DaoGenerator();
            daoGenerator.generateAll(schema, "./app/src/main/java");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
