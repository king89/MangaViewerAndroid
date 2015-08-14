package com.king.mangaviewer.model;

import org.joda.time.DateTime;

import java.util.Date;

public class FavouriteMangaMenuItem extends MangaMenuItem {

    /**
     * @param menu MangaMenuItem
     */
    private DateTime favouriteDate;

    public FavouriteMangaMenuItem(MangaMenuItem menu) {
        super(menu.id, menu.title, menu.description, menu.imagePath, menu.url, menu.getMangaWebSource());
        // TODO Auto-generated constructor stub
        favouriteDate = DateTime.now();
    }

    public String getHash() {
        String tx = this.getMangaWebSource().getId() + "|" + this.getUrl();
        return tx;
    }

}
