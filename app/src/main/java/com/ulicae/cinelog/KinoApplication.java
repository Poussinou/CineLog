package com.ulicae.cinelog;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.ulicae.cinelog.dao.DaoMaster;
import com.ulicae.cinelog.dao.DaoSession;

/**
 * Created by ryan on 12/05/17.
 */

public class KinoApplication extends Application {

    DaoMaster.DevOpenHelper helper;
    SQLiteDatabase db;
    DaoMaster daoMaster;
    DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        // Required initialization logic here!
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}