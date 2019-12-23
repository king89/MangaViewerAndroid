package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.repository.LocalMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import javax.inject.Inject

class RemoveLocalMangaMenuUseCase @Inject constructor(
    private val localMangaRepository: LocalMangaRepository
) {
    fun execute(menuList: List<MangaMenuItem>): Completable {
        return localMangaRepository.removeMangaList(menuList)
    }
}