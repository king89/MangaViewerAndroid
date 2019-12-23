package com.king.mangaviewer.model

import com.king.mangaviewer.common.Constants.DATE_FORMAT_LONG
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

class FavouriteMangaMenuItem : MangaMenuItem, Comparable<FavouriteMangaMenuItem> {

    var favouriteDate: String = ""
        private set
    var updatedDate: String = ""
    var updateCount: Int = 0
    var chapterCount: Int = 0

    constructor(menu: MangaMenuItem, chapterCount: Int) : super(menu.id, menu.title,
        menu.description, menu.imagePath,
        menu.url, menu.mangaWebSource) {
        // TODO Auto-generated constructor stub
        favouriteDate = DateTime.now().toString(DATE_FORMAT_LONG)
        this.chapterCount = chapterCount
    }

    constructor(menu: MangaMenuItem) : this(menu, 0)

    override fun compareTo(other: FavouriteMangaMenuItem): Int {
        //if have update, then bigger
        if (this.updateCount > 0 && other.updateCount > 0 || this.updateCount == 0 && other.updateCount == 0) {
            var l: DateTime? = null
            var r: DateTime? = null
            if (this.updatedDate.isNotEmpty()) {
                l = DateTime.parse(updatedDate,
                    DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_LONG).toFormatter())
            }
            if (other.updatedDate.isNotEmpty()) {
                r = DateTime.parse(other.updatedDate,
                    DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_LONG).toFormatter())
            }
            //sort by update date
            return if (l != null && r != null) {
                l.compareTo(r)
            } else if (l == null && r == null) {
                this.title.compareTo(other.title)
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
