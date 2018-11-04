package com.king.mangaviewer.ui.chapter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.adapter.MangaChapterItemWrapper
import com.king.mangaviewer.adapter.WrapperType
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.data.MangaRepository
import com.king.mangaviewer.domain.usecase.GetChapterListUseCase
import com.king.mangaviewer.domain.usecase.GetReadChapterUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaChapterUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.util.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class MangaChapterActivityViewModel @Inject constructor(
        private val appRepository: AppRepository,
        private val mangaRepository: MangaRepository,
        private val getChapterListUseCase: GetChapterListUseCase,
        private val getReadChapterUseCase: GetReadChapterUseCase,
        private val selectMangaChapterUseCase: SelectMangaChapterUseCase
) :
        BaseActivityViewModel() {

    private val mChapterPair = MutableLiveData<Pair<List<MangaChapterItem>, List<MangaChapterItem>>>()
    val chapterPair: LiveData<Pair<List<MangaChapterItem>, List<MangaChapterItem>>> = mChapterPair

    init {
        mChapterPair.value = Pair(listOf(), listOf())
    }

    override fun attachToView() {
        getChapterList()
    }

    fun getChapterList() {
        getChapterListUseCase.execute()
                .map {
                    val readList = getReadChapterUseCase.execute().blockingGet()
                    Pair(it, readList)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Loading }
                .doAfterTerminate { mLoadingState.value = Idle }
                .subscribe({
                    mChapterPair.value = it
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }

    }

    fun updateHistoryChapter() {
        getReadChapterUseCase.execute()
                .map {
                    Pair(mChapterPair.value?.first ?: emptyList(), it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mChapterPair.value = it
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }
    }

    companion object {
        const val TAG = "MangaChapterActivityViewModel"
    }
}
