package com.king.mangaviewer.model;

import com.king.mangaviewer.common.util.StringUtils;

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
        return StringUtils.getHash(tx);
    }

}
