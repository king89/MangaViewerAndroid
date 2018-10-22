package com.king.mangaviewer.viewmodel

import android.content.Context
import android.support.annotation.WorkerThread

import com.king.mangaviewer.datasource.HistoryMangaDataSource
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem

/**
 * Created by KinG on 12/27/2015.
 */
class HistoryViewModel(context: Context) : ViewModelBase(context) {
    private val mHistoryChapterList: List<HistoryMangaChapterItem>? = null
    private val mHistoryMangaDataSource: HistoryMangaDataSource
    fun getHistoryChapterList(): List<HistoryMangaChapterItem> = mHistoryMangaDataSource.allHistoryMangaItem

    init {
        this.mHistoryMangaDataSource = HistoryMangaDataSource(context)
    }

    @WorkerThread
    fun getLastRead(menu: MangaMenuItem): MangaChapterItem? {
        var result: MangaChapterItem? = null
        return mHistoryMangaDataSource.allHistoryMangaItem.firstOrNull {
            it.menu.hash == menu.hash
        }

    }

    //add histroy
    fun addChapterItemToHistory(chapter: MangaChapterItem): Boolean {
        val historyItem = HistoryMangaChapterItem(chapter)
        mHistoryMangaDataSource.addToHistory(historyItem)
        return true
    }

    fun clearHistory() {
        mHistoryMangaDataSource.clearAll()
    }
}
