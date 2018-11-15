package com.king.mangaviewer.domain.usecase

import android.annotation.SuppressLint
import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DeleteAllHistoryUseCase @Inject constructor(
        private val appViewModel: AppViewModel,
        private val historyMangaRepository: HistoryMangaRepository
) {
    @SuppressLint("CheckResult")
    fun execute(): Completable {
        return historyMangaRepository.clearAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        const val TAG = "DeleteAllHistoryUseCase"
    }
}
