package com.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDBOpenHelper";

    MyDBOpenHelper(Context context) {
        super(context, Config.dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Config.tableName + "(id INTEGER PRIMARY KEY AUTOINCREMENT, rowNum INTEGER, device VARCHAR(10) DEFAULT 'Z5', " +
                "num VARCHAR(10), watchId VARCHAR(20), IMEI VARCHAR(20))";
        LogUtils.i(TAG, "create");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
