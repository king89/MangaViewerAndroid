package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
import javax.inject.Inject

class GetFavoriteMangaListUseCase @Inject constructor(private val appViewModel: AppViewModel) {
    @SuppressLint("CheckResult")
    fun execute(): Observable<List<FavouriteMangaMenuItem>> {
        return Observable.fromCallable {
            Logger.d(TAG, "GetFavoriteMangaListUseCase execute called")

            val list = appViewModel.Setting.favouriteMangaList
            list.sort()
            list.reverse()
            list
        }
                .share()
    }

    companion object {
        const val TAG = "GetFavoriteMangaListUseCase"
    }
}
