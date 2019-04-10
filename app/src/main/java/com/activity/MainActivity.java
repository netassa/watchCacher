package com.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.qrcodescan.R;
import com.utils.CommonUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.openQrCodeScan)
    Button openQrCodeScan;
    @BindView(R.id.qrCodeText)
    TextView qrCodeText;
    @BindView(R.id.imeiText)
    TextView imeiText;
    @BindView(R.id.deviceText)
    TextView deviceTesxt;

    //打开扫描界面请求码
    private static final int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;


    private static final String TAG = "MainActivity";
//    @BindView(R.id.imageView)
//    ImageView imageView;
    private SQLiteDatabase database;
    private MyDBOpenHelper myDBOpenHelper;
    private Context context;
    private static final int REQUST_CODE_TAKE_PICTURE_URI = 2;
    Uri photoUri;
    File file;
    int tempNum;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PublicMethod.disableAPIDialog();
        ButterKnife.bind(this);
        context = MainActivity.this;
        myDBOpenHelper = new MyDBOpenHelper(context);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        SqlMethod.setRowNum(PublicMethod.getPreferenceInt(context, "rowNum"));
        tempNum = PublicMethod.getPreferenceInt(context, "tempNum");
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @OnClick({R.id.openQrCodeScan,R.id.export, R.id.insert, R.id.delete, R.id.delete_all,
            R.id.preview})//, R.id.takePhoto,R.id.getCount, R.id.query})
    public void onClick(View view) {
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        database = myDBOpenHelper.getWritableDatabase();
        switch (view.getId()) {
            case R.id.openQrCodeScan:
                //打开二维码扫描界面
                LogUtils.i(TAG, "openQrCodeScan");
                if(CommonUtil.isCameraCanUse()){
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }else{
                    Toast.makeText(this,"请打开此应用的摄像头权限！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.insert:
                LogUtils.i(TAG, "insert");
                String IMEI = imeiText.getText().toString();
                String device = deviceTesxt.getText().toString();
                String watchId = qrCodeText.getText().toString();
                String num =  tempNum + "#";
                LogUtils.i(TAG, "IMEI:" + IMEI + ", device:" + device + ", watchId:" + ", num:" + num );
                String[] str = {device, num, watchId, IMEI};
                if(device.isEmpty()) {
                    Toast.makeText(context, "机型为空", Toast.LENGTH_LONG).show();
                } else if(watchId.equals(getText(R.string.qrcodetext) + "")) {
                    Toast.makeText(context, "绑定号为空", Toast.LENGTH_LONG).show();
                } else if(IMEI.isEmpty()) {
                    Toast.makeText(context, "IMEI为空", Toast.LENGTH_LONG).show();
                } else {
                    tempNum++;
                    SqlMethod.insert(context, database, Config.tableName, str);
                    PublicMethod.writePreferenceInt(context, "tempNum", tempNum);
                    Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.export:
                LogUtils.i(TAG, "export");
                long total = SqlMethod.getCount(database, Config.tableName);
                if(total ==  0) {
                    Toast.makeText(context, "无数据", Toast.LENGTH_LONG).show();
                } else {
                    String dataStr[][] = SqlMethod.queryAll(database, Config.tableName);
                    PublicMethod.writeToExcel(dataStr);
                    Toast.makeText(context, "导出成功", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.delete:
                LogUtils.i(TAG, "delete");
                int deleteId = SqlMethod.getRowNum();
                if (deleteId > 0) {
                    SqlMethod.delete(context, database, Config.tableName, deleteId);
                    tempNum--;
                    PublicMethod.writePreferenceInt(context, "tempNum", tempNum);
                    Toast.makeText(context, "删除完毕", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "无数据", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.delete_all:
                LogUtils.i(TAG, "delete_all");
                if (SqlMethod.getRowNum() > 0) {
                    tempNum = 1;
                    PublicMethod.writePreferenceInt(context, "tempNum", tempNum);
                    SqlMethod.deleteAll(context, database, Config.tableName);
                    Toast.makeText(context, "已清空", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "无数据", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.preview:
                LogUtils.i(TAG, "preview");
                Intent intent = new Intent(MainActivity.this, Preview.class);
                startActivity(intent);
                break;
//            case R.id.takePhoto:
//                LogUtils.i(TAG, "takePhoto");
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                file = PublicMethod.getSavePhotoFile();
//                photoUri = Uri.fromFile(file);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                startActivityForResult(cameraIntent, REQUST_CODE_TAKE_PICTURE_URI);
//                break;
//            case R.id.query:
//                LogUtils.i(TAG, "query");
//                StringBuilder stringBuilder = new StringBuilder();
//                int id = SqlMethod.getRowNum();
//                LogUtils.i(TAG, String.valueOf(id));
//                if (id > 0) {
//                    String[] arr = SqlMethod.query(database, Config.tableName, id);
//                    for (String value : arr) {
//                        stringBuilder.append(value + "\t");
//                    }
//                    Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(context, "无数据", Toast.LENGTH_LONG).show();
//                }
//                break;
//            case R.id.getCount:
//                LogUtils.i(TAG, "getCount");
//                long count = SqlMethod.getCount(database, Config.tableName);
//                Toast.makeText(context, Long.toString(count), Toast.LENGTH_LONG).show();
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        Bitmap bitmap;
        switch (requestCode) {
            case REQUEST_CODE:
                //扫描结果回调
                LogUtils.i(TAG, "REQUEST_CODE:" + REQUEST_CODE);
                if (resultCode == RESULT_OK) { //RESULT_OK = -1
                    LogUtils.i(TAG, "RESULT_OK");
                    final Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("qr_scan_result");
                    //将扫描出的信息显示出来
                    String temp[] = scanResult.split("/");
                    final String watchId = temp[temp.length - 1];
                    qrCodeText.setText(watchId);
                    new Thread() {
                        @Override
                        public void run() {
                            String url = "http://watch.module.okii.com/smartwatch/watchaccount/bindnumber/" + watchId;
                            LogUtils.i(TAG, url);
                            response = GetImei.sendGet(url, watchId);
                            Message message = new Message();
                            message.what = 0x1;
                            Bundle bundle1 = new Bundle();
                            bundle.putString("response", response);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }.start();
                }
                break;
//            case REQUST_CODE_TAKE_PICTURE_URI:
//                LogUtils.i(TAG, "REQUEST_CODE_TAKE_PICTURE_URI:" + REQUST_CODE_TAKE_PICTURE_URI);
//                ContentResolver resolver = getContentResolver();
//                try {
//                    bitmap = BitmapFactory.decodeStream(resolver.openInputStream(photoUri));
//                    imageView.setImageBitmap(bitmap);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
        }
    }
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            LogUtils.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
            switch (message.what) {
                case 0x1:
                    String response = message.getData().getString("response");
                    LogUtils.i(TAG, response);
                    String imei = "";
                    String device = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String data = jsonObject.getString("data");
                        JSONObject jsonObject1 = new JSONObject(data);
                        imei = jsonObject1.getString("imei");
                        LogUtils.i(TAG, "imei:" + imei);
                        device = jsonObject1.getString("model");
                        LogUtils.i(TAG, "device:" + device);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    imeiText.setText(imei);
                    deviceTesxt.setText(device);
                    break;
            }
        }

    };
}



