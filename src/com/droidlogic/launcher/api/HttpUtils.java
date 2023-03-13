package com.droidlogic.launcher.api;

import com.droidlogic.launcher.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static String doRequest(String requestUrl) {
        String result = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                int length;
                byte[] bytes = new byte[512];
                while ((length = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, length);
                }
                result = outputStream.toString();
                Logger.i("HttpUtils", " result:" + result);
            } else {
                Logger.i("HttpUtils", "request error -->code:" + connection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Logger.i("HttpUtils", "IOException result:" + e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.i("HttpUtils", "close IOException result:" + e);
            }
        }
        return result;
    }
}
