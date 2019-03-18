package com.king.mangaviewer.common

import com.king.mangaviewer.domain.data.mangaprovider.WebHHComic
import com.king.mangaviewer.domain.data.mangaprovider.WebIManhua
import com.king.mangaviewer.domain.data.mangaprovider.WebTestManga

object Constants {
    const val DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT_SHORT = "yyyy-MM-dd HH:mm"
    val MANGAFOLDER = "Manga"
    val SETTINGFOLDER = "Setting"
    val LOGTAG = "MangaViewer"

    enum class MSGType {
        Menu,
        Chapter,
        Page
    }

    enum class WebSiteEnum private constructor(
            //        Local();

            val clsName: String, val index: Int) {

        IManhua(WebIManhua::class.java.name, 0),
        HHComic(WebHHComic::class.java.name, 1),
        TestManga(WebTestManga::class.java.name, 2)

    }

    enum class SaveType {
        Temp,
        Local
    }
}