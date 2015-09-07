package com.king.mangaviewer.model;

import org.joda.time.DateTime;

public class FavouriteMangaMenuItem extends MangaMenuItem implements Comparable<FavouriteMangaMenuItem>{

    /**
     * @param menu MangaMenuItem
     */
    private String mFavouriteDate;
    private int mUpdateCount;
    private int mChapterCount;

    public FavouriteMangaMenuItem(final MangaMenuItem menu, int chapterCount) {
        super(menu.id, menu.title, menu.description, menu.imagePath, menu.url, menu.getMangaWebSource());
        // TODO Auto-generated constructor stub
        mFavouriteDate = DateTime.now().toString("yyyy-MM-dd hh:mm:ss");
        this.mChapterCount = chapterCount;
    }

    @Override
    public int compareTo(FavouriteMangaMenuItem another) {
        if (this.mFavouriteDate != null && another.mFavouriteDate != null) {
            return this.mFavouriteDate.compareTo(another.mFavouriteDate);
        }
        else {
            return this.getTitle().compareTo(another.getTitle());
        }
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
