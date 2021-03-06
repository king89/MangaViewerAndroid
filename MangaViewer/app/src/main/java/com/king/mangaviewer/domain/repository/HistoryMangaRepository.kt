package com.king.mangaviewer.domain.repository

import com.king.mangaviewer.domain.data.local.HistoryMangaDataSource
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

//use for manga local data and remote data

interface HistoryMangaRepository {
    fun addToHistory(item: HistoryMangaChapterItem): Completable
    fun getHistoryMangaItem(chapterHash: String): Single<HistoryMangaChapterItem>
    fun getAllHistoryMangaItem(
        menu: MangaMenuItem? = null): Single<List<HistoryMangaChapterItem>>

    //return latest chapter item for every menu item
    fun getHistoryMenuList(): Single<List<HistoryMangaChapterItem>>

    fun getLastReadMangaItem(
        menu: MangaMenuItem? = null): Single<HistoryMangaChapterItem>

    fun removeHistory(item: HistoryMangaChapterItem): Completable
    fun removeRelatedHistory(item: HistoryMangaChapterItem): Completable
    fun clearAll(): Completable
    fun updateAllHash(): Completable
}

class HistoryMangaRepositoryImpl @Inject constructor(
    private val historyMangaDataSource: HistoryMangaDataSource
) : HistoryMangaRepository {

    override fun updateAllHash(): Completable = historyMangaDataSource.updateAllHash()

    override fun addToHistory(
        item: HistoryMangaChapterItem): Completable = historyMangaDataSource.addToHistory(item)

    override fun getHistoryMangaItem(
        chapterHash: String): Single<HistoryMangaChapterItem> = historyMangaDataSource.getHistoryMangaItem(
        chapterHash)

    override fun getAllHistoryMangaItem(
        menu: MangaMenuItem?): Single<List<HistoryMangaChapterItem>> = historyMangaDataSource.getAllHistoryMangaItem(
        menu)

    override fun getHistoryMenuList(): Single<List<HistoryMangaChapterItem>> = historyMangaDataSource.getHistoryMenuList()

    override fun getLastReadMangaItem(
        menu: MangaMenuItem?): Single<HistoryMangaChapterItem> = historyMangaDataSource.getLastReadMangaItem(
        menu)

    override fun removeHistory(
        item: HistoryMangaChapterItem): Completable = historyMangaDataSource.removeHistory(item)

    override fun removeRelatedHistory(
        item: HistoryMangaChapterItem): Completable =
        historyMangaDataSource.removeRelatedHistory(item)

    override fun clearAll(): Completable = historyMangaDataSource.clearAll()
}