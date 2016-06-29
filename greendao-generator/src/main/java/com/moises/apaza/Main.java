package com.moises.apaza;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Main {

    public static final String NOTE = "Note";
    public static final String ALARM = "Alarm";

    public static final String IMAGE = "Image";
    public static final String NOTE_IMAGE = "NoteImage";

    public static final String AUDIO = "Audio";
    public static final String NOTE_AUDIO = "NoteAudio";

    public static final String MESSAGE = "Message";
    public static final String NOTE_MESSAGE = "NoteMessage";

    interface ColumnsNote{
        String CODE = "code";
        String COLOR = "color";
        String CREATE_AT = "createAt";
        String UPDATE_AT = "updateAt";
        String ID_ALARM = "idAlarm";
    }

    interface ColumnsAlarm {
        String CODE = "code";
        String ALARM_DATE = "alarmDate";
        String REASON = "reason";
        String STATUS = "status";
    }

    interface ColumnsImage{
        String CODE = "code";
        String ROUTE = "route";
        String DESCRIPTION = "description";
        String CREATE_AT = "createAt";
        String ID_NOTE = "idNote";
    }

    interface ColumnsAudio{
        String CODE = "code";
        String ROUTE = "route";
        String DURATION = "duration";
        String CREATE_AT = "createAt";
        String ID_NOTE = "idNote";
    }

    interface ColumnsMessage{
        String CODE = "code";
        String TEXT_MESSAGE = "textMessage";
        String CREATE_AT = "createAt";
        String ID_NOTE = "idNote";
    }

    public static void main(String[] args){
        Schema schema = new Schema(1, "com.apaza.moises.notevoice.database");
        Entity note = schema.addEntity(NOTE);
        note.addIdProperty();
        note.addStringProperty(ColumnsNote.CODE);
        note.addStringProperty(ColumnsNote.COLOR);
        note.addStringProperty(ColumnsNote.CREATE_AT);
        note.addStringProperty(ColumnsNote.UPDATE_AT);

        /*-------------------TABLE ALARM--------------*/
        Entity alarm = schema.addEntity(ALARM);
        alarm.addIdProperty();
        alarm.addLongProperty(ColumnsAlarm.CODE);
        alarm.addDateProperty(ColumnsAlarm.ALARM_DATE);
        alarm.addStringProperty(ColumnsAlarm.REASON);
        alarm.addBooleanProperty(ColumnsAlarm.STATUS);

        Property idAlarm = alarm.addLongProperty(ColumnsNote.ID_ALARM).getProperty();
        note.addToOne(note, idAlarm);

        /*-------------------TABLE IMAGE--------------*/
        Entity image = schema.addEntity(IMAGE);
        image.addIdProperty();
        image.addStringProperty(ColumnsImage.CODE);
        image.addStringProperty(ColumnsImage.ROUTE);
        image.addStringProperty(ColumnsImage.DESCRIPTION);
        Property createAt = image.addDateProperty(ColumnsImage.CREATE_AT).getProperty();

        Property idNote = image.addLongProperty(ColumnsImage.ID_NOTE).getProperty();
        image.addToOne(note, idNote);

        ToMany noteToImage = note.addToMany(image, idNote);
        noteToImage.setName(NOTE_IMAGE);
        noteToImage.orderDesc(createAt);

        /*-------------------TABLE AUDIO--------------*/

        try{
            DaoGenerator daoGenerator = new DaoGenerator();
            daoGenerator.generateAll(schema, "./app/src/main/java");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
