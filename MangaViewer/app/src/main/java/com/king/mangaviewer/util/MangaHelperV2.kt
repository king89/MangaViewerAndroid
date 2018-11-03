package com.king.mangaviewer.util

import android.support.annotation.WorkerThread
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaMenuItem
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
                        .title, null, pageUrlList[i].imagePath,
                        pageUrlList[i].url,
                        mangaWebSource))
            }
        }
        return mangaList
    }
}