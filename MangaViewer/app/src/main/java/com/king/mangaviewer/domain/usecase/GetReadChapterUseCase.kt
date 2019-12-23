package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.repository.HistoryMangaRepository
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Observable
import javax.inject.Inject

class GetReadChapterUseCase @Inject constructor(private val appViewModel: AppViewModel,
    private val historyMangaRepository: HistoryMangaRepository) {
    fun execute(menu: MangaMenuItem? = null): Observable<List<MangaChapterItem>> {
        val menu = menu ?: appViewModel.Manga.selectedMangaMenuItem

        return historyMangaRepository.getAllHistoryMangaItem(menu)
            .toObservable()
            .flatMapIterable { it }
            .map { it as MangaChapterItem }
            .toList()
            .toObservable()
            .share()

    }

}
