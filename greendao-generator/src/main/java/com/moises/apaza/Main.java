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
        Schema schema = new Schema(4, "com.apaza.moises.notevoice.database");

        /*-------------------TABLE ALARM--------------*/
        Entity alarm = schema.addEntity(ALARM);
        alarm.addIdProperty();
        alarm.addLongProperty(ColumnsAlarm.CODE);
        alarm.addDateProperty(ColumnsAlarm.ALARM_DATE);
        alarm.addStringProperty(ColumnsAlarm.REASON);
        alarm.addBooleanProperty(ColumnsAlarm.STATUS);
        alarm.addContentProvider();

        /*-------------------TABLE NOTE--------------*/
        Entity note = schema.addEntity(NOTE);
        note.addIdProperty();
        note.addStringProperty(ColumnsNote.CODE);
        note.addStringProperty(ColumnsNote.COLOR);
        note.addDateProperty(ColumnsNote.CREATE_AT);
        note.addDateProperty(ColumnsNote.UPDATE_AT);
        note.addContentProvider();

        Property idAlarm = note.addLongProperty(ColumnsNote.ID_ALARM).getProperty();
        note.addToOne(alarm, idAlarm); //A note have a alarm

        /*-------------------TABLE IMAGE--------------*/
        Entity image = schema.addEntity(IMAGE);
        image.addIdProperty();
        image.addStringProperty(ColumnsImage.CODE);
        image.addStringProperty(ColumnsImage.ROUTE);
        image.addStringProperty(ColumnsImage.DESCRIPTION);
        Property createAtImage = image.addDateProperty(ColumnsImage.CREATE_AT).getProperty();

        Property idNoteImage = image.addLongProperty(ColumnsImage.ID_NOTE).getProperty();
        image.addToOne(note, idNoteImage);
        image.addContentProvider();

        ToMany noteToImage = note.addToMany(image, idNoteImage);
        noteToImage.setName(NOTE_IMAGE);
        noteToImage.orderDesc(createAtImage);

        /*-------------------TABLE AUDIO--------------*/
        Entity audio = schema.addEntity(AUDIO);
        audio.addIdProperty();
        audio.addStringProperty(ColumnsAudio.CODE);
        audio.addStringProperty(ColumnsAudio.ROUTE);
        audio.addIntProperty(ColumnsAudio.DURATION);
        Property createAtAudio = audio.addDateProperty(ColumnsAudio.CREATE_AT).getProperty();

        Property idNoteAudio = audio.addLongProperty(ColumnsAudio.ID_NOTE).getProperty();
        audio.addToOne(audio, idNoteAudio);
        audio.addContentProvider();

        ToMany noteToAudio = note.addToMany(audio, idNoteAudio);
        noteToAudio.setName(NOTE_AUDIO);
        noteToAudio.orderDesc(createAtAudio);

        /*-------------------TABLE MESSAGE--------------*/
        Entity message = schema.addEntity(MESSAGE);
        message.addIdProperty();
        message.addStringProperty(ColumnsMessage.CODE);
        message.addStringProperty(ColumnsMessage.TEXT_MESSAGE);
        Property createAtMessage = message.addDateProperty(ColumnsMessage.CREATE_AT).getProperty();

        Property idNoteMessage = message.addLongProperty(ColumnsMessage.ID_NOTE).getProperty();
        message.addToOne(message, idNoteMessage);
        message.addContentProvider();

        ToMany noteToMessage = note.addToMany(message, idNoteMessage);
        noteToMessage.setName(NOTE_MESSAGE);
        noteToMessage.orderDesc(createAtMessage);

        try{
            DaoGenerator daoGenerator = new DaoGenerator();
            daoGenerator.generateAll(schema, "./app/src/main/java");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
