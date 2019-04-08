package com.activity;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {
    private static String defaultFileName = Config.defaultDir + "test.txt";
    private static final String TAG = "TestLog";
    private static Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);

    public static void v(String TAG, String message) {
        print("verbose", TAG, message);
    }

    public static void d(String TAG, String message) {
        print("debug", TAG, message);
    }

    public static void i(String TAG, String message) {
        print("info", TAG, message);
    }

    public static void w(String TAG, String message) {
        print("warn", TAG, message);
    }

    public static void e(String TAG, String message) {
        print("error", TAG, message);
    }

    private static void print(String level, String TAG, String message) {
        PrintStream out = System.out;
        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(defaultFileName,true));
            System.setOut(printStream);
            System.out.println(format.format(new Date()) + ":" + message);
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        switch (level) {
            case "verbose":
                Log.v(TAG, message);
                break;
            case "debug":
                Log.d(TAG, message);
                break;
            case "info":
                Log.i(TAG, message);
                break;
            case "warn":
                Log.w(TAG, message);
                break;
            case "error":
                Log.e(TAG, message);
                break;
            default:
                Log.i(TAG, message);
                break;
        }
    }

}
