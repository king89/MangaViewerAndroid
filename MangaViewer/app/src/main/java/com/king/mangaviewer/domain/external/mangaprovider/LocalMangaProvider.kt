package com.king.mangaviewer.domain.external.mangaprovider


import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaUriType
import com.king.mangaviewer.model.TitleAndUrl
import com.king.mangaviewer.util.Logger

import java.io.File
import java.io.FilenameFilter
import java.util.ArrayList
import java.util.HashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.inject.Inject

/**
 * Created by KinG on 12/24/2014.
 */
class LocalMangaProvider @Inject
constructor() : MangaProvider() {
    private var LOG_TAG = "LocalManga"

    init {

        // TODO Auto-generated constructor stub
        WEBSITE_URL = ""
        WEB_SEARCH_URL = ""
        CHARSET = "utf8"
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

        return fileList.sorted().also { Logger.d(LOG_TAG, "files: $it") }
    }

    private fun isImageType(name: String): Boolean {
        val extName = name.substringAfterLast(".")
        val imgTypeList = listOf("png", "jpg", "jpeg", "webp")

        return imgTypeList.contains(extName)
    }

    override fun getImageUrl(pageUrl: String, nowNum: Int): String {
        return pageUrl
    }


    override fun getChapterList(menu: MangaMenuItem): List<TitleAndUrl>? {
        val path = File(menu.url)
        var fileList: MutableList<String>? = null
        val chapterList = ArrayList<TitleAndUrl>()
        try {
            path.mkdirs()
        } catch (e: SecurityException) {
            Logger.e(LOG_TAG, e, "unable to write on the sd card ")
        }

        // Checks whether path exists
        if (path.exists()) {
            val filter = FilenameFilter { dir, filename ->
                val sel = File(dir, filename)
                // Filters based on whether the file is hidden or not
                sel.isFile && sel.name.contains(".zip") && !sel.isHidden
            }

            val fList = path.list(filter)
            fileList = ArrayList()
            for (i in fList.indices) {
                fileList.add(fList[i])
                // Convert into file path
                val sel = File(path, fList[i])

                chapterList.add(TitleAndUrl(sel.name, sel.absolutePath))
            }

            chapterList.sort()
            chapterList.reverse()
        }
        return chapterList
    }


    override fun getLatestMangaList(state: HashMap<String, Any>): List<MangaMenuItem> {
        val topMangaList = ArrayList<TitleAndUrl>()

        for (i in 0..9) {
            val url = WEBSITE_URL + i
            val title = "Test Menu $i"
            val imageUrl = ""
            topMangaList.add(TitleAndUrl(title, url, imageUrl))

        }

        return toMenuItem(topMangaList)
    }

    override fun getLoaderType(): MangaUriType {
        return MangaUriType.ZIP
    }
}
