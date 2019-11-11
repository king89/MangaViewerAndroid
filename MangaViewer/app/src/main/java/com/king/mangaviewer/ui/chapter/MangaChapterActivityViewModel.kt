package com.king.mangaviewer.ui.chapter

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.adapter.DownloadState
import com.king.mangaviewer.adapter.MangaChapterStateItem
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.repository.HistoryMangaRepository
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
import io.reactivex.Single
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

    val chapterList = MutableLiveData<List<MangaChapterItem>>()
    val chapterHistoryList = MutableLiveData<List<MangaChapterItem>>()

    val chapterStateList: MutableLiveData<Map<String, MangaChapterStateItem>> =
        MediatorLiveData<Map<String, MangaChapterStateItem>>().apply {
            this.value = emptyMap()
            addSource(chapterList) {
                updateChapterStateList()
            }
            addSource(chapterHistoryList) {
                updateChapterStateList()
            }
        }


    private val mFavouriteState = MutableLiveData<Boolean>().apply { value = false }
    val favouriteState = mFavouriteState
    // true for default , false for reverse
    private var order = true

    override fun attachToView() {
        getChapterList()
        getHistoryChapter()
        getFavoriteState()
    }

    private fun updateChapterStateList() {
        Single.fromCallable {
            val stateMap = hashMapOf<String, MangaChapterStateItem>()
            if (chapterList.value?.isNotEmpty() == true
                && chapterHistoryList.value?.isNotEmpty() == true) {

                val chapterList = chapterList.value!!
                val historyMap = chapterHistoryList.value!!.let {
                    val hMap = hashMapOf<String, MangaChapterItem>()
                    it.forEach { chapter ->
                        hMap[chapter.hash] = chapter
                    }
                    hMap
                }

                chapterList.forEach { item ->
                    val isRead = historyMap.containsKey(item.hash)
                    val downloadState = DownloadState.None
                    stateMap[item.hash] = MangaChapterStateItem(downloadState, isRead)
                }
            }
            stateMap
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chapterStateList.value = it
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
    }

    fun getChapterList() {
        getChapterListUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mLoadingState.value = Loading }
            .doAfterTerminate { mLoadingState.value = Idle }
            .subscribe({
                chapterList.postValue(it)
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
            .also {
                Logger.d(TAG, "getChapterList")
            }

    }

    fun getHistoryChapter() {
        getReadChapterUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                if (mLoadingState.value == Loading) return@doAfterTerminate
                mLoadingState.value = Idle
            }

            .subscribe({
                chapterHistoryList.value = it
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
    }

    fun addToFavorite() {
        val menu = appRepository.appViewModel.Manga.selectedMangaMenuItem
        addToFavoriteUseCase.execute(menu, chapterList.value?.size ?: 0)
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
        var chapterList = chapterList.value!!
        chapterList = chapterList.reversed()
        appRepository.appViewModel.Manga.mangaChapterList = chapterList
        this.chapterList.postValue(chapterList)
    }

    companion object {
        const val TAG = "MangaChapterActivityViewModel"
    }
}
