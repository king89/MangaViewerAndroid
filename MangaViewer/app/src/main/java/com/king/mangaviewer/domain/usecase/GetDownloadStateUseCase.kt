package com.king.mangaviewer.domain.usecase

import com.king.mangaviewer.domain.data.local.DownloadState
import com.king.mangaviewer.domain.repository.DownloadTaskRepository
import com.king.mangaviewer.model.MangaChapterHash
import com.king.mangaviewer.model.MangaMenuHash
import io.reactivex.Observable
import javax.inject.Inject


class GetDownloadStateUseCase @Inject constructor(
    private val downloadTaskRepository: DownloadTaskRepository
) {
    fun execute(menuHash: MangaMenuHash): Observable<Map<MangaChapterHash, DownloadState>> {
        return downloadTaskRepository.getTaskListObservable()
            .map {
                it.filter { item -> item.value.menuHash == menuHash }
                    .mapValues { item -> item.value.downLoadState }
            }
    }
}