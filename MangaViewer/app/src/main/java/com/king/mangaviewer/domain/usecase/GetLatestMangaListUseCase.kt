package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
import io.reactivex.Single
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

class GetLatestMangaListUseCase @Inject constructor(private val appViewModel: AppViewModel) {
    @SuppressLint("CheckResult")
    fun execute(): Observable<List<MangaMenuItem>> {
        val state: HashMap<String, Any> = HashMap()
        return Observable.create {
            it.onNext(listOf())
            var mangaList: ArrayList<MangaMenuItem>? = null
            val mPattern = ProviderFactory.getPattern(appViewModel.Setting.selectedWebSource)
            val pageUrlList = mPattern!!.getLatestMangaList(state)
            if (mangaList == null) {
                mangaList = ArrayList()
            }
            if (pageUrlList != null) {
                for (i in pageUrlList.indices) {
                    mangaList.add(MangaMenuItem("Menu-$i", pageUrlList[i]
                            .title, null, pageUrlList[i].imagePath,
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