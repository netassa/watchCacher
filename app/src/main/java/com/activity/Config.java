package com.activity;

import android.os.Environment;

import java.io.File;

public class Config {
    static final String dbName = "my.db";
    static final String tableName = "data";
    static final String defaultDir = Environment.getExternalStorageDirectory() +
            File.separator + "TestLog" + File.separator;
}
