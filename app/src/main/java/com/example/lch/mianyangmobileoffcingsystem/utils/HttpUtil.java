package com.example.lch.mianyangmobileoffcingsystem.utils;

import com.example.lch.mianyangmobileoffcingsystem.interfaces.HttpCallbackListener;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lch on 2017/3/10.
 */

public class HttpUtil {

    public static void sendHttpClientRequest(final String[] values, final String address, final HttpCallbackListener callbackListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //String value = "1234";
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("post");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    //传数据
//                    OutputStream outputStream = connection.getOutputStream();
//                    outputStream.write(value.getBytes());
//                    outputStream.flush();
                    //接收数据
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line=bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    if (callbackListener != null) {
                        callbackListener.onSuccess(response.toString());
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    if (callbackListener!=null) {
                        callbackListener.onFailed(e);
                    }
                }finally {
                    if (connection!=null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
