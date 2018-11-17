package com.king.mangaviewer.domain.data

import com.king.mangaviewer.domain.data.local.HistoryMangaDataSource
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

//use for manga local data and remote data

interface HistoryMangaRepository : HistoryMangaDataSource {

}

class HistoryMangaRepositoryImpl @Inject constructor(
        private val historyMangaDataSource: HistoryMangaDataSource
) : HistoryMangaRepository {

    override fun addToHistory(
            item: HistoryMangaChapterItem): Completable = historyMangaDataSource.addToHistory(item)

    override fun getAllHistoryMangaItem(
            menu: MangaMenuItem?): Single<List<HistoryMangaChapterItem>> = historyMangaDataSource.getAllHistoryMangaItem(
            menu)

    override fun removeHistory(
            item: HistoryMangaChapterItem): Completable = historyMangaDataSource.removeHistory(item)

    override fun removeRelatedHistory(
            item: HistoryMangaChapterItem): Completable =
            historyMangaDataSource.removeRelatedHistory(item)

    override fun clearAll(): Completable = historyMangaDataSource.clearAll()
}