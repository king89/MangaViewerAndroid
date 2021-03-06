package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.repository.DownloadedMangaRepository
import com.king.mangaviewer.domain.repository.HistoryMangaRepository
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SelectDownloadedMangaChapterUseCase @Inject constructor(
    private val appViewModel: AppViewModel,
    private val downloadedMangaRepository: DownloadedMangaRepository,
    private val historyMangaRepository: HistoryMangaRepository) {
    fun execute(chapter: MangaChapterItem): Completable {
        return Completable.fromCallable {
            appViewModel.Manga.selectedMangaChapterItem = chapter
            appViewModel.Manga.selectedMangaMenuItem = chapter.menu
            appViewModel.Manga.nowPagePosition = 0
            Any()
        }.andThen(historyMangaRepository.addToHistory(HistoryMangaChapterItem(chapter)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}