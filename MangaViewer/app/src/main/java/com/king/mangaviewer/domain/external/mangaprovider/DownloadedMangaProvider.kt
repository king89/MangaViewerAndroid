package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.model.TitleAndUrl

class DownloadedMangaProvider : MangaProvider() {

    override fun getLatestMangaList(html: String): List<TitleAndUrl>? {
        return super.getLatestMangaList(html)
    }
}