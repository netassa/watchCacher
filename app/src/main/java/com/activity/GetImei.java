package com.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GetImei {
    private static final String TAG = "GetImei";

    public static String sendGet(String url, String watchId) {
        String result = "";
        String urlName = url;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(urlName);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            String temp = "{\"accountId\":\"16cbb0ff2e934992b420195276bc8833\",\"appId\":\"2\",\"deviceId\":\"" + watchId + "\",\"imFlag\":\"1\",\"mac\":\"14:9f:b6:f3:21:38\",\"program\":\"watch\",\"registId\":0,\"timestamp\":\"2018-12-05 14:43:28\",\"token\":\"00000070000001002a060c9900000005\"}";
            LogUtils.i(TAG, temp);
            connection.setRequestProperty("Base-Request-Param", temp);
            connection.setRequestProperty("Eebbk-Sign", "9BC3EDFB13FE064731FFBCE94BABADDA");
            connection.setRequestProperty("Version", "W_2.80");
            connection.setRequestProperty("grey", "619386");
            connection.setRequestProperty("cache-control", "no-cache");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
