package com.king.mangaviewer.model

import com.king.mangaviewer.util.StringUtils

open class MangaMenuItem
/**
 * @param id
 * @param title
 * @param description
 * @param imagePath
 */
(id: String, title: String, description: String,
        imagePath: String, url: String,
        mangaWebSource: MangaWebSource)// TODO Auto-generated constructor stub
    : BaseItem(id, title, description, imagePath, url, mangaWebSource) {

    val hash: String
        get() {
            val tx = this.mangaWebSource.className + "|" + this.url
            return StringUtils.getHash(tx)
        }

}
