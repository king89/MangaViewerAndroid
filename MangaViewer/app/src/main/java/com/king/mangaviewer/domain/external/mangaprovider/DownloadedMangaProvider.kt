package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.domain.data.local.DownloadedManga
import com.king.mangaviewer.domain.repository.DownloadedMangaRepository
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.model.TitleAndUrl
import com.king.mangaviewer.util.Logger
import java.util.ArrayList
import java.util.HashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
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

    override fun getPageList(firstPageUrl: String): List<String> {

        val fileList = ArrayList<String>()
        try {
            var ze: ZipEntry? = null
            val zp = ZipFile(firstPageUrl)
            val it = zp.entries()
            while (it.hasMoreElements()) {
                ze = it.nextElement()
                if (ze!!.isDirectory || !isImageType(ze.name)) continue
                fileList.add(ze.name)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fileList.sorted().also { Logger.d(TAG, "files: $it") }
    }

    private fun DownloadedManga.toMangaMenu(): MangaMenuItem {
        val imagePath = ""
        return MangaMenuItem(menu.hash, menu.title, description, imagePath, menu.url,
            MangaWebSource.DOWNLOAD)
    }


    private fun DownloadedManga.toChapterItem(menu: MangaMenuItem): MangaChapterItem {
        return MangaChapterItem(hash, title, description, "", url, menu)
    }

    private fun isImageType(name: String): Boolean {
        val extName = name.substringAfterLast(".")
        val imgTypeList = listOf("png", "jpg", "jpeg", "webp")

        return imgTypeList.contains(extName)
    }

    companion object {
        const val TAG = "DownloadedMangaProvider"
    }
}