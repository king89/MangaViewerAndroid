package com.king.mangaviewer.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.sql.Date;
import java.text.DateFormat;

public class FavouriteMangaMenuItem extends MangaMenuItem implements Comparable<FavouriteMangaMenuItem> {

    /**
     * @param menu MangaMenuItem
     */
    private String mFavouriteDate;
    private String mUpdatedDate;
    private int mUpdateCount;
    private int mChapterCount;
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public FavouriteMangaMenuItem(final MangaMenuItem menu, int chapterCount) {
        super(menu.id, menu.title, menu.description, menu.imagePath, menu.url, menu.getMangaWebSource());
        // TODO Auto-generated constructor stub
        mFavouriteDate = DateTime.now().toString(DATE_FORMAT);
        this.mChapterCount = chapterCount;
    }

    public FavouriteMangaMenuItem(final MangaMenuItem menu) {
        super(menu.id, menu.title, menu.description, menu.imagePath, menu.url, menu.getMangaWebSource());
        // TODO Auto-generated constructor stub
        mFavouriteDate = DateTime.now().toString(DATE_FORMAT);
        this.mChapterCount = 0;
    }

    public static FavouriteMangaMenuItem createFavouriteMangaMenuItem(MangaMenuItem menu, String favouriteDate,
                                                                      String updatedDate, int chapterCount, int updateCount) {
        FavouriteMangaMenuItem item = new FavouriteMangaMenuItem(menu, chapterCount);
        item.setUpdateCount(updateCount);
        item.setUpdatedDate(updatedDate);
        item.mFavouriteDate = favouriteDate;
        return item;
    }

    @Override
    public int compareTo(FavouriteMangaMenuItem another) {
        //if have update, then bigger
        if (this.getUpdateCount() > 0 && another.getUpdateCount() > 0 || (this.getUpdateCount() == 0 && another.getUpdateCount() == 0)) {
            DateTime l = null;
            DateTime r = null;
            if (this.mUpdatedDate != null) {
                l = DateTime.parse(mUpdatedDate, new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter());
            }
            if (another.mUpdatedDate != null) {
                r = DateTime.parse(another.mUpdatedDate, new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter());
            }
            //sort by update date
            if (l != null && r != null) {
                return l.compareTo(r);
            } else if (l == null && r == null) {
                return this.getTitle().compareTo(another.getTitle());
            } else if (r == null) {
                return 1;
            } else {
                return -1;
            }
        }else if (this.getUpdateCount() > 0){
            return 1;
        }else {
            return -1;
        }
    }

    public String getFavouriteDate() {
        return mFavouriteDate;
    }

    public String getUpdatedDate() {
        return mUpdatedDate;
    }

    public void setUpdatedDate(String mUpdatedDate) {
        this.mUpdatedDate = mUpdatedDate;
    }

    public int getChapterCount() {
        return mChapterCount;
    }

    public int getUpdateCount() {
        return mUpdateCount;
    }

    public void setChapterCount(int chapterCount) {
        this.mChapterCount = chapterCount;
    }

    public void setUpdateCount(int updateCount) {
        this.mUpdateCount = updateCount;
    }
}
