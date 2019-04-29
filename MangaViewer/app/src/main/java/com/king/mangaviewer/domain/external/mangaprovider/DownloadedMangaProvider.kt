package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.domain.data.local.DownloadedManga
import com.king.mangaviewer.domain.repository.DownloadedMangaRepository
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.model.TitleAndUrl
import java.util.HashMap
import javax.inject.Inject

class DownloadedMangaProvider @Inject constructor(
    private val downloadedMangaRepository: DownloadedMangaRepository
) : MangaProvider() {

    override fun getLatestMangaList(
        state: HashMap<String, Any>): List<MangaMenuItem> {
        return downloadedMangaRepository.getMangaMenuList()
            .toObservable()
            .flatMapIterable { it }
            .map {
                it.toMangaMenu()
            }
            .toList()
            .blockingGet()
    }

    override fun getChapterList(menu: MangaMenuItem): List<TitleAndUrl> {
        return downloadedMangaRepository.getMangaChapterList(menu)
            .toObservable()
            .flatMapIterable { it }
            .map { TitleAndUrl(it.title, it.url, "") }
            .toList()
            .blockingGet()
    }

    private fun DownloadedManga.toMangaMenu(): MangaMenuItem {
        val imagePath = ""
        return MangaMenuItem(menu.hash, menu.title, description, imagePath, menu.url,
            MangaWebSource.DOWNLOAD)
    }


    private fun DownloadedManga.toChapterItem(menu: MangaMenuItem): MangaChapterItem {
        return MangaChapterItem(hash, title, description, "", url, menu)
    }
}