package com.apaza.moises.notevoice.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.apaza.moises.notevoice.database.Alarm;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ALARM".
*/
public class AlarmDao extends AbstractDao<Alarm, Long> {

    public static final String TABLENAME = "ALARM";

    /**
     * Properties of entity Alarm.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Code = new Property(1, Long.class, "code", false, "CODE");
        public final static Property AlarmDate = new Property(2, java.util.Date.class, "alarmDate", false, "ALARM_DATE");
        public final static Property Reason = new Property(3, String.class, "reason", false, "REASON");
        public final static Property Status = new Property(4, Boolean.class, "status", false, "STATUS");
    };


    public AlarmDao(DaoConfig config) {
        super(config);
    }
    
    public AlarmDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ALARM\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CODE\" INTEGER," + // 1: code
                "\"ALARM_DATE\" INTEGER," + // 2: alarmDate
                "\"REASON\" TEXT," + // 3: reason
                "\"STATUS\" INTEGER);"); // 4: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ALARM\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Alarm entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long code = entity.getCode();
        if (code != null) {
            stmt.bindLong(2, code);
        }
 
        java.util.Date alarmDate = entity.getAlarmDate();
        if (alarmDate != null) {
            stmt.bindLong(3, alarmDate.getTime());
        }
 
        String reason = entity.getReason();
        if (reason != null) {
            stmt.bindString(4, reason);
        }
 
        Boolean status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(5, status ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Alarm readEntity(Cursor cursor, int offset) {
        Alarm entity = new Alarm( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // code
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // alarmDate
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // reason
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0 // status
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Alarm entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCode(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setAlarmDate(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setReason(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Alarm entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Alarm entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}