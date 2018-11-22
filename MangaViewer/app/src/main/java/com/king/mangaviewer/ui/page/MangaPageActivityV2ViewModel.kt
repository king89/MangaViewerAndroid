package com.king.mangaviewer.ui.page

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.base.ErrorMessage
import com.king.mangaviewer.base.ErrorMessage.GenericError
import com.king.mangaviewer.base.ErrorMessage.NoError
import com.king.mangaviewer.base.ErrorMessage.ViewModelError
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.usecase.GetChapterListUseCase
import com.king.mangaviewer.domain.usecase.GetPageListUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaChapterUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.SubError.NoNextChapter
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.SubError.NoPrevChapter
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment.Companion
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MangaPageActivityV2ViewModel @Inject constructor(
        private val appRepository: AppRepository,
        private val getPageListUseCase: GetPageListUseCase,
        private val selectMangaChapterUseCase: SelectMangaChapterUseCase
) :
        BaseActivityViewModel() {

    private val mDataList = MutableLiveData<List<MangaUri>>()
    val dataList: LiveData<List<MangaUri>> = mDataList

    val currentPageNum = MutableLiveData<Int>().apply { value = 0 }
    val totalPageNum = Transformations.map(dataList) {
        it!!.size
    }

    val errorMessage = MutableLiveData<ErrorMessage>().apply { value = NoError }

    private val mSelectedChapterName = MutableLiveData<String>()
    val selectedChapterName: LiveData<String> = mSelectedChapterName

    val prevAndNextChapterName = MutableLiveData<Pair<String?, String?>>()

    init {
        Logger.d(TAG, "MangaPageActivityV2ViewModel init")
        mDataList.value = emptyList()
        mSelectedChapterName.value = appRepository.appViewModel.Manga.selectedMangaChapterItem.title
    }

    override fun attachToView() {
        getPageList()
    }

    fun getPageList() {
        getPageListObservable()
                .ignoreElement()
                .onErrorComplete()
                .subscribe()
                .apply { disposable.add(this) }

    }

    private fun getPageListObservable(): Single<MutableList<MangaUri>> {
        return getPageListUseCase.execute()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMapIterable { it }
                .map {
                    it.apply {
                        webImageUrl = MangaHelperV2.getWebImageUrl(it)
                    }
                    MangaUri(it.webImageUrl, it.referUrl)
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Loading }
                .doAfterTerminate { mLoadingState.value = Idle }
                .doAfterSuccess {
                    mDataList.value = it
                    prevAndNextChapterName.value = (getPrevChapter()?.title to getNextChapter()?.title)
                    Logger.d(TAG, "getPageListObservable doAfterSuccess")
                }
                .doOnError {
                    Logger.e(TAG, it)
                    errorMessage.value = GenericError
                }
    }

    fun nextChapter() {
        val chapter = getNextChapter()
        if (chapter != null) {
            selectChapter(chapter)
        } else {
            errorMessage.value = NoNextChapter
        }
    }

    fun prevChapter() {
        val chapter = getPrevChapter()
        if (chapter != null) {
            selectChapter(chapter)
        } else {
            errorMessage.value = NoPrevChapter
        }
    }

    private fun getPrevChapter(): MangaChapterItem? {
        val chapterList = appRepository.appViewModel.Manga.mangaChapterList
        val currentChapter = appRepository.appViewModel.Manga.selectedMangaChapterItem
        val pos = chapterList.indexOf(currentChapter)
        return if (pos + 1 < chapterList.size) {
            chapterList[pos + 1]
        } else {
            null
        }
    }

    private fun getNextChapter(): MangaChapterItem? {
        val chapterList = appRepository.appViewModel.Manga.mangaChapterList
        val currentChapter = appRepository.appViewModel.Manga.selectedMangaChapterItem
        val pos = chapterList.indexOf(currentChapter)
        return if (pos - 1 >= 0) {
            chapterList[pos - 1]
        } else {
            null
        }
    }

    private fun selectChapter(chapter: MangaChapterItem) {
        selectMangaChapterUseCase.execute(chapter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Loading }
                .doAfterTerminate { mLoadingState.value = Idle }
                .doOnComplete {
                    Logger.d(TAG, "selectChapter on complete")
                    mSelectedChapterName.value = chapter.title
                }
                .andThen(getPageListObservable())
                .doOnError {
                    Logger.e(TAG, it)
                    errorMessage.value = GenericError
                }
                .ignoreElement()
                .onErrorComplete()
                .subscribe()
                .apply { disposable.add(this) }
    }

    companion object {
        const val TAG = "MangaChapterActivityViewModel"
    }

    sealed class SubError : ViewModelError() {
        object NoNextChapter : SubError()
        object NoPrevChapter : SubError()
    }
}
