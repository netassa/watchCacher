package com.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlMethod {
    private static int rowNum;
    private static final String TAG = "SqlMethod";

    public static void setRowNum(int rowNum) {
        SqlMethod.rowNum = rowNum;
    }

    public static int getRowNum() {
        return rowNum;
    }

    public static void insert(Context context, SQLiteDatabase database, String table, String[] str) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        rowNum = PublicMethod.getPreferenceInt(context, "rowNum");
        rowNum++;
        PublicMethod.writePreferenceInt(context, "rowNum", rowNum);
        ContentValues values = new ContentValues();
        values.put("rowNum", rowNum);
        values.put("device", str[0]);
        values.put("num", str[1]);
        values.put("watchId", str[2]);
        values.put("IMEI", str[3]);
        LogUtils.i(TAG, "insert");
        LogUtils.i(TAG, str[0]);
        database.insert(table, null, values);
        LogUtils.i(TAG, Integer.toString(rowNum));
    }

    public static String[] query(SQLiteDatabase database, String table, int rowNum) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        LogUtils.i(TAG, "query");
        Cursor cursor = database.query(table, null, "rowNum=?",
                new String[]{Integer.toString(rowNum)}, null, null, null);
        if(cursor.moveToFirst()) {
            int pid = cursor.getInt(cursor.getColumnIndex("rowNum"));
            String device = cursor.getString(cursor.getColumnIndex("device"));
            String num = cursor.getString(cursor.getColumnIndex("num"));
            String watchId = cursor.getString(cursor.getColumnIndex("watchId"));
            String IMEI = cursor.getString(cursor.getColumnIndex("IMEI"));
            cursor.close();
            return new String[]{Integer.toString(pid), device, num, watchId, IMEI};
        }
        return new String[]{};
    }

    public static String[][] queryAll(SQLiteDatabase database, String table) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        Cursor cursor = database.query(table, null, null,
                null, null, null, null);
        int count = (int)getCount(database, table);
        LogUtils.i(TAG, "count:" + count);
        String[][] str = new String[count][4];
        if (cursor.moveToFirst()) {
            for(int i = 0; i < count; i++) {
                str[i][0] = cursor.getString(cursor.getColumnIndex("device"));
                str[i][1] = cursor.getString(cursor.getColumnIndex("num"));
                str[i][2] = cursor.getString(cursor.getColumnIndex("watchId"));
                str[i][3] = cursor.getString(cursor.getColumnIndex("IMEI"));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return str;
    }

    public static void delete(Context context, SQLiteDatabase database, String table, int rowNum) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        String sql = "DELETE FROM " + table + " WHERE rowNum = " + rowNum;
        SqlMethod.rowNum--;
        LogUtils.i(TAG, "rowNum:" + SqlMethod.rowNum);
        PublicMethod.writePreferenceInt(context, "rowNum", SqlMethod.rowNum);
        database.execSQL(sql);
    }

    public static long getCount(SQLiteDatabase database, String table) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        String sql = "SELECT COUNT (*) FROM " + table;
        Cursor cursor =  database.rawQuery("SELECT COUNT (*) FROM " + table,null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        return result;
    }

    public static void deleteAll(Context context, SQLiteDatabase database, String table) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        String sql = "DELETE FROM " + table;
        database.execSQL(sql);
        rowNum = 0;
        PublicMethod.writePreferenceInt(context, "rowNum", rowNum);
    }
}
