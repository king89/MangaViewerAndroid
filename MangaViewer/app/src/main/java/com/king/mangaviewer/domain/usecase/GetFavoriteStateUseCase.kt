package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.repository.FavoriteMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import javax.inject.Inject

class GetFavoriteStateUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val favouriteMangaRepository: FavoriteMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(menu: MangaMenuItem): Single<Boolean> {
        return favouriteMangaRepository.checkIsFavorite(menu)

    }

    companion object {
        const val TAG = "GetFavoriteStateUseCase"
    }
}
