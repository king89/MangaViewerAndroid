package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
import javax.inject.Inject

class SearchMangaMenuUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val providerFactory: ProviderFactory
) {
    fun execute(state: HashMap<String, Any>): Observable<List<MangaMenuItem>> {
        return Observable.create {
            it.onNext(listOf())
            var mangaList: ArrayList<MangaMenuItem>? = null
            val mPattern = providerFactory.getPattern(appViewModel.Setting.selectedWebSource)
            val pageUrlList = mPattern.getSearchingList(state)
            if (mangaList == null) {
                mangaList = ArrayList()
            }
            if (pageUrlList != null) {
                for (i in pageUrlList.indices) {
                    mangaList.add(MangaMenuItem("Menu-$i", pageUrlList[i]
                            .title, "", pageUrlList[i].imagePath,
                            pageUrlList[i].url,
                            appViewModel.Setting.selectedWebSource))
                }
            }
            it.onNext(mangaList)
            appViewModel.Manga.mangaMenuList = mangaList
            it.onComplete()
        }
    }
}