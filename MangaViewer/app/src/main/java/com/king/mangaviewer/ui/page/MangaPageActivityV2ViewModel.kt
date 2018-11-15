package com.king.mangaviewer.ui.page

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.usecase.GetChapterListUseCase
import com.king.mangaviewer.domain.usecase.GetPageListUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaChapterUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.ErrorMessage.NoError
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.ErrorMessage.NoNextChapter
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.ErrorMessage.NoPrevChapter
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.ErrorMessage.OopsError
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MangaPageActivityV2ViewModel @Inject constructor(
        private val appRepository: AppRepository,
        private val getChapterListUseCase: GetChapterListUseCase,
        private val getPageListUseCase: GetPageListUseCase,
        private val selectMangaChapterUseCase: SelectMangaChapterUseCase
) :
        BaseActivityViewModel() {

    private val mDataList = MutableLiveData<List<MangaUri>>()
    val dataList: LiveData<List<MangaUri>> = mDataList

    val errorMessage = MutableLiveData<ErrorMessage>().apply { value = NoError }
    val mSelectedChapterName = MutableLiveData<String>()
    val selectedChapterName: LiveData<String> = mSelectedChapterName

    init {
        mDataList.value = emptyList()
        mSelectedChapterName.value = appRepository.appViewModel.Manga.selectedMangaChapterItem.title
    }

    override fun attachToView() {
        getPageList()
    }

    fun getPageList() {
        getPageListUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Loading }
                .doAfterTerminate { mLoadingState.value = Idle }
                .toObservable()
                .flatMapIterable { it }
                .map {
                    it.apply {
                        webImageUrl = MangaHelperV2.getWebImageUrl(it)
                    }
                    MangaUri(it.webImageUrl, it.referUrl)
                }
                .toList()
                .subscribe({
                    mDataList.value = it
                }, {
                    Logger.e(TAG, it)
                })
                .apply { disposable.add(this) }

    }

    fun nextChapter() {
        val chapterList = appRepository.appViewModel.Manga.mangaChapterList
        val currentChapter = appRepository.appViewModel.Manga.selectedMangaChapterItem

        val pos = chapterList.indexOf(currentChapter)
        if (pos - 1 >= 0) {
            selectChapter(chapterList[pos - 1])
        } else {
            errorMessage.value = NoNextChapter
        }

    }

    fun prevChapter() {
        val chapterList = appRepository.appViewModel.Manga.mangaChapterList
        val currentChapter = appRepository.appViewModel.Manga.selectedMangaChapterItem

        val pos = chapterList.indexOf(currentChapter)
        if (pos + 1 < chapterList.size) {
            selectChapter(chapterList[pos + 1])
        } else {
            errorMessage.value = NoPrevChapter
        }

    }

    private fun selectChapter(chapter: MangaChapterItem) {
        selectMangaChapterUseCase.execute(chapter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Loading }
                .doAfterTerminate { mLoadingState.value = Idle }
                .subscribe(
                        {
                            mSelectedChapterName.value = chapter.title
                            getPageList()
                        },
                        {
                            Logger.e(TAG, it)
                            errorMessage.value = OopsError
                        })
                .apply { disposable.add(this) }
    }

    companion object {
        const val TAG = "MangaChapterActivityViewModel"
    }

    sealed class ErrorMessage {
        object NoError : ErrorMessage()
        object OopsError : ErrorMessage()
        object NoNextChapter : ErrorMessage()
        object NoPrevChapter : ErrorMessage()
    }
}
