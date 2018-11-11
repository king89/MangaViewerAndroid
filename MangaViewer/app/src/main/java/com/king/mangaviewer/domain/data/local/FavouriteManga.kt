package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.king.mangaviewer.model.FavouriteMangaMenuItem

@Entity(tableName = "favourite_manga")
class FavouriteManga(
        @PrimaryKey
        @ColumnInfo(name = "hash")
        var hash: String,

        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "description")
        var description: String,

        @ColumnInfo(name = "imagePath")
        var imagePath: String,

        @ColumnInfo(name = "url")
        var url: String,

        @ColumnInfo(name = "manga_websource_id")
        var manga_websource_id: Int,

        @ColumnInfo(name = "favourite_date")
        var favourite_date: String,

        @ColumnInfo(name = "updated_date")
        var updated_date: String,

        @ColumnInfo(name = "update_count")
        var update_count: Int,

        @ColumnInfo(name = "chapter_count")
        var chapter_count: Int

) {
    constructor(mangaItem: FavouriteMangaMenuItem) : this(
            mangaItem.hash,
            mangaItem.title,
            mangaItem.description,
            mangaItem.imagePath,
            mangaItem.url,
            mangaItem.mangaWebSource.id,
            mangaItem.favouriteDate,
            mangaItem.updatedDate,
            mangaItem.updateCount,
            mangaItem.chapterCount
    )
}
