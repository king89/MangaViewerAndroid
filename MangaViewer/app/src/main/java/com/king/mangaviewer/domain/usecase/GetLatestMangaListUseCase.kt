package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
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
            val source = appViewModel.Setting.selectedWebSource
            val mPattern = ProviderFactory.getPattern(source)
            val pageUrlList = mPattern!!.getLatestMangaList(state)
            if (mangaList == null) {
                mangaList = ArrayList()
            }
            if (pageUrlList != null) {
                for (i in pageUrlList.indices) {
                    mangaList.add(MangaMenuItem("Menu-$i", pageUrlList[i]
                            .title, "", pageUrlList[i].imagePath,
                            pageUrlList[i].url,
                            source))
                }
            }
            it.onNext(mangaList)
            appViewModel.Manga.mangaMenuList = mangaList
            it.onComplete()
        }
    }
}