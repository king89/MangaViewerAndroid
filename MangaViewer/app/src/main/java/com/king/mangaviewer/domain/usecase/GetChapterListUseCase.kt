package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Single
import javax.inject.Inject

class GetChapterListUseCase @Inject constructor(appViewModel: AppViewModel) {
    fun execute(menu: MangaMenuItem): Single<List<MangaChapterItem>> {

        return Single.error(Exception())
    }
}