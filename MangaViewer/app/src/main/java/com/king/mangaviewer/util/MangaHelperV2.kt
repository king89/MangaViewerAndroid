package com.king.mangaviewer.util

import android.support.annotation.WorkerThread
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaPageItem
import com.king.mangaviewer.model.MangaWebSource
import java.util.ArrayList
import java.util.HashMap

object MangaHelperV2 {
    /* Menu */
    @WorkerThread
    fun getLatestMangeList(mangaList: MutableList<MangaMenuItem>?,
            state: HashMap<String, Any>, mangaWebSource: MangaWebSource): List<MangaMenuItem> {
        var mangaList = mangaList
        val mPattern = ProviderFactory.getPattern(
                mangaWebSource)
        val pageUrlList = mPattern!!.getLatestMangaList(state)
        if (mangaList == null) {
            mangaList = ArrayList()
        }
        if (pageUrlList != null) {
            for (i in pageUrlList.indices) {
                mangaList.add(MangaMenuItem("Menu-$i", pageUrlList[i]
                        .title, "", pageUrlList[i].imagePath,
                        pageUrlList[i].url,
                        mangaWebSource))
            }
        }
        return mangaList
    }

    /* Chapter */
    @WorkerThread
    fun getChapterList(menu: MangaMenuItem): List<MangaChapterItem> {
        val mPattern = ProviderFactory.getPattern(menu.mangaWebSource)

        val tauList = mPattern!!.getChapterList(menu.url)
        val list = ArrayList<MangaChapterItem>()
        if (tauList != null) {
            for (i in tauList.indices) {
                list.add(MangaChapterItem("Chapter-$i", tauList[i]
                        .title, "", tauList[i].imagePath,
                        tauList[i].url, menu))
            }
        }
        return list
    }

    @WorkerThread
    fun getPageList(chapter: MangaChapterItem): List<MangaPageItem> {
        val mPattern = ProviderFactory.getPattern(chapter.mangaWebSource)
        val pageUrlList = mPattern!!.getPageList(chapter.url)
        val mangaPageList = ArrayList<MangaPageItem>()
        if (pageUrlList !=
                null) {
            for (i in pageUrlList.indices) {
                val item = MangaPageItem("page-$i", "", "", "", pageUrlList[i], chapter, i,
                        pageUrlList.size)
                item.referUrl = chapter.url
                mangaPageList.add(item)
            }
        }
        return mangaPageList
    }
}