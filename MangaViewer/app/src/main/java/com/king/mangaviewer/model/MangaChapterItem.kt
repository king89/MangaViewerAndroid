package com.king.mangaviewer.model

import android.text.TextUtils

import com.king.mangaviewer.util.StringUtils

open class MangaChapterItem
/**
 * @param id
 * @param title
 * @param description
 * @param imagePath
 * @param menu
 */
(id: String, title: String, description: String,
        imagePath: String, url: String, var menu: MangaMenuItem) :
        BaseItem(id, title, description, imagePath, url, menu.mangaWebSource) {

    val hash: String
        get() {
            val sb = StringBuilder()
            sb.append(menu.hash + "|" + this.url)
            return StringUtils.getHash(sb.toString())
        }

    override var title: String = ""
        get() {
            val title = super.title
            return if (!TextUtils.isEmpty(title)) {
                title.replace(menu.title + "", "")
            } else super.title
        }

    override fun equals(other: Any?): Boolean {
        return if (other is MangaChapterItem) {
            this.hash == other.hash
        } else super.equals(other)
    }
}
