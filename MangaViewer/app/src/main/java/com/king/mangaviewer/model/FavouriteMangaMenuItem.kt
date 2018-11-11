package com.king.mangaviewer.model

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder

import java.sql.Date
import java.text.DateFormat

class FavouriteMangaMenuItem : MangaMenuItem, Comparable<FavouriteMangaMenuItem> {

    /**
     * @param menu MangaMenuItem
     */
    var favouriteDate: String = ""
        private set
    var updatedDate: String = ""
    var updateCount: Int = 0
    var chapterCount: Int = 0

    constructor(menu: MangaMenuItem, chapterCount: Int) : super(menu.id, menu.title,
            menu.description, menu.imagePath,
            menu.url, menu.mangaWebSource) {
        // TODO Auto-generated constructor stub
        favouriteDate = DateTime.now().toString(DATE_FORMAT)
        this.chapterCount = chapterCount
    }

    constructor(menu: MangaMenuItem) : super(menu.id, menu.title, menu.description, menu.imagePath,
            menu.url, menu.mangaWebSource) {
        // TODO Auto-generated constructor stub
        favouriteDate = DateTime.now().toString(DATE_FORMAT)
        this.chapterCount = 0
    }

    override fun compareTo(another: FavouriteMangaMenuItem): Int {
        //if have update, then bigger
        if (this.updateCount > 0 && another.updateCount > 0 || this.updateCount == 0 && another.updateCount == 0) {
            var l: DateTime? = null
            var r: DateTime? = null
            if (this.updatedDate.isNotEmpty()) {
                l = DateTime.parse(updatedDate,
                        DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter())
            }
            if (another.updatedDate.isNotEmpty()) {
                r = DateTime.parse(another.updatedDate,
                        DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter())
            }
            //sort by update date
            return if (l != null && r != null) {
                l.compareTo(r)
            } else if (l == null && r == null) {
                this.title.compareTo(another.title)
            } else if (r == null) {
                1
            } else {
                -1
            }
        } else return if (this.updateCount > 0) {
            1
        } else {
            -1
        }
    }

    companion object {
        val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

        fun createFavouriteMangaMenuItem(menu: MangaMenuItem, favouriteDate: String,
                updatedDate: String, chapterCount: Int, updateCount: Int): FavouriteMangaMenuItem {
            val item = FavouriteMangaMenuItem(menu, chapterCount)
            item.updateCount = updateCount
            item.updatedDate = updatedDate
            item.favouriteDate = favouriteDate
            return item
        }
    }
}
