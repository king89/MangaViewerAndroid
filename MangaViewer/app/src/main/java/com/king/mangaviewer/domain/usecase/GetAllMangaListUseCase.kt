package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import javax.inject.Inject

class GetAllMangaListUseCase @Inject constructor(appViewModel: AppViewModel) {
    fun execute(chapter: MangaChapterItem): Single<List<MangaMenuItem>> {

        return Single.error(Exception())
    }
}