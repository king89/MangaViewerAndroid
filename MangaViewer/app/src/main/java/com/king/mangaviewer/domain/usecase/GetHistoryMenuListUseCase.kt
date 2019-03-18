package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
import javax.inject.Inject

class GetHistoryMenuListUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val historyMangaRepository: HistoryMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(menuItem: MangaMenuItem?): Observable<List<HistoryMangaChapterItem>> {
        return historyMangaRepository.getHistoryMenuList()
                .toObservable()
                .share()
    }

    companion object {
        const val TAG = "GetHistoryMenuListUseCase"
    }
}
