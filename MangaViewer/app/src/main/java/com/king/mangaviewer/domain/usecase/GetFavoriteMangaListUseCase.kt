package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.FavoriteMangaRepository
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
import javax.inject.Inject

class GetFavoriteMangaListUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val favouriteMangaRepository: FavoriteMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(): Observable<List<FavouriteMangaMenuItem>> {
        return favouriteMangaRepository.getFavouriteMangaList()
                .map {
                    it.sortedDescending()
                }.toObservable()
                .share()
    }

    companion object {
        const val TAG = "GetFavoriteMangaListUseCase"
    }
}
