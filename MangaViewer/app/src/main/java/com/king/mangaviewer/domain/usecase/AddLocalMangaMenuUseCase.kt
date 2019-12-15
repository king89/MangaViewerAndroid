package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.repository.LocalMangaRepository
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import java.io.File
import javax.inject.Inject

class AddLocalMangaMenuUseCase @Inject constructor(
    private val appViewModel: AppViewModel,
    private val localMangaRepository: LocalMangaRepository
) {
    fun execute(file: File): Completable {
        val menuTitle = file.absolutePath.let {
            it.substring(it.lastIndexOf("/") + 1)
        }
        val menu = MangaMenuItem("", menuTitle, "", "", file.absolutePath, MangaWebSource.LOCAL)
        return localMangaRepository.addManga(menu)
    }
}