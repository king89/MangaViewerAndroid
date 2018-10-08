package com.king.mangaviewer.util;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by liang on 10/26/2016.
 */

public class NetworkHelper {
    private static final int HTTP_TIMEOUT_NUM = 30000;

    public static InputStream downLoadFromUrl(String fileUrl, String refer) throws IOException {
        URL url = null;
        try {

            String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
            url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HTTP_TIMEOUT_NUM);
            conn.setReadTimeout(HTTP_TIMEOUT_NUM);
            conn.setDoInput(true);
            conn.setRequestProperty("Referer", refer);
            conn.setRequestProperty("User-Agent", UserAgent);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            return inputStream;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("NetworkHelper", fileUrl);
            throw e;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("NetworkHelper", fileUrl);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("NetworkHelper", fileUrl);
            throw e;
        }

    }
}
