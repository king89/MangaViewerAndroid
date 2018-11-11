package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.FavoriteMangaRepository
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import java.util.ArrayList
import javax.inject.Inject

class GetChapterListUseCase @Inject constructor(private val appViewModel: AppViewModel,
        private val favoriteMangaRepository: FavoriteMangaRepository) {
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
                            .title, "", tauList[i].imagePath,
                            tauList[i].url, menu))
                }
            }
            appViewModel.Manga.mangaChapterList = list
            list
        }
                .doAfterSuccess {
                    //update chapter count if it is favorite manga
                    val menu = appViewModel.Manga.selectedMangaMenuItem
                    if (favoriteMangaRepository.checkIsFavorite(menu).blockingGet()) {
                        val chapterCount = it.size
                        favoriteMangaRepository.updateFavouriteManga(menu, chapterCount).blockingGet()
                    }
                }
                .map { it }
    }
}