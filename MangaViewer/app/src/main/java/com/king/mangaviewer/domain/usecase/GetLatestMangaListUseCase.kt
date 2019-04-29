package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.external.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.BackpressureStrategy.LATEST
import io.reactivex.Flowable
import io.reactivex.Observable
import java.util.HashMap
import javax.inject.Inject

class GetLatestMangaListUseCase @Inject constructor(
    private val appViewModel: AppViewModel,
    private val providerFactory: ProviderFactory
) {
    @SuppressLint("CheckResult")
    fun execute(source: MangaWebSource): Observable<List<MangaMenuItem>> {

        val state: HashMap<String, Any> = HashMap()
        return Flowable.create<List<MangaMenuItem>>({
            try {
                val mPattern = providerFactory.getPattern(source)
                val mangaList = mPattern.getLatestMangaList(state)

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