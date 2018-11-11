package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.FavoriteMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val favouriteMangaRepository: FavoriteMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(menu: MangaMenuItem, chapterCount: Int = 0): Completable {
        return favouriteMangaRepository.addFavouriteManga(menu, chapterCount)

    }

    companion object {
        const val TAG = "AddToFavoriteUseCase"
    }
}
