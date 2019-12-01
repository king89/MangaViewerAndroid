package com.king.mangaviewer.adapter

import com.king.mangaviewer.domain.data.local.DownloadState
import com.king.mangaviewer.domain.data.local.DownloadState.NONE


data class MangaChapterStateItem(
    val downloaded: DownloadState = NONE,
    val isRead: Boolean = false)
