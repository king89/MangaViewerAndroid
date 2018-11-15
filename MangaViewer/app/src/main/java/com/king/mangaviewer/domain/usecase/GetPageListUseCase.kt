package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaPageItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import java.util.ArrayList
import javax.inject.Inject

class GetPageListUseCase @Inject constructor(private val appViewModel: AppViewModel,
        private val providerFactory: ProviderFactory) {
    fun execute(chapter: MangaChapterItem? = null): Single<List<MangaPageItem>> {
        return Single.fromCallable {
            val chapterItem = chapter ?: appViewModel.Manga.selectedMangaChapterItem
            val mPattern = providerFactory.getPattern(chapterItem.mangaWebSource)
            val pageUrlList = mPattern.getPageList(chapterItem.url)
            val mangaPageList = ArrayList<MangaPageItem>()

            for (i in pageUrlList.indices) {
                val item = MangaPageItem("page-$i", "", "", "", pageUrlList[i], chapterItem,
                        i,
                        pageUrlList.size)
                item.referUrl = chapterItem.url
                mangaPageList.add(item)
            }

            mangaPageList
        }
    }
}