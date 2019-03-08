package com.king.mangaviewer.ui.chapter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.domain.usecase.AddToFavoriteUseCase
import com.king.mangaviewer.domain.usecase.GetChapterListUseCase
import com.king.mangaviewer.domain.usecase.GetFavoriteStateUseCase
import com.king.mangaviewer.domain.usecase.GetReadChapterUseCase
import com.king.mangaviewer.domain.usecase.RemoveFromFavoriteUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaChapterUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MangaChapterActivityViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val mangaRepository: HistoryMangaRepository,
    private val getChapterListUseCase: GetChapterListUseCase,
    private val getReadChapterUseCase: GetReadChapterUseCase,
    private val selectMangaChapterUseCase: SelectMangaChapterUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val removeFromFavoriteUseCase: RemoveFromFavoriteUseCase,
    private val getFavouriteStateUseCase: GetFavoriteStateUseCase
) :
    BaseActivityViewModel() {

    private val mChapterPair = MutableLiveData<Pair<List<MangaChapterItem>, List<MangaChapterItem>>>()
    val chapterPair: LiveData<Pair<List<MangaChapterItem>, List<MangaChapterItem>>> = mChapterPair

    private val mFavouriteState = MutableLiveData<Boolean>().apply { value = false }
    val favouriteState = mFavouriteState
    // true for default , false for reverse
    private var order = true
    init {
        mChapterPair.value = Pair(emptyList(), emptyList())
    }

    override fun attachToView() {
        getChapterList()
        getFavoriteState()
    }

    fun getChapterList() {
        getChapterListUseCase.execute()
            .map {
                val readList = getReadChapterUseCase.execute().blockingLast()
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
        if (mChapterPair.value!!.first.isNotEmpty()) {
            getReadChapterUseCase.execute()
                .map {
                    Pair(mChapterPair.value?.first!!, it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Idle }
                .doAfterTerminate { mLoadingState.value = Idle }
                .subscribe({
                    mChapterPair.value = it
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }
        }
    }

    fun addToFavorite() {
        val menu = appRepository.appViewModel.Manga.selectedMangaMenuItem
        addToFavoriteUseCase.execute(menu, mChapterPair.value?.first?.size ?: 0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mFavouriteState.value = true
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
    }

    fun removeFromFavorite() {
        val menu = appRepository.appViewModel.Manga.selectedMangaMenuItem
        removeFromFavoriteUseCase.execute(menu)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mFavouriteState.value = false
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
    }

    fun getFavoriteState() {
        val menu = appRepository.appViewModel.Manga.selectedMangaMenuItem
        getFavouriteStateUseCase.execute(menu)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mFavouriteState.value = it
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
    }

    fun selectChapter(chapter: MangaChapterItem, callback: () -> Unit) {
        selectMangaChapterUseCase.execute(chapter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ callback() },
                { Logger.e(TAG, it) })
            .apply { disposable.add(this) }
    }

    fun sort() {
        order = !order
        var (chapterList, historyList) = mChapterPair.value!!
        chapterList = chapterList.reversed()
        appRepository.appViewModel.Manga.mangaChapterList = chapterList
        mChapterPair.postValue(chapterList to historyList)

    }

    companion object {
        const val TAG = "MangaChapterActivityViewModel"
    }
}
