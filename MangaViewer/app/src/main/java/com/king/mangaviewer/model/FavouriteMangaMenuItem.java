package com.king.mangaviewer.model;

import org.joda.time.DateTime;

public class FavouriteMangaMenuItem extends MangaMenuItem implements Comparable<FavouriteMangaMenuItem>{

    /**
     * @param menu MangaMenuItem
     */
    private String mFavouriteDate;
    private String mUpdatedDate;
    private int mUpdateCount;
    private int mChapterCount;

    public FavouriteMangaMenuItem(final MangaMenuItem menu, int chapterCount) {
        super(menu.id, menu.title, menu.description, menu.imagePath, menu.url, menu.getMangaWebSource());
        // TODO Auto-generated constructor stub
        mFavouriteDate = DateTime.now().toString("yyyy-MM-dd hh:mm:ss");
        mUpdatedDate = mFavouriteDate;
        this.mChapterCount = chapterCount;
    }

    public static FavouriteMangaMenuItem createFavouriteMangaMenuItem(MangaMenuItem menu, String favouriteDate,
                                                                      String updatedDate, int chapterCount,int updateCount){
        FavouriteMangaMenuItem item = new FavouriteMangaMenuItem(menu,chapterCount);
        item.setUpdateCount(updateCount);
        item.setUpdatedDate(updatedDate);
        item.mFavouriteDate = favouriteDate;
        return item;
    }

    @Override
    public int compareTo(FavouriteMangaMenuItem another) {
        if (this.mUpdatedDate != null && another.mUpdatedDate != null) {
            return this.mUpdatedDate.compareTo(another.mUpdatedDate);
        }
        else {
            return this.getTitle().compareTo(another.getTitle());
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
