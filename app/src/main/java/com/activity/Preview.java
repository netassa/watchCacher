package com.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Preview extends AppCompatActivity {
    @BindView(R.id.table)
    SmartTable table;
    private SQLiteDatabase database;
    private MyDBOpenHelper myDBOpenHelper;
    private Context context;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        List<Data> list = new ArrayList<>();
        context = getApplicationContext();
        myDBOpenHelper = new MyDBOpenHelper(context);
        database = myDBOpenHelper.getReadableDatabase();
        String[][] str = SqlMethod.queryAll(database, Config.tableName);
        for(int i = 0; i < str.length; i++) {
            list.add(new Data(str[i][0], str[i][1], str[i][2], str[i][3]));
        }
        table.setData(list);
        table.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));
    }
}
