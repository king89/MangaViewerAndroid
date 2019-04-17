package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.repository.FavoriteMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import javax.inject.Inject

class RemoveFromFavoriteUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val favouriteMangaRepository: FavoriteMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(menu: MangaMenuItem): Completable {
        return favouriteMangaRepository.removeFavouriteManga(menu)

    }

    companion object {
        const val TAG = "RemoveFromFavoriteUseCase"
    }
}
