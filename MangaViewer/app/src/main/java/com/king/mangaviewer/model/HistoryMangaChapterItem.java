package com.king.mangaviewer.model;

import org.joda.time.DateTime;

/**
 * Created by KinG on 12/23/2015.
 */
public class HistoryMangaChapterItem extends MangaChapterItem {
    private String mLastReadDate;
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public HistoryMangaChapterItem(MangaChapterItem chapterItem){
        super(chapterItem.getId(),chapterItem.getTitle(),chapterItem.getDescription(),chapterItem.getImagePath(),chapterItem.getUrl(),chapterItem.getMenu());
        mLastReadDate = DateTime.now().toString(DATE_FORMAT);
    }

    public String getLastReadDate(){
        return mLastReadDate;
    }

}
