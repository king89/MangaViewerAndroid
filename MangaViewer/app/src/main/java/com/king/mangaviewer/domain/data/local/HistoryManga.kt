package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

class HistoryMangaMenu(
        @PrimaryKey
        @ColumnInfo(name = "hash")
        var hash: String,

        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "description")
        var description: String,

        @ColumnInfo(name = "image_path")
        var imagePath: String,

        @ColumnInfo(name = "url")
        var url: String
)

@Entity(tableName = "history_manga")
class HistoryManga(
        @PrimaryKey
        @ColumnInfo(name = "hash")
        var hash: String,

        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "description")
        var description: String,

        @ColumnInfo(name = "image_path")
        var imagePath: String,

        @ColumnInfo(name = "url")
        var url: String,

        @ColumnInfo(name = "manga_websource_id")
        var manga_websource_id: Int,

        @ColumnInfo(name = "last_read_time")
        var favourite_date: String,

        @Embedded(prefix = "menu_")
        var menu: HistoryMangaMenu


)