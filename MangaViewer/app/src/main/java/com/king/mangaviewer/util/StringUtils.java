package com.king.mangaviewer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String getHash(String tx) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(tx.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

}
