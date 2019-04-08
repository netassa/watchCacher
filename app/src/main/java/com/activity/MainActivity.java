package com.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.qrcodescan.R;
import com.utils.CommonUtil;

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

    //打开扫描界面请求码
    private static final int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;


    private static final String TAG = "MainActivity";
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.device)
    EditText deviceText;
    private SQLiteDatabase database;
    private MyDBOpenHelper myDBOpenHelper;
    private Context context;
    private static final int REQUST_CODE_TAKE_PICTURE_URI = 2;
    Uri photoUri;
    File file;
    int tempNum;

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
    }

    @OnClick({R.id.openQrCodeScan,R.id.export, R.id.insert, R.id.getCount, R.id.query, R.id.delete, R.id.delete_all,
            R.id.preview, R.id.takePhoto})
    public void onClick(View view) {
        database = myDBOpenHelper.getWritableDatabase();
        switch (view.getId()) {
            case R.id.openQrCodeScan:
                //打开二维码扫描界面
                if(CommonUtil.isCameraCanUse()){
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }else{
                    Toast.makeText(this,"请打开此应用的摄像头权限！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.insert:
                String device = deviceText.getText() + "";
                String watchId = qrCodeText.getText() + "";
                String IMEI = "jfldjfld";
                String num =  tempNum + "#";
                String[] str = {device, num, watchId, IMEI};
                LogUtils.i(TAG, "Test");
                if(device.isEmpty()) {
                    Toast.makeText(context, "请输入机型", Toast.LENGTH_LONG).show();
                } else if(watchId.equals(getText(R.string.qrcodetext) + "")) {
                    Toast.makeText(context, "绑定号为空", Toast.LENGTH_LONG).show();
                } else if(IMEI.isEmpty()) {
                    Toast.makeText(context, "IMEI为空", Toast.LENGTH_LONG).show();
                } else {
                    tempNum++;
                    SqlMethod.insert(context, database, Config.tableName, str);
                    PublicMethod.writePreferenceInt(context, "tempNum", tempNum);
                    Toast.makeText(context, "插入完毕", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.query:
                StringBuilder stringBuilder = new StringBuilder();
                int id = SqlMethod.getRowNum();
                LogUtils.i(TAG, String.valueOf(id));
                if (id > 0) {
                    String[] arr = SqlMethod.query(database, Config.tableName, id);
                    for (String value : arr) {
                        stringBuilder.append(value + "\t");
                    }
                    Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "无数据", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.export:
                LogUtils.i(TAG, "query");
                long total = SqlMethod.getCount(database, Config.tableName);
                if(total ==  0) {
                    Toast.makeText(context, "无数据", Toast.LENGTH_LONG).show();
                } else {
                    String dataStr[][] = SqlMethod.queryAll(database, Config.tableName);
                    PublicMethod.writeToExcel(dataStr);
                    Toast.makeText(context, "导出成功", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.getCount:
                long count = SqlMethod.getCount(database, Config.tableName);
                Toast.makeText(context, Long.toString(count), Toast.LENGTH_LONG).show();
                break;
            case R.id.delete:
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
                tempNum = 1;
                PublicMethod.writePreferenceInt(context, "tempNum", tempNum);
                SqlMethod.deleteAll(context, database, Config.tableName);
                break;
            case R.id.preview:
                Intent intent = new Intent(MainActivity.this, Preview.class);
                startActivity(intent);
                break;
            case R.id.takePhoto:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = PublicMethod.getSavePhotoFile();
                photoUri = Uri.fromFile(file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, REQUST_CODE_TAKE_PICTURE_URI);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        switch (requestCode) {
            case REQUEST_CODE:
                //扫描结果回调
                if (resultCode == RESULT_OK) { //RESULT_OK = -1
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("qr_scan_result");
                    //将扫描出的信息显示出来
                    String temp[] = scanResult.split("/");
                    qrCodeText.setText(temp[temp.length - 1]);
                }
                break;
            case REQUST_CODE_TAKE_PICTURE_URI:
                ContentResolver resolver = getContentResolver();
                try {
                    bitmap = BitmapFactory.decodeStream(resolver.openInputStream(photoUri));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
