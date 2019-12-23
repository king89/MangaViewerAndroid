package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.MangaChapterItem

data class DownloadTask(val chapter: MangaChapterItem)
data class DownloadTaskStateItem(
    val menuHash: String,
    val chapterHash: String,
    var downLoadState: DownloadState)

enum class DownloadState {
    PENDING,
    PAUSE,
    DOWNLOADING,
    FINISHED,
    ERROR,
    NONE
}