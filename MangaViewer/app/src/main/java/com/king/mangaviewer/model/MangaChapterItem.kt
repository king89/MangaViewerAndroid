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
        imagePath: String, url: String, menu: MangaMenuItem) :
        BaseItem(id, title, description, imagePath, url, menu.mangaWebSource) {
    var menu: MangaMenuItem = menu

    val hash: String
        get() {
            val sb = StringBuilder()
            sb.append(menu!!.hash + "|" + this.url)
            return StringUtils.getHash(sb.toString())
        }

    override var title: String = ""
        get() {
            val title = super.title
            return if (!TextUtils.isEmpty(title) && menu != null) {
                title.replace(menu!!.title + "", "")
            } else super.title
        }

    override fun equals(obj: Any?): Boolean {
        return if (obj is MangaChapterItem) {
            this.hash == obj.hash
        } else super.equals(obj)
    }
}
