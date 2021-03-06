package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.StringUtils
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface HistoryMangaDataSource {
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

class HistoryMangaLocalDataSource @Inject constructor(
    private val appViewModel: AppViewModel,
    private val historyMangaDAO: HistoryMangaDAO
) : HistoryMangaDataSource {

    override fun updateAllHash(): Completable {
        return Completable.fromAction{
            historyMangaDAO.getList().blockingGet().forEach {
                it.hash = StringUtils.getHash(it.url)
                historyMangaDAO.update(it)
            }
        }
    }

    override fun addToHistory(
        item: HistoryMangaChapterItem): Completable = Completable.fromCallable {
        historyMangaDAO.insert(item.toHistoryManga())
    }

    override fun getHistoryMangaItem(chapterHash: String): Single<HistoryMangaChapterItem> {
        return historyMangaDAO.getItem(chapterHash)
            .map { it.toHistoryChapterItem() }
    }

    override fun getHistoryMenuList(): Single<List<HistoryMangaChapterItem>> {
        return historyMangaDAO.getLastReadMangaItem()
            .toObservable()
            .flatMapIterable { it }
            .map {
                it.toHistoryChapterItem()
            }
            .toList()
    }

    override fun getAllHistoryMangaItem(
        menu: MangaMenuItem?): Single<List<HistoryMangaChapterItem>> {
        return historyMangaDAO.getList()
            .toObservable()
            .flatMapIterable { it }
            .map {
                it.toHistoryChapterItem()
            }.filter {
                menu ?: return@filter true
                menu.hash == it.menu.hash
            }
            .toList()
    }

    override fun getLastReadMangaItem(menu: MangaMenuItem?): Single<HistoryMangaChapterItem> {
        return if (menu == null) {
            historyMangaDAO.getLastReadItem()
                .map {
                    it.toHistoryChapterItem()
                }
        } else {
            historyMangaDAO.getLastReadItem(menu.hash)
                .map {
                    it.toHistoryChapterItem()
                }
        }
    }

    override fun removeHistory(item: HistoryMangaChapterItem): Completable =
        Completable.fromCallable {
            historyMangaDAO.delete(item.toHistoryManga())
        }

    override fun removeRelatedHistory(item: HistoryMangaChapterItem): Completable {
        return Completable.fromCallable {
            getAllHistoryMangaItem(item.menu).blockingGet().forEach {
                removeHistory(it).blockingAwait()
            }
        }

    }

    override fun clearAll(): Completable =
        Completable.fromCallable {
            historyMangaDAO.deleteAll()
        }

    private fun HistoryMangaChapterItem.toHistoryManga(): HistoryManga {

        val historyMenu = HistoryMangaMenu(menu.hash, menu.title, menu.description, menu.imagePath,
            menu.url)
        return HistoryManga(hash, title, description, imagePath, url, mangaWebSource.id,
            lastReadDate, lastReadPageNum,
            historyMenu)

    }

    private fun HistoryManga.toHistoryChapterItem(): HistoryMangaChapterItem {
        val source = appViewModel.Setting.mangaWebSources.first {
            it.id == manga_websource_id
        }
        val menu = MangaMenuItem(menu.hash, menu.title, menu.description, menu.imagePath,
            menu.url, source)
        val chapter = MangaChapterItem(hash, title, description, imagePath, url, menu)
        return HistoryMangaChapterItem(chapter, last_read_page_num, last_read_time)
    }
}