package com.king.mangaviewer.ui.page

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.king.mangaviewer.MyApplication
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.base.ErrorMessage
import com.king.mangaviewer.base.ErrorMessage.GenericError
import com.king.mangaviewer.base.ErrorMessage.NoError
import com.king.mangaviewer.base.ErrorMessage.ViewModelError
import com.king.mangaviewer.component.ReadingDirection
import com.king.mangaviewer.component.ReadingDirection.LTR
import com.king.mangaviewer.component.ReadingDirection.RTL
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.usecase.AddToHistoryUseCase
import com.king.mangaviewer.domain.usecase.GetPageListUseCase
import com.king.mangaviewer.domain.usecase.SelectLastReadChapterUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaChapterUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaPageItem
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.model.MangaUriType
import com.king.mangaviewer.model.MangaUriType.ZIP
import com.king.mangaviewer.model.MangaUriType.WEB
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.SubError.NoNextChapter
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.SubError.NoPrevChapter
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MangaPageActivityV2ViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val getPageListUseCase: GetPageListUseCase,
    private val selectMangaChapterUseCase: SelectMangaChapterUseCase,
    private val addToHistoryUseCase: AddToHistoryUseCase,
    private val selectLastReadChapterUseCase: SelectLastReadChapterUseCase
) :
    BaseActivityViewModel() {

    private val mDataList = MutableLiveData<List<MangaUri>>()
    val dataList: LiveData<List<MangaUri>> = mDataList

    val chapterList get() = appRepository.appViewModel.Manga.mangaChapterList
    val currentChapter get() = appRepository.appViewModel.Manga.selectedMangaChapterItem

    var currentPageNum = 0
    val totalPageNum = Transformations.map(dataList) {
        it!!.size
    }

    val errorMessage = MutableLiveData<ErrorMessage>().apply { value = NoError }

    private val mSelectedChapterName = MutableLiveData<String>()
    val selectedChapterName: LiveData<String> = mSelectedChapterName

    private val mReadingDirection = MutableLiveData<Boolean>().apply {
        //TODO should not use context here
        value = appRepository.appViewModel.Setting.getIsFromLeftToRight(MyApplication.context)
    }
    val readingDirection: LiveData<ReadingDirection> = Transformations.map(mReadingDirection) {
        Logger.d(TAG, "MangaPageActivityV2ViewModel readingDirection update")

        if (it)
            LTR
        else
            RTL
    }

    val prevAndNextChapterName = MutableLiveData<Pair<String?, String?>>()

    init {
        Logger.d(TAG, "MangaPageActivityV2ViewModel init")
        mDataList.value = emptyList()
        mSelectedChapterName.value = appRepository.appViewModel.Manga.selectedMangaChapterItem?.title
            ?: ""

    }

    override fun attachToView() {
        showChapterLoading()
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
                MangaUri(it.webImageUrl, it.referUrl, it.getMangaLoaderType())
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate { hideChapterLoading() }
            .doAfterSuccess {
                currentPageNum = appRepository.appViewModel.Manga.nowPagePosition
                Logger.d(TAG, "start with page: ${currentPageNum}")
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
        showChapterLoading()
        val chapter = getNextChapter()
        if (chapter != null) {
            selectChapter(chapter)
        } else {
            errorMessage.value = NoNextChapter
            hideChapterLoading()
        }
    }

    fun prevChapter() {
        showChapterLoading()
        val chapter = getPrevChapter()
        if (chapter != null) {
            selectChapter(chapter)
        } else {
            errorMessage.value = NoPrevChapter
            hideChapterLoading()
        }
    }

    private fun getPrevChapter(): MangaChapterItem? {
        val pos = getCurrentChapterPos()
        return if (pos + 1 < chapterList.size) {
            chapterList[pos + 1]
        } else {
            null
        }
    }

    private fun getNextChapter(): MangaChapterItem? {
        val pos = getCurrentChapterPos()
        return if (pos - 1 >= 0) {
            chapterList[pos - 1]
        } else {
            null
        }
    }

    fun getCurrentChapterPos(): Int {
        return chapterList.indexOf(currentChapter)
    }

    fun selectChapter(chapter: MangaChapterItem) {
        selectMangaChapterUseCase.execute(chapter)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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

    fun showChapterLoading() {
        mLoadingState.value = Loading
    }

    fun hideChapterLoading() {
        mLoadingState.value = Idle
    }

    fun toggleDirection() {
        //TODO should not use context here
        mReadingDirection.value = mReadingDirection.value!!.not()
        appRepository.appViewModel.Setting.setIsFromLeftToRight(MyApplication.context,
            mReadingDirection.value!!)
    }

    fun saveCurrentReadPage() {
        addToHistoryUseCase.execute(appRepository.appViewModel.Manga.selectedMangaChapterItem,
            currentPageNum)
            .subscribeOn(Schedulers.newThread())
            .doOnError { Logger.e(TAG, it) }
            .onErrorComplete()
            .subscribe()
    }

    fun recoverFromLastRead() {
        selectLastReadChapterUseCase.execute().subscribe()
    }

    private fun MangaPageItem.getMangaLoaderType(): MangaUriType {
        return if (this.mangaWebSource.id == -1) {
            ZIP
        } else {
            WEB
        }
    }

    companion object {
        const val TAG = "MangaPageActivityV2ViewModel"
    }

    sealed class SubError : ViewModelError() {
        object NoNextChapter : SubError()
        object NoPrevChapter : SubError()
    }
}
