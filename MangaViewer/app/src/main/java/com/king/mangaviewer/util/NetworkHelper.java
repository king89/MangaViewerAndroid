package com.king.mangaviewer.util;

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

            String UserAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5";
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
            throw e;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
