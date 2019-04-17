package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.model.TitleAndUrl
import javax.inject.Inject

class DownloadedMangaProvider @Inject constructor(): MangaProvider() {

    override fun getLatestMangaList(html: String): List<TitleAndUrl>? {
        return super.getLatestMangaList(html)
    }
}