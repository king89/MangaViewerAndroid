package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.base.ErrorMessage.NoError
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.usecase.DeleteAllHistoryUseCase
import com.king.mangaviewer.domain.usecase.DeleteHistoryChapterUseCase
import com.king.mangaviewer.domain.usecase.GetHistoryMenuListUseCase
import com.king.mangaviewer.domain.usecase.SelectHistoryChapterUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryFragmentViewModel @Inject constructor(
        private val appRepository: AppRepository,
        private val getHistoryChapterListUseCase: GetHistoryMenuListUseCase,
        private val selectMangaMenuUseCase: SelectMangaMenuUseCase,
        private val selectHistoryChapterUseCase: SelectHistoryChapterUseCase,
        private val deleteHistoryChapterUseCase: DeleteHistoryChapterUseCase,
        private val deleteAllHistoryUseCase: DeleteAllHistoryUseCase
) : BaseFragmentViewModel() {

    private val _mangaList = MutableLiveData<List<HistoryMangaChapterItem>>()
    val mangaList: LiveData<List<HistoryMangaChapterItem>> = _mangaList

    override fun attachToView() {
        getData(false)
    }

    private fun getData(isSilent: Boolean) {
        getHistoryChapterListUseCase.execute(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    if (!isSilent) mLoadingState.value = Loading
                }
                .doAfterTerminate { mLoadingState.value = Idle }
                .subscribe({
                    _mangaList.value = it
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }
    }

    fun selectChapter(chapterItem: HistoryMangaChapterItem) {
        selectHistoryChapterUseCase.execute(chapterItem).subscribe()
    }

    fun selectMenu(chapterItem: HistoryMangaChapterItem) {
        selectMangaMenuUseCase.execute(chapterItem.menu).subscribe()
    }

    fun refresh(isSilent: Boolean) {
        getData(isSilent)
    }

    fun deleteChapter(item: HistoryMangaChapterItem) {
        deleteHistoryChapterUseCase.execute(item)
                .subscribe({
                    getData(false)
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }

    }

    fun deleteMenu(item: HistoryMangaChapterItem) {
        deleteHistoryChapterUseCase.execute(item, true)
                .subscribe({
                    getData(false)
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }

    }

    fun clearAllHistory() {
        deleteAllHistoryUseCase.execute().subscribe(
                {
                    mErrorMessage.value = NoError
                    getData(false)
                },
                {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }
    }

    companion object {
        const val TAG = "HistoryFragmentViewModel"
    }
}
