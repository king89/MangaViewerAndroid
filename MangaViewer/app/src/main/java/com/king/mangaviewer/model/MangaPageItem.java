package com.king.mangaviewer.model;

import java.io.File;

public class MangaPageItem extends BaseItem {
    MangaChapterItem chapter = null;
    String webImageUrl = "";
    double imageHeight = 0;
    double imageWidth = 0;
    Boolean isLoaded = false;
    int nowNum = 0;
    int totalNum = 0;

    /**
     * @param id
     * @param title
     * @param description
     * @param imagePath
     * @param url
     * @param chapter
     * @param nowNum
     * @param totalNum
     */
    public MangaPageItem(String id, String title, String description,
                         String imagePath, String url, MangaChapterItem chapter, int nowNum,
                         int totalNum) {
        super(id, title, description, imagePath, url);
        this.chapter = chapter;
        this.nowNum = nowNum;
        this.totalNum = totalNum;
    }

    public String getFolderPath() {
        return this.getChapter().getMenu().getTitle().getBytes().toString() + File.separator + this.getChapter().getTitle().getBytes().toString();
    }


    public MangaChapterItem getChapter() {
        return chapter;
    }


    public void setChapter(MangaChapterItem chapter) {
        this.chapter = chapter;
    }

    public String getWebImageUrl() {
        return webImageUrl;
    }

    public void setWebImageUrl(String webImageUrl) {
        this.webImageUrl = webImageUrl;
    }

    public int getNowNum() {
        return nowNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

}
