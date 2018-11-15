package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import io.reactivex.Observable
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

class GetLatestMangaListUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val providerFactory: ProviderFactory
) {
    @SuppressLint("CheckResult")
    fun execute(): Observable<List<MangaMenuItem>> {

        val state: HashMap<String, Any> = HashMap()
        return Flowable.create<List<MangaMenuItem>>({
            try {
                val mangaList = ArrayList<MangaMenuItem>()
                val source = appViewModel.Setting.selectedWebSource
                val mPattern = providerFactory.getPattern(source)
                val pageUrlList = mPattern!!.getLatestMangaList(state)

                if (pageUrlList != null) {
                    for (i in pageUrlList.indices) {
                        mangaList.add(MangaMenuItem("Menu-$i", pageUrlList[i]
                                .title, "", pageUrlList[i].imagePath,
                                pageUrlList[i].url,
                                source))
                    }
                }
                appViewModel.Manga.mangaMenuList = mangaList
                it.onNext(mangaList.toList())
                it.onComplete()
            } catch (e: Exception) {
                it.tryOnError(e)
            }
        }, LATEST)
                .toObservable()
                .share()
    }

}