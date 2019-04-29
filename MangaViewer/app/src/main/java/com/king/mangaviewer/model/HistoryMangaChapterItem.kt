package com.king.mangaviewer.model

import com.king.mangaviewer.common.Constants.DATE_FORMAT_LONG
import com.king.mangaviewer.util.StringUtils
import org.joda.time.DateTime

/**
 * Created by KinG on 12/23/2015.
 */
class HistoryMangaChapterItem(chapterItem: MangaChapterItem,
        val lastReadPageNum: Int = 0,
        val lastReadDate: String = DateTime.now().toString(DATE_FORMAT_LONG)) :
        MangaChapterItem(chapterItem.id, chapterItem.title, chapterItem.description,
                chapterItem.imagePath, chapterItem.url, chapterItem.menu) {

}
