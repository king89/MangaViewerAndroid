package com.king.mangaviewer.domain.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

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
        var last_read_time: String,

        @ColumnInfo(name = "last_read_page_num")
        var last_read_page_num: Int,

        @Embedded(prefix = "menu_")
        var menu: HistoryMangaMenu


)