package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "downloaded_manga")
data class DownloadedManga(
    @PrimaryKey
    @ColumnInfo(name = "hash")
    var hash: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "created_date_time")
    var createdDateTime: String,

    @ColumnInfo(name = "origin_hash")
    var originHash: String,

    @Embedded(prefix = "menu_")
    var menu: DownloadedMangaMenu

)


class DownloadedMangaMenu(
    @ColumnInfo(name = "hash")
    var hash: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "cover_image_base64")
    var coverImageBase64: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "genre")
    var genre: String,

    @ColumnInfo(name = "origin_hash")
    var originHash: String
)