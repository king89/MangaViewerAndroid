package com.king.mangaviewer.domain.usecase

import android.content.Context
import com.king.mangaviewer.domain.data.local.DownloadTask
import com.king.mangaviewer.domain.data.local.DownloadedManga
import com.king.mangaviewer.domain.repository.DownloadTaskRepository
import com.king.mangaviewer.domain.repository.DownloadedMangaRepository
import com.king.mangaviewer.util.Logger
import io.reactivex.Completable
import javax.inject.Inject


class FinishDownloadUseCase @Inject constructor(
    private val context: Context,
    private val downloadTaskRepository: DownloadTaskRepository,
    private val downloadedMangaRepository: DownloadedMangaRepository
) {
    fun execute(downloadTask: DownloadTask, folderPath: String, fileUri: String): Completable {
        return downloadTaskRepository.finishTask(downloadTask)
            .andThen {
                //add it to downloaded repo
                Logger.d("FinishDownloadUseCase", "folder:$folderPath, file:$fileUri")
                val manga = DownloadedManga.createFromTask(downloadTask, folderPath, fileUri)
                downloadedMangaRepository.addDownloadedManga(manga).subscribe()
            }

    }
}