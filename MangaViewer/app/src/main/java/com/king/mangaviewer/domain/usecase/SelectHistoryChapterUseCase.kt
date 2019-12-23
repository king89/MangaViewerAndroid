package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.repository.HistoryMangaRepository
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SelectHistoryChapterUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val historyMangaRepository: HistoryMangaRepository,
        private val getChapterListUseCase: GetChapterListUseCase) {
    fun execute(chapter: HistoryMangaChapterItem): Completable {
        return Completable.fromCallable {
            appViewModel.Manga.selectedMangaChapterItem = chapter
            appViewModel.Manga.selectedMangaMenuItem = chapter.menu
            //make sure the pos is the latest
            appViewModel.Manga.nowPagePosition = historyMangaRepository.getHistoryMangaItem(chapter.hash).blockingGet().lastReadPageNum
            Any()
        }.andThen(historyMangaRepository.addToHistory(HistoryMangaChapterItem(chapter)))
                .andThen(getChapterListUseCase.execute().toCompletable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}