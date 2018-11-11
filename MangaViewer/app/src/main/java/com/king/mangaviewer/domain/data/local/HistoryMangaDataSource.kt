package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface HistoryMangaDataSource {
    fun addToHistory(item: HistoryMangaChapterItem): Completable
    fun getAllHistoryMangaItem(
            menu: MangaMenuItem? = null): Single<List<HistoryMangaChapterItem>>

    fun removeHistory(item: HistoryMangaChapterItem): Completable
    fun clearAll(): Completable
}

class HistoryMangaLocalDataSource @Inject constructor(
        private val appViewModel: AppViewModel,
        private val historyMangaDAO: HistoryMangaDAO
) : HistoryMangaDataSource {
    override fun addToHistory(
            item: HistoryMangaChapterItem): Completable = Completable.fromCallable {
        historyMangaDAO.insert(item.toHistoryManga())
    }

    override fun getAllHistoryMangaItem(
            menu: MangaMenuItem?): Single<List<HistoryMangaChapterItem>> {
        return historyMangaDAO.getList()
                .toObservable()
                .flatMapIterable { it }
                .map {
                    it.toHistoryChapterItem()
                }
                .toList()
    }

    override fun removeHistory(item: HistoryMangaChapterItem): Completable =
            Completable.fromCallable {
                historyMangaDAO.delete(item.toHistoryManga())
            }

    override fun clearAll(): Completable =
            Completable.fromCallable {
                historyMangaDAO.deleteAll()
            }

    private fun HistoryMangaChapterItem.toHistoryManga(): HistoryManga {

        val historyMenu = HistoryMangaMenu(menu.hash, menu.title, menu.description, menu.imagePath,
                menu.url)
        return HistoryManga(hash, title, description, imagePath, url, mangaWebSource.id,
                lastReadDate,
                historyMenu)

    }

    private fun HistoryManga.toHistoryChapterItem(): HistoryMangaChapterItem {
        val source = appViewModel.Setting.mangaWebSources.first() {
            it.id == manga_websource_id
        }
        val menu = MangaMenuItem(menu.hash, menu.title, menu.description, menu.imagePath,
                menu.url, source)
        val chapter = MangaChapterItem(hash, title, description, imagePath, url, menu)
        return HistoryMangaChapterItem(chapter, last_read_time)
    }
}