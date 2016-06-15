package com.apaza.moises.notevoice.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.apaza.moises.notevoice.global.ContractNoteVoice;
import com.apaza.moises.notevoice.database.DaoMaster;
import com.apaza.moises.notevoice.database.DaoSession;

public class HandlerDB {
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private static HandlerDB handlerDB;

    private HandlerDB(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, ContractNoteVoice.NAME_DB, null);
        try{
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static HandlerDB getInstance(Context context){
        if(handlerDB == null)
            handlerDB = new HandlerDB(context);
        return handlerDB;
    }

    public DaoSession getDaoSession(){
        if (daoSession == null)
            daoSession = daoMaster.newSession();
        return daoSession;
    }
}
