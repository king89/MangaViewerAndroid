package com.king.mangaviewer.adapter

import com.king.mangaviewer.model.MangaChapterItem

data class MangaChapterItemWrapper(
        val displayName: String,
        val type: Int,
        val chapter: MangaChapterItem? = null,
        val isRead: Boolean = false)

object WrapperType {
    const val CHAPTER = 0
    const val CATEGORY = 1
    const val LAST_READ = 2
}