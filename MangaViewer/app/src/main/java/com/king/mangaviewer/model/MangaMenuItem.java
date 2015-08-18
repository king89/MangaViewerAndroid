package com.king.mangaviewer.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MangaMenuItem extends BaseItem {

    /**
     * @param id
     * @param title
     * @param description
     * @param imagePath
     */
    public MangaMenuItem(String id, String title, String description,
                         String imagePath, String url, MangaWebSource mangaWebSource) {
        super(id, title, description, imagePath, url, mangaWebSource);
        // TODO Auto-generated constructor stub
    }

    public String getHash() {
        String tx = this.getMangaWebSource().getClassName() + "|" + this.getUrl();
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
