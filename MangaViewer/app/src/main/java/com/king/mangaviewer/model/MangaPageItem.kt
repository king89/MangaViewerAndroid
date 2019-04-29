package com.king.mangaviewer.model

import com.king.mangaviewer.util.StringUtils

import java.io.File
import java.lang.Exception

data class MangaPageItem(
    var id: String,
    var title: String,
    var description: String,
    var imagePath: String,
    var url: String,
    var chapter: MangaChapterItem? = null,
    var nowNum: Int = 0,
    var totalNum: Int = 0) {

    var webImageUrl = ""
    var referUrl = ""

    val mangaWebSource: MangaWebSource
        get() = chapter?.mangaWebSource ?: throw Exception("No manga web source")

    val folderPath: String
        get() = (StringUtils.getHash(this.chapter!!.menu.title) + File.separator
            + StringUtils.getHash(this.chapter!!.title))
}
