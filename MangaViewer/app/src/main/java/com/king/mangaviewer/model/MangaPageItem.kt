package com.king.mangaviewer.model

import com.king.mangaviewer.util.StringUtils

import java.io.File

class MangaPageItem
/**
 * @param id
 * @param title
 * @param description
 * @param imagePath
 * @param url
 * @param chapter
 * @param nowNum
 * @param totalNum
 */
(id: String, title: String, description: String,
        imagePath: String, url: String, chapter: MangaChapterItem, nowNum: Int,
        totalNum: Int) : BaseItem(id, title, description, imagePath, url, chapter.mangaWebSource) {
    var chapter: MangaChapterItem? = null
    var webImageUrl = ""
    var nowNum = 0
        internal set
    var totalNum = 0
        internal set
    var referUrl = ""

    val folderPath: String
        get() = (StringUtils.getHash(this.chapter!!.menu.title) + File.separator
                + StringUtils.getHash(this.chapter!!.title))

    init {
        this.chapter = chapter
        this.nowNum = nowNum
        this.totalNum = totalNum
    }
}
