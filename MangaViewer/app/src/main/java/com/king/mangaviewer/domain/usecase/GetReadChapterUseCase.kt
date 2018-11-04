package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import javax.inject.Inject

class GetReadChapterUseCase @Inject constructor(private val appViewModel: AppViewModel) {
    fun execute(): Single<List<MangaChapterItem>> {
        return Single.fromCallable {
            val menu = appViewModel.Manga.selectedMangaMenuItem
            val list = appViewModel.HistoryManga.getHistoryChapterList(menu)
            list
        }
    }

}
