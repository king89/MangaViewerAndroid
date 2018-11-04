package com.king.mangaviewer.domain.usecase

import android.os.Looper
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import java.util.ArrayList
import javax.inject.Inject

class GetChapterListUseCase @Inject constructor(private val appViewModel: AppViewModel) {
    fun execute(): Single<List<MangaChapterItem>> {
        return Single.fromCallable {
            appViewModel.Manga.mangaChapterList = emptyList()

            val menu = appViewModel.Manga.selectedMangaMenuItem
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
            appViewModel.Manga.mangaChapterList = list
            list
        }
                .doAfterSuccess {
                    //update chapter count if it is favorite manga
                    val menu = appViewModel.Manga.selectedMangaMenuItem
                    if (appViewModel.Setting.checkIsFavourited(menu)) {
                        val chapterCount = it.size
                        appViewModel.Setting.removeFavouriteManga(menu)
                        appViewModel.Setting.addFavouriteManga(menu, chapterCount)
                    }
                }
                .map { it }
    }
}