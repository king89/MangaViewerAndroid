package com.king.mangaviewer.model;

import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.Date;

public class FavouriteMangaMenuItem extends MangaMenuItem implements Comparable<FavouriteMangaMenuItem>{

    /**
     * @param menu MangaMenuItem
     */
    private String favouriteDate;

    public FavouriteMangaMenuItem(final MangaMenuItem menu) {
        super(menu.id, menu.title, menu.description, menu.imagePath, menu.url, menu.getMangaWebSource());
        // TODO Auto-generated constructor stub
        favouriteDate = DateTime.now().toString("yyyy-MM-dd hh:mm:ss");
    }

    @Override
    public int compareTo(FavouriteMangaMenuItem another) {
        return this.favouriteDate.compareTo(another.favouriteDate);
    }
}
