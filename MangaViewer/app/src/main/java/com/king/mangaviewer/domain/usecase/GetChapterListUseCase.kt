package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import java.util.ArrayList
import javax.inject.Inject

class GetChapterListUseCase @Inject constructor(appViewModel: AppViewModel) {
    fun execute(menu: MangaMenuItem): Single<List<MangaChapterItem>> {
        return Single.fromCallable {
            val mPattern = ProviderFactory.getPattern(menu.mangaWebSource)

            val tauList = mPattern.getChapterList(menu.url)
            val list = ArrayList<MangaChapterItem>()
            if (tauList != null) {
                for (i in tauList.indices) {
                    list.add(MangaChapterItem("Chapter-$i", tauList[i]
                            .title, null, tauList[i].imagePath,
                            tauList[i].url, menu))
                }
            }
            list
        }
    }
}