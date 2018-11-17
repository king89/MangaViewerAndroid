package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DeleteHistoryChapterUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val historyMangaRepository: HistoryMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(item: HistoryMangaChapterItem, alsoDeleteRelatedChapter: Boolean = false): Completable {
        return if (alsoDeleteRelatedChapter) {
            historyMangaRepository.removeRelatedHistory(item)
        } else {
            historyMangaRepository.removeHistory(item)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        const val TAG = "DeleteHistoryChapterUseCase"
    }
}
