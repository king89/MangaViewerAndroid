package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import javax.inject.Inject

class SelectMangaMenuUseCase @Inject constructor(appViewModel: AppViewModel) {
    fun execute(menu: MangaMenuItem): Completable {

        return Completable.error(Exception())
    }
}