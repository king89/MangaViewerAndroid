package com.king.mangaviewer.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class StringUtils {

    public static String inputStreamToString2(final InputStream stream, String charset) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, charset));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }

    public static String inputStreamToString(final InputStream stream, String charset) throws IOException {
        StringBuilder sBuilder = new StringBuilder();
        byte[] data = new byte[1024];
        while (stream.read(data) != -1) {
            sBuilder.append(new String(data, Charset.forName(charset)));
        }

        return sBuilder.toString();
    }

}
