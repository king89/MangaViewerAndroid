package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import javax.inject.Inject

class SelectMangaChapterUseCase @Inject constructor(private val appViewModel: AppViewModel) {
    fun execute(chapter: MangaChapterItem): Completable {
        appViewModel.Manga.selectedMangaChapterItem = chapter
        return Completable.complete()
    }
}