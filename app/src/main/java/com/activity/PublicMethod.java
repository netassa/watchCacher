package com.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class PublicMethod {
    private static final String TAG = "PublicMethod";
    private static String defaultFileName = Config.defaultDir;
    private static Format format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINESE);
    public static void writeToExcel(String[][] str) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        WritableWorkbook book;
        try {
            book = Workbook.createWorkbook(new File(defaultFileName + format.format(new Date()) + "_IMEI.xls"));
            WritableSheet sheet = book.createSheet("绑定号与IMEI",0);
            String[] titles = {"机型", "编号", "绑定号", "IMEI"};
            for(int i = 0; i < titles.length; i++) {
                Label label = new Label(i, 0, titles[i]);
                sheet.addCell(label);
            }
            for(int k = 0; k < str.length; k++) {
                for(int j = 0; j < str[k].length; j++) {
                    Label label = new Label(j, k, str[k][j]);
                    sheet.addCell(label);
                }
            }
            book.write();
            book.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public static File getSavePhotoFile() {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        String photoPath = Config.defaultDir + "/camera/" + format.format(new Date()) + ".jpg";
        File file = new File(photoPath);
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public static String getPreferenceString(Context context, String name) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        SharedPreferences preferences = context.getSharedPreferences("codeScan", Context.MODE_PRIVATE);
        return preferences.getString(name, null);
    }

    public static int  getPreferenceInt(Context context, String name) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        SharedPreferences preferences = context.getSharedPreferences("codeScan", Context.MODE_PRIVATE);
        if(name.equals("tempNum")) {
            return preferences.getInt(name, 1);
        }
        return preferences.getInt(name, 0);
    }

    public static void writePreferenceString(Context context, String name, String value) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        SharedPreferences preferences = context.getSharedPreferences("codeScan", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        editor.commit();
    }
    public static void writePreferenceInt(Context context, String name, int value) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + ", name:" + name + ", value" + value);
        SharedPreferences preferences = context.getSharedPreferences("codeScan", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void disableAPIDialog() {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        if (Build.VERSION.SDK_INT < 28) return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
