package com.king.mangaviewer.model

import org.joda.time.DateTime

/**
 * Created by KinG on 12/23/2015.
 */
class HistoryMangaChapterItem(chapterItem: MangaChapterItem,
        val lastReadPageNum: Int = 0,
        val lastReadDate: String = DateTime.now().toString(DATE_FORMAT)) :
        MangaChapterItem(chapterItem.id, chapterItem.title, chapterItem.description,
                chapterItem.imagePath, chapterItem.url, chapterItem.menu) {

    companion object {
        val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

}
