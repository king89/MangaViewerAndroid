package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "downloaded_manga")
data class DownloadedManga(
    @ColumnInfo(name = "menu_hash")
    var menuHash: String,

    @ColumnInfo(name = "chapter_hash")
    var chapterHash: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "chapter_name")
    var chapterName: String,

    @ColumnInfo(name = "menu_cover_image_base64")
    var menuCoverImageBase64: String,

    @ColumnInfo(name = "menu_url")
    var menuUrl: String,

    @ColumnInfo(name = "chapter_url")
    var chapterUrl: String,

    @ColumnInfo(name = "genre")
    var genre: String

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

}
