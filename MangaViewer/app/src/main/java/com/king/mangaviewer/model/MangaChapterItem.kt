package com.king.mangaviewer.model

import android.text.TextUtils

import com.king.mangaviewer.util.StringUtils

open class MangaChapterItem(
    var id: String,
    var title: String,
    var description: String,
    var imagePath: String,
    var url: String,
    var menu: MangaMenuItem) {

    var hash: String = StringUtils.getHash(url.trim())
    val mangaWebSource: MangaWebSource
        get() = menu.mangaWebSource

    init {
        title = if (!TextUtils.isEmpty(title)) {
            title.replace(menu.title + "", "")
        } else title
    }


    override fun equals(other: Any?): Boolean {
        return if (other is MangaChapterItem) {
            this.hash == other.hash
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + imagePath.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + menu.hashCode()
        result = 31 * result + hash.hashCode()
        return result
    }
}
