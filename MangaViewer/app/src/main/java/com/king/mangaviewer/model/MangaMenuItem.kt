package com.king.mangaviewer.model

import com.king.mangaviewer.util.StringUtils

open class MangaMenuItem(
    var id: String,
    var title: String,
    var description: String,
    var imagePath: String,
    var url: String,
    var mangaWebSource: MangaWebSource) {

    var hash: String = StringUtils.getHash(url.trim())

}
