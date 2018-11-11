package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import javax.inject.Inject

class GetReadChapterUseCase @Inject constructor(private val appViewModel: AppViewModel,
        private val historyMangaRepository: HistoryMangaRepository) {
    fun execute(): Single<List<MangaChapterItem>> {
        val menu = appViewModel.Manga.selectedMangaMenuItem

        return historyMangaRepository.getAllHistoryMangaItem(menu)
                .toObservable()
                .flatMapIterable { it }
                .map { it as MangaChapterItem }
                .toList()

    }

}
