package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import org.joda.time.DateTime

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
) {
    companion object {
        fun createFromTask(downloadTask: DownloadTask, folderPath: String,
            fileUri: String): DownloadedManga {
            val originMenu = downloadTask.chapter.menu

            val menu = with(originMenu) {
                MangaMenuItem(id, title, description, imagePath, folderPath,
                    MangaWebSource.DOWNLOAD)
            }

            val originChapter = downloadTask.chapter
            val chapter = with(originChapter) {
                MangaChapterItem(id, title, description, imagePath, fileUri, menu)
            }

            val downloadMenu = with(menu) {
                DownloadedMangaMenu(hash, title, description, imagePath, url, "", originMenu.hash)
            }
            val downloadChapter = with(chapter) {
                DownloadedManga(hash, description, title, url,
                    DateTime.now().toString(),
                    originChapter.hash,
                    downloadMenu)
            }

            return downloadChapter
        }
    }
}

data class DownloadedMangaMenu(
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