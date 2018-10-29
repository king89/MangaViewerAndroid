package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaPageItem
import io.reactivex.Single
import java.util.ArrayList

class GetPageListUseCase() {
    fun execute(chapter: MangaChapterItem): Single<List<MangaPageItem>> {
        return Single.fromCallable {
            val mPattern = ProviderFactory.getPattern(chapter.mangaWebSource)
            val pageUrlList = mPattern.getPageList(chapter.url)
            val mangaPageList = ArrayList<MangaPageItem>()
            if (pageUrlList !=
                    null) {
                for (i in pageUrlList.indices) {
                    val item = MangaPageItem("page-$i", null, null, null, pageUrlList[i], chapter,
                            i,
                            pageUrlList.size)
                    item.referUrl = chapter.url
                    mangaPageList.add(item)
                }
            }
            mangaPageList
        }
    }
}