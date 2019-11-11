package com.king.mangaviewer.adapter

import com.king.mangaviewer.adapter.DownloadState.None

data class MangaChapterStateItem(
    val downloaded: DownloadState = None,
    val isRead: Boolean = false)

enum class DownloadState {
    Downloaded,
    Pending,
    None
}