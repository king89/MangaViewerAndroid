package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SelectLastReadChapterUseCase @Inject constructor(
    private val appViewModel: AppViewModel,
    private val historyMangaRepository: HistoryMangaRepository,
    private val getChapterListUseCase: GetChapterListUseCase) {
    fun execute(menuItem: MangaMenuItem? = null): Completable {
        return Single.fromCallable {
            val chapter = historyMangaRepository.getLastReadMangaItem(menuItem).blockingGet()
            appViewModel.Manga.selectedMangaChapterItem = chapter
            appViewModel.Manga.selectedMangaMenuItem = chapter.menu
            appViewModel.Manga.nowPagePosition = chapter.lastReadPageNum
            chapter
        }.doOnSuccess {
            historyMangaRepository.addToHistory(it)
        }.ignoreElement()
            .andThen(getChapterListUseCase.execute().ignoreElement())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}