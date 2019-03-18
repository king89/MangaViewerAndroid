package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddToHistoryUseCase @Inject constructor(private val appViewModel: AppViewModel,
        private val historyMangaRepository: HistoryMangaRepository) {
    fun execute(chapter: MangaChapterItem, pageNum: Int = 0): Completable {
        return historyMangaRepository.addToHistory(HistoryMangaChapterItem(chapter, pageNum))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}