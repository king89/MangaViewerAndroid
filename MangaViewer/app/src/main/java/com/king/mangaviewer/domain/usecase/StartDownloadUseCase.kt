package com.king.mangaviewer.domain.usecase

import android.content.Context
import com.king.mangaviewer.domain.data.local.DownloadTask
import com.king.mangaviewer.domain.repository.DownloadTaskRepository
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.service.download.MangaDownloadService
import io.reactivex.Completable
import javax.inject.Inject


class StartDownloadUseCase @Inject constructor(
    private val context: Context,
    private val downloadTaskRepository: DownloadTaskRepository
) {
    fun execute(chapterList: List<MangaChapterItem>): Completable {
        return Completable.fromAction {
            chapterList.forEach {
                downloadTaskRepository.addTask(DownloadTask(it)).subscribe()
            }
            MangaDownloadService.startNextTask(context)
        }
    }
}